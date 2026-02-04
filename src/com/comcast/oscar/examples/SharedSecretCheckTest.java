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
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/** */
public class SharedSecretCheckTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    String sSharedSecret = "abcdef";

    File file = null;

    try {
      file =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "d10_defaultcmtestCmtsMicCheck-Key-abcdef.cm");
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

    ConfigurationFile cf = new ConfigurationFile(1, tb, sSharedSecret);

    cf.commit();
  }
}
