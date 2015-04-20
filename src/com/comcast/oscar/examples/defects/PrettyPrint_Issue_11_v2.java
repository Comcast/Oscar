package com.comcast.oscar.examples.defects;

import java.io.FileNotFoundException;

import com.comcast.oscar.configurationfile.ConfigrationFileException;
import com.comcast.oscar.configurationfile.ConfigrationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
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


 * @author Maurice Garcia (maurice.garcia.2015@gmail.com)
 */


public class PrettyPrint_Issue_11_v2 {

	public static void main(String[] args) {
		/***************************
		 * Text to Binary
		 ***************************/
		ConfigrationFileImport cfi = null;
		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName("Issue-11-Parsing-Error-v2.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30,cfi.getTlvBuilder());
		
		ConfigurationFileExport cfe =  new ConfigurationFileExport (cf);
		
		//System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		//System.out.println("+-------------------------------------------------------------------------------------------+");
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_DEFAULT_TLV));

	}

}
