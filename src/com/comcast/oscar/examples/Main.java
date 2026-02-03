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
	
	@author Maurice Garcia (mgarcia01752@outlook.com)

*/

/**
 */
public class Main {

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		
/*		
		
		


		
		
		
		String firstName = "Maurice";		
		HexString hexStrBuff = new HexString(firstName);
		System.out.println("Maurice -> " + hexStrBuff.hexCompressed());
		
		String lastName = " Garcia";
		if (hexStrBuff.add(lastName))
			System.out.println ("Add Succesful");
		else 
			System.out.println ("Add Failed");
		System.out.println("Maurice Garcia -> " + hexStrBuff.hexCompressed());

		System.out.println("+--------------------------------------------------------------------------------------------------+");
		hexStrBuff.clear();
		
		firstName = "Maurice";		
		hexStrBuff = new HexString(firstName);
		System.out.println("Maurice -> " + hexStrBuff.hexCompressed());
		
		lastName = " Garcia";
		if (hexStrBuff.add(lastName,true))
			System.out.println ("Add Succesful");
		else 
			System.out.println ("Add Failed");
		System.out.println("Maurice Garcia -> " + hexStrBuff.hexCompressed());

		System.out.println("+--------------------------------------------------------------------------------------------------+");
		
		
		hexStrBuff.clear();
		System.out.println("Clear -> Maurice Garcia -> (" + hexStrBuff.hexCompressed() + ")");
		
		lastName = "Maurice Garcia";
		if (hexStrBuff.add(lastName))
			System.out.println ("Add Succesful");
		else 
			System.out.println ("Add Failed");
		System.out.println("Maurice Garcia -> \t (" + hexStrBuff.hexCompressed() + ")");
		
		System.out.println("+--------------------------------------------------------------------------------------------------+");
		
		Integer number = 723000000;
		hexStrBuff.clear();
		if (hexStrBuff.add(number))
			System.out.println ("Add Succesful");
		else 
			System.out.println ("Add Failed");
		System.out.println("723000000 \t" + hexStrBuff.hexCompressed());

		System.out.println("+--------------------------------------------------------------------------------------------------+");
		
		hexStrBuff.clear();
		byte[] bytes = {-1, 0, 1, 2, 3 };
		if (hexStrBuff.add(bytes))
			System.out.println ("Add Succesful");
		else 
			System.out.println ("Add Failed");
		System.out.println("-1, 0, 1, 2, 3 \t" + hexStrBuff.hexCompressed());
		
		System.out.println("HexByteLength: " + hexStrBuff.hexByteLength());		
		
		System.out.println("+--------------------------------------------------------------------------------------------------+");
		
		hexStrBuff.clear();
		
		for (int counter = 0 ; counter < 16 ; counter++)
			hexStrBuff.add(counter);
		
		System.out.println("HexByteLength: " + hexStrBuff.hexByteLength());

		System.out.println("+--------------------------------------------------------------------------------------------------+");
		
		TlvBuilder tlvBuild = new TlvBuilder();
		
		Integer type = 100;
		Integer value = 65535;
		tlvBuild.add(type, value);
		
		System.out.println("TLV-BUFFER: " + tlvBuild.toString());
		System.out.println("TLV-HEX-LENGTH: " + tlvBuild.hexByteLength());
		
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
		tlvBuild4.add(1,freq4);
		
		tlvBuildStart.add(tlvBuild1);
		tlvBuildStart.add(tlvBuild2);
		tlvBuildStart.add(tlvBuild3);
		tlvBuildStart.add(tlvBuild4);

		System.out.println("TLV-BUFFER: (" + tlvBuildStart.toString() + ")");
		System.out.println("TLV-HEX-LENGTH: " + tlvBuildStart.hexByteLength());
		
		if (tlvBuildStart.hexByteLength() == 24) {
			System.out.println ("Add Succesful");	
		} else {
			System.out.println ("Add Failed");
		}

		System.out.println("+--------------------------------------------------------------------------------------------------+");
		
		tlvBuildStart.encapsulateTlv(254);		
		System.out.println("TLV-BUFFER: (" + tlvBuildStart.toString() + ")");
		System.out.println("TLV-HEX-LENGTH: " + tlvBuildStart.hexByteLength());
*/

	}

}
