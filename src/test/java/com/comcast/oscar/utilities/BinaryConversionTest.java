/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BinaryConversionTest {
  @Test
  void preservesIntegerWidthsSignAndTrailingBytes() {
    byte[][] inputs = {
      {},
      {(byte) 255},
      {(byte) 128, 1},
      {1, 2, 3},
      {(byte) 128, 0, 0, 0},
      {(byte) 255, (byte) 255, (byte) 255, (byte) 255},
      {1, 2, 3, 4, 99}
    };
    int[] expected = {0, 255, 32769, 66051, Integer.MIN_VALUE, -1, 16909060};
    for (int index = 0; index < inputs.length; index++) {
      byte[] original = inputs[index].clone();
      assertEquals(expected[index], BinaryConversion.byteArrayToInteger(inputs[index]));
      assertArrayEquals(original, inputs[index]);
    }
    assertThrows(NullPointerException.class, () -> BinaryConversion.byteArrayToInteger(null));
  }

  @Test
  void preservesEveryByteInMostSignificantBitOrder() {
    byte[] expected = new byte[256];
    var raw = new StringBuilder();
    var grouped = new StringBuilder();
    for (int value = 0; value < expected.length; value++) {
      expected[value] = (byte) value;
      String bits = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
      raw.append(bits);
      grouped.append('(').append(bits).append(')');
    }
    assertArrayEquals(expected, BinaryConversion.binaryMSBMaskToByteArray(raw.toString()));
    assertArrayEquals(expected, BinaryConversion.binaryMSBMaskToByteArray(grouped.toString()));
  }

  @Test
  void preservesLegacyParenthesisAndEmptyInputHandling() {
    assertArrayEquals(new byte[0], BinaryConversion.binaryMSBMaskToByteArray(""));
    assertArrayEquals(new byte[0], BinaryConversion.binaryMSBMaskToByteArray("()"));
    assertArrayEquals(
        new byte[] {(byte) 0x81, 0x42},
        BinaryConversion.binaryMSBMaskToByteArray("(1000)0001(01000010)"));
  }

  @Test
  void rejectsPartialBytesAndInvalidDigits() {
    for (String input :
        new String[] {"1", "0000000", "000000000", "00000002", "0000000x", "0000000 "}) {
      assertThrows(
          IllegalArgumentException.class,
          () -> BinaryConversion.binaryMSBMaskToByteArray(input),
          input);
    }
    assertThrows(NullPointerException.class, () -> BinaryConversion.binaryMSBMaskToByteArray(null));
  }
}
