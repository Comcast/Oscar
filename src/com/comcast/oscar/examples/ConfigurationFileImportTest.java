package com.comcast.oscar.examples;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;

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
public class ConfigurationFileImportTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		
		File file = null;
		
		try {
			file = new File(new java.io.File( "." ).getCanonicalPath() + File.separatorChar + "testfiles" + File.separatorChar +  "DOCSIS-GOLDEN.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		ConfigurationFileImport 	cfiDocsisConfig = null;
		
		try {
			try {
				cfiDocsisConfig = new ConfigurationFileImport(file);
			} catch (ConfigurationFileException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		System.out.println(cfiDocsisConfig.toString(":"));
		System.out.println(cfiDocsisConfig.toString());
		
	}

}
