/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class Docsis31I25CatalogTest {
  @Test
  void includesAllPreviouslyMissingConfigurationTypes() {
    var registry = TlvRegistry.forFamily(TlvRegistry.DOCSIS_FAMILY);
    // MULPI I25 Table 108, pp. 594-596; these are configuration settings, not REG-only types.
    for (int type : new int[] {45, 76, 79, 81, 82, 83, 84, 88, 91, 92, 93, 94, 101, 102}) {
      assertNotNull(registry.find(List.of(type)), "Missing configuration type " + type);
    }
  }

  @Test
  void includesConfigurationChildrenButNotResponseOnlyChildren() {
    var registry = TlvRegistry.forFamily(TlvRegistry.DOCSIS_FAMILY);
    for (var path :
        List.of(
            List.of(79, 9),
            List.of(84, 3),
            List.of(93, 47, 1),
            List.of(94, 47, 5),
            List.of(101, 4),
            List.of(102, 3),
            List.of(24, 40, 6),
            List.of(70, 42, 10))) {
      assertNotNull(registry.find(path), "Missing configuration path " + path);
    }
    assertNull(registry.find(List.of(93, 2)));
    assertNull(registry.find(List.of(93, 47, 2)));
    assertNull(registry.find(List.of(70, 42, 2)));
  }

  @Test
  void coversI25ConfigurationTableAndKeepsReservedTypesOut() {
    var registry = TlvRegistry.forFamily(TlvRegistry.DOCSIS_FAMILY);
    // Table 108 Cfg File column; eSAFE services are delegated separately.
    int[] configurationTypes = {
      0, 1, 2, 3, 6, 7, 9, 10, 11, 14, 17, 18, 19, 20, 21, 22, 23, 24, 25, 28, 29, 32, 33, 34, 35,
      36, 37, 38, 39, 40, 41, 42, 43, 45, 53, 54, 55, 56, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67,
      68, 69, 70, 71, 72, 73, 74, 76, 79, 81, 82, 83, 84, 88, 91, 92, 93, 94, 101, 102, 255
    };
    for (int type : configurationTypes) assertNotNull(registry.find(List.of(type)), "Type " + type);
    for (int type : new int[] {85, 86, 87, 96, 97, 98, 99, 100, 103, 104}) {
      assertNull(registry.find(List.of(type)), "Not an I25 configuration encoding: " + type);
    }
    assertEquals("ClassicServiceClassName", registry.find(List.of(70, 42, 3)).name());
    assertEquals("LowLatencyServiceClassName", registry.find(List.of(70, 42, 4)).name());
    assertEquals(-1, registry.find(List.of(70, 38, 1)).maxInstances());
    assertEquals("byte_array", registry.find(List.of(88)).codec());
  }

  @Test
  void editionAndHashIdentifyTheBundledI25Source() throws Exception {
    var catalog =
        new org.json.JSONObject(
            java.nio.file.Files.readString(java.nio.file.Path.of("res/tlv-registry/docsis.json")));
    String edition = "CM-SP-MULPIv3.1-I25-230419";
    byte[] source =
        java.nio.file.Files.readAllBytes(
            java.nio.file.Path.of(
                "skills/oscar-docsis-31-config-tlvs/references/sources/" + edition + ".txt"));
    assertEquals(edition, catalog.getString("edition"));
    assertEquals(
        java.util.HexFormat.of()
            .formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(source)),
        catalog.getString("sourceSha256"));
  }

  @Test
  void allowsRepeatedChannelListsAndAggregateFlows() {
    var registry = TlvRegistry.forFamily(TlvRegistry.DOCSIS_FAMILY);
    // I25 C.1.1.22 permits repeated lists/channels; 7.7.4 supports multiple ASFs.
    for (var path :
        List.of(List.of(41), List.of(41, 1), List.of(41, 2), List.of(70), List.of(71))) {
      assertEquals(-1, registry.find(path).maxInstances(), "Repeatable " + path);
    }
  }
}
