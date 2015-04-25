package com.comcast.oscar.examples;

import java.io.File;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class DPoEConfigurationFileTest {

	public static void main(String[] args) {

		File dpoeConfigFile = TestDirectoryStructure.fInputDirFileName("DPoE-1.cm");
		
		ConfigurationFileExport cfeDocsis = new ConfigurationFileExport (ConfigurationFileExport.DPOE_VER_20, dpoeConfigFile);
		
		System.out.println(cfeDocsis.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

	}

}
