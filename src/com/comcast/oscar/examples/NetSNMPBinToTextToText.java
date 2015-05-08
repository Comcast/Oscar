package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class NetSNMPBinToTextToText {

	public static void main(String[] args) {

		for (int iCounter = 0; iCounter<100; iCounter++) {

			ConfigurationFileExport cfe = 
					new ConfigurationFileExport (TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt.bin"),
							ConfigurationFileExport.DOCSIS_VER_31);

			System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));


			ConfigurationFileImport cfi = 
					new ConfigurationFileImport(new StringBuilder(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV)));

			ConfigurationFile cf = new ConfigurationFile(cfi.getConfigurationFileType(),cfi.getTlvBuilder());


			cfe = new ConfigurationFileExport(cf);
			System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		}
	}

}
