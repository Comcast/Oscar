package com.comcast.oscar.test;

import com.comcast.oscar.utilities.DirectoryStructure;

/** */
public class TestDirectoryStructure extends DirectoryStructure {

  protected static String sInputDirName = "input";
  protected static String sOutputDirName = "output";
  protected static String sTestDirName = "test";
  protected static String sCertificateDirName = "certificate";
  protected static String sDigitMapDirName = "digitmap";
  protected static String sInputBulkDirName = "inputBulk";
  protected static String sOutputBulkDirName = "outputBulk";

  public static final File fTestDirPath =
      new File(DirectoryStructure.sBasePath() + File.separator + sTestDirName);

  /**
   * @return File
   */
  public static File inputDir() {
    return new File(fTestDirPath + File.separator + sInputDirName);
  }

  /**
   * @param sFileName
   * @return File
   */
  public static File fInputDirFileName(String sFileName) {
    return new File(inputDir() + File.separator + sFileName);
  }

  /**
   * @return File
   */
  public static File outputDir() {
    return new File(fTestDirPath + File.separator + sOutputDirName);
  }

  /**
   * @param sFileName
   * @return File
   */
  public static File fOutputDirFileName(String sFileName) {
    return new File(outputDir() + File.separator + sFileName);
  }

  /**
   * @return File
   */
  public static File certificateDir() {
    return new File(fTestDirPath + File.separator + sCertificateDirName);
  }

  /**
   * @param sFileName
   * @return File
   */
  public static File fCertificateDirFileName(String sFileName) {
    return new File(certificateDir() + File.separator + sFileName);
  }

  /**
   * @return File
   */
  public static File digitMapDir() {
    return new File(fTestDirPath + File.separator + sDigitMapDirName);
  }

  /**
   * @param sFileName
   * @return File
   */
  public static File fDigitMapDirFileName(String sFileName) {
    return new File(digitMapDir() + File.separator + sFileName);
  }

  /**
   * @return File
   */
  public static File inputBulkDir() {
    return new File(fTestDirPath + File.separator + sInputBulkDirName);
  }

  /**
   * @param sFileName
   * @return File
   */
  public static File fInputBulkDirFileName(String sFileName) {
    return new File(inputDir() + File.separator + sFileName);
  }

  /**
   * @return File
   */
  public static File outputBulkDir() {
    return new File(fTestDirPath + File.separator + sOutputBulkDirName);
  }

  /**
   * @param sFileName
   * @return File
   */
  public static File fOutputBulkDirFileName(String sFileName) {
    return new File(outputDir() + File.separator + sFileName);
  }
}
