package com.comcast.oscar.netsnmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import com.comcast.oscar.utilities.DirectoryStructure;
import com.comcast.oscar.utilities.Disk;
import com.comcast.oscar.utilities.HexString;
import com.comcast.oscar.utilities.PrettyPrint;
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
	private static BidiMap<String, String> bmDotTextMap = null;
    
	private static final Pattern NETSNMP_DESCRIPTION = Pattern.compile(""
            + ".*DESCRIPTION\\s+\"(.*)\"", Pattern.CASE_INSENSITIVE);
	
	static {
		
		FixNullNetSNMPJSON();
		
		omNetSNMP = new ObjectMapper();
		
		bmDotTextMap = new DualHashBidiMap<String,String>();
		
		try {
			bmDotTextMap = omNetSNMP.readValue(DirectoryStructureNetSNMP.fNetSNMPJSON(), 
												new TypeReference<DualHashBidiMap<String, String>>() {});
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
	 * @param sOID OID either Dotted or Textual OID
	 * @return .1.3.6.x.x.x.x.x */
	public static String toDottedOID(String sOID) {
	
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug)
			System.out.println("NetSNMP.toDottedOID(): " + sOID);
		
		if (isDottedOID(sOID)) {
			
			if (debug|localDebug)
				System.out.println("NetSNMP.toDottedOID() Is a DootedOID -> " + sOID);
			
			return sOID;
		}
		
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
	 * @param sOID OID either Dotted or Textual OID
	 * @return Example: docsDevNmAccessIp.1 */
	public static String toTextualOID(String sOID) {

		boolean localDebug = Boolean.FALSE;
	
		if (debug|localDebug)
			System.out.println("NetSNMP.toTextualOID(): " + sOID);
		
		if (!isDottedOID(sOID)) {
			return sOID;
		}
		
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
	 * @param sOID OID either Dotted or Textual OID
	 * @param boolDotTextFormat TRUE = Textual OID Output , FALSE = Dotted OID Output
	 * @return Dotted or Textual OID */
	public static String toOIDFormat(String sOID , boolean boolDotTextFormat) {
		
		/* Textual OID */
		if (boolDotTextFormat) {
			return toTextualOID(sOID);
		} 
		/* Dotted OID */
		else {
			return toDottedOID(sOID);
		}
		
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
	 * @param sOID
	 * @return
	 */
	public static String getDescription(String sOID) {
		
		boolean localDebug = Boolean.FALSE;
		String sDescription = "";
		String sSnmpTranslate = "";	
		
		if (debug|localDebug)
			System.out.println("NetSNMP.toTextualOID(): " + sOID);
		
		/* If not installed, bypass and return input */
		if (!isSnmptranslateInstalled()) {
			return sOID;
		}
		
		if (isDottedOID(sOID)) {
			
			sSnmpTranslate = 	Constants.SNMP_TRANSLATE_CMD +  	
								Constants.MIB_PARAMETER + 
								Constants.SNMP_TRANSLATE_DESCRIPTION_DOTTED_OID +
								sOID;
		} else {
			sSnmpTranslate = 	Constants.SNMP_TRANSLATE_CMD +  	
					Constants.MIB_PARAMETER + 
					Constants.SNMP_TRANSLATE_DESCRIPTION_TEXTUAL_OID +
					sOID;			
		}
				
		Matcher mDescription = NETSNMP_DESCRIPTION.matcher(runSnmpTranslate(sSnmpTranslate).toString());
		
		if (mDescription.find()) {			
			sDescription = PrettyPrint.ToParagraphForm(mDescription.group(1).replaceAll("\\s+", " "));		
		}
				
		return sDescription;	
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

		bmDotTextMap.put(sOIDKey, sOIDConvert);
		
		try {
			omNetSNMP.writeValue(DirectoryStructureNetSNMP.fNetSNMPJSON(), bmDotTextMap);
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
	 * @return returns a lookup value, blank if nothing is found
	 */
	private static String CheckOIDDBLookup(String sOID) {
		
		String sReturn = "";
				
		if (bmDotTextMap.containsKey(sOID)) {
			
			if (debug)
				System.out.println("CheckOIDDBLookup().containsKey " + sOID + " -> " + bmDotTextMap.get(sOID));
			
			return bmDotTextMap.get(sOID);
		
		} else if (bmDotTextMap.containsValue(sOID)) {
			
			if (debug)
				System.out.println("CheckOIDDBLookup().containsValue " + sOID + " -> " + bmDotTextMap.getKey(sOID));
			
			return bmDotTextMap.getKey(sOID);			
		}
		
		return sReturn;
		
	}
	
	/**
	 * Checks to see if the DB file is empty, if so put a single entry to prevent error
	 */
	private static void FixNullNetSNMPJSON() {
		
		if (!DirectoryStructureNetSNMP.fNetSNMPJSON().exists()) {
			Disk.writeToDisk("{\"1.3.6\":\"dod\"}", DirectoryStructureNetSNMP.fNetSNMPJSON());
		} else if (HexString.fileToByteArray(DirectoryStructureNetSNMP.fNetSNMPJSON()).length == 0) {
			Disk.writeToDisk("{\"1.3.6\":\"dod\"}", DirectoryStructureNetSNMP.fNetSNMPJSON());
		}
		
	}
	
}

