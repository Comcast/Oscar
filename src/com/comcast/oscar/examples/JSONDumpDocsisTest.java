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
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;


/**
 */
public class JSONDumpDocsisTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		boolean TEXT = true;
		boolean BINARY = true;
		
		File fDocsisText = null;		
		File fDocsisBin = null;
		TlvDisassemble tdDOCSIS = null;
		ConfigurationFileImport cfiDOCSIS = null;
																	/* TEXT */
		
		if (TEXT) {													

			try {
				
				fDocsisText = new File(new java.io.File( "." ).getCanonicalPath() 
						+ File.separatorChar + "testfiles" 
						+ File.separatorChar + "DOCSIS-GOLDEN.txt");
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				try {
					cfiDOCSIS = new ConfigurationFileImport(fDocsisText);
				} catch (ConfigurationFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			tdDOCSIS = new TlvDisassemble(cfiDOCSIS.getTlvBuilder(),TlvDisassemble.TLV_TYPE_DOCSIS);

			System.out.println("DOCSIS-TEXT-JSONArray: " + tdDOCSIS.getTlvDictionary().toString());

		}
		
															/* Binary */
		
		if (BINARY) {

			try {
				
				fDocsisBin = new File(new java.io.File( "." ).getCanonicalPath() 
						+ File.separatorChar + "testfiles" 
						+ File.separatorChar + "DOCSIS-GOLDEN.bin");
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			ConfigurationFileExport cfeDocsis = new ConfigurationFileExport(fDocsisBin);

			TlvDisassemble tdDocsis = null;
			
			try {
				tdDocsis = new TlvDisassemble(cfeDocsis.getTlvBuilder(),TlvDisassemble.TLV_TYPE_DOCSIS);
			} catch (TlvException e) {
				e.printStackTrace();
			}

			System.out.println("DOCSIS-BIN-JSONArray: " + tdDocsis.getTlvDictionary().toString());	
		}	
	}

}
