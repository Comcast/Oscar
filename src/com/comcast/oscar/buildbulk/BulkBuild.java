package com.comcast.oscar.buildbulk;

import com.comcast.oscar.compiler.PacketCableConstants;
import com.comcast.oscar.configurationfile.CommonTlvInsertions;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.dictionary.DictionarySQLQueries;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @bannerLicense Copyright 2015 Comcast Cable Communications Management, LLC<br>
 *     ___________________________________________________________________<br>
 *     Licensed under the Apache License, Version 2.0 (the "License")<br>
 *     you may not use this file except in compliance with the License.<br>
 *     You may obtain a copy of the License at<br>
 *     http://www.apache.org/licenses/LICENSE-2.0<br>
 *     Unless required by applicable law or agreed to in writing, software<br>
 *     distributed under the License is distributed on an "AS IS" BASIS,<br>
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
 *     See the License for the specific language governing permissions and<br>
 *     limitations under the License.<br>
 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */
public class BulkBuild {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(BulkBuild.class);

  public static final Integer BINARY_OUTPUT = 0;
  public static final Integer TEXT_OUTPUT = 1;

  private int iCompilingMethod;

  private File fInputDirectory, fOutputDirectory;

  @SuppressWarnings("unused")
  private List<File> lfInputFiles = null;

  private String sSharedSecretKey = " ";

  private CommonTlvInsertions ctiCommonTlvInsertions = null;

  private List<TlvBuilder> ltlvConfigurationFileInsert = null;

  /**
   * @param fInputDirectory
   * @param fOutputDirectory
   * @param iCompilingMethod
   * @param sSharedSecretKey String
   */
  public BulkBuild(
      File fInputDirectory, File fOutputDirectory, int iCompilingMethod, String sSharedSecretKey) {
    this.fInputDirectory = fInputDirectory;
    this.fOutputDirectory = fOutputDirectory;
    this.iCompilingMethod = iCompilingMethod;
    this.sSharedSecretKey = sSharedSecretKey;
  }

  /**
   * @param fInputDirectory
   * @param fOutputDirectory
   * @param iCompilingMethod
   * @param sSharedSecretKey
   * @param cti
   */
  public BulkBuild(
      File fInputDirectory,
      File fOutputDirectory,
      int iCompilingMethod,
      String sSharedSecretKey,
      CommonTlvInsertions cti) {
    this.fInputDirectory = fInputDirectory;
    this.fOutputDirectory = fOutputDirectory;
    this.iCompilingMethod = iCompilingMethod;
    this.sSharedSecretKey = sSharedSecretKey;
    this.ctiCommonTlvInsertions = cti;
  }

  /**
   * @return boolean
   */
  public boolean start() {

    boolean boolStatus = true;

    // Loops thur all the files found in the input directory
    for (File fInput : getInputFiles()) {

      if (logger.isDebugEnabled()) logger.debug("BulkBuild.start() - File: " + fInput.toString());

      byte[] bInput = HexString.fileToByteArray(fInput);

      /*Check to see if the file is Binary or Text*/
      // Text Input
      if (HexString.verifyAsciiPlainText(bInput)) {

        if (iCompilingMethod == TEXT_OUTPUT) {

        } else if (iCompilingMethod == BINARY_OUTPUT) {

          if (HexString.verifyAsciiPlainText(bInput)) {

            if (logger.isDebugEnabled())
              logger.debug(
                  "BulkBuild.start() - Building From Text File: ("
                      + fInput.getName()
                      + ") To Binary File: "
                      + fInput.getName());

            textToBinary(
                fInput, buildNewFilePath(fOutputDirectory, fInput.getName()), sSharedSecretKey);
          }
        }

        // Binary Input
      } else {

        if (iCompilingMethod == TEXT_OUTPUT) {

          if (logger.isDebugEnabled())
            logger.debug(
                "BulkBuild.start() - Building From Binary File: ("
                    + fInput.getName()
                    + ") To Text File: "
                    + fInput.getName());

          binaryToText(
              fInput, buildNewFilePath(fOutputDirectory, fInput.getName()), sSharedSecretKey);

        } else if (iCompilingMethod == BINARY_OUTPUT) {

          if (logger.isDebugEnabled())
            logger.debug(
                "BulkBuild.start() - Building From Binary File: ("
                    + fInput.toString()
                    + ") To Binary File: "
                    + fInput.getName());

          binaryToBinary(
              fInput, buildNewFilePath(fOutputDirectory, fInput.getName()), sSharedSecretKey);
        }
      }
    }

    return boolStatus;
  }

  /**
   * @return List<File>
   */
  public List<File> getInputFiles() {

    List<File> lfInputFiles = new ArrayList<File>();

    for (final File fInputFile : fInputDirectory.listFiles()) {
      lfInputFiles.add(fInputFile);
    }

    return lfInputFiles;
  }

  /**
   * @param fConfigurationFile
   */
  public void mergeConfigurationFile(File fConfigurationFile) {

    ltlvConfigurationFileInsert = new ArrayList<TlvBuilder>();

    byte[] bConfigurationFile = HexString.fileToByteArray(fConfigurationFile);

    if (HexString.verifyAsciiPlainText(bConfigurationFile)) {

      ConfigurationFileImport cfi = null;

      try {
        cfi = new ConfigurationFileImport(bConfigurationFile);
      } catch (NullPointerException e) {
        e.printStackTrace();
      } catch (ConfigurationFileException e) {
        e.printStackTrace();
      }

      ltlvConfigurationFileInsert.add(cfi.getTlvBuilder());

    } else {

      TlvBuilder tb = new TlvBuilder();

      try {
        tb.add(new HexString(bConfigurationFile));
      } catch (TlvException e) {
        e.printStackTrace();
      }

      ltlvConfigurationFileInsert.add(tb);
    }
  }

