/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ManCvcImportTextToBinaryTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    /*******************************DEFAULT SECTION*************************************/

    String sSharedSecret = "SHAREDSECRET";
    /*******************************DEFAULT SECTION*************************************/

    File fDocsisText = null;

    try {
      fDocsisText =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "DOCSIS-GOLDEN-NO-MAN-CVC.txt");

    } catch (IOException e1) {
      e1.printStackTrace();
    }

    ConfigurationFileImport cfiDocsis = null;

    try {
      try {
        cfiDocsis = new ConfigurationFileImport(fDocsisText);
      } catch (ConfigurationFileException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    ConfigurationFile cfDocsis =
        new ConfigurationFile(
            ConfigurationFile.DOCSIS_VER_31, cfiDocsis.getTlvBuilder(), sSharedSecret);

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
                  + "XXXXX-NA-CVC.der");

    } catch (IOException e) {
      e.printStackTrace();
    }

    TlvBuilder tbManCVC = new TlvBuilder();

    try {
      tbManCVC.add(Constants.MANUFACTURER_CVC, HexString.fileToByteArray(fManCVC));
    } catch (TlvException e1) {
      e1.printStackTrace();
    }

    cfDocsis.add(tbManCVC);

    cfDocsis.commit();

    File fDocsisBin = null;

    try {
      fDocsisBin =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "output"
                  + File.separatorChar
                  + "DOCSIS-GOLDEN-WITH-MAN-CVC.bin");

    } catch (IOException e2) {
      e2.printStackTrace();
    }

    cfDocsis.setConfigurationFileName(fDocsisBin);

    if (cfDocsis.writeToDisk()) {
      System.out.println("Write to File: DOCSIS-GOLDEN-WITH-MAN-CVC.bin");
    } else {
      System.out.println("Write to File FAILED: DOCSIS-GOLDEN-WITH-MAN-CVC.bin");
    }
  }
}
