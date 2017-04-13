package com.comcast.oscar.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class HexString {
	//Log4J2 logging
    private static final Logger logger = LogManager.getLogger(HexString.class);

	private StringBuilder sbHexBuffer = new StringBuilder();
	
	private static final boolean debug = Boolean.FALSE;

	final static String HEX_NULL = "00";
	
	public static final Integer BITS_PER_BYTE 	= 8;
	
	public static final int HEX_STRING_1_BYTE_LENGTH = 2;

	/**
	 * Add as a ByteArray
	 * 
	 * @param bHexBuffer
	 */
	public HexString(byte[] bHexBuffer) {
		
		if (bHexBuffer != null) {
			if (bHexBuffer.length > 0)
				this.add(bHexBuffer);
		} else {
			throw new RuntimeException("HexString Construct Encounter a Null ByteArray");
		}
		
	}

	/**
	 * 
	 * Add a byte
	 * 
	 * @param bHexBuffer
	 */
	public HexString(byte bHexBuffer) {
		this.add(bHexBuffer);
	}
	
	/**
	 * 
	 * Add another HexString Object
	 * 
	 * @param hsObject
	 */
	public HexString(HexString hsObject) {
		if (hsObject.length() > 0)
			this.add(hsObject.toByteArray());
	}

	/**
	 * 
	 */
	public HexString() {
		super();
	}

	/**
	 * Clears Object Buffer
	 * 
	 */
	public void clear () {
		//Create a new StringBuilder
		sbHexBuffer = new StringBuilder();		
	}

	/**
	 * Checks to see if buffer is empty
	
	 * @return boolean
	 */
	public boolean isEmpty() {
		if (sbHexBuffer.length() == 0)
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}
	
	/**
	 * 
	 * Add integer
	 * 
	 * @param iValue
	
	 * @return boolean
	 */
	public boolean add(Integer iValue) {

		if (isModulus(Integer.toHexString(iValue).toUpperCase())) {
			sbHexBuffer.append(Integer.toHexString(iValue).toUpperCase());
		} else {
			sbHexBuffer.append("0" + Integer.toHexString(iValue).toUpperCase());	
		}

		return isModulus();
	}

	/**
	 * 
	 * Add String and appends a NULL Terminated byte 0x00
	 * 
	 * @param sValue
	 * @param boolIncludeTerminationNull
	
	 * @return boolean
	 */
	public boolean add(String sValue, boolean boolIncludeTerminationNull) {

		for (int index=0; index < sValue.length(); index++) {
			sbHexBuffer.append(Integer.toHexString(sValue.charAt(index)));
		}

		if (boolIncludeTerminationNull) {
			sbHexBuffer.append("00");	
		}

		return isModulus();
	}

	/**
	 * 
	 * Add ASCII String
	 * 
	 * @param sValue
	
	 * @return boolean
	 */
	public boolean add(String sValue) {
		//Add String as ByteArray
		add(sValue.getBytes());
		
		return isModulus();
	}

	/**
	 * 
	 * Add ByteArray
	 * 
	 * @param bValue
	
	 * @return boolean
	 */
	public boolean add(byte[] bValue) {

		for (byte bByte : bValue) {
			sbHexBuffer.append(String.format("%02X ", bByte));
		}

		return isModulus();
	}

	/**
	 * 
	 * Add Single Byte
	 * 
	 * @param bValue
	
	 * @return boolean
	 */
	public boolean add(byte bValue) {

		sbHexBuffer.append(String.format("%02X ", bValue));

		return isModulus();
	}
	
	/**
	 * 
	
	 * @return  HexString  */
	@Override
	public String toString() {
		return sbHexBuffer.toString();
	}

	/**
	 * 
	 * @param sSeperation
	
	 * @return the hex string with a separation character ':'  i.e.: xx:xx:xx */
	public String toString(String sSeperation) {
		return sbHexBuffer.toString().trim().replace(" ", sSeperation);
	}
	
	/**
	 * 
	
	 * @return Hex String with no seperation charcter */
	public String hexCompressed() {
		return sbHexBuffer.toString().replace(" ", "");
	}

	/**
	 * 
	 * @param sDelimter
	
	 * @return String
	 */
	public String hexDelimited(String sDelimter) {
		return sbHexBuffer.toString();
	}

	/**
	 * 
	
	 * @return ArrayList<String>
	 */
	public ArrayList<String> toHexStringList () {
		
		ArrayList<String> alTlvHexBuffer = new ArrayList<String>();
	    
	    for (byte b : toByteArray()) 
	    	alTlvHexBuffer.add(String.format("%02X ", b));	    	

		return alTlvHexBuffer;
	}

	/**
	 * 
	
	 * @return byte[]
	 */
	public byte[] toByteArray() {
	     HexBinaryAdapter adapter = new HexBinaryAdapter();    
	     return adapter.unmarshal(this.hexCompressed());	
	}
	
	/**
	 * 
	
	 * @return int
	 */
	public int length() {
		return toByteArray().length;
	}
	
	/**
	 * 
	
	 * @return String
	 */
	public String toASCII () {
		return new String(toByteArray()).toString();
	}
	
	/**
	 * 
	 * @return Integer of the first 4 bytes of the ByteArray*/
	public int toInteger () {
		
		ByteArrayOutputStream  baosByteToInt = new ByteArrayOutputStream();
				
		while ((baosByteToInt.size()+toByteArray().length) < 4) {
			baosByteToInt.write(0x00);
		}

		try {
			baosByteToInt.write(toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		byte[] bByteToInt = baosByteToInt.toByteArray();
		
		return 	bByteToInt[0] << 24 | 
				(bByteToInt[1] & 0xFF) << 16 | 
				(bByteToInt[2] & 0xFF) << 8 | 
				(bByteToInt[3] & 0xFF);	
	}
	
	/**
	 * 
	
	 * @return the binary representation of the HEX String */
	public String toAsciiBinary () {
	    		
		StringBuilder sbAsciiBinary = new StringBuilder(toByteArray().length * Byte.SIZE);
	    
		for(int iIndex = 0; iIndex < Byte.SIZE * toByteArray().length; iIndex++)
			sbAsciiBinary.append((toByteArray()[iIndex / Byte.SIZE] << iIndex % Byte.SIZE & 0x80) == 0 ? '0' : '1');
	    
		return sbAsciiBinary.toString();
	}
	
	/**
	 * 
	 * @param iTotalLength
	
	 * @return int
	 */
	public int prefixNullPaddToLength (int iTotalLength) {
		
		ByteArrayOutputStream  baosByteArray = new ByteArrayOutputStream();
		
		if (debug) 
			logger.debug("HexString.prefixNullPaddToLength().itotalLength: " + iTotalLength);
		
		//PADD Nulls in first for prefix
		while ((toByteArray().length + baosByteArray.size()) < iTotalLength) {
			baosByteArray.write(0x00);
		}
		
		//Write in byte array
		try {
			baosByteArray.write(toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Clear current array
		clear();
		
		//Replace with new byte Array
		add(baosByteArray.toByteArray());
		
		//return new length
		return length();
	}

	/**
	 * 
	 * @param iTotalLength
	
	 * @return int
	 */
	public int suffixNullPaddToLength (int iTotalLength) {
		ByteArrayOutputStream  baosByteArray = new ByteArrayOutputStream();

		//Write in byte array
		try {
			baosByteArray.write(toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Add PADD to the total Byte Array
		while ((toByteArray().length + baosByteArray.size()) < iTotalLength) {
			baosByteArray.write(0x00);
		}
			
		//Clear current array
		clear();
		
		//Replace with new byte Array
		add(baosByteArray.toByteArray());
		
		//return new length
		return length();
	}
	
	/* ***********************************************************************
	 * 						Static Methods
	 *************************************************************************/
	/**
	 * Method toHexString.
	 * @param iValue Integer
	 * @return String
	 */
	public static String toHexString(Integer iValue) {

		if (isModulus(Integer.toHexString(iValue).toUpperCase())) {
			return Integer.toHexString(iValue).toUpperCase();
		} else {
			return "0" + Integer.toHexString(iValue).toUpperCase();	
		}

	}

	/**
	 * 
	 * @param sValue
	
	 * @return String
	 */
	public static String toHexString(String sValue) {

		StringBuffer stringBuffer = new StringBuffer();

		for (int index=0; index < sValue.length(); index++) {
			stringBuffer.append(Integer.toHexString(sValue.charAt(index)));
		}

		return stringBuffer.toString().toUpperCase();
	}

	/**
	 * 
	 * @param sHexString
	
	 * @return Integer
	 */
	public static Integer getHexByteLength (String sHexString) {
		return sHexString.length()/2;
	}
	
	/**
	 * 
	 * @param sHexString
	
	 * @return byte[]
	 */
	public static byte[] toByteArray(String sHexString) {
		
		//Need to add HexString Check	
		sHexString = sHexString	.replaceAll("(\\]|\\)|\\}|\\(|\\[|\\{)", "")
								.replace(":", "");
		
		HexBinaryAdapter adapter = new HexBinaryAdapter();	     
	     
	     if (isModulus(sHexString.replace(" ", "")))
	    	 return adapter.unmarshal(sHexString.replace(" ", ""));
	     else
	    	 return new HexString().toByteArray();
	}

	/**
	 * 
	 * @param sHexString
	
	 * @return boolean
	 */
	public static boolean isHexString (String sHexString) {		

		//Need to clean up	
		sHexString = sHexString	.replaceAll("(\\]|\\)|\\}|\\(|\\[|\\{)", "")
								.replace(":", "");
		
		if (isModulus(sHexString))
			return sHexString.matches("\\p{XDigit}+");
		else
			return false;
	}
	
	/**
	 * 
	 * @param filePointer
	
	 * @return byte[]
	 */
 	public static byte[] fileToByteArray(File filePointer) {
		
       byte[] bFileBuffer = new byte[(int) filePointer.length()];
       
       FileInputStream fisFileBuffer = null;
       
		try {
			fisFileBuffer = new FileInputStream(filePointer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			fisFileBuffer.read(bFileBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		try {
			fisFileBuffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bFileBuffer;
	}
 	
 	/**
 	 * 
 	 * @param filePointer
 	
 	 * @return InputStream
 	 */
 	public static InputStream fileToInputStream(File filePointer) {
 		
        byte[] bFileBuffer = new byte[(int) filePointer.length()];
        
        InputStream isFileBuffer = null;
        
 		try {
 			isFileBuffer = new FileInputStream(filePointer);
 		} catch (FileNotFoundException e) {
 			e.printStackTrace();
 		}
 		
 		try {
 			isFileBuffer.read(bFileBuffer);
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 		
 		return isFileBuffer;
 		
 	}
 		
	/**
	 * 
	 * @param bInetAddress
	
	 * @return String
	 */
 	public static String toInetAddress (byte[] bInetAddress) {

		InetAddress inetIpAddress = null;

		try {
			inetIpAddress = InetAddress.getByAddress(bInetAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return inetIpAddress.toString().replace("/", "");
	}

	/**
	 * 
	 * @param sAscii
	
	 * @return String
	 */
	public static String asciiToHex (String sAscii) {
		return new HexString(sAscii.getBytes()).hexCompressed();
	}

	/**
	 * 
	 * @param sAscii
	 * @param sSeperation
	
	 * @return String
	 */
	public static String asciiToHex (String sAscii, String sSeperation) {
		return new HexString(sAscii.getBytes()).toString(sSeperation);
	}
	
	/**
	 * Convert Integer to a ByteArray of a Max of 4 bytes
	 * @param iNumber
	
	 * @return byte[] */
	public static byte[] intToByteArray (int iNumber) {
		
		HexString hsInteger = new HexString();
		
		hsInteger.add(iNumber);
		
		return hsInteger.toByteArray();
	}
	
	/**
	 * 
	 * @param bString
	
	 * @return byte[]
	 */
	public static byte[] stripNullTerminatedString (byte[] bString)  {
		
		if (bString.length == 0) 
			return bString;
		
		ByteArrayOutputStream  baosString = new ByteArrayOutputStream();
		
		baosString.write(bString, 0, bString.length -1);
		
		return baosString.toByteArray();		
	}
		
	/**
	 * Get the ByteArray of an InetAddress IPv4 or IPv6
	 * @param sInetAddress
	
	 * @return byte[]
	 */
	public static byte[] inetAddressToByteArray (String sInetAddress) {
		
	    InetAddress iaInetAddress = null;
	    
		try {
			iaInetAddress = InetAddress.getByName(sInetAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	    
	    return iaInetAddress.getAddress();
	    
	}

	/**
	 * 
	 * @param bytes
	 * @return String Binary representation of the Byte Array*/
	public static String toAsciiBinary (byte[] bytes) {
	    
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
	    
		for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
	        sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
	    
		return sb.toString();
	}
	
	/**
	 * 
	 * @param bByteArray
	 * @param iNumBytesInGroup
	
	 * @return List<byte[]>
	 */
	public static List<byte[]> byteArrayGroup (byte[] bByteArray, int iNumBytesInGroup) {
		
		boolean localDebug = Boolean.FALSE;
		
		List<byte[]> lbByteArrayGroup = new ArrayList<byte[]>();
		
		ByteArrayOutputStream  baosByteArrayGroup = new ByteArrayOutputStream();
		
		int iIndex = 0;
		int iNumBytesInGroupCount = 0;
		
		while (iIndex < bByteArray.length) {
			
			if (debug|localDebug) {
				logger.debug(	"HexString.byteArrayGroup() -> " +
									"iIndex: " + iIndex + " -> " +
									"iNumBytesInGroupCount: " + iNumBytesInGroupCount);
			}
			
			if (iNumBytesInGroup != iNumBytesInGroupCount) {
				baosByteArrayGroup.write(bByteArray[iIndex++]);
				iNumBytesInGroupCount++;
			} else {
				lbByteArrayGroup.add(baosByteArrayGroup.toByteArray());
				baosByteArrayGroup = new ByteArrayOutputStream();
				iNumBytesInGroupCount = 0;
			}
			
		}
		
		lbByteArrayGroup.add(baosByteArrayGroup.toByteArray());
		
		if (debug|localDebug) {
			logger.debug(	"HexString.byteArrayGroup() -> " +
								"lbByteArrayGroup.List.Size: " + lbByteArrayGroup.size());
		}
		
		return lbByteArrayGroup;
	}
	
	/**
	 *
	 * Need to make sure that CR,NL and TABS are respected
	 * 
	 * 0x0d = CR
	 * 0x0a = NL
	 * 0x09 = TAB
	 * 
	 * @param bByteArray
	
	 * @return true is no NON text Char is found with exception of white space */
	public static boolean verifyAsciiPlainText (byte[] bByteArray) {
				
		for (byte bByte : bByteArray) {
						
			if ((bByte < 0x20) || (bByte > 0x7e)) {
				
				/*
				 * Need to make sure that CR,NL and TABS are respected
				 * 
				 * 0x0d = CR
				 * 0x0a = NL
				 * 0x09 = TAB
				 * 
				 */
				
				if ((bByte != 0x0d) && (bByte != 0x0a) && (bByte != 0x09)) {
					return false;
				}
				
			}
		}
			
		return true;
	}
	
	/**
	 * 
	 * @param bByteArray
	
	 * @return boolean
	 */
	public static boolean containAsciiWhiteSpace (byte[] bByteArray) {
		
		for (byte bByte : bByteArray) 
			if ((bByte == 0x0d) || (bByte == 0x0a) || (bByte == 0x09) || (bByte == 0x20)) {return true;}
			
		return false;
	}
	
	/**
	 * 
	 * @param ba ByteArray
	 * @return Integer of the first 4 bytes of the ByteArray*/
	public static int toInteger (byte[] ba) {
		
		ByteArrayOutputStream  baosByteToInt = new ByteArrayOutputStream();
				
		while ((baosByteToInt.size()+ba.length) < 4) {
			baosByteToInt.write(0x00);
		}

		try {
			baosByteToInt.write(ba);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		byte[] bByteToInt = baosByteToInt.toByteArray();
		
		return 	bByteToInt[0] << 24 | 
				(bByteToInt[1] & 0xFF) << 16 | 
				(bByteToInt[2] & 0xFF) << 8 | 
				(bByteToInt[3] & 0xFF);	
	}
	
	/* ********************************************************************
	 *  							Private Methods 
	 * ********************************************************************/

	/**
	 * 
	
	 * @return boolean
	 */
 	private boolean isModulus() {

 		boolean boolIsMod = false;
 		
		//Check modulus for even pair
		if ((sbHexBuffer.length() % 2) == 0) {
			boolIsMod = true;
		} 
		
		return boolIsMod;

	}

 	/**
 	 * 
 	 * @param sHexString
 	
 	 * @return boolean
 	 */
	private static boolean isModulus(String sHexString) {

		boolean boolIsMod = false;
		
		//Some Clean up in case it is separated by spaces and colons
		String sRegex = "//s+|/:";
		String sReplacement = "";

		sHexString.replaceAll(sRegex, sReplacement);

		//Check modulus for even pair
		if ((sHexString.length() % 2) == 0) {
			boolIsMod = true;
		}
		
		return boolIsMod;
	}

}
