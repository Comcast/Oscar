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


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import com.comcast.oscar.utilities.HexString;


/**
 */
public class SnmpVarBindTest {


	/**
	 * Method readFile.
	 * @param file String
	 * @return String
	 * @throws IOException
	 */
	public static String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }

	    return stringBuilder.toString();
	}
	
	
	/**
	 * Method main.
	 * @param args String[]
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParseException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println("TEXT-FILE: " + readFile("c:\\digitMap.txt"));

		VariableBinding vbTlv64 = new VariableBinding(new OID("1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.1.1") , new OctetString(readFile("c:\\digitMap.txt")));
		
		ByteArrayOutputStream baosCounter32 = new ByteArrayOutputStream();
		
		vbTlv64.encodeBER(baosCounter32);
		
		System.out.println("BER-HEX: " + new HexString(baosCounter32.toByteArray()).toString());
	}

}
