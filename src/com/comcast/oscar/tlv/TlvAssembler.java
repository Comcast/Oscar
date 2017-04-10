package com.comcast.oscar.tlv;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comcast.oscar.ber.OIDBERConversion;
import com.comcast.oscar.datatype.DataTypeDictionaryReference;
import com.comcast.oscar.datatype.DataTypeFormatConversion;
import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.utilities.HexString;
import com.comcast.oscar.utilities.JSONTools;

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

public class TlvAssembler extends TlvBuilder {
	//Log4J2 logging
    private static final Logger logger = LogManager.getLogger(TlvAssembler.class);
	
	private JSONArray jaTlvDictionary = null;
	
	private final boolean debug = Boolean.FALSE;
	
	/**
	 * 
	 * @param jaTlvDictionary
	 */
	public TlvAssembler(JSONArray jaTlvDictionary) {
		
		super();
		
		this.jaTlvDictionary = jaTlvDictionary;
			
		convertJSONToTLV();
	}
	
	/* Empty Constructor to deal with null pointers */
	public TlvAssembler() {}

	/**
	 * 
	 * @return String JSONArray of Dictionary*/
	public String toJSONArray() {
		return jaTlvDictionary.toString();	
	}
	
	/**
	 *  Convert a JSONArray to TLV to buffer*/
	private void convertJSONToTLV () {
		
		boolean localDebug = Boolean.FALSE;
		
		TlvBuilder tbLocalBuilder = new TlvBuilder();
		
		//Cycle thru JSON Array and inspect each JSON Object
		for (int iJsonArrayIndex = 0 ; iJsonArrayIndex < jaTlvDictionary.length() ; iJsonArrayIndex++ ) {

			if (debug|localDebug) logger.debug("+--------------------------------JSON-ARRAY-INDEX: " + iJsonArrayIndex + "--------------------------------------------+");

			JSONObject joTlvDictionary = null;

			try {
				joTlvDictionary = jaTlvDictionary.getJSONObject(iJsonArrayIndex);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			try {

				//Check to see if this object have SubTypes , if so go into SubType Array
				if (joTlvDictionary.getBoolean(Dictionary.ARE_SUBTYPES)) {
					
					if (debug|localDebug) 
						logger.debug("TlvAssembler.convertJSONToTLV: TRUE-SUBTYPES: " + joTlvDictionary);

					//If this is the start of the array, I need only advance 2 bytes to the next Type
					tbLocalBuilder.add(buildTlvFromTlvDictionary (joTlvDictionary.getJSONArray(Dictionary.SUBTYPE_ARRAY)));
					
					//Add TopLevel TLV Type
					try {
						tbLocalBuilder.encapsulateTlv(joTlvDictionary.getInt(Dictionary.TYPE));
					} catch (TlvException e) {
						e.printStackTrace();
					}
					
					//Add to Superclass TlvBuilder
					super.add(tbLocalBuilder.clone());
					
					//Clean up
					tbLocalBuilder.clear();
					
				} else {

					if (debug|localDebug) 
						logger.debug("TlvAssembler.convertJSONToTLV: FALSE-SUBTYPES: " + joTlvDictionary);

					//Add to Superclass TlvBuilder//
					super.add(buildTlvFromTlvDictionary (joTlvDictionary));						
				}


			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
	}
	
	/**
	 * 
	 * @param jaTlvDictionary JSONArray of Dictionary	
	 * @return TlvBuilder*/
	private TlvBuilder buildTlvFromTlvDictionary (JSONArray jaTlvDictionary) {
		
		boolean localDebug = Boolean.FALSE;
		
		TlvBuilder tbLocalTlvBuilderFinal = new TlvBuilder();
		
		TlvBuilder tbLocalTlvBuilder = new TlvBuilder();
				
		//Cycle thru JSON Array and inspect each JSON Object
		for (int iJsonArrayIndex = 0 ; iJsonArrayIndex < jaTlvDictionary.length() ; iJsonArrayIndex++ ) {

			if (debug|localDebug) 
				logger.debug("+--------------------------------JSON-ARRAY-INDEX: " + iJsonArrayIndex + "--------------------------------------------+");

			JSONObject joTlvDictionary = null;

			try {
				joTlvDictionary = jaTlvDictionary.getJSONObject(iJsonArrayIndex);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			try {

				//Check to see if this object have SubTypes , if so go into SubType Array
				if (joTlvDictionary.getBoolean(Dictionary.ARE_SUBTYPES)) {
					
					//Get Type Value to later prefix
					int iTypeLocal = joTlvDictionary.getInt(Dictionary.TYPE);
					
					if (debug|localDebug) 
						logger.debug(
								"SUB-TYPE-ARRAY-TRUE: -> Type: " + joTlvDictionary.getString(Dictionary.TYPE) + 
								" -> " + joTlvDictionary.getString(Dictionary.TLV_NAME));

					//If this is the start of the array, I need only advance 2 bytes to the next Type
					tbLocalTlvBuilder.add(buildTlvFromTlvDictionary (joTlvDictionary.getJSONArray(Dictionary.SUBTYPE_ARRAY)));
					
					if (tbLocalTlvBuilder.length() != 0) {

						if (debug|localDebug) 
							logger.debug("TlvAssembler.buildTlvFromTlvDictionary:" +
									" -> TLV-NAME: " + joTlvDictionary.getString(Dictionary.TLV_NAME) + 
									" -> tbLocalTlvBuilder.length: " + tbLocalTlvBuilder.length());
					
						try {
							tbLocalTlvBuilder.encapsulateTlv(iTypeLocal);
						} catch (TlvException e) {
							e.printStackTrace();
						}						
					}
					
					//Aggregate to final TLV Build
					tbLocalTlvBuilderFinal.add(tbLocalTlvBuilder);
					
					//Clean up previous TLV Builds
					tbLocalTlvBuilder.clear();
					
				} else {

					if (debug|localDebug) 
						logger.debug(	"SUB-TYPE-ARRAY-TRUE: -> Type: " + joTlvDictionary.getString(Dictionary.TYPE) + 
											" -> " + joTlvDictionary.getString(Dictionary.TLV_NAME));

					//If this is the start of the array, I need only advance 2 bytes to the next Type
					tbLocalTlvBuilder.add(buildTlvFromTlvDictionary (joTlvDictionary));
					
					//Aggregate to final TLV Build
					tbLocalTlvBuilderFinal.add(tbLocalTlvBuilder);
					
					//Clean up previous TLV Builds
					tbLocalTlvBuilder.clear();
				}


			} catch (JSONException e) {
				e.printStackTrace();
			}		
			
		}
			
		if (debug|localDebug) 
			logger.debug("TlvAssembler.buildTlvFromTlvDictionary().tbLocalTlvBuilderFinal: " + tbLocalTlvBuilderFinal.toStringSeperation(":"));
		

		return tbLocalTlvBuilderFinal;

	}
	
	/**
	 * 
	 * @param joTlvDictionary	
	 * @return TlvBuilder
	 * @throws JSONException */
	private TlvBuilder buildTlvFromTlvDictionary (JSONObject joTlvDictionary) throws JSONException {
		
		boolean localDebug = Boolean.FALSE;
				
		if (localDebug|debug) {
			logger.debug("TlvAssembler.buildTlvFromTlvDictionary(jo): " + joTlvDictionary.toString());
			logger.debug("TlvAssembler.buildTlvFromTlvDictionary.super: " + super.toString());
		}
		
		TlvBuilder tbLocalTlvBuilder = new TlvBuilder();
		
		//Need to determine if there is a Value in the Object
		if (joTlvDictionary.has(Dictionary.VALUE)) {
					
			//Get TLV Value Length
			int iTlvValueLength = joTlvDictionary.getInt(Dictionary.LENGTH_MAX);
			
			//Get the DataTpe
			String sDataType = joTlvDictionary.getString(Dictionary.DATA_TYPE);
			
			//Get Type
			int iType = joTlvDictionary.getInt(Dictionary.TYPE);

			
			/*
			 * 140320 -mgarcia
			 * 	Issue found with submitting no value for the Value Tag.
			 * 	Only Strings will be allowed to have no Value
			 * 	Else Return an empty TlvBuilder Object
			 */
			if ((joTlvDictionary.getString(Dictionary.VALUE).matches("\\s*")) && 
					(
							(!sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING_NULL_TERMINATED)) ||
							(!sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING))
					)
				) {
				return tbLocalTlvBuilder;				
			}
			
			//Determine how to encode the value
			if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_INTEGER)) {
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary().DATA_TYPE_INTEGER: " + tbLocalTlvBuilder.toString());
								
				//Get Integer Value
				int iTlvValue = joTlvDictionary.getInt(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString();
				hsTlvValue.add(iTlvValue);
				
				//PADD If Necessary
				hsTlvValue.prefixNullPaddToLength(iTlvValueLength);
				
				//Add Integer TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue);
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("DATA_TYPE_INTEGER: " + tbLocalTlvBuilder.toString());
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_OID)) {
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary().DATA_TYPE_OID: " + tbLocalTlvBuilder.toString());
				
				//Convert JSONObject to Map
				//Map<String,Object> msoOidDataTypeValue = JSONTools.toMap(joTlvDictionary.getJSONObject(Dictionary.VALUE));
				
				JSONArray ja = joTlvDictionary.getJSONArray(Dictionary.VALUE);
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary()->ja.getJSONObject(0): " + ja.getJSONObject(0).toString());
				
				Map<String,Object> msoOidDataTypeValue = JSONTools.toMap(ja.getJSONObject(0));
				
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary().msoODV: " + msoOidDataTypeValue.toString());
						
				OIDBERConversion obcOidDataTypeValue = new  OIDBERConversion(msoOidDataTypeValue);
				
				if (joTlvDictionary.getInt(Dictionary.BYTE_LENGTH) > 1) {
					
					//In this case, this is using an Extended SNMP OID and create a VarBind TLV
					TlvVariableBinding tvbLocal = new TlvVariableBinding();
					
					//Build Extended OID to TlvVariableBinding
					try {
						tvbLocal.add(joTlvDictionary.getInt(Dictionary.TYPE), obcOidDataTypeValue.getBER(), joTlvDictionary.getInt(Dictionary.BYTE_LENGTH));
					} catch (TlvException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//Added TlvVariableBinding to TlvBuilder
					tbLocalTlvBuilder.add(tvbLocal);
					
				} else {
					//ADD OID TLV
					try {
						tbLocalTlvBuilder.add(joTlvDictionary.getInt(Dictionary.TYPE),obcOidDataTypeValue.getBER());
					} catch (Exception e) {
						e.printStackTrace();
					}			
				}
							
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_OID: " + tbLocalTlvBuilder.toString());
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY)) {
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary().DATA_TYPE_BYTE_ARRAY: " + tbLocalTlvBuilder.toString());
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(HexString.toByteArray(sTlvValue));
				
				//Add Integer TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_BYTE_ARRAY: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING_NULL_TERMINATED)) {
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary().DATA_TYPE_STRING_NULL_TERMINATED: " + tbLocalTlvBuilder.toString());

				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString();
				
				//Append Null Terminates at the end of the string
				hsTlvValue.add(sTlvValue, true);
				
				//Add Integer TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_STRING_NULL_TERMINATED: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING)) {
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary().DATA_TYPE_STRING: " + tbLocalTlvBuilder.toString());

				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert String to HexString
				HexString hsTlvValue = new HexString();
				hsTlvValue.add(sTlvValue);
				
				//Add Integer TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_STRING: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_MULTI_TLV_BYTE_ARRAY)) {
				
				if (debug|localDebug) 
					logger.debug("Type: " + iType + " -> TlvBuilder.buildTlvFromTlvDictionary().DATA_TYPE_MULTI_TLV_BYTE_ARRAY: " + tbLocalTlvBuilder.toString());

				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(HexString.toByteArray(sTlvValue));
				
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_MULTI_TLV_BYTE_ARRAY: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING_BITS)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);

				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_STRING_BITS: " + sTlvValue);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(DataTypeFormatConversion.binaryBitMaskToByteArray(sTlvValue));
				
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_STRING_BITS: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_DOUBLE_BYTE_ARRAY)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(DataTypeFormatConversion.doubleByteArray(sTlvValue));
				
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_DOUBLE_BYTE_ARRAY: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV4_ADDR)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(DataTypeFormatConversion.inetAddressToByteArray(sTlvValue));
							
				
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_BYTE_ARRAY_IPV4_ADDR: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV6_ADDR)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(DataTypeFormatConversion.inetAddressToByteArray(sTlvValue));
				
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_BYTE_ARRAY_IPV6_ADDR: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(DataTypeFormatConversion.ipv4TransportAddressToByteArray(sTlvValue));
												
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(DataTypeFormatConversion.ipv6TransportAddressToByteArray(sTlvValue));
												
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_INET_ADDR)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
					
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray(sTlvValue));

				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_TRANSPORT_ADDR_INET_ADDR: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_MAC_ADDRESS)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(DataTypeFormatConversion.macAddressFormatToByteArray(sTlvValue));
				
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_MAC_ADDRESS: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert Integer to Byte Array
				HexString hsTlvValue = new HexString(DataTypeFormatConversion.hexStringToByte(sTlvValue));
				
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsTlvValue.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_BYTE: " + tbLocalTlvBuilder.toString());	
				
			} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_OID_ASN1_OBJECT_6)) {
				
				//Get String Value HEX String
				String sTlvValue = joTlvDictionary.getString(Dictionary.VALUE);
				
				//Convert OID to Byte Array
				HexString hsOIDObj6 = new HexString(DataTypeFormatConversion.oidObj6ToByteArray(sTlvValue));
				
				//Add Byte Array TLV
				try {
					tbLocalTlvBuilder.add(iType,hsOIDObj6.toByteArray());
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) 
					logger.debug("TlvAssembler.buildTlvFromTlvDictionary() -> DATA_TYPE_OID_ASN1_OBJECT_6: " + tbLocalTlvBuilder.toString());	
				
			}			
			
		} 			
				
		return tbLocalTlvBuilder;	
	}
}
