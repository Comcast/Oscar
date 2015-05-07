package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class NetSNMPBinToText {

	public static void main(String[] args) {

		ConfigurationFileExport cfe = 
				new ConfigurationFileExport (TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin"),
											 ConfigurationFileExport.DOCSIS_VER_31);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

	}

}
