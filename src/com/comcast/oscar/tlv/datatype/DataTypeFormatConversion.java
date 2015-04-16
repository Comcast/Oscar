package com.comcast.oscar.tlv.datatype;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snmp4j.smi.OID;

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

public class DataTypeFormatConversion {

	
	public static final Integer IPV4_ADDRESS_BYTE_LENGTH = 4;
	public static final Integer IPV6_ADDRESS_BYTE_LENGTH = 16;
	public static final Integer IP_PORT_BYTE_LENGTH = 2;
	public static final Integer MAC_ADDRESS_LENGTH = 6;
	public static final Integer INET_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH = (IPV6_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH);
	public static final Integer IPV6_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH = (IPV6_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH);
	public static final Integer IPV4_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH = (IPV4_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH);
	
	//public static String IP_PORT_RANGE = "(6553[0-5]|655[0-2][0-9]\\d|65[0-4](\\d){2}|6[0-4](\\d){3}|[1-5](\\d){4}| {0,3})";	
	public static final String IP_PORT_RANGE = "([0-9]|6[0-4](\\d){3}|65[0-4](\\d){2}|655[0-2][0-9]\\d|6553[0-5])";

	public static boolean debug = Boolean.FALSE;
	
	//http://www.pretechsol.com/2013/05/ipv6-java-regular-expression-example.html#.UnujD_lwp8E
    public static final Pattern IPV6_ADDRESS_REGEX = Pattern.compile(""
            + "(^(((?=(?>.*?::)(?!.*::)))(::)?([0-9A-F]{1,4}::?){0,5}"
            + "|([0-9A-F]{1,4}:){6})(\\2([0-9A-F]{1,4}(::?|$)){0,2}|((25[0-5]"
            + "|(2[0-4]|1\\d|[1-9])?\\d)(\\.|$)){4}|[0-9A-F]{1,4}:[0-9A-F]{1,"
            + "4})(?<![^:]:|\\.)\\z)", Pattern.CASE_INSENSITIVE);

    public static final Pattern IPV6_TRANSPORT_REGEX = Pattern.compile(""
            + "(^(((?=(?>.*?::)(?!.*::)))(::)?([0-9A-F]{1,4}::?){0,5}"
            + "|([0-9A-F]{1,4}:){6})(\\2([0-9A-F]{1,4}(::?|$)){0,2}|((25[0-5]"
            + "|(2[0-4]|1\\d|[1-9])?\\d)(\\.|$)){4}|[0-9A-F]{1,4}:[0-9A-F]{1,"
            + "4})(?<![^:]:|\\.))"
            + 	"\\(("+ IP_PORT_RANGE + ")\\)", Pattern.CASE_INSENSITIVE);

    public static final Pattern IPV4_ADDRESS_REGEX = Pattern.compile(""
    		+ "(^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
    		+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
    		+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
    		+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$)");
    
    public static final Pattern IPV4_TRANSPORT_REGEX = Pattern.compile(""
    		+ "(^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
    		+   "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
    		+   "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
    		+   "([01]?\\d\\d?|2[0-4]\\d|25[0-5]))" 
    		+ 	"\\(("+ IP_PORT_RANGE + ")\\)");
   
    
    /**
     * 
     * Format: (0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)....
     * 
     */
    public static final Pattern DOUBLE_BYTE_ARRAY_REGEX = Pattern.compile(""
    		+ "(\\(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\,([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\))+");

    /**
     * Format: (01:FF:FF:FF:FF:FF)(02:FF:FF:FF:FF:FF)(03:FF:FF:FF:FF:FF)....
     */
    public static final Pattern MAC_ADDRESS_REGEX = Pattern.compile(""
    		+ "(" +
    			"(" +
    				"\\(([0-9A-F]{2}[\\.:-]){5}" +
    				"([0-9A-F]{2})" +
    				"(\\)))+)"
    			, Pattern.CASE_INSENSITIVE);
 
