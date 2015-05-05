package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class BerEncodingIssueTest {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
			
		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName("bsod.cm"));
		
		System.out.println(cfe.toPrettyPrint(1));
		
	}

}
