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


import com.comcast.oscar.configurationfile.ConfigurationFileExport;


/**
 */
public class TlvNotationDefinitionLookUpTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String sTlv;
	
		ConfigurationFileExport cfe = new ConfigurationFileExport(ConfigurationFileExport.DOCSIS_VER_31);
		
		sTlv = "1" ;			
		System.out.println(cfe.getTlvDefintion(sTlv));
		
		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");
		sTlv = "17" ;
		System.out.println(cfe.getTlvDefintion(sTlv));

		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");	
		sTlv = "17.1" ;
		System.out.println(cfe.getTlvDefintion(sTlv));

		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");	
		sTlv = "23.10.1";
		System.out.println(cfe.getTlvDefintion(sTlv));
		
		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");	
		sTlv = "217.53.2.1";
		System.out.println(cfe.getTlvDefintion(sTlv));

		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");	
		sTlv = "23.10.1";
		System.out.println(cfe.getTlvDefintion(sTlv));

		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");	
		sTlv = "50.4.2";
		System.out.println(cfe.getTlvDefintion(sTlv));		

		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");	
		sTlv = "29";
		System.out.println(cfe.getTlvDefintion(sTlv));		

		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");	
		sTlv = "43.6.2";
		System.out.println(cfe.getTlvDefintion(sTlv));	
		
		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");	
		System.out.println("+--------------------------------------------------------------------------------------------------------------------+");	
		ConfigurationFileExport cfePacketCable = new ConfigurationFileExport(ConfigurationFileExport.PKT_CBL_VER_20);
			
		sTlv = "64" ;			
		System.out.println(cfePacketCable.getTlvDefintion(sTlv));

		
		
	}

	

	
	
}
