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
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/



import com.comcast.oscar.compiler.packetcablecompiler.PacketCableCompiler;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

/**
 */
public class PacketCableHexOutput {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		byte[] baPacketCable = HexString.fileToByteArray(TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin"));
		
		try {
			for (HexString hs : PacketCableCompiler.getTopLevelTlvToHexStringList(baPacketCable)) {
				System.out.println("TLV-HEX: " + hs.toString(":"));
			}
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
	}

}
