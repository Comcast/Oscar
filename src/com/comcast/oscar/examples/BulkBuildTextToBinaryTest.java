package com.comcast.oscar.examples;

import java.io.File;
import java.io.IOException;

import com.comcast.oscar.buildbulk.BulkBuild;
import com.comcast.oscar.configurationfile.CommonTlvInsertions;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvException;

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
public class BulkBuildTextToBinaryTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String sSharedSecret = "SHAREDSECRET";

		boolean boolOVERWRITE_TLV = true;

		CommonTlvInsertions cti = new CommonTlvInsertions();



		/*																TEXT to Binary														*/

		cti = new CommonTlvInsertions();


		/* Firmware Filename */
		String sFirmwareFileName = "firmware.bin";		
		cti.insertFirmwareFileName(sFirmwareFileName,boolOVERWRITE_TLV);


		/* Manufacture Certificate */		
		File fCertificate = null;

		try {
			fCertificate  = new File(new java.io.File( "." ).getCanonicalPath() + File.separatorChar + "certificate" + File.separatorChar + "XXXXX-CVC.der");
		} catch (IOException e) {
			e.printStackTrace();
		}

		cti.insertCoSignCVC(fCertificate,boolOVERWRITE_TLV);



		/* MaxCPE */
		int iMaxCPE = 1;
		cti.insertMaxCPE(iMaxCPE,boolOVERWRITE_TLV);

		/* DownStream Frequency */
		int iDownstreamFreq = 723000000;
		cti.insertDownstreamFrequency(iDownstreamFreq,boolOVERWRITE_TLV);

		/*Direct TLV Insertion - UpstreamChannelID TLV: [2]*/
		String sTLV = "020101";
		try {
			cti.insertTLV(sTLV, boolOVERWRITE_TLV);
		} catch (TlvException e) {
			e.printStackTrace();
		}

		BulkBuild bb = new BulkBuild(	TestDirectoryStructure.inputDir(), 
										TestDirectoryStructure.outputDir(),
										BulkBuild.TEXT_OUTPUT,
										sSharedSecret,
										cti);

		bb.start();		


	}

}
