package com.comcast.oscar.examples.api;

import org.snmp4j.asn1.BER;
import org.snmp4j.smi.OID;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
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


 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */

public class CreateDocsisFileFromRawTLVInsertions {

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {

		String sSharedSecret = "SHAREDSECRET";
		
		/*Build TLV For DOCSIS*/
		TlvBuilder tb = new TlvBuilder();
		
											/**************************************
											 * 	Various Ways to Build your TLVs		
											 **************************************/
		/* Network Address */
		try {
			tb.add(3,1);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		/*
		 * Upstream/Downstram Rate Limit
		 */
		byte[] ba = HexString.toByteArray("18:20:01:02:00:01:04:04:68:73:64:00:06:01:07:08:04:00:0B:B8:00:09:04:00:98:96:80:0E:02:17:C8:0F:01:02");		
		HexString hs = new HexString(ba);
		ba = HexString.toByteArray("19:13:01:02:00:02:06:01:07:08:04:00:86:47:00:09:04:01:31:2D:00");		
		hs.add(ba);
		
		try {
			tb.add(hs);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		/* 
		 * 1.3.6.1.2.1.1.4 - sysContact 
		 */
		tb.add(Constants.SNMP_MIB_OBJ, new OID("1.3.6.1.2.1.1.4.0"),BER.OCTETSTRING ,"TestString");
		
		
		/*******************************************
		 * Create Configuration File Object 
		 *******************************************/
		ConfigurationFile cf = new ConfigurationFile(	ConfigurationFile.DOCSIS_VER_30,
														tb,
														sSharedSecret);
		
		/*Compiles Current TLVs with Shared Secret Key*/
		cf.commit();
		
		/*View Configuration*/
		ConfigurationFileExport cfe = new ConfigurationFileExport(cf);
				
		/* OUTPUT */	
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		
		System.out.println("Hex Representatation: ");
		try {
			System.out.println(cfe.getTlvBuilder().toStringSeperation(":"));
		} catch (TlvException e) {
			e.printStackTrace();
		}
	}

}
