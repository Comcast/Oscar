/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class EmbeddedServiceCatalogTest {
  @Test
  void everyCatalogUsesTheSameDefinitionContract() throws Exception {
    JSONObject schema = new JSONObject(Files.readString(Path.of("docs/tlv-registry.schema.json")));
    Set<String> fields =
        Arrays.stream(TlvDefinition.class.getRecordComponents())
            .map(component -> component.getName())
            .collect(Collectors.toSet());
    assertEquals(
        fields,
        schema
            .getJSONObject("$defs")
            .getJSONObject("definition")
            .getJSONObject("properties")
            .keySet());
    try (var paths = Files.list(Path.of("res/tlv-registry"))) {
      for (Path path : paths.filter(p -> p.toString().endsWith(".json")).toList()) {
        JSONObject catalog = new JSONObject(Files.readString(path));
        assertTrue(
            schema.getJSONObject("properties").keySet().containsAll(catalog.keySet()),
            path.toString());
        for (Object required : schema.getJSONArray("required"))
          assertTrue(catalog.has((String) required));
        assertTrue(catalog.getString("sourceSha256").matches("[0-9a-f]{64}"));
        assertFalse(catalog.has("parentPath") && catalog.has("attachments"));
        for (Object value : catalog.getJSONArray("definitions")) {
          JSONObject entry = (JSONObject) value;
          assertEquals(fields, entry.keySet(), path + " " + entry.getJSONArray("path"));
        }
        catalog.remove("parentPath");
        try (var input =
            new ByteArrayInputStream(catalog.toString().getBytes(StandardCharsets.UTF_8))) {
          // Strict shared deserialization checks types, widths, codecs, parents, names and
          // duplicates.
          TlvRegistry.read(input, catalog.getString("family"), path.toString());
        }
      }
    }
  }

  @Test
  void rejectsAlternateDefinitionFieldsAndMissingRequiredFields() throws Exception {
    String source = Files.readString(Path.of("res/tlv-registry/opencable.json"));
    for (boolean missing : new boolean[] {false, true}) {
      JSONObject catalog = new JSONObject(source);
      catalog.remove("parentPath");
      JSONObject definition = catalog.getJSONArray("definitions").getJSONObject(0);
      if (missing) definition.remove("codec");
      else definition.put("kind", "unsignedInteger");
      try (var input =
          new ByteArrayInputStream(catalog.toString().getBytes(StandardCharsets.UTF_8))) {
        assertThrows(java.io.IOException.class, () -> TlvRegistry.read(input, "opencable", "test"));
      }
    }
  }

  @Test
  void deferredErouterDefinitionsAreNotRuntimeEntries() throws Exception {
    JSONObject catalog = new JSONObject(Files.readString(Path.of("res/tlv-registry/erouter.json")));
    Set<String> paths = new HashSet<>();
    for (Object value : catalog.getJSONArray("definitions"))
      paths.add(((JSONObject) value).getJSONArray("path").toString());
    assertFalse(paths.contains("[4]"));
    assertFalse(paths.contains("[54,2]"));
    String notes =
        Files.readString(Path.of("skills/oscar-erouter-config-tlvs/references/tlv-notes.md"));
    assertTrue(notes.contains("unresolved"));
    assertTrue(notes.contains("sensitive"));
  }
}
