package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.utilities.HexString;

public class PrettyPrintTest {

	public static void main(String[] args) {

		HexString hs = new HexString(HexString.fileToByteArray(TestDirectoryStructure.fInputDirFileName("bsod.txt")));
		
		//PrettyPrint pp = new PrettyPrint(hs.toASCII());
		
		//System.out.println(pp.toString());
		
		ConfigurationFileExport cfe = new ConfigurationFileExport (TestDirectoryStructure.fInputDirFileName("bsod.cm"));
		
		//Does not Export Default TLVs
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		
	}

}
