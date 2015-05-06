package com.comcast.oscar.netsnmp;

import com.comcast.oscar.utilities.DirectoryStructure;

public class Constants {
	
	public final static String MIB_PARAMETER 			= " -M all " + DirectoryStructure.fMibsTextDir();
	public final static String SNMP_TRANSLATE_PARAMETER = " -Ta ";
	public final static String SNMP_TRANSLATE_DEBUG 	= " -D all ";
	public final static String SNMP_TRANSLATE_VERSION 	= " -V ";
	
}
