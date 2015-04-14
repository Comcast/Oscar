package com.comcast.oscar.examples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.comcast.oscar.ber.BEROIDConversion;
import com.comcast.oscar.utilities.HexString;

public class BerEncodingIssueTest {

	public static void main(String[] args) {

		String sObjectID;
		
		sObjectID = "1.3.6.1.2.1.41.10.1.'String'.14.0";
		
		System.out.println("OID-DOT: " + sObjectID);
			
		VariableBinding vbCounter32BER = new VariableBinding(new OID(sObjectID));

		ByteArrayOutputStream baosCounter32 = new ByteArrayOutputStream();
		
		try {
			vbCounter32BER.encodeBER(baosCounter32);
		} catch (IOException e) {
			e.printStackTrace();
		}
								
		System.out.println(new HexString().hexCompressed());
		
	    BEROIDConversion ocOID = new BEROIDConversion(baosCounter32.toByteArray());
	    
	    System.out.println("OID-DOT: " + ocOID.toString());
	    
	    ocOID.getOidValue();
		
	}

}
