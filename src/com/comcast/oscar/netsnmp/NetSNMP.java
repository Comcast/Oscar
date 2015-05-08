package com.comcast.oscar.netsnmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.comcast.oscar.utilities.DirectoryStructure;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class NetSNMP  {

	private static Boolean debug = Boolean.FALSE;	
		
	private static ObjectMapper omNetSNMP = null;	
	private static Map<String,String> hmDotTextMap = null;
	
	static {
		
		omNetSNMP = new ObjectMapper();
		
		hmDotTextMap = new HashMap<String,String>();
		
		try {
			hmDotTextMap = omNetSNMP.readValue(DirectoryStructureNetSNMP.fNetSNMPJSON(), 
												new TypeReference<HashMap<String, String>>() {});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Convert: docsDevNmAccessIp.1 -> .1.3.6.1.2.1.69.1.2.1.2.1
	 * 
	 * @param sOID
	 * @return .1.3.6.x.x.x.x.x
	 */
	public static String toDottedOID(String sOID) {
	
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug)
			System.out.println("NetSNMP.toDottedOID(): " + sOID);
				
		if (!CheckOIDDBLookup(sOID).isEmpty()) {
			return CheckOIDDBLookup(sOID);
		}
		
		/* If not installed, bypass and return input */
		if (!isSnmptranslateInstalled()) {
			return sOID;
		}
		
		String sDottedOID = "";
		
		/* Not a clean way to do it, but it works */
		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD + 	
				Constants.MIB_PARAMETER + 
				Constants.SNMP_TRANSLATE_OID_NAME_2_OID_DEC +
				sOID.replaceAll("\\s+1", " .iso")
					.replaceAll("\\s+\\.1", " .iso");

		if (debug|localDebug)
			System.out.println("NetSNMP.toDottedOID(): " + sSnmpTranslate);

		sDottedOID = runSnmpTranslate(sSnmpTranslate).get(0);
		
		/* Add Converted OIDS to Map for later Storage */
		UpdateJsonDB(sOID,sDottedOID);
		
		if (debug|localDebug)
			System.out.println("NetSNMP.toDottedOID(): " + sDottedOID);
		
		return runSnmpTranslate(sSnmpTranslate).get(0);
	}

	/**
	 * 
	 * Convert: .1.3.6.1.2.1.69.1.2.1.2.1 -> docsDevNmAccessIp.1
	 * 
	 * @param sOID
	 * @return docsDevNmAccessIp.1 */
	public static String toTextualOID(String sOID) {

		boolean localDebug = Boolean.FALSE;
	
		if (debug|localDebug)
			System.out.println("NetSNMP.toTextualOID(): " + sOID);
				
		if (!CheckOIDDBLookup(sOID).isEmpty()) {
			
			if (debug|localDebug)
				System.out.println("NetSNMP.toTextualOID(): (" + CheckOIDDBLookup(sOID) + ")");
			
			return CheckOIDDBLookup(sOID);
		}
		
		/* If not installed, bypass and return input */
		if (!isSnmptranslateInstalled()) {
			return sOID;
		}
		
		String sTextualOID = "";
		
		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD +  	
								Constants.MIB_PARAMETER + 
								Constants.SNMP_TRANSLATE_OID_DEC_2_OID_NAME +
								sOID;

		if (debug|localDebug)
			System.out.println("NetSNMP.toTextualOID(): " + sSnmpTranslate);

		sTextualOID = runSnmpTranslate(sSnmpTranslate).get(0);
		
		/* Add Converted OIDS to Map for later Storage */
		UpdateJsonDB(sOID,sTextualOID);
		
		if (debug|localDebug)
			System.out.println("NetSNMP.toTextualOID(): " + sTextualOID);
		
		return sTextualOID;

	}

	/**
	 * 
	 * @return True = Install | False = Not-Install*/
	public static boolean isSnmptranslateInstalled() {

		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD +  	
								Constants.SNMP_TRANSLATE_VERSION;

		if (runSnmpTranslate(sSnmpTranslate) == null) {
			return false;
		} else {
			return true;
		}
				
	}

	/**
	 * 
	 * If OID starts with .1.3.6 it is considered a DottedOID
	 * 
	 * @param sOID
	 * @return */
	public static boolean isDottedOID(String sOID) {

		if (Constants.ISO_ORG_DOD_DOTTED.matcher(sOID).find()) {
			return true;
		}

		return false;	
	}

	/**
	 * 
	 * @param sSnmpTranslateCMD
	 * @return OID Translation - Null is snmptranslate is not installed*/
	private static ArrayList<String> runSnmpTranslate(String sSnmpTranslateCMD) {

		boolean localDebug = Boolean.FALSE;

		if (debug|localDebug)
			System.out.println(sSnmpTranslateCMD);

		ArrayList<String> als = new ArrayList<String>();

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(sSnmpTranslateCMD);
		} catch (IOException e1) {
			/* If not found or installed */
			return null;
		}

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		String sStd_IO = "";

		/*Read the output from the command If Any */

		int iCounter = 0;
		try {
			while ((sStd_IO = stdInput.readLine()) != null) {

				//Clean up White Space
				if (!sStd_IO.isEmpty())
					als.add(sStd_IO);

				if (debug|localDebug)
					System.out.println(++iCounter + " IN: " + sStd_IO);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while ((sStd_IO = stdError.readLine()) != null) {

				als.add(sStd_IO);

				if (debug|localDebug)
					System.out.println(++iCounter + " OUT: " + sStd_IO);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return als;
	}
	

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
	public static class DirectoryStructureNetSNMP extends DirectoryStructure {
		
		/**
		 * @return NetSNMP subdirectory*/
		public static File fNetSNMPJSON() 
		{
			return new File(fDbDir().getName() + File.separator + Constants.DOTTED_TEXTUAL_NetSNMP_MAP_FILE) ;
		}
		
	}
	
	/**
	 * 
	 * @param sOIDKey
	 * @param sOIDConvert
	 */
	private static void UpdateJsonDB(String sOIDKey, String sOIDConvert) {
		
		/*TODO to go back and use a bidirectional collector */
		hmDotTextMap.put(sOIDKey, sOIDConvert);
		hmDotTextMap.put(sOIDConvert, sOIDKey);
		
		try {
			omNetSNMP.writeValue(DirectoryStructureNetSNMP.fNetSNMPJSON(), hmDotTextMap);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param sOID
	 * @return
	 */
	private static String CheckOIDDBLookup(String sOID) {
		
		String sReturn = "";
				
		if (hmDotTextMap.containsKey(sOID)) {
			
			if (debug)
				System.out.println("CheckOIDDBLookup().containsKey " + sOID + " -> " + hmDotTextMap.get(sOID));
			
			return hmDotTextMap.get(sOID);
		
		}
		
		return sReturn;
		
	}
	
}