    public static final String DOUBLE_BYTE_ARRAY_FORMAT = "(0,0)(0,0)(0,0)(0,0) = (Integer < 256,Integer < 256)(Integer < 256,Integer < 256)....";
    public static final String IPV4_TRANSPORT_FORMAT 	= "0.0.0.0(0) = IPv4_Address(Port)";
    public static final String IPV6_TRANSPORT_FORMAT 	= "XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX(0) = IPv6_Address(Port)";
    public static final String STRING_BITS_FORMAT 		= "(00000000)(11111111)(00000000)(11111111) = 8 Bits Per Byte = 00:FF:00:FF";
    public static final String IPV6_ADDRESS_FORMAT 		= "XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX = IPv6 Address";
    public static final String IPV4_ADDRESS_FORMAT 		= "0.0.0.0 = IPv4 Address";
    public static final String MAC_ADDRESS_FORMAT		= "(xx:xx:xx:xx:xx:xx)";
    
	/**
	 * 
	 * Expected Format:
	 * 
	 * 		XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX(PORT ADDRESS)
	 * 
	
	
	 * @param bIPv6TransportAddress byte[]
	 * @return String
	 * @throws DataTypeFormatException
	 */
 	public static String byteArrayToIPv6TransportAddress (byte[] bIPv6TransportAddress) throws DataTypeFormatException {
		
		boolean localDebug = true;
		
		if (bIPv6TransportAddress.length != (IPV6_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH)) {
			 throw new DataTypeFormatException ("Invalid bIpv6TransportAddress Byte Length");
		}
		
		ByteArrayOutputStream baosIpv6Address = new ByteArrayOutputStream();
		
		baosIpv6Address.write(bIPv6TransportAddress, 0, IPV6_ADDRESS_BYTE_LENGTH);
		
		String sIPv6Address = HexString.toInetAddress(baosIpv6Address.toByteArray());
		
		ByteArrayOutputStream baosTransport = new ByteArrayOutputStream();
		
		baosTransport.write(bIPv6TransportAddress, IPV6_ADDRESS_BYTE_LENGTH+1, IP_PORT_BYTE_LENGTH);
	
		int iIpPort = new HexString(baosTransport.toByteArray()).toInteger();
		
		String sIPv6TransportAddress = sIPv6Address+"(" + iIpPort + ")";
		
		if (debug|localDebug) {
			System.out.println("DataTypeFormatConversion.byteArrayToIpv6TransportAddress() : " + sIPv6TransportAddress);
		}
		
		return sIPv6TransportAddress;
		
	}
	
	/**
	 * 
	 * Convert : XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX(PORT ADDRESS) to ByteArray
	 * 
	
	
	 * @param sIPv6TransportAddress String
	 * @return byte[]
	 */
	public static byte[] ipv6TransportAddressToByteArray (String sIPv6TransportAddress) {
		
		ByteArrayOutputStream baosIPv6TransportAddress = new ByteArrayOutputStream();
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("DataTypeFormatConversion.IPv6TransportAddressToByteArray() -> sIPv6TransportAddress: " + sIPv6TransportAddress);
		}
		
