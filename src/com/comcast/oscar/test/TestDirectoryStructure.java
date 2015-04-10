package com.comcast.oscar.test;

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

import com.comcast.oscar.utilities.DirectoryStructure;

/**
 */
public class TestDirectoryStructure extends DirectoryStructure {
	
	protected static String sInputDirName 	= "input";
	protected static String sOutputDirName 	= "output";
	protected static String sTestDirName 	= "test";
	protected static String sCertificateDirName = "certificate";
	protected static String sDigitMapDirName = "digitmap";
	protected static String sInputBulkDirName 	= "inputBulk";
	protected static String sOutputBulkDirName 	= "outputBulk";
	
	public final static File fTestDirPath = new File(DirectoryStructure.sBasePath() + File.separator + sTestDirName);
	
	/**
	 * 
	
	 * @return File
	 */
	public static File inputDir() {
		return new File(fTestDirPath + File.separator + sInputDirName);
	}

	/**
	 * 
	 * @param sFileName
	
	 * @return File
	 */
	public static File fInputDirFileName(String sFileName) {
		return new File(inputDir() + File.separator + sFileName);
	}
	
	/**
	 * 
	
	 * @return File
	 */
	public static File outputDir() {
		return new File(fTestDirPath + File.separator + sOutputDirName);
	}
	
	/**
	 * 
	 * @param sFileName
	
	 * @return File
	 */
	public static File fOutputDirFileName(String sFileName) {
		return new File(outputDir() + File.separator + sFileName);
	}
	
	/**
	 * 
	
	 * @return File
	 */
	public static File certificateDir() {
		return new File(fTestDirPath + File.separator + sCertificateDirName);
	}
	
	/**
	 * 
	 * @param sFileName
	
	 * @return File
	 */
	public static File fCertificateDirFileName(String sFileName) {
		return new File(certificateDir() + File.separator + sFileName);
	}
	
	/**
	 * 
	
	 * @return File
	 */
	public static File digitMapDir() {
		return new File(fTestDirPath + File.separator + sDigitMapDirName);
	}
	
	/**
	 * 
	 * @param sFileName
	
	 * @return File
	 */
	public static File fDigitMapDirFileName(String sFileName) {
		return new File(digitMapDir() + File.separator + sFileName);
	}

	/**
	 * 
	
	 * @return File
	 */
	public static File inputBulkDir() {
		return new File(fTestDirPath + File.separator + sInputBulkDirName);
	}

	/**
	 * 
	 * @param sFileName
	
	 * @return File
	 */
	public static File fInputBulkDirFileName(String sFileName) {
		return new File(inputDir() + File.separator + sFileName);
	}
	
	/**
	 * 
	
	 * @return File
	 */
	public static File outputBulkDir() {
		return new File(fTestDirPath + File.separator + sOutputBulkDirName);
	}
	
	/**
	 * 
	 * @param sFileName
	
	 * @return File
	 */
	public static File fOutputBulkDirFileName(String sFileName) {
		return new File(outputDir() + File.separator + sFileName);
	}

}
