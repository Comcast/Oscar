/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

public class FileConversions {

  /**
   * @param baFile
   * @return byte[]
   */
  public static byte[] dosToUnix(byte[] baFile) {

    String sFile = HexString.toAsciiBinary(baFile).replaceAll("\r\n", "\n");

    return HexString.toByteArray(HexString.asciiToHex(sFile));
  }
}