		if (verifyIPv6TransportAddressFormat(sIPv6TransportAddress)) {
			
			Matcher mIPv6TransportAddress =  IPV6_TRANSPORT_REGEX.matcher(sIPv6TransportAddress);
			
			if (mIPv6TransportAddress.find()) {
				
				if (debug|localDebug) {
					for (int groupCount = 0 ; groupCount < mIPv6TransportAddress.groupCount() ; groupCount++)				
						System.out.println("Group:" + groupCount + " Found value: -" + mIPv6TransportAddress.group(groupCount) + "-");
				}
				
			}
			
			/* ***************************************************
			 *					Group 1 = IP Address
			* ****************************************************/
			try {
				baosIPv6TransportAddress.write(HexString.inetAddressToByteArray(mIPv6TransportAddress.group(1)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/* ***************************************************
			 *					Group 14 = Port Address
			* ****************************************************/
			// MUST END WITH 2 BYTES
			HexString hsInteger = new HexString();
			
			//Clean up response
			hsInteger.add(Integer.parseInt(mIPv6TransportAddress.group(14).replaceAll("\\(|\\)|\\s+", "")));
			hsInteger.prefixNullPaddToLength(IP_PORT_BYTE_LENGTH);
						
			try {
				baosIPv6TransportAddress.write(hsInteger.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	
		if (debug|localDebug) {
			System.out.println("DataTypeFormatConversion.IPv6TransportAddressToByteArray() -> " 
								+ "Input: " + sIPv6TransportAddress + " -> "
								+ "HEX: " + new HexString(baosIPv6TransportAddress.toByteArray()).toString(":"));			
		}
		
		return baosIPv6TransportAddress.toByteArray();
		
	}
	
	/**
	 * 
	 * @param sIPv6TransportAddress
	
	 * @return boolean
	 */
	public static boolean verifyIPv6TransportAddressFormat (String sIPv6TransportAddress) {
	
		boolean localDebug = Boolean.FALSE;
			
		if (IPV6_TRANSPORT_REGEX.matcher(sIPv6TransportAddress).matches()) {
			if (debug|localDebug) {
				System.out.println("DataTypeFormatConversion.verifyIPv6TransportAddressFormat() -> GOOD: " + sIPv6TransportAddress);
			}
			
			return true;
		} else {
			
			if (debug|localDebug) {
				System.out.println("DataTypeFormatConversion.verifyIPv6TransportAddressFormat() -> BAD: " + sIPv6TransportAddress);
			}
			
			return false;
		}
		
	}
		
	/**
	 * 
	 * Expected Format:
	 * 
	 * 		XXX.XXX.XXX.XXX(PORT ADDRESS)
	 * 
	
	
	 * @param bIPv4TransportAddress byte[]
	 * @return String
	 * @throws DataTypeFormatException
	 */
	public static String byteArrayToIPv4TransportAddress (byte[] bIPv4TransportAddress) throws DataTypeFormatException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (bIPv4TransportAddress.length != (IPV4_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH)) {
			 throw new DataTypeFormatException ("Invalid bIPv4TransportAddress Byte Length");
		}
		
		ByteArrayOutputStream baosIPv4Address = new ByteArrayOutputStream();
		
		baosIPv4Address.write(bIPv4TransportAddress, 0, IPV4_ADDRESS_BYTE_LENGTH);
		
		String sIPv4Address = HexString.toInetAddress(baosIPv4Address.toByteArray());
		
		ByteArrayOutputStream baosTransport = new ByteArrayOutputStream();
		
		baosTransport.write(bIPv4TransportAddress, IPV4_ADDRESS_BYTE_LENGTH+1, IP_PORT_BYTE_LENGTH);
	
		int iIpPort = new HexString(baosTransport.toByteArray()).toInteger();
		
		String sIPv4TransportAddress = sIPv4Address+"(" + iIpPort + ")";
		
		if (debug|localDebug) {
			System.out.println("DataTypeFormatConversion.byteArrayToIpv6TransportAddress() : " + sIPv4TransportAddress);
		}
		
		return sIPv4TransportAddress;
	}

	/**
	 * 
	 * @param sIpv4TransportAddress
	
	 * @return byte[]
	 */
	public static byte[] ipv4TransportAddressToByteArray (String sIpv4TransportAddress) {
		
		ByteArrayOutputStream baosIPv4TransportAddress = new ByteArrayOutputStream();
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("DataTypeFormatConversion.ipv4TransportAddressToByteArray() -> sIpv4TransportAddress: " + sIpv4TransportAddress);
		}
		
		if (verifyIPv4TransportAddressFormat(sIpv4TransportAddress)) {
			
			Matcher mIPv4TransportAddress =  IPV4_TRANSPORT_REGEX.matcher(sIpv4TransportAddress);
			
			if (mIPv4TransportAddress.find()) {
				
				if (debug|localDebug) {
					for (int groupCount = 0 ; groupCount < mIPv4TransportAddress.groupCount() ; groupCount++)				
						System.out.println("Group:" + groupCount + " Found value: -" + mIPv4TransportAddress.group(groupCount) + "-");
				}
				
			}
			
			//Group 1 = IPv6 Address
			try {
				baosIPv4TransportAddress.write(HexString.inetAddressToByteArray(mIPv4TransportAddress.group(1)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//Group 6 = Port Address
			
			HexString hsInteger = new HexString();		
			hsInteger.add(Integer.parseInt(mIPv4TransportAddress.group(6).replaceAll("\\(|\\)|\\s+", "")));
			hsInteger.prefixNullPaddToLength(IP_PORT_BYTE_LENGTH);
			
			try {
				baosIPv4TransportAddress.write(hsInteger.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}			
		} else {
			
		}
	
		if (debug|localDebug) {
			System.out.println("DataTypeFormatConversion.IPv4TransportAddressToByteArray() -> " 
								+ "Input: " + sIpv4TransportAddress + " -> "
								+ "HEX: " + new HexString(baosIPv4TransportAddress.toByteArray()).toString(":"));			
		}
		
		return baosIPv4TransportAddress.toByteArray();
	}

	/**
	 * 
	 * @param bInetTransportAddress
	
	
	 * @return String
	 * @throws DataTypeFormatException */
	public static String byteArrayToInetTransportAddress (byte[] bInetTransportAddress) throws DataTypeFormatException  {
		
		boolean localDebug = Boolean.FALSE;
		
		String sInetAddress = null;
		
		String sInetTransportAddress = null;
		
		ByteArrayOutputStream baosInetAddress = new ByteArrayOutputStream();
		
		ByteArrayOutputStream baosTransport = new ByteArrayOutputStream();
		
		if (bInetTransportAddress.length == IPV6_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH) {
			
			baosInetAddress.write(bInetTransportAddress, 0, IPV6_ADDRESS_BYTE_LENGTH);
					
			sInetAddress = HexString.toInetAddress(baosInetAddress.toByteArray());
			
			baosTransport.write(bInetTransportAddress, IPV4_ADDRESS_BYTE_LENGTH+1, IP_PORT_BYTE_LENGTH);
			
		} else if (bInetTransportAddress.length == IPV4_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH) {
			
			baosInetAddress.write(bInetTransportAddress, 0, IPV4_ADDRESS_BYTE_LENGTH);	
					
			sInetAddress = HexString.toInetAddress(baosInetAddress.toByteArray());
			
			baosTransport.write(bInetTransportAddress, IPV4_ADDRESS_BYTE_LENGTH, IP_PORT_BYTE_LENGTH);
		} else {
			throw new DataTypeFormatException (	
												"Invalid InetTransportAddress Byte Length: " + bInetTransportAddress.length +
												" -> Hex: " + new HexString(bInetTransportAddress).toString(":")
												);
		}
					
		int iIpPort = new HexString(baosTransport.toByteArray()).toInteger();
				
		sInetTransportAddress = sInetAddress + "(" + iIpPort + ")";
		
		if (debug|localDebug) {
			System.out.println("DataTypeFormatConversion.byteArrayToIpv6TransportAddress() : " + sInetTransportAddress);
		}
		
		return sInetTransportAddress;
	}

	/**
	 * 
	 * @param sInetTransportAddress
	
	 * @return byte[]
	 */
	public static byte[] inetTransportAddressToByteArray (String sInetTransportAddress) {
		
		boolean localDebug = Boolean.FALSE;
		
		if (verifyIPv6TransportAddressFormat(sInetTransportAddress)) {
			
			return ipv6TransportAddressToByteArray(sInetTransportAddress);
		
		} else if (verifyIPv4TransportAddressFormat(sInetTransportAddress)) {
		
			return ipv4TransportAddressToByteArray(sInetTransportAddress);
		
		} else {
			if (debug|localDebug) {
				System.out.println("DataTypeFormatConversion.inetTransportAddressToByteArray() IncorrectFormat: " + sInetTransportAddress);
			}
		}
		
		return new byte[] {};
			
	}

	/**
	 * 
	 * @param sIPv4TransportAddress
	
	 * @return boolean
	 */
	public static boolean verifyIPv4TransportAddressFormat (String sIPv4TransportAddress) {
		
		boolean localDebug = Boolean.FALSE;
			
		if (IPV4_TRANSPORT_REGEX.matcher(sIPv4TransportAddress).matches()) {
			if (localDebug) {
				System.out.println("DataTypeFormatConversion.verifyIPv4TransportAddressFormat() -> GOOD: " + sIPv4TransportAddress);
			}
			
			return true;
			
		} else {
			
			if (debug|localDebug) {
				System.out.println("DataTypeFormatConversion.verifyIPv4TransportAddressFormat() -> BAD: " + sIPv4TransportAddress);
			}
			
			return false;
		}
		
	}
	
	/**
	 * 
	 * Format: (0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)....
	 * 
	
	
	
	 * @param bDoubleByteArray byte[]
	 * @return String
	 * @throws IllegalArgumentException */
	public static String doubleByteArray (byte[] bDoubleByteArray) throws IllegalArgumentException {
		
		if ((bDoubleByteArray.length %2) != 0) {
			throw new IllegalArgumentException("DataTypeFormatConversion.doubleByteArray() ByteArray Must be a Multiple of 2 Bytes");
		}
		
		String sDoubleByteArray = "(";
		
		for (int iIndex = 0 ; iIndex < bDoubleByteArray.length; iIndex++) {
			
			sDoubleByteArray += BinaryConversion.byteToUnsignedInteger(bDoubleByteArray[iIndex]);
						
			if ((iIndex %2) == 0) {
				sDoubleByteArray += ",";
			} else if (iIndex == (bDoubleByteArray.length-1)) {
				
			}  else if ((iIndex %1) == 0) {
				sDoubleByteArray += ")(";
			}
				
		}
		
		sDoubleByteArray += ")";
		
		return sDoubleByteArray;
	}

	/**
	 * 
	 * Format: (0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)....
	 * 
	 * @param sDoubleByteArray
	
	
	 * @return byte[]
	 * @throws IllegalArgumentException */
	public static byte[] doubleByteArray (String sDoubleByteArray) throws IllegalArgumentException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (!doubleByteArrayValidate(sDoubleByteArray)) {
			throw new IllegalArgumentException("DataTypeFormatConversion.doubleByteArray() Invalid String Format: " + sDoubleByteArray);	
		}
		
		sDoubleByteArray = sDoubleByteArray	.replaceAll("\\)\\("  , ",")
											.replaceAll("\\)|\\(" , "");
	
		ByteArrayOutputStream baosDoubleByteArray = new ByteArrayOutputStream();
		
		if (debug|localDebug) {
			System.out.println("DataTypeFormatConversion.doubleByteArray() : " + sDoubleByteArray);
		}
		
		for (String sNumber : sDoubleByteArray.split(",")) {
	
			if (debug|localDebug) {
				System.out.println("DataTypeFormatConversion.doubleByteArray() : " 
						+ sNumber + " -> " + new HexString(HexString.intToByteArray(Integer.parseInt(sNumber))).toString(":")
						+ " -> " + new HexString(HexString.intToByteArray(Integer.parseInt(sNumber))).toAsciiBinary());
			}
			
			try {
				baosDoubleByteArray.write(HexString.intToByteArray(Integer.parseInt(sNumber)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return baosDoubleByteArray.toByteArray();
	}
	
	/**
	 * 
	 * @param sDoubleByteArray
	
	 * @return boolean
	 */
	public static boolean doubleByteArrayValidate (String sDoubleByteArray) {
		
		boolean localDebug = Boolean.FALSE;

		Matcher mDoubleByteArray =  DOUBLE_BYTE_ARRAY_REGEX.matcher(sDoubleByteArray);
		
		if (mDoubleByteArray.find()) {
			
			if (debug|localDebug) {
				for (int groupCount = 0 ; groupCount < mDoubleByteArray.groupCount() ; groupCount++)				
					System.out.println("Group:" + groupCount + " Found value: -" + mDoubleByteArray.group(groupCount) + "-");
			}
			
		}
		
		if (DOUBLE_BYTE_ARRAY_REGEX.matcher(sDoubleByteArray).matches()) {
			if (debug|localDebug) {
				System.out.println("DataTypeFormatConversion.doubleByteArrayValidate() -> GOOD: " + sDoubleByteArray);
			}
			
			return true;
		} else {
			
			if (debug|localDebug) {
				System.out.println("DataTypeFormatConversion.doubleByteArrayValidate() -> BAD: " + sDoubleByteArray);
			}
			
			return false;
		}
	
	}
	
	/**
	 * 
	 * @param sBinaryBitMask
	
	 * @return byte[]
	 */
	public static byte [] binaryBitMaskToByteArray (String sBinaryBitMask) {		
		return BinaryConversion.binaryMSBMaskToByteArray(sBinaryBitMask);
	}
	
	/**
	 * 
	 * @param bBinaryBitMask
	
	 * @return StringBuilder
	 */
	public static StringBuilder byteArrayBinaryBitMaskToString (byte[] bBinaryBitMask) {		
		return new StringBuilder(new HexString(bBinaryBitMask).toAsciiBinary()).reverse();
	}	

	/**
	 * 
	 * @param bBinaryBitMask
	 * @param iBitGroup
	
	 * @return StringBuilder
	 */
	public static StringBuilder byteArrayBinaryBitMaskToString (byte[] bBinaryBitMask , int iBitGroup) {		
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug)
			System.out.println("sbBinaryBitMask: " + new HexString(bBinaryBitMask).toAsciiBinary() + " -> BitGroup: " + iBitGroup);
		
		StringBuilder sbBinaryBitMask = new StringBuilder(new HexString(bBinaryBitMask).toAsciiBinary());
		
		if (debug|localDebug)
			System.out.println("sbBinaryBitMask: " + sbBinaryBitMask);
		
		StringBuilder sbBinaryBitMaskUpdate = new StringBuilder();
		
		sbBinaryBitMaskUpdate.append('(');
						
		for (int iIndex = 0  ; iIndex < sbBinaryBitMask.length(); iIndex++) {
				
			if (((iIndex %(iBitGroup)) == 0) && (iIndex != 0))
				sbBinaryBitMaskUpdate.append(")(");	

			sbBinaryBitMaskUpdate.append(sbBinaryBitMask.charAt(iIndex));
		
		}
				
		sbBinaryBitMaskUpdate.append(')');
		
		return sbBinaryBitMaskUpdate;
	}
	
	/**
	 * Format: (00000000)(11111111)(00000000)(11111111) = 32Bits in * Bits Per Byte
	 * 
	 * @param sBinaryBitMask
	
	 * @return boolean
	 */
	public static boolean binaryBitMaskValidate (String sBinaryBitMask) {		
		return true;
	}
	
	/**
	 * 
	 * Format: (00:00:00:00:00:00)(00:00:00:00:00:00)...
	 *
	 * 
	
	
	
	 * @param bMacAddressFormatList byte[]
	 * @return String
	 * @throws IllegalArgumentException */
	public static String byteArrayToMacAddressFormat (byte[] bMacAddressFormatList) throws IllegalArgumentException {
		
		if ((bMacAddressFormatList.length %MAC_ADDRESS_LENGTH) != 0) {
			throw new IllegalArgumentException("DataTypeFormatConversion.byteArrayToMacAddress() ByteArray Must be a Multiple of 6 Bytes: ByteLength: " + bMacAddressFormatList.length);
		}
			
		// ByteArray -> 00:00:00:00:00:00
		StringBuilder sbMacAddress = new StringBuilder();
		
		for (byte[] bByteArray : HexString.byteArrayGroup(bMacAddressFormatList, 6)) {
			sbMacAddress.append('(');
			sbMacAddress.append(new HexString(bByteArray).toString(":")).append(')');
		}
												
		return sbMacAddress.toString();
				
	}

	/**
	 * 
	 * @param sMacAddressFormatList
	
	
	 * @return byte[]
	 * @throws IllegalArgumentException */
	public static byte[] macAddressFormatToByteArray (String sMacAddressFormatList) throws IllegalArgumentException {
		
		if (!macAddressFormatValidation(sMacAddressFormatList)) {
			throw new IllegalArgumentException("Invalid MacAddress Format: " + sMacAddressFormatList);
		}
				
		return HexString.toByteArray(sMacAddressFormatList);			
	}
	
	/**
	 * 
	 * @param sMacAddress
	
	 * @return boolean
	 */
	public static boolean macAddressFormatValidation (String sMacAddress) {

		boolean localDebug = Boolean.FALSE;

		Matcher mMacAddress =  MAC_ADDRESS_REGEX.matcher(sMacAddress);
		
		if (mMacAddress.find()) {
			
			if (debug|localDebug) {
				for (int groupCount = 0 ; groupCount < mMacAddress.groupCount() ; groupCount++)				
					System.out.println("Group:" + groupCount + " Found value: -" + mMacAddress.group(groupCount) + "-");
			}
			
		}
		
		if (MAC_ADDRESS_REGEX.matcher(sMacAddress).matches()) {
			if (debug|localDebug) {
				System.out.println("DataTypeFormatConversion.macAddressFormatValidation() -> GOOD: " + mMacAddress);
			}
			
			return true;
			
		} else {
			
			if (debug|localDebug) {
				System.out.println("DataTypeFormatConversion.macAddressFormatValidation() -> BAD: " + mMacAddress);
			}
			
			return false;
		}
		
	}
	
	/**
	 * 
	 * @param sInetAddress
	
	 * @return byte[]
	 */
	public static byte[] inetAddressToByteArray (String sInetAddress) {
		return HexString.inetAddressToByteArray(sInetAddress);
	}
	
	/**
	 * 
	 * @param bInetAddress
	
	 * @return String
	 */
	public static String inetAddressToString (byte[] bInetAddress) {
		return HexString.toInetAddress(bInetAddress);
	}
	
	/**
	 * 
	 * @param sHexByte
	
	 * @return byte
	 */
	public static byte hexStringToByte (String sHexByte) {
		return HexString.toByteArray(sHexByte)[0];
	}
	
	/**
	 * 
	 * @param sDisplayHelp
	
	 * @return The display help of the DataType */
	public static String getDataTypeDisplayHelp (String sDisplayHelp) {
		
		if (sDisplayHelp.equalsIgnoreCase(DataTypeDictionaryReference.DATA_TYPE_DOUBLE_BYTE_ARRAY)) {
			return DOUBLE_BYTE_ARRAY_FORMAT;
		} else if (sDisplayHelp.equalsIgnoreCase(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR)) {
			return IPV4_TRANSPORT_FORMAT;
		} else if (sDisplayHelp.equalsIgnoreCase(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR)) {
			return IPV6_TRANSPORT_FORMAT;
		} else if (sDisplayHelp.equalsIgnoreCase(DataTypeDictionaryReference.DATA_TYPE_STRING_BITS)) {
			return STRING_BITS_FORMAT;
		} else if (sDisplayHelp.equalsIgnoreCase(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV6_ADDR)) {
			return IPV6_ADDRESS_FORMAT;
		} else if (sDisplayHelp.equalsIgnoreCase(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV4_ADDR)) {
			return IPV4_ADDRESS_FORMAT;
		} else {
			return "No Display Help Avaliable";
		}
		
	}
	
	/**
	 * 
	 * @param sOidObj6
	 * @return byte array of the OID Object 6
	 */
	public static byte[] oidObj6ToByteArray (String sOidObj6) {
		
		OID oid = new OID(sOidObj6);
		
		ByteArrayOutputStream baosOID = new ByteArrayOutputStream();
		
		try {
			oid.encodeBER(baosOID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return baosOID.toByteArray();
	}
	
}
