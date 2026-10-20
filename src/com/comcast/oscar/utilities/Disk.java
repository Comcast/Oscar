/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Disk {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(Disk.class);

  /**
   * @param bStream
   * @param fOutput
   * @return boolean
   */
  public static boolean writeToDisk(byte[] bStream, File fOutput) {

    if (bStream == null) {

      logger.debug("Disk.writeToDisk() - NULL ByteArray");

      return false;
    }

    logger.debug(
        () ->
            "Disk.writeToDisk() "
                + " - Total Byte Count: "
                + bStream.length
                + " - FileName: "
                + fOutput.getName());

    OutputStream out = null;

    if (fOutput.getName() != null) {

      try {
        out = new FileOutputStream(fOutput);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

    } else if (fOutput.getName().length() != 0) {

      try {

        out = new FileOutputStream(fOutput.getName());

      } catch (IOException e) {
        e.printStackTrace();
      }

    } else {

      File fCf = null;

      fCf = fOutput;

      try {
        out = new FileOutputStream(fCf);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    try {
      out.write(bStream);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return true;
  }

  /**
   * @param fOutput
   * @param sInput String
   * @return boolean
   */
  public static boolean writeToDisk(String sInput, File fOutput) {

    HexString hsInput = new HexString();

    hsInput.add(sInput);

    byte[] baInput = hsInput.toByteArray();

    logger.debug(
        () ->
            "Disk.writeToDisk() "
                + " - Total Byte Count: "
                + baInput.length
                + " - FileName: "
                + fOutput);

    OutputStream out = null;

    if (fOutput.getName() != null) {

      try {
        out = new FileOutputStream(fOutput);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

    } else if (fOutput.getName().length() != 0) {

      try {

        out = new FileOutputStream(fOutput.getName());

      } catch (IOException e) {
        e.printStackTrace();
      }

    } else {

      File fCf = null;

      fCf = fOutput;

      try {
        out = new FileOutputStream(fCf);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    try {
      out.write(baInput);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return true;
  }
}
