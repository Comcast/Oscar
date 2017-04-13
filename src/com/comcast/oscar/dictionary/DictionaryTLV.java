package com.comcast.oscar.dictionary;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comcast.oscar.ber.OIDToJSONArray;
import com.comcast.oscar.datatype.DataTypeDictionaryReference;
import com.comcast.oscar.parser.tlvParser;

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

public class DictionaryTLV implements Dictionary {

	static boolean debug = Boolean.FALSE;

	/**
	 * Select Static DOCSIS or PACKET_CABLE
	 * 
	 * @param iDictionaryTlvType
	 */
	DictionaryTLV(int iDictionaryTlvType) {
		//Load Dictionary
	}

	/**
	 * 
	 * @param iType Integer
	 * @see com.comcast.oscar.dictionary.Dictionary#getMajorTlvDefinition(Integer)
	 */

	public void getMajorTlvDefinition(Integer iType) {
	}

	/**
	 * 
	 * @param iType Integer
	 * @return JSONObject
	 * @see com.comcast.oscar.dictionary.Dictionary#getTlvDefinition(Integer)
	 */

	public JSONObject getTlvDefinition(Integer iType) {
		return null;
	}

	/**
	 * 
	 * @param iCableLabsConfigType int
	 * @return JSONArray
	 * @see com.comcast.oscar.dictionary.Dictionary#getAllTlvDefinition(int)
	 */

	public JSONArray getAllTlvDefinition(int iCableLabsConfigType) {
		return null;
	}

