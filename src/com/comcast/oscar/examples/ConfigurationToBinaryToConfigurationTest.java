package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/*
	Copyright 2015 Comcast Cable Communications Management, LLC
	___________________________________________________________________
	Licensed under the Apache License, Version 2.0 (the "License")
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

	@author Maurice Garcia (mgarcia01752@outlook.com)

*/

/** */
public class ConfigurationToBinaryToConfigurationTest {

  /**
   * @param args
   */
  @SuppressWarnings("deprecation")
  public static void main(String[] args) {

    boolean DOCSIS = true;
    boolean PACKET_CABLE_20 = true;

    File file = null;
    File file2 = null;

    if (DOCSIS) {
      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");
      System.out.println(
          "+----------------------------------------DOCSIS--------------------------------------------------------------+");
      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");

      System.out.println(
          "+-------------------------------------Text To"
              + " Binary--------------------------------------------------------+");

      try {
        file =
            new File(
                new java.io.File(".").getCanonicalPath()
                    + File.separatorChar
                    + "testfiles"
                    + File.separatorChar
                    + "DOCSIS-GOLDEN.txt");
      } catch (IOException e) {
        e.printStackTrace();
      }

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

      TlvBuilder tb = new TlvBuilder();

      try {
        tb.add(new HexString(cfi.toByteArray()));
      } catch (TlvException e) {
        e.printStackTrace();
      }

      ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31, tb);

      cf.commit();

      System.out.println(new HexString(cf.toByteArray()).toString(":"));

      try {
        file =
            new File(
                new java.io.File(".").getCanonicalPath()
                    + File.separatorChar
                    + "testfiles"
                    + File.separatorChar
                    + "output"
                    + File.separatorChar
                    + "DOCSIS-GOLDEN.cm");
      } catch (IOException e) {
        e.printStackTrace();
      }

      cf.setConfigurationFileName(file);

      cf.writeToDisk();

      System.out.println(
          "+-------------------------------------Binary To"
              + " Text--------------------------------------------------------+");

      /* Binary To Text */
      try {

        file2 =
            new File(
                new java.io.File(".").getCanonicalPath()
                    + File.separatorChar
                    + "testfiles"
                    + File.separatorChar
                    + "output"
                    + File.separatorChar
                    + "DOCSIS-GOLDEN.cm");

      } catch (IOException e) {
        e.printStackTrace();
      }

      ConfigurationFileExport cfe = new ConfigurationFileExport(file2);

      System.out.println(cfe.toPrettyPrint(0));

      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");
    }

    if (PACKET_CABLE_20) {
      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");
      System.out.println(
          "+----------------------------------------PACKET"
              + " CABLE-2.0----------------------------------------------------+");
      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");

      try {

        file =
            new File(
                new java.io.File(".").getCanonicalPath()
                    + File.separatorChar
                    + "testfiles"
                    + File.separatorChar
                    + "IMS-PKT-CABLE-CONFIG.txt");
      } catch (IOException e) {
        e.printStackTrace();
      }

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
          new ConfigurationFile(ConfigurationFile.PKT_CBL_VER_20, cfi.getTlvBuilder());

      cf.commit();

      try {

        file =
            new File(
                new java.io.File(".").getCanonicalPath()
                    + File.separatorChar
                    + "testfiles"
                    + File.separatorChar
                    + "output"
                    + File.separatorChar
                    + "IMS-PKT-CABLE-CONFIG.bin");
      } catch (IOException e) {
        e.printStackTrace();
      }

      cf.setConfigurationFileName(file);

      cf.writeToDisk();

      System.out.println(
          "+-------------------------------------Binary To"
              + " Text--------------------------------------------------------+");

      /* Binary To Text */

      try {

        file2 =
            new File(
                new java.io.File(".").getCanonicalPath()
                    + File.separatorChar
                    + "testfiles"
                    + File.separatorChar
                    + "output"
                    + File.separatorChar
                    + "IMS-PKT-CABLE-CONFIG.bin");
      } catch (IOException e) {
        e.printStackTrace();
      }

      ConfigurationFileExport cfe = new ConfigurationFileExport(file2);

      System.out.println(cfe.toPrettyPrint(0));

      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");

      System.out.println(new HexString(cf.toByteArray()).toString(":"));

      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");

      ConfigurationFileExport cfeDOCTLV = new ConfigurationFileExport(cf);

      System.out.println(cfeDOCTLV.toPrettyPrint(0));
    }
  }
}
