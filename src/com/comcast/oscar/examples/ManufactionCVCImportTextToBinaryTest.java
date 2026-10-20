/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/** */
public class ManufactionCVCImportTextToBinaryTest {

  /**
   * @param args
   */
  @SuppressWarnings("deprecation")
  public static void main(String[] args) {

    File file = null;

    System.out.println(
        "+------------------------------------------------------------------------------------------------------------+");
    System.out.println(
        "+----------------------------------------DOCSIS--------------------------------------------------------------+");
    System.out.println(
        "+------------------------------------------------------------------------------------------------------------+");

    try {
      file =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "DocsisTestFile-CVC-Test.txt");
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(new HexString(HexString.fileToByteArray(file)).toASCII());

    ConfigurationFileImport cfi = null;

    try {
      try {
        cfi = new ConfigurationFileImport(file);
      } catch (ConfigurationFileException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    ConfigurationFile cf =
        new ConfigurationFile(
            ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE, cfi.getTlvBuilder());

    /* Get CVC File */

    File fCVC = null;

    try {
      fCVC =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "-NA-CVC.der");
    } catch (IOException e) {
      e.printStackTrace();
    }

    TlvBuilder tbCVC = new TlvBuilder();

    try {
      tbCVC.add(Constants.MANUFACTURER_CVC, HexString.fileToByteArray(fCVC));
    } catch (TlvException e1) {
      e1.printStackTrace();
    }

    // Add CVC
    cf.add(tbCVC);

    cf.commit();

    System.out.println(
        "+-------------------------------------------AFTER CVC"
            + " INSERTION-----------------------------------------------------------------+");

    ConfigurationFileExport cfeDOCTLV = new ConfigurationFileExport(cf);

    System.out.println(cfeDOCTLV.toPrettyPrint(0));
  }
}
