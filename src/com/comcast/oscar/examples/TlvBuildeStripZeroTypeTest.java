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


import java.util.HashMap;
import java.util.Map;

import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

/**
 */
public class TlvBuildeStripZeroTypeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Map<Integer,Integer> mii = new HashMap<Integer,Integer>();
		
		String sTLV = "010101020101030004010105000600";
		
		byte[] ba = null;
		
		try {
			ba = TlvBuilder.stripZeroByteTLV(HexString.toByteArray(sTLV), mii);
		} catch (TlvException e) {
			e.printStackTrace();
		}

		System.out.println("TLV: " + sTLV);
		System.out.println("TLV: " + new HexString(ba).toString(""));
	}

}
