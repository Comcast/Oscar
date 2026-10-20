/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import java.nio.charset.StandardCharsets;

public class FileConversions {

  /**
   * Replaces CRLF byte pairs with LF without modifying the input. Standalone CR/LF and all other
   * bytes are preserved, including zero and non-ASCII bytes. For example, 61:0D:0A:62 becomes
   * 61:0A:62. ISO-8859-1 provides a reversible mapping for every byte; it does not interpret the
   * original text encoding.
   *
   * @param input non-null bytes in an ASCII-compatible encoding; not a UTF-16 text converter
   * @return a new byte array, including when input is empty or contains no CRLF pairs
   * @throws NullPointerException if input is null
   */
  public static byte[] dosToUnix(byte[] input) {
    return new String(input, StandardCharsets.ISO_8859_1)
        .replace("\r\n", "\n")
        .getBytes(StandardCharsets.ISO_8859_1);
  }
}
