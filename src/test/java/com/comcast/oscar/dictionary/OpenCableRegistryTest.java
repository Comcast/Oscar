/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OpenCableRegistryTest {
  @org.junit.jupiter.api.Test
  void repeatedContainersPreserveBoundariesAndEncounterOrder() throws Exception {
    assertEquals(-1, TlvRegistry.forFamily("docsis").find(List.of(217)).maxInstances());
    assertRoundTrip(
        "Docsis { eSTBConfig { VendorSpecificInformation { VendorID 001122; } }"
            + " NetworkAccess 1; eSTBConfig { IPModeControl 2; } }",
        HexFormat.of().parseHex("d9072b050803001122030101d903010102"));
    // Duplicating a container preserves bytes; device-level child semantics are separate.
    assertRoundTrip(
        "Docsis { eSTBConfig { VendorSpecificInformation { VendorID 001122; } }"
            + " eSTBConfig { VendorSpecificInformation { VendorID 001122; } } }",
        HexFormat.of().parseHex("d9072b050803001122d9072b050803001122"));
  }

  @org.junit.jupiter.api.Test
  void repeatedContainersPreserveFinalizedMicBytes() throws Exception {
    var imported =
        new ConfigurationFileImport(
            new StringBuilder(
                "Docsis { NetworkAccess 1; eSTBConfig { VendorSpecificInformation { VendorID"
                    + " 001122; } } eSTBConfig { IPModeControl 2; } }"));
    var original =
        new com.comcast.oscar.configurationfile.ConfigurationFile(
            imported.getConfigurationFileType(), imported.getTlvBuilder(), "public-lab-key");
    original.commit();
    var decoded =
        new ConfigurationFileImport(
            new StringBuilder(new ConfigurationFileExport(original).toPrettyPrint(false)));
    var rebuilt =
        new com.comcast.oscar.configurationfile.ConfigurationFile(
            decoded.getConfigurationFileType(), decoded.getTlvBuilder(), "public-lab-key");
    rebuilt.commit();
    assertArrayEquals(original.toByteArray(), rebuilt.toByteArray());
  }

  @org.junit.jupiter.api.Test
  void repeatedPrefixesPreserveInstances() throws Exception {
    String text =
        "Docsis { eSTBConfig { HomeNetworkPrefixValidation { InstanceNumber 2; PrefixUsage 2;"
            + " IPAddressVersion 1; IPv4PrefixLength 24; IPv4SubnetAddress 192.0.2.0; }"
            + " HomeNetworkPrefixValidation { InstanceNumber 1; PrefixUsage 1; IPAddressVersion 2;"
            + " IPv6PrefixLength 64; IPv6NetworkAddress 2001:db8:0:0:0:0:0:0; } } }";
    byte[] expected =
        HexFormat.of()
            .parseHex(
                "d93427120101020201020301010401180504c0000200"
                    + "271e010101020101030102060140071020010db8000000000000000000000000");
    assertRoundTrip(text, expected);
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.MethodSource("snmpAndVendorVectors")
  void snmpAndVendorBytes(String fragment, String hex) throws Exception {
    assertRoundTrip("Docsis { eSTBConfig { " + fragment + " } }", HexFormat.of().parseHex(hex));
  }

  static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments>
      snmpAndVendorVectors() {
    return java.util.stream.Stream.of(
        org.junit.jupiter.params.provider.Arguments.of(
            "Snmp11 .1.3.6.1.2.1.1.5.0 OctetString \"lab\";",
            "d9130b11300f06082b0601020101050004036c6162"),
        org.junit.jupiter.params.provider.Arguments.of(
            "SNMPv1v2cCoexistenceConfig { SNMPv1v2cCommunityName \"lab\";"
                + " SNMPv1v2cTransportAddressAccess { SNMPv1v2cTransportAddress 192.0.2.1(161);"
                + " SNMPv1v2cTransportMask 255.255.255.255(65535); } SNMPv1v2cAccessViewType 1;"
                + " SNMPv1v2cAccessViewName \"view\"; }",
            "d922352001036c616202100106c000020100a10206ffffffffffff030101040476696577"),
        org.junit.jupiter.params.provider.Arguments.of(
            "SNMPv3AccessViewConfig { SNMPv3AccessViewName \"view\"; SNMPv3AccessViewSubtree"
                + " .1.3.6; SNMPv3AccessViewMask \"FF\"; SNMPv3AccessViewType 1; }",
            "d9143612010476696577020406022b060301ff040101"),
        org.junit.jupiter.params.provider.Arguments.of(
            "VendorSpecificInformation { VendorID 00:11:22; }", "d9072b050803001122"),
        org.junit.jupiter.params.provider.Arguments.of(
            "SNMPv3NotificationReceiver { IPv4Address 192.0.2.1; UDPPortNumber 162; TrapType 2;"
                + " Timeout 1000; Retries 3; SnmpFilteringParameters .1.3.6; SecurityName \"lab\";"
                + " }",
            "d92326210104c0000201020200a203020002040203e805020003060406022b0607036c6162"));
  }

  @org.junit.jupiter.api.Test
  void ipv6TransportPortAndRepeatedAccessRows() throws Exception {
    String row =
        "SNMPv1v2cTransportAddressAccess { SNMPv1v2cTransportAddress 2001:db8:0:0:0:0:0:1(161); }";
    String fragment =
        "Docsis { eSTBConfig { SNMPv1v2cCoexistenceConfig { SNMPv1v2cCommunityName \"lab\"; "
            + row
            + row
            + " } } }";
    assertRoundTrip(
        fragment,
        HexFormat.of()
            .parseHex(
                "d933353101036c6162"
                    + "0214011220010db800000000000000000000000100a1"
                    + "0214011220010db800000000000000000000000100a1"));
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.ValueSource(
      ints = {0, 9, 10, 161, 162, 9999, 10000, 59999, 60000, 65535})
  void transportPortBoundaries(int port) throws Exception {
    String address = "192.0.2.1(" + port + ")";
    byte[] bytes =
        com.comcast.oscar.datatype.DataTypeFormatConversion.inetTransportAddressToByteArray(
            address);
    assertEquals(6, bytes.length);
    assertEquals(port, (Byte.toUnsignedInt(bytes[4]) << 8) | Byte.toUnsignedInt(bytes[5]));
    assertEquals(
        address,
        com.comcast.oscar.datatype.DataTypeFormatConversion.byteArrayToInetTransportAddress(bytes));
    assertEquals(
        address,
        com.comcast.oscar.datatype.DataTypeFormatConversion.byteArrayToIPv4TransportAddress(bytes));
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.ValueSource(
      strings = {"d903270201", "d903270001", "d903fa0100", "d90401020001"})
  void malformedOrUnknownChildrenAreNotSilentlyDropped(String hex) throws Exception {
    var bytes = new com.comcast.oscar.tlv.TlvBuilder();
    bytes.add(new com.comcast.oscar.utilities.HexString(HexFormat.of().parseHex(hex)));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConfigurationFileExport(
                    bytes, com.comcast.oscar.configurationfile.ConfigurationFile.DOCSIS_VER_31)
                .toPrettyPrint(false, true));
  }

  @org.junit.jupiter.api.Test
  void rejectsOutOfRangeTransportPorts() {
    for (String value : java.util.List.of("192.0.2.1(65536)", "192.0.2.1(-1)", "192.0.2.1(999999)"))
      assertThrows(
          IllegalArgumentException.class,
          () ->
              com.comcast.oscar.datatype.DataTypeFormatConversion.inetTransportAddressToByteArray(
                  value));
  }

  @org.junit.jupiter.api.Test
  void repeatedVarBindsStayInsideTheirParent() throws Exception {
    assertRoundTrip(
        "Docsis { eSTBConfig { Snmp11 .1.3.6.1.2.1.1.5.0 OctetString \"one\"; Snmp11"
            + " .1.3.6.1.2.1.1.5.0 OctetString \"two\"; } }",
        HexFormat.of()
            .parseHex(
                "d9260b11300f06082b0601020101050004036f6e65"
                    + "0b11300f06082b06010201010500040374776f"));
  }

  private void assertRoundTrip(String text, byte[] expected) throws Exception {
    var parsed = new ConfigurationFileImport(new StringBuilder(text));
    assertArrayEquals(
        expected, parsed.toByteArray(), HexFormat.of().formatHex(parsed.toByteArray()));
    String decoded =
        new ConfigurationFileExport(parsed.getTlvBuilder(), parsed.getConfigurationFileType())
            .toPrettyPrint(false, true);
    assertArrayEquals(
        expected,
        assertDoesNotThrow(() -> new ConfigurationFileImport(new StringBuilder(decoded)), decoded)
            .toByteArray());
  }

  @ParameterizedTest
  @ValueSource(strings = {"Docsis"})
  void ipv6PrefixRoundTrips(String header) throws Exception {
    String text =
        header
            + " { eSTBConfig { HomeNetworkPrefixValidation { "
            + "InstanceNumber 1; PrefixUsage 1; IPAddressVersion 2; "
            + "IPv6PrefixLength 64; IPv6NetworkAddress 2001:db8:0:0:0:0:0:0; } } }";
    byte[] expected =
        HexFormat.of()
            .parseHex("d920271e010101020101030102060140071020010db8000000000000000000000000");
    var parsed = new ConfigurationFileImport(new StringBuilder(text));
    assertArrayEquals(
        expected, parsed.toByteArray(), HexFormat.of().formatHex(parsed.toByteArray()));
    String decoded =
        new ConfigurationFileExport(parsed.getTlvBuilder(), parsed.getConfigurationFileType())
            .toPrettyPrint(false, true);
    assertArrayEquals(
        expected,
        assertDoesNotThrow(() -> new ConfigurationFileImport(new StringBuilder(decoded)), decoded)
            .toByteArray());
  }

  @ParameterizedTest
  @ValueSource(strings = {"Docsis"})
  void sourceDerivedPrefixAndModeBytesRoundTrip(String header) throws Exception {
    // HOST2.1-CFR I17 15.2.6, 15.2.8 and 15.2.9: IP mode differs from eRouter.
    String text =
        header
            + """
              {
                eSTBConfig {
                  IPModeControl 2;
                  HomeNetworkPrefixValidation {
                    InstanceNumber 1; PrefixUsage 1; IPAddressVersion 1;
                    IPv4PrefixLength 24; IPv4SubnetAddress 192.0.2.0;
                  }
                  SEBServerEnableTLSCipherSuites 224;
                }
              }
              """;
    var parsed = new ConfigurationFileImport(new StringBuilder(text));
    byte[] expected =
        HexFormat.of().parseHex("d91a01010227120101010201010301010401180504c00002002801e0");
    assertArrayEquals(
        expected, parsed.toByteArray(), HexFormat.of().formatHex(parsed.toByteArray()));
    String decoded =
        new ConfigurationFileExport(parsed.getTlvBuilder(), parsed.getConfigurationFileType())
            .toPrettyPrint(false, true);
    assertArrayEquals(
        expected,
        assertDoesNotThrow(() -> new ConfigurationFileImport(new StringBuilder(decoded)), decoded)
            .toByteArray());
  }

  @ParameterizedTest
  @ValueSource(strings = {"docsis"})
  void resolvesSharedDefinitionsWithoutConfusingErouter(String family) {
    var registry = TlvRegistry.forFamily(family);
    assertEquals("IPModeControl", registry.find(List.of(217, 1)).name());
    assertEquals("InitializationMode", registry.find(List.of(202, 1)).name());
    assertEquals(4, registry.find(List.of(217, 39, 5)).lengthMin());
    assertEquals(16, registry.find(List.of(217, 39, 7)).lengthMin());
    assertTrue(registry.find(List.of(217, 1)).description().contains("15.2.6"));
    assertNull(TlvRegistry.forFamily("docsis40").find(List.of(217)));
  }
}
