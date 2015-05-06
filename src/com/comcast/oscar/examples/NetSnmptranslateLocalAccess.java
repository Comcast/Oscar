package com.comcast.oscar.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.comcast.oscar.netsnmp.Constants;

public class NetSnmptranslateLocalAccess {

	public static void main(String[] args) {

		for (int iCount = 0; iCount < 1; iCount++)
			NetSnmptranslateLocalAccess.runSnmpTranslate();

	}
	
	public static void runSnmpTranslate() {
		
		String sSnmpTranslate = "snmptranslate" + 		
													Constants.MIB_PARAMETER + 
													Constants.SNMP_TRANSLATE_PARAMETER +
													"1.3.6.1.2.1.69" + " 1.3.6.1.2.1.69";
		
		System.out.println(sSnmpTranslate);
		
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(sSnmpTranslate);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		String sStd_IO = "";

		/*Read the output from the command If Any */
		try {
			while ((sStd_IO = stdInput.readLine()) != null) {
				System.out.println(sStd_IO);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while ((sStd_IO = stdError.readLine()) != null) {
				System.out.println(sStd_IO);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

}
