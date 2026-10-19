package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.tlv.TlvAssembler;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class TlvRegistryTest {
  @ParameterizedTest
  @ValueSource(strings = {"docsis", "dpoe"})
  void decodesDropClassifierEthertypeAndSiblingVlan(String family) throws Exception {
    // CM-SP-MULPIv3.1-I21-201020, C.2.1.8.3 and C.2.1.9, pp. 684-685:
    // 60.10.3 is a three-byte value; 60.11 is its ancestor's sibling, not its child.
    var registry = TlvRegistry.forFamily(family);
    int configurationType =
        family.equals("docsis")
            ? com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants
                .DOCSIS_31_CONFIGURATION_TYPE
            : com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants
                .DPOE_20_CONFIGURATION_TYPE;
    assertTrue(registry.children(List.of(60, 10, 3)).isEmpty());
    assertEquals("8021PQPacketClassification", registry.find(List.of(60, 11)).name());
    for (byte[] payload :
        List.of(
            new byte[] {10, 5, 3, 3, 4, 0, 0},
            new byte[] {10, 5, 3, 3, 4, 0, 0, 11, 8, 1, 2, 0, 7, 2, 2, 0, 100})) {
      var builder = new TlvBuilder();
      builder.add(60, payload);
      byte[] expected = builder.toByteArray();
      var exporter =
          new com.comcast.oscar.configurationfile.ConfigurationFileExport(
              builder, configurationType);
      String text = exporter.toPrettyPrint(false, true);
      assertTrue(text.contains("EthertypeDSAPMacType"), text);
      assertArrayEquals(expected, parse(text).toByteArray());
    }
  }

  @Test
  void loadsAllFamiliesAndReturnsIndependentTemplates() {
    for (String family : List.of("docsis", "packetcable", "dpoe")) {
      TlvRegistry registry = TlvRegistry.forFamily(family);
      assertFalse(registry.children(List.of()).isEmpty());
      TlvDefinition root = registry.children(List.of()).get(0);
      JSONObject first = registry.template(root.path());
      first.put(Dictionary.VALUE, "changed");
      assertFalse(registry.template(root.path()).has(Dictionary.VALUE));
      assertThrows(UnsupportedOperationException.class, () -> root.path().add(99));
    }
    TlvRegistry registry = TlvRegistry.forFamily("docsis");
    JSONObject first = registry.template(List.of(4));
    first.getJSONArray(Dictionary.SUBTYPE_ARRAY).getJSONObject(0).put(Dictionary.VALUE, 123);
    assertFalse(
        registry
            .template(List.of(4))
            .getJSONArray(Dictionary.SUBTYPE_ARRAY)
            .getJSONObject(0)
            .has(Dictionary.VALUE));
  }

  @ParameterizedTest
  @ValueSource(strings = {"Docsis", "DPoE"})
  void parsesClassifierErrorNamesAndPreservesEncodedValues(String header) throws Exception {
    String family = header.equals("Docsis") ? "docsis" : "dpoe";
    var registry = TlvRegistry.forFamily(family);
    assertEquals("ErroredParameter", registry.find(List.of(22, 8, 1)).name());
    assertEquals("ErrorCode", registry.find(List.of(22, 8, 2)).name());
    assertEquals("ErrorMessage", registry.find(List.of(22, 8, 3)).name());
    var imported =
        parse(
            header
                + " { UpstreamPacketClassification { ClassifierError { "
                + "ErroredParameter; ErrorCode 1; ErrorMessage hello; } } }");
    // Existing legacy codec layout: 22 -> 8 -> code 2 and NUL-terminated message 3.
    byte[] expected = {22, 13, 8, 11, 2, 1, 1, 3, 6, 104, 101, 108, 108, 111, 0};
    assertArrayEquals(expected, imported.toByteArray());
    var exporter =
        new com.comcast.oscar.configurationfile.ConfigurationFileExport(
            imported.getTlvBuilder(), imported.getConfigurationFileType());
    String text = exporter.toPrettyPrint(false, true);
    assertTrue(text.contains("ErrorCode"));
    assertTrue(text.contains("ErrorMessage"));
    assertFalse(text.contains("Error Message"));
    assertArrayEquals(expected, parse(text).toByteArray());
  }

  @Test
  void allCatalogNamesAreSingleTokens() {
    for (String family : List.of("docsis", "docsis40", "dpoe", "packetcable")) {
      assertSingleTokenNames(TlvRegistry.forFamily(family), List.of());
    }
  }

  private static void assertSingleTokenNames(TlvRegistry registry, List<Integer> path) {
    for (var definition : registry.children(path)) {
      assertFalse(
          definition.name().codePoints().anyMatch(Character::isWhitespace),
          definition.path() + ": " + definition.name());
      assertSingleTokenNames(registry, definition.path());
    }
  }

  @Test
  void parserPreservesRepeatedRootValuesAndDocumentIsolation() {
    ConfigurationFileImport first = parse("Docsis { NetworkAccess 1; NetworkAccess 0; }");
    ConfigurationFileImport second = parse("Docsis { NetworkAccess 0; }");
    assertArrayEquals(new byte[] {3, 1, 1, 3, 1, 0}, first.toByteArray());
    assertArrayEquals(new byte[] {3, 1, 0}, second.toByteArray());
    assertArrayEquals(new byte[] {3, 1, 1, 3, 1, 0}, first.toByteArray());
  }

  @Test
  void parsesNestedSymbolsAndDecodesKnownBytes() throws Exception {
    byte[] expected = {4, 3, 1, 1, 7};
    assertArrayEquals(expected, parse("Docsis { ClassOfService { ClassID 7; } }").toByteArray());
    TlvBuilder builder = new TlvBuilder();
    builder.add(new HexString(expected));
    var decoded =
        new TlvDisassemble(builder, TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME).getTlvDictionary();
    assertEquals(
        7,
        decoded
            .getJSONObject(0)
            .getJSONArray(Dictionary.SUBTYPE_ARRAY)
            .getJSONObject(0)
            .getInt(Dictionary.VALUE));
    assertArrayEquals(expected, new TlvAssembler(decoded).toByteArray());
  }

  @Test
  void parsesPacketCableAndDpoeWithoutSql() {
    assertArrayEquals(new byte[] {3, 1, 1}, parse("DPoE { NetworkAccess 1; }").toByteArray());
    assertArrayEquals(
        new byte[] {(byte) 254, 1, 1},
        parse("PacketCable-1.0 { TelephonyConfigFileBeginEnd 1; }").toByteArray());
  }

  @Test
  void countsEveryMigratedDefinition() {
    assertEquals(677, count(TlvRegistry.forFamily("docsis"), List.of()));
    assertEquals(13, count(TlvRegistry.forFamily("packetcable"), List.of()));
    assertEquals(624, count(TlvRegistry.forFamily("dpoe"), List.of()));
  }

  private static int count(TlvRegistry registry, List<Integer> parent) {
    return registry.children(parent).stream()
        .mapToInt(child -> 1 + count(registry, child.path()))
        .sum();
  }

  @Test
  void resolvesCorrectedPathsWithoutDroppingDefinitions() {
    TlvRegistry registry = TlvRegistry.forFamily("docsis");
    assertEquals("CMReqUpstreamAttrMask", registry.find(List.of(43, 9, 3)).name());
    assertEquals("CMUpstreamForAttrMask", registry.find(List.of(43, 9, 4)).name());
    assertEquals(4, registry.children(List.of(43, 9)).size());
    assertEquals("integer", registry.find(List.of(25, 35, 1)).codec());
    assertEquals("string_bits", registry.find(List.of(54, 3)).codec());
  }

  @Test
  void familySelectionDoesNotRetainPreviousNames() {
    TlvDictionary dictionary = new TlvDictionary();
    assertTrue(dictionary.getAllTopLevelTypeNameToType().containsKey("NetworkAccess"));
    dictionary.updateDictionaryTablename(TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME);
    assertFalse(dictionary.getAllTopLevelTypeNameToType().containsKey("NetworkAccess"));
    assertEquals(2, dictionary.getTopLevelByteLength().get(64));
    assertEquals(5, dictionary.getAllTlvDefinition(102).length());
    assertThrows(
        IllegalArgumentException.class, () -> dictionary.updateDictionaryTablename("invalid"));
  }

  @Test
  void keepsLegacyChildSerializationOrder() {
    var children = TlvRegistry.forFamily("docsis").children(List.of(25));
    assertEquals(14, children.get(children.size() - 1).type());
  }

  @Test
  void cliSelectsDpoeCatalogForLongAndShortOptions() {
    for (String option : List.of("de", "dpoe", "de1", "dpoe1.0", "de2", "dpoe2.0")) {
      var specification = new com.comcast.oscar.cli.commands.Specification();
      specification.setValues(new String[] {option, "2.0"});
      assertEquals(TlvDisassemble.TLV_TYPE_DPOE, specification.getTlvDisassemble());
    }
  }

  @Test
  void cliSelectsDocsis40WithoutUsingLegacyCatalog() {
    var specification = new com.comcast.oscar.cli.commands.Specification();
    specification.setValues(new String[] {"d", "4.0"});
    assertEquals(5, specification.getConfigurationFileType());
    assertEquals("DOCSIS40_TLV_DEFINITION", specification.getTlvDisassemble());
    assertFalse(specification.isPacketCable());
  }

  @Test
  void rejectsMalformedCatalogsBeforeUse() throws Exception {
    String definition =
        """
        {"path":[3],"name":"Example","codec":"integer","lengthMin":1,"lengthMax":1,
         "lengthBytes":1,"supportedVersion":"1","maxInstances":1,"description":"Example"}
        """;
    assertNotNull(read(definition));
    assertThrows(
        IllegalArgumentException.class, () -> read(definition.replace("Example", "Bad Name")));
    assertThrows(
        IllegalArgumentException.class, () -> read(definition.replace("Example", "Bad\\tName")));
    assertThrows(IllegalArgumentException.class, () -> read(definition + "," + definition));
    assertThrows(IllegalArgumentException.class, () -> read(definition.replace("[3]", "[3,1]")));
    assertThrows(IllegalArgumentException.class, () -> read(definition.replace("integer", "typo")));
    assertThrows(
        IllegalArgumentException.class,
        () -> read(definition.replace("\"lengthMin\":1", "\"lengthMin\":2")));
    assertThrows(
        Exception.class,
        () -> read(definition.replace("\"path\":[3]", "\"path\":[3],\"path\":[4]")));
    assertThrows(Exception.class, () -> read(definition.replace("\"lengthBytes\":1,", "")));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Docsis { UndefinedTlv 1; }",
        "DPoE { UndefinedTlv 1; }",
        "PacketCable-1.0 { UndefinedTlv 1; }",
        "PacketCable-1.0 { NetworkAccess 1; }",
        "Docsis { UndefinedTlv; }"
      })
  void rejectsNamesOutsideSelectedRegistry(String configuration) {
    IllegalArgumentException failure =
        assertThrows(IllegalArgumentException.class, () -> parse(configuration));
    assertTrue(failure.getMessage().contains("TLV"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Docsis { ClassOfService { UndefinedChild 7; } }",
        "Docsis { ClassOfService { UndefinedChild; } }",
        "Docsis { ClassOfService { NetworkAccess 1; } }",
        "Docsis { ClassOfService { UndefinedContainer { ClassID 7; } } }",
        "Docsis { NetworkAccess { ClassID 7; } }"
      })
  void rejectsUndefinedNestedPaths(String configuration) {
    IllegalArgumentException failure =
        assertThrows(IllegalArgumentException.class, () -> parse(configuration));
    assertTrue(failure.getMessage().contains("TLV"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Docsis { NetworkAccess 1 }",
        "Docsis { NetworkAccess 1;",
        "Docsis { NetworkAccess @1; }",
        "Docsis { NetworkAccess 1; } UndefinedTlv 1;",
        "Docsis { NetworkAccess; } trailing"
      })
  void rejectsMalformedConfigurationText(String configuration) {
    assertThrows(IllegalArgumentException.class, () -> parse(configuration));
  }

  @Test
  void rejectsInvalidIntegerValue() {
    assertThrows(
        IllegalArgumentException.class, () -> parse("Docsis { NetworkAccess notAnInteger; }"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"DOCSIS-GOLDEN.txt", "DOCSIS-DUPLICATE-OID.txt", "IMS-PKT-CABLE-CONFIG.txt"})
  void importsExistingExampleFiles(String filename) throws Exception {
    Path example = Path.of("src/com/comcast/oscar/examples/testfiles", filename);
    assertTrue(new ConfigurationFileImport(example.toFile()).toByteArray().length > 0);
  }

  @Test
  void rejectsUndefinedNameInGoldenExample() throws Exception {
    String example =
        Files.readString(Path.of("src/com/comcast/oscar/examples/testfiles/DOCSIS-GOLDEN.txt"));
    String invalid = example.replaceFirst("\\bNetworkAccess\\b", "UndefinedTlv");
    assertNotEquals(example, invalid);
    IllegalArgumentException failure =
        assertThrows(IllegalArgumentException.class, () -> parse(invalid));
    assertTrue(failure.getMessage().contains("UndefinedTlv"));
  }

  @ParameterizedTest
  @MethodSource("truncatedTlvBuffers")
  void rejectsTruncatedBinaryWithoutChangingBuilder(byte[] malformed) throws Exception {
    TlvBuilder builder = new TlvBuilder();
    byte[] valid = {3, 1, 1};
    builder.add(new HexString(valid));
    assertThrows(TlvException.class, () -> builder.add(new HexString(malformed)));
    assertArrayEquals(valid, builder.toByteArray());
  }

  private static Stream<byte[]> truncatedTlvBuffers() {
    return Stream.of(
        new byte[] {3},
        new byte[] {3, 2, 1},
        new byte[] {3, 1, 1, 4},
        new byte[] {3, (byte) 255, 1});
  }

  private static TlvRegistry read(String definitions) throws Exception {
    String json =
        "{\"formatVersion\":1,\"family\":\"docsis\",\"edition\":\"test\","
            + "\"sourceSha256\":\"test\",\"definitions\":["
            + definitions
            + "]}";
    return TlvRegistry.read(
        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), "docsis", "test.json");
  }

  private static ConfigurationFileImport parse(String text) {
    return new ConfigurationFileImport(new StringBuilder(text));
  }
}
