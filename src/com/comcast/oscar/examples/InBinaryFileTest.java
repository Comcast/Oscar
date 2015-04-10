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

import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.utilities.HexString;

/**
 */
public class InBinaryFileTest {

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {
		
		File fBin = TestDirectoryStructure.fInputDirFileName("xxxx.bin");
		
		if (HexString.verifyAsciiPlainText(HexString.fileToByteArray(fBin))) {
			System.out.println("ASCII");
		} else {
			System.out.println("BIN");
		}
		
	}
	
}
