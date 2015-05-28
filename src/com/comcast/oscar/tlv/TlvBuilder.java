package com.comcast.oscar.tlv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.snmp4j.smi.OID;

import com.comcast.oscar.ber.BERService;
import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
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

public class TlvBuilder implements TlvBuild {

	private final static int ZERO_BYTE_LENGTH 	= 0;
	
	private final static int ONE_BYTE_LENGTH	= 1;
		
	private static boolean debug = Boolean.FALSE;
	
	protected List<String> lsTlvBuffer = new ArrayList<String>();	
	protected byte[] baTlvBuffer = null;
	protected List<byte[]> lbTlvBuilder = new ArrayList<byte[]>();
	protected Map<Integer,Integer> miiTlvTypeTpByteLength = new HashMap<Integer,Integer>();
	
	public final static byte 	TLV_LENGTH_POS_OFFSET 		= 0x01;	
	public final static byte 	TLV_TYPE_OVERHEAD			= 0x01;
	public final static byte 	TLV_TYPE_LENGTH_OVERHEAD 	= (TLV_LENGTH_POS_OFFSET + TLV_TYPE_OVERHEAD);
	public final static int		MAX_TLV_LENGTH				= 254;
	public final static int 	END_OF_BYTE_ARRAY_INDEX 	= -1;
	public final static byte	TLV_ONE_BYTE_LENGTH			= 0x01;
	
 	/**
 	 * Main Constructor  TlvBuilder creates Type Length Value based on the Basic Encoding Rules for 1 Byte length or 255 Byte Value
 	 * 
 	 * 		Type: 3 , Length: 1 , Value: 1 -> 03:01:01
	 *		Type: 4 , Length: 3 , Value: 01:01:01 or 0x15 or DEC:21 -> 41:03:01:01:01
 	 */
	public TlvBuilder() {
		super();
	}

	/**
	 * 
	 * @param cfi - ConfigurationFileImport */
	public void add (ConfigurationFileImport cfi) {
				
		try {
			add(new HexString(cfi.toByteArray()));
		} catch (TlvException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * This method assumes that each Type is of 1 byte Length
	 * 
	 * @param hsObject Add HexString object, will throw exception if Bytes are not of a TLV Construct
	 * @throws TlvException */
	public void add (HexString hsObject) throws TlvException {
		
		if (debug)
			System.out.println("TlvBuilder.add(hs) " + hsObject.toString(":"));
		
		if (hsObject == null) {
			throw new NullPointerException("HexString.add(hs): hsObject is a NULL");
		}
		
		//Make sure that TLV Variable is of proper Length
		if (isTLVConstruct(hsObject)) {
			lsTlvBuffer.add(hsObject.toString());
			bTlvBuilder.add(hsObject.toByteArray());
		} else {
			lsTlvBuffer.add(hsObject.toString());
			bTlvBuilder.add(hsObject.toByteArray());
			throw new TlvException ("HexString Object not in a TLV Format\n" + hsObject.toString(":"));
		}
		
	} 
		
	/**
	 * 
	 * @param iTlvType Type integer 0>Type<256
	 * @param iValue 
	 * @throws TlvException */
	public void add (int iTlvType, Integer iValue) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug)
			System.out.println("TlvBuilder.add(i,i) " + "Type: " + iTlvType + " - Value: " + iValue);
		
		if (iTlvType < 0)
			throw new TlvException("TlvBuilder.add() Type Less than 0");

		if (iTlvType > 255)
			throw new TlvException("TlvBuilder.add() Type MUST be less than 256");
		
		String sValue = HexString.toHexString(iValue);

		lsTlvBuffer.add(HexString.toHexString(iTlvType) + 
						HexString.toHexString(HexString.getHexByteLength(sValue)) + 
						sValue);
		
		updateMapTypeToByteLength(iTlvType, ONE_BYTE_LENGTH);
		
	}

	/**
	 * 
	 * @param iTlvType Type integer 0>Type<256
	 * @param hsValue = HexString of the Value, length is automatically calculated
	 * @throws TlvException */
	public void add (int iTlvType, HexString hsValue) throws TlvException {

		if (debug)
			System.out.println("TlvBuilder.add(i,hs) " + "Type: " + iTlvType + " - Value: " + hsValue);
		
		if (iTlvType < 0)
			throw new TlvException("TlvBuilder.add() Type Must be greater than 0");
	
		if (iTlvType > 255)
			throw new TlvException("TlvBuilder.add() Type MUST be less than 256");
		
		String sValue = hsValue.hexCompressed();

		lsTlvBuffer.add(HexString.toHexString(iTlvType) + 
						HexString.toHexString(HexString.getHexByteLength(sValue)) + 
						sValue);
		
		updateMapTypeToByteLength(iTlvType, ONE_BYTE_LENGTH);
	}

	/**
	 * 
	 * @param tbObject TlvBuilder Object
	 */
	public void add (TlvBuilder tbObject) {

		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("TlvBuilder.add(tb) " + "TlvBuilder: " + tbObject + " - Size: " + tbObject.length());
			System.out.println("TlvBuilder.add(tb) " + "TlvBuilder: " + tbObject + " - Size: " + length());
			System.out.println("TlvBuilder.add(tb) " + "TlvBuilder.lsTlvBuffer: " + tbObject + " - Size: " + lsTlvBuffer.size());
		}
		
		lsTlvBuffer.add(tbObject.toString());
				
		if (debug|localDebug) {
			System.out.println("TlvBuilder.add(tb) " + "TlvBuilder: " + tbObject + " - Size: " + length());
			System.out.println("TlvBuilder.add(tb) - AFTER " + "TlvBuilder.lsTlvBuffer: " + tbObject + " - Size: " + lsTlvBuffer.size());
		}
		
		updateMapTypeToByteLength(tbObject);
		
