/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmbeddedProfileTest {
  @ParameterizedTest
  @ValueSource(strings = {"Docsis", "Docsis-4.0"})
  void preservesRepeatedContainersAndIndependentWireBytes(String header) throws Exception {
    String text =
        header
            + """
               {
                eRouterConfig { InitializationMode 3; }
                eRouterConfig { RouterAdvertisementTransInterval 30; }
              }
              """;
    var parsed = new ConfigurationFileImport(new StringBuilder(text));
    byte[] expected = java.util.HexFormat.of().parseHex("ca03010103ca040a02001e");
    assertArrayEquals(expected, parsed.toByteArray());
    String decoded =
        new ConfigurationFileExport(parsed.getTlvBuilder(), parsed.getConfigurationFileType())
            .toPrettyPrint(false, true);
    assertArrayEquals(
        expected, new ConfigurationFileImport(new StringBuilder(decoded)).toByteArray());
  }

  // CM-SP-eRouter-I21-220209 B.3, p.87: successive 202 containers form one
  // configuration in encounter order. Each singleton child occurs only once.
  private static final String SPLIT_EROUTER =
      """
      eRouterConfig { RouterAdvertisementTransInterval 30; }
      eRouterConfig { TR-069ManagementServer { EnableCWMP 1; Username "x"; } }
      eRouterConfig { InitializationMode 3; }
      """;

  @ParameterizedTest
  @ValueSource(strings = {"Docsis", "Docsis-4.0"})
  void preservesNonNumericContainerOrderAndInterveningSibling(String header) throws Exception {
    String fragment = SPLIT_EROUTER.replaceFirst("}\\n", "}\nNetworkAccess 1;\n");
    var parsed = new ConfigurationFileImport(new StringBuilder(header + " { " + fragment + " }"));
    // Independent wire oracle: RA first, network sibling, nested CWMP, then init.
    byte[] expected =
        java.util.HexFormat.of().parseHex("ca040a02001e030101ca080206010101030178ca03010103");
    assertArrayEquals(expected, parsed.toByteArray());
    String decoded =
        new ConfigurationFileExport(parsed.getTlvBuilder(), parsed.getConfigurationFileType())
            .toPrettyPrint(false, true);
    assertArrayEquals(
        expected, new ConfigurationFileImport(new StringBuilder(decoded)).toByteArray());
  }

  @ParameterizedTest
  @ValueSource(strings = {"3.1", "4.0"})
  void repeatedContainersSurviveCompleteFileIntegrityRebuild(String version) throws Exception {
    String base =
        Files.readString(Path.of("examples/docsis", version, "DOCSIS-" + version + "-LAB.txt"));
    var imported =
        new ConfigurationFileImport(
            new StringBuilder(base.substring(0, base.lastIndexOf('}')) + SPLIT_EROUTER + "}"));
    var file =
        new com.comcast.oscar.configurationfile.ConfigurationFile(
            imported.getConfigurationFileType(), imported.getTlvBuilder(), "SHAREDSECRET");
    file.commit();
    byte[] bytes = file.toByteArray();
    var containers = new java.io.ByteArrayOutputStream();
    int count = 0;
    for (int offset = 0; offset < bytes.length; ) {
      int type = Byte.toUnsignedInt(bytes[offset]);
      if (type == 255) break;
      if (type == 0) {
        offset++;
        continue;
      }
      assertTrue(offset + 1 < bytes.length);
      int length = Byte.toUnsignedInt(bytes[offset + 1]) + 2;
      assertTrue(offset + length <= bytes.length);
      if (type == 202) {
        containers.write(bytes, offset, length);
        count++;
      }
      offset += length;
    }
    assertEquals(3, count);
    assertArrayEquals(
        java.util.HexFormat.of().parseHex("ca040a02001eca080206010101030178ca03010103"),
        containers.toByteArray());
    var decoded =
        new ConfigurationFileImport(
            new StringBuilder(new ConfigurationFileExport(file).toPrettyPrint(false)));
    var rebuilt =
        new com.comcast.oscar.configurationfile.ConfigurationFile(
            decoded.getConfigurationFileType(), decoded.getTlvBuilder(), "SHAREDSECRET");
    rebuilt.commit();
    assertArrayEquals(bytes, rebuilt.toByteArray());
  }

  @ParameterizedTest
  @ValueSource(strings = {"3.1", "4.0"})
  void completeLabFilesWithErouterRoundTrip(String version) throws Exception {
    Path root = Path.of("examples/docsis");
    String base = Files.readString(root.resolve(version + "/DOCSIS-" + version + "-LAB.txt"));
    for (String name : List.of("disabled", "dual-stack", "tr069-lab")) {
      String fragment = Files.readString(root.resolve("3.1/erouter/" + name + ".txt"));
      String children =
          fragment
              .substring(fragment.indexOf('{') + 1, fragment.lastIndexOf('}'))
              .replace("NetworkAccess 1;", "");
      String text = base.substring(0, base.lastIndexOf('}')) + children + "}";
      if (version.equals("4.0"))
        text = Files.readString(root.resolve("4.0/erouter/" + name + ".txt"));
      var imported = new ConfigurationFileImport(new StringBuilder(text));
      var file =
          new com.comcast.oscar.configurationfile.ConfigurationFile(
              imported.getConfigurationFileType(), imported.getTlvBuilder(), "SHAREDSECRET");
      file.commit();
      var decoded =
          new ConfigurationFileImport(
              new StringBuilder(new ConfigurationFileExport(file).toPrettyPrint(false)));
      var rebuilt =
          new com.comcast.oscar.configurationfile.ConfigurationFile(
              decoded.getConfigurationFileType(), decoded.getTlvBuilder(), "SHAREDSECRET");
      rebuilt.commit();
      assertArrayEquals(file.toByteArray(), rebuilt.toByteArray(), version + " " + name);
    }
  }

  @Test
  void bothProfilesResolveSharedDefinitionsWithoutEnablingAmbiguitiesInD4() {
    for (var path : List.of(List.of(202, 1), List.of(202, 2, 2), List.of(202, 10))) {
      var legacy = TlvRegistry.forFamily("docsis").find(path);
      var modern = TlvRegistry.forFamily("docsis40").find(path);
      assertEquals(legacy.name(), modern.name());
      assertEquals(legacy.codec(), modern.codec());
      assertEquals(legacy.lengthMin(), modern.lengthMin());
    }
    assertNull(TlvRegistry.forFamily("docsis40").find(List.of(202, 2, 7)));
    assertNull(TlvRegistry.forFamily("docsis40").find(List.of(202, 4)));
    assertNull(TlvRegistry.forFamily("docsis40").find(List.of(201)));
  }

  @Test
  void rejectsInvalidAttachmentsBeforePublishingRegistry() throws Exception {
    Path directory = Path.of("res/tlv-registry");
    TlvRegistry base;
    try (var input = Files.newInputStream(directory.resolve("docsis40.json"))) {
      base = TlvRegistry.read(input, "docsis40", "test base");
    }
    for (String paths : List.of("[[4]]", "[[2,2]]", "[[1],[1]]", "[[250]]")) {
      JSONObject profile = new JSONObject(Files.readString(directory.resolve("docsis40.json")));
      profile
          .getJSONArray("attachments")
          .getJSONObject(0)
          .put("includedPaths", new org.json.JSONArray(paths));
      TlvRegistry baseRegistry = base;
      assertThrows(
          IllegalArgumentException.class,
          () ->
              TlvRegistry.compose(
                  baseRegistry,
                  profile,
                  name -> Files.newInputStream(directory.resolve(name)),
                  "docsis40"));
    }
    for (String catalog : List.of("../erouter.json", "/erouter.json")) {
      JSONObject profile = new JSONObject(Files.readString(directory.resolve("docsis40.json")));
      profile.getJSONArray("attachments").getJSONObject(0).put("reference", catalog);
      TlvRegistry baseRegistry = base;
      assertThrows(
          IllegalArgumentException.class,
          () ->
              TlvRegistry.compose(
                  baseRegistry,
                  profile,
                  name -> {
                    throw new AssertionError("Must reject before opening a file");
                  },
                  "docsis40"));
    }
  }
}
