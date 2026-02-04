package com.comcast.oscar.examples;

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
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileNotFoundException;

/** */
public class MergeConfigurationFilesTest {

  /**
   * @param args
   */
  @SuppressWarnings("deprecation")
  public static void main(String[] args) {

    String SHARED_SECRET = "SHAREDSECRET";

    // Template File
    ConfigurationFileImport cfiTemplate = null;
    ConfigurationFileExport cfeTemplate = null;
    TlvBuilder tbTemplate = null;

    // Input File
    ConfigurationFileImport cfiInput = null;
    ConfigurationFileExport cfeInput = null;
    TlvBuilder tbInput = null;

    int iCFTInput = -1, iCFTTemplate = -2;

    // Input File - Check for Text or Binary
    File fInput = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt");

    if (HexString.verifyAsciiPlainText(HexString.fileToByteArray(fInput))) {

      try {
        cfiInput = new ConfigurationFileImport(fInput);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (ConfigurationFileException e) {
        e.printStackTrace();
      }

      iCFTInput = cfiInput.getConfigurationFileType();

      tbInput = cfiInput.getTlvBuilder();

    } else {

      cfeInput = new ConfigurationFileExport(fInput);

      try {
        tbInput = cfeInput.getTlvBuilder();
      } catch (TlvException e) {
        e.printStackTrace();
      }

      iCFTInput = cfeInput.getConfigurationFileType();
    }

    // Template File - Check for Text or Binary
    File fTemplate = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN-TEMPLATE-MERGE.txt");

    if (HexString.verifyAsciiPlainText(HexString.fileToByteArray(fTemplate))) {

      try {
        cfiTemplate = new ConfigurationFileImport(fTemplate);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (ConfigurationFileException e) {
        e.printStackTrace();
      }

      tbTemplate = cfiTemplate.getTlvBuilder();

      iCFTTemplate = cfeInput.getConfigurationFileType();

    } else {

      cfeTemplate = new ConfigurationFileExport(fTemplate);

      try {
        tbTemplate = cfeTemplate.getTlvBuilder();
      } catch (TlvException e) {
        e.printStackTrace();
      }

      iCFTTemplate = cfeInput.getConfigurationFileType();
    }

    // If the Configuration File Type Do not Match Exit
    if (iCFTInput != iCFTTemplate) {
      System.out.println(
          "InputFile: "
              + fInput.getName()
              + " Template File: "
              + fTemplate.getName()
              + " are not of the same ConfigurationFile Type");
      return;
    }

    ConfigurationFile cf = null;
    if (iCFTInput > ConfigurationFile.DOCSIS_VER_31) {
      // Create Configuration File - Packet Cable
      cf = new ConfigurationFile(iCFTInput, tbInput);
    } else {
      // Create Configuration File - DOCSIS
      cf = new ConfigurationFile(iCFTInput, tbInput, SHARED_SECRET);
    }

    // Add Template to Configuration File
    cf.add(tbTemplate);

    // Finalize file with Security Hashes
    cf.commit();

    File fOutput =
        TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN-TEMPLATE-MERGE-OUT.txt");

    ConfigurationFileExport cfeMerge = null;

    cfeMerge = new ConfigurationFileExport(cf);

    // Write Merge file to TEXT
    cfeMerge.writeToDisk(fOutput);
  }
}
