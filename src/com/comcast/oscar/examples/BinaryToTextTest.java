package com.comcast.oscar.examples;
import java.io.File;
import java.io.IOException;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.snmp4j.smi.SMIManagerServiceException;
import com.comcast.oscar.test.TestDirectoryStructure;


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
public class BinaryToTextTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		try {
			SMIManagerService.SmiManagerStart();
		} catch (SMIManagerServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean DOCSIS = false;
		boolean PC = true;

		File fDocsisBin = null;
		File fPacketCableBin = null;

		if (DOCSIS) {

			try {
				fDocsisBin = new File(new java.io.File( "." ).getCanonicalPath() 		
						+ File.separatorChar + "testfiles" 
						+ File.separatorChar + "xxxxxx.cm");
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			ConfigurationFileExport cfeDocsis = new ConfigurationFileExport (fDocsisBin);

			System.out.println(cfeDocsis.toPrettyPrint(0));

		} 

		System.out.println("+============================================================================================================================+");

		if (PC) {

			fPacketCableBin = TestDirectoryStructure.fInputDirFileName("xxxxxx.bin");

			ConfigurationFileExport cfePCTLV = new ConfigurationFileExport (fPacketCableBin);

			System.out.println(cfePCTLV.toPrettyPrint(0));
			
			
		}
	}

}
