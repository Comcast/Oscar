package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class PrettyPrintSuppressTLVComment {

	public static void main(String[] args) {
		
		/***************************
		 * Binary to Text
		 ***************************/
		@SuppressWarnings("deprecation")
		ConfigurationFileExport cfe = new ConfigurationFileExport (TestDirectoryStructure.fInputDirFileName("cm.bin"));
			
		/* No Suppression */
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

		/* With Suppression */
		System.out.println(cfe.toPrettyPrint(	ConfigurationFileExport.EXPORT_FOUND_TLV,
												ConfigurationFileExport.SUPPRESS_TLV_COMMENT));


	}

}
