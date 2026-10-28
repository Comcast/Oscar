/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.utilities.TlvTextString;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Compare every composed registry path with its actual template hierarchy. */
class AllTlvTemplateCoverageTest {
  static Stream<Arguments> families() {
    return Stream.of(
        Arguments.of(DOCSIS_10_CONFIGURATION_TYPE, "docsis"),
        Arguments.of(DOCSIS_11_CONFIGURATION_TYPE, "docsis"),
        Arguments.of(DOCSIS_20_CONFIGURATION_TYPE, "docsis"),
        Arguments.of(DOCSIS_30_CONFIGURATION_TYPE, "docsis"),
        Arguments.of(DOCSIS_31_CONFIGURATION_TYPE, "docsis"),
        Arguments.of(DOCSIS_40_CONFIGURATION_TYPE, "docsis40"),
        Arguments.of(PKT_CABLE_10_CONFIGURATION_TYPE, "packetcable"),
        Arguments.of(PKT_CABLE_15_CONFIGURATION_TYPE, "packetcable"),
        Arguments.of(PKT_CABLE_20_CONFIGURATION_TYPE, "packetcable"),
        Arguments.of(DPOE_10_CONFIGURATION_TYPE, "dpoe"),
        Arguments.of(DPOE_20_CONFIGURATION_TYPE, "dpoe"),
        Arguments.of(DPOG_10_CONFIGURATION_TYPE, "dpog"));
  }

  @ParameterizedTest(name = "all TLVs: {1}, type {0}")
  @MethodSource("families")
  void everyRegistryDefinitionAppearsUnderItsOwnParent(int type, String family) {
    var expected = new LinkedHashMap<String, List<Integer>>();
    collect(registry(type, family), List.of(), "", expected);
    String output =
        new ConfigurationFileExport(type).toPrettyPrint(ConfigurationFileExport.EXPORT_DEFAULT_TLV);
    Set<String> actual = entries(output);
    var missing = new LinkedHashMap<>(expected);
    actual.forEach(missing::remove);
    // Empty VarBinds require OID/type/value and are intentionally omitted.
    // Keep them in expected so any emitted entry still needs its correct parent.
    var registry = registry(type, family);
    missing
        .entrySet()
        .removeIf(
            entry -> {
              var definition = registry.find(entry.getValue());
              return definition.codec().equals("oid")
                  && Set.of("Snmp11", "Snmp64").contains(definition.name());
            });
    assertTrue(missing.isEmpty(), () -> "Missing registry TLVs in all-TLV export: " + missing);
    var unexpected = new LinkedHashSet<>(actual);
    unexpected.removeAll(expected.keySet());
    assertTrue(unexpected.isEmpty(), () -> "Unexpected or misplaced template TLVs: " + unexpected);
  }

  private static TlvRegistry registry(int type, String family) {
    return family.equals(TlvRegistry.PACKETCABLE_FAMILY)
        ? TlvRegistry.forPacketCable(PacketCableVersion.fromConfigurationFileType(type))
        : TlvRegistry.forFamily(family);
  }

  private static void collect(
      TlvRegistry registry,
      List<Integer> parent,
      String prefix,
      Map<String, List<Integer>> result) {
    for (var definition : registry.children(parent)) {
      String name = prefix + definition.name();
      result.put(name, definition.path());
      collect(registry, definition.path(), name + "/", result);
    }
  }

  private static Set<String> entries(String output) {
    var parents = new ArrayList<String>();
    var result = new LinkedHashSet<String>();
    boolean rootSeen = false;
    for (String raw : TlvTextString.withoutComments(output).split("\\R")) {
      String line = raw.strip();
      if (line.isEmpty()) continue;
      if (line.startsWith("}")) {
        if (!parents.isEmpty()) parents.removeLast();
        continue;
      }
      boolean container = line.endsWith("{");
      if (container && !rootSeen) {
        rootSeen = true;
        continue;
      }
      assertTrue(rootSeen, "Unexpected content before configuration root: " + line);
      String name = line.split("[\\s;{]", 2)[0];
      String qualified = parents.isEmpty() ? name : String.join("/", parents) + "/" + name;
      assertTrue(result.add(qualified), "Duplicate template entry: " + qualified);
      if (container) parents.add(name);
      else assertTrue(line.endsWith(";"), "Unexpected template statement: " + line);
    }
    assertTrue(rootSeen, "Missing template root");
    assertTrue(parents.isEmpty(), "Unclosed template containers");
    return result;
  }
}
