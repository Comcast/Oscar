package com.comcast.oscar.compiler;

import static com.comcast.oscar.compiler.DocsisMicVerifier.Status.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class DocsisMicVerifierTest {
  private static final byte[] KEY = "public-test-key".getBytes(StandardCharsets.US_ASCII);
  private static final byte[] EXT_KEY =
      "different-public-test-key".getBytes(StandardCharsets.US_ASCII);
  private static final DocsisMicVerifier.Profile PROFILE = DocsisMicVerifier.Profile.DOCSIS_31;

  @Test
  void verifiesIndependentLegacyVectorAndNeverChangesInputs() throws Exception {
    // Physical order deliberately differs from MIC order; repeated 3s retain their order.
    byte[] parameters = hex("120105030101030100");
    byte[] cm = tlv(6, MessageDigest.getInstance("MD5").digest(parameters));
    byte[] legacy = hmac(KEY, concat(hex("030101030100"), cm, hex("120105")));
    byte[] binary = concat(parameters, cm, tlv(7, legacy), hex("ff000000"));
    byte[] original = binary.clone();
    byte[] originalKey = KEY.clone();
    var result = DocsisMicVerifier.verify(binary, PROFILE, KEY);
    assertEquals(MATCH, result.cm());
    assertEquals(MATCH, result.cmts());
    assertEquals(NOT_PRESENT, result.extended());
    assertArrayEquals(original, binary);
    assertArrayEquals(originalKey, KEY);
    assertEquals(MISMATCH, DocsisMicVerifier.verify(binary, PROFILE, EXT_KEY).cmts());
    binary[5] ^= 1;
    var changed = DocsisMicVerifier.verify(binary, PROFILE, KEY);
    assertEquals(MISMATCH, changed.cm());
    assertEquals(MISMATCH, changed.cmts());
  }

  @Test
  void verifiesExplicitAndImplicitMd5WithDistinctKeysAndZeroedSelfCoverage() throws Exception {
    for (boolean explicit : new boolean[] {false, true}) {
      byte[] binary = fixture(explicit, 1);
      for (var profile : DocsisMicVerifier.Profile.values()) {
        var result = DocsisMicVerifier.verify(binary, profile, KEY, EXT_KEY);
        assertEquals(MATCH, result.cm());
        assertEquals(MATCH, result.cmts());
        assertEquals(MATCH, result.extended());
        assertEquals(DocsisMicVerifier.Reason.NONE, result.reason());
        var wrongExtended = DocsisMicVerifier.verify(binary, profile, KEY, KEY);
        assertEquals(explicit ? MATCH : MISMATCH, wrongExtended.cmts());
        assertEquals(MISMATCH, wrongExtended.extended());
        var wrongLegacy = DocsisMicVerifier.verify(binary, profile, EXT_KEY, EXT_KEY);
        assertEquals(explicit ? MISMATCH : MATCH, wrongLegacy.cmts());
        assertEquals(MATCH, wrongLegacy.extended());
      }
    }
  }

  @Test
  void verifiesMmhAndChecksImplicitZeroPadding() throws Exception {
    // MMH primitive itself has independent SEC3 published-vector tests in Docsis40Test.
    for (boolean explicit : new boolean[] {false, true}) {
      byte[] binary = fixture(explicit, 2);
      var result = DocsisMicVerifier.verify(binary, PROFILE, KEY, EXT_KEY);
      assertEquals(MATCH, result.cm());
      assertEquals(MATCH, result.cmts());
      assertEquals(MATCH, result.extended());
      if (!explicit) {
        binary[binary.length - 2] = 1;
        assertEquals(MISMATCH, DocsisMicVerifier.verify(binary, PROFILE, KEY, EXT_KEY).extended());
      }
    }
  }

  @Test
  void reportsMissingKeysAndMissingDigestsSeparately() throws Exception {
    var empty = DocsisMicVerifier.verify(hex("030101ff"), PROFILE, KEY);
    assertEquals(NOT_PRESENT, empty.cm());
    assertEquals(NOT_PRESENT, empty.cmts());
    var missingKey = DocsisMicVerifier.verify(fixture(true, 1), PROFILE, null, null);
    assertEquals(MATCH, missingKey.cm());
    assertEquals(CANNOT_VERIFY, missingKey.cmts());
    assertEquals(CANNOT_VERIFY, missingKey.extended());
    assertEquals(DocsisMicVerifier.Reason.MISSING_SECRET, missingKey.reason());
  }

  @Test
  void refusesMalformedAmbiguousOrUnsupportedInput() throws Exception {
    for (String input :
        new String[] {
          "",
          "0301",
          "030101",
          "030101ff01",
          "061000ff",
          "06000600ff",
          "2b020600ff",
          "00030101ff",
          "2b080606010101010101ff"
        }) {
      var result = DocsisMicVerifier.verify(hex(input), PROFILE, KEY);
      assertEquals(CANNOT_VERIFY, result.cmts(), input);
      assertEquals(DocsisMicVerifier.Reason.MALFORMED_BINARY, result.reason(), input);
    }
    byte[] reserved = fixture(false, 43);
    var result = DocsisMicVerifier.verify(reserved, PROFILE, KEY);
    assertEquals(CANNOT_VERIFY, result.extended());
    assertEquals(DocsisMicVerifier.Reason.UNSUPPORTED_ALGORITHM, result.reason());
    byte[] duplicate =
        concat(
            Arrays.copyOf(fixture(false, 1), fixture(false, 1).length - 1),
            tlv(7, new byte[16]),
            hex("ff"));
    assertEquals(CANNOT_VERIFY, DocsisMicVerifier.verify(duplicate, PROFILE, KEY).cmts());
    for (int length = 0; length < fixture(true, 1).length; length++) {
      var truncated =
          DocsisMicVerifier.verify(Arrays.copyOf(fixture(true, 1), length), PROFILE, KEY);
      assertEquals(CANNOT_VERIFY, truncated.cmts());
    }
  }

  @Test
  void supportsTwoByteLengthTlvWithoutReserializingIt() throws Exception {
    // DOCSIS 4.0 TLV 85 uses a two-byte length in the registry.
    var registry = com.comcast.oscar.dictionary.TlvRegistry.forFamily("docsis40");
    int type =
        registry.children(java.util.List.of()).stream()
            .filter(def -> def.lengthBytes() == 2)
            .findFirst()
            .orElseThrow()
            .path()
            .getFirst();
    byte[] parameter = new byte[] {(byte) type, 0, 1, 42};
    byte[] cm = tlv(6, MessageDigest.getInstance("MD5").digest(parameter));
    byte[] binary = concat(parameter, cm, tlv(7, hmac(KEY, cm)), hex("ff"));
    var result = DocsisMicVerifier.verify(binary, DocsisMicVerifier.Profile.DOCSIS_40, KEY);
    assertEquals(MATCH, result.cm());
    assertEquals(MATCH, result.cmts());
  }

  @Test
  void verifiesOscarsDocsis40GenerationInBothExtendedModes() {
    for (int algorithm : new int[] {1, 2}) {
      for (boolean explicit : new boolean[] {false, true}) {
        String zeros = "00:".repeat(algorithm == 2 ? 7 : 15) + "00";
        String source =
            "Docsis-4.0 { NetworkAccess 1; "
                + "UpstreamServiceFlow { ServiceFlowReference 1; QosParameterSetType 7; } "
                + "DownstreamServiceFlow { ServiceFlowReference 2; QosParameterSetType 7; } "
                + "DocsisExtension { VendorId [FF:FF:FF]; ExtendedCmtsMic { HmacType "
                + algorithm
                + "; MicBitmap [10:00:00:C0:00:10]; "
                + (explicit ? "ExplicitDigest [" + zeros + "]; " : "")
                + "} } }";
        var imported =
            new com.comcast.oscar.configurationfile.ConfigurationFileImport(
                new StringBuilder(source));
        var file =
            new com.comcast.oscar.configurationfile.ConfigurationFile(
                5, imported.getTlvBuilder(), "public-test-key");
        file.commit();
        var result =
            DocsisMicVerifier.verify(file.toByteArray(), DocsisMicVerifier.Profile.DOCSIS_40, KEY);
        assertEquals(MATCH, result.cm());
        assertEquals(MATCH, result.cmts());
        assertEquals(MATCH, result.extended());
      }
    }
  }

  @Test
  void generatesDocsis31ExtendedBytesAgainstIndependentDigests() throws Exception {
    for (int algorithm : new int[] {1, 2}) {
      for (boolean explicit : new boolean[] {false, true}) {
        int digestLength = algorithm == 2 ? 8 : 16;
        byte[] extension =
            tlv(
                43,
                concat(
                    tlv(8, hex("ffffff")),
                    tlv(
                        6,
                        concat(
                            tlv(1, new byte[] {(byte) algorithm}),
                            tlv(2, hex("100020000010")),
                            explicit ? tlv(3, new byte[digestLength]) : new byte[0]))));
        byte[] sorted = hex("030101030100120105");
        byte[] covered = concat(sorted, extension);
        byte[] digest = algorithm == 2 ? DocsisMmh.digest(KEY, covered) : hmac(KEY, covered);
        var compiler =
            new DocsisCompiler(
                "public-test-key",
                com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants
                    .DOCSIS_31_CONFIGURATION_TYPE);
        compiler.add(
            new com.comcast.oscar.utilities.HexString(
                concat(hex("120105030101030100"), extension)));
        compiler.commit();
        if (explicit) {
          System.arraycopy(digest, 0, extension, extension.length - digestLength, digestLength);
        }
        byte[] cm = tlv(6, MessageDigest.getInstance("MD5").digest(concat(sorted, extension)));
        byte[] cmts =
            explicit
                ? hmac(KEY, concat(hex("030101030100"), extension, cm, hex("120105")))
                : Arrays.copyOf(digest, 16);
        byte[] expected = concat(sorted, extension, cm, tlv(7, cmts), hex("ff"));
        expected = Arrays.copyOf(expected, (expected.length + 3) / 4 * 4);
        assertArrayEquals(expected, compiler.toByteArray());
        compiler.commit();
        assertArrayEquals(expected, compiler.toByteArray());
        assertEquals(MATCH, DocsisMicVerifier.verify(expected, PROFILE, KEY).extended());
      }
    }
  }

  @Test
  void importsDocsis31ExtendedNamesAndGeneratesMic() {
    for (int algorithm : new int[] {1, 2}) {
      for (boolean explicit : new boolean[] {false, true}) {
        String zeros = "00:".repeat(algorithm == 2 ? 7 : 15) + "00";
        String source =
            "Docsis { NetworkAccess 1; VendorSpecific { VendorId [FF:FF:FF]; "
                + "ExtCmtsMicConf { ExtCmtsMicHMACType "
                + algorithm
                + "; ExtCmtsMicBitmap 000100000000000000000000000000000000000000010000; "
                + (explicit ? "ExpExtCmtsMicDigest [" + zeros + "]; " : "")
                + "} } }";
        var imported =
            new com.comcast.oscar.configurationfile.ConfigurationFileImport(
                new StringBuilder(source));
        var file =
            new com.comcast.oscar.configurationfile.ConfigurationFile(
                com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants
                    .DOCSIS_31_CONFIGURATION_TYPE,
                imported.getTlvBuilder(),
                "public-test-key");
        file.commit();
        var result = DocsisMicVerifier.verify(file.toByteArray(), PROFILE, KEY);
        assertEquals(MATCH, result.cm());
        assertEquals(MATCH, result.cmts());
        assertEquals(MATCH, result.extended());
      }
    }
  }

  @Test
  void rejectsInvalidExtendedGenerationInputs() throws Exception {
    for (String fields :
        new String[] {
          "010101", // missing bitmap
          "0101010200", // empty bitmap
          "010101020180", // reserved bit zero
          "010101020101", // selects CMTS MIC
          "01010102010203080000000000000000", // explicit selects CM MIC
          "01010102011003080000000000000000", // wrong MD5 digest length
          "010101010101020110" // duplicate algorithm
        }) {
      byte[] extension = tlv(43, concat(tlv(8, hex("ffffff")), tlv(6, hex(fields))));
      var compiler =
          new DocsisCompiler(
              "public-test-key",
              com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants
                  .DOCSIS_31_CONFIGURATION_TYPE);
      compiler.add(new com.comcast.oscar.utilities.HexString(extension));
      assertThrows(IllegalArgumentException.class, compiler::commit, fields);
    }
  }

  private static byte[] fixture(boolean explicit, int algorithm) throws Exception {
    byte[] parameters = hex("120105030101030100");
    byte[] bitmap = hex("100020000010"); // Types 3, 18 and 43; retain physical order.
    int digestSize = algorithm == 2 ? 8 : 16;
    byte[] fields =
        concat(
            tlv(1, new byte[] {(byte) algorithm}),
            tlv(2, bitmap),
            explicit ? tlv(3, new byte[digestSize]) : new byte[0]);
    byte[] extension = tlv(43, concat(tlv(8, hex("ffffff")), tlv(6, fields)));
    byte[] extendedInput = concat(parameters, extension);
    byte[] extendedDigest =
        algorithm == 2 ? DocsisMmh.digest(EXT_KEY, extendedInput) : hmac(EXT_KEY, extendedInput);
    if (explicit) {
      System.arraycopy(extendedDigest, 0, extension, extension.length - digestSize, digestSize);
    }
    byte[] body = concat(parameters, extension);
    byte[] cm = tlv(6, MessageDigest.getInstance("MD5").digest(body));
    byte[] cmts =
        explicit
            ? hmac(KEY, concat(hex("030101030100"), extension, cm, hex("120105")))
            : Arrays.copyOf(extendedDigest, 16);
    return concat(body, cm, tlv(7, cmts), hex("ff"));
  }

  private static byte[] tlv(int type, byte[] value) {
    return concat(new byte[] {(byte) type, (byte) value.length}, value);
  }

  private static byte[] hex(String text) {
    return HexFormat.of().parseHex(text);
  }

  private static byte[] concat(byte[]... arrays) {
    var result = new ByteArrayOutputStream();
    for (byte[] array : arrays) {
      result.writeBytes(array);
    }
    return result.toByteArray();
  }

  private static byte[] hmac(byte[] key, byte[] message) throws Exception {
    var mac = Mac.getInstance("HmacMD5");
    mac.init(new SecretKeySpec(key, "HmacMD5"));
    return mac.doFinal(message);
  }
}
