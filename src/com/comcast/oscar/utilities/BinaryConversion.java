/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class BinaryConversion {

  public static final Integer BITS_PER_BYTE = 8;
  public static final Integer BIT_TRUE = 1;
  public static final Integer BIT_FALSE = 0;
  public static final Integer BASE_2 = 2;

  /**
   * @param bByte
   * @return integer
   */
  public static int byteToUnsignedInteger(byte bByte) {
    return (bByte & 0x000000FF);
  }

  /**
   * Converts eight-bit groups to bytes in most-significant-bit order. For example,
   * "(10000000)(00000001)" produces 80:01.
   *
   * @param bitMask non-null binary text; parentheses are ignored for legacy compatibility
   * @return a new array with one byte per eight characters after removing parentheses; empty input
   *     produces an empty array
   * @throws IllegalArgumentException if the remaining length is not a multiple of eight or a group
   *     cannot be parsed as a base-two integer
   * @throws NullPointerException if bitMask is null
   */
  public static byte[] binaryMSBMaskToByteArray(String bitMask) {
    String bits = bitMask.replace("(", "").replace(")", "");
    if (bits.length() % Byte.SIZE != 0) {
      throw new IllegalArgumentException("Binary mask length must be divisible by " + Byte.SIZE);
    }

    byte[] result = new byte[bits.length() / Byte.SIZE];
    for (int index = 0, offset = 0; index < result.length; index++, offset += Byte.SIZE) {
      // Parse the existing string range without allocating a substring or hexadecimal wrapper.
      result[index] = (byte) Integer.parseInt(bits, offset, offset + Byte.SIZE, BASE_2);
    }
    return result;
  }

  /**
   * @param iStartPostion
   * @param iLength
   * @param bByteArray
   * @return int
   */
  public static int getUnsignedIntegerFromByteArrayRange(
      int iStartPostion, int iLength, byte[] bByteArray) {

    ByteArrayOutputStream baosByteArray = new ByteArrayOutputStream();

    baosByteArray.write(bByteArray, iStartPostion, iLength);

    return byteArrayToInteger(baosByteArray.toByteArray());
  }

  /**
   * Reads a big-endian integer without modifying the input. Short arrays are zero-extended on the
   * left; arrays longer than four bytes use only their first four bytes. For example, FF becomes
   * 255, while FF:FF:FF:FF becomes -1.
   *
   * @param bytes non-null bytes; empty input is allowed
   * @return the signed 32-bit result, or zero for empty input
   * @throws NullPointerException if bytes is null
   */
  public static int byteArrayToInteger(byte[] bytes) {
    if (bytes.length >= Integer.BYTES) {
      return ByteBuffer.wrap(bytes).getInt();
    }
    int result = 0;
    for (byte value : bytes) {
      result = (result << Byte.SIZE) | Byte.toUnsignedInt(value);
    }
    return result;
  }
}
