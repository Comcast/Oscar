package com.comcast.oscar.examples;

import com.comcast.oscar.netsnmp.NetSNMP;

/**
 * @bannerLicense
	Copyright 2015 Comcast Cable Communications Management, LLC<br>
	___________________________________________________________________<br>
	Licensed under the Apache License, Version 2.0 (the "License")<br>
	you may not use this file except in compliance with the License.<br>
	You may obtain a copy of the License at<br>
	http://www.apache.org/licenses/LICENSE-2.0<br>
	Unless required by applicable law or agreed to in writing, software<br>
	distributed under the License is distributed on an "AS IS" BASIS,<br>
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
	See the License for the specific language governing permissions and<br>
	limitations under the License.<br>


 * @author Maurice Garcia (maurice.garcia.2015@gmail.com)
 */


public class NetSNMPGetDescription {

	public static void main(String[] args) {
		
		System.out.println(NetSNMP.getDescription("docsDev"));

		System.out.println("\n\n\n");
		
		System.out.println(NetSNMP.getDescription("1.3.6.1.2.1.69"));
		
	}

}
