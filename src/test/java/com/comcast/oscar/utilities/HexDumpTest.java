/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HexDumpTest {
  @Test
  void colonFormatHonorsOffsetWithoutLeadingDelimiter() {
    byte[] bytes = {0, 1, (byte) 255};
    assertEquals("01:FF", HexDump.dumpHexString(bytes, 1));
    assertEquals("", HexDump.dumpHexString(bytes, bytes.length));
    assertEquals("00:01:FF", HexDump.dumpHexString(bytes, 0));
  }

  @Test
  void printsAsciiForFinalFullRow() {
    byte[] bytes = "ABCDEFGHIJKLMNOP".getBytes(StandardCharsets.US_ASCII);
    assertEquals(
        "\n0x00000000 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 50 ABCDEFGHIJKLMNOP",
        HexDump.dumpHexString(bytes));
  }

  @Test
  void printsSpaceAndTildeButMasksOtherNonPrintableBytes() {
    assertEquals(
        "\n0x00000000 20 7E 1F 7F 80" + " ".repeat(34) + " ~...",
        HexDump.dumpHexString(new byte[] {32, 126, 31, 127, (byte) 128}));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 15, 16, 17, 32, 33})
  void alignsRowsAndUsesAbsoluteSliceOffsets(int length) {
    byte[] bytes = new byte[length + 2];
    Arrays.fill(bytes, (byte) 'A');
    StringBuilder expected = new StringBuilder();
    for (int row = 0; row < length || row == 0; row += 16) {
      int count = Math.min(16, length - row);
      expected.append(String.format("\n0x%08X", row + 2));
      expected.append(" 41".repeat(count));
      expected.append(" ".repeat((16 - count) * 3 + 1));
      expected.append("A".repeat(count));
    }
    byte[] original = bytes.clone();
    assertEquals(expected.toString(), HexDump.dumpHexString(bytes, 2, length));
    assertArrayEquals(original, bytes);
  }

  @Test
  void convertsEveryByteAndFixedWidthIntegers() {
    byte[] bytes = new byte[256];
    StringBuilder expected = new StringBuilder();
    for (int value = 0; value < bytes.length; value++) {
      bytes[value] = (byte) value;
      expected.append(String.format("%02X", value));
      assertEquals(String.format("%02X", value), HexDump.toHexString((byte) value));
      assertArrayEquals(new byte[] {(byte) value}, HexDump.toByteArray((byte) value));
    }
    assertEquals(expected.toString(), HexDump.toHexString(bytes));
    assertArrayEquals(
        bytes,
        HexDump.hexStringToByteArray(expected.toString().toLowerCase(java.util.Locale.ROOT)));
    assertEquals("0102", HexDump.toHexString(bytes, 1, 2));
    assertEquals("FFFFFFFF", HexDump.toHexString(-1));
    assertEquals("00000001", HexDump.toHexString(1));
    assertEquals("80000000", HexDump.toHexString(Integer.MIN_VALUE));
    assertArrayEquals(new byte[] {1, 2, 3, 4}, HexDump.toByteArray(0x01020304));
    assertEquals("", HexDump.toHexString(new byte[0]));
    assertArrayEquals(new byte[0], HexDump.hexStringToByteArray(""));
  }

  @Test
  void rejectsInvalidHexClearly() {
    for (String value : new String[] {"0", "001", "GG", "00:11", " 00"}) {
      assertThrows(IllegalArgumentException.class, () -> HexDump.hexStringToByteArray(value));
    }
  }

  @Test
  void rejectsInvalidRangesEvenWhenEmptyOrOverflowing() {
    byte[] bytes = {1, 2, 3};
    for (int[] range : new int[][] {{-1, 0}, {4, 0}, {0, -1}, {2, 2}, {1, Integer.MAX_VALUE}}) {
      assertThrows(
          IndexOutOfBoundsException.class, () -> HexDump.dumpHexString(bytes, range[0], range[1]));
      assertThrows(
          IndexOutOfBoundsException.class, () -> HexDump.toHexString(bytes, range[0], range[1]));
    }
    assertThrows(IndexOutOfBoundsException.class, () -> HexDump.dumpHexString(bytes, -1));
    assertThrows(IndexOutOfBoundsException.class, () -> HexDump.dumpHexString(bytes, 4));
    assertThrows(NullPointerException.class, () -> HexDump.dumpHexString(null));
    assertThrows(NullPointerException.class, () -> HexDump.hexStringToByteArray(null));
  }
}
