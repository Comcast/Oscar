/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.constants.Constants;
import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ConfigurationFileExportTextTest {
  private static final Path FIXTURES = Path.of("src/test/resources/export-text");
  @TempDir Path temporary;

  static Stream<Arguments> exactFixtures() {
    return Stream.of("nested", "packetcable")
        .flatMap(name -> Stream.of(Arguments.of(name, false), Arguments.of(name, true)));
  }

  @ParameterizedTest
  @MethodSource("exactFixtures")
  void exactTextPreservesNestedRepeatedTlvPathsAndQuotedContent(String fixture, boolean suppress)
      throws Exception {
    var source =
        new ConfigurationFileImport(
            new StringBuilder(Files.readString(FIXTURES.resolve(fixture + "-source.txt"))));
    byte[] original = source.toByteArray();
    var exporter =
        new ConfigurationFileExport(source.getTlvBuilder(), source.getConfigurationFileType());
    exporter.setDotTextOIDOutputFormat(false);
    String output = exporter.toPrettyPrint(false, suppress);
    String expected =
        Files.readString(
            FIXTURES.resolve(fixture + "-" + (suppress ? "plain" : "annotated") + ".txt"));
    assertEquals(expected, body(output));
    assertTrue(output.startsWith("/*\n" + Constants.APACHE_20_LICENCE_DISCLAIMER));
    assertFalse(output.contains(exporter.END_OF_CODE_BLOCK));
    assertFalse(output.contains("\r"));
    assertArrayEquals(
        original, new ConfigurationFileImport(new StringBuilder(output)).toByteArray());
    assertArrayEquals(original, source.toByteArray());
    // The historical suppression setting persists until explicitly changed.
    assertEquals(output, exporter.toPrettyPrint(false));
    assertEquals(output, exporter.toPrettyPrint(false, suppress));
    assertEquals(
        Files.readString(FIXTURES.resolve(fixture + "-annotated.txt")),
        body(exporter.toPrettyPrint(false, false)));
  }

  static Stream<Arguments> headers() {
    return Stream.of(
        Arguments.of(DOCSIS_10_CONFIGURATION_TYPE, "Docsis"),
        Arguments.of(DOCSIS_11_CONFIGURATION_TYPE, "Docsis"),
        Arguments.of(DOCSIS_20_CONFIGURATION_TYPE, "Docsis"),
        Arguments.of(DOCSIS_30_CONFIGURATION_TYPE, "Docsis"),
        Arguments.of(DOCSIS_31_CONFIGURATION_TYPE, "Docsis"),
        Arguments.of(DOCSIS_40_CONFIGURATION_TYPE, "Docsis-4.0"),
        Arguments.of(PKT_CABLE_10_CONFIGURATION_TYPE, "PacketCable-1.0"),
        Arguments.of(PKT_CABLE_15_CONFIGURATION_TYPE, "PacketCable-1.5"),
        Arguments.of(PKT_CABLE_20_CONFIGURATION_TYPE, "PacketCable-2.0"),
        Arguments.of(DPOE_10_CONFIGURATION_TYPE, "DPoE"),
        Arguments.of(DPOE_20_CONFIGURATION_TYPE, "DPoE"),
        Arguments.of(DPOG_10_CONFIGURATION_TYPE, "DPoG"));
  }

  @ParameterizedTest
  @MethodSource("headers")
  void referenceTemplatesKeepTheirHeadersAndDropEmptyContainers(int type, String header) {
    var exporter = new ConfigurationFileExport(type);
    assertEquals(header + " {\n}\n", body(exporter.toPrettyPrint(false, false)));
    assertTrue(body(exporter.toPrettyPrint(true, false)).startsWith(header + " {\n"));
  }

  @Test
  void constructorsProduceIdenticalOutputAndFileBytesMatchText() throws Exception {
    var imported = new ConfigurationFileImport(new StringBuilder("Docsis { NetworkAccess 1; }"));
    Path binary = temporary.resolve("input.cfg");
    Files.write(binary, imported.toByteArray());
    var builderExport =
        new ConfigurationFileExport(imported.getTlvBuilder(), DOCSIS_31_CONFIGURATION_TYPE);
    var fileExport = new ConfigurationFileExport(binary.toFile(), DOCSIS_31_CONFIGURATION_TYPE);
    var configurationExport =
        new ConfigurationFileExport(
            new ConfigurationFile(
                DOCSIS_31_CONFIGURATION_TYPE, imported.getTlvBuilder(), "public-lab-key"));
    String expected = builderExport.toPrettyPrint(false, false);
    assertEquals(expected, fileExport.toPrettyPrint(false, false));
    assertEquals(expected, configurationExport.toPrettyPrint(false, false));
    assertEquals("Docsis {\n\tNetworkAccess 1; /* TLV: [3]*/\n}\n", body(expected));
    Path text = temporary.resolve("output.txt");
    assertTrue(builderExport.writeToDisk(text.toFile()));
    assertArrayEquals(expected.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(text));
    assertFalse(builderExport.writeToDisk(temporary.toFile()));
    assertFalse(builderExport.writeToDisk(temporary.resolve("missing/output.txt").toFile()));
  }

  @Test
  void missingOrMalformedDefinitionPathsFailPromptlyAndContainersWork() {
    var exporter = new ConfigurationFileExport(DOCSIS_31_CONFIGURATION_TYPE);
    assertTrue(exporter.getTlvDefintion("25.1").contains("ServiceFlowReference"));
    assertTrue(exporter.getTlvDefintion("202.2").contains("TR-069ManagementServer"));
    for (String path : List.of("25.999", "25.1.1", "202.999", "999", "", "25.", "x", "25..1")) {
      assertTimeoutPreemptively(
          Duration.ofSeconds(1),
          () -> assertThrows(IllegalArgumentException.class, () -> exporter.getTlvDefintion(path)),
          path);
    }
  }

  @Test
  void malformedDecodedEntryCannotProducePartialOutputOrOverwriteFile() throws Exception {
    var builder = new TlvBuilder();
    builder.add(new HexString(HexString.toByteArray("030101")));
    var exporter = new ConfigurationFileExport(builder, DOCSIS_31_CONFIGURATION_TYPE);
    exporter.getTopLevelTLVJSON(3).getJSONObject(0).remove(Dictionary.TLV_NAME);
    Path destination = temporary.resolve("keep.txt");
    Files.writeString(destination, "keep");
    assertThrows(IllegalArgumentException.class, () -> exporter.toPrettyPrint(false));
    assertThrows(IllegalArgumentException.class, () -> exporter.writeToDisk(destination.toFile()));
    assertEquals("keep", Files.readString(destination));
  }

  @Test
  void emptySourcesAndUnknownFamiliesHaveClearErrors() throws Exception {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ConfigurationFileExport(new TlvBuilder(), DOCSIS_31_CONFIGURATION_TYPE));
    File empty = Files.createFile(temporary.resolve("empty.cfg")).toFile();
    assertThrows(
        IllegalArgumentException.class,
        () -> new ConfigurationFileExport(empty, DOCSIS_31_CONFIGURATION_TYPE));
    assertThrows(
        java.io.UncheckedIOException.class,
        () ->
            new ConfigurationFileExport(
                temporary.resolve("missing.cfg").toFile(), DOCSIS_31_CONFIGURATION_TYPE));
    assertThrows(
        IllegalArgumentException.class, () -> new ConfigurationFileExport(Integer.MAX_VALUE));
  }

  @Test
  void recursiveRenderingHasNoFixedIndentationDepth() throws Exception {
    var builder = new TlvBuilder();
    builder.add(new HexString(HexString.toByteArray("030101")));
    var exporter = new ConfigurationFileExport(builder, DOCSIS_31_CONFIGURATION_TYPE);
    JSONObject entry = exporter.getTopLevelTLVJSON(3).getJSONObject(0);
    JSONObject leaf = new JSONObject(entry.toString());
    var expected = new StringBuilder("Docsis {\n");
    int depth = 8;
    for (int level = 1; level <= depth; level++) {
      expected.append("\t".repeat(level)).append("Branch").append(level).append(" {\n");
      entry.put(Dictionary.TLV_NAME, "Branch" + level).put(Dictionary.ARE_SUBTYPES, true);
      JSONObject child = level == depth ? leaf : new JSONObject();
      entry.put(Dictionary.SUBTYPE_ARRAY, new JSONArray().put(child));
      entry = child;
    }
    expected.append("\t".repeat(depth + 1)).append("NetworkAccess 1; /* TLV: [3]*/\n");
    for (int level = depth; level >= 1; level--) expected.append("\t".repeat(level)).append("}\n");
    expected.append("}\n");
    assertEquals(expected.toString(), body(exporter.toPrettyPrint(false, false)));
  }

  private static String body(String text) {
    // Exclude only the versioned help/license banner; compare every body character verbatim.
    return text.substring(text.indexOf("*/") + "*/\n\n".length());
  }
}
