package com.comcast.oscar.examples;
import java.io.File;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.utilities.DirectoryStructure;


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
public class BinaryToTextTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		boolean DOCSIS = true;
		boolean PC = false;

		File fDocsisBin = null;
		File fPacketCableBin = null;

		if (DOCSIS) {

			fDocsisBin = DirectoryStructure.fTestFile("d10_ssd_codetest_862_oscar.cm");

			ConfigurationFileExport cfeDocsis = new ConfigurationFileExport (fDocsisBin,ConfigurationFileExport.DOCSIS_VER_30);

			System.out.println(cfeDocsis.toPrettyPrint(false));

		} 

		//System.out.println("+============================================================================================================================+");

		if (PC) {

		//	fPacketCableBin = TestDirectoryStructure.fInputDirFileName("xxxxxx.bin");
//
	//		ConfigurationFileExport cfePCTLV = new ConfigurationFileExport (fPacketCableBin);
//
	//		System.out.println(cfePCTLV.toPrettyPrint(0));
			
			
		}
	}

}
