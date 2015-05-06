package com.comcast.oscar.netsnmp;

import com.comcast.oscar.utilities.DirectoryStructure;

public class Constants {
	
	public final static String SNMP_TRANSLATE_CMD =  "snmptranslate";
	public final static String MIB_PARAMETER = " -m ALL -M " + DirectoryStructure.fMibsTextDir();
	public final static String SNMP_TRANSLATE_OID_NAME_2_OID_DEC = " -IR -On ";
	public final static String SNMP_TRANSLATE_OID_DEC_2_OID_NAME = " -Oas ";
	public final static String SNMP_TRANSLATE_DEBUG 	= " -D all ";
	public final static String SNMP_TRANSLATE_VERSION 	= " -V ";
	
}
