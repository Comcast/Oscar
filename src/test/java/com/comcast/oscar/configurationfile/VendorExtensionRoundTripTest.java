/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.utilities.HexString;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class VendorExtensionRoundTripTest {
  private String export(String hex, DocsisVersion version) throws Exception {
    var builder = new TlvBuilder();
    builder.add(new HexString(HexFormat.of().parseHex(hex)));
    return new ConfigurationFileExport(builder, version.configurationFileType())
        .toPrettyPrint(false, true);
  }

  @Test
  void preservesVendorChildrenAndOverlappingStandardNumbers() throws Exception {
    for (DocsisVersion version :
        new DocsisVersion[] {DocsisVersion.DOCSIS_3_1, DocsisVersion.DOCSIS_4_0}) {
      for (String hex :
          new String[] {
            "2b0f08030005caf2080001020304050607",
            "2b0e08030005ca0101abf201cd0101ef",
            "2b080803fffffff201ab",
            "2b0b0803ffffff010400000001",
            "2b0708030005caf200",
            "2b0808030005caf201ab2b0808030005caf201cd",
            "180a2b0808030005caf201ab",
            "160a2b0808030005caf201ab"
          }) {
        // D4 nested classifier/service-flow extensions are not yet in its registry.
        if (version == DocsisVersion.DOCSIS_4_0 && !hex.startsWith("2b")) {
          continue;
        }
        String text = export(hex, version);
        assertArrayEquals(
            HexFormat.of().parseHex(hex),
            new ConfigurationFileImport(new StringBuilder(text)).toByteArray(),
            text);
      }
      String text = export("2b0e08030005ca0101abf201cd0101ef", version);
      assertTrue(text.contains("RawTlv1"));
      assertTrue(text.contains("RawTlv242"));
      assertArrayEquals(
          HexFormat.of().parseHex("2b0e08030005ca0101abf201aa0101ef"),
          new ConfigurationFileImport(
                  new StringBuilder(text.replace("cd", "aa").replace("CD", "AA")))
              .toByteArray());
    }
  }

  @Test
  void rejectsMalformedVendorContainers() {
    for (String hex :
        new String[] {
          "2b03f201ab",
          "2b0408020005",
          "2b0a08030005ca08030005ca",
          "2b0808030005caf202ab",
          "2b0608030005caf2"
        }) {
      assertThrows(
          IllegalArgumentException.class, () -> export(hex, DocsisVersion.DOCSIS_3_1), hex);
    }
  }

  @Test
  void rejectsMalformedTextAndOutOfScopeRawChildren() {
    for (String body :
        new String[] {
          "VendorSpecific { RawTlv242 AA; }",
          "VendorSpecific { VendorId 00:05; RawTlv242 AA; }",
          "VendorSpecific { VendorId 00:05:CA; VendorId 00:05:CA; }",
          "VendorSpecific { VendorId 00:05:CA; RawTlv8 AA; }",
          "VendorSpecific { VendorId 00:05:CA; RawTlv256 AA; }",
          "VendorSpecific { VendorId 00:05:CA; RawTlv242 \"xyz\"; }",
          "VendorSpecific { VendorId 00:05:CA; RawTlv242 \"abc\"; }",
          "VendorSpecific { VendorId 00:05:CA; CMLoadBalancingID 1; }",
          "UpstreamServiceFlow { RawTlv242 AA; }",
          "RawTlv242 AA;",
          "VendorSpecific { VendorId 00:05:CA; RawTlv242 \"" + "ab".repeat(248) + "\"; }"
        }) {
      assertThrows(
          RuntimeException.class,
          () ->
              new ConfigurationFileImport(new StringBuilder("Docsis { " + body + " }"))
                  .toByteArray(),
          body);
    }
  }

  @Test
  void validatesNestedOpaqueVendorContainers() throws Exception {
    assertThrows(
        IllegalArgumentException.class, () -> export("18052b03f201ab", DocsisVersion.DOCSIS_3_1));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConfigurationFileImport(
                    new StringBuilder(
                        "Docsis { UpstreamServiceFlow { VendorSpecificQoS F2:01:AB; } }"))
                .toByteArray());
    assertThrows(
        IllegalArgumentException.class,
        () -> export("2b0c0803ffffff01050000000001", DocsisVersion.DOCSIS_3_1));
  }

  @Test
  void exposesOpaqueInstanceMetadataAndRetainsValuelessTemplates() throws Exception {
    var builder = new TlvBuilder();
    builder.add(new HexString(HexFormat.of().parseHex("2b0808030005caf201ab")));
    var exporter =
        new ConfigurationFileExport(builder, DocsisVersion.DOCSIS_3_1.configurationFileType());
    var raw =
        exporter
            .getTopLevelTLVJSON(43)
            .getJSONObject(0)
            .getJSONArray("SubTypeArray")
            .getJSONObject(1);
    assertEquals(242, raw.getInt("Type"));
    assertEquals("[-1,43,242]", raw.getJSONArray("ParentTypeList").toString());
    assertEquals("byte_array", raw.getString("DataType"));
    assertEquals("RawTlv242", raw.getString("TlvName"));
    assertArrayEquals(
        HexFormat.of().parseHex("2b0508030005ca"),
        new ConfigurationFileImport(
                new StringBuilder("Docsis { VendorSpecific { VendorId 00:05:CA; RawTlv242; } }"))
            .toByteArray());
  }
}
