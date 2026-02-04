package com.comcast.oscar.examples;

import com.comcast.oscar.utilities.HexString;

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
public class HexStringTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    System.out.println(
        "+-----------------------------------------HEX STRING "
            + " CHECKS---------------------------------------------------------+");
    HexString hsString = new HexString();
    hsString.add("Maurice");
    System.out.println("HEX: " + hsString + " -> String: " + hsString.toASCII());

    hsString.clear();
    hsString.add(255);
    System.out.println("HEX: " + hsString + " -> String: " + hsString.toInteger());

    hsString.clear();
    hsString.add(65535);
    System.out.println("HEX: " + hsString + " -> String: " + hsString.toInteger());

    hsString.clear();
    hsString.add(16777215);
    System.out.println("HEX: " + hsString + " -> String: " + hsString.toInteger());

    hsString.clear();
    hsString.add(2147483647);
    System.out.println("HEX: " + hsString + " -> String: " + hsString.toInteger());

    hsString.clear();
    hsString.add(1431655765);
    System.out.println("HEX: " + hsString);
    System.out.println("HEX: " + hsString + " -> String: " + hsString.toAsciiBinary());

    hsString.clear();
    byte[] bBerInetAddressV4 = {0x00, 0x00, 0x00, 0x00};
    hsString.add(bBerInetAddressV4);
    System.out.println("HEX: " + hsString);
    System.out.println("IP: " + HexString.toInetAddress(bBerInetAddressV4));

    hsString.clear();
    byte[] bBerInetAddressV4Mask = {(byte) 0xff, (byte) 0xff, 0x00, 0x00};
    hsString.add(bBerInetAddressV4);
    System.out.println("HEX: " + hsString);
    System.out.println("IP: " + HexString.toInetAddress(bBerInetAddressV4Mask));

    byte[] bMacAddress = {
      (byte) 0x01,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0x02,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0x03,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0xff,
      (byte) 0xff,
    };

    for (byte[] bByteArray : HexString.byteArrayGroup(bMacAddress, 6)) {
      System.out.println("Hex: " + new HexString(bByteArray));
    }
  }
}
