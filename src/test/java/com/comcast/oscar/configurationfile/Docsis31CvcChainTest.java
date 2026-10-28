/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes.*;
import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE;
import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.tlv.TlvBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Synthetic chain bytes exercise transport, not certificate trust or MIC authentication. */
class Docsis31CvcChainTest {
  @ParameterizedTest
  @ValueSource(ints = {MANUFACTURER_CVC_CHAIN, CO_SIGNER_CVC_CHAIN})
  void chainFragmentsRoundTripWithoutLosingBytes(int certificateType) throws Exception {
    // I21 C.1.2.21/22: nine full fragments and a final 123-byte fragment.
    byte[] payload = new byte[9 * TlvBuilder.MAX_TLV_LENGTH + 123];
    for (int index = 0; index < payload.length; index++) {
      payload[index] = (byte) index;
    }
    var wire = new TlvBuilder();
    wire.add(certificateType, payload);
    var registry = TlvRegistry.forFamily(TlvRegistry.DOCSIS_FAMILY);
    assertNotNull(registry.find(List.of(certificateType)), "Certificate chain must be registered");
    String text =
        new ConfigurationFileExport(wire, DOCSIS_31_CONFIGURATION_TYPE).toPrettyPrint(false, true);
    assertTrue(text.contains(registry.find(List.of(certificateType)).name()));
    assertArrayEquals(
        wire.toByteArray(), new ConfigurationFileImport(new StringBuilder(text)).toByteArray());
  }

  @Test
  void repeatedAddressContainersPreserveIpv4AndIpv6Pairs() throws Exception {
    var community = new TlvBuilder();
    community.add(1, "lab".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    for (int addressLength : new int[] {6, 18}) {
      var pair = new TlvBuilder();
      pair.add(1, new byte[addressLength]);
      pair.add(2, new byte[addressLength]);
      community.add(2, pair.toByteArray());
    }
    community.add(3, new byte[] {1});
    var wire = new TlvBuilder();
    wire.add(SNMPV1V2C_COEXIST, community.toByteArray());
    wire.add(SNMPV1V2C_COEXIST, community.toByteArray());
    String text =
        new ConfigurationFileExport(wire, DOCSIS_31_CONFIGURATION_TYPE).toPrettyPrint(false, true);
    assertArrayEquals(
        wire.toByteArray(), new ConfigurationFileImport(new StringBuilder(text)).toByteArray());
  }

  @Test
  void coexistenceAllowsRepeatedCommunitiesAndAddressPairs() throws Exception {
    var registry = TlvRegistry.forFamily(TlvRegistry.DOCSIS_FAMILY);
    // I21 C.1.2.13 permits multiple 53 and 53.2, but only one of each other child.
    assertEquals(-1, registry.find(List.of(SNMPV1V2C_COEXIST)).maxInstances());
    assertEquals(-1, registry.find(List.of(SNMPV1V2C_COEXIST, 2)).maxInstances());
    assertEquals(1, registry.find(List.of(SNMPV1V2C_COEXIST, 2, 1)).maxInstances());
    assertEquals(1, registry.find(List.of(SNMPV1V2C_COEXIST, 2, 2)).maxInstances());
  }
}
