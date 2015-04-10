package com.comcast.oscar.examples;
import java.io.File;
import java.io.FileNotFoundException;

import com.comcast.oscar.configurationfile.ConfigrationFileException;
import com.comcast.oscar.configurationfile.ConfigrationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.snmp4j.smi.SMIManagerServiceException;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexDump;
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
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/

/**
 */
public class ConfigurationFileToHexDumpTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		boolean DOCSIS = true;

		boolean PACKET_CABLE_20 = true;

		File file = null;

		try {
			SMIManagerService.SmiManagerStart();
		} catch (SMIManagerServiceException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if (DOCSIS) {
			System.out.println("+------------------------------------------------------------------------------------------------------------+");
			System.out.println("+----------------------------------------DOCSIS--------------------------------------------------------------+");
			System.out.println("+------------------------------------------------------------------------------------------------------------+");

			file = new File("c:" + File.separatorChar + "DocsisTestFile-131220.txt");			
			String sConfigurationFileName = "c:" + File.separatorChar + "config" + File.separatorChar + "DocsisTestFile-131220.bin";

			ConfigrationFileImport cfi = null;

			try {
				try {
					cfi = new ConfigrationFileImport(file);
				} catch (ConfigrationFileException e) {
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

			ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30,tb);

			cf.commit();

			System.out.println(HexDump.dumpHexString(cf.toByteArray()));


		} 

		if (PACKET_CABLE_20) {

			System.out.println("+------------------------------------------------------------------------------------------------------------+");
			System.out.println("+----------------------------------------PACKET CABLE-2.0----------------------------------------------------+");
			System.out.println("+------------------------------------------------------------------------------------------------------------+");

			file = new File("c:" + File.separatorChar + "PacketCable-2.0.txt");
			String sConfigurationFileName = "c:" + File.separatorChar + "config" + File.separatorChar + "PacketCable-2.0.bin";

			ConfigrationFileImport cfi = null;

			try {
				try {
					cfi = new ConfigrationFileImport(file);
				} catch (ConfigrationFileException e) {
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

			ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.PKT_CBL_VER_20,tb);

			cf.commit();

			System.out.println(HexDump.dumpHexString(cf.toByteArray()));


		}
	}


}
