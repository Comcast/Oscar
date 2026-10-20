/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.DocsisVersion;
import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class DocsisMicConsolidationTest {
  private static final String KEY = "public-lab-key";
  private static final byte[] PARAMETERS = hex("030101180701020001060107190701020002060107");

  @ParameterizedTest
  @EnumSource(DocsisVersion.class)
  void allCompilerRevisionsMatchIndependentDigestsAndRepeatCommit(DocsisVersion version)
      throws Exception {
    boolean oldest = version == DocsisVersion.DOCSIS_1_0;
    byte[] parameters = oldest ? concat(PARAMETERS, hex("0403010101")) : PARAMETERS;
    DocsisCompiler compiler =
        version == DocsisVersion.DOCSIS_4_0
            ? new Docsis40Compiler(KEY)
            : new DocsisCompiler(KEY, version.configurationFileType());
    compiler.add(new HexString(parameters));
    compiler.commit();
    byte[] cm = tlv(6, MessageDigest.getInstance("MD5").digest(parameters));
    byte[] cmtsInput =
        concat(
            hex("030101"),
            oldest ? hex("0403010101") : new byte[0],
            cm,
            hex("180701020001060107190701020002060107"));
    byte[] expected = complete(parameters, cm, tlv(7, independentHmac(cmtsInput)));
    assertArrayEquals(expected, compiler.toByteArray());
    compiler.commit();
    assertArrayEquals(expected, compiler.toByteArray());
    var profile =
        switch (version) {
          case DOCSIS_3_1 -> DocsisMicVerifier.Profile.DOCSIS_31;
          case DOCSIS_4_0 -> DocsisMicVerifier.Profile.DOCSIS_40;
          default -> DocsisMicVerifier.Profile.LEGACY_DOCSIS;
        };
    byte[] original = expected.clone();
    var verified =
        DocsisMicVerifier.verify(expected, profile, KEY.getBytes(StandardCharsets.UTF_8));
    assertEquals(DocsisMicVerifier.Status.MATCH, verified.cm());
    assertEquals(DocsisMicVerifier.Status.MATCH, verified.cmts());
    assertEquals(
        DocsisMicVerifier.Status.MISMATCH,
        DocsisMicVerifier.verify(expected, profile, new byte[] {1}).cmts());
    assertArrayEquals(original, expected);
  }

  @ParameterizedTest
  @EnumSource(DocsisMicRules.Coverage.class)
  void coverageDifferencesAndRepeatedInstancesMatchIndependentInput(
      DocsisMicRules.Coverage coverage) throws Exception {
    // Reverse physical order, two MaximumCPE values, and old/new coverage discriminators.
    byte[] ipv6 = hex("3b1020010db8000000000000000000000001");
    byte[] ipv4 = hex("1404c0000201");
    byte[] cos = hex("0403010101");
    byte[] phs = hex("1a03010101");
    byte[] parameters = concat(ipv6, phs, hex("120105"), cos, ipv4, PARAMETERS, hex("120107"));
    byte[] cm = tlv(6, MessageDigest.getInstance("MD5").digest(parameters));
    boolean legacy = coverage == DocsisMicRules.Coverage.LEGACY_COMPATIBILITY;
    byte[] selected =
        concat(
            hex("030101"),
            legacy ? cos : new byte[0],
            cm,
            hex("120105120107"),
            ipv4,
            legacy ? new byte[0] : ipv6,
            hex("180701020001060107190701020002060107"),
            legacy ? phs : new byte[0]);
    byte[] result =
        DocsisMicFinalizer.finish(parameters, TlvRegistry.forFamily("docsis"), KEY, coverage);
    assertArrayEquals(complete(parameters, cm, tlv(7, independentHmac(selected))), result);
    assertArrayEquals(parameters, Arrays.copyOf(result, parameters.length));
    var profile =
        legacy ? DocsisMicVerifier.Profile.LEGACY_DOCSIS : DocsisMicVerifier.Profile.DOCSIS_31;
    assertEquals(
        DocsisMicVerifier.Status.MATCH,
        DocsisMicVerifier.verify(result, profile, KEY.getBytes(StandardCharsets.UTF_8)).cmts());
    var other =
        legacy ? DocsisMicVerifier.Profile.DOCSIS_31 : DocsisMicVerifier.Profile.LEGACY_DOCSIS;
    assertEquals(
        DocsisMicVerifier.Status.MISMATCH,
        DocsisMicVerifier.verify(result, other, KEY.getBytes(StandardCharsets.UTF_8)).cmts());
    assertArrayEquals(
        result, DocsisMicFinalizer.finish(result, TlvRegistry.forFamily("docsis"), KEY, coverage));
  }

  @Test
  @SuppressWarnings("deprecation")
  void publicCompatibilityAliasesHaveOneImmutableOwner() {
    assertSame(
        DocsisMicRules.orderedTypes(DocsisMicRules.Coverage.LEGACY_COMPATIBILITY),
        com.comcast.oscar.cablelabsdefinitions.Constants.DOCSIS_CMTS_MIC_TLV_LIST);
    assertEquals(
        List.of(1, 2, 3, 4, 17, 43, 6, 18, 19, 20, 22, 23, 24, 25, 28, 29, 26, 35, 36, 37, 40),
        DocsisMicRules.orderedTypes(DocsisMicRules.Coverage.LEGACY_COMPATIBILITY));
    assertEquals(
        List.of(1, 2, 3, 17, 43, 6, 18, 19, 20, 59, 22, 23, 24, 25, 28, 29, 35, 36, 37, 40),
        DocsisMicRules.orderedTypes(DocsisMicRules.Coverage.DOCSIS_31_AND_40));
    for (var coverage : DocsisMicRules.Coverage.values()) {
      assertThrows(
          UnsupportedOperationException.class, () -> DocsisMicRules.orderedTypes(coverage).clear());
    }
    assertArrayEquals(
        hex("d41d8cd98f00b204e9800998ecf8427e"),
        new DocsisCompiler(KEY, DocsisVersion.DOCSIS_3_1.configurationFileType())
            .getCmMIC(new byte[0]));
  }

  @Test
  void sharedFinalizerUsesRegistryLengthWidthsAndCanonicalEndPadding() throws Exception {
    // DOCSIS 4.0 type 103 has a two-octet length. MIC logic treats its payload as opaque.
    byte[] longParameter = concat(hex("670100"), new byte[256]);
    longParameter[3] = (byte) 255;
    longParameter[4] = 6;
    byte[] parameters = concat(PARAMETERS, longParameter);
    byte[] result = DocsisMicFinalizer.finish(parameters, TlvRegistry.forFamily("docsis40"), KEY);
    byte[] cm = tlv(6, MessageDigest.getInstance("MD5").digest(parameters));
    byte[] selected = concat(hex("030101"), cm, hex("180701020001060107190701020002060107"));
    assertArrayEquals(complete(parameters, cm, tlv(7, independentHmac(selected))), result);
    assertEquals(0, result.length % 4);
  }

  @Test
  void failedRecommitDiscardsStaleMicAndRejectsEmptyKey() throws Exception {
    var compiler = new DocsisCompiler(KEY, DocsisVersion.DOCSIS_2_0.configurationFileType());
    compiler.add(new HexString(PARAMETERS));
    compiler.commit();
    compiler.setSharedSecretKey("");
    assertThrows(IllegalArgumentException.class, compiler::commit);
    assertArrayEquals(PARAMETERS, compiler.toByteArray());
    compiler.setSharedSecretKey(null);
    assertThrows(IllegalArgumentException.class, compiler::commit);
    compiler.setSharedSecretKey(KEY);
    compiler.commit();
    assertEquals(
        DocsisMicVerifier.Status.MATCH,
        DocsisMicVerifier.verify(
                compiler.toByteArray(),
                DocsisMicVerifier.Profile.LEGACY_DOCSIS,
                KEY.getBytes(StandardCharsets.UTF_8))
            .cmts());
  }

  @Test
  void everyTrailerAlignmentUsesOnlyRequiredZeroPadding() throws Exception {
    for (int length = 0; length < 4; length++) {
      byte[] parameters = concat(PARAMETERS, tlv(11, new byte[length]));
      var compiler = new DocsisCompiler(KEY, DocsisVersion.DOCSIS_2_0.configurationFileType());
      compiler.add(new HexString(parameters));
      compiler.commit();
      byte[] cm = tlv(6, MessageDigest.getInstance("MD5").digest(parameters));
      byte[] selected = concat(hex("030101"), cm, hex("180701020001060107190701020002060107"));
      assertArrayEquals(
          complete(parameters, cm, tlv(7, independentHmac(selected))), compiler.toByteArray());
    }
  }

  @Test
  void wireIdentifiersHaveOnePrimitiveOwnerAndPreserveBoxedAliases() throws Exception {
    var owner = com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes.class;
    var aliases = com.comcast.oscar.cablelabsdefinitions.Constants.class;
    for (var field : owner.getFields()) {
      assertEquals(int.class, field.getType(), field.getName());
      assertTrue(java.lang.reflect.Modifier.isFinal(field.getModifiers()));
      assertEquals(Integer.class, aliases.getField(field.getName()).getType());
      assertEquals(field.getInt(null), aliases.getField(field.getName()).get(null));
    }
  }

  @Test
  void compatibilityAliasIsIndependentOfClassInitializationOrder() throws Exception {
    String constants = "com.comcast.oscar.cablelabsdefinitions.Constants";
    String rules = "com.comcast.oscar.compiler.DocsisMicRules";
    var location = DocsisMicRules.class.getProtectionDomain().getCodeSource().getLocation();
    for (String first : List.of(constants, rules)) {
      try (var loader =
          new java.net.URLClassLoader(
              new java.net.URL[] {location}, ClassLoader.getPlatformClassLoader())) {
        Class.forName(first, true, loader);
        var alias =
            Class.forName(constants, true, loader).getField("DOCSIS_CMTS_MIC_TLV_LIST").get(null);
        assertEquals(
            DocsisMicRules.orderedTypes(DocsisMicRules.Coverage.LEGACY_COMPATIBILITY), alias);
      }
    }
  }

  private static byte[] independentHmac(byte[] input) throws Exception {
    Mac mac = Mac.getInstance("HmacMD5");
    mac.init(new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "HmacMD5"));
    return mac.doFinal(input);
  }

  private static byte[] complete(byte[] parameters, byte[] cm, byte[] cmts) {
    byte[] unpadded = concat(parameters, cm, cmts, hex("ff"));
    return Arrays.copyOf(unpadded, ((unpadded.length + 3) / 4) * 4);
  }

  private static byte[] tlv(int type, byte[] value) {
    return concat(new byte[] {(byte) type, (byte) value.length}, value);
  }

  private static byte[] concat(byte[]... arrays) {
    var out = new ByteArrayOutputStream();
    for (byte[] bytes : arrays) out.writeBytes(bytes);
    return out.toByteArray();
  }

  private static byte[] hex(String input) {
    return HexFormat.of().parseHex(input);
  }
}
