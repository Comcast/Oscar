package com.comcast.oscar.ber;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.asn1.BER;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;

import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
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

/**
 */
public class BERService {

	static BER ber;
	
	public static final String CONVERSION_ERROR = "Error converting";
	
	public static final String COUNTER32	= "Counter32";
	public static final String COUNTER64	= "Counter64";
	public static final String GAUGE32 		= "Gauge32";
	public static final String INTEGER32	= "Integer32";
	public static final String TIMETICKS	= "TimeTicks";
	public static final String IPADDRESS	= "IpAddress";
	public static final String OCTETSTRING 	= "OctetString";
	public static final String HEXSTRING 	= "HexString";
	
	public static final byte HEX = (byte) 0xFE;
		
	final static boolean debug = Boolean.FALSE;

	/**
	 * 
	 * @param sObjectID
	 * @param bBerDataType
	 * @param lNumber
	
	
	 * @return String
	 * @throws Exception */
	public static String setOIDEncoding (String sObjectID , byte bBerDataType , long lNumber) throws Exception {
		
		boolean localDebug = Boolean.FALSE;
		
		if (localDebug|debug) {
			System.out.println("BERService.setOIDEncoding(s,b,l) - OID: " + sObjectID + " -> Value: " + lNumber);
		}
		
		String berString = null;
		    
		if ((BER.COUNTER == bBerDataType)  || (BER.COUNTER32 == bBerDataType)) {
			
			VariableBinding vbCounter32BER = new VariableBinding(new OID(sObjectID) , new Counter32(lNumber));

			ByteArrayOutputStream baosCounter32 = new ByteArrayOutputStream();
			
			vbCounter32BER.encodeBER(baosCounter32);
									
			berString = new HexString(baosCounter32.toByteArray()).hexCompressed();
			
		} else if (BER.COUNTER64 == bBerDataType) {
							
			VariableBinding vbCounter64BER = new VariableBinding(new OID(sObjectID) , new Counter64(lNumber));

			ByteArrayOutputStream baosCounter64 = new ByteArrayOutputStream();
			
			vbCounter64BER.encodeBER(baosCounter64);
									
			berString = new HexString(baosCounter64.toByteArray()).hexCompressed();
						
		} else if ((BER.GAUGE == bBerDataType) || (BER.GAUGE32 == bBerDataType)) {
					
			VariableBinding vbGauge32BER  = new VariableBinding(new OID(sObjectID) , new Gauge32(lNumber));

			ByteArrayOutputStream baosGauge32 = new ByteArrayOutputStream();
			
			vbGauge32BER.encodeBER(baosGauge32);
									
			berString = new HexString(baosGauge32.toByteArray()).hexCompressed();
				
		} else if ((BER.INTEGER == bBerDataType) || (BER.INTEGER32 == bBerDataType)) {
						
			VariableBinding vbInteger32BER  = new VariableBinding(new OID(sObjectID) , new Integer32((int)lNumber));

			ByteArrayOutputStream baosInteger32BER = new ByteArrayOutputStream();
			
			vbInteger32BER.encodeBER(baosInteger32BER);
									
			berString = new HexString(baosInteger32BER.toByteArray()).hexCompressed();
			
		} else if ((BER.TIMETICKS == bBerDataType)) {
			
			VariableBinding vbTimeTicksBER  = new VariableBinding(new OID(sObjectID) , new TimeTicks(lNumber));

			ByteArrayOutputStream baosTimeTicksBER = new ByteArrayOutputStream();
			
			vbTimeTicksBER.encodeBER(baosTimeTicksBER);
									
			berString = new HexString(baosTimeTicksBER.toByteArray()).hexCompressed();		
			
		}
		
		return berString;
	}
	
	/**
	 * 
	 * @param sObjectID
	 * @param bBerDataType
	 * @param sValue
	
	
	 * @return String
	 * @throws Exception */
	public static String setOIDEncoding (String sObjectID , byte bBerDataType , String sValue) throws Exception {

		boolean localDebug = Boolean.FALSE;
		
		if (localDebug|debug) {
			System.out.println("BERService.setOIDEncoding(s,b,s) - OID: " + sObjectID + " -> Value: " + sValue);
		}
		
		String berString = null;
		
		if (BER.IPADDRESS == bBerDataType) {
			
			if (localDebug|debug)
				System.out.println("BERService.setOIDEncoding() -> StringOID: " + sObjectID);
			
			VariableBinding vbIpBER = new VariableBinding(new OID(sObjectID) , new IpAddress(sValue));

			ByteArrayOutputStream baosIpAddress = new ByteArrayOutputStream();
			
			vbIpBER.encodeBER(baosIpAddress);
									
			berString = new HexString(baosIpAddress.toByteArray()).hexCompressed();
			
		} else if (BER.OCTETSTRING == bBerDataType) {
			
			VariableBinding vbOctBER = new VariableBinding(new OID(sObjectID) , new OctetString(sValue));

			ByteArrayOutputStream baosOctetString = new ByteArrayOutputStream();
			
			vbOctBER.encodeBER(baosOctetString);
									
			berString = new HexString(baosOctetString.toByteArray()).hexCompressed();
						
		} 
		
		return berString;
	}	

