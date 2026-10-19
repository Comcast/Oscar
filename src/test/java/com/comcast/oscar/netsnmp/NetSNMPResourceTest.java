/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.netsnmp;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NetSNMPResourceTest {
  @Test
  void trackedMapIsReadableAndContainsOnlyOidNamePairs() throws Exception {
    Path source = Path.of("res", Constants.DOTTED_TEXTUAL_NetSNMP_MAP_FILE);
    String json = Files.readString(source);
    var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enable(com.fasterxml.jackson.core.JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    mapper.enable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    var mappings = mapper.readTree(json);
    assertTrue(mappings.isObject());
    assertFalse(mappings.isEmpty());
    assertTrue(
        json.lines().count() >= mappings.size() + 2L,
        "Keep one mapping per line; runtime cache writes must not flatten this resource");
    var oidPattern = java.util.regex.Pattern.compile("\\.?[0-2](?:\\.[0-9]+)+");
    var entries = mappings.fields();
    while (entries.hasNext()) {
      var entry = entries.next();
      assertTrue(entry.getValue().isTextual(), entry.getKey());
      String value = entry.getValue().textValue();
      assertFalse(entry.getKey().isBlank());
      assertFalse(value.isBlank(), entry.getKey());
      assertNotEquals(
          oidPattern.matcher(entry.getKey()).matches(),
          oidPattern.matcher(value).matches(),
          "Expected one numeric OID and one name: " + entry);
    }
    assertEquals(".1.3.6.1.6.3.15.1.2.2.1.6", mappings.path("usmUserAuthKeyChange").asText());
    assertEquals(".1.3.6.1.6.3.16.1.2.1.3", mappings.path("vacmGroupName").asText());
  }

  @Test
  void testCacheCannotOverwriteTrackedResource() throws Exception {
    Path source = Path.of("res", Constants.DOTTED_TEXTUAL_NetSNMP_MAP_FILE).toRealPath();
    Path cache =
        NetSNMP.DirectoryStructureNetSNMP.fNetSNMPJSON().toPath().toAbsolutePath().normalize();
    assertNotEquals(source, cache);
    assertTrue(cache.startsWith(Path.of("target").toRealPath()));
  }

  @Test
  void initializesMissingAndEmptyCachesFromBundledMap(@TempDir Path directory) throws Exception {
    Path cache = directory.resolve("res").resolve(Constants.DOTTED_TEXTUAL_NetSNMP_MAP_FILE);
    try (var bundled =
        getClass().getResourceAsStream("/" + Constants.DOTTED_TEXTUAL_NetSNMP_MAP_FILE)) {
      assertNotNull(bundled);
      byte[] expected = bundled.readAllBytes();
      NetSNMP.initializeCache(cache.toFile());
      assertArrayEquals(expected, Files.readAllBytes(cache));
      Files.write(cache, new byte[0]);
      NetSNMP.initializeCache(cache.toFile());
      assertArrayEquals(expected, Files.readAllBytes(cache));
    }
    assertFalse(Files.exists(directory.resolve("db")));
    assertNull(getClass().getResource("/dictionary.sqlite"));
  }

  @Test
  void preservesExistingCache(@TempDir Path directory) throws Exception {
    Path cache = directory.resolve(Constants.DOTTED_TEXTUAL_NetSNMP_MAP_FILE);
    String existing = "{\"customName\": \".1.3.6.1.4.1\"}";
    Files.writeString(cache, existing);
    NetSNMP.initializeCache(cache.toFile());
    assertEquals(existing, Files.readString(cache));
  }

  @Test
  void securityObjectsResolveFromBundledMap() {
    String[][] mappings = {
      {"usmUserSecurityName", ".1.3.6.1.6.3.15.1.2.2.1.3"},
      {"usmUserAuthKeyChange", ".1.3.6.1.6.3.15.1.2.2.1.6"},
      {"usmUserPrivKeyChange", ".1.3.6.1.6.3.15.1.2.2.1.9"},
      {"vacmGroupName", ".1.3.6.1.6.3.16.1.2.1.3"},
      {"snmpCommunityName", ".1.3.6.1.6.3.18.1.1.1.2"},
      {"usmDHUserAuthKeyChange", ".1.3.6.1.3.101.1.1.2.1.1"}
    };
    for (String[] mapping : mappings) {
      assertEquals(mapping[1], NetSNMP.toDottedOID(mapping[0]));
      assertEquals(mapping[0], NetSNMP.toTextualOID(mapping[1]));
    }
  }

  @Test
  void resourceMapSupportsBidirectionalLookups() {
    assertEquals("res", NetSNMP.DirectoryStructureNetSNMP.fNetSNMPJSON().getParentFile().getName());
    assertEquals(".1.3.6.1.2.1.69.1.2.1.2.1", NetSNMP.toDottedOID("docsDevNmAccessIp.1"));
    assertEquals("docsDevNmAccessIp.1", NetSNMP.toTextualOID("1.3.6.1.2.1.69.1.2.1.2.1"));
  }
}
