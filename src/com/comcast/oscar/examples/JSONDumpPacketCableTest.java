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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.snmp4j.smi.SMIManagerServiceException;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;


/**
 */
public class JSONDumpPacketCableTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		boolean TEXT = true;
		boolean BINARY = true;
		
		File fPacketCableTxt = null;
		File fPacketCableBin = null;
		
		ConfigurationFileImport cfiPacketCable = null;
	
		try {
			SMIManagerService.SmiManagerStart();
		} catch (SMIManagerServiceException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

																			/* TEXT */
		if (TEXT) {													

			try {
				
				fPacketCableTxt = new File(new java.io.File( "." ).getCanonicalPath() 
						+ File.separatorChar + "testfiles" 
						+ File.separatorChar +  "IMS-PKT-CABLE-CONFIG.txt");
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				try {
					cfiPacketCable = new ConfigurationFileImport(fPacketCableTxt);
				} catch (ConfigurationFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			TlvDisassemble tdPacketCable = new TlvDisassemble(cfiPacketCable.getTlvBuilder(),TlvDisassemble.TLV_TYPE_PACKET_CABLE);

			System.out.println("PacketCable-TEXT-JSONArray: " + tdPacketCable.getTlvDictionary().toString());

		}
		
																			/* Binary */
		if (BINARY) {

			TlvDisassemble tdPacketCable = null;
			
			try {
				
				fPacketCableBin = new File(new java.io.File( "." ).getCanonicalPath() 
						+ File.separatorChar + "testfiles" 
						+ File.separatorChar +  "IMS-PKT-CABLE-CONFIG.bin");
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			ConfigurationFileExport cfePacketCable = new ConfigurationFileExport(fPacketCableBin);

			try {
				tdPacketCable = new TlvDisassemble(cfePacketCable.getTlvBuilder(),TlvDisassemble.TLV_TYPE_PACKET_CABLE);
			} catch (TlvException e) {
				e.printStackTrace();
			}

			System.out.println("PacketCable-BIN-JSONArray: " + tdPacketCable.getTlvDictionary().toString());	
		}

	}

}
