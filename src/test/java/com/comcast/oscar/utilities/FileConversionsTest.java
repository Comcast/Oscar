/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class FileConversionsTest {
  @Test
  void normalizesOnlyCrLfPairs() {
    assertArrayEquals(
        "a\nb\nc\rd\n\r".getBytes(StandardCharsets.UTF_8),
        FileConversions.dosToUnix("a\r\nb\nc\rd\r\n\r".getBytes(StandardCharsets.UTF_8)));
    assertArrayEquals(
        new byte[] {'\r', '\n'}, FileConversions.dosToUnix(new byte[] {'\r', '\r', '\n'}));
    assertArrayEquals(
        new byte[] {'\n', '\n'}, FileConversions.dosToUnix(new byte[] {'\r', '\n', '\r', '\n'}));
  }

  @Test
  void preservesEveryOtherByteAndDoesNotMutateInput() {
    byte[] input = new byte[256];
    for (int index = 0; index < input.length; index++) input[index] = (byte) index;
    byte[] original = input.clone();
    byte[] result = FileConversions.dosToUnix(input);
    assertArrayEquals(original, result);
    assertArrayEquals(original, input);
    assertNotSame(input, result);
  }

  @Test
  void preservesUtf8AndEmbeddedZero() {
    byte[] input = "café\u0000\r\n東京".getBytes(StandardCharsets.UTF_8);
    assertArrayEquals(
        "café\u0000\n東京".getBytes(StandardCharsets.UTF_8), FileConversions.dosToUnix(input));
  }

  @Test
  void handlesEmptyAndNullInput() {
    byte[] input = new byte[0];
    assertArrayEquals(input, FileConversions.dosToUnix(input));
    assertNotSame(input, FileConversions.dosToUnix(input));
    assertThrows(NullPointerException.class, () -> FileConversions.dosToUnix(null));
  }
}
