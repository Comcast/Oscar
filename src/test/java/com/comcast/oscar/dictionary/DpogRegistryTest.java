/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Applicability regressions; these do not certify inherited DOCSIS 3.0 wire encodings. */
class DpogRegistryTest {
  @Test
  void selectsDedicatedCatalogWithoutChangingDpoe() {
    var dictionary = new TlvDictionary(TlvDictionary.DPOG_DICTIONARY_TABLE_NAME);
    assertEquals(TlvDictionary.DPOG_DICTIONARY_TABLE_NAME, dictionary.getDictionaryTableName());
    assertEquals(
        dictionary.getTopLevelTLV().size(),
        dictionary
            .getAllTlvDefinition(ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE)
            .length());
    assertNotNull(TlvRegistry.forFamily("dpoe").find(List.of(70)));
    assertNotNull(TlvRegistry.forFamily("dpoe").find(List.of(5)));
  }

  @Test
  void excludesDhcpAndUnsupportedFileSettings() {
    // C.1/Table 7, pp. 73–75: 5 is DHCP-only, the others MUST NOT be supported.
    var registry = TlvRegistry.forFamily("dpog");
    for (int tlvType : new int[] {1, 2, 4, 5, 17, 19, 20, 26, 39, 40, 41, 56, 65, 70, 71, 72, 73}) {
      assertNull(registry.find(List.of(tlvType)), "Unexpected DPoG TLV " + tlvType);
    }
    assertNotNull(registry.find(List.of(3)));
    assertNotNull(registry.find(List.of(11)));
  }

  @Test
  void distinguishesClassifierContextsAndServiceFlows() {
    var registry = TlvRegistry.forFamily("dpog");
    // C.4/Table 9 versus C.10/Table 15: VLAN/S-VLAN/I-TAG/MPLS differ by parent.
    for (int classifierType : new int[] {22, 23}) {
      for (int childType : new int[] {11, 14, 15, 17}) {
        assertNull(registry.find(List.of(classifierType, childType)));
      }

      assertNull(registry.find(List.of(classifierType, 10, 4)));
    }
    assertNotNull(registry.find(List.of(22, 13)));
    // 23.13 is required by DPoG, but lacks a legacy definition; do not invent its encoding.
    assertNull(registry.find(List.of(23, 13)));
    for (List<Integer> path :
        List.of(List.of(60, 11, 2), List.of(60, 14, 10), List.of(60, 15, 13), List.of(60, 17, 2))) {
      assertNotNull(registry.find(path), path.toString());
    }
    // C.5/Table 10: excluded concatenation and resequencing; retain SHOULD NOT latency.
    assertNull(registry.find(List.of(24, 14)));
    assertNull(registry.find(List.of(25, 17)));
    assertNotNull(registry.find(List.of(25, 14)));
    assertTrue(registry.find(List.of(24, 6)).description().contains("0x07"));
    assertTrue(registry.find(List.of(24, 15)).description().contains("other values MAY"));
  }

  @Test
  void preservesRecommendationAndOptionalMicDistinctions() {
    var registry = TlvRegistry.forFamily("dpog");
    for (List<Integer> path : List.of(List.of(6), List.of(7), List.of(43, 6))) {
      assertTrue(registry.find(path).description().contains("support: MAY"));
    }
    assertTrue(registry.find(List.of(10)).description().contains("support: SHOULD NOT"));
    assertTrue(registry.find(List.of(34)).description().contains("support: SHOULD"));
    assertNull(registry.find(List.of(43, 5)));
    assertNotNull(registry.find(List.of(43, 7, 2, 1)));
    assertNotNull(registry.find(List.of(53, 2, 2)));
    assertTrue(registry.find(List.of(3)).description().contains("unverified"));
  }
}
