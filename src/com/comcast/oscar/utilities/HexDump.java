/*
 * SPDX-License-Identifier: Apache-2.0
 */
/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.comcast.oscar.utilities;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.Objects;

/** Uppercase hexadecimal conversion and traditional offset/hex/ASCII dumps. */
public class HexDump {
  private static final int BYTES_PER_ROW = 16;
  // One leading space plus two hexadecimal digits per byte.
  private static final int HEX_COLUMN_WIDTH = 3;
  private static final HexFormat HEX = HexFormat.of().withUpperCase();
  private static final HexFormat COLON_HEX = HEX.withDelimiter(":");
  private static final HexFormat ROW_HEX = HEX.withPrefix(" ");

  /**
   * Formats all bytes as 16-byte rows with absolute offsets and printable ASCII.
   *
   * @param array non-null bytes; never modified
   * @return dump beginning with a newline; empty input retains an empty offset row
   * @throws NullPointerException if array is null
   */
  public static String dumpHexString(byte[] array) {
    return dumpHexString(array, 0, array.length);
  }

  /**
   * Formats bytes from offset to the end as colon-separated uppercase hex, without a leading
   * delimiter. For example, {0, 1, -1} at offset 1 yields "01:FF".
   *
   * @param array non-null bytes; never modified
   * @param offset starting byte index, from zero through array.length inclusive
   * @return colon-separated hex, or empty text when offset equals array.length
   * @throws IndexOutOfBoundsException if offset is outside the array
   * @throws NullPointerException if array is null
   */
  public static String dumpHexString(byte[] array, int offset) {
    Objects.checkFromToIndex(offset, array.length, array.length);
    return COLON_HEX.formatHex(array, offset, array.length);
  }

  /**
   * Formats a bounded slice as 16-byte rows: newline, eight-digit 0x offset, hex, then ASCII.
   * Partial rows are padded to align the ASCII column; bytes outside space through tilde become
   * dots. A one-byte 41 slice at offset zero ends with "41" and an aligned "A".
   *
   * @param array non-null bytes; never modified
   * @param offset absolute starting byte index
   * @param length number of bytes; zero retains an empty offset row
   * @return formatted dump with no trailing newline
   * @throws IndexOutOfBoundsException if the slice is negative, overflowing or outside the array
   * @throws NullPointerException if array is null
   */
  public static String dumpHexString(byte[] array, int offset, int length) {
    Objects.checkFromIndexSize(offset, length, array.length);
    StringBuilder result = new StringBuilder();
    int end = offset + length;
    int row = offset;
    do {
      int count = Math.min(BYTES_PER_ROW, end - row);
      result.append("\n0x").append(HEX.toHexDigits(row));
      ROW_HEX.formatHex(result, array, row, row + count);
      result.append(" ".repeat((BYTES_PER_ROW - count) * HEX_COLUMN_WIDTH + 1));
      for (int index = row; index < row + count; index++) {
        int value = Byte.toUnsignedInt(array[index]);
        result.append(value >= ' ' && value <= '~' ? (char) value : '.');
      }
      row += count;
    } while (row < end);
    return result.toString();
  }

  /** Returns two uppercase hex digits for a byte; for example, -1 becomes "FF". */
  public static String toHexString(byte value) {
    return HEX.toHexDigits(value);
  }

  /**
   * Returns uppercase hex without delimiters; empty input produces empty text.
   *
   * @param array non-null bytes; never modified
   * @throws NullPointerException if array is null
   */
  public static String toHexString(byte[] array) {
    return HEX.formatHex(array);
  }

  /**
   * Returns uppercase hex for a slice, with two digits per byte and no delimiters.
   *
   * @param array non-null bytes; never modified
   * @param offset starting byte index
   * @param length number of bytes; zero produces empty text
   * @throws IndexOutOfBoundsException if the slice is negative, overflowing or outside the array
   * @throws NullPointerException if array is null
   */
  public static String toHexString(byte[] array, int offset, int length) {
    Objects.checkFromIndexSize(offset, length, array.length);
    return HEX.formatHex(array, offset, offset + length);
  }

  /** Returns eight uppercase hex digits, including leading zeros; -1 becomes "FFFFFFFF". */
  public static String toHexString(int value) {
    return HEX.toHexDigits(value);
  }

  /** Returns a new single-element array containing the supplied byte. */
  public static byte[] toByteArray(byte value) {
    return new byte[] {value};
  }

  /** Returns a new four-byte, big-endian representation; 0x01020304 becomes 01:02:03:04. */
  public static byte[] toByteArray(int value) {
    return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
  }

  /**
   * Parses pairs of hexadecimal digits, accepting either letter case and no separators.
   *
   * @param hexString non-null, even-length hex; empty text is allowed
   * @return a new byte array; for example, "00ff" becomes {0, -1}
   * @throws IllegalArgumentException for odd-length text or non-hexadecimal characters
   * @throws NullPointerException if hexString is null
   */
  public static byte[] hexStringToByteArray(String hexString) {
    return HEX.parseHex(hexString);
  }
}
