package com.comcast.oscar.examples;

import java.io.FileNotFoundException;

import com.comcast.oscar.configurationfile.ConfigrationFileException;
import com.comcast.oscar.configurationfile.ConfigrationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.utilities.HexString;
import com.comcast.oscar.utilities.PrettyPrint;

public class PrettyPrintTest {

	public static void main(String[] args) {

		/***************************
		 * Text to Binary
		 ***************************/
		ConfigrationFileImport cfi = null;
		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName("bsod.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30,cfi.getTlvBuilder());
		
		ConfigurationFileExport cfe =  new ConfigurationFileExport (cf);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		System.out.println("+-------------------------------------------------------------------------------------------+");
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_DEFAULT_TLV));

		System.out.println("+-------------------------------------------------------------------------------------------+");		
		System.out.println("+-------------------------------------------------------------------------------------------+");		
		System.out.println("+-------------------------------------------------------------------------------------------+");		
		System.out.println("+-------------------------------------------------------------------------------------------+");		

		/***************************
		 * Binary to Text
		 ***************************/
		cfe = new ConfigurationFileExport (TestDirectoryStructure.fInputDirFileName("bsod.cm"));
		
		//Does not Export Default TLVs
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		System.out.println("+-------------------------------------------------------------------------------------------+");		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_DEFAULT_TLV));
		
	}

}
