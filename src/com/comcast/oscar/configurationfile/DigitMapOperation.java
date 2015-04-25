package com.comcast.oscar.configurationfile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comcast.oscar.ber.BEROIDConversion;
import com.comcast.oscar.ber.OIDToJSONArray;
import com.comcast.oscar.compiler.packetcablecompiler.PacketCableConstants;
import com.comcast.oscar.sql.queries.DictionarySQLQueries;
import com.comcast.oscar.tlv.TlvAssembler;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvVariableBinding;
import com.comcast.oscar.tlv.dictionary.Dictionary;
import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;

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


public class DigitMapOperation {

	public static String DEFAULT_DIGIT_MAP_OID = PacketCableConstants.PKT_CABLE_DIGIT_MAP_OID;
	
	private static boolean debug = Boolean.FALSE;
	
	private ConfigurationFileExport cfe = null;
	
	private ConfigrationFileImport cfi = null;
	
	private JSONArray jaTLV64 = null;
	
	/**
	 * 
	 * @param cfe
	 */
	public DigitMapOperation(ConfigurationFileExport cfe) {
		
		this.cfe = cfe;
		
		this.jaTLV64 = cfe.getTopLevelTLVJSON(64);
	}

	/**
	 * 
	 * @param cfi
	 */
	public DigitMapOperation(ConfigrationFileImport cfi) {
		
		this.cfi = cfi;
		
		this.jaTLV64 = cfi.getTopLevelTLVJSON(64);		
	}
	
	/**
	 * 
	
	 * @return String output
	 * 
	 * Example:
	 *		OID: 1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.1

			TIMER S=4.000000
			TIMER Z=2.000000
			
			domain = "@ims.comcast.net"
			dialString = ";user=dialstring"
			dialPhone = ";user=phone" */

