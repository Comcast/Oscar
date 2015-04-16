package com.comcast.oscar.tlv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comcast.oscar.ber.BEROIDConversion;
import com.comcast.oscar.compiler.packetcablecompiler.PacketCableConstants;
import com.comcast.oscar.sql.queries.DictionarySQLConstants;
import com.comcast.oscar.sql.queries.DictionarySQLQueries;
import com.comcast.oscar.tlv.datatype.DataTypeDictionaryReference;
import com.comcast.oscar.tlv.datatype.DataTypeFormatConversion;
import com.comcast.oscar.tlv.datatype.DataTypeFormatException;
import com.comcast.oscar.tlv.dictionary.Dictionary;
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

public class TlvDisassemble extends DictionarySQLQueries {

	private TlvBuilder tbTlvBuffer = null;
	
	private TlvVariableBinding tvbTlvBuffer = null;
	
	private boolean debug = Boolean.FALSE;
	
	public final static String TLV_TYPE_DOCSIS = DictionarySQLConstants.DOCSIS_DICTIONARY_TABLE_NAME;	
	public final static String TLV_TYPE_PACKET_CABLE = DictionarySQLConstants.PACKET_CABLE__DICTIONARY_TABLE_NAME;

	/**
	 * 
	 * @param tbTlvBuffer
	
	 * @param sTlvType String
	 */
	public TlvDisassemble (TlvBuilder tbTlvBuffer, String sTlvType) {
		
		super();

		if (this.debug) 
			System.out.println("TlvDisassemble(t,s) DICTIONARY-TABLE-NAME: " + sTlvType);

		//Update local pointer
		this.tbTlvBuffer = tbTlvBuffer;

		if (this.debug) 
			System.out.println("TlvDisassemble(tb,s) TLV Buffer: " + tbTlvBuffer.toStringSeperation(":"));
	
		//Check the first byte to determine if it is a DOCSIS File or PacketCable File
		super.updateDictionaryTablename(sTlvType);
	}	

	/**
	 * 
	 * @param tvbTlvBuffer
	
	 * @param sTlvType String
	 */
	public TlvDisassemble (TlvVariableBinding tvbTlvBuffer, String sTlvType) {
		
		super();

		if (this.debug) 
			System.out.println("TlvDisassemble(tvb,s) DICTIONARY-TABLE-NAME: " + sTlvType);

		//Update local pointer
		this.tvbTlvBuffer = tvbTlvBuffer;

		if (this.debug) 
			System.out.println("TlvDisassemble(tvb,s) TLV Buffer: " + tvbTlvBuffer.toString());

		
		//Check the first byte to determine if it is a DOCSIS File or PacketCable File
		super.updateDictionaryTablename(sTlvType);
	}
	
