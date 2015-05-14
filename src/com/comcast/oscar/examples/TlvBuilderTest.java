package com.comcast.oscar.examples;

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


import java.io.File;

import org.snmp4j.asn1.BER;
import org.snmp4j.smi.OID;

import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexDump;
import com.comcast.oscar.utilities.HexString;


/**
 */
public class TlvBuilderTest {

	/**
	 * @param args
	
	 * @throws TlvException  */
	public static void main(String[] args) throws TlvException {
		
		System.out.println("+--------------------------------------------------------------------------------------------------+");
		
		TlvBuilder tlvBuild = new TlvBuilder();
		
		Integer type = 100;
		Integer value = 65535;
		tlvBuild.add(type, value);
		
		System.out.println("TLV-BUFFER: " + tlvBuild.toString());
		System.out.println("TLV-HEX-LENGTH: " + tlvBuild.length());
		
		Integer freq1 = 723000000;
		Integer freq2 = 729000000;
		Integer freq3 = 735000000;
		Integer freq4 = 741000000;
		
		TlvBuilder tlvBuildStart = new TlvBuilder();
		TlvBuilder tlvBuild1 = new TlvBuilder();
		TlvBuilder tlvBuild2 = new TlvBuilder();
		TlvBuilder tlvBuild3 = new TlvBuilder();
		TlvBuilder tlvBuild4 = new TlvBuilder();
		
		tlvBuild1.add(1,freq1);
		tlvBuild2.add(1,freq2);
		tlvBuild3.add(1,freq3);
		tlvBuild3.add(2,freq3);
		tlvBuild4.add(1,freq4);
		tlvBuild4.add(3,freq4);
		
		tlvBuildStart.add(tlvBuild1);
		tlvBuildStart.add(tlvBuild2);
		tlvBuildStart.add(tlvBuild3);
		tlvBuildStart.add(tlvBuild4);

		System.out.println("TLV-BUFFER: (" + tlvBuildStart.toString() + ")");
		System.out.println("TLV-HEX-LENGTH: " + tlvBuildStart.length());
		
		int iNextIndex = TlvBuilder.nextTLVIndex(tlvBuildStart.toByteArray(), 0);
		System.out.println("START-TLV-INDEX: 0 -> NEXT-TLV-INDEX: " + iNextIndex);
		
		iNextIndex = TlvBuilder.nextTLVIndex(tlvBuildStart.toByteArray(), iNextIndex);
		System.out.println("NEXT-TLV-INDEX: " + iNextIndex);		

		iNextIndex = TlvBuilder.nextTLVIndex(tlvBuildStart.toByteArray(), iNextIndex);
		System.out.println("NEXT-TLV-INDEX: " + iNextIndex);		
		
		iNextIndex = TlvBuilder.nextTLVIndex(tlvBuildStart.toByteArray(), iNextIndex);
		System.out.println("NEXT-TLV-INDEX: " + iNextIndex);		

		
		if (tlvBuildStart.length() == 24) {
			System.out.println ("Add Succesful");	
		} else {
			System.out.println ("Add Failed");
		}

		System.out.println("+-----------------------------------ENCAPSULATE---------------------------------------------------+");
		System.out.println("PRE-ENCAP: " + new HexString(tlvBuildStart.toByteArray()).toString());
		tlvBuildStart.encapsulateTlv(254);
		System.out.println("PST-ENCAP: " + new HexString(tlvBuildStart.toByteArray()).toString());
		
		System.out.println("+-----------------------------------GET-MAJOR-TLV-TYPE-LIST---------------------------------------------------+");
		for (Integer iType : tlvBuildStart.getTopLevelTlvList())
			System.out.println("Major-TLV-Type: " + iType);
		
		System.out.println("+---------------------------------------STRIP-TLV-(2)----------------------------------------------------------+");
		System.out.println("BEFOR-TLV-STRIP: " + tlvBuildStart);
		HexString hsTlvStrip = new HexString(TlvBuilder.stripTlv(2, tlvBuildStart.toByteArray()));
		System.out.println("AFTER-TLV-STRIP: " + hsTlvStrip.hexCompressed());

		System.out.println("+---------------------------------------STRIP-TLV-(1)----------------------------------------------------------+");
		System.out.println("BEFOR-TLV-STRIP: " + tlvBuildStart);
		hsTlvStrip = new HexString(TlvBuilder.stripTlv(1, tlvBuildStart.toByteArray()));
		System.out.println("AFTER-TLV-STRIP: " + hsTlvStrip.hexCompressed());
		
		System.out.println("+--------------------------------------------------------------------------------------------------+");		
		tlvBuildStart.encapsulateTlv(254);		
		System.out.println("TLV-BUFFER: (" + tlvBuildStart.toString() + ")");
		System.out.println("TLV-HEX-LENGTH: " + tlvBuildStart.length());
		
		System.out.println("+--------------------------------------------------------------------------------------------------+");	
		tlvBuildStart.clear();
		
		String SnmpOID = "1.3.6.1.2.1.4115.10.1.14.0";
		
		tlvBuildStart.add(0x11, new OID(SnmpOID), BER.COUNTER32 , 1);		
		System.out.println("TLV-BUFFER-OID: (" + tlvBuildStart.toString() + ")");
		System.out.println("TLV-HEX-LENGTH-OID: " + tlvBuildStart.length());

		System.out.println("+--------------------------------------------------------------------------------------------------+");		
		tlvBuildStart.add(0x11,new OID(SnmpOID), BER.OCTETSTRING, "Maurice");
		System.out.println("TLV-BUFFER-OID: (" + tlvBuildStart.toString() + ")");
		System.out.println("TLV-HEX-LENGTH-OID: " + tlvBuildStart.length());
		System.out.println("TLV-BUFFER-OID: (" + tlvBuildStart.toStringArray() + ")");
		
		System.out.println("+----------------------------------TLV BUILDER BYTE ARRAY---------------------------------------------------+");	
		HexString hsTlvBuildStart = new HexString(tlvBuildStart.toByteArray());
		System.out.println("TLV-BUFFER-OID: (" + hsTlvBuildStart + ")");

		System.out.println("+----------------------------------TLV BUILDER - ADD CERTIFICATE---------------------------------------------------+");	
		
		File cvcFile = new File("c:\\xxxxxx.der");
	
		TlvBuilder tbTlvCertificate = new TlvBuilder();
		
		tbTlvCertificate.add(32, HexString.fileToByteArray(cvcFile));
		
		for (byte[] bTLV :tbTlvCertificate.sortByTopLevelTlv()) {			
			System.out.println("TLV: " + new HexString(bTLV).toString());					
		}
		
		System.out.println(HexDump.dumpHexString(tbTlvCertificate.toByteArray()));
				
	}

}
