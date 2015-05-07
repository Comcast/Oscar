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
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/

/**
 */
public class ConfigurationFileTest {

	/**
	 * Method main.
	 * @param args String[]
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
	
		boolean DOCSIS = true;
		
		if (DOCSIS) {
			System.out.println("+--------------------------------------------------------------------------------------------------------+");

			System.out.println("ConfigurationFileExportTest: DOCSIS File");

			ConfigurationFileExport cfeDOCTLV = new ConfigurationFileExport (new File("c:\\d10_syscontact.cm"));

			//ConfigrationFileExport cfeDOCTLV = new ConfigrationFileExport (new File("c:\\d10_syscontact-reduce.cm"));
		
			System.out.println(cfeDOCTLV.toPrettyPrint(0));
		}

	}	
		

}
