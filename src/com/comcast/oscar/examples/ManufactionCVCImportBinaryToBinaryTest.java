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
import java.io.IOException;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.compiler.DocsisConstants;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.snmp4j.smi.SMIManagerServiceException;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;


/**
 */
public class ManufactionCVCImportBinaryToBinaryTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/*******************************DEFAULT SECTION*************************************/
		try {
			SMIManagerService.SmiManagerStart();
		} catch (SMIManagerServiceException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}	
		String sSharedSecret = "SHAREDSECRET";		
		/*******************************DEFAULT SECTION*************************************/

		File fDocsisBin = null;

		try {
			
			fDocsisBin = new File(new java.io.File( "." ).getCanonicalPath() 
					+ File.separatorChar + "testfiles" 
					+ File.separatorChar + "DOCSIS-GOLDEN-NO-MAN-CVC.bin");

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		TlvBuilder tb = new TlvBuilder();
		try {
			tb.add(new HexString(HexString.fileToByteArray(fDocsisBin)));
		} catch (TlvException e3) {
			e3.printStackTrace();
		}
		


		File fManCVC = null;

		try {

			fManCVC = new File(new java.io.File( "." ).getCanonicalPath() 
					+ File.separatorChar + "testfiles"
					+ File.separatorChar + "certificate"
					+ File.separatorChar + "CVC.der");

		} catch (IOException e) {
			e.printStackTrace();
		}

		TlvBuilder tbManCVC = new TlvBuilder();

		try {
			tbManCVC.add(Constants.MANUFACTURER_CVC, HexString.fileToByteArray(fManCVC));
		} catch (TlvException e1) {
			e1.printStackTrace();
		}
		
		tb.add(tbManCVC);
		
		try {
			tb = TlvBuilder.reorderTLV(true,tb);
		} catch (TlvException e1) {
			e1.printStackTrace();
		}
		
		ConfigurationFile cfDocsis = null;
		
		cfDocsis = new ConfigurationFile(	ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE,
											tb,
											sSharedSecret);
		cfDocsis.commit();
		
		File fDocsisBinOut = null;

		try {
			
			fDocsisBinOut = new File(new java.io.File( "." ).getCanonicalPath() 
					+ File.separatorChar + "testfiles" 
					+ File.separatorChar + "output" 
					+ File.separatorChar + "DOCSIS-GOLDEN-WITH-MAN-CVC-2.bin");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cfDocsis.setConfigurationFileName(fDocsisBinOut);

		if (cfDocsis.writeToDisk()) {
			System.out.println("Write to File: DOCSIS-GOLDEN-WITH-MAN-CVC-2.bin");
		} else {
			System.out.println("Write to File FAILED: DOCSIS-GOLDEN-WITH-MAN-CVC-2.bin");
		}

	}

}

