/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.utilities.HexString;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.Test;

class PacketCableProfileTest {
  @Test
  void selectsProfilesThroughPublicApi() {
    assertNull(TlvRegistry.forPacketCable(PacketCableVersion.PACKETCABLE_1_5).find(List.of(38, 8)));
    assertNotNull(
        TlvRegistry.forPacketCable(PacketCableVersion.PACKETCABLE_2_0).find(List.of(38, 8)));
    assertThrows(
        IllegalArgumentException.class,
        () -> PacketCableVersion.fromConfigurationFileType(ConfigurationFile.DOCSIS_VER_31));
  }

  @Test
  void validatesVendorSuboptionBoundsAndAsciiWithoutNormalizing() {
    for (String payload : new String[] {"0806303030354341", "0806303030356361f200"}) {
      byte[] value = HexFormat.of().parseHex(payload);
      var builder = new TlvBuilder();
      assertDoesNotThrow(() -> builder.add(43, value));
      assertDoesNotThrow(
          () ->
              PacketCablePayloadValidation.validate(
                  builder.toByteArray(), PacketCableVersion.PACKETCABLE_2_0));
    }
    for (String hex :
        new String[] {
          "2b", "2b0408063030", "2b08080661613a62623a", "2b080806fffffffffffe", "2b0108"
        }) {
      assertThrows(
          IllegalArgumentException.class,
          () ->
              PacketCablePayloadValidation.validate(
                  HexFormat.of().parseHex(hex), PacketCableVersion.PACKETCABLE_2_0),
          hex);
    }
  }

  @Test
  void roundTripsIpv6ReceiverOnlyUnder20() throws Exception {
    String hex = "2616081020010db800000000000000000000000103020002";
    byte[] expected = HexFormat.of().parseHex(hex);
    var builder = new TlvBuilder();
    builder.add(new HexString(expected));
    String text =
        new ConfigurationFileExport(builder, ConfigurationFile.PKT_CBL_VER_20)
            .toPrettyPrint(false, true);
    assertArrayEquals(expected, new ConfigurationFileImport(new StringBuilder(text)).toByteArray());
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConfigurationFileExport(builder, ConfigurationFile.PKT_CBL_VER_15)
                .toPrettyPrint(false, true));
  }

  @Test
  void preservesTwoByteSnmpLengthForLargeVarbind() throws Exception {
    String text =
        "PacketCable-2.0 { Snmp64 1.3.6.1.2.1.1.1.0 OctetString \"" + "x".repeat(300) + "\"; }";
    byte[] expected = new ConfigurationFileImport(new StringBuilder(text)).toByteArray();
    assertEquals(64, Byte.toUnsignedInt(expected[0]));
    assertTrue(expected.length > 254);
    var builder = new TlvBuilder();
    builder.add(
        new com.comcast.oscar.tlv.TlvVariableBinding(
            expected,
            new com.comcast.oscar.dictionary.TlvDictionary(
                    com.comcast.oscar.dictionary.TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME)
                .getTopLevelByteLength()));
    String exported =
        new ConfigurationFileExport(builder, ConfigurationFile.PKT_CBL_VER_20)
            .toPrettyPrint(false, true);
    assertArrayEquals(
        expected, new ConfigurationFileImport(new StringBuilder(exported)).toByteArray());
  }

  @Test
  void rejectsBinaryOuiInPacketCable20() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConfigurationFileImport(
                    new StringBuilder("PacketCable-2.0 { VendorSpecific 08030005ca; }"))
                .toByteArray());
  }

  @Test
  void catalogDescribesContainersAndOpaqueVendorPayloads() {
    var registry = TlvRegistry.forFamily(TlvRegistry.PACKETCABLE_FAMILY);
    assertEquals("byte_array", registry.find(List.of(43)).codec());
    assertEquals("byte_array", registry.find(List.of(38)).codec());
    assertEquals(-1, registry.find(List.of(38)).maxInstances());
  }

  @Test
  void legacyTemplateDoesNotAdvertiseIpv6Receiver() {
    assertFalse(
        new ConfigurationFileExport(ConfigurationFile.PKT_CBL_VER_15)
            .toPrettyPrint(true, true)
            .contains("SNMPv3NRIPv6Address"));
    assertTrue(
        new ConfigurationFileExport(ConfigurationFile.PKT_CBL_VER_20)
            .toPrettyPrint(true, true)
            .contains("SNMPv3NRIPv6Address"));
  }

  @Test
  void vendorPayloadAndRepeatedReceiversRoundTripExactly() throws Exception {
    for (int version :
        new int[] {ConfigurationFile.PKT_CBL_VER_15, ConfigurationFile.PKT_CBL_VER_20}) {
      String vendor =
          version == ConfigurationFile.PKT_CBL_VER_20
              ? "2b0b0806303030354341f201ab"
              : "2b0808030005caf201ab";
      String hex = "fe0101" + vendor + "260a0104c000020103020002260a0104c000020203020003fe01ff";
      byte[] expected = HexFormat.of().parseHex(hex);
      var builder = new TlvBuilder();
      builder.add(new HexString(expected));
      String text = new ConfigurationFileExport(builder, version).toPrettyPrint(false, true);
      assertArrayEquals(
          expected, new ConfigurationFileImport(new StringBuilder(text)).toByteArray(), text);
    }
  }
}
