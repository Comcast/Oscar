package com.comcast.oscar.examples;

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

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.test.TestDirectoryStructure;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Verifies CLI Operation: -c,--compile compile the text to a binary
 *
 * @author mgarci00
 * @version $Revision: 1.0 $
 */
public class TextToBinaryTest {

  /**
   * Method main.
   *
   * @param args String[]
   */
  public static void main(String[] args) {

    ConfigurationFileImport cfiDocsis = null;

    boolean DOCSIS = true, PC = false;

    if (DOCSIS) {

      try {
        try {
          cfiDocsis =
              new ConfigurationFileImport(
                  TestDirectoryStructure.fInputDirFileName("DOCSIS-US-FLOW.txt"));
        } catch (ConfigurationFileException e) {
          e.printStackTrace();
        }
      } catch (IOException e1) {
        e1.printStackTrace();
      }

      ConfigurationFile cfDocsis =
          new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30, cfiDocsis.getTlvBuilder());

      cfDocsis.commit();

      cfDocsis.setConfigurationFileName(
          TestDirectoryStructure.fOutputDirFileName("DOCSIS-US-FLOW.bin"));

      if (cfDocsis.writeToDisk()) {
        System.out.println("Write to File: ");
      } else {
        System.out.println("Write to File FAILED: ");
      }
    }

    if (PC) {

      ConfigurationFileImport cfiPacketCable = null;

      try {
        cfiPacketCable =
            new ConfigurationFileImport(TestDirectoryStructure.fInputDirFileName("xxxx.txt"));
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ConfigurationFileException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      ConfigurationFile cfPacketCAble =
          new ConfigurationFile(ConfigurationFile.PKT_CBL_VER_20, cfiPacketCable.getTlvBuilder());

      cfPacketCAble.commit();

      cfPacketCAble.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName("xxxx.bin"));

      if (cfPacketCAble.writeToDisk()) {
        System.out.println("Write to File: ");
      } else {
        System.out.println("Write to File FAILED: ");
      }
    }
  }
}
