package com.comcast.oscar.examples;

import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.utilities.HexString;
import com.comcast.oscar.utilities.PrettyPrint;

public class PrettyPrintTest {

	public static void main(String[] args) {

		HexString hs = new HexString(HexString.fileToByteArray(TestDirectoryStructure.fInputDirFileName("bsod.txt")));
		
		PrettyPrint pp = new PrettyPrint(hs.toASCII());
		
		System.out.println(pp.toString());
		
	}

}
