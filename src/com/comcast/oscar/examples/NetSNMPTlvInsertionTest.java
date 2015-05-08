package com.comcast.oscar.examples;

import org.snmp4j.asn1.BER;

import com.comcast.oscar.configurationfile.CommonTlvInsertions;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class NetSNMPTlvInsertionTest {

	public static void main(String[] args) {


		ConfigurationFileExport cfe = 
				new ConfigurationFileExport (TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt.bin"),
											 ConfigurationFileExport.DOCSIS_VER_31);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		
		ConfigurationFileImport cfi = 
				new ConfigurationFileImport(new StringBuilder(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV)));
		
		ConfigurationFile cf = new ConfigurationFile(cfi.getConfigurationFileType(),cfi.getTlvBuilder());
		
		CommonTlvInsertions.insertSnmpOID("docsDevNmAccessIp.1", BER.INTEGER, 1, cf, CommonTlvInsertions.FINALIZE_FALSE);
		
		cfe = new ConfigurationFileExport(cf);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		
	}

}
