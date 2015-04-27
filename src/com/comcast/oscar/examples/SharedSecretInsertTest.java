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

import com.comcast.oscar.configurationfile.ConfigrationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.snmp4j.smi.SMIManagerServiceException;
import com.comcast.oscar.utilities.HexString;

/**
 */
public class SharedSecretInsertTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			SMIManagerService.SmiManagerStart();
		} catch (SMIManagerServiceException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		File fDocsisText = null;
		
		try {
			fDocsisText = new File(new java.io.File( "." ).getCanonicalPath() 
					+ File.separatorChar + "testfiles" 
					+ File.separatorChar + "DOCSIS-GOLDEN.txt");
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		ConfigurationFileImport cfiDocsis = null;

		try {
			try {
				cfiDocsis = new ConfigurationFileImport(fDocsisText);
			} catch (ConfigrationFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		File fSharedSecret = null;
		
		try {
			fSharedSecret = new File(new java.io.File( "." ).getCanonicalPath() 
					+ File.separatorChar + "testfiles" 
					+ File.separatorChar + "sharedsecret.txt");
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		ConfigurationFile cfDocsis = new ConfigurationFile(	ConfigurationFile.DOCSIS_VER_31,
															cfiDocsis.getTlvBuilder(),
															new HexString(HexString.fileToByteArray(fSharedSecret)).toASCII());

		cfDocsis.commit();
		
		File fDocsisBin = null;
		
		try {
			fDocsisBin = new File(new java.io.File( "." ).getCanonicalPath() 
					+ File.separatorChar + "testfiles" 
					+ File.separatorChar + "output" 
					+ File.separatorChar + "DOCSIS-GOLDEN-SHARED-SECRET-KEY-SHAREDSECRET.bin");
			
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		cfDocsis.setConfigurationFileName(fDocsisBin);
		
		if (cfDocsis.writeToDisk()) {
			System.out.println("Write to File: DOCSIS-GOLDEN-SHARED-SECRET-KEY-SHAREDSECRET.bin");
		} else {
			System.out.println("Write to File FAILED: DOCSIS-GOLDEN-SHARED-SECRET-KEY-SHAREDSECRET.bin");
		}
		
	}

}
