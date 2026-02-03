package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;

/**
 * @bannerLicense
	Copyright 2015 Comcast Cable Communications Management, LLC<br>
	___________________________________________________________________<br>
	Licensed under the Apache License, Version 2.0 (the "License")<br>
	you may not use this file except in compliance with the License.<br>
	You may obtain a copy of the License at<br>
	http://www.apache.org/licenses/LICENSE-2.0<br>
	Unless required by applicable law or agreed to in writing, software<br>
	distributed under the License is distributed on an "AS IS" BASIS,<br>
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
	See the License for the specific language governing permissions and<br>
	limitations under the License.<br>


 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */


public class PrettyPrintSuppressTLVComment {

	public static void main(String[] args) {
		
		/***************************
		 * Binary to Text
		 ***************************/
		@SuppressWarnings("deprecation")
		ConfigurationFileExport cfe = new ConfigurationFileExport (TestDirectoryStructure.fInputDirFileName("cm.bin"));
			
		/* No Suppression */
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

		/* With Suppression */
		System.out.println(cfe.toPrettyPrint(	ConfigurationFileExport.EXPORT_FOUND_TLV,
												ConfigurationFileExport.SUPPRESS_TLV_COMMENT));


	}

}
