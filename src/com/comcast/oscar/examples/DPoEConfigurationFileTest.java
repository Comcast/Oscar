package com.comcast.oscar.examples;

import java.io.File;
import java.util.ArrayList;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

public class DPoEConfigurationFileTest {

	public static void main(String[] args) {

		
		/* BINARY FILE TO TEXT */
		File dpoeConfigFile = TestDirectoryStructure.fInputDirFileName("DPoE-1.cm");
		
		ConfigurationFileExport cfe = new ConfigurationFileExport (dpoeConfigFile,ConfigurationFileExport.DPOE_VER_20);
		
		//System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

		
		/*CONFIGURATION FILE OBJECT TO TEXT*/
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(ConfigurationFile.DPOE_VER_20,cfe.getTlvBuilder());
		} catch (TlvException e) {
			e.printStackTrace();
		}
				
		cfe = new ConfigurationFileExport (cf);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
				
		/*TlvBuilder to Text*/
		
		TlvBuilder tb = new TlvBuilder();
		try {
			tb.add(new HexString(cf.toByteArray()));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		cfe = new ConfigurationFileExport (tb,ConfigurationFileExport.DPOE_VER_20);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		
	}

}