  /**
   * @param fTextInput
   * @param fBinOutput
   * @param sSharedSecretKey String
   * @return boolean
   */
  public boolean textToBinary(File fTextInput, File fBinOutput, String sSharedSecretKey) {

    ConfigurationFileImport cfi = null;

    try {
      try {
        cfi = new ConfigurationFileImport(fTextInput);
      } catch (ConfigurationFileException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    ConfigurationFile cf =
        new ConfigurationFile(
            cfi.getConfigurationFileType(), cfi.getTlvBuilder(), sSharedSecretKey);

    commonTlvInsertionsToConfigurationFile(ctiCommonTlvInsertions, cf);

    cf.commit();

    cf.setConfigurationFileName(fBinOutput);

    return cf.writeToDisk();
  }

  /**
   * @param fBinInput
   * @param fTextOutput
   * @param sSharedSecretKey String
   * @return boolean
   */
  @SuppressWarnings("deprecation")
  public boolean binaryToText(File fBinInput, File fTextOutput, String sSharedSecretKey) {

    boolean boolStripFinalize = true;

    if (logger.isDebugEnabled())
      logger.debug(
          "BulkBuild.binaryToText(f,f,s) +------------------------------------------------------+");

    ConfigurationFileExport cfe = null;

    if (ctiCommonTlvInsertions != null) {

      byte[] baConfigFile = HexString.fileToByteArray(fBinInput);

      TlvBuilder tb = new TlvBuilder();

      DictionarySQLQueries dsq = null;

      try {
        tb.add(new HexString(HexString.fileToByteArray(fBinInput)));
      } catch (TlvException e) {
        e.printStackTrace();
      }

      /*
       * There should only be 1 TopLevel TLV in the TlvBuilder
       * Check Type against Dictionary
       */

      if (baConfigFile[0] == PacketCableConstants.FILE_MARKER) {
        dsq = new DictionarySQLQueries(DictionarySQLQueries.PACKET_CABLE_DICTIONARY_TABLE_NAME);
      } else {
        dsq = new DictionarySQLQueries(DictionarySQLQueries.DOCSIS_DICTIONARY_TABLE_NAME);
      }

      // At this point, need to check if TLV is of Configuration Type
      for (TlvBuilder tbTemp : ctiCommonTlvInsertions.getTlvBuilderList()) {

        // Get Type
        int iType = BinaryConversion.byteToUnsignedInteger(tbTemp.toByteArray()[0]);

        // Check against Dictionary
        if (dsq.getTopLevelByteLength().containsKey(iType)) {
          tb.add(tbTemp);
        }
      }

      // Need to strip off MIC and EOF before Submission, can cause later issues when building files
      cfe = new ConfigurationFileExport(tb, boolStripFinalize);

    } else {

      cfe = new ConfigurationFileExport(fBinInput);
    }

    return cfe.writeToDisk(fTextOutput);
  }

  /**
   * @param fBinInput
   * @param fBinOutput
   * @param sSharedSecretKey String
   * @return boolean
   */
  public boolean binaryToBinary(File fBinInput, File fBinOutput, String sSharedSecretKey) {

    if (logger.isDebugEnabled()) logger.debug("BulkBuild.binaryToBinary(f,f,s)");

    @SuppressWarnings("deprecation")
    ConfigurationFileExport cfe = new ConfigurationFileExport(fBinInput);

    ConfigurationFile cf = null;

    try {
      cf =
          new ConfigurationFile(
              cfe.getConfigurationFileType(), cfe.getTlvBuilder(), sSharedSecretKey);
    } catch (TlvException e) {
      e.printStackTrace();
    }

    // Insert CommongTLVInsertion
    commonTlvInsertionsToConfigurationFile(ctiCommonTlvInsertions, cf);

    cf.commit();

    cf.setConfigurationFileName(fBinOutput);

    return cf.writeToDisk();
  }

  /**
   * @param fPath
   * @param sFile
   * @return File
   */
  private File buildNewFilePath(File fPath, String sFile) {
    return new java.io.File(fPath.toString() + File.separator + sFile);
  }

  /**
   * This method will do a Specification Check before adding TLVs this Method assumes that each
   * TlvBuilder has only 1 TopLevel TLV
   *
   * @param cti
   * @param cf
   */
  private void commonTlvInsertionsToConfigurationFile(
      CommonTlvInsertions cti, ConfigurationFile cf) {

    if (cti != null) {

      DictionarySQLQueries dsq = null;

      /*
       * There should only be 1 TopLevel TLV in the TlvBuilder
       * Check Type against Dictionary
       */

      if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31) {
        dsq = new DictionarySQLQueries(DictionarySQLQueries.DPOE_DICTIONARY_TABLE_NAME);
      } else {
        dsq = new DictionarySQLQueries(DictionarySQLQueries.DOCSIS_DICTIONARY_TABLE_NAME);
      }

      // Cycle thru Each Map Entry and insert the TlvBuilder -
      // this is so that there is only 1 TLV and not multiple TLV of the same type
      for (Entry<Integer, TlvBuilder> eTlvToTlvBuilder : cti.getTlvBuilderMap().entrySet()) {

        TlvBuilder tb = eTlvToTlvBuilder.getValue();

        // Get Type
        int iType = BinaryConversion.byteToUnsignedInteger(tb.toByteArray()[0]);

        // Check against Dictionary
        if (dsq.getTopLevelByteLength().containsKey(iType)) {
          cf.add(tb);
        }
      }
    }
  }
}