	/**
	 * 
	 * @param fTlvBuffer
	
	 * @param sTlvType String
	 */
	public TlvDisassemble (File fTlvBuffer , String sTlvType) {
		
		super();
		
		if (debug) 
			System.out.println("TlvDisassemble(f,s) DICTIONARY-TABLE-NAME: " + sTlvType);
		
        byte[] bTlvBuffer = new byte[(int) fTlvBuffer.length()];
                
        FileInputStream fisTlvBuffer;
        
		try {
			fisTlvBuffer = new FileInputStream(fTlvBuffer);
			fisTlvBuffer.read(bTlvBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.tbTlvBuffer = new TlvBuilder();

		if (this.debug) 
			System.out.println("TlvDisassemble(f,s) TLV Buffer: " + tbTlvBuffer.toStringSeperation(":"));
		
		try {
			tbTlvBuffer.add(new HexString(bTlvBuffer));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		if (debug) System.out.println("TlvDisassemble(f,s) TLV-BUFFER: " + tbTlvBuffer.toStringSeperation(":"));
		
		//Check the first byte to determine if it is a DOCSIS File or PacketCable File
		super.updateDictionaryTablename(sTlvType);
		
	}
	
	/**
	 * 
	
	 * @return JSONArray
	 */
	public JSONArray getTlvDictionary() {

		boolean localDebug = Boolean.FALSE;
		
		//Store the entire return
		JSONArray jaTlvDictionary = new JSONArray();

		//Store JSON Object temp for TLV Value insertion
		JSONObject joTlvDictionary;

		//ByteArrayOutputStream Temp
		ByteArrayOutputStream boasTlvBufferTemp = null;
		
		List<Integer> liTlvBuffer = null;
		
		//Check which Type of TLV Buffer we are using
		if (tbTlvBuffer != null) {
			
			if (debug|localDebug) 
				System.out.println("TlvDisassemble.getTlvDictionary() - Using TlvBuilder Class");
			
			liTlvBuffer = tbTlvBuffer.getTopLevelTlvList(getTopLevelByteLength());
		
		} else if (tvbTlvBuffer != null) {
			
			if (debug|localDebug) 
				System.out.println("TlvDisassemble.getTlvDictionary() - Using TlvVariableBinding Class");
			
			liTlvBuffer = tvbTlvBuffer.getTopLevelTlvList();
		}

		if (debug|localDebug) 
			System.out.println("TlvDisassemble.getTlvDictionary() - TLV-LIST: " + liTlvBuffer);
	
		//Get Major TLV from tbTlvBuffer via Map Type -> Byte Length
		for (Integer iType : liTlvBuffer) {
			
			//mgarcia - Added 140103 - Need a new Buffer for each Type Look up
			boasTlvBufferTemp = new ByteArrayOutputStream();
			
			if (debug|localDebug) 
				System.out.println("TlvDisassemble.getTlvDictionary() MAJOR-TLV-TYPE: " + iType);

			//Get TLV Dictionary via TLV Type
			joTlvDictionary = super.getTlvDictionary(iType);
			
			if (debug|localDebug)
				System.out.println("TlvDisassemble.getTlvDictionary.joTlvDictionary("+ iType +"): " + joTlvDictionary);
			
			if (joTlvDictionary == null) {
				if (debug|localDebug) System.out.println("TlvDisassemble.getTlvDictionary() -> INVALID-TLV: " + iType);
				continue;
			}

			//Create Map to hold TYPE to Byte Length
			Map<Integer,Integer> miiTypeByteLength = new HashMap<Integer,Integer>();
			
			//need to account for VariableNumByteLength TLV's			
			if (tvbTlvBuffer != null) {
				
				miiTypeByteLength.put(iType, tvbTlvBuffer.getMapTypeToByteLength().get(iType));

				//fetch Major TLV and store in BASO
				try {
					boasTlvBufferTemp.write(TlvBuilder.fetchTlv(new ArrayList<Integer>(Arrays.asList(iType)), 	//Contains Type
																miiTypeByteLength , 							//Contains Type -> ByteLength
																tvbTlvBuffer.toByteArray()));					//Contains TLV ByteArray
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					
					//Update ByteLength for this TLV
					try {
						joTlvDictionary.put(Dictionary.BYTE_LENGTH, miiTypeByteLength.get(iType));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					//Load JSON Object
					jaTlvDictionary.put(loadTlvValuesIntoTlvDictionary (tvbTlvBuffer.toByteArray() , joTlvDictionary));
					
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) { 
					System.out.println("TlvDisassemble.getTlvDictionary() VAR-BIND-TLV-BUFFER: " + tvbTlvBuffer);
					System.out.println("TlvDisassemble.getTlvDictionary() joTlvDictionary: " + joTlvDictionary);
				}
				
			} else if (tbTlvBuffer != null) {
				
				//Map to hold TYPE to Byte Length
				try {
					miiTypeByteLength.put(joTlvDictionary.getInt(Dictionary.TYPE), joTlvDictionary.getInt(Dictionary.BYTE_LENGTH));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
	
				if (debug|localDebug) { 
					System.out.println("TlvDisassemble.getTlvDictionary() MapTypeToByteLen: " + miiTypeByteLength);
				}
				
				//fetch Major TLV and store in BASO
				try {
					boasTlvBufferTemp.write(TlvBuilder.fetchTlv(new ArrayList<Integer>(Arrays.asList(iType)), 	//Contains Type
																miiTypeByteLength , 							//Contains Type -> ByteLength
																tbTlvBuffer.toByteArray()));					//Contains TLV ByteArray
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (debug|localDebug) {
					System.out.println("TlvDisassemble.getTlvDictionary() - BEFORE-LOAD-TLV - TLV-BUFFER-LENGTH: " + boasTlvBufferTemp.size());
					System.out.println("TlvDisassemble.getTlvDictionary() - BEFORE-LOAD-TLV - TLV-BUFFER: " + new HexString(boasTlvBufferTemp.toByteArray()).toString(":"));
					System.out.println("TlvDisassemble.getTlvDictionary() - BEFORE-LOAD-TLV - joTlvDictionary: " + joTlvDictionary);
				}
				
				try {
					jaTlvDictionary.put(loadTlvValuesIntoTlvDictionary (boasTlvBufferTemp.toByteArray() , joTlvDictionary));
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				if (debug|localDebug) { 
					System.out.println("TlvDisassemble.getTlvDictionary() - AFTER-LOAD-TLV - TLV-BUFFER-LENGTH: " + new HexString(boasTlvBufferTemp.toByteArray()).toString(":"));
					System.out.println("TlvDisassemble.getTlvDictionary() - AFTER-LOAD-TLV - joTlvDictionary: " + joTlvDictionary);
				}
			}
					
		}

		return jaTlvDictionary;
	}

	/**
	 * 
	
	 * @return List<byte[]>
	 */
	public List<byte[]> toListByteArray () {
		return tbTlvBuffer.sortByTopLevelTlv();
	}
	
	/**
	 * 
	 * @param iTlvType
	
	 * @return JSONArray
	 */
	public JSONArray getTopLevelTLVJSON (int iTlvType) {
		
		 boolean localDebug = Boolean.FALSE;
		
		JSONArray jaTLV = new JSONArray();
		
		JSONArray jaTlvDictionary = getTlvDictionary();
		
		JSONObject joTlvDictionary = null;
		
		for (int iIndex = 0 ; iIndex < jaTlvDictionary.length() ; iIndex++) {
			
			try {
				joTlvDictionary = jaTlvDictionary.getJSONObject(iIndex);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (debug|localDebug) {
				System.out.println("TlvDisassemble.getTopLevelTLVJSON() - JSONObject: " + joTlvDictionary);
			}
			
			try {
				
				if (joTlvDictionary.getInt(Dictionary.TYPE) == iTlvType) {
					jaTLV.put(joTlvDictionary);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
		return jaTLV;
	}
	
	/**
	 * 
	 * @param fConfiguraitonFile
	
	 * @return Return the Type of File to determine DataBase Selection */
	public static String GetConfiguraitonFileType(File fConfiguraitonFile) {
	
		//Convert to Byte Array
		byte[] bTLV = HexString.fileToByteArray(fConfiguraitonFile);

		if (bTLV[0] == PacketCableConstants.FILE_MARKER) {
			return TLV_TYPE_PACKET_CABLE;		
		} else {
			return TLV_TYPE_DOCSIS;			
		}
		
	}
	
	/**
	 * 
	 * @param bTlvBuffer
	 * @param jaTlvDictionary
	 */
	private void loadTlvValuesIntoTlvDictionary (byte[] bTlvBuffer , JSONArray jaTlvDictionary) {

		boolean localDebug = Boolean.FALSE;
		
		//Cycle thru JSON Array and inspect each JSON Object
		for (int iJsonArrayIndex = 0 ; iJsonArrayIndex < jaTlvDictionary.length() ; iJsonArrayIndex++ ) {

			if (debug|localDebug) 
				System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,ja) +-----INDEX: " + iJsonArrayIndex + "-----+");

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
						System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,ja) SUB-TYPE-ARRAY-TRUE: " + joTlvDictionary);

					//If this is the start of the array, I need only advance 2 bytes to the next Type
					loadTlvValuesIntoTlvDictionary (bTlvBuffer , joTlvDictionary.getJSONArray(Dictionary.SUBTYPE_ARRAY)); 
				
				} else {

					if (debug) 
						System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,ja) SUB-TYPE-ARRAY-FALSE: " + joTlvDictionary);

					//If this is the start of the array, I need only advance 2 bytes to the next Type
					try {
						loadTlvValuesIntoTlvDictionary (bTlvBuffer , joTlvDictionary);
					} catch (TlvException e) {
						e.printStackTrace();
					}						
				}


			} catch (JSONException e) {
				e.printStackTrace();
			}			

		}
	}

	/**
	 * 
	 * @param bTlvBuffer
	 * @param joTlvDictionary
	
	
	 * @return JSONObject
	 * @throws TlvException */
	private JSONObject loadTlvValuesIntoTlvDictionary (byte[] bTlvBuffer , JSONObject joTlvDictionary) throws TlvException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("+=================================loadTlvValuesIntoTlvDictionary============================================+");
			System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) - TlvBufferLength: " + bTlvBuffer.length);
			System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) - ByteArray: " + new HexString(bTlvBuffer).toString(":"));
		}
		
		try {
			
			if (joTlvDictionary.getBoolean(Dictionary.ARE_SUBTYPES)) {
				
				if (debug|localDebug) 
					System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo).joTlvDictionary.SubTypesFound");
				
				loadTlvValuesIntoTlvDictionary (bTlvBuffer , new JSONArray().put(joTlvDictionary));	
			
			} else {

				if (debug|localDebug) 
					System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo).joTlvDictionary.No-SubTypesFound");
				
				//Get ParentTypeList Array
				byte [] bParentChildTlvEncodeList = toParentTypeListByteArray(joTlvDictionary.getJSONArray(Dictionary.PARENT_TYPE_LIST));
				
				HexString hsPCTEL = new HexString(bParentChildTlvEncodeList);

				//Get Specific TLV if available			
				byte [] bPCTEPointer = TlvVariableBinding.findTLVIndex(	bTlvBuffer, 
																		bParentChildTlvEncodeList , 
																		joTlvDictionary.getInt("ByteLength"));						
				
				HexString hsPCTEPointer = new HexString(bPCTEPointer);
				
				if (debug|localDebug) 
					System.out.println(	"TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) " +
										" -> TlvBufferLength: " + bTlvBuffer.length +
										" -> bParentChildTlvEncodeList: " + hsPCTEL +
										" -> HexPointerIndex: " + hsPCTEPointer + 
										" -> HexPointerIndexLength: " + bPCTEPointer.length);
				
				if (bPCTEPointer.length != 0) {
					
					//Get Value
					byte [] bValue = TlvBuilder.getTlvValue(bTlvBuffer, bPCTEPointer[0], joTlvDictionary.getInt(Dictionary.BYTE_LENGTH));

					if (debug|localDebug) {
						System.out.println("+-------------------------------------------------------------------------------------------------+");
						System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) BYTE-ARRAY-VALUE-LENGTH: " + bValue.length);
						System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) TLV_NAME: " + joTlvDictionary.getString(Dictionary.TLV_NAME));
						System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) BYTE_LENGTH: " + joTlvDictionary.getString(Dictionary.BYTE_LENGTH));					
					}
					
					//Determine the Data Type of Value
					String sDataType = joTlvDictionary.getString(Dictionary.DATA_TYPE);

					//Convert Value to Proper Data Type
					if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_INTEGER)) {
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) HEX-INT: " + new HexString(bValue).toHexStringList());
						
						//Get Value
						int iTlvValue = new HexString(bValue).toInteger();

						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, iTlvValue);

						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) TLV-VALUE: " + iTlvValue);

					} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_OID)) {
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) HEX-OID-PRE: " + new HexString(bValue).toHexStringList());
						
						//Need to see if this is a BER byte array
						//bValue = BERService.cleanBEROIDPrefix(bValue);
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) HEX-OID-PST: " + new HexString(bValue).toHexStringList());
						
						//Get Value
						BEROIDConversion bocOID = new BEROIDConversion(bValue); 

						JSONArray jaOID = new JSONArray();
						
						jaOID.put(bocOID.toMap());
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, jaOID);
					
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) - DATA_TYPE_OID - joTlvDictionary: " + joTlvDictionary);
					
					} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY)) {
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) HEX-BYTE-ARRAY: " + new HexString(bValue).toHexStringList());
						
						//Get Value
						String sTlvValue = new HexString(bValue).toString(":");

						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
						
					} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING_NULL_TERMINATED)) {
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) HEX-STRING-NULL-TERMINATED: " + new HexString(bValue).toHexStringList());
						
						//Get Value
						String sTlvValueASCII = new HexString(HexString.stripNullTerminatedString(bValue)).toASCII();

						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValueASCII);
								
					} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING)) {
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) HEX-STRING: " + new HexString(bValue).toHexStringList());
						
						//Get Value
						String sTlvValueASCII = new HexString(bValue).toASCII();

						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValueASCII);
												
					} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_MULTI_TLV_BYTE_ARRAY)) {
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) HEX-MULTI-TLV-BYTE-ARRAY: " + new HexString(bValue).toString(":"));
						
						//Get Value
						String sTlvValue = new HexString(bValue).toString(":");

						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
												
					} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR)) {
						
						String sTlvValue = null;
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR: " + new HexString(bValue).toString(":"));
						
						try {
							sTlvValue = DataTypeFormatConversion.byteArrayToIPv4TransportAddress(bValue);
						} catch (DataTypeFormatException e) {
							e.printStackTrace();
						}
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
												
					} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR)) {
						
						String sTlvValue = null;
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR: " + new HexString(bValue).toString(":"));
						
						try {
							sTlvValue = DataTypeFormatConversion.byteArrayToIPv6TransportAddress(bValue);
						} catch (DataTypeFormatException e) {
							e.printStackTrace();
						}
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
												
					} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_INET_ADDR)) {
						
						String sTlvValue = null;
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_TRANSPORT_ADDR_INET_ADDR: " + new HexString(bValue).toString(":"));
						
						try {
							sTlvValue = DataTypeFormatConversion.byteArrayToInetTransportAddress(bValue);
						} catch (DataTypeFormatException e) {
							e.printStackTrace();
						}
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
												
					} else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_DOUBLE_BYTE_ARRAY)) {
						
						String sTlvValue = null;
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_DOUBLE_BYTE_ARRAY: " + new HexString(bValue).toString(":"));
						
						try {
							sTlvValue = DataTypeFormatConversion.doubleByteArray(bValue);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
												
					}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING_BITS)) {
						
						StringBuilder sbTlvValue = null;
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_STRING_BITS: " + new HexString(bValue).toString(":"));
						
						sbTlvValue = DataTypeFormatConversion.byteArrayBinaryBitMaskToString(bValue,8);
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sbTlvValue.toString());
												
					}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_MAC_ADDRESS)) {
						
						String sTlvValue = null;
						
						if (debug|localDebug) { 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_MAC_ADDRESS: " + new HexString(bValue).toString(":"));
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) TLV-NAME: " + joTlvDictionary.getString(Dictionary.TLV_NAME));
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) TLV-NAME: " + joTlvDictionary.getString(Dictionary.PARENT_TYPE_LIST));
						}
						
						sTlvValue = DataTypeFormatConversion.byteArrayToMacAddressFormat(bValue);
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
												
					}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV4_ADDR)) {
						
						String sTlvValue = null;
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_BYTE_ARRAY_IPV4_ADDR: " + new HexString(bValue).toString(":"));
						
						sTlvValue = DataTypeFormatConversion.inetAddressToString(bValue);
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
												
					}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV6_ADDR)) {
						
						String sTlvValue = null;
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_BYTE_ARRAY_IPV6_ADDR: " + new HexString(bValue).toString(":"));
						
						sTlvValue = DataTypeFormatConversion.inetAddressToString(bValue);
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
												
					}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE)) {
						
						String sTlvValue = null;
						
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_BYTE: " + new HexString(bValue).toString(":"));
						
						sTlvValue = new HexString(bValue).toString();
						
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
												
					}  else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_OID_ASN1_OBJECT_6)) {
												
						if (debug|localDebug) 
							System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) DATA_TYPE_BYTE: " + new HexString(bValue).toString(":"));
						
						String sObjectOnlyHex = "30" + HexString.toHexString(bValue.length) + new HexString(bValue).toString();
						
						HexString hsHexOID = new HexString(HexString.toByteArray(sObjectOnlyHex));
						
					    BEROIDConversion bocOidAsnObj5 = new BEROIDConversion(hsHexOID.toByteArray());
					    
					    if (debug|true) 
					    	System.out.println("Hex -> OID-DOT: " + bocOidAsnObj5.getOidDotNotaion());
					    
						//Insert Value into JSON Object
						joTlvDictionary.put(Dictionary.VALUE,  bocOidAsnObj5.getOidDotNotaion());
												
					}

				}
				
				if (debug|localDebug) 
					System.out.println("TlvDisassemble.loadTlvValuesIntoTlvDictionary(b,jo) " + joTlvDictionary);
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return joTlvDictionary;
	}
	
	/**
	 * 
	 * @param jaParentTypeList
	
	 * @return byte[]
	 */
	private byte[] toParentTypeListByteArray (JSONArray jaParentTypeList) {

		ByteArrayOutputStream  baosParentTypeList = new ByteArrayOutputStream();

		int iParentTypeListIndex = 0;

		try {

			//Check to see the first entry is a -1, this indicate the start of the Major TLV
			//Then increment
			if (jaParentTypeList.getInt(iParentTypeListIndex) == -1)

				//Cycle Thru all the elements and add them to the byte array output stream
				while(++iParentTypeListIndex < jaParentTypeList.length())

					//Add to output stream
					baosParentTypeList.write(jaParentTypeList.getInt(iParentTypeListIndex));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return baosParentTypeList.toByteArray();
	}
}
