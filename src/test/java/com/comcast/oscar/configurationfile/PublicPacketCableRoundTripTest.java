/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvVariableBinding;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Independent upstream wire fixtures; provenance and licensing live beside the resources. */
class PublicPacketCableRoundTripTest {
  private static final String RESOURCE_DIRECTORY = "/packetcable-public/";

  static Stream<Arguments> fixtures() {
    return Stream.of(PacketCableVersion.PACKETCABLE_1_5, PacketCableVersion.PACKETCABLE_2_0)
        .flatMap(
            version ->
                Stream.of(
                    Arguments.of(
                        version,
                        "packetcable_digitmap.bin",
                        9696,
                        "c95f601becd0c946d2b6635fde759b280c3b02d66069e26176f24820325c25a3"),
                    Arguments.of(
                        version,
                        "PacketCable_TLV64.cm",
                        536,
                        "58f5b375528103ff511c66eefe7c98a063ea35d5fcaf59da227280d4fc387bea")));
  }

  @ParameterizedTest(name = "{0}: {1}")
  @MethodSource("fixtures")
  void preservesIndependentPublicBinary(
      PacketCableVersion version, String fixture, int expectedLength, String expectedSha256)
      throws Exception {
    byte[] original;
    try (var input = getClass().getResourceAsStream(RESOURCE_DIRECTORY + fixture)) {
      assertNotNull(input, "Missing public fixture: " + fixture);
      original = input.readAllBytes();
    }
    assertEquals(expectedLength, original.length);
    assertEquals(
        expectedSha256,
        HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(original)));
    var builder = new TlvBuilder();
    builder.add(
        new TlvVariableBinding(
            original,
            new TlvDictionary(TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME)
                .getTopLevelByteLength()));
    var exporter = new ConfigurationFileExport(builder, version.configurationFileType());
    exporter.setDotTextOIDOutputFormat(ConfigurationFileExport.DOTTED_OID_FORMAT);
    exporter.setTemplateBannerLevel(TemplateBannerLevel.DISABLE);
    String text = exporter.toPrettyPrint(false, true);
    assertTrue(text.contains("Snmp64"), "Extended-length VarBind must survive export");
    byte[] rebuilt = new ConfigurationFileImport(new StringBuilder(text)).toByteArray();
    assertArrayEquals(original, rebuilt, "Public binary changed during round trip: " + fixture);
  }
}
