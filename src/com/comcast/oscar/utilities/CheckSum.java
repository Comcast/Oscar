/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSum {

  /**
   * Method getMD5.
   *
   * @param bMessage ByteArray of the Message
   * @return byte[] MD5 Hash of the Message
   */
  public static byte[] getMD5(byte[] bMessage) {

    MessageDigest md = null;

    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return md.digest(bMessage);
  }

  /**
   * @param bTlvArray ByteArray of the Message
   * @return SHA-1 Hash of the Message
   */
  public static byte[] getSHA1(byte[] bTlvArray) {

    MessageDigest md = null;

    try {
      md = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return md.digest(bTlvArray);
  }
}
