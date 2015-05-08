package com.comcast.oscar.ber;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.comcast.oscar.netsnmp.NetSNMP;
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

public class BEROIDConversion {
	
	//Save a copy of the constructor variable
	private byte[] bBER;
	
	//Converted OID to BERInputStream
	private BERInputStream bisBER;
	
	//ByteBuffer of BER from constructor
	private ByteBuffer bbBER;
	
	//OID from BER
	private OID oOID;
	
	//VarBind BER
	VariableBinding vbBER;
	
	private final boolean debug = Boolean.FALSE;
	
	public final static String OID = "OID";
	public final static String DATA_TYPE = "DATA_TYPE";
	public final static String VALUE = "VALUE";
		
	Vector<? extends VariableBinding> vecBERVarBind;
	
	/**
	 * 
	 * @param bBER
	 */
	public BEROIDConversion (byte[] bBER) {
		
		if (debug) {
			
			HexString hs = new HexString(bBER);
			
			System.out.println("BEROIDConversion() " + hs.toString());
		}
		
		//Save copy of BER
		this.bBER = bBER;

		//Convert to ByteBuffer
		bbBER = ByteBuffer.allocate(this.bBER.length);

		//Add byte Array
		bbBER.put(this.bBER).rewind();
		
		//Convert to BERInputStream
		bisBER = new BERInputStream(bbBER);
		
		//Create a VarBind
		vbBER = new VariableBinding();
		
		try {
			vbBER.decodeBER(bisBER);
		} catch (IOException e) {
			if (debug) {
				e.printStackTrace();	
			}
			
		}

		oOID = vbBER.getOid();
		
		if (debug) {
			System.out.println("BEROIDConversion() " + oOID.toString());
			System.out.println("BEROIDConversion() " + vbBER.toString());
			System.out.println("BEROIDConversion() " + vbBER.getSyntax());
		}
		
	}
		
	/**
	 * 
	
	 * @return String
	 */
	public String getOidDotNotaion () {		
		return oOID.toDottedString();
	}

	/**
	 * 
	
	 * @return String
	 */
	public String getOidName() {		
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("BEROIDConversion.getOidName() - BER: " + new HexString(bBER).toString());			
			System.out.println("BEROIDConversion.getOidName() - OID.toString(): " + oOID.toString());
			System.out.println("BEROIDConversion.getOidName() - OID.format(): " + oOID.format());
		}
		
		return oOID.format();
	}
	
	/**
	 * 
	
	 * @return String
	 */
	public String getOidValue () {
		return vbBER.toValueString();
	}
	
	/**
	 * 
	
	 * @return Integer
	 */
	public Integer getDataType () {
		return vbBER.getSyntax();
	}
	
	/**
	 * 
	 * @return String
	 */
	@Override
	public String toString () {
		
		HashMap<String, String> hmssReturn = new HashMap<String,String>();
		
		hmssReturn.put(OID, NetSNMP.toTextualOID(getOidName()));
		hmssReturn.put(DATA_TYPE, getDataType().toString());
		hmssReturn.put(VALUE, getOidValue());
		
		return hmssReturn.toString();
	}
	
	/**
	 * 
	
	 * @return Map<String,String>
	 */
	public Map<String,String> toMap () {
		
		boolean localDebug = Boolean.FALSE;
		
		Map<String, String> mssReturn = new HashMap<String,String>();

		mssReturn.put(OID, NetSNMP.toTextualOID(getOidName()));
				
		//Make sure it is a OCTET String and a HexString with : and is it contains NON PlainText ASCII Char
		//mgarcia - 140224 - Added Logic to figure out a HexString vs. OctetString in some conditions from Packet ACE Tool
		if (	
				(vbBER.getSyntax() == BinaryConversion.byteToUnsignedInteger(BER.OCTETSTRING)) 										&&
				
				/* Checks to see if the string is in DEXTER HEX FORMAT */  /*Checks to see if there is a HEX String of 1 byte */
				((!vbBER.getVariable().toString().matches("(.*:.*)+?"))	&& 
						(vbBER.getVariable().toString().length() <= HexString.HEX_STRING_1_BYTE_LENGTH)) 							&&
				
				/* Make sure that String is a HEX Format */
				(HexString.isHexString(vbBER.getVariable().toString()))																&&		
				
				/* Checks to see if the HEX String Contains and Values that are outside of the Typical ASCII Chars*/
				(!HexString.verifyAsciiPlainText(HexString.toByteArray(vbBER.getVariable().toString())))
				
			) {
			
			if (localDebug|debug)
				System.out.println("BEROIDConversion.toMap() - Found a NON-ASCII Plain Text Characters");		

			mssReturn.put(DATA_TYPE, Integer.toString(BinaryConversion.byteToUnsignedInteger(BERService.HEX)));

			mssReturn.put(VALUE, vbBER.getVariable().toString());
			
		} else if (
				(vbBER.getSyntax() == BinaryConversion.byteToUnsignedInteger(BER.OCTETSTRING)) 										&& 
				((HexString.isHexString(vbBER.getVariable().toString())) && (vbBER.getVariable().toString().matches("(.*:.*)+?")))	&& 
				(!HexString.verifyAsciiPlainText(HexString.toByteArray(vbBER.getVariable().toString())))
			) {

			if (localDebug|debug)
				System.out.println("BEROIDConversion.toMap() - Found a NON-ASCII Plain Text Characters");
			
			mssReturn.put(DATA_TYPE, Integer.toString(BinaryConversion.byteToUnsignedInteger(BERService.HEX)));

			mssReturn.put(VALUE, vbBER.getVariable().toString());
			
		} else if (
					(vbBER.getSyntax() == BinaryConversion.byteToUnsignedInteger(BER.OCTETSTRING)) &&
					(HexString.containAsciiWhiteSpace(HexString.toByteArray(HexString.asciiToHex(getOidValue()))))
				) {

			if (localDebug|debug)
				System.out.println("BEROIDConversion.toMap() - Found a ASCII Plain Text Characters: \"" + getOidValue() + "\"");
			
			//mgarcia - 140214 - Remarked this out, if no errors are found since this date, remove line
			//mssReturn.put(DATA_TYPE, Integer.toString(BinaryConversion.byteToUnsignedInteger(BERService.HEX)));

			mssReturn.put(DATA_TYPE, getDataType().toString());
			
			mssReturn.put(VALUE, vbBER.getVariable().toString());

		} else {
			
			mssReturn.put(DATA_TYPE, getDataType().toString());

			mssReturn.put(VALUE, getOidValue());		
		}
		
		return mssReturn;
	}
}
