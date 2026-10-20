/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DpoeSnmpNamingTest {
  static java.util.stream.Stream<Integer> versions() {
    return java.util.stream.Stream.of(ConfigurationFile.DPOE_VER_10, ConfigurationFile.DPOE_VER_20);
  }

  @ParameterizedTest
  @MethodSource("versions")
  void nestedVarBindUsesSnmp11AndPreservesWireBytes(int version) throws Exception {
    var definition = TlvRegistry.forFamily("dpoe").find(List.of(217, 11));
    assertEquals("Snmp11", definition.name());
    assertEquals(1, definition.lengthBytes());
    String source = "DPoE { eSTBConfig { Snmp11 .1.3.6.1.2.1.1.5.0 OctetString \"lab\"; } }";
    var parsed = new ConfigurationFileImport(new StringBuilder(source));
    byte[] expected = HexFormat.of().parseHex("d9130b11300f06082b0601020101050004036c6162");
    assertArrayEquals(expected, parsed.toByteArray());
    var exporter = new ConfigurationFileExport(parsed.getTlvBuilder(), version);
    exporter.setDotTextOIDOutputFormat(ConfigurationFileExport.DOTTED_OID_FORMAT);
    String decoded = exporter.toPrettyPrint(false, true);
    assertTrue(decoded.contains("Snmp11"));
    assertFalse(decoded.contains("SnmpOID"));
    assertArrayEquals(
        expected, new ConfigurationFileImport(new StringBuilder(decoded)).toByteArray());
  }
}
