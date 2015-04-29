package com.comcast.oscar.examples;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.compiler.docsiscompiler.DocsisConstants;
import com.comcast.oscar.configurationfile.ConfigrationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.snmp4j.smi.SMIManagerServiceException;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
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
public class CoCVCImportBinaryToBinaryTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {


		try {
			SMIManagerService.SmiManagerStart();
		} catch (SMIManagerServiceException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		File file = null;
		
		System.out.println("+------------------------------------------------------------------------------------------------------------+");
		System.out.println("+----------------------------------------DOCSIS--------------------------------------------------------------+");
		System.out.println("+------------------------------------------------------------------------------------------------------------+");
		
		try {
			file = new File(new java.io.File( "." ).getCanonicalPath() + File.separatorChar + "testfiles" + File.separatorChar + "DocsisTestFile-CVC-Test.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(new HexString(HexString.fileToByteArray(file)).toASCII());
		
		ConfigurationFileImport cfi = null;

		try {
			try {
				cfi = new ConfigurationFileImport(file);
			} catch (ConfigrationFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE,cfi.getTlvBuilder());
		
															
		/* Get CVC File */
		
		File fCVC = null;
		
		try {
			fCVC = new File(new java.io.File( "." ).getCanonicalPath() + File.separatorChar + "testfiles" + File.separatorChar + "XXXXX-NA-CVC.der");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TlvBuilder tbCVC = new TlvBuilder();
		
		try {
			tbCVC.add(Constants.CO_SIGNER_CVC, HexString.fileToByteArray(fCVC));
		} catch (TlvException e1) {
			e1.printStackTrace();
		}
		
		
		//Add CVC
		cf.add(tbCVC);
		
		cf.commit();

		System.out.println("+-------------------------------------------AFTER CVC INSERTION-----------------------------------------------------------------+");

		ConfigurationFileExport cfeDOCTLV = new ConfigurationFileExport(cf);

		System.out.println(cfeDOCTLV.toPrettyPrint(0));

	}

}