	/**
	 * 
	 * @param sTlvDotNotation Example 24.1.3
	 * @param dsq DictionarySQLQueries
	
	 * @return ArrayDeque<String> of TLV Names found in Dictionary */
	public static ArrayDeque<String> getTypeHierarchyStack (String sTlvDotNotation, DictionarySQLQueries dsq) {

		boolean localDebug = Boolean.FALSE;
		
		ArrayDeque<String> adTypeHierarchyStack = new ArrayDeque<String>();

		List<String> lsTlvDotNotation = new ArrayList<String>();

		lsTlvDotNotation = Arrays.asList(sTlvDotNotation.split("\\."));

		if (debug|localDebug)
			System.out.println("ConfigrationFileExport.getTlvDefintion(): " + lsTlvDotNotation.toString());

		//Get TLV Dictionary for the Top Level
		JSONObject joTlvDictionary = dsq.getTlvDefinition(Integer.decode(lsTlvDotNotation.get(0)));

		//Search for TLV Definition
		if (lsTlvDotNotation.size() == 1) {

			try {
				adTypeHierarchyStack.addFirst(joTlvDictionary.getString(Dictionary.TLV_NAME));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (lsTlvDotNotation.size() >= 1) {

			try {
				adTypeHierarchyStack.addFirst(joTlvDictionary.getString(Dictionary.TLV_NAME));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			int iRecursiveSearch = 0;

			while (iRecursiveSearch < lsTlvDotNotation.size()) {

				if (debug|localDebug)
					System.out.println("ConfigrationFileExport.getTlvDefintion(): WHILE-LOOP");

				try {

					if (joTlvDictionary.getString(Dictionary.TYPE).equals(lsTlvDotNotation.get(iRecursiveSearch))) {

						if (joTlvDictionary.getBoolean(Dictionary.ARE_SUBTYPES)) {

							try {
								JSONArray jaTlvDictionary = joTlvDictionary.getJSONArray(Dictionary.SUBTYPE_ARRAY);

								for (int iIndex = 0 ; iIndex < jaTlvDictionary.length() ; iIndex++) {

									if (debug|localDebug)
										System.out.println("ConfigrationFileExport.getTlvDefintion(): FOR-LOOP");

									JSONObject joTlvDictionaryTemp = jaTlvDictionary.getJSONObject(iIndex);

									if (joTlvDictionaryTemp.getString(Dictionary.TYPE).equals(lsTlvDotNotation.get(iRecursiveSearch+1))) {
										joTlvDictionary = joTlvDictionaryTemp;
										iRecursiveSearch++;
										
										try {
											adTypeHierarchyStack.addFirst(joTlvDictionary.getString(Dictionary.TLV_NAME));
										} catch (JSONException e) {
											e.printStackTrace();
										}
										
										break;
									}
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							
							iRecursiveSearch++;
						}
					}

				} catch (JSONException e1) {
					e1.printStackTrace();
				}

			}

		}

		return adTypeHierarchyStack;

	}

	/**
	 * 
	 * @param joDictionary
	 * @param iValue
	 */
	public static void updateDictionaryValue (JSONObject joDictionary , int iValue) {
		try {
			joDictionary.put(Dictionary.VALUE, iValue);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param joDictionary
	 * @param sValue
	 */
	public static void updateDictionaryValue (JSONObject joDictionary , String sValue) {
		try {
			joDictionary.put(Dictionary.VALUE, sValue);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 
	 * @param joDictionary
	
	 * @param boolValue boolean
	 */
	public static void updateDictionaryValue (JSONObject joDictionary , boolean boolValue) {
		try {
			joDictionary.put(Dictionary.VALUE, boolValue);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 
	 * @param jaDictionary
	
	 * @return Map<String,Integer>
	 */
	public static Map<String,Integer> getTopLevelTypeNameToTypeViaDictionary (JSONArray jaDictionary) {

		boolean localDebug = Boolean.FALSE;

		Map<String,Integer> msiTypeNameToType = new HashMap<String,Integer>();

		if (debug|localDebug) 
			System.out.println("DictionaryTLV.getAllTopLevelTypeNameToTypeViaDictionary(): JA-DICT: -> " + jaDictionary);

		for (int iJsonArrayIndex = 0 ; iJsonArrayIndex < jaDictionary.length() ; iJsonArrayIndex++ ) {

			//Get Object
			JSONObject joTlvDictionary = new JSONObject();
			try {
				joTlvDictionary = jaDictionary.getJSONObject(iJsonArrayIndex);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				msiTypeNameToType.put(joTlvDictionary.getString(Dictionary.TLV_NAME), joTlvDictionary.getInt(Dictionary.TYPE));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (debug|localDebug) 
			System.out.println("DictionaryTLV.getAllTopLevelTypeNameToTypeViaDictionary(): Map ->  " + msiTypeNameToType.toString());


		return msiTypeNameToType;
	}

	/**
	 * 
	 * @param adqsTypeHierarchyStack - TLV Name
	 * @param joDictionary
	 * @param sValue
	 */
	public static void updateDictionaryValue (final ArrayDeque<String> adqsTypeHierarchyStack , JSONObject joDictionary , final String sValue) {

		boolean localDebug = Boolean.FALSE;

		//Create a local copy
		ArrayDeque<String> adqsTypeHierarchyStackLocal = adqsTypeHierarchyStack.clone();

		if (debug|localDebug) {
			System.out.println("DictionaryTLV.updateDictionaryValue(ad,jo,s): adqsTypeHierarchyStackLocal: " + adqsTypeHierarchyStackLocal);		
			System.out.println("DictionaryTLV.updateDictionaryValue(ad,jo,s): sValue: " + sValue);	
		}

		try {

			if (joDictionary.get(Dictionary.DATA_TYPE) == DataTypeDictionaryReference.DATA_TYPE_OID) {

				if (debug|localDebug) 
					System.out.println("DictionaryTLV.updateDictionaryValue(ad,jo,s): DATA_TYPE_OID: " + sValue);		

			} else if (joDictionary.getBoolean(Dictionary.ARE_SUBTYPES)) {

				//Remove Starting TlvName
				adqsTypeHierarchyStackLocal.removeLast();

				searchAndUpdateDictionaryValue (adqsTypeHierarchyStackLocal , joDictionary.getJSONArray(Dictionary.SUBTYPE_ARRAY) , sValue);
			} else {
				searchAndUpdateDictionaryValue (adqsTypeHierarchyStackLocal , joDictionary , sValue);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}


	}

	/**
	 * 
	 * @param adqsTypeHierarchyStack
	 * @param joDictionary
	 * @param ctx
	
	 * @return JSONObject
	 */
	public static JSONObject updateDictionaryValue (final ArrayDeque<String> adqsTypeHierarchyStack , JSONObject joDictionary , tlvParser.SnmpContext ctx) {

		boolean localDebug = Boolean.FALSE;

		//Create a local copy
		ArrayDeque<String> adqsTypeHierarchyStackLocal = adqsTypeHierarchyStack.clone();

		if (debug|localDebug) {
			System.out.println("DictionaryTLV.updateDictionaryValue(ad,jo,ctx): adqsTypeHierarchyStackLocal: " + adqsTypeHierarchyStackLocal);		
			System.out.println("DictionaryTLV.updateDictionaryValue(ad,jo,ctx): OID ------> " + ctx.oid().getText());
			System.out.println("DictionaryTLV.updateDictionaryValue(ad,jo,ctx): DATA-TYPE-> " + ctx.dataType().getText());
			System.out.println("DictionaryTLV.updateDictionaryValue(ad,jo,ctx): VALUE-----> " + ctx.value().getText());
		}

		try {

			joDictionary.put(Dictionary.VALUE, new OIDToJSONArray(ctx).toJSONArray());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (debug|localDebug) {
			System.out.println("DictionaryTLV.updateDictionaryValue(ad,jo,s)" + joDictionary);
		}

		return joDictionary;
	}

	/**
	 * 
	 * @param adqsTypeHierarchyStack
	
	 * @param sValue
	
	 * @param jaTlvDictionary JSONArray
	 */
	private static void searchAndUpdateDictionaryValue (ArrayDeque<String> adqsTypeHierarchyStack , JSONArray jaTlvDictionary , final String sValue) {

		boolean localDebug = Boolean.FALSE;

		//Create a local copy
		ArrayDeque<String> adqsTypeHierarchyStackLocal = adqsTypeHierarchyStack.clone();

		//Cycle thru JSON Array and inspect each JSON Object
		for (int iJsonArrayIndex = 0 ; iJsonArrayIndex < jaTlvDictionary.length() ; iJsonArrayIndex++ ) {

			if (debug|localDebug) 
				System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,ja,s) +-----INDEX: " + iJsonArrayIndex + "-----+");

			JSONObject joTlvDictionary = null;

			try {
				joTlvDictionary = jaTlvDictionary.getJSONObject(iJsonArrayIndex);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			try {

				//Check to see if this object have SubTypes , if so go into SubType Array
				if ((joTlvDictionary.getBoolean(Dictionary.ARE_SUBTYPES)) && (joTlvDictionary.getString(Dictionary.TLV_NAME).equalsIgnoreCase(adqsTypeHierarchyStackLocal.peekLast()))) {

					if (debug|localDebug) { 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,ja,s) SUB-TYPE-ARRAY-TRUE: " + joTlvDictionary);
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,ja,s) -> TLV-SUB-NAME: -> " + adqsTypeHierarchyStackLocal.peekLast());
					}

					adqsTypeHierarchyStackLocal.removeLast();

					searchAndUpdateDictionaryValue (adqsTypeHierarchyStackLocal , joTlvDictionary.getJSONArray(Dictionary.SUBTYPE_ARRAY) , sValue); 

				} else if (adqsTypeHierarchyStackLocal.size() == 1) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,ja,s) SUB-TYPE-ARRAY-FALSE: " + joTlvDictionary);

					searchAndUpdateDictionaryValue (adqsTypeHierarchyStackLocal , joTlvDictionary , sValue);						
				}


			} catch (JSONException e) {
				e.printStackTrace();
			}			

		}		

	}

	/**
	 * 
	 * @param adqsTypeHierarchyStack
	
	 * @param sValue
	
	 * @param joTlvDictionary JSONObject
	 * @return JSONObject
	 */
	private static JSONObject searchAndUpdateDictionaryValue (ArrayDeque<String> adqsTypeHierarchyStack , JSONObject joTlvDictionary , final String sValue) {

		boolean localDebug = Boolean.FALSE;

		if (debug|localDebug) 
			System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s): Peek: " + adqsTypeHierarchyStack.peek());		

		try {
			if (joTlvDictionary.getString(Dictionary.TLV_NAME).equalsIgnoreCase(adqsTypeHierarchyStack.peek())) {

				String sDataType = joTlvDictionary.getString(Dictionary.DATA_TYPE);

				//Determine Data Type and insert with correct 
				//Convert Value to Proper Data Type
				if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_INTEGER)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s): " + sValue);

					//Get Value
					int iValue = Integer.parseInt(sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, iValue);

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_INTEGER: " + iValue);

				} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_OID)) {					
					/* DO NOTHING */
				} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_BYTE_ARRAY: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING_NULL_TERMINATED)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_STRING_NULL_TERMINATED: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_STRING: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_MULTI_TLV_BYTE_ARRAY)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_MULTI_TLV_BYTE_ARRAY: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_INET_ADDR)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_TRANSPORT_ADDR_INET_ADDR: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_DOUBLE_BYTE_ARRAY)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_DOUBLE_BYTE_ARRAY: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING_BITS)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_STRING_BITS: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_MAC_ADDRESS)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_MAC_ADDRESS: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV4_ADDR)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_BYTE_ARRAY_IPV4_ADDR: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV6_ADDR)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_BYTE_ARRAY_IPV6_ADDR: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_BYTE: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_OID_ASN1_OBJECT_6)) {

					if (debug|localDebug) 
						System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s) -> DATA_TYPE_BYTE: " + sValue);

					//Insert Value into JSON Object
					joTlvDictionary.put(Dictionary.VALUE, sValue);

				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (debug|localDebug) 
			System.out.println("DictionaryTLV.searchAndUpdateDictionaryValue(ad,jo,s): RETURN: " + joTlvDictionary);

		return joTlvDictionary;

	}	

}
