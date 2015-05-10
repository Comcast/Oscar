package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class NetSNMPBinToTextToText {

	public static void main(String[] args) {

		ConfigurationFileExport cfe = 
				new ConfigurationFileExport (TestDirectoryStructure.fInputDirFileName("bsod.cm"),
											 ConfigurationFileExport.DOCSIS_VER_31);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

		
		ConfigurationFileImport cfi = 
				new ConfigurationFileImport(new StringBuilder(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV)));
		
		ConfigurationFile cf = new ConfigurationFile(cfi.getConfigurationFileType(),cfi.getTlvBuilder());
		
		
		cfe = new ConfigurationFileExport(cf);
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
	}

}
