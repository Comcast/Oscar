package com.comcast.oscar.examples;

import java.io.File;
import java.util.ArrayList;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

public class DPoEConfigurationFileTest {

	public static void main(String[] args) {

		File dpoeConfigFile = TestDirectoryStructure.fInputDirFileName("DPoE-1.cm");
		
		HexString hs = new HexString(HexString.fileToByteArray(dpoeConfigFile));

		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(hs);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ArrayList<byte[]> alb = (ArrayList<byte[]>) tb.sortByTopLevelTlv();
		
		for (byte[] ba : alb) {
			HexString hsTlv = new HexString(ba);
			System.out.println(hsTlv.toString(":"));		
		}
		
		
		ConfigurationFileExport cfeDocsis = new ConfigurationFileExport (dpoeConfigFile,ConfigurationFileExport.DPOE_VER_20);
		
		System.out.println(cfeDocsis.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

	}

}
