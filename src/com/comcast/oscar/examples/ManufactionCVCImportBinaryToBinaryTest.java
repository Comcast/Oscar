/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.IOException;

/** */
public class ManufactionCVCImportBinaryToBinaryTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    /*******************************DEFAULT SECTION*************************************/
    String sSharedSecret = "SHAREDSECRET";
    /*******************************DEFAULT SECTION*************************************/

    File fDocsisBin = null;

    try {

      fDocsisBin =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "DOCSIS-GOLDEN-NO-MAN-CVC.bin");

    } catch (IOException e1) {
      e1.printStackTrace();
    }

    TlvBuilder tb = new TlvBuilder();
    try {
      tb.add(new HexString(HexString.fileToByteArray(fDocsisBin)));
    } catch (TlvException e3) {
      e3.printStackTrace();
    }

    File fManCVC = null;

    try {

      fManCVC =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "certificate"
                  + File.separatorChar
                  + "CVC.der");

    } catch (IOException e) {
      e.printStackTrace();
    }

    TlvBuilder tbManCVC = new TlvBuilder();

    try {
      tbManCVC.add(Constants.MANUFACTURER_CVC, HexString.fileToByteArray(fManCVC));
    } catch (TlvException e1) {
      e1.printStackTrace();
    }

    tb.add(tbManCVC);

    try {
      tb = TlvBuilder.reorderTLV(true, tb);
    } catch (TlvException e1) {
      e1.printStackTrace();
    }

    ConfigurationFile cfDocsis = null;

    cfDocsis =
        new ConfigurationFile(
            ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE, tb, sSharedSecret);
    cfDocsis.commit();

    File fDocsisBinOut = null;

    try {

      fDocsisBinOut =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "output"
                  + File.separatorChar
                  + "DOCSIS-GOLDEN-WITH-MAN-CVC-2.bin");

    } catch (IOException e) {
      e.printStackTrace();
    }

    cfDocsis.setConfigurationFileName(fDocsisBinOut);

    if (cfDocsis.writeToDisk()) {
      System.out.println("Write to File: DOCSIS-GOLDEN-WITH-MAN-CVC-2.bin");
    } else {
      System.out.println("Write to File FAILED: DOCSIS-GOLDEN-WITH-MAN-CVC-2.bin");
    }
  }
}
