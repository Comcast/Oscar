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
import java.io.IOException;

import org.snmp4j.smi.OID;

import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.utilities.HexString;


/**
 */
public class SmiManagerTest {
	
	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {
		
		File fSnmp4JLic = null;
		
		try {
			fSnmp4JLic = new File(new java.io.File( "." ).getCanonicalPath() + File.separatorChar + "licence" + File.separatorChar +  "snmp4jLicence.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SMIManagerService.SmiManagerStart(fSnmp4JLic);
		
		String sOID = "docsDevNmAccessIp.1";
		
		OID oOID = null;
		
		oOID = new OID(sOID);

		System.out.println("OID: "  + oOID.toDottedString() + " -> " + new HexString(oOID.toByteArray()).toString(":"));
		
		System.out.println("OID: "  + oOID.getSyntaxString());
		
		oOID = new OID(oOID.toDottedString());

		System.out.println("OID: "  + oOID.format() + " -> " + new HexString(oOID.toByteArray()).toString(":"));
		
		System.out.println("OID: "  + oOID.getSyntaxString());

	}

}
