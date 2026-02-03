package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileNotFoundException;

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
public class ConfigurationToBinaryFileTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    boolean DOCSIS = true;

    boolean PACKET_CABLE_20 = false;

    File file = null;

    if (DOCSIS) {
      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");
      System.out.println(
          "+----------------------------------------DOCSIS--------------------------------------------------------------+");
      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");

      file =
          new File(
              "c:" + File.separatorChar + "testfiles" + File.separatorChar + "DOCSIS-GOLDEN.txt");

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
          new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30, cfi.getTlvBuilder());

      cf.commit();

      file =
          new File(
              "c:"
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "output"
                  + File.separatorChar
                  + "DOCSIS-GOLDEN.bin");

      cf.setConfigurationFileName(file);

      if (cf.writeToDisk()) {
        System.out.println("Write to File: ");
      } else {
        System.out.println("Write to File FAILED: ");
      }
    }

    if (PACKET_CABLE_20) {

      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");
      System.out.println(
          "+----------------------------------------PACKET"
              + " CABLE-2.0----------------------------------------------------+");
      System.out.println(
          "+------------------------------------------------------------------------------------------------------------+");

      file = new File("c:" + File.separatorChar + "PacketCable-2.0.txt");
      String sConfigurationFileName =
          "c:" + File.separatorChar + "config" + File.separatorChar + "PacketCable-2.0.bin";

      ConfigurationFileImport cfi = null;

      try {
        try {
          cfi = new ConfigurationFileImport(file);
        } catch (ConfigurationFileException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      TlvBuilder tb = new TlvBuilder();

      try {
        tb.add(new HexString(cfi.toByteArray()));
      } catch (TlvException e) {
        e.printStackTrace();
      }

      ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.PKT_CBL_VER_20, tb);

      cf.commit();

      System.out.println(new HexString(cf.toByteArray()).toString(":"));

      cf.setConfigurationFileName(sConfigurationFileName);

      if (cf.writeToDisk()) {
        System.out.println("Write to File: " + sConfigurationFileName);
      } else {
        System.out.println("Write to File FAILED: " + sConfigurationFileName);
      }
    }
  }
}
