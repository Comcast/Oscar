package com.comcast.oscar.examples;

import com.comcast.oscar.netsnmp.NetSNMP;

public class NetSNMPGetDescription {

	public static void main(String[] args) {
		
		System.out.println(NetSNMP.getDescription("docsDev"));

		System.out.println("\n\n\n");
		
		System.out.println(NetSNMP.getDescription("1.3.6.1.2.1.69"));
		
	}

}
