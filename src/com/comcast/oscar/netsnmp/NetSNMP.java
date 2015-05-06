package com.comcast.oscar.netsnmp;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NetSNMP extends ArrayList<String> {

	private static final long serialVersionUID = 1L;
	private Boolean debug = Boolean.FALSE;
	
	/**
	 * 
	 */
	public NetSNMP() {
		super();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> OidName2OidDec() {
		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD + 	
								Constants.MIB_PARAMETER + 
								Constants.SNMP_TRANSLATE_OID_NAME_2_OID_DEC +
								super.toString().replace(',', ' ').replace('[', ' ').replace(']', ' ');
		
		return runSnmpTranslate(sSnmpTranslate);
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> OidDec2OidName() {
		
		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD +  	
								Constants.MIB_PARAMETER + 
								Constants.SNMP_TRANSLATE_OID_NAME_2_OID_DEC +
								super.toString().replace(',', ' ').replace('[', ' ').replace(']', ' ');
		
		return runSnmpTranslate(sSnmpTranslate);

	}

	/**
	 * 
	 * @param sSnmpTranslateCMD
	 * @return
	 */
	private ArrayList<String> runSnmpTranslate(String sSnmpTranslateCMD) {
		
		ArrayList<String> als = new ArrayList<String>();
		
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(sSnmpTranslateCMD);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		String sStd_IO = "";

		/*Read the output from the command If Any */

		int iCounter = 0;
		try {
			while ((sStd_IO = stdInput.readLine()) != null) {
				
				als.add(sStd_IO);
				
				if (debug)
					System.out.println(++iCounter + ": " + sStd_IO);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while ((sStd_IO = stdError.readLine()) != null) {
				
				als.add(sStd_IO);
				
				if (debug)
					System.out.println("OUT:" + sStd_IO);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return als;
	}
}