	/**
	 * 
	 * @param sObjectID
	 * @param bBerDataType
	 * @param bValue
	
	
	 * @return String
	 * @throws Exception */
	public static String setOIDEncoding (String sObjectID , byte bBerDataType , byte[] bValue) throws Exception {
	
		boolean localDebug = Boolean.FALSE;
		
		if (localDebug|debug) {
			System.out.println("BERService.setOIDEncoding(s,b,b) - OID: " + sObjectID + " -> Value: " + new HexString(bValue).toString(":"));
		}
		
		String berString = "";
		
		if (BER.OCTETSTRING == bBerDataType) {
			
			VariableBinding vbOctBER = new VariableBinding(new OID(sObjectID) , new OctetString(bValue));

			ByteArrayOutputStream baosOctetString = new ByteArrayOutputStream();
			
			vbOctBER.encodeBER(baosOctetString);
									
			berString = new HexString(baosOctetString.toByteArray()).hexCompressed();
			
		} 
			
		return berString;
	}
	
	/**
	 * 
	 * @param sObjectID
	 * @param bBerDataType
	 * @param sValue
	
	
	 * @return byte[]
	 * @throws Exception */
	public static byte[] setOIDEncodingToByteArray (String sObjectID , byte bBerDataType , String sValue) throws Exception {		
		
		boolean localDebug = Boolean.FALSE;
		
		if (localDebug|debug) {
			System.out.println("BERService.setOIDEncodingToByteArray(s,b,s) - OID: " + sObjectID + " -> Value: " + sValue);
		}
		
		if (bBerDataType == HEX) {		
			return HexString.toByteArray(setOIDEncoding ( sObjectID ,  BER.OCTETSTRING ,  HexString.toByteArray(sValue)));
		} else {
			return HexString.toByteArray(setOIDEncoding ( sObjectID ,  bBerDataType ,  sValue));
		}
	
	}

	/**
	 * 
	 * @param sObjectID
	 * @param bBerDataType
	 * @param lNumber
	
	
	 * @return byte[]
	 * @throws Exception */
	public static byte[] setOIDEncodingToByteArray (String sObjectID , byte bBerDataType , long lNumber) throws Exception {		
		return HexString.toByteArray(setOIDEncoding ( sObjectID ,  bBerDataType ,  lNumber));
	}
	
