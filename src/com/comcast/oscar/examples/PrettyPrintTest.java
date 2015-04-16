package com.comcast.oscar.examples;

import com.comcast.oscar.utilities.PrettyPrint;

public class PrettyPrintTest {

	public static void main(String[] args) {

		String sCode = "One { Two Three Four Five; Two Three Four Five; Two Three Four Five;}{Two Three}}";
		
		StringBuilder sbCode = new StringBuilder(sCode);
		
		PrettyPrint pp = new PrettyPrint(sbCode);
		
		System.out.println(pp.toString());
		
	}

}