		if (debug|localDebug) {
			System.out.println("TlvBuilder.add(tb) - AFTER " + "TlvBuilder.lsTlvBuffer: " + tbObject + " - Size: " + lsTlvBuffer.size());
		}
	}
	
	/**
	 * 
	 * @param booDirection true = ascending | false = descending 
	 * @param tb TlvBuilder Object containing TLVs
	 * @return TlvBuilder TlvBuilder Object containing  reordered TLVs
	 * @throws TlvException  */
	public static TlvBuilder reorderTLV (boolean booDirection , TlvBuilder tb) throws TlvException {
		
		List<Integer> liType = new ArrayList<Integer>();
		
		TlvBuilder tbReorder = new TlvBuilder();
		
		byte[] bReorderTLV = null;
		
		for (int iIndex = 1 ; iIndex < 255 ; iIndex++) {
			liType.add(iIndex);
		}
		
		if (booDirection) {
			bReorderTLV = fetchTlv (liType , tb.toByteArray());
		} else {
			bReorderTLV = fetchTlv (liType , tb.toByteArray());
		}
		
		tbReorder.add(new HexString(bReorderTLV));
		
		return tbReorder;
		
	}

	/**
	 * 
	 * @param iTlvType - Must be 0 - 255
	 * @param bValue byte array containing a value of 1 byte or greater
	 * @throws TlvException 
	 * @see com.comcast.oscar.tlv.TlvBuild#add(int, byte[]) */
	public void add (int iTlvType , byte[] bValue) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("+===============================================================================================+");
			System.out.println("TlvBuilder.add(i,b) " + "Type: " + iTlvType + " - Length: " + bValue.length  + " - Value: " + new HexString(bValue).toString(":"));
			//Thread.dumpStack();
		}
		
		if (iTlvType < 0)
			throw new TlvException("TlvBuilder.add() Type Less than 0");
		
		if (bValue ==  null)
			throw new TlvException("TlvBuilder.add() Byte Array is NULL");
		
		if (iTlvType > 255)
			throw new TlvException("TlvBuilder.add() Type MUST be less than 256");
				
		//Set the index to 0
		int iIndex = 0;
		
		//if the length is more than MAX_TLV_LENGTH=254, need to segment the TLV
		while ((bValue.length - iIndex) > MAX_TLV_LENGTH) {
			
			//Create BAOS
			ByteArrayOutputStream baosTopLevelTlvList = new ByteArrayOutputStream();
			
			//Write to BAOS
			baosTopLevelTlvList.write(bValue, iIndex, MAX_TLV_LENGTH);
			
			//Add segmented TLV
			add(iTlvType, new HexString(baosTopLevelTlvList.toByteArray()));
			
			//Advance pointer to the next 255 position
			iIndex += MAX_TLV_LENGTH;
			
			if (debug|localDebug)
				System.out.println("TlvBuilder.add(i,b) - Type: " + iTlvType + " - Hex: " + new HexString(baosTopLevelTlvList.toByteArray()).toString(":"));
		}
		
		//if there are no more bytes, skip this part
		if ((bValue.length - iIndex) != 0) {
			//Create BAOS
			ByteArrayOutputStream baosTopLevelTlvList = new ByteArrayOutputStream();
			
			//Write to BAOS
			baosTopLevelTlvList.write(bValue, iIndex, (bValue.length - iIndex));
			
			//Add last segmented TLV
			add(iTlvType, new HexString(baosTopLevelTlvList.toByteArray()));
			
			if (debug|localDebug)
				System.out.println("TlvBuilder.add(i,b) - Single Entry - Type: " + iTlvType + " - Hex: " + new HexString(baosTopLevelTlvList.toByteArray()).toString(":"));
		}
		
		updateMapTypeToByteLength(iTlvType, ONE_BYTE_LENGTH);
				
	}

	/**
	 * 
	 * @param iTlvType Must be 0>Type<256
	 * @param bValue byte array containing a value of 1 byte or greater
	 * @param booleanStripExistingTlv = True = Strip Existing TLV ; False = Do Not Strip TLV
	 * @throws TlvException */
	public void add (int iTlvType , byte[] bValue , boolean booleanStripExistingTlv) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("+===============================================================================================+");
			System.out.println("TlvBuilder.add(i,ba,bool) " + "Type: " + iTlvType + " - Length: " + bValue.length  + " - Value: " + new HexString(bValue).toString(":"));
			Thread.dumpStack();
		}
		
		//Strip Incoming TLV
		if (booleanStripExistingTlv) {
			
			//Without Striped TLV
			byte[] baNewTLV = stripTlv(iTlvType, toByteArray());
			
			//Clear class Buffer
			clear();
			
			//ReAdd Buffer
			add(new HexString(baNewTLV));
		}
		
		//Add New TLV
		add(iTlvType,bValue);
				
	}
	
	/**
	 * 
	 * @param iSnmpTlvType Must be 0>Type<256
	 * @param oObjectID	   OID object from SNMP4J	
	 * @param bBerDataType BER Datatype
	 * @param lNumber	   Number of Long Type*/
	public void add (int iSnmpTlvType, OID oObjectID , byte bBerDataType , long lNumber) {	
		
		String sTlvObjectID = "";

		try {

			sTlvObjectID = BERService.setOIDEncoding(oObjectID.format(), bBerDataType, lNumber);

			lsTlvBuffer.add(HexString.toHexString(iSnmpTlvType) + 
					HexString.toHexString((sTlvObjectID.length()/2)) + 
					sTlvObjectID);

		} catch (Exception e) {
			e.printStackTrace();			
		}

		updateMapTypeToByteLength(iSnmpTlvType, ONE_BYTE_LENGTH);
	}

	/**
	 * 
	 * @param iSnmpTlvType Must be 0>Type<256
	 * @param oObjectID	   OID object from SNMP4J	
	 * @param bBerDataType BER Datatype
	 * @param sValue String value */
	public void add (int iSnmpTlvType, OID oObjectID , byte bBerDataType , String sValue) {	

		String sTlvObjectID = "";
		
		//This Will need to use a SNMP VarBinding
		//I m hard coding this the Number of Byte Length, need to go back and fix this
		if (sValue.length() > 254) {
			updateMapTypeToByteLength(iSnmpTlvType , 2);
		}
		
		try {

			sTlvObjectID = BERService.setOIDEncoding(oObjectID.format(), bBerDataType, sValue);

			lsTlvBuffer.add(HexString.toHexString(iSnmpTlvType) + 
					HexString.toHexString((sTlvObjectID.length()/2)) + 
					sTlvObjectID);

		} catch (Exception e) {
			e.printStackTrace();						
		}

		updateMapTypeToByteLength(iSnmpTlvType, ONE_BYTE_LENGTH);
		
	}

	/**
	 * 
	 * @param iSnmpTlvType Must be 0>Type<256
	 * @param oObjectID	   OID object from SNMP4J	
	 * @param bBerDataType BER Datatype
	 * @param bValue byteArray Value*/
	public void add (int iSnmpTlvType, OID oObjectID , byte bBerDataType , byte[] bValue) {	

		String sTlvObjectID = "";
		
		//This Will need to use a SNMP VarBinding
		//I m hard coding this the Number of Byte Length, need to go back and fix this
		if (bValue.length > 254) {
			updateMapTypeToByteLength(iSnmpTlvType , 2);
		} else {
			updateMapTypeToByteLength(iSnmpTlvType, ONE_BYTE_LENGTH);	
		}
		
		try {

			sTlvObjectID = BERService.setOIDEncoding(oObjectID.format(), bBerDataType, bValue);
			
			lsTlvBuffer.add(HexString.toHexString(iSnmpTlvType) + 
					HexString.toHexString((sTlvObjectID.length()/2)) + 
					sTlvObjectID);

		} catch (Exception e) {
			e.printStackTrace();						
		}
	
	}
	
	/**
	 * 
	 * This option is used when TLVs are greater than 1 byte Length or a length of 254 bytes
	 * 
	 * @param tvbObject TlvVariableBinding Object*/
	public void add (TlvVariableBinding tvbObject) {

		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug){
			System.out.println("+-------------------------------------------------------------------------------------------------+");
			System.out.println("TlvBuilder.add(tvb) -> Size: " + tvbObject.length() + " - TlvVariableBinding: " + tvbObject);
			System.out.println("TlvBuilder.add(tvb) -> TlvVariableBinding-Map: " + tvbObject.getMapTypeToByteLength());
		
		}
		
		this.lsTlvBuffer.add(new HexString(tvbObject.toByteArray()).toString());
		
		this.miiTlvTypeTpByteLength.putAll(tvbObject.getMapTypeToByteLength());
		
		this.lbTlvBuilder.addAll(tvbObject.getByteListTlvBuffer());
		
		if (debug|localDebug) {
			System.out.println("TlvBuilder.add(tvb) -> TlvBuilder.lsTlvBuffer: " + lsTlvBuffer);
			System.out.println("TlvBuilder.add(tvb) -> TlvBuilder.miiTlvTypeTpByteLength: " + miiTlvTypeTpByteLength);
			System.out.println("TlvBuilder.add(tvb) -> TlvVariableBinding.byteArray: " + new HexString(tvbObject.toByteArray()).toString());
		}	
	}
	
	/**
	 *
	 * @return String if a List Array String Style of the buffer in Hex
	 */
	public String toStringArray () {
		return lsTlvBuffer.toString();
	}

	/**
	 * 
	 * @return String Style of the buffer in Hex*/
	@Override
	public String toString () {
		return lsTlvBuffer.toString().replaceAll("\\[|\\]|\\,|\\s", "");		
	}
	
	/**
	 * 
	 * @param sSeperation
	 * @return String Style of the buffer in Hex with a defined separation character*/
	public String toStringSeperation (String sSeperation) {
		return new HexString(toByteArray()).toString(sSeperation);
	}

	/**
	 * 	
	 * @return int length of buffer in bytes*/
 	public int length () {
		return toByteArray().length;
	}

	/**
	 * 
	 * @param iTlvType	Must be 0 thru 255
	 * 
	 * Encapsulate a TLV with the current TLV in the Buffer
	 * 
	 * Example:
	 * iTlvType = 1
	 * 
	 * TLV 030101 -> 0103030101
	 * 
	 * @throws TlvException */
	public void encapsulateTlv (int iTlvType) throws TlvException {		

		if (debug)
			System.out.println("TlvBuilder.encapsulateTlv(i) " + "Type: " + iTlvType );
		
		if (iTlvType < 0)
			throw new TlvException("TlvBuilder.encapsulateTlv() Type Less than 0");
		
		if (iTlvType > 255)
			throw new TlvException("TlvBuilder.add() Type MUST be less than 256");
		
		lsTlvBuffer.add(0,HexString.toHexString(iTlvType) + HexString.toHexString(length()));				
	}

	/**
	 * 
	 * @param iTlvType	Must be 0 thru 255
	 * @param iLength
	 * 
	 * Encapsulate a TLV with the current TLV in the Buffer
	 * 
	 * Example:
	 * iTlvType = 1
	 * iLength = 3
	 * TLV 030101 -> 0103030101
	 * 
	 * @throws TlvException */
	public void encapsulateTlv (int iTlvType , int iLength) throws TlvException {		

		if (debug)
			System.out.println("TlvBuilder.encapsulateTlv(i,i) " + "Type: " + iTlvType + " -> Length: " + iLength);
		
		if (iTlvType < 0)
			throw new TlvException("TlvBuilder.encapsulateTlv() Type Less than 0");
		
		if (iLength < 0)
			throw new TlvException("TlvBuilder.encapsulateTlv() Length is less than 0");
		
		if (iTlvType > 255)
			throw new TlvException("TlvBuilder.add() Type MUST be less than 256");

		lsTlvBuffer.add(0,HexString.toHexString(iTlvType) + HexString.toHexString(iLength));			
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.tlv.TlvBuild#clear()
	 * 
	 * Clear buffer
	 */
	public void clear() {
		lsTlvBuffer.clear();	
	}

	/**
	 * 
	 * @return TLV in a new byte array, not a pointer to buffer */
	public byte[] toByteArray() {
		HexBinaryAdapter adapter = new HexBinaryAdapter();
		return adapter.unmarshal(this.toString());
	}

	/**
	 * 
	 * @return a list of TopLevel TLV * @see com.comcast.oscar.tlv.TlvBuild#getTopLevelTlvList()
	 */
	public List<Integer> getTopLevelTlvList() {

		boolean localDebug = Boolean.FALSE;
		
		List<Integer> liTopLevelTlvList = new ArrayList<Integer>();

		byte[] bTlvBuffer = toByteArray();
		
		//Cycle thru the Byte Array to find the TopLevel TLV's
		for  (int iIndex = 0; iIndex < toByteArray().length;) {
			
			//When you reach EOF, add to list and break out of loop
			if (BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]) == Constants.END_OF_FILE) {
				liTopLevelTlvList.add(BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]));
				break;
			} 
			//If you see padding, advance one more index till you see a TYPE
			else if (BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]) == Constants.PAD) {
				iIndex++;
				continue;
			}
			
			//Add TYPE to List
			liTopLevelTlvList.add(BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]));
			
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.getTopLevelTlvList() " +
									"+----INDEX: " + iIndex + 
									"--TLV: " + BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]) + 
									"----+");

			//Point to next TLV TYPE			
			iIndex += (BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex+TLV_LENGTH_POS_OFFSET])) + 
															TLV_TYPE_LENGTH_OVERHEAD;
			
			if (debug|localDebug) {
				System.out.println(	"TlvBuilder.getTopLevelTlvList()  " +
									"LENGTH: " + BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex+TLV_LENGTH_POS_OFFSET]) + 
									" -> NEXT-INDEX: " + iIndex );
			}
			
		}
			
		return liTopLevelTlvList;
	}

	/**
	 * 
	 * @param miiTypeByteLength	- 
	 * 
	 * @return List<Integer>
	 * @see com.comcast.oscar.tlv.TlvBuild#getTopLevelTlvList(Map<Integer,Integer>)*/
	public List<Integer> getTopLevelTlvList (Map<Integer,Integer> miiTypeByteLength) {
		
		boolean localDebug = Boolean.FALSE;
		
		if (miiTypeByteLength == null) {
			try {
				throw new TlvException("miiTypeByteLength is NULL");
			} catch (TlvException e) {
				e.printStackTrace();
			}
		}
		
		if (debug|localDebug)
			System.out.println("TlvBuilder.getTopLevelTlvList() : " + miiTypeByteLength);
		
		//Create a list of found TopLevel TLV's
		List<Integer> liTopLevelTlvList = new ArrayList<Integer>();
		
		//Convert to ByteArray
		byte[] bTlvBuffer = toByteArray();
		
		//Size of byteLength
		int iTypeByteLength = 0;
		
		//Cycle thru the Byte Array to find the TopLevel TLV's
		for  (int iIndex = 0; iIndex < toByteArray().length;) {
	
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.getTopLevelTlvList: -> Type: " + BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]) + 
									" Index: " + iIndex + " of " + toByteArray().length);
			
			//Record the TYPE that was found
			liTopLevelTlvList.add(BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]));
			
			//Fetch Type Byte Length Size to determine the next TopLevel TLV
			iTypeByteLength = miiTypeByteLength.get(BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]));
			
			try {
				iIndex = nextTLVIndex(bTlvBuffer, iIndex, iTypeByteLength);
			} catch (TlvException e) {
				e.printStackTrace();
			}
			
		}
			
		return liTopLevelTlvList;
	}
	
	/**
	 * 	
	 * @return a Clone of the TLV Buffer */
	public List<String> cloneTlvBuffer () {

		List<String> lCloneTlvBuffer = new ArrayList<String>(lsTlvBuffer.size());

		for(String item: lsTlvBuffer) 
			lCloneTlvBuffer.add(item);

		return lCloneTlvBuffer;
	}
	
	/**
	 * @return This method returns a TlvBuild of the bytes contained */
	@Override
	public TlvBuilder clone() {
			
		TlvBuilder tbClone =  new TlvBuilder();
		
		try {
			tbClone.add(new HexString(this.toByteArray()));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		return tbClone;
	}

	/**
	 *	
	 * @return List<byte[]>*/
	public List<byte[]> sortByTopLevelTlv () {
		
		boolean localDebug = Boolean.FALSE;
		
		ByteArrayOutputStream baosTopLevelTlvList;
		
		List<byte[]> lbTopLevelTlvList = new ArrayList<byte[]>();
		
		byte[] bMajorTlv = toByteArray();
		
		int iIndex = 0, iNextIndex = 0;
		
		//Cycle thru Byte Array
		while (iIndex < (bMajorTlv.length-1)) {
						
			//Create new BAOS
			baosTopLevelTlvList = new ByteArrayOutputStream();
			
			//Find the next TLV Index
			try {
				
				if (localDebug|debug)
					System.out.println("TlvBuilder.sortByTopLevelTlv() -> nextTLVIndex()" 
											+ " -> Index: " + iIndex 
											+ " -> Length: " + bMajorTlv.length
											+ " -> Value: " + bMajorTlv[iIndex]);
			
				iNextIndex = nextTLVIndex(bMajorTlv, iIndex);
				
			} catch (TlvException e) {
				e.printStackTrace();
			}

			//Write only the Major TLV in this Cycle
			baosTopLevelTlvList.write(	bMajorTlv, 
										iIndex, 					// Index
										((iNextIndex-iIndex) )); 	//Calculate Length
			
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.sortByTopLevelTlv () +---- TYPE: " + BinaryConversion.byteToUnsignedInteger(bMajorTlv[iIndex]) + 
									" ---- Length: " +bMajorTlv.length + 
									" ---- INDEX: " + iIndex + 
									" ---- NEXT-INDEX: " + iNextIndex + 
									" ---- TLV-LENGTH: " + (iNextIndex-iIndex) + 
									" ---- BAOS: " + baosTopLevelTlvList.size() + " ----+");			
			
			//Add TLV to List
			lbTopLevelTlvList.add(baosTopLevelTlvList.toByteArray());
					
			//Update to the new Index
			iIndex = iNextIndex;
		}
		
		return lbTopLevelTlvList;
		
	}
	
	/**
	 * 
	 * Unless specified in the Map, this method assumes each TopLevel is 1 byte of length
	 * 
	 * @param miiTopLevelTLVByteLength <Type,NumByteLength>	
	 * @return list of TLV Bytes */
	public List<byte[]> sortByTopLevelTlv (Map<Integer,Integer> miiTopLevelTLVByteLength) {
		
		boolean localDebug = Boolean.FALSE;
	
		if (debug|localDebug) {
			Thread.dumpStack();
			System.out.println(	"+==============================sortByTopLevelTlv(Map)=========================================================+");
			System.out.println(	"TlvBuilder.sortByTopLevelTlv(Map) -> TlvToByteMap: " + miiTopLevelTLVByteLength);
		}
				
		//Create a ByteArrayOutputStream
		ByteArrayOutputStream baosTopLevelTlvList;
		
		//Create a list to contain the TopLevel TLVs
		List<byte[]> lbTopLevelTlvList = new ArrayList<byte[]>();
		
		//Get ByteArray from This Object
		byte[] bMajorTlv = toByteArray();
		
		int iIndex = 0, iNextIndex = 0;
		
		int iByteLength = 1;

		if (debug|localDebug) {
			System.out.println(	"TlvBuilder.sortByTopLevelTlv(Map) -> bMajorTlv.length: " 		+ bMajorTlv.length);
			System.out.println(	"TlvBuilder.sortByTopLevelTlv(Map) -> HEX-bMajorTlv.length: " 	+ new HexString(bMajorTlv).toString());		
		}
		
		//Cycle thru Byte Array
		while (iIndex < bMajorTlv.length) {

			//Create new BAOS
			baosTopLevelTlvList = new ByteArrayOutputStream();
			
			int iTypeFound = BinaryConversion.byteToUnsignedInteger(bMajorTlv[iIndex]);

			if (debug|localDebug) 
				System.out.println(	"TlvBuilder.sortByTopLevelTlv(Map) " 	+
											" -> toByteArray().length: " 	+ toByteArray().length + 
											" -> Type: " 					+ iTypeFound + 
											" -> iIndex: " 					+ iIndex + 
											" -> iByteLength: "			 	+ iByteLength + 
											" -> NEXT-INDEX: "  			+ iNextIndex 
											);
			
										//Get byte Length Via TYPE
			
			//Workaround for building Default Configuration File Default Settings
			if (!miiTopLevelTLVByteLength.containsKey(iTypeFound)) {
				
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.sortByTopLevelTlv(Map) - Type Found: " + iTypeFound + " - No Key Map Match");
				
				iIndex += TLV_TYPE_LENGTH_OVERHEAD; continue;
				
			} else {
				
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.sortByTopLevelTlv(Map) - Type Found: " + iTypeFound + " - Key Map Match");
			}
						
			iByteLength = miiTopLevelTLVByteLength.get(iTypeFound);

			if (debug|localDebug) 
				System.out.println(	"TlvBuilder.sortByTopLevelTlv(Map) - PRE-NEXT-TLV()" +
											" -> Type: " 		+ iTypeFound	+
											" -> iIndex: " 		+ iIndex 		+ 
											" -> iByteLength: "	+ iByteLength
											);
			
			//Find the next TLV Index
			try {
				iNextIndex = nextTLVIndex(bMajorTlv, iIndex , iByteLength);
			} catch (TlvException e) {
				e.printStackTrace();
			}
			
			if (debug|localDebug) 
				System.out.println(	"TlvBuilder.sortByTopLevelTlv(Map) " +
											" -> Type: " 		+ iTypeFound	+
											" -> iIndex: " 		+ iIndex 			+ 
											" -> iByteLength: "	+ iByteLength 		+ 
											" -> NEXT-INDEX:  "	+ iNextIndex
											);
			
			//Write only the Major TLV in this Cycle
			baosTopLevelTlvList.write(	bMajorTlv, 
										iIndex, 				// Index
										((iNextIndex - iIndex))); 	//Calculate Length
			
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.sortByTopLevelTlv(Map) " +
									" -> TYPE: " 		+ iTypeFound + 
									" -> Length: " 		+ bMajorTlv.length + 
									" -> INDEX: " 		+ iIndex + 
									" -> NEXT-INDEX: " 	+ iNextIndex + 
									" -> TLV-LENGTH: " 	+ (iNextIndex-iIndex) + 
									" -> BAOS: " 		+ baosTopLevelTlvList.size()
									);			
			
			//Add TLV to List
			lbTopLevelTlvList.add(baosTopLevelTlvList.toByteArray());
					
			//Update to the new Index
			iIndex = iNextIndex;
		}
		
		return lbTopLevelTlvList;
		
	}

	/**
	 * 
	 * @return Map<Integer,Integer>
	 * @see com.comcast.oscar.tlv.TlvBuild#getMapTypeToByteLength()*/
	public Map<Integer,Integer> getMapTypeToByteLength () {
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug| localDebug)
			System.out.println("TlvBuilder.getMapTypeToByteLength(): " + miiTlvTypeTpByteLength);
	
		return miiTlvTypeTpByteLength;
	}
	
	/**
	 *	
	 * @return Boolean
	 * @see com.comcast.oscar.tlv.TlvBuild#isEmpty()*/
	public Boolean isEmpty() {
		return lsTlvBuffer.isEmpty();
	}
	
	/**
	 *
	 * @return HexString*/
	public HexString getHexString() {
		return new HexString(toByteArray());
	}
	
	/**
	 * 
	 * @param baTlv
	 * @param miiTypeToByteLength	
	 * @return list of TLVs in Groups of TopLevel TLV */
	public static List<HexString> topLevelTlvToHexStringList (byte[] baTlv , Map<Integer,Integer> miiTypeToByteLength) {
		
		List<HexString> lhs = new ArrayList<HexString>();
		
		for (int iIndex = 1 ; iIndex <= 255 ; iIndex++) {
			
			List<Integer> liType = new ArrayList<Integer>();
			
			liType.add(iIndex);
			
			byte[] baTlvFound = fetchTlv(liType, miiTypeToByteLength, baTlv);
			
			if (baTlvFound.length > 0) {
				lhs.add(new HexString(baTlvFound));
			}
						
		}
		
		return lhs;
		
	}
		
	/**
	 * 
	 * This method assumes each type has a 1 byte length encoding
	 * 
	 * @param iType Must be 0 thru 255
	 * @param bTlvByteArray	Value of TLV in a ByteArray
	 * @return byte[]
	 * @throws TlvException */
	public static byte[] stripTlv (int iType , byte[] bTlvByteArray) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if (iType < 0)
			throw new TlvException("TlvBuilder.stripTlv() Type Less than 0");
		
		if (bTlvByteArray ==  null)
			throw new TlvException("TlvBuilder.stripTlv() Byte Array is NULL");

		if (iType > 255)
			throw new TlvException("TlvBuilder.add() Type MUST be less than 256");
		
		ByteArrayOutputStream  baosStripedTlvByteArray = new ByteArrayOutputStream();

		int iIndex = 0;
		int iTypeLength = 0;
		
		//The -1 is so that this loops assume that there is no next final type
		while (iIndex < bTlvByteArray.length-1) {
			
			if (debug|localDebug) {
				System.out.println("+--------------------------------------------------------------------------------------------+");
				System.out.println("TlvBuilder.stripTlv(i,b) - Type: " + BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]));
				System.out.println("TlvBuilder.stripTlv(i,b) - INDEX: " + iIndex);
				System.out.println("TlvBuilder.stripTlv(i,b) - ByteArrayLen: " + bTlvByteArray.length);
			}
			
			iTypeLength = (BinaryConversion.byteToUnsignedInteger(	bTlvByteArray[iIndex+TLV_LENGTH_POS_OFFSET])) + 
																	TLV_TYPE_LENGTH_OVERHEAD;

			//If no type match, Copy TLV to ByteArray
			if (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]) != iType) {
				
				if (debug|localDebug) {
					System.out.println("TlvBuilder.stripTlv(i,b) - Adding-Type: " + BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]));
					System.out.println("TlvBuilder.stripTlv(i,b) - INDEX: " + iIndex);
					System.out.println("TlvBuilder.stripTlv(i,b) - bTlvByteArray.size: " + bTlvByteArray.length);
				}
								
				/*if (bTlvByteArray != null)*/
					baosStripedTlvByteArray.write(bTlvByteArray, iIndex, iTypeLength);
				
			} else {
				
				if (debug|localDebug) {
					System.out.println("TlvBuilder.stripTlv(i,b) - Skipping-Type: " + BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]));
					System.out.println("TlvBuilder.stripTlv(i,b) - INDEX: " + iIndex);
				}				
			
			}
	
			//Goto Next TLV Index
			iIndex += iTypeLength;

		}		

		return baosStripedTlvByteArray.toByteArray();
	}

	/**
	 * 
	 * @param iType - Integer 0>Type<255
	 * @param bTlvByteArray ByteArray containing TLV Buffer
	 * @param miiTypeToByteLength Map Containing a Type to Bytelength association	
	 * @return byte[] ByteArray that do not contain the specified Type
	 * @throws TlvException */
	public static byte[] stripTlv (int iType , byte[] bTlvByteArray , Map<Integer,Integer> miiTypeToByteLength) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if ((iType < 0) && (iType > 255))
			throw new TlvException("TlvBuilder.stripTlv() Type Less than 0 or Greater than 255");
		
		if (bTlvByteArray ==  null)
			throw new TlvException("TlvBuilder.stripTlv() Byte Array is NULL");
		
		if (miiTypeToByteLength ==  null)
			throw new TlvException("TlvBuilder.stripTlv() No Type To ByteLength Map Found");
		
		ByteArrayOutputStream  baosStripedTlvByteArray = new ByteArrayOutputStream();

		int iIndex = 0;
		int iTypeLength = 0;
		int iTypeFound = 0;
		int iTypeByteLength = 0;
		
		//The -1 is so that this loops assume that there is no next final type
		while (iIndex < bTlvByteArray.length-1) {
			
			iTypeFound = BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]);
			
			if (debug|localDebug) {
				System.out.println("+--------------------------------------------------------------------------------------------+");
				System.out.println("TlvBuilder.stripTlv(i,b) - Type: " + BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]));
				System.out.println("TlvBuilder.stripTlv(i,b) - INDEX: " + iIndex);
				System.out.println("TlvBuilder.stripTlv(i,b) - ByteArrayLen: " + bTlvByteArray.length);
			}
			
			if (miiTypeToByteLength.containsKey(iTypeFound)) {
				iTypeByteLength = miiTypeToByteLength.get(iTypeFound);
				
				iTypeLength = (BinaryConversion.byteToUnsignedInteger(	bTlvByteArray[iIndex+TLV_LENGTH_POS_OFFSET])) + 
						iTypeByteLength + TLV_ONE_BYTE_LENGTH;	
				
			} else {
				
				iTypeLength = (BinaryConversion.byteToUnsignedInteger(	bTlvByteArray[iIndex+TLV_LENGTH_POS_OFFSET])) + 
						TLV_TYPE_LENGTH_OVERHEAD;				
			}
						
			//If no type match, Copy TLV to ByteArray
			if (iTypeFound != iType) {
				
				if (debug|localDebug) {
					System.out.println("TlvBuilder.stripTlv(i,b) - Adding-Type: " + BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]));
					System.out.println("TlvBuilder.stripTlv(i,b) - INDEX: " + iIndex);
					System.out.println("TlvBuilder.stripTlv(i,b) - NUM-OF-BYTES: " + iTypeLength);
					System.out.println("TlvBuilder.stripTlv(i,b) - bTlvByteArray.size: " + bTlvByteArray.length);
				}
								
				/*if (bTlvByteArray != null)*/
					baosStripedTlvByteArray.write(bTlvByteArray, iIndex, iTypeLength);
				
			} else {
				
				if (debug|localDebug) {
					System.out.println("TlvBuilder.stripTlv(i,b) - Skipping-Type: " + BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]));
					System.out.println("TlvBuilder.stripTlv(i,b) - INDEX: " + iIndex);
				}				
			
			}
	
			//Goto Next TLV Index
			iIndex += iTypeLength;

		}		

		return baosStripedTlvByteArray.toByteArray();
	}
	
	/**
	 * 
	 * @param liType - List of integers that contain the Types that are to be fetched from TLV ByteArray
	 * @param bTlvByteArray	Byte Array that contains TLV, MUST be in TLV Construct
	 * @return byte[]
	 */
	public static byte[] fetchTlv (List<Integer> liType , byte[] bTlvByteArray) {

		boolean localDebug = Boolean.FALSE;
		
		ByteArrayOutputStream  baosStripedTlvByteArray = new ByteArrayOutputStream();

		if (debug|localDebug) {
			System.out.println("+------------------------------------------------------------------------------------------------+");
			System.out.println("TlvBuilder.fetchTlv(LIST): Find: " + liType);
			System.out.println("TlvBuilder.fetchTlv(LIST): " + new HexString(bTlvByteArray).toString());
		}
		
		for (int iType : liType ) {

			int iIndex = 0;
			int iTypeLength = 0;

			while (iIndex < bTlvByteArray.length) {
				
				//Look ahead to see if we exceed Byte Array Length
				if ((iIndex + TLV_LENGTH_POS_OFFSET) >= bTlvByteArray.length) {					
					
					if (debug|localDebug)
						System.out.println("TlvBuilder.fetchTlv(l,b): - Look Ahead Exceeds Index");
					
					break;
				}
				
				iTypeLength = (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex + TLV_LENGTH_POS_OFFSET])) + 
																	TLV_TYPE_LENGTH_OVERHEAD;

				if (debug|localDebug)
					System.out.println(	"TlvBuilder.fetchTlv(LIST) " +
										"+---- TYPE: " + BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]) + 
										" ---- Length: " + bTlvByteArray.length + 
										" ---- INDEX: " + iIndex + 
										" ---- TYPE-LENGTH: " + iTypeLength + 
										" ---- BAOS: " + baosStripedTlvByteArray.size() + " ----+");
				
				//If no type match, Copy TLV to ByteArray
				if (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]) == iType) {
					
					if (debug|localDebug)
						System.out.println(	"FOUND - TlvBuilder.fetchTlv(LIST) " +
											"+---- TYPE: " + BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]) + 
											" ---- Length: " + bTlvByteArray.length + 
											" ---- INDEX: " + iIndex + 
											" ---- TYPE-LENGTH: " + iTypeLength + 
											" ---- BAOS: " + baosStripedTlvByteArray.size() + " ----+");
					
					baosStripedTlvByteArray.write(bTlvByteArray, iIndex, iTypeLength);
										
				}

				//Goto Next TLV Index
				iIndex += iTypeLength;

			}
		}

		return baosStripedTlvByteArray.toByteArray();
	}	

	/**
	 * 
	 * If a Type to Byte Length is not defined, this method assumes that the type is of 1 byte Length
	 * 
	 * @param liType - List of integers that contain the Types that are to be fetched from TLV ByteArray
	 * @param bTlvByteArray	Byte Array that contains TLV, MUST be in TLV Construct
	 * @param _miiTypeByteLength Map<Integer,Integer>
	 * @return byte[]
	 */
	public static byte[] fetchTlv (List<Integer> liType , Map<Integer,Integer> _miiTypeByteLength , byte[] bTlvByteArray) {
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("+===================================TlvBuilder.fetchTlv(list,m,b)====================================================+");
			System.out.println("TlvBuilder.fetchTlv(list,m,b): TypeList:" + liType );
			System.out.println("TlvBuilder.fetchTlv(list,m,b): Map<Integer,Integer>:" + _miiTypeByteLength );
		}
				
		ByteArrayOutputStream  baosStripedTlvByteArray = new ByteArrayOutputStream();

		if (debug|localDebug)
			System.out.println("TlvBuilder.fetchTlv(list,m,b): TypeList:" + liType + " - VALUE: " + new HexString(bTlvByteArray).toString());

		if (debug|localDebug)
			System.out.println("TlvBuilder.fetchTlv(list,m,b): TypeByteLength: " + _miiTypeByteLength);
		
		for (int iType : liType ) {

			if (debug|localDebug)
				System.out.println("TlvBuilder.fetchTlv(list,m,b) -> Fetching Type: " + iType);
						
			int iIndex = 0;
			int iTypeLength = 0 , 
				iTypeByteLength = 0,
				iTypeFound = 0;
			
			//This Will Assume 1 byte if no Type has need registered in Map
			if (_miiTypeByteLength.containsKey(iType)) {
				
				if (debug|localDebug)
					System.out.println("TlvBuilder.fetchTlv(list,m,b) -> FoundMapKey: " + iType + " - Length: " + _miiTypeByteLength.get(iType));
				
				iTypeByteLength = _miiTypeByteLength.get(iType);
				
			} else {				
				iTypeByteLength = TLV_LENGTH_POS_OFFSET;
			}
			
			if (debug|localDebug)
				System.out.println("TlvBuilder.fetchTlv(list,m,b) -> Type: " + iType + " -> NumOfByteLength: " + iTypeByteLength);
						
			while (iIndex < bTlvByteArray.length-1) {
				
				iTypeFound = BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]);
								
				try {
					iTypeLength = getTlvLength(bTlvByteArray, iIndex, iTypeByteLength);
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				//Determine Length of TLV Value
				if (debug|localDebug)
					System.out.println(	"TlvBuilder.fetchTlv(list,m,b) " +
										" 1077 -> TYPE: " + iTypeFound +
										" -> NumByteLength: " + iTypeByteLength + 
										" -> Total-Length: " + bTlvByteArray.length + 
										" -> INDEX: " + iIndex + 
										" -> ACTUAL-ENCODED-VALUE-LENGTH: " + iTypeLength + 
										" -> BAOS: " + baosStripedTlvByteArray.size() + " ----+");
				
				//If the TypeLength is 0 , goto next Index
				if (iTypeLength == 0) {
					iIndex++; continue;
				}
							
				//If no type match, Copy TLV to ByteArray
				if (iTypeFound == iType) {

					if (debug|localDebug) {
						System.out.println(	"TlvBuilder.fetchTlv(list,m,b) " +
											" -> Found TLV: (" + iType + ") " +
											" -> Index: " + iIndex);
					}
					
					baosStripedTlvByteArray.write(bTlvByteArray, iIndex, (iTypeLength + TLV_TYPE_LENGTH_OVERHEAD + (iTypeByteLength -1)));
					
					if (debug|localDebug) {
						System.out.println(	"TlvBuilder.fetchTlv(list,m,b) " +
											" -> ByteLength: " + baosStripedTlvByteArray.size());
						
						System.out.println(	"TlvBuilder.fetchTlv(list,m,b) " +
											" -> TLV Hex : " + new HexString(baosStripedTlvByteArray.toByteArray()).toString(":"));
					}
					
				}
				
				if (debug|localDebug) {
						System.out.println(	"TlvBuilder.fetchTlv(list,m,b) " +
											" -> No Type Match - Found: " + iTypeFound + " - Searching For: " + iType);				
				}

				//Goto Next TLV Index
				iIndex += (iTypeLength + iTypeByteLength + TLV_TYPE_OVERHEAD);
				
				if (debug|localDebug)
					System.out.println(	"TlvBuilder.fetchTlv(list,m,b) -> Next-Index: " + iIndex);
			}
		}

		if (debug|localDebug) {
			System.out.println(	"TlvBuilder.fetchTlv(list,m,b) -> BAOS: " + new HexString(baosStripedTlvByteArray.toByteArray()).toString(":"));
		}
		
		return baosStripedTlvByteArray.toByteArray();
	}
	
	/**
	 * 
	 * @param bTlvBuffer
	 * @param iInitalPosition	
	 * @return -1 = End of ByteArray Index * @throws TlvException */
	public static int nextTLVIndex(byte[] bTlvBuffer , int iInitalPosition) throws TlvException {		
		
		boolean localDebug = Boolean.FALSE;
		
		if (iInitalPosition < 0)
			throw new TlvException("TlvBuilder.nextTLVIndex() Type Less than 0");
		
		if (bTlvBuffer ==  null)
			throw new TlvException("TlvBuilder.nextTLVIndex() Byte Array is NULL");
		
		if ((iInitalPosition + TLV_LENGTH_POS_OFFSET) >= bTlvBuffer.length) {
			if (debug|localDebug) {
				System.out.println("TlvBuilder.nextTLVIndex(b,i) - BAD - : bTlvBuffer.length: " + bTlvBuffer.length);			
				System.out.println("TlvBuilder.nextTLVIndex(b,i): iInitalPosition: " + iInitalPosition);
				System.out.println("TlvBuilder.nextTLVIndex(b,i): Hex: " + new HexString(bTlvBuffer).toString(":"));
			}
			
			//Added this to indicate that pointer exceeded byte array
			return END_OF_BYTE_ARRAY_INDEX;
		} 
		
		return (iInitalPosition + 
				BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iInitalPosition + TLV_LENGTH_POS_OFFSET]) + 
				TLV_TYPE_LENGTH_OVERHEAD); 
	}

	/**
	 * 
	 * @param bTlvBuffer
	 * @param iInitalPosition
	 * @param iByteLength	
	 * @return int
	 * @throws TlvException */
	public static int nextTLVIndex(byte[] bTlvBuffer , int iInitalPosition , int iByteLength) throws TlvException {
	
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {		
			System.out.println("+================================TlvBuilder.nextTLVIndex(b,i,i)========================================+");
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> TLV-TYPE: " + BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iInitalPosition]));
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> NUM-BYTE-LEN: " + iByteLength);
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> STARTINDEX: " + iInitalPosition);
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> HEX: " + new HexString(bTlvBuffer).toString(":"));
		}
		
		if (iInitalPosition < 0)
			throw new TlvException("TlvBuilder.nextTLVIndex() Type Less than 0");
		
		if (bTlvBuffer ==  null)
			throw new TlvException("TlvBuilder.nextTLVIndex() Byte Array is NULL");

		if (iByteLength < 0)
			throw new TlvException("TlvBuilder.nextTLVIndex() Byte Length Less than 0");
		
		ByteArrayOutputStream baosTlvByteLength = new ByteArrayOutputStream();
		
		baosTlvByteLength.write(bTlvBuffer, (iInitalPosition + 1), iByteLength);
		
		int iTlvLength = new HexString(baosTlvByteLength.toByteArray()).toInteger();
		
		if (debug|localDebug) {		
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> TLV-TYPE: " + BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iInitalPosition]));
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> NUM-BYTE-LEN: " + iByteLength);
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> LENGTH-HEX: " + new HexString(baosTlvByteLength.toByteArray()).toString(":"));
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> TLV-LENGTH: " + iTlvLength);
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> STARTINDEX: " + iInitalPosition);
			System.out.println("TlvBuilder.nextTLVIndex(b,i,i) -> NEXT-INDEX: " + (iInitalPosition + (iTlvLength + iByteLength) + TLV_TYPE_OVERHEAD));
		}
		
		return (iInitalPosition + (iTlvLength + iByteLength) + TLV_TYPE_OVERHEAD); 
	}
	
	/**
	 * 
	 * @param bTlvBuffer
	 * @param bParentChildTlvEncodeList		
	 * @return byte[]
	 * @throws TlvException */
	public static byte[] findTLVIndex (byte[] bTlvBuffer , byte[] bParentChildTlvEncodeList) throws TlvException {
	
		boolean localDebug = Boolean.FALSE;
		
		if (bTlvBuffer ==  null)
			throw new TlvException("TlvBuilder.findTLVIndex() TLV Byte Array is NULL");

		if (bParentChildTlvEncodeList ==  null)
			throw new TlvException("TlvBuilder.findTLVIndex() bParentChildTlvEncodeList is NULL");
		
		ByteArrayOutputStream  baosTlvTypeIndexList = new ByteArrayOutputStream();
		
		if (debug|localDebug) 
			System.out.println("TlvBuilder.findTLVIndex(b,b) " + new HexString(bParentChildTlvEncodeList).toString());
		
		//Need to make sure that length is at least 2 bytes
		if (bTlvBuffer.length < TLV_TYPE_LENGTH_OVERHEAD) {
			return baosTlvTypeIndexList.toByteArray();	
		} 

		if (debug|localDebug) 
			System.out.println(	"TlvBuilder.findTLVIndex(b,b) bParentChildTlvEncodeList: " + bTlvBuffer.length);
		
		//Jump ahead to the second TLV Type
		int iStartIndexEncodeList = 0;
	
		//Start into Sub TLV Array
		int iStartIndex = 0;

		while ((iStartIndex < bTlvBuffer.length) && (iStartIndexEncodeList < bParentChildTlvEncodeList.length)) {
		
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.findTLVIndex(b,b) bParentChildTlvEncodeList: " + new HexString(bParentChildTlvEncodeList).toString() + 
									" - iStartIndex: " + iStartIndex +
									" - bTlvBuffer.length: " + bTlvBuffer.length +
									" - Type: "+ bTlvBuffer[iStartIndex] +
									" - iStartIndexEncodeList: " + iStartIndexEncodeList +
									" - bParentChildTlvEncodeList: " + bParentChildTlvEncodeList[iStartIndexEncodeList] +
									" - baosTlvTypeIndexList: " + new HexString(baosTlvTypeIndexList.toByteArray()).toHexStringList());

			//Found a Matching Type, next step is to see if we reached the end of the ParentChildTlvEncodeList
			if (bTlvBuffer[iStartIndex] == bParentChildTlvEncodeList[iStartIndexEncodeList]) {

				//If we get to this point, we reached the end of the ParentChildTlvEncodeList
				if (iStartIndexEncodeList == bParentChildTlvEncodeList.length-1) {

					//Save Index
					baosTlvTypeIndexList.write(iStartIndex);

					//Finish looking thru Buffer in-case there are other TLVs
					iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex);

				} else {

					//We havn't reached the end of ParentChildTlvEncodeList, got next index
					iStartIndexEncodeList++;

					//Jump over length byte and into the next TLV Type
					iStartIndex += TLV_TYPE_LENGTH_OVERHEAD;
				}


			} else {
				//If there is Type Match, goto next TLV Encode
				iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex);
			}
			
			//If the index exceeds buffer length, we reached the end of the loop
			if (iStartIndex >= bTlvBuffer.length) 
				break;
			
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.findTLVIndex(b,b).bParentChildTlvEncodeList: " + new HexString(bParentChildTlvEncodeList).toString() + 
						" - iStartIndex: " + iStartIndex +
						" - bTlvBuffer.length: " + bTlvBuffer.length +
						" - Type: "+ bTlvBuffer[iStartIndex] +
						" - iStartIndexEncodeList: " + iStartIndexEncodeList +
						" - bParentChildTlvEncodeList: " + bParentChildTlvEncodeList[iStartIndexEncodeList] +
						" - baosTlvTypeIndexList: " + new HexString(baosTlvTypeIndexList.toByteArray()).toHexStringList());
		}
			
		return  baosTlvTypeIndexList.toByteArray();
	}

	/**
	 * 
	 * @param bTlvBuffer
	 * @param iType	
	 * @return the index where the Type is found * @throws TlvException */
	public static int findTLVIndex (byte[] bTlvBuffer , int iType) throws TlvException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (bTlvBuffer ==  null)
			throw new TlvException("TlvBuilder.findTLVIndex() TLV Byte Array is NULL");
		
		if (debug|localDebug) 
			System.out.println("TlvBuilder.findTLVIndex(b,i) Type " + iType);

		if (debug|localDebug) 
			System.out.println(	"TlvBuilder.findTLVIndex(b,i) bParentChildTlvEncodeList: " + bTlvBuffer.length);
		
		//Jump ahead to the second TLV Type
		int iStartIndexEncodeList = 0;
	
		//Start into Sub TLV Array
		int iStartIndex = 0;

		while (iStartIndex < bTlvBuffer.length) {
		
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.findTLVIndex(b,b) Type: " + iType + 
									" - iStartIndex: " + iStartIndex +
									" - bTlvBuffer.length: " + bTlvBuffer.length +
									" - Type: "+ bTlvBuffer[iStartIndex] +
									" - iStartIndexEncodeList: " + iStartIndexEncodeList);

			//Found a Matching Type, next step is to see if we reached the end of the ParentChildTlvEncodeList
			if (BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iStartIndex]) != iType) {

				//Finish looking thru Buffer in-case there are other TLVs
				iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex);

			} else {
				break;
			}
		}
		
		return  iStartIndex;
	}
	
	/**
	 * 
	 * @param bTlvBuffer
	 * @param bParentChildTlvEncodeList
	 * @param miiTypeByteLength	
	 * @return byte[]
	 * @throws TlvException */
	public static byte[] findTLVIndex (byte[] bTlvBuffer , byte[] bParentChildTlvEncodeList , Map<Integer,Integer> miiTypeByteLength) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if (bTlvBuffer ==  null)
			throw new TlvException("TlvBuilder.findTLVIndex() TLV Byte Array is NULL");

		if (bParentChildTlvEncodeList ==  null)
			throw new TlvException("TlvBuilder.findTLVIndex() bParentChildTlvEncodeList is NULL");
		
		ByteArrayOutputStream  baosTlvTypeIndexList = new ByteArrayOutputStream();
		
		if (debug|localDebug) 
			System.out.println("TlvBuilder.findTLVIndex(b,b,m) " + new HexString(bParentChildTlvEncodeList).toString());
		
		//Need to make sure that length is at least 2 bytes
		if (bTlvBuffer.length < TLV_TYPE_LENGTH_OVERHEAD) {
			return baosTlvTypeIndexList.toByteArray();	
		} 

		if (debug|localDebug) 
			System.out.println(	"TlvBuilder.findTLVIndex(b,b,m).bParentChildTlvEncodeList: " + bTlvBuffer.length);
		
		//Jump ahead to the second TLV Type
		int iStartIndexEncodeList = 0;
	
		//Start into Sub TLV Array
		int iStartIndex = 0;
		
		//Type ByteLength
		int iTypeByteLength = 0;

		while ((iStartIndex < bTlvBuffer.length) && (iStartIndexEncodeList < bParentChildTlvEncodeList.length)) {
		
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.findTLVIndex(b,b,m).bParentChildTlvEncodeList: " + new HexString(bParentChildTlvEncodeList).toString() + 
									" - iStartIndex: " + iStartIndex +
									" - bTlvBuffer.length: " + bTlvBuffer.length +
									" - Type: "+ bTlvBuffer[iStartIndex] +
									" - iStartIndexEncodeList: " + iStartIndexEncodeList +
									" - bParentChildTlvEncodeList: " + bParentChildTlvEncodeList[iStartIndexEncodeList] +
									" - baosTlvTypeIndexList: " + new HexString(baosTlvTypeIndexList.toByteArray()).toHexStringList());

			//Found a Matching Type, next step is to see if we reached the end of the ParentChildTlvEncodeList
			if (bTlvBuffer[iStartIndex] == bParentChildTlvEncodeList[iStartIndexEncodeList]) {

				//If we get to this point, we reached the end of the ParentChildTlvEncodeList
				if (iStartIndexEncodeList == bParentChildTlvEncodeList.length-1) {

					//Save Index
					baosTlvTypeIndexList.write(iStartIndex);
					
					//Lookup the Byte Length via Type
					iTypeByteLength = miiTypeByteLength.get(BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iStartIndex]));
					
					//Finish looking thru Buffer in-case there are other TLVs
					iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex , iTypeByteLength);

				} else {

					//We havn't reached the end of ParentChildTlvEncodeList, got next index
					iStartIndexEncodeList++;

					//Jump over length byte and into the next TLV Type
					iStartIndex += TLV_TYPE_LENGTH_OVERHEAD;
				}


			} else {
				//If there is Type Match, goto next TLV Encode
				iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex);
			}
			
			//If the index exceeds buffer length, we reached the end of the loop
			if (iStartIndex >= bTlvBuffer.length) 
				break;
			
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.findTLVIndex(b,b,m).bParentChildTlvEncodeList: " + new HexString(bParentChildTlvEncodeList).toString() + 
						" - iStartIndex: " + iStartIndex +
						" - bTlvBuffer.length: " + bTlvBuffer.length +
						" - Type: "+ bTlvBuffer[iStartIndex] +
						" - iStartIndexEncodeList: " + iStartIndexEncodeList +
						" - bParentChildTlvEncodeList: " + bParentChildTlvEncodeList[iStartIndexEncodeList] +
						" - baosTlvTypeIndexList: " + new HexString(baosTlvTypeIndexList.toByteArray()).toHexStringList());
		}
			
		return  baosTlvTypeIndexList.toByteArray();
	}
	
	/**
	 * 
	 * @param bTlvBuffer	
	 * @return byte[]
	 * @throws TlvException */
	public static byte [] getTlvValue (byte[] bTlvBuffer) throws TlvException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (bTlvBuffer ==  null)
			throw new TlvException("TlvBuilder.getTlvValue() bTlvBuffer is NULL");
		
		ByteArrayOutputStream  baosTlvBuffer = new ByteArrayOutputStream();
		
		if (debug|localDebug) 
			System.out.println( "TlvBuilder.getTlvValue(b).bTlvBuffer-Length: " + bTlvBuffer.length + 
								" HEX: " + new HexString(bTlvBuffer).toString(":"));
		
		baosTlvBuffer.write(bTlvBuffer, 
							TLV_TYPE_LENGTH_OVERHEAD, 
							BinaryConversion.byteToUnsignedInteger(bTlvBuffer[TLV_LENGTH_POS_OFFSET]));
			
		return baosTlvBuffer.toByteArray();
	}

	/**
	 * 
	 * @param bTlvBuffer
	 * @param iInitalPosition	
	 * @return byte[]
	 * @throws TlvException */
	public static byte [] getTlvValue (byte[] bTlvBuffer , int iInitalPosition) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if (bTlvBuffer ==  null)
			throw new TlvException("TlvBuilder.getTlvValue() bTlvBuffer is NULL");

		if (iInitalPosition < 0)
			throw new TlvException("TlvBuilder.getTlvValue() iInitalPosition than 0");
		
		if (debug|localDebug)
			System.out.println(	"getTlvLength(b,i) - bTlvBuffer.length: " + bTlvBuffer.length + 
								" - iInitalPosition: " + iInitalPosition);
		
		ByteArrayOutputStream  baosTlvBuffer = new ByteArrayOutputStream();

		//Write the value to OutputStream
		baosTlvBuffer.write(bTlvBuffer, 
							iInitalPosition + TLV_TYPE_LENGTH_OVERHEAD, 
							BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iInitalPosition+TLV_LENGTH_POS_OFFSET]));

		return baosTlvBuffer.toByteArray();
	}
	
	/**
	 * 
	 * @param bTlvBuffer
	 * @param iInitalPosition
	 * @param iByteLength	
	 * @return byte[]
	 * @throws TlvException */
	public static byte [] getTlvValue (byte[] bTlvBuffer , int iInitalPosition , int iByteLength) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if (bTlvBuffer ==  null)
			throw new TlvException("TlvBuilder.getTlvValue(b,i,i) bTlvBuffer is NULL");

		if (iInitalPosition < 0)
			throw new TlvException("TlvBuilder.getTlvValue(b,i,i) iInitalPosition than 0");

		if (iByteLength < 0)
			throw new TlvException("TlvBuilder.getTlvValue(b,i,i) iByteLength than 0");
				
		//Create ByteArrayOutputStream
		ByteArrayOutputStream  baosTlvBuffer = new ByteArrayOutputStream();
		
		//Determine TLV Length
		int iTlvValueLength = getTlvLength (bTlvBuffer , iInitalPosition , iByteLength);

		if (debug|localDebug) {
			System.out.println(	"+---------------------------------------------------------------------------------------------------------+");
			System.out.println(	"TlvBuilder.getTlvValue(b,i,i) - " +
								"bTlvBuffer.length: " + bTlvBuffer.length +
								" - Type: " + BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iInitalPosition]) + 
								" - iTlvValueLength: " + iTlvValueLength +
								" - iInitalPosition: " + iInitalPosition +
								" - iByteLength: " + iByteLength +
								" - TLV_TYPE_OVERHEAD: " + TLV_TYPE_OVERHEAD +
								" - Hex: " + new HexString(bTlvBuffer).toString(":"));
		}
		
		//Write the value to OutputStream
		baosTlvBuffer.write(bTlvBuffer, 
							(iInitalPosition + iByteLength + TLV_TYPE_OVERHEAD) , 
							iTlvValueLength);

		return baosTlvBuffer.toByteArray();
	}
	
	/**
	 * 
	 * @param bTlvBuffer
	 * @param iInitalPosition
	 * @param iByteLength	
	 * @return int
	 * @throws TlvException */
	public static int getTlvLength (byte[] bTlvBuffer , int iInitalPosition , int iByteLength) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if (bTlvBuffer ==  null)
			throw new TlvException("TlvBuilder.getTlvValue() bTlvBuffer is NULL");

		if (iInitalPosition < 0)
			throw new TlvException("TlvBuilder.getTlvValue() iInitalPosition than 0");

		if (iByteLength < 0)
			throw new TlvException("TlvBuilder.getTlvValue() iByteLength than 0");
		
		if (iByteLength == 0) return iByteLength;
				
		ByteArrayOutputStream baosTlvByteLength = new ByteArrayOutputStream();
		
		baosTlvByteLength.write(bTlvBuffer, iInitalPosition+1, iByteLength);

		if (debug|localDebug)
			System.out.println(	"TlvBuilder.getTlvLength(b,i,i) " +
								" - bTlvBuffer.length: " + bTlvBuffer.length +
								" - iInitalPosition: " + iInitalPosition + 
								" - iByteLength: " + iByteLength +
								" - Hex: " + new HexString(baosTlvByteLength.toByteArray()) +
								" - Length: " + new HexString(baosTlvByteLength.toByteArray()).toInteger());
		
		return  new HexString(baosTlvByteLength.toByteArray()).toInteger();

	}	

	/**
	 * This method take a multiple single byte length TLV, strip the TopLevel TLV byte and length and concatenate all the values
	 * <p>
	 * 
	 * @param tbTLV
	
	 * @return byte[]
	 */
	public static byte[] coupleMultipleTopLevelTlvValues (TlvBuilder tbTLV) {
		
		boolean localDebug = Boolean.FALSE;
		
		HexString hsTLV = new HexString();
		
		for (byte[] bTLV : tbTLV.sortByTopLevelTlv()) {
			try {
				hsTLV.add(getTlvValue(bTLV));
			} catch (TlvException e) {
				e.printStackTrace();
			}
		}
		
		if (debug|localDebug)
			System.out.println("TlvBuilder.coupleMultipleTopLevelTlvValues()" + hsTLV.toString(":"));
		
		return hsTLV.toByteArray();
		
	}
	
	/**
	 * 
	 * This method assumes all TopLevel TLV are 1 Byte Length
	 * 
	 * @param bTLV
	
	
	 * @return List<byte[]>
	 * @throws TlvException */
	public static List<byte[]> getTlvHexByType(byte[] bTLV) throws TlvException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug| localDebug)
			System.out.println("TlvBuilder.getTlvHexByType(): " + new HexString(bTLV).toString(":"));
		
		List<byte[]> lbTLV = new ArrayList<byte[]>();
		
		byte[] bFetchTLV = null;
		
		int iNextIndex = 0;
		
		while (iNextIndex < bTLV.length) {
			
			int iTypeFound = BinaryConversion.byteToUnsignedInteger(bTLV[iNextIndex]);
			
			//Added this to make sure index does not excess byteArray and cause a NPE
			if (iNextIndex == END_OF_BYTE_ARRAY_INDEX) {				
				if (debug|localDebug)
					System.out.println("TlvBuilder.getTlvHexByType() - END_OF_BYTE_ARRAY_INDEX - Exit For Loop: ");
				break;
			}
			
			//Create a new list
			List<Integer> li = new ArrayList<Integer>();
			
			//Add type to list
			li.add(iTypeFound);
							
			//Get TLV from Type
			bFetchTLV = fetchTlv(li , bTLV);
			
			if (debug|localDebug)
				System.out.println("TlvBuilder.getTlvHexByType(): Type: (" + iTypeFound + ") -> " + new HexString(bFetchTLV).toString(":"));
			
			//Load ByteArray of TLV
			lbTLV.add(bFetchTLV);
			
			//Index will be The TYPE
			iNextIndex = TlvBuilder.nextTLVIndex(bTLV, iNextIndex);
			
		}
		
		return lbTLV;
		
	}

	/**
	 * 
	 * @param bTLV
	 * @param miiTypeToByteLength	
	 * @return List<byte[]>
	 * @throws TlvException */
	public static List<byte[]> getTlvHexByType(byte[] bTLV , Map<Integer,Integer> miiTypeToByteLength) throws TlvException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug| localDebug)
			System.out.println("TlvBuilder.getTlvHexByType(): " + new HexString(bTLV).toString(":"));
		
		List<byte[]> lbTLV = new ArrayList<byte[]>();
		
		byte[] bFetchTLV = null;
		
		int iNextIndex = 0;
		
		while (iNextIndex < bTLV.length) {
			
			//Added this to make sure index does not excess byteArray and cause a NPE
			if (iNextIndex == END_OF_BYTE_ARRAY_INDEX) {				
				if (debug|localDebug)
					System.out.println("TlvBuilder.getTlvHexByType() - END_OF_BYTE_ARRAY_INDEX - Exit For Loop: ");
				break;
			}
			
			List<Integer> li = new ArrayList<Integer>();
			
			li.add(BinaryConversion.byteToUnsignedInteger(bTLV[iNextIndex]));
			
			if (debug| localDebug)
				System.out.println("TlvBuilder.getTlvHexByType(): TypeToByteLength: " + miiTypeToByteLength);
							
			//Get TLV from Type
			bFetchTLV = fetchTlv(li , miiTypeToByteLength, bTLV);
			
			if (debug|localDebug)
				System.out.println("TlvBuilder.getTlvHexByType(): Type: (" + BinaryConversion.byteToUnsignedInteger(bTLV[iNextIndex]) + ") -> " + new HexString(bFetchTLV).toString(":"));
			
			//Load ByteArray of TLV
			lbTLV.add(bFetchTLV);
			
			//Index will be The TYPE
			iNextIndex = TlvBuilder.nextTLVIndex(bTLV, iNextIndex);
			
		}
		
		
		return lbTLV;
		
	}
	
	/**
	 * 
	 * @return boolean
	 */
	public boolean isTLVConstruct () {
		
		boolean localDebug = Boolean.FALSE;
		
		boolean returnStatus = true;
		
		byte[] bTLV = toByteArray();
		
		int 	iIndex = 0 , 
				iType = 0,
				iNumOfByteLength = 0;
		
		//Check to see of there is any length
		if (bTLV.length == 0) {
			
			if (debug|localDebug) 
				System.out.println(	"TlvBuilder.isTLVConstruct() -> ByteArray is of 0 Length");
			
			return false;
		}
		
		if (debug|localDebug) 
			System.out.println(	"+========================================================================+");
		
		//cycle thru Byte Array
		while (iIndex < bTLV.length) {
			
			//Current Type
			iType = BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]);

			if (debug|localDebug) 
				System.out.println(	"TlvBuilder.isTLVConstruct() -> " +
									"Type: " + iType +			
									" Index: " +  iIndex + 
									" bTLV.length: " +  bTLV.length +
									" miiTlvTypeTpByteLength: " + miiTlvTypeTpByteLength);
			
			
			//Checks to see if there is a mapping, if one does not exists, it will assume that it is 1 byte Length
			if (miiTlvTypeTpByteLength.containsKey(iType)) {
				//Number of bytes for the Length of the TLV
				iNumOfByteLength = miiTlvTypeTpByteLength.get(iType);				
			} else {
				iNumOfByteLength = 1;
			}

						
			//Look for Next TLV Index
			try {
				iIndex = nextTLVIndex(bTLV, iIndex, iNumOfByteLength);
			} catch (TlvException e) {
				e.printStackTrace();
			}
	
			//Check for EOF for DOCSIS due to PADDING
			if ((iIndex < bTLV.length) && (BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]) == 255)) {
				
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.isTLVConstruct() -> FOUND-EOF");
				
				return true;
			
			//This index would be out of bound if past total TLV Length
			} else if (iIndex > bTLV.length) {
	
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.isTLVConstruct() -> " +
										" Index Exceeds Length" + " -> " +
										"iIndex: " +  iIndex + " -> " + 
										"bTLV.length: " +  bTLV.length);
				
				return false;
			
			//Just for DEBUG Purpose
			} else {
				
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.isTLVConstruct() -> iIndex: " +  iIndex + 
										" -> bTLV.length: " +  bTLV.length);
				
			}
			
		}
		
		return returnStatus;	
	}
	
	/**
	 * 
	 * @param bTLV
	 * @param miiTlvTypeTpByteLength
	 * @return boolean
	 */
	public static boolean isTLVConstruct (byte[] bTLV , Map<Integer,Integer> miiTlvTypeTpByteLength) {
		
		boolean localDebug = Boolean.FALSE;
		
		boolean returnStatus = true;
		
		int 	iIndex = 0 , 
				iType = 0,
				iNumOfByteLength = 0;
		
		//If the user does not want to provide a Map, it will assume that TLV is 1 byte encoded
		if (miiTlvTypeTpByteLength == null) {
			miiTlvTypeTpByteLength = new HashMap<Integer,Integer>();
		}
		
		//Check to see of there is any length
		if (bTLV.length == 0) {
			
			if (debug|localDebug) 
				System.out.println(	"TlvBuilder.isTLVConstruct() -> ByteArray is of 0 Length");
			
			return Boolean.FALSE;
		}
		
		//cycle thru Byte Array
		while (iIndex < bTLV.length) {
			
			//Current Type
			iType = BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]);

			if (debug|localDebug) 
				System.out.println(	"TlvBuilder.isTLVConstruct() -> " +
									"Type: " + iType +			
									" Index: " +  iIndex + 
									" bTLV.length: " +  bTLV.length +
									" miiTlvTypeTpByteLength: " + miiTlvTypeTpByteLength);
			
			
			//Checks to see if there is a mapping, if one does not exists, it will assume that it is 1 byte Length
			if (miiTlvTypeTpByteLength.containsKey(iType)) {
				//Number of bytes for the Length of the TLV
				iNumOfByteLength = miiTlvTypeTpByteLength.get(iType);				
			} else {
				iNumOfByteLength = 1;
			}

						
			//Look for Next TLV Index
			try {
				iIndex = nextTLVIndex(bTLV, iIndex, iNumOfByteLength);
			} catch (TlvException e) {
				e.printStackTrace();
			}
	
			//Check for EOF for DOCSIS due to PADDING
			if ((iIndex < bTLV.length) && (BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]) == 255)) {
				
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.isTLVConstruct() -> FOUND-EOF");
				
				return true;
			
			//This index would be out of bound if past total TLV Length
			} else if (iIndex > bTLV.length) {
	
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.isTLVConstruct() -> " +
										" Index Exceeds Length" + " -> " +
										"iIndex: " +  iIndex + " -> " + 
										"bTLV.length: " +  bTLV.length);
				
				return false;
			
			//Just for DEBUG Purpose
			} else {
				
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.isTLVConstruct() -> iIndex: " +  iIndex + 
										" -> bTLV.length: " +  bTLV.length);
				
			}
			
		}
		
		return returnStatus;	
	}	

	/**
	 * 
	 * @param iTlvType
	 * @param baValue
	 * @return byte[]
	 * @throws TlvException */
	public static byte[] encapsulateTlv(int iTlvType, byte[] baValue) throws TlvException {		

		if (debug)
			System.out.println("TlvBuilder.encapsulateTlv(i) " + "Type: " + iTlvType );
		
		if (iTlvType < 0)
			throw new TlvException("TlvBuilder.encapsulateTlv() Type Less than 0");

		if (iTlvType > 254)
			throw new TlvException("TlvBuilder.encapsulateTlv() Type Greater than 254");
		
		if (baValue == null)
			throw new TlvException("TlvBuilder.encapsulateTlv() - ByteArray is Null");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
		int iByteArrayLength = baValue.length;
		
		baos.write(iTlvType);baos.write(iByteArrayLength);
		
		try {
			baos.write(baValue);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return baos.toByteArray();
		
	}
	
	/**
	 * This method will remove any TLV that contain a Zero Byte Length
	 * 
	 * @param bTlvByteArray
	 * @param miiTypeToByteLength Map<Integer,Integer>
	 * @return byte[]
	 * @throws TlvException */
	public static byte[] stripZeroByteTLV(byte[] bTlvByteArray , Map<Integer,Integer> miiTypeToByteLength) throws TlvException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (bTlvByteArray ==  null)
			throw new TlvException("TlvBuilder.stripZeroByteTLV(): bTlvByteArray is NULL");

		if (bTlvByteArray.length < 3)
			throw new TlvException("TlvBuilder.stripZeroByteTLV(): ByteArray is less than 3 Bytes, not a TLV Encoded Array");
		
		ByteArrayOutputStream baosStripZeroByteTLV = new ByteArrayOutputStream();

		int iIndex 			=  0 , 
			iTypeLength 	= -1 ,
			iTypeFound		= -1 ,
			iTypeByteLength =  0;
		
		while (iIndex < bTlvByteArray.length) {
			
			//Current Type Found
			iTypeFound = BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]);
			
			//Determine the number of byte length
			if (miiTypeToByteLength.containsKey(iTypeFound)) {
				iTypeByteLength = miiTypeToByteLength.get(iTypeFound);
			} else {
				iTypeByteLength = ONE_BYTE_LENGTH;
			}
						
			//Look ahead to see if we exceed Byte Array Length
			if ((iIndex + TLV_LENGTH_POS_OFFSET) >= bTlvByteArray.length) {					
				
				if (debug|localDebug)
					System.out.println("TlvBuilder.stripZeroByteTLV(b,mii): - Look Ahead Exceeds Index");
				
				break;
			}
			
			//Check for 0 byte Length if so move to next TLV Index
			if (bTlvByteArray[iIndex+1] == ZERO_BYTE_LENGTH) {
	
				if (debug|localDebug)
					System.out.println(	"TlvBuilder.stripZeroByteTLV(b,mii) - Zero Byte Found" 	+
										" - TYPE: " 	+ iTypeFound 				+ 
										" - Length: " 	+ bTlvByteArray[iIndex+TLV_TYPE_OVERHEAD] 	+ 
										" - INDEX: " 	+ iIndex);

				
				//Advance to the next Type
				iIndex += TLV_TYPE_LENGTH_OVERHEAD;
				
				continue;
			}
			
			//Get Value Length
			iTypeLength = TlvBuilder.getTlvLength(bTlvByteArray, iIndex, iTypeByteLength);
			
			if (debug|localDebug)
				System.out.println(	"TlvBuilder.stripZeroByteTLV(b,mii)" +
									" - TYPE: " + iTypeFound +
									" - TypeLength: " + iTypeLength +
									" - Length: " + bTlvByteArray.length + 
									" - INDEX: " + iIndex + 
									" - TYPE-LENGTH: " + iTypeLength + 
									" - BAOS: " + baosStripZeroByteTLV.size() + " ----+");
			
			//Write TLV into ByteArray
			baosStripZeroByteTLV.write(bTlvByteArray, iIndex, (iTypeLength + iTypeByteLength + TLV_TYPE_OVERHEAD));
			
			//Goto Next TLV Index
			iIndex += (iTypeLength + iTypeByteLength + TLV_TYPE_OVERHEAD);

		}
		
		if (debug|localDebug)
			System.out.println(	"TlvBuilder.stripZeroByteTLV(b,mii): " + new HexString(baosStripZeroByteTLV.toByteArray()).toString(":"));
		
		return baosStripZeroByteTLV.toByteArray();
	}
	
	/**
	 * 
	 * @param ba
	 * @return - return in Hex TopLevel TLV Dump separated by newline
	 */
	public static String tlvDump(byte[] ba) {
		
		String sTlvDump = "";
		
		HexString hs = new HexString(ba);

		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(hs);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ArrayList<byte[]> alb = (ArrayList<byte[]>) tb.sortByTopLevelTlv();
		
		for (byte[] ba1 : alb) {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(ba1[0]);
			int iType = HexString.toInteger(baos.toByteArray());
			
			HexString hsTlv = new HexString(ba1);
			sTlvDump += ("[Type:"+iType+"] " + hsTlv.toString(":") + "\n");
		}
		
		return sTlvDump;
	}
	
	
	/* *****************************************************************************
	 * 							Private Methods
	 *******************************************************************************/
	/**
	 * 
	 * @param iType
	 * @param iTypeLength
	 */
 	private void updateMapTypeToByteLength(int iType , int iTypeLength) {
		
		boolean localDebug = Boolean.FALSE;
		
		if (localDebug|debug)
			System.out.println("TlvBuilder.updateMapTypeToByteLength(tb)");
		
		if (!miiTlvTypeTpByteLength.containsKey(iType)) {
			miiTlvTypeTpByteLength.put(iType, iTypeLength);
		}
		
	}
	
	/**
	 * 
	 * @param tbObject
	 */
	private void updateMapTypeToByteLength(TlvBuilder tbObject) {
		
		boolean localDebug = Boolean.FALSE;
		
		if (localDebug|debug)
			System.out.println("TlvBuilder.updateMapTypeToByteLength(tb)");
		
		HashMap<Integer,Integer> hmTlvTypeTpByteLength = new HashMap<Integer,Integer>();
		
		//Original Map
		hmTlvTypeTpByteLength.putAll(miiTlvTypeTpByteLength);
		
		//Overwrite Map
		hmTlvTypeTpByteLength.putAll(tbObject.getMapTypeToByteLength());
		
		//Update Map Pointer
		miiTlvTypeTpByteLength = hmTlvTypeTpByteLength;
	
		ArrayList<byte[]> albTlvBuffer = new ArrayList<byte[]>();
		albTlvBuffer.addAll(bTlvBuilder);		

		HashMap<Integer,Integer> hmiiTlvTypeToByteLength = new HashMap<Integer,Integer>();
		hmiiTlvTypeToByteLength.putAll(miiTlvTypeTpByteLength);
		
	}

	/**
	 * 
	 * @param hsTLV
	
	 * @return boolean
	 */
	private boolean isTLVConstruct (HexString hsTLV) {
		
		boolean localDebug = Boolean.FALSE;
		
		boolean returnStatus = true;
		
		byte[] bTLV = hsTLV.toByteArray();
		
		int 	iIndex = 0 , 
				iType = 0,
				iNumOfByteLength = 0;
		
		//Check to see of there is any length
		if (bTLV.length == 0) {
			
			if (debug|localDebug) 
				System.out.println(	"TlvBuilder.isTLVConstruct() -> ByteArray is of 0 Length");
			
			return Boolean.FALSE;
		}
		
		//cycle thru Byte Array
		while (iIndex < bTLV.length) {
			
			//Current Type
			iType = BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]);

			if (debug|localDebug) 
				System.out.println(	"TlvBuilder.isTLVConstruct() -> " +
									"Type: " + iType +			
									" Index: " +  iIndex + 
									" bTLV.length: " +  bTLV.length +
									" miiTlvTypeTpByteLength: " + miiTlvTypeTpByteLength);
			
			
			//Checks to see if there is a mapping, if one does not exists, it will assume that it is 1 byte Length
			if (miiTlvTypeTpByteLength.containsKey(iType)) {
				//Number of bytes for the Length of the TLV
				iNumOfByteLength = miiTlvTypeTpByteLength.get(iType);				
			} else {
				iNumOfByteLength = 1;
			}

						
			//Look for Next TLV Index
			try {
				iIndex = nextTLVIndex(bTLV, iIndex, iNumOfByteLength);
			} catch (TlvException e) {
				e.printStackTrace();
			}
	
			//Check for EOF for DOCSIS due to PADDING
			if ((iIndex < bTLV.length) && (BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]) == 255)) {
				
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.isTLVConstruct() -> FOUND-EOF");
				
				return true;
			
			//This index would be out of bound if past total TLV Length
			} else if (iIndex > bTLV.length) {
	
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.isTLVConstruct() -> " +
										" Index Exceeds Length" + " -> " +
										"iIndex: " +  iIndex + " -> " + 
										"bTLV.length: " +  bTLV.length);
				
				return false;
			
			//Just for DEBUG Purpose
			} else {
				
				if (debug|localDebug) 
					System.out.println(	"TlvBuilder.isTLVConstruct() -> iIndex: " +  iIndex + 
										" -> bTLV.length: " +  bTLV.length);
				
			}
			
		}
		
		return returnStatus;	
	}
}
