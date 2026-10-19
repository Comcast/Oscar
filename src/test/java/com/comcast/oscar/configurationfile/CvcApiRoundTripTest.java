/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static com.comcast.oscar.cablelabsdefinitions.Constants.CO_SIGNER_CVC;
import static com.comcast.oscar.cablelabsdefinitions.Constants.MANUFACTURER_CVC;
import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE;
import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvVariableBinding;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Synthetic certificate bytes test transport preservation, not X.509 validity or trust. */
class CvcApiRoundTripTest {
  // CM-SP-MULPIv3.1-I21-201020, C.1.2.10 and C.1.2.11: successive 254-byte fragments.
  private static final int FRAGMENT_BYTES = 254;
  private static final String LAB_KEY = "SHAREDSECRET";

  @ParameterizedTest(name = "{0}, {1} bytes")
  @MethodSource("certificates")
  void preservesCvcFragmentsAndCompleteBinary(String label, int payloadLength, int[] certificateTypes)
      throws Exception {
    var imported = new ConfigurationFileImport(new StringBuilder("Docsis { NetworkAccess 1; }"));
    var configuration = new ConfigurationFile(DOCSIS_31_CONFIGURATION_TYPE, imported.getTlvBuilder(), LAB_KEY);
    var certificates = new TlvBuilder();
    for (int certificateType : certificateTypes) {
      byte[] payload = payload(payloadLength, certificateType);
      var certificate = new TlvBuilder();
      certificate.add(certificateType, payload);
      assertFragments(certificate.toByteArray(), certificateType, payload);
      certificates.add(certificateType, payload);
    }
    configuration.add(certificates);
    configuration.commit();
    byte[] expected = configuration.toByteArray().clone();
    byte[] current = expected.clone();
    for (int iteration = 0; iteration < 2; iteration++) {
      var binary = new TlvBuilder();
      binary.add(new TlvVariableBinding(current.clone(),
          new TlvDictionary(TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME).getTopLevelByteLength()));
      var exporter = new ConfigurationFileExport(binary, DOCSIS_31_CONFIGURATION_TYPE);
      String text = exporter.toPrettyPrint(false, true);
      for (int certificateType : certificateTypes) {
        assertTrue(text.contains(certificateType == MANUFACTURER_CVC ? "ManufacturerCVC" : "Co-signerCVC"), text);
      }
      var reparsed = new ConfigurationFileImport(new StringBuilder(text));
      var rebuilt = new ConfigurationFile(DOCSIS_31_CONFIGURATION_TYPE, reparsed.getTlvBuilder(), LAB_KEY);
      rebuilt.commit();
      current = rebuilt.toByteArray();
      assertArrayEquals(expected, current, label + " iteration " + iteration);
      assertArrayEquals(expected, configuration.toByteArray(), "Export must not mutate input");
    }
  }

  private static Stream<Arguments> certificates() {
    return Stream.of(1, 253, 254, 255, 508, 1025).flatMap(length -> Stream.of(
        Arguments.of("Manufacturer", length, new int[] {MANUFACTURER_CVC}),
        Arguments.of("Co-signer", length, new int[] {CO_SIGNER_CVC}),
        Arguments.of("Both", length, new int[] {MANUFACTURER_CVC, CO_SIGNER_CVC})));
  }

  private static byte[] payload(int length, int certificateType) {
    byte[] payload = new byte[length];
    for (int index = 0; index < length; index++) {
      payload[index] = (byte) (index + certificateType);
    }
    return payload;
  }

  private static void assertFragments(byte[] encoded, int certificateType, byte[] expectedPayload) {
    var reconstructed = new ByteArrayOutputStream();
    int offset = 0;
    while (offset < encoded.length) {
      assertTrue(offset + 2 <= encoded.length, "Complete fragment header");
      assertEquals(certificateType, Byte.toUnsignedInt(encoded[offset++]));
      int fragmentLength = Byte.toUnsignedInt(encoded[offset++]);
      assertEquals(Math.min(FRAGMENT_BYTES, expectedPayload.length - reconstructed.size()), fragmentLength);
      assertTrue(offset + fragmentLength <= encoded.length, "Complete fragment value");
      reconstructed.writeBytes(Arrays.copyOfRange(encoded, offset, offset + fragmentLength));
      offset += fragmentLength;
    }
    assertArrayEquals(expectedPayload, reconstructed.toByteArray(), "Preserve all payload bytes in order");
  }
}
