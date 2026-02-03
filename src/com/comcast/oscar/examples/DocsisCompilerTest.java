package com.comcast.oscar.examples;

import com.comcast.oscar.compiler.DocsisCompiler;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileInputStream;
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
public class DocsisCompilerTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    System.out.println(
        "+----------------------------------DOCSIS"
            + " COMPILER---------------------------------------------------+");

    File fDocsisConfiguration = TestDirectoryStructure.inputDir();

    byte[] bDocsisConfiguration = new byte[(int) fDocsisConfiguration.length()];

    FileInputStream fisDocsisConfiguration;

    try {
      fisDocsisConfiguration = new FileInputStream(fDocsisConfiguration);
      fisDocsisConfiguration.read(bDocsisConfiguration);
    } catch (IOException e) {
      e.printStackTrace();
    }

    DocsisCompiler dcDocsisCompiler = new DocsisCompiler(1);

    TlvBuilder tbDocsisConfiguration = new TlvBuilder();

    try {
      tbDocsisConfiguration.add(new HexString(bDocsisConfiguration));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    dcDocsisCompiler.add(tbDocsisConfiguration);
  }
}
