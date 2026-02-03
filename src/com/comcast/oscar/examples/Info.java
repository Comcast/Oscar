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


import com.comcast.oscar.dictionary.DictionarySQLConstants;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;


/**
 */
public class Info {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*
		 * 
		 * http://www.tutorialspoint.com/java/java_regular_expressions.htm
		 * 
		 * 
		 * 
		 */
		
		TlvBuilder tbTLV = new TlvBuilder();
		
		String sTlvHex = "03010132080406020401020304";
		
		try {
			tbTLV.add(new HexString(HexString.toByteArray(sTlvHex)));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		TlvDisassemble tlvDisassemble = new TlvDisassemble(tbTLV,DictionarySQLConstants.DOCSIS_DICTIONARY_TABLE_NAME);

		System.out.println(tlvDisassemble.getTlvDictionary().toString());
		
		
		System.out.println(System.getProperty("user.dir"));
	}

}
