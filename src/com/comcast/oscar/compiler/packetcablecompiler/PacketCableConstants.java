package com.comcast.oscar.compiler.packetcablecompiler;

import com.comcast.oscar.constants.Constants;

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


public class PacketCableConstants {
	
	public static final byte FILE_MARKER 	=	(byte)0xFE;
	public static final byte START_OF_FILE 	= 	(byte)0x01;
	public static final byte END_OF_FILE 	= 	(byte)0xFF;
	
	public static final byte SNMP_TLV_11 		=	(byte)0x0B;
	public static final byte SNMP_TLV_64 		=	(byte)0x40;
	
	public static final String PKT_CABLE_10_BASIC_AUTH_OID 	= "1.3.6.1.4.1.4491.2.2.1.1.2.7.0";
	public static final String PKT_CABLE_15_BASIC_AUTH_OID 	= "1.3.6.1.4.1.4491.2.2.1.1.2.7.0";
	public static final String PKT_CABLE_20_BASIC_AUTH_OID 	= "1.3.6.1.2.1.140.1.2.11.0";
	public static final String PKT_CABLE_DIGIT_MAP_OID		= "1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.1";
	
	public static final int PACKET_CABLE_STANDARD_BYTE_LENGTH = 1;
	public static final int SNMP_TLV_64_BYTE_LENGTH		= 2;
}
