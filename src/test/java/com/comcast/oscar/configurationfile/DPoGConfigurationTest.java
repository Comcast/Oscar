/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.cli.commands.Specification;
import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.tlv.DPoGTlvCodec;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvVariableBinding;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class DPoGConfigurationTest {
  private static final int DPOG = ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE;
  // Deliberately reverse child/type order to detect dictionary-order rewrites.
  private static final String PROFILE =
      """
      DPoG {
        DownstreamServiceFlow { QualityOfServiceParameterSetType 07; ServiceFlowReference 2; }
        NetworkAccess 1;
        UpstreamServiceFlow { QualityOfServiceParameterSetType 07; ServiceFlowReference 1; }
      }
      """;

  @Test
  void unsignedVectorPreservesSourceAndBinaryOrder() throws Exception {
    var imported = new ConfigurationFileImport(new StringBuilder(PROFILE));
    assertEquals(DPOG, imported.getConfigurationFileType());
    // DPoG C01 Table 10 and MULPI 3.0 C01 C.2.2: SF ref is 2 octets, QoS is 1.
    byte[] expected = HexFormat.of().parseHex("190706010701020002030101180706010701020001ff0000");
    var compiler = new ConfigurationFile(DPOG, imported.getTlvBuilder(), "");
    compiler.commit();
    assertArrayEquals(expected, compiler.toByteArray());
    String text = new ConfigurationFileExport(compiler).toPrettyPrint(false, true);
    assertTrue(text.indexOf("DownstreamServiceFlow") < text.indexOf("NetworkAccess"));
    assertTrue(
        text.indexOf("QualityOfServiceParameterSetType") < text.indexOf("ServiceFlowReference"));
    var reimported = new ConfigurationFileImport(new StringBuilder(text));
    var rebuilt = new ConfigurationFile(DPOG, reimported.getTlvBuilder(), "");
    rebuilt.commit();
    assertArrayEquals(expected, rebuilt.toByteArray());
    rebuilt.commit();
    assertArrayEquals(expected, rebuilt.toByteArray());
    rebuilt.clear();
    assertEquals(0, rebuilt.toByteArray().length);
  }

  @Test
  void rejectsExcludedNamesInvalidQosAndMissingFlows() throws Exception {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConfigurationFileImport(
                new StringBuilder("DPoG { DownstreamFrequency 500000000; }")));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConfigurationFileImport(
                    new StringBuilder(
                        PROFILE.replace("NetworkAccess 1", "NetworkAccess 4294967296")))
                .toByteArray());
    var invalid = new ConfigurationFileImport(new StringBuilder(PROFILE.replace("07", "06")));
    var compiler = new ConfigurationFile(DPOG, invalid.getTlvBuilder(), "");
    assertThrows(IllegalArgumentException.class, compiler::commit);
    var missing = new ConfigurationFileImport(new StringBuilder("DPoG { NetworkAccess 1; }"));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ConfigurationFile(DPOG, missing.getTlvBuilder(), "").commit());
  }

  @Test
  void rejectsMalformedUnknownAndRepeatedChildBytesBeforeExport() {
    for (String malformed : new String[] {"03", "030201", "030102", "ff01", "18050102000106"}) {
      assertThrows(
          IllegalArgumentException.class,
          () -> DPoGTlvCodec.parse(HexFormat.of().parseHex(malformed)),
          malformed);
    }
    for (String unsupported : new String[] {"010400000001", "00", "1806010101010102"}) {
      assertThrows(
          RuntimeException.class,
          () -> {
            var builder = new TlvBuilder();
            builder.add(
                new TlvVariableBinding(
                    HexFormat.of().parseHex(unsupported),
                    new TlvDictionary(TlvDictionary.DPOG_DICTIONARY_TABLE_NAME)
                        .getTopLevelByteLength()));
            new ConfigurationFileExport(builder, DPOG);
          },
          unsupported);
    }
  }

  @Test
  void signedFileMatchesIndependentMd5AndHmacVector() throws Exception {
    var imported = new ConfigurationFileImport(new StringBuilder(PROFILE));
    byte[] parameters = imported.toByteArray();
    byte[] cmDigest = java.security.MessageDigest.getInstance("MD5").digest(parameters);
    var cmTlv = new java.io.ByteArrayOutputStream();
    cmTlv.write(6);
    cmTlv.write(16);
    cmTlv.writeBytes(cmDigest);
    var micInput = new java.io.ByteArrayOutputStream();
    micInput.writeBytes(HexFormat.of().parseHex("030101"));
    micInput.writeBytes(cmTlv.toByteArray());
    micInput.writeBytes(HexFormat.of().parseHex("180706010701020001190706010701020002"));
    var hmac = javax.crypto.Mac.getInstance("HmacMD5");
    hmac.init(
        new javax.crypto.spec.SecretKeySpec(
            "test-key".getBytes(java.nio.charset.StandardCharsets.US_ASCII), "HmacMD5"));
    var expected = new java.io.ByteArrayOutputStream();
    expected.writeBytes(parameters);
    expected.writeBytes(cmTlv.toByteArray());
    expected.write(7);
    expected.write(16);
    expected.writeBytes(hmac.doFinal(micInput.toByteArray()));
    expected.write(255);
    while (expected.size() % 4 != 0) {
      expected.write(0);
    }
    var configuration = new ConfigurationFile(DPOG, imported.getTlvBuilder(), "test-key");
    configuration.commit();
    assertArrayEquals(expected.toByteArray(), configuration.toByteArray());
  }

  @Test
  void supportsExtendedMicAndRefreshesItAfterEdit() throws Exception {
    String extended =
        PROFILE.substring(0, PROFILE.lastIndexOf('}'))
            + """
                VendorSpecific {
                  VendorId [FF:FF:FF];
                  ExtCmtsMicConf { ExtCmtsMicHMACType 1; ExtCmtsMicBitmap (00010000); }
                }
              }
              """;
    var imported = new ConfigurationFileImport(new StringBuilder(extended));
    var original = new ConfigurationFile(DPOG, imported.getTlvBuilder(), "test-key");
    original.commit();
    var check =
        com.comcast.oscar.compiler.DocsisMicVerifier.verify(
            original.toByteArray(),
            com.comcast.oscar.compiler.DocsisMicVerifier.Profile.DOCSIS_31,
            "test-key".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    assertEquals(com.comcast.oscar.compiler.DocsisMicVerifier.Status.MATCH, check.cm());
    assertEquals(com.comcast.oscar.compiler.DocsisMicVerifier.Status.MATCH, check.extended());
    String exported = new ConfigurationFileExport(original).toPrettyPrint(false, true);
    var unchanged = new ConfigurationFileImport(new StringBuilder(exported));
    var rebuilt = new ConfigurationFile(DPOG, unchanged.getTlvBuilder(), "test-key");
    rebuilt.commit();
    assertArrayEquals(original.toByteArray(), rebuilt.toByteArray());
    var edited =
        new ConfigurationFileImport(
            new StringBuilder(exported.replace("NetworkAccess 1", "NetworkAccess 0")));
    var changed = new ConfigurationFile(DPOG, edited.getTlvBuilder(), "test-key");
    changed.commit();
    assertFalse(java.util.Arrays.equals(original.toByteArray(), changed.toByteArray()));
    assertEquals(
        com.comcast.oscar.compiler.DocsisMicVerifier.Status.MATCH,
        com.comcast.oscar.compiler.DocsisMicVerifier.verify(
                changed.toByteArray(),
                com.comcast.oscar.compiler.DocsisMicVerifier.Profile.DOCSIS_31,
                "test-key".getBytes(java.nio.charset.StandardCharsets.US_ASCII))
            .extended());
    assertThrows(
        IllegalArgumentException.class,
        () -> new ConfigurationFile(DPOG, unchanged.getTlvBuilder(), "").commit());
  }

  @Test
  void decodesNewCatalogPathsAndDiagnosesDpog() throws Exception {
    // DUT control/mask, downstream CMIM and general-extension vendor ID.
    byte[] raw = HexFormat.of().parseHex("2d0601010102018017040d0240002b050803ffffff");
    var binding =
        new TlvVariableBinding(
            raw,
            new TlvDictionary(TlvDictionary.DPOG_DICTIONARY_TABLE_NAME).getTopLevelByteLength());
    var builder = new TlvBuilder();
    builder.add(binding);
    String text = new ConfigurationFileExport(builder, DPOG).toPrettyPrint(false, true);
    var imported = new ConfigurationFileImport(new StringBuilder(text));
    assertArrayEquals(raw, imported.toByteArray());
    var report =
        TlvDisassemble.diagnose(
            raw,
            new com.comcast.oscar.tlv.DisassemblyOptions(
                com.comcast.oscar.tlv.DisassemblyOptions.Family.DPOG, 1024, 32, 100));
    assertTrue(
        report.entries().stream()
            .noneMatch(
                entry ->
                    entry.status()
                        == com.comcast.oscar.tlv.TlvDisassemblyReport.Status.UNKNOWN_TYPE));
    assertThrows(
        IllegalArgumentException.class,
        () -> DPoGTlvCodec.parse(HexFormat.of().parseHex("0b01ff")));
  }

  @Test
  void routesCliAndTemplatesToDpog() {
    for (String alias : new String[] {"dg", "dpog", "dg1", "dpog1.0"}) {
      var specification = new Specification();
      specification.setValues(new String[] {alias, "1.0"});
      assertEquals(DPOG, specification.getConfigurationFileType());
      assertEquals(TlvDisassemble.TLV_TYPE_DPOG, specification.getTlvDisassemble());
      assertFalse(specification.isPacketCable());
    }
    var unsupported = new Specification();
    unsupported.setValues(new String[] {"dg", "2.0"});
    assertEquals(-1, unsupported.getConfigurationFileType());
    String template = new ConfigurationFileExport(DPOG).toPrettyPrint(false, true);
    assertTrue(template.contains("DPoG"));
    assertFalse(template.contains("DownstreamFrequency"));
  }
}
