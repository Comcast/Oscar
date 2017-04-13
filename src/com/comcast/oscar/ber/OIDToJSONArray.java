package com.comcast.oscar.ber;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.netsnmp.NetSNMP;
import com.comcast.oscar.parser.tlvParser;
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

public class OIDToJSONArray {

	private String sOID, sDataType, sValue = null;
	
	private final String OID = "OID";
	private final String DATA_TYPE = "DATA_TYPE";
	private final String VALUE = "VALUE";
	
	/**
	 * 
	 * @param sOID
	 * @param sDataType
	 * @param sValue*/
	public OIDToJSONArray (String sOID, String sDataType , String sValue) {
		this.sOID = NetSNMP.toDottedOID(sOID);
		this.sDataType = sDataType;
		this.sValue = sValue;	
	}

	/**
	 * 
	 * @param sOID
	 * @param bValue*/
	public OIDToJSONArray (String sOID , byte[] bValue) {
		this.sOID = NetSNMP.toDottedOID(sOID);
		this.sDataType = Integer.toString(BinaryConversion.byteToUnsignedInteger(BERService.HEX));
		this.sValue = new HexString(bValue).toString(":");	
	}
	
	/**
	 * Takes tlvParser that contains SNMP OID Values and Builds a JSONArray
	 * @param ctx*/
	public OIDToJSONArray (tlvParser.SnmpContext ctx) {
		this.sOID = NetSNMP.toDottedOID(ctx.oid().getText());
		this.sDataType = Integer.toString(BinaryConversion.byteToUnsignedInteger(BERService.berStringDataTypeToByte(ctx.dataType().getText())));
		this.sValue = ctx.value().getText();
	}
	
	/**
	 * 
	 * @return Map of the OID, DataType and Value */
	public Map<String,String> toMap () {
		
		Map<String, String> mssReturn = new HashMap<String,String>();
		
		mssReturn.put(OID, this.sOID);
		mssReturn.put(DATA_TYPE, this.sDataType);
		
		//Clean up double quotes in the from and end of string
		this.sValue = this.sValue.replaceAll("^\"|\"$", "");			
		mssReturn.put(VALUE, this.sValue);
					
		return mssReturn;
	}
	
	/**
	 *
	 * @return JSON Array of the OID, DataType and Value */
	public JSONArray toJSONArray () {
		
		JSONArray jaOID = new JSONArray();
		
		JSONObject joOID = new JSONObject();
		
		try {
			
			this.sOID = this.sOID.replaceAll("^\\.","");
					
			joOID.put(OID, this.sOID);
			
			joOID.put(DATA_TYPE, this.sDataType);
			
			//Clean up double quotes in the from and end of string
			this.sValue = this.sValue.replaceAll("^\"|\"$", "");
			joOID.put(VALUE, this.sValue);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jaOID.put(joOID);
	}
	
	/**
	 * 
	 * @param joSnmpDictionary	
	 * @return JSONObject
	 * @throws JSONException  */
	public JSONObject updateSnmpJsonObject (JSONObject joSnmpDictionary) throws JSONException {	
		return joSnmpDictionary.put(Dictionary.VALUE, toJSONArray());
	}
	
}
