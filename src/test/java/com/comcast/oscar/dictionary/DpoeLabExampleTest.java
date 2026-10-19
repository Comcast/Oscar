/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

/** Regression for the documented public-lab classifier/QoS subset, not full DPoE conformance. */
class DpoeLabExampleTest {
  @Test
  void preservesLabValuesAndExportReimport() throws Exception {
    String text = Files.readString(Path.of("examples/dpoe/2.0/lab-vlan1501-qos.txt"));
    var imported = new ConfigurationFileImport(new StringBuilder(text));
    // Independent values from the cited screenshot: 1001=03e9, 2001=07d1,
    // 100 Mbit/s=05f5e100, 2000=000007d0, VLAN 1501=05dd.
    // Child order below is OSCAR's current catalog order, not the screenshot's order.
    byte[] expected =
        HexFormat.of()
            .parseHex(
                "030101"
                    + "1816"
                    + "010203e9"
                    + "060107"
                    + "080405f5e100"
                    + "0f0104"
                    + "1104000007d0"
                    + "1613"
                    + "010133"
                    + "030203e9"
                    + "0d0440000000"
                    + "0e04020205dd"
                    + "190d"
                    + "010207d1"
                    + "060107"
                    + "080405f5e100"
                    + "170d"
                    + "01013d"
                    + "030207d1"
                    + "0e04020205dd");
    assertArrayEquals(expected, imported.toByteArray());
    var exported =
        new ConfigurationFileExport(imported.getTlvBuilder(), imported.getConfigurationFileType());
    var reimported =
        new ConfigurationFileImport(new StringBuilder(exported.toPrettyPrint(false, true)));
    assertArrayEquals(expected, reimported.toByteArray());
  }
}
