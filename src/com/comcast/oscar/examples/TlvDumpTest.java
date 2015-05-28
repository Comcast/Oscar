package com.comcast.oscar.examples;

import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

public class TlvDumpTest {

	public static void main(String[] args) {

		
		HexString hs = new HexString(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName("DPoE-1-Test.bin")));

		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(hs);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		System.out.println(TlvBuilder.tlvDump(tb.toByteArray()));

	}

}
