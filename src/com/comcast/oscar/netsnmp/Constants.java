package com.comcast.oscar.netsnmp;

import com.comcast.oscar.utilities.DirectoryStructure;
import java.util.regex.Pattern;

public class Constants {

  public static final String SNMP_TRANSLATE_CMD = "snmptranslate";
  public static final String SNMP_TRANSLATE_OID_NAME_2_OID_DEC = " -IR -On ";
  public static final String SNMP_TRANSLATE_OID_DEC_2_OID_NAME = " -Oas ";
  public static final String SNMP_TRANSLATE_DEBUG = " -D all ";
  public static final String SNMP_TRANSLATE_VERSION = " -V ";
  public static final String SNMP_TRANSLATE_DESCRIPTION_TEXTUAL_OID = " -IR -Td ";
  public static final String SNMP_TRANSLATE_DESCRIPTION_DOTTED_OID = " -Td ";
  public static final String MIB_PARAMETER = " -m ALL -M " + DirectoryStructure.fMibsTextDir();
  public static final String DOTTED_TEXTUAL_NetSNMP_MAP_FILE = "DottedTextualNetSNMPMap.json";
  public static final Pattern ISO_ORG_DOD_DOTTED = Pattern.compile("^(\\.1|1)\\.3\\.6");
}