	public String getDigitMap() {
		
		boolean localDebug = Boolean.FALSE;
		
		String sDigitMapScript = null;
		
		String sDigitMapOID = null;
		
		JSONObject joTLV64 = null;
		
		JSONObject joDigitMap = null;
		
		try {
			
			joTLV64 = jaTLV64.getJSONObject(0);
						
			if (debug|localDebug)
				System.out.println("DigitMapOperation.getDigitMap()" + joTLV64);
			
			JSONArray jaDigitMap = joTLV64.getJSONArray(Dictionary.VALUE);
			
			joDigitMap = jaDigitMap.getJSONObject(0);
			
			sDigitMapScript = joDigitMap.getString(BEROIDConversion.VALUE);
			
			sDigitMapOID = joDigitMap.getString(BEROIDConversion.OID);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		String sDigitMapOut = "OID: " + sDigitMapOID + "\n\n" + sDigitMapScript;
		
		if (cfe != null) {
			 sDigitMapOut = "OID: " + sDigitMapOID + "\n\n" + sDigitMapScript;
		} else if (cfi != null) {
			 sDigitMapOut = "OID: " + sDigitMapOID + "\n\n" + new HexString(HexString.toByteArray(sDigitMapScript)).toASCII();
		}
				
		return sDigitMapOut;
	}
	
	/**
	 * 
	
	 * @return JSONObject
	 */
	public JSONObject getDigitMapJSON () {
		
		JSONObject joTLV64 = new JSONObject();
		
		JSONObject joDigitMap = new JSONObject();
		
		try {
			joTLV64 = jaTLV64.getJSONObject(0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONArray jaDigitMap = new JSONArray();
		
		try {
			jaDigitMap = joTLV64.getJSONArray(Dictionary.VALUE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			joDigitMap = jaDigitMap.getJSONObject(0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return joDigitMap;
	}
	
	/**
	 * 
	 * @param fDigitMap
	
	 * @return TlvBuilder
	 */
	public static TlvBuilder getDigitMapTlvBuilder(File fDigitMap) {
		
		boolean localDebug = Boolean.FALSE;
						
		//Convert File to ByteArray
		byte[] bDigitMap = HexString.fileToByteArray(fDigitMap);
		
		//Need to get the JSON Dictionary Object, in this case, we need to use Snmp64
		DictionarySQLQueries dsqSnmp64 = new DictionarySQLQueries(DictionarySQLQueries.PACKET_CABLE_DICTIONARY_TABLE_NAME);
		
		//Get JSON Dictionary Object
		JSONObject joDictSnmp64 = dsqSnmp64.getTlvDictionary(64);
		
		//Create JSON ARRAY for JSON Snmp64 Dictionary
		OIDToJSONArray otjoSnmp64 = new OIDToJSONArray(DEFAULT_DIGIT_MAP_OID,bDigitMap);
		
		try {
			joDictSnmp64 = otjoSnmp64.updateSnmpJsonObject(joDictSnmp64);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// Validate: http://www.jsoneditoronline.org/
		if (debug|localDebug)
			System.out.println("DigitMapOperation.getDigitMapTlvBuilder() - Snmp64 - JSON-DICTIONARY: " + joDictSnmp64);
				
										/* Convert to TLV */
		
		//Create a 	TlvAssembler for the JSON Dictionary			
		TlvAssembler taSnmp64 = null;
		
		//Create an JSONArray Object - Need to make this more clean
		JSONArray jaSnmp64 = new JSONArray();
		
		//Add JSONObject to JSONArray for TlvAssembler
		jaSnmp64.put(joDictSnmp64);
		
		//Get TlvAssembler() with Snmp64 TLV Encoding
		taSnmp64 = new TlvAssembler(jaSnmp64);
		
		//Create Map for Type To ByteLengh
		Map<Integer,Integer> mii = new HashMap<Integer,Integer>();
		
		//Add
		mii.put(BinaryConversion.byteToUnsignedInteger(PacketCableConstants.SNMP_TLV_64),2);
				
		//Create TlvVariableBinding
		TlvVariableBinding tvb = new TlvVariableBinding(taSnmp64.toByteArray(),mii);

		//Create tlvBuilder
		TlvBuilder tb = new TlvBuilder();
		
		//Add TlvVariable to TlvBuilder
		tb.add(tvb);
		
		if (debug|localDebug) {
			System.out.println("DigitMapOperation.getDigitMapTlvBuilder() - TlvBuilder: " + tb.toStringSeperation(":"));
			System.out.println("DigitMapOperation.getDigitMapTlvBuilder() - TlvBuilder-Map: " + tb.getMapTypeToByteLength());	
		}
		
		//Return TlvBuilder
		return tb;
		
	}
	
	/**
	 * 
	 * @param baDigitMap
	 * @param sOID
	
	 * @return TlvBuilder
	 */
	public static TlvBuilder getDigitMapTlvBuilder(byte[] baDigitMap, String sOID) {
		
		boolean localDebug = Boolean.FALSE;
		
		//Need to get the JSON Dictionary Object, in this case, we need to use Snmp64
		DictionarySQLQueries dsqSnmp64 = new DictionarySQLQueries(DictionarySQLQueries.PACKET_CABLE_DICTIONARY_TABLE_NAME);
		
		//Get JSON Dictionary Object
		JSONObject joDictSnmp64 = dsqSnmp64.getTlvDictionary(64);
		
		//Create JSON ARRAY for JSON Snmp64 Dictionary
		OIDToJSONArray otjoSnmp64 = new OIDToJSONArray(sOID,baDigitMap);
		
		try {
			joDictSnmp64 = otjoSnmp64.updateSnmpJsonObject(joDictSnmp64);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// Validate: http://www.jsoneditoronline.org/
		if (debug|localDebug)
			System.out.println("DigitMapOperation.getDigitMapTlvBuilder() - Snmp64 - JSON-DICTIONARY: " + joDictSnmp64);
				
										/* Convert to TLV */
		
		//Create a 	TlvAssembler for the JSON Dictionary			
		TlvAssembler taSnmp64 = null;
		
		//Create an JSONArray Object - Need to make this more clean
		JSONArray jaSnmp64 = new JSONArray();
		
		//Add JSONObject to JSONArray for TlvAssembler
		jaSnmp64.put(joDictSnmp64);
		
		//Get TlvAssembler() with Snmp64 TLV Encoding
		taSnmp64 = new TlvAssembler(jaSnmp64);
		
		//Create Map for Type To ByteLengh
		Map<Integer,Integer> mii = new HashMap<Integer,Integer>();
		
		//Add
		mii.put(BinaryConversion.byteToUnsignedInteger(PacketCableConstants.SNMP_TLV_64),2);
				
		//Create TlvVariableBinding
		TlvVariableBinding tvb = new TlvVariableBinding(taSnmp64.toByteArray(),mii);

		//Create tlvBuilder
		TlvBuilder tb = new TlvBuilder();
		
		//Add TlvVariable to TlvBuilder
		tb.add(tvb);
		
		if (debug|localDebug) {
			System.out.println("DigitMapOperation.getDigitMapTlvBuilder() - TlvBuilder: " + tb.toStringSeperation(":"));
			System.out.println("DigitMapOperation.getDigitMapTlvBuilder() - TlvBuilder-Map: " + tb.getMapTypeToByteLength());	
		}
		
		//Return TlvBuilder
		return tb;
		
	}
	
}
