package com.comcast.oscar.examples;
import java.io.File;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;

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

/**
 */
public class ConfigurationFileExportTest {

	/**
	 * Method main.
	 * @param args String[]
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		
		boolean DOCSIS = false;
		
		boolean PACKETCABLE = true;
		
		boolean DEXTER_TEST_FILE = false;
		
		if (DOCSIS) {
			System.out.println("+--------------------------------------------------------------------------------------------------------+");

			System.out.println("ConfigurationFileExportTest: DOCSIS File");

			ConfigurationFileExport cfeDOCTLV = new ConfigurationFileExport (new File("c:\\d10_syscontact.cm"));

			//ConfigrationFileExport cfeDOCTLV = new ConfigrationFileExport (new File("c:\\d10_syscontact-reduce.cm"));
		
			System.out.println(cfeDOCTLV.toPrettyPrint(0));
		}

		if (PACKETCABLE) {
			System.out.println("+--------------------------------------------------------------------------------------------------------+");

			System.out.println("ConfigurationFileExportTest: PACKET CABLE IMS File");

			ConfigurationFileExport cfePCTLV = new ConfigurationFileExport (new File("c:\\IMS-PKT-CABLE-CONFIG.bin"));

			System.out.println(cfePCTLV.toPrettyPrint(0));
		}
	
		if (DEXTER_TEST_FILE) {
			System.out.println("+--------------------------------------------------------------------------------------------------------+");

			System.out.println("ConfigurationFileExportTest: DOCSIS File");

			//ConfigrationFileExport cfeDOCTLV = new ConfigrationFileExport (new File("c:\\d10_syscontact.cm"));

			ConfigurationFileExport cfeDOCTLV = new ConfigurationFileExport (new File("C:\\dexter\\-2035192058\\Freedom\\tg852_gold_c01_pd_freq1"));
		
			System.out.println(cfeDOCTLV.toPrettyPrint(0));
		}

	}

}
