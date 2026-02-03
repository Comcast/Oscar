package com.comcast.oscar.examples;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.DigitMapOperation;

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
public class DigitMapFromPacketCableFileTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		File fPacketCableBin = null;
		
		File fPacketCableTxt = null;
		
		DigitMapOperation dmo = null;

		boolean BIN = true;
		boolean TEXT = false;
		
											/* Binary Input */
		if (BIN) {
			
			try {

				fPacketCableBin = new File(new java.io.File( "." ).getCanonicalPath()	
						+ File.separatorChar + "output" 
						+ File.separatorChar + "IMS-PKT-CABLE-CONFIG-DIGIT-MAP.txt-102.bin");
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			ConfigurationFileExport cfePacketCable = new ConfigurationFileExport (fPacketCableBin);	

			dmo = new DigitMapOperation(cfePacketCable);

			System.out.println(dmo.getDigitMap());


											/* Text Input */
		}

		System.out.println("+==============================================================================================================+");
		
		if (TEXT) {
			
			try {

				fPacketCableTxt = new File(new java.io.File( "." ).getCanonicalPath()	
						+ File.separatorChar + "testfiles" 
						+ File.separatorChar + "IMS-PKT-CABLE-CONFIG.txt");
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			ConfigurationFileImport cfiPacketCable = null;

			try {
				try {
					cfiPacketCable = new ConfigurationFileImport (fPacketCableTxt);
				} catch (ConfigurationFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}	

			dmo = new DigitMapOperation(cfiPacketCable);

			System.out.println(dmo.getDigitMap());

		}
	}
}
