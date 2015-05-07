package com.comcast.oscar.examples;

import java.io.FileNotFoundException;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class PrettyPrintTest {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		/***************************
		 * Text to Binary
		 ***************************/
		ConfigurationFileImport cfi = null;
		try {
			cfi = new ConfigurationFileImport(TestDirectoryStructure.fInputDirFileName("bsod.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ConfigurationFileException e) {
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
