/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class DirectoryStructure {

  private static final Logger logger = LogManager.getLogger(DirectoryStructure.class);

  private static boolean fromJar = false;

  /** Determine if launched from Jar file. For testing purposes. Set in Main */
  public static void setFromJar() {
    fromJar = true;
  }

  /**
   * Decide the correct path to start from
   *
   * @return base directory
   */
  public static String sBasePath() {

    if (fromJar) {
      File file = new File(System.getProperty("java.class.path"));

      if (file.getParent() != null) {
        return file.getParent().toString();
      }
    }

    try {
      return new java.io.File("").getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Find active path. Used for Input
   *
   * @return active directory
   */
  public static String sActivePath() {
    String activePath = null;

    try {
      activePath = new java.io.File("").getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return activePath;
  }

  /**
   * Directory for certificates
   *
   * @return certificates subdirectory
   */
  public static File fCertificatesDir() {
    return new File(sBasePath() + File.separator + "certificates");
  }

  /**
   * Directory for bulk builds
   *
   * @return build subdirectory
   */
  public static File fBuildDir() {
    return new File(sBasePath() + File.separator + "build");
  }

  /**
   * @return runtime resources subdirectory
   */
  public static File fResourcesDir() {
    return new File(sBasePath() + File.separator + "res");
  }

  /**
   * @return return test directory (used only for testing code)
   */
  public static File fTestDir() {
    return new File(sBasePath() + File.separator + "test");
  }

  /**
   * @param sFilename Filename in Test directory
   * @return
   */
  public static File fTestFile(String sFilename) {
    return new File(sBasePath() + File.separator + "test" + File.separator + sFilename);
  }

  /**
   * Directory for licenses
   *
   * @return licenses subdirectory
   */
  public static File fLicensesDir() {
    return new File(sBasePath() + File.separator + "licenses");
  }

  /**
   * Directory for mibs
   *
   * @return mibs subdirectory
   */
  public static File fMibsDir() {
    return new File(sBasePath() + File.separator + "mibs");
  }

  /**
   * Directory for mib binaries
   *
   * @return bin subdirectory in mibs subdirectory
   */
  public static File fMibsBinaryDir() {
    return new File(fMibsDir() + File.separator + "bin");
  }

  /**
   * Directory for mib text
   *
   * @return txt subdirectory in mibs subdirectory
   */
  public static File fMibsTextDir() {
    return new File(fMibsDir() + File.separator + "txt");
  }

  /**
   * @return timestamped directory
   */
  public static File fTimestampedDir() {
    return new File(File.separator + getDate());
  }

  /**
   * @return Date in yyyMMdd_HHmmss format
   */
  public static String getDate() {
    long time = System.currentTimeMillis();
    Date date = new Date(time);
    DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String formatted = format.format(date);
    return formatted;
  }

  /** Create subdirectories if they do not exist */
  public static void createDirectories() {
    if (!fCertificatesDir().exists()) {
      fCertificatesDir().mkdir();
    }

    if (!fMibsDir().exists()) {
      fMibsDir().mkdir();
    }

    if (!fMibsBinaryDir().exists()) {
      fMibsBinaryDir().mkdir();
    }

    if (!fMibsTextDir().exists()) {
      fMibsTextDir().mkdir();
    }
  }
}
