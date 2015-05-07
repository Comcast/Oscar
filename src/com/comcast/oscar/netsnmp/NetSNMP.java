package com.comcast.oscar.netsnmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NetSNMP extends ArrayList<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2264150856597322191L;
	private static Boolean debug = Boolean.FALSE;

	/**
	 * 
	 */
	public NetSNMP() {
		super();
	}

	/**
	 * 
	 * Convert: docsDevNmAccessIp.1 -> .1.3.6.1.2.1.69.1.2.1.2.1
	 * 
	 * @return - ArrayList<String> -> .1.3.6.1.2.1.69.1.2.1.2.1 ....
	 */
	public ArrayList<String> toDottedOID() {

		/* Not a clean way to do it, but it works */
		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD + 	
				Constants.MIB_PARAMETER + 
				Constants.SNMP_TRANSLATE_OID_NAME_2_OID_DEC +
				super.toString().replace(',', ' ')
				.replace('[', ' ')
				.replace(']', ' ')
				.replaceAll("\\s+1", " .iso")
				.replaceAll("\\s+\\.1", " .iso");

		return runSnmpTranslate(sSnmpTranslate);
	}
	
	/**
	 * 
	 * 
	 * Convert: docsDevNmAccessIp.1 -> .1.3.6.1.2.1.69.1.2.1.2.1
	 * 
	 * @param sOID
	 * @return
	 */
	public static String toDottedOID(String sOID) {

		/* Not a clean way to do it, but it works */
		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD + 	
				Constants.MIB_PARAMETER + 
				Constants.SNMP_TRANSLATE_OID_NAME_2_OID_DEC +
				sOID.replaceAll("\\s+1", " .iso")
					.replaceAll("\\s+\\.1", " .iso");

		return runSnmpTranslate(sSnmpTranslate).get(0);
	}

	/**
	 * 
	 * Convert: .1.3.6.1.2.1.69.1.2.1.2.1 -> docsDevNmAccessIp.1
	 * 
	 * @return ArrayList<String> -> docsDevNmAccessIp.1 ....
	 */
	public ArrayList<String> toTextualOID() {

		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD +  	
				Constants.MIB_PARAMETER + 
				Constants.SNMP_TRANSLATE_OID_DEC_2_OID_NAME +
				super.toString().replace(',', ' ').replace('[', ' ').replace(']', ' ');

		if (debug)
			System.out.println("NetSNMP.OidDec2OidName(): " + sSnmpTranslate);

		return runSnmpTranslate(sSnmpTranslate);

	}

	/**
	 * 
	 * @param sOID - Input MUST be a DottedOID - .1.3.6
	 * @return
	 */
	public static String toTextualOID(String sOID) {

		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD +  	
				Constants.MIB_PARAMETER + 
				Constants.SNMP_TRANSLATE_OID_DEC_2_OID_NAME +
				sOID;

		if (true)
			System.out.println("NetSNMP.OidDec2OidName(): " + sSnmpTranslate);

		return runSnmpTranslate(sSnmpTranslate).get(0);

	}

	/**
	 * 
	 * @return
	 */
	public boolean isSnmptranslateInstalled() {

		String sSnmpTranslate = Constants.SNMP_TRANSLATE_CMD +  	
				Constants.SNMP_TRANSLATE_VERSION;

		return runSnmpTranslate(sSnmpTranslate).get(0).contains("NET-SNMP");
	}

	/**
	 * 
	 * If OID starts with .1.3.6 it is considered a DottedOID
	 * 
	 * @param sOID
	 * @return
	 */
	public static boolean isDottedOID(String sOID) {

		if (Constants.ISO_ORG_DOD_DOTTED.matcher(sOID).find()) {
			return true;
		}

		return false;	
	}

	/**
	 * 
	 * @param sSnmpTranslateCMD
	 * @return
	 */
	private static ArrayList<String> runSnmpTranslate(String sSnmpTranslateCMD) {

		boolean localDebug = Boolean.FALSE;

		if (debug|localDebug)
			System.out.println(sSnmpTranslateCMD);

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

				//Clean up White Space
				if (!sStd_IO.isEmpty())
					als.add(sStd_IO);

				if (debug|localDebug)
					System.out.println(++iCounter + " IN: " + sStd_IO);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while ((sStd_IO = stdError.readLine()) != null) {

				als.add(sStd_IO);

				if (debug|localDebug)
					System.out.println(++iCounter + " OUT: " + sStd_IO);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return als;
	}
}

