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
