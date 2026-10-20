/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.snmp4j.asn1.BER;
import org.snmp4j.smi.*;

class TlvBuilderSnmpTest {
  private static final OID OID_VALUE = new OID("1.3.6.1.2.1.1.5.0");

  @ParameterizedTest
  @ValueSource(ints = {11, 64})
  void encodesEveryOverloadWithFixedHeaderWidth(int type) throws Exception {
    var text = new TlvBuilder();
    text.add(type, OID_VALUE, BER.OCTETSTRING, "lab");
    assertEncoding(text, type, new OctetString("lab"));
    var bytes = new TlvBuilder();
    bytes.add(type, OID_VALUE, BER.OCTETSTRING, new byte[] {0, -1, 1});
    assertEncoding(bytes, type, new OctetString(new byte[] {0, -1, 1}));
    var number = new TlvBuilder();
    number.add(type, OID_VALUE, BER.INTEGER, -42L);
    assertEncoding(number, type, new Integer32(-42));
  }

  @Test
  void shortSnmp64HasLeadingZeroLengthByte() {
    var builder = new TlvBuilder();
    builder.add(64, OID_VALUE, BER.OCTETSTRING, "x");
    assertArrayEquals(
        java.util.HexFormat.of().parseHex("40000f300d06082b06010201010500040178"),
        builder.toByteArray());
  }

  @ParameterizedTest
  @ValueSource(ints = {255, 256, 65535, 65536})
  void checksCompleteBerLengthAtWireBoundaries(int encodedLength) throws Exception {
    int valueLength = encodedLength - (encodedLength <= 256 ? 16 : 18);
    byte[] value = new byte[valueLength];
    assertEquals(encodedLength, encode(new OctetString(value)).length);
    for (int type : new int[] {11, 64}) {
      var builder = new TlvBuilder();
      if (encodedLength > (type == 11 ? 255 : 65535)) {
        assertThrows(
            IllegalArgumentException.class,
            () -> builder.add(type, OID_VALUE, BER.OCTETSTRING, value));
        assertEquals(0, builder.length());
        assertTrue(builder.getMapTypeToByteLength().isEmpty());
      } else {
        builder.add(type, OID_VALUE, BER.OCTETSTRING, value);
        assertEncoding(builder, type, new OctetString(value));
      }
    }
  }

  @Test
  void countsEncodedTextNotCharacters() throws Exception {
    var builder = new TlvBuilder();
    String value = "é".repeat(200);
    assertThrows(
        IllegalArgumentException.class, () -> builder.add(11, OID_VALUE, BER.OCTETSTRING, value));
    builder.add(64, OID_VALUE, BER.OCTETSTRING, value);
    assertEncoding(builder, 64, new OctetString(value));
  }

  @Test
  void failuresPreserveExistingBytesAndMetadata() {
    var builder = new TlvBuilder();
    builder.add(11, OID_VALUE, BER.OCTETSTRING, "keep");
    byte[] original = builder.toByteArray();
    Map<Integer, Integer> metadata = Map.copyOf(builder.getMapTypeToByteLength());
    assertThrows(IllegalArgumentException.class, () -> builder.add(64, OID_VALUE, BER.NULL, "bad"));
    assertThrows(
        IllegalArgumentException.class,
        () -> builder.add(64, OID_VALUE, BER.INTEGER, new byte[] {1}));
    assertThrows(
        IllegalArgumentException.class,
        () -> builder.add(64, OID_VALUE, BER.INTEGER, Long.MAX_VALUE));
    assertThrows(
        IllegalArgumentException.class, () -> builder.add(12, OID_VALUE, BER.OCTETSTRING, "bad"));
    assertThrows(
        IllegalArgumentException.class, () -> builder.add(64, null, BER.OCTETSTRING, "bad"));
    assertThrows(
        IllegalArgumentException.class,
        () -> builder.add(64, OID_VALUE, BER.OCTETSTRING, (String) null));
    assertArrayEquals(original, builder.toByteArray());
    assertEquals(metadata, builder.getMapTypeToByteLength());
  }

  @Test
  void preservesNumericTypesAndIpAddress() throws Exception {
    Variable[] values = {
      new Counter32(4294967295L),
      new Gauge32(4294967295L),
      new TimeTicks(4294967295L),
      new Counter64(-1L),
      new Integer32(Integer.MIN_VALUE)
    };
    for (Variable value : values) {
      var builder = new TlvBuilder();
      builder.add(64, OID_VALUE, (byte) value.getSyntax(), value.toLong());
      assertEncoding(builder, 64, value);
    }
    var address = new TlvBuilder();
    address.add(64, OID_VALUE, BER.IPADDRESS, "192.0.2.1");
    assertEncoding(address, 64, new IpAddress("192.0.2.1"));
  }

  @Test
  void rejectsInvalidInputsAcrossOverloads() {
    var builder = new TlvBuilder();
    assertThrows(
        IllegalArgumentException.class, () -> builder.add(11, OID_VALUE, BER.COUNTER32, -1L));
    assertThrows(
        IllegalArgumentException.class, () -> builder.add(11, OID_VALUE, BER.GAUGE32, 4294967296L));
    assertThrows(IllegalArgumentException.class, () -> builder.add(11, OID_VALUE, BER.NULL, 1L));
    assertThrows(
        IllegalArgumentException.class, () -> builder.add(11, new OID(), BER.OCTETSTRING, "x"));
    assertThrows(
        IllegalArgumentException.class,
        () -> builder.add(11, OID_VALUE, BER.OCTETSTRING, (byte[]) null));
    assertEquals(0, builder.length());
    assertTrue(builder.getMapTypeToByteLength().isEmpty());
  }

  private static byte[] encode(Variable value) throws Exception {
    var output = new ByteArrayOutputStream();
    new VariableBinding(OID_VALUE, value).encodeBER(output);
    return output.toByteArray();
  }

  private static void assertEncoding(TlvBuilder builder, int type, Variable value)
      throws Exception {
    byte[] payload = encode(value);
    byte[] actual = builder.toByteArray();
    int width = type == 11 ? 1 : 2;
    assertEquals(type, Byte.toUnsignedInt(actual[0]));
    int length = 0;
    for (int i = 1; i <= width; i++) length = (length << 8) | Byte.toUnsignedInt(actual[i]);
    assertEquals(payload.length, length);
    assertArrayEquals(payload, Arrays.copyOfRange(actual, 1 + width, actual.length));
    assertEquals(width, builder.getMapTypeToByteLength().get(type));
  }
}
