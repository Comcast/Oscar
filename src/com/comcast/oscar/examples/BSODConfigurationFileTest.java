package com.comcast.oscar.examples;

import java.io.FileNotFoundException;

import com.comcast.oscar.configurationfile.ConfigrationFileException;
import com.comcast.oscar.configurationfile.ConfigrationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class BSODConfigurationFileTest {

	public static void main(String[] args) {

		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName("bsod.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		//Convert to a Configuration File Object
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30,cfi.getTlvBuilder());

		ConfigurationFileExport cfe = new ConfigurationFileExport(cf);
		
		System.out.print(cfe.toPrettyPrint(1));
		
	}

}
