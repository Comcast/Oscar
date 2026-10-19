/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvVariableBinding;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Complete in-memory API round trips; no fixture files or text repair. */
class ConfigurationApiRoundTripTest {
  private static final String LAB_KEY = "SHAREDSECRET";
  private static final String SNMP =
      """
      Snmp11 1.3.6.1.2.1.1.5.0 OctetString "synthetic-lab";
      Snmp11 1.3.6.1.2.1.1.6.0 OctetString "test-location";
      """;

  @ParameterizedTest(name = "{0}")
  @MethodSource("configurations")
  void binaryTextBinaryPreservesCompleteBytes(
      String label, int configurationType, String header, String dictionary, String settings)
      throws Exception {
    var imported = new ConfigurationFileImport(new StringBuilder(header + " { " + settings + " }"));
    var original = new ConfigurationFile(configurationType, imported.getTlvBuilder(), LAB_KEY);
    original.commit();
    byte[] expected = original.toByteArray().clone();
    assertTrue(expected.length > imported.toByteArray().length, "Compiler must add finalization");

    byte[] current = expected.clone();
    for (int iteration = 0; iteration < 2; iteration++) {
      // Reconstruct from bytes alone, preserving family-specific length widths (e.g. Snmp64).
      var binary = new TlvBuilder();
      binary.add(
          new TlvVariableBinding(
              current.clone(), new TlvDictionary(dictionary).getTopLevelByteLength()));
      var exporter = new ConfigurationFileExport(binary, configurationType);
      exporter.setDotTextOIDOutputFormat(ConfigurationFileExport.DOTTED_OID_FORMAT);
      String text = exporter.toPrettyPrint(false, true);
      assertTrue(text.contains(header + " {"), label + " must preserve its importable header");
      assertTrue(text.contains("synthetic-lab"));
      if (configurationType != DOCSIS_40_CONFIGURATION_TYPE) {
        assertTrue(text.contains("test-location"));
      }
      var reparsed = new ConfigurationFileImport(new StringBuilder(text));
      var rebuilt = new ConfigurationFile(configurationType, reparsed.getTlvBuilder(), LAB_KEY);
      rebuilt.commit();
      current = rebuilt.toByteArray();
      assertArrayEquals(expected, current, label + " iteration " + iteration);
      assertArrayEquals(expected, original.toByteArray(), "Export must not mutate the original");
    }
  }

  private static Stream<Arguments> configurations() {
    String basic = "NetworkAccess 1; " + SNMP;
    String nested =
        basic
            + """
              UpstreamServiceFlow {
                ServiceFlowReference 1001;
                QualityOfServiceParameterSetType 07;
                MaxSubstainedTrafficRate 100000000;
                SchedulingType 2;
              }
              DownstreamServiceFlow {
                ServiceFlowReference 2001;
                QualityOfServiceParameterSetType 07;
                MaxSubstainedTrafficRate 1000000000;
              }
              """;
    String docsis40 =
        "NetworkAccess 1; "
            + """
              UpstreamServiceFlow {
                ServiceFlowReference 1001;
                ServiceClassName synthetic-lab;
                QosParameterSetType 7;
                MaxSustainedTrafficRate 1;
                DataRateUnitSetting 3;
                AqmEncodings { SfAqmDisable 0; ClassicAqmLatencyTarget 10; }
              }
              DownstreamServiceFlow {
                ServiceFlowReference 2001;
                QosParameterSetType 7;
                MaxSustainedTrafficRate 1;
                DataRateUnitSetting 3;
              }
              """;
    String packet =
        "TelephonyConfigFileBeginEnd 1; "
            + SNMP
            + "Snmp64 1.3.6.1.2.1.1.4.0 OctetString \"lab-contact\";"
            + " TelephonyConfigFileBeginEnd 255;";
    return Stream.of(
        Arguments.of(
            "DPoG 1.0",
            DPOG_10_CONFIGURATION_TYPE,
            "DPoG",
            TlvDictionary.DPOG_DICTIONARY_TABLE_NAME,
            nested),
        Arguments.of(
            "DOCSIS 1.0",
            DOCSIS_10_CONFIGURATION_TYPE,
            "Docsis",
            TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME,
            basic),
        Arguments.of(
            "DOCSIS 1.1",
            DOCSIS_11_CONFIGURATION_TYPE,
            "Docsis",
            TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME,
            basic),
        Arguments.of(
            "DOCSIS 2.0",
            DOCSIS_20_CONFIGURATION_TYPE,
            "Docsis",
            TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME,
            basic),
        Arguments.of(
            "DOCSIS 3.0",
            DOCSIS_30_CONFIGURATION_TYPE,
            "Docsis",
            TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME,
            basic),
        Arguments.of(
            "DOCSIS 3.1",
            DOCSIS_31_CONFIGURATION_TYPE,
            "Docsis",
            TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME,
            nested),
        Arguments.of(
            "DOCSIS 4.0",
            DOCSIS_40_CONFIGURATION_TYPE,
            "Docsis-4.0",
            TlvDictionary.DOCSIS_40_DICTIONARY_TABLE_NAME,
            docsis40),
        Arguments.of(
            "DPoE 1.0",
            DPOE_10_CONFIGURATION_TYPE,
            "DPoE",
            TlvDictionary.DPOE_DICTIONARY_TABLE_NAME,
            nested),
        Arguments.of(
            "DPoE 2.0",
            DPOE_20_CONFIGURATION_TYPE,
            "DPoE",
            TlvDictionary.DPOE_DICTIONARY_TABLE_NAME,
            nested),
        Arguments.of(
            "PacketCable 1.0",
            PKT_CABLE_10_CONFIGURATION_TYPE,
            "PacketCable-1.0",
            TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME,
            packet),
        Arguments.of(
            "PacketCable 1.5",
            PKT_CABLE_15_CONFIGURATION_TYPE,
            "PacketCable-1.5",
            TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME,
            packet),
        Arguments.of(
            "PacketCable 2.0",
            PKT_CABLE_20_CONFIGURATION_TYPE,
            "PacketCable-2.0",
            TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME,
            packet));
  }
}
