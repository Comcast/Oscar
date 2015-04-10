package com.comcast.oscar.snmp4j.smi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.snmp4j.SNMP4JSettings;

import com.comcast.oscar.utilities.DirectoryStructure;
import com.comcast.oscar.utilities.HexString;
import com.snmp4j.smi.SmiManager;
import com.snmp4j.smi.SmiParseException;
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


public class SMIManagerService {

	public static boolean DEBUG = Boolean.FALSE;

	public static boolean SNMP_SMI_SPEC_CHECK = true;

	/* LICENCE KEY MUST BE REMOVE BEFORE BEFORE REPO SUBMISSION*/
	public static String LICENSE = ""; 

	public static boolean MIB_COMPILE_OUTPUT_VERBOSE = false;
	
	private static SmiManager smiManager = null;

	/**
	 * 
	 */
	public static void SmiManagerStart() throws SMIManagerServiceException {

		/* This is used in case it is used as a service*/
		if (!isNull()) {return ;}

		boolean localDebug = Boolean.FALSE;

		if (!CheckForSMIParserPackage()) {
			throw new SMIManagerServiceException ("NO SmiParser Class Found");
		}

		try {
			smiManager = new SmiManager(LICENSE, DirectoryStructure.fMibsBinaryDir());
		} catch (IOException e) {
			e.printStackTrace();
		}

		SNMP4JSettings.setOIDTextFormat(smiManager);

		SNMP4JSettings.setVariableTextFormat(smiManager);

		smiManager.setOidFormat(SmiManager.OIDFormat.ObjectNameAndDecodedIndex4RoundTrip);

		try {
			for (String sModule : smiManager.listModules()) {

				if (DEBUG|localDebug) 
					System.out.println("SMIManagerService.smiManagerStart() -> Module: " + sModule);

				smiManager.loadModule(sModule); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param fLicence
	 */
	public static void SmiManagerStart (File fLicence) {

		if (!isNull()) {return ;}

		boolean localDebug = Boolean.FALSE;

		LICENSE = new HexString(HexString.fileToByteArray(fLicence)).toASCII();	

		try {
			smiManager = new SmiManager(LICENSE,DirectoryStructure.fMibsBinaryDir());
		} catch (IOException e) {
			e.printStackTrace();
		}

		SNMP4JSettings.setOIDTextFormat(smiManager);

		SNMP4JSettings.setVariableTextFormat(smiManager);

		smiManager.setOidFormat(SmiManager.OIDFormat.ObjectNameAndDecodedIndex4RoundTrip);

		try {
			for (String sModule : smiManager.listModules()) {

				if (DEBUG|localDebug) 
					System.out.println("SMIManagerService.smiManagerStart() -> Module: " + sModule);

				smiManager.loadModule(sModule); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 
	 * @param fLicence
	 * @param fMibRepository
	 */
	public static void SmiManagerStart (File fLicence , File fMibRepository) {

		if (!isNull()) {return ;}

		boolean localDebug = Boolean.FALSE;

		String sLicense = new HexString(HexString.fileToByteArray(fLicence)).toASCII();	

		try {
			smiManager = new SmiManager(sLicense, fMibRepository);
		} catch (IOException e) {
			e.printStackTrace();
		}

		SNMP4JSettings.setOIDTextFormat(smiManager);

		SNMP4JSettings.setVariableTextFormat(smiManager);

		smiManager.setOidFormat(SmiManager.OIDFormat.ObjectNameAndDecodedIndex4RoundTrip);

		try {
			for (String sModule : smiManager.listModules()) {

				if (DEBUG|localDebug) 
					System.out.println("SMIManagerService.smiManagerStart() -> Module: " + sModule);

				smiManager.loadModule(sModule); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 
	 * @return true is null, false is not
	 */
	public static boolean isNull() {

		boolean boolIsNull = false;
		
		if (smiManager == null) {
			boolIsNull = true;
		}
		
		return boolIsNull;
	}

	/**
	 * 
	 * @return return current SmiManagerObject
	 */
	public static SmiManager GetSmiManagerObject() {
		return smiManager;
	}

	/**
	 * 
	 * Compile text MIBS to SNMP4J Style Compile MIB Format
	 * 
	 * @param fDirectory
	 */
	public static void CompileMIBS () {

		boolean localDebug = Boolean.FALSE;

		if(DEBUG|localDebug)
			System.out.println("MIB-DIR-PATH: " + DirectoryStructure.fMibsTextDir().toString());

		File fDirectoyPath = DirectoryStructure.fMibsTextDir();

		if (!SNMP_SMI_SPEC_CHECK) {
			
			if(MIB_COMPILE_OUTPUT_VERBOSE)
				System.out.println("SNMP SMI STRICT CHECKING DISABLED");

			try {
				smiManager.compile(fDirectoyPath.listFiles(),null,true,true,true);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			
			if(MIB_COMPILE_OUTPUT_VERBOSE)
				System.out.println("SNMP SMI STRICT CHECKING ENABLE");

			for (File fMIB : fDirectoyPath.listFiles()) {

				if(MIB_COMPILE_OUTPUT_VERBOSE)
					System.out.println("+------Compiling-MIB--------------------(" + fMIB.getName() + ")----------------------+");
				
				try {
					smiManager.compile(fMIB);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (SmiParseException e) {
					
					if(MIB_COMPILE_OUTPUT_VERBOSE) {
						System.out.println("+------MIB-Compile-Error----------------(" + fMIB.getName() + ")----------------------+");
						System.out.println(e.getMessage());
					}
				}			
			}

		}
	}

	/**
	 * 
	 * @return true if the SmiParser Class is found.
	 */
	public static boolean CheckForSMIParserPackage() {

		boolean localDebug = Boolean.FALSE;

		try {

			Class.forName("com.agentpp.smiparser.SMI");

			if(DEBUG|localDebug)
				System.out.println("smiparser.SMI Class Found");
			return true;

		} catch (ClassNotFoundException e) {

			if(DEBUG|localDebug)
				System.out.println("smiparser.SMI Class Not Found ");

			return false;
		}
	}


}
