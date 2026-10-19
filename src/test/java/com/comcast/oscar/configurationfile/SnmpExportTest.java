/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SnmpExportTest {
  private record Varbind(String type, String value) {}

  @ParameterizedTest
  @ValueSource(strings = {"PacketCable-1.0", "PacketCable-1.5", "PacketCable-2.0"})
  void preservesSnmpValueTypesAndBytesRegardlessOfLengthField(String header) throws Exception {
    var varbinds =
        List.of(
            new Varbind("IpAddress", "192.0.2.1"),
            new Varbind("Integer32", "-42"),
            new Varbind("Counter32", "4294967295"),
            new Varbind("Gauge32", "12345"),
            new Varbind("OctetString", "synthetic-lab"),
            new Varbind("HexString", "00:01:ff"));
    for (String tlvName : List.of("Snmp11", "Snmp64")) {
      for (var varbind : varbinds) {
        String statement =
            tlvName + " 1.3.6.1.2.1.1.5.0 " + varbind.type() + " \"" + varbind.value() + "\";";
        var imported =
            new ConfigurationFileImport(
                new StringBuilder(
                    header
                        + " { TelephonyConfigFileBeginEnd 1; "
                        + statement
                        + " TelephonyConfigFileBeginEnd 255; }"));
        byte[] original = imported.toByteArray();
        var exporter =
            new ConfigurationFileExport(
                imported.getTlvBuilder(), imported.getConfigurationFileType());
        exporter.setDotTextOIDOutputFormat(ConfigurationFileExport.DOTTED_OID_FORMAT);
        String exported = exporter.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV, false);
        assertTrue(
            exported
                .toLowerCase()
                .contains((varbind.type() + " \"" + varbind.value() + "\";").toLowerCase()),
            header + " " + tlvName + " " + varbind.type() + ": " + exported);
        var reimported =
            new ConfigurationFileImport(
                new StringBuilder(exported.replace("PacketCable-X.X", header)));
        assertArrayEquals(
            original, reimported.toByteArray(), header + " " + tlvName + " " + varbind.type());
      }
    }
  }
}
