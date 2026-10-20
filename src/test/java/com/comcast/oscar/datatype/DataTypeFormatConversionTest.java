/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.datatype;

import static com.comcast.oscar.datatype.DataTypeFormatConversion.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HexFormat;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DataTypeFormatConversionTest {
  @ParameterizedTest
  @ValueSource(
      strings = {
        "::",
        "::1",
        "2001:db8::1",
        "FE80::ABCD",
        "1:2:3:4:5:6:7:8",
        "1:2:3:4:5:6:7::",
        "::1:2:3:4:5:6:7",
        "::ffff:192.0.2.1",
        "::192.0.2.1",
        "::ffff:192.000.002.001",
        "1:2:3:4:5:6:192.0.2.1",
        "1:2:3:4:5::192.0.2.1"
      })
  void acceptsIpv6AndPreservesTransportBytes(String address) throws Exception {
    assertTrue(IPV6_ADDRESS_REGEX.matcher(address).matches(), address);
    for (int port : new int[] {0, 1, 255, 256, 32767, 32768, 65535}) {
      String text = address + "(" + port + ")";
      assertTrue(verifyIPv6TransportAddressFormat(text), text);
      byte[] encoded = ipv6TransportAddressToByteArray(text);
      assertEquals(18, encoded.length);
      assertEquals(port, ((encoded[16] & 255) << 8) | (encoded[17] & 255));
      assertArrayEquals(
          encoded, ipv6TransportAddressToByteArray(byteArrayToIPv6TransportAddress(encoded)));
      assertArrayEquals(
          encoded, inetTransportAddressToByteArray(byteArrayToInetTransportAddress(encoded)));
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        ":",
        ":::1",
        "1::2::3",
        "1:2:3:4:5:6:7",
        "1:2:3:4:5:6:7:8:9",
        "1:2:3:4:5:6:7:8::",
        "::1:2:3:4:5:6:7:8",
        "12345::",
        "gggg::1",
        "[::1]",
        "fe80::1%eth0",
        "localhost",
        "::ffff:256.0.0.1",
        "1:2:3:4:5:6::192.0.2.1",
        " ::1",
        "::1\n",
        "::1suffix",
        "192.0.2.1",
        "::ffff:192.0.2"
      })
  void rejectsMalformedIpv6(String address) {
    assertFalse(IPV6_ADDRESS_REGEX.matcher(address).matches(), address);
    assertFalse(verifyIPv6TransportAddressFormat(address + "(161)"), address);
    assertEquals(0, ipv6TransportAddressToByteArray(address + "(161)").length);
  }

  @Test
  void handlesEveryCompressionPositionAndWidth() {
    for (int start = 0; start < 8; start++) {
      for (int width = 1; start + width <= 8; width++) {
        String left = String.join(":", java.util.Collections.nCopies(start, "abcd"));
        String right = String.join(":", java.util.Collections.nCopies(8 - start - width, "abcd"));
        String compressed = left + "::" + right;
        byte[] expected = new byte[18];
        for (int word = 0; word < 8; word++) {
          if (word < start || word >= start + width) {
            expected[word * 2] = (byte) 0xab;
            expected[word * 2 + 1] = (byte) 0xcd;
          }
        }
        assertArrayEquals(
            expected, ipv6TransportAddressToByteArray(compressed + "(0)"), compressed);
      }
    }
  }

  @Test
  void matchesNamedGroupsAndKnownWireBytes() {
    var matcher = IPV4_TRANSPORT_REGEX.matcher("192.0.2.1(161)");
    assertTrue(matcher.matches());
    assertEquals("192.0.2.1", matcher.group("address"));
    assertEquals("161", matcher.group("port"));
    assertArrayEquals(
        HexFormat.of().parseHex("c000020100a1"), ipv4TransportAddressToByteArray("192.0.2.1(161)"));
    assertArrayEquals(
        HexFormat.of().parseHex("0102030400a1"),
        ipv4TransportAddressToByteArray("001.002.003.004(0161)"));
    assertArrayEquals(
        HexFormat.of().parseHex("00000000000000000000ffffc000020100a1"),
        ipv6TransportAddressToByteArray("::ffff:192.0.2.1(161)"));
    var v6 = IPV6_TRANSPORT_REGEX.matcher("2001:db8::1(161)");
    assertTrue(v6.matches());
    assertEquals("2001:db8::1", v6.group("address"));
    assertEquals("161", v6.group("port"));
  }

  @Test
  void acceptsEveryPortAndDecimalByte() {
    for (int port = 0; port <= 65535; port++) {
      assertTrue(verifyIPv4TransportAddressFormat("192.0.2.1(" + port + ")"));
    }
    for (int value = 0; value <= 255; value++) {
      assertTrue(IPV4_ADDRESS_REGEX.matcher(value + ".0.0.255").matches());
      byte[] expected = {(byte) value, (byte) (255 - value)};
      assertArrayEquals(expected, doubleByteArray("(" + value + "," + (255 - value) + ")"));
      assertArrayEquals(expected, doubleByteArray(doubleByteArray(expected)));
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "-1", "65536", "99999", "100000", "+1", "1.0", "", " 1", "1 ", "１", "1)junk", "1\n"
      })
  void rejectsInvalidPorts(String port) {
    for (String address : new String[] {"192.0.2.1", "2001:db8::1"}) {
      String text = address + "(" + port + ")";
      assertFalse(verifyIPv4TransportAddressFormat(text));
      assertFalse(verifyIPv6TransportAddressFormat(text));
      assertThrows(IllegalArgumentException.class, () -> inetTransportAddressToByteArray(text));
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "256.0.0.1",
        "1.2.3",
        "1.2.3.4.5",
        "-1.0.0.0",
        "1..2.3",
        "localhost",
        "1.2.3.4\n",
        " 1.2.3.4",
        "0x01.2.3.4",
        "0000.1.2.3"
      })
  void rejectsInvalidIpv4(String address) {
    assertFalse(IPV4_ADDRESS_REGEX.matcher(address).matches());
    assertFalse(verifyIPv4TransportAddressFormat(address + "(161)"));
    assertEquals(0, ipv4TransportAddressToByteArray(address + "(161)").length);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "()",
        "(1)",
        "(1,2,3)",
        "(256,0)",
        "(-1,0)",
        "(1, 2)",
        "(1,2)junk",
        "(1,2)\n",
        "(1,2)(3,4",
        "(１,2)"
      })
  void rejectsInvalidPairs(String text) {
    assertFalse(doubleByteArrayValidate(text));
    assertThrows(IllegalArgumentException.class, () -> doubleByteArray(text));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "(00:11:22:33:44:fF)",
        "(00-11-22-33-44-ff)",
        "(00.11.22.33.44.ff)",
        "(00:11-22.33:44:ff)"
      })
  void acceptsMacSeparatorsAndCase(String text) {
    assertTrue(macAddressFormatValidation(text));
    byte[] expected = HexFormat.of().parseHex("0011223344ff");
    assertArrayEquals(expected, macAddressFormatToByteArray(text));
    assertArrayEquals(expected, macAddressFormatToByteArray(byteArrayToMacAddressFormat(expected)));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "()",
        "00:11:22:33:44:55",
        "(0:11:22:33:44:55)",
        "(00:11:22:33:44)",
        "(00:11:22:33:44:55:66)",
        "(GG:11:22:33:44:55)",
        "(00:11:22:33:44:55)\n",
        "(00:11:22:33:44:55)junk"
      })
  void rejectsInvalidMacs(String text) {
    assertFalse(macAddressFormatValidation(text));
    assertThrows(IllegalArgumentException.class, () -> macAddressFormatToByteArray(text));
  }

  @Test
  void handlesLongListsAndMalformedSuffixesWithoutStackOverflow() {
    String pairs = "(0,255)".repeat(10000);
    String macs = "(00:11:22:33:44:55)".repeat(10000);
    assertTrue(doubleByteArrayValidate(pairs));
    assertTrue(macAddressFormatValidation(macs));
    assertFalse(doubleByteArrayValidate(pairs + "("));
    assertFalse(macAddressFormatValidation(macs + "("));
    assertFalse(IPV6_ADDRESS_REGEX.matcher("a:".repeat(10000)).matches());
    assertFalse(IPV4_TRANSPORT_REGEX.matcher("9".repeat(10000)).matches());
  }

  @Test
  void seededBinaryTransportRoundTripsAndLengthChecks() throws Exception {
    Random random = new Random(1729);
    for (int length : new int[] {6, 18}) {
      for (int attempt = 0; attempt < 100; attempt++) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        byte[] original = bytes.clone();
        assertArrayEquals(
            bytes, inetTransportAddressToByteArray(byteArrayToInetTransportAddress(bytes)));
        assertArrayEquals(original, bytes);
      }
    }
    for (int length : new int[] {0, 1, 5, 7, 17, 19}) {
      assertThrows(
          DataTypeFormatException.class, () -> byteArrayToInetTransportAddress(new byte[length]));
    }
    assertThrows(IllegalArgumentException.class, () -> doubleByteArray(new byte[1]));
    assertThrows(IllegalArgumentException.class, () -> byteArrayToMacAddressFormat(new byte[5]));
    assertThrows(NullPointerException.class, () -> verifyIPv4TransportAddressFormat(null));
    assertThrows(NullPointerException.class, () -> verifyIPv6TransportAddressFormat(null));
  }
}
