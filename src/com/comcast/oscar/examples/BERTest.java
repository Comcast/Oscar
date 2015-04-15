package com.comcast.oscar.examples;
import java.io.ByteArrayOutputStream;

import org.snmp4j.asn1.BER;
import org.snmp4j.smi.OID;

import com.comcast.oscar.ber.BEROIDConversion;
import com.comcast.oscar.ber.BERService;
import com.comcast.oscar.utilities.HexString;


/*
	Copyright 2015 Comcast Cable Communications Management, LLC
	___________________________________________________________________
	Licensed under the Apache License, Version 2.0 (the "License")
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/


/**
 */
public class BERTest {

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {
		System.out.println("+--------------------------------------------------------------------------------------------------+");
		
		String SnmpOID = "1.3.6.1.2.1.4115.10.1.14.0";
		
		OID oid = new OID(SnmpOID);
		System.out.println("OID-FORMAT: " + oid.format());

		System.out.println("+--------------------------------------------------------------------------------------------------+");
		
		SnmpOID = "1.3.6.1.2.1.4115.10.1.14.0";
		
		long lNum = 1;
		
		try {
			System.out.println("COUNTER32-OID: " + SnmpOID + " \t-> BER: " + BERService.setOIDEncoding(SnmpOID,BER.COUNTER32,lNum));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
		try {
			System.out.println("COUNTER64-OID: " + SnmpOID + " \t-> BER: " + BERService.setOIDEncoding(SnmpOID,BER.COUNTER64,lNum));
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		try {
			System.out.println("GAUGE32-OID: " + SnmpOID + " \t-> BER: " + BERService.setOIDEncoding(SnmpOID,BER.GAUGE32,lNum));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			System.out.println("INTEGER32-OID: " + SnmpOID + " \t-> BER: " + BERService.setOIDEncoding(SnmpOID,BER.INTEGER32,lNum));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			System.out.println("TIMETICKS-OID: " + SnmpOID + " \t-> BER: " + BERService.setOIDEncoding(SnmpOID,BER.TIMETICKS,lNum));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String sOctectString = "Maurice";
		try {
			System.out.println("OCTETSTRING-OID: " + SnmpOID + " \t-> BER: " + BERService.setOIDEncoding(SnmpOID, BER.OCTETSTRING , sOctectString));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sIpAddress = "1.1.1.1";
		try {
			System.out.println("IPADDRESS-OID: " + SnmpOID + " \t-> BER: " + BERService.setOIDEncoding(SnmpOID, BER.IPADDRESS , sIpAddress));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("+--------------------------------------HEX to OID Conversion------------------------------------------------------+");

		HexString hsBER = new HexString(HexString.toByteArray("3010060b2b06010201a0130a010e00410101"));    
	    System.out.println("OID-HEX-CHECK: " + hsBER);
	    BEROIDConversion ocOID = new BEROIDConversion(hsBER.toByteArray());
	    System.out.println("OID-DOT: " + ocOID.getOidDotNotaion());
	    ocOID.getOidValue();

		System.out.println("+--------------------------------------OID to ByteArray to OID Conversion------------------------------------------------------+");

		String SnmpOIDStringEmbedded = "1.3.6.1.2.1.4115.10.'maurice'.14.0";
		System.out.println("OID-DOT: " + SnmpOIDStringEmbedded);
		
		ByteArrayOutputStream baosOID = new ByteArrayOutputStream();
		
		try {
			//baosOID =  BERService.encodeOID(new OID(SnmpOIDStringEmbedded));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BEROIDConversion ocOIDNameEmbedded = new BEROIDConversion(baosOID.toByteArray());
		
		System.out.println("OID-DOT: " + ocOIDNameEmbedded.getOidDotNotaion());
		
		System.out.println("OID-HEX: " + new HexString(baosOID.toByteArray()).toString());
	
	}

}
