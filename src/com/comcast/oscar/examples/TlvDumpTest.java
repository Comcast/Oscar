package com.comcast.oscar.examples;

import java.util.ArrayList;

import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

public class TlvDumpTest {

	public static void main(String[] args) {

		
		HexString hs = new HexString(HexString.fileToByteArray(TestDirectoryStructure.fInputDirFileName("bsod.cm")));

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

	}

}
