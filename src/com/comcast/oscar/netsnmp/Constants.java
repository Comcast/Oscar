package com.comcast.oscar.netsnmp;

import java.util.regex.Pattern;

import com.comcast.oscar.utilities.DirectoryStructure;

public class Constants {
	
	public final static String SNMP_TRANSLATE_CMD =  "snmptranslate";
	public final static String SNMP_TRANSLATE_OID_NAME_2_OID_DEC = " -IR -On ";
	public final static String SNMP_TRANSLATE_OID_DEC_2_OID_NAME = " -Oas ";
	public final static String SNMP_TRANSLATE_DEBUG 	= " -D all ";
	public final static String SNMP_TRANSLATE_VERSION 	= " -V ";	
	public final static String MIB_PARAMETER = " -m ALL -M " + DirectoryStructure.fMibsTextDir();
	public final static String DOTTED_TEXTUAL_NetSNMP_MAP_FILE = "DottedTextualNetSNMPMap.json";
	public static final Pattern ISO_ORG_DOD_DOTTED = Pattern.compile("^\\.1\\.3\\.6");
	
}