	/**
	 * 
	 * @param oidString
	
	 * @return OutputStream
	 */
	public static OutputStream encodeOID (String sOID) {
		
		OutputStream baosBER = new ByteArrayOutputStream();

		//Make sure that OID is in a valid format to convert to BER ASN.1 Format
		//If Not return an empty Array

		OID oid = new OID(sOID);

		if (oid.isValid()) {

			try {
				
				//Encode IOD to ASN.1 Encoding using BER
				oid.encodeBER(baosBER);

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			return new ByteArrayOutputStream();
		}

/*		
		VariableBinding vbOID = new VariableBinding(new OID(sOID));

		ByteArrayOutputStream baosOID = new ByteArrayOutputStream();
		
		try {
			vbOID.encodeBER(baosOID);
		} catch (IOException e) {
			e.printStackTrace();
		}
*/		
		return baosBER;
	}

	/**
	 *
	 * @param OID oid
	 * @return VariableBinding
	 */
	public static VariableBinding encodeOID (OID oid) {
		
		VariableBinding vbOID = new VariableBinding(oid);

		ByteArrayOutputStream baosOID = new ByteArrayOutputStream();
		
		try {
			vbOID.encodeBER(baosOID);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return vbOID;
	}
	
	/**
	 * 
	 * @param bBERSetValueTLV
	
	
	 * @return String
	 * @throws TlvException */
	public static String getBERSetValue (byte[] bBERSetValueTLV) throws TlvException {
		
		boolean localDebug = Boolean.FALSE;
		
		byte bBerDataType = bBERSetValueTLV[0];
		byte[] bBerDataValue = TlvBuilder.getTlvValue(bBERSetValueTLV);
		
		Integer iBERValue = null;
		String sBERValue = null;

		if ((BER.COUNTER == bBerDataType)  || (BER.COUNTER32 == bBerDataType)) {
			
			iBERValue = new HexString(bBerDataValue).toInteger();
			
			sBERValue = iBERValue.toString();
						
		} else if (BER.COUNTER64 == bBerDataType) {
			
			iBERValue = new HexString(bBerDataValue).toInteger();
			
			sBERValue = iBERValue.toString();
			
		} else if ((BER.GAUGE == bBerDataType) || (BER.GAUGE32 == bBerDataType)) {
			
			iBERValue = new HexString(bBerDataValue).toInteger();
			
			sBERValue = iBERValue.toString();
			
		} else if ((BER.INTEGER == bBerDataType) || (BER.INTEGER32 == bBerDataType)) {
			
			iBERValue = new HexString(bBerDataValue).toInteger();
			
			sBERValue = iBERValue.toString();
			
		} else if ((BER.TIMETICKS == bBerDataType)) {
			
			iBERValue = new HexString(bBerDataValue).toInteger();
			
			sBERValue = iBERValue.toString();
			
		} else if (BER.IPADDRESS == bBerDataType) {
			
			if (debug|localDebug) {
				HexString hsBerIpAddress = new HexString(bBerDataValue);
				System.out.println("BER.IPADDRESS: " + hsBerIpAddress);
			}
			
			sBERValue = HexString.toInetAddress(bBerDataValue);
			
		} else if (BER.OCTETSTRING == bBerDataType) {
						
			sBERValue = new HexString(bBerDataValue).toASCII();
		}
		
		return sBERValue;
	}
	
	/**
	 * 
	 * @param bBERSetValueTLV
	
	 * @return int
	 */
	public static int getBERType (byte[] bBERSetValueTLV) {	
		return BinaryConversion.byteToUnsignedInteger(bBERSetValueTLV[0]);
	}
	
	/**
	 * 
	 * @param bOIDBER
	
	 * @return byte[]
	 */
	public static byte[] cleanBEROIDPrefix (byte[] bOIDBER) {
		
		if (bOIDBER[0] == 0x30) {
			
			ByteArrayOutputStream baosOIDBER = new ByteArrayOutputStream();
			
			baosOIDBER.write(bOIDBER, 2, bOIDBER.length-2);
			
			return baosOIDBER.toByteArray();
			
		} else {
			return bOIDBER;
		}
				
	}

	/**
	 * 
	 * @param iBerDataType
	
	 * @return boolean
	 */
	public static boolean isNumberDataType (int iBerDataType) {
		
		boolean boolStatus = false;
		
		if ((iBerDataType == BER.COUNTER) 		|| 
				(iBerDataType == BER.COUNTER32) || 
				(iBerDataType == BER.COUNTER64) || 
				(iBerDataType == BER.GAUGE) 	||
				(iBerDataType == BER.GAUGE32) 	|| 
				(iBerDataType == BER.INTEGER) 	||
				(iBerDataType == BER.INTEGER32)	||
				(iBerDataType == BER.TIMETICKS)) {
			boolStatus =  true;
		}
		
		return boolStatus;
	}

	/**
	 * 
	 * @param iBerDataType
	
	 * @return boolean
	 */
	public static boolean isStringDataType (int iBerDataType) {
		
		boolean boolStatus = false;
		
		if ((iBerDataType == BER.IPADDRESS) || (iBerDataType == BER.OCTETSTRING) ) {
			boolStatus =  true;
		}
		
		return boolStatus;
	}
	
	/**
	 * @param sDataType
	
	 * @return byte */
	public static byte berStringDataTypeToByte (String sDataType) {
		
		if 			(COUNTER32.equalsIgnoreCase(sDataType)) {
			return BER.COUNTER32;
		} else if 	(COUNTER64.equalsIgnoreCase(sDataType)) {
			return BER.COUNTER64;
		} else if 	(GAUGE32.equalsIgnoreCase(sDataType)) {
			return BER.GAUGE32;
		} else if 	(INTEGER32.equalsIgnoreCase(sDataType)) {
			return BER.INTEGER32;
		} else if 	(TIMETICKS.equalsIgnoreCase(sDataType)) {
			return BER.TIMETICKS;
		} else if 	(IPADDRESS.equalsIgnoreCase(sDataType)) {
			return BER.IPADDRESS;
		} else if 	(OCTETSTRING.equalsIgnoreCase(sDataType)) {
			return BER.OCTETSTRING;
		} else if	(HEXSTRING.equalsIgnoreCase(sDataType)) {
			return	HEX;
		}
					
		return 0x00;

	}
	
	/**
	 * 
	
	 * @return List of String SNMP DataType */
	public static List<String> getDataTypeStringList() {
		
		List<String> ls = new ArrayList<String>();
		
		ls.add(COUNTER32);
		ls.add(COUNTER64);
		ls.add(GAUGE32);
		ls.add(OCTETSTRING);
		ls.add(INTEGER32);
		ls.add(TIMETICKS);
		ls.add(IPADDRESS);
		ls.add(HEXSTRING);
		
		return ls;
		
	}
		
	/**
	 * 
	 * @param baos
	
	
	 * @return String
	 * @throws Exception */
	@SuppressWarnings("unused")
	private static String baosToHexString(OutputStream baos) throws Exception {
		
		byte[] bytes = ((ByteArrayOutputStream) baos).toByteArray();
		
		String result = "";
		
		for (int i=0; i < bytes.length; i++) {
			
			try{
				result +=
						Integer.toString( ( bytes[i] & 0xff ) + 0x100, 16).substring( 1 );
			}catch(Exception e){
				e.printStackTrace();
				return CONVERSION_ERROR;
			}
		}

		return result;
	}
		
}
