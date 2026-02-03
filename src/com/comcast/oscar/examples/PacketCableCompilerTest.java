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

import com.comcast.oscar.compiler.PacketCableCompiler;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** */
public class PacketCableCompilerTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    System.out.println(
        "+----------------------------------PACKET-CABLE"
            + " COMPILER---------------------------------------------------+");

    File fPckCblConfiguration = new File("c:\\IMS-PKT-CABLE-CONFIG.bin");

    // File fPckCblConfiguration = new File("c:\\IMS-PKT-CABLE-CONFIG-SHA1.bin");

    System.out.println(
        "PC: HEX:                                                "
            + new HexString(HexString.fileToByteArray(fPckCblConfiguration)).toString());

    byte[] bPckCblConfiguration = new byte[(int) fPckCblConfiguration.length()];

    FileInputStream fisPckCblConfiguration;

    try {
      fisPckCblConfiguration = new FileInputStream(fPckCblConfiguration);
      fisPckCblConfiguration.read(bPckCblConfiguration);
    } catch (IOException e) {
      e.printStackTrace();
    }

    PacketCableCompiler pccPacketCableCompiler = new PacketCableCompiler(102);

    TlvBuilder tbPktCbleConfiguration = new TlvBuilder();

    try {
      tbPktCbleConfiguration.add(new HexString(bPckCblConfiguration));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    pccPacketCableCompiler.add(tbPktCbleConfiguration);

    pccPacketCableCompiler.commit();

    System.out.println(
        "PC-CONFIG-HEX:                                          "
            + pccPacketCableCompiler.toString());

    /*
     * 										WRITE TO FILE
     */
    OutputStream out = null;

    try {
      out = new FileOutputStream("C:\\PC-TEST-FILE.pck");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    try {
      out.write(pccPacketCableCompiler.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
