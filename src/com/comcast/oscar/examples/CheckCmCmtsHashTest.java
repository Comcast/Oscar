package com.comcast.oscar.examples;
import java.io.File;

import com.comcast.oscar.compiler.DocsisCompiler;
import com.comcast.oscar.tlv.TlvException;
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
	
	@author Maurice Garcia (mgarcia01752@outlook.com)

*/


/**
 */
public class CheckCmCmtsHashTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Open Working File
		File file = new File("c:\\d10_defaultcmtest.cm");
		
		HexString hsObject = new  HexString(HexString.fileToByteArray(file));

		//Print File in Hex
		System.out.println("d10_defaultcmtest.cm: \n" + hsObject.toString(":"));
		
		//Compile File to DOCSIS Compiler
		DocsisCompiler docComp = new DocsisCompiler("SHAREDSECRET",0);
		
		try {
			docComp.add(hsObject);
		} catch (TlvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Finalize
		docComp.commit();
		
		//Print Output to Verify CM and CMTS HASH is the same 
		System.out.println("ReCompiled: \n" + new HexString(docComp.toByteArray()).toString(":"));
		
	}

}
