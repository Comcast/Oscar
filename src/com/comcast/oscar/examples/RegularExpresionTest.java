package com.comcast.oscar.examples;

/*
	Copyright 2015 Comcast Cable Communications Management, LLC
	___________________________________________________________________
	Licensed under the Apache License, Version 2.0 (the "License")
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/


import com.comcast.oscar.tlv.datatype.DataTypeDictionaryReference;
import com.comcast.oscar.tlv.datatype.DataTypeFormatConversion;
import com.comcast.oscar.utilities.HexString;


/**
 */
public class RegularExpresionTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		System.out.println("+----------------------------IPv6TransportAddress----------------------------+");	
		System.out.println(new HexString(DataTypeFormatConversion.ipv6TransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(65535)")).toString(":"));		
		System.out.println(new HexString(DataTypeFormatConversion.ipv6TransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(0)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv6TransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(70000)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv6TransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv6TransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv6TransportAddressToByteArray ("2002:558d:0:0:x:0:0:0(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv6TransportAddressToByteArray ("2002:558f:558f:558f:558f:558f:558f:558f")).toString(":"));

		System.out.println("+----------------------------IPv4TransportAddress----------------------------+");			
		System.out.println(new HexString(DataTypeFormatConversion.ipv4TransportAddressToByteArray ("255.1.1.1(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv4TransportAddressToByteArray ("255.1.1.1(0)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv4TransportAddressToByteArray ("1.1.1.1(65536")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv4TransportAddressToByteArray ("1.1.1.1(70000)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv4TransportAddressToByteArray ("256.1.1.1(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.ipv4TransportAddressToByteArray ("ff.ff.ff.ff(65535)")).toString(":"));

		System.out.println("+----------------------------INET-TransportAddress----------------------------+");			
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(65536)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(0)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(70000)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(65535)")).toString(":"));		
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("2002:558d:0:0:x:0:0:0(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("2002:558f:558f:558f:558f:558f:558f:558f")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("255.1.1.1(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("255.1.1.1(0)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("1.1.1.1(65536)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("1.1.1.1(70000)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("256.1.1.1(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("ff.ff.ff.ff(65535)")).toString(":"));
	

		System.out.println(new HexString(DataTypeFormatConversion.ipv6TransportAddressToByteArray ("2002:558d:0:0:0:0:0:0(65535)")).toString(":"));
		System.out.println(new HexString(DataTypeFormatConversion.inetTransportAddressToByteArray ("255.1.1.1(65535)")).toString(":"));
		
		System.out.println("+----------------------------DoubleByteArray----------------------------+");			

		byte[] bSubMgtGrp0 = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};		
		System.out.println(DataTypeFormatConversion.doubleByteArray(bSubMgtGrp0));

		byte[] bSubMgtGrp1 = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};		
		System.out.println(DataTypeFormatConversion.doubleByteArray(bSubMgtGrp1));

		String sDoubleByteArray = "(0,255)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)";
		System.out.println(new HexString(DataTypeFormatConversion.doubleByteArray(sDoubleByteArray)).toString(":"));
		
		sDoubleByteArray = "(0,255)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)";
		System.out.println(new HexString(DataTypeFormatConversion.doubleByteArray(sDoubleByteArray)).toString(":"));

		sDoubleByteArray = "(0,255)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)";
		DataTypeFormatConversion.doubleByteArrayValidate(sDoubleByteArray);
	
		sDoubleByteArray = "(0,255)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)(0,0)";
		DataTypeFormatConversion.doubleByteArrayValidate(sDoubleByteArray);

		System.out.println("+----------------------------BIT MASK FORMATING----------------------------+");
		
		String sBinaryBitMask = "000011110000111100001111000011110000111100001111000011110000111100001111";
		System.out.println(new HexString(DataTypeFormatConversion.binaryBitMaskToByteArray(sBinaryBitMask)).toString(":"));	
		
		byte[] bBinaryBitMask = new byte[] {(byte)0x00,(byte)0xF0,(byte)0x00,(byte)0xF0,(byte)0x00,(byte)0xF0,(byte)0x00,(byte)0xF0,(byte)0x00,(byte)0xF0};
		System.out.println(DataTypeFormatConversion.byteArrayBinaryBitMaskToString(bBinaryBitMask));	

		sDoubleByteArray = "(0,255)(0,0)(0,15)(0,0)(15,0)(0,0)";
		System.out.println("+--------------------------------------------------------------------------+");
		System.out.println(sDoubleByteArray);
		System.out.println(DataTypeFormatConversion.byteArrayBinaryBitMaskToString(DataTypeFormatConversion.doubleByteArray(sDoubleByteArray),16));
		System.out.println("+--------------------------------------------------------------------------+");
		System.out.println(sDoubleByteArray);
		System.out.println(DataTypeFormatConversion.byteArrayBinaryBitMaskToString(DataTypeFormatConversion.doubleByteArray(sDoubleByteArray),8));
		System.out.println("+--------------------------------------------------------------------------+");
		System.out.println(sDoubleByteArray);
		System.out.println(DataTypeFormatConversion.byteArrayBinaryBitMaskToString(DataTypeFormatConversion.doubleByteArray(sDoubleByteArray),4));
		System.out.println("+--------------------------------------------------------------------------+");
		System.out.println(sDoubleByteArray);
		System.out.println(DataTypeFormatConversion.byteArrayBinaryBitMaskToString(DataTypeFormatConversion.doubleByteArray(sDoubleByteArray),2));
		System.out.println("+--------------------------------------------------------------------------+");
		System.out.println(sDoubleByteArray);
		System.out.println(DataTypeFormatConversion.byteArrayBinaryBitMaskToString(DataTypeFormatConversion.doubleByteArray(sDoubleByteArray),1));	
	
		System.out.println("+----------------------------MacAddressBIT MASK FORMATING----------------------------+");
		byte[] bMacAddress1 = {	(byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 
								(byte) 0x02, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
								(byte) 0x03, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
		};

		byte[] bMacAddress = {	(byte) 0x01, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
		
		System.out.println(DataTypeFormatConversion.byteArrayToMacAddressFormat(bMacAddress));
		
		if (DataTypeFormatConversion.macAddressFormatValidation(DataTypeFormatConversion.byteArrayToMacAddressFormat(bMacAddress))) {
			System.out.println("MacAddress-Format-Correct");
		} else {
			System.out.println("MacAddress-Format-InCorrect");
		}
		
		System.out.println("Hex: " 
		+ new HexString(DataTypeFormatConversion.macAddressFormatToByteArray("(01:FF:FF:FF:FF:FF)(02:FF:FF:FF:FF:FF)(03:FF:FF:FF:FF:FF)")).toString(":"));
		
		System.out.println("+------------------------------------------------------------------------------------------------------------+");
		System.out.println("DataType: " + DataTypeFormatConversion.getDataTypeDisplayHelp(DataTypeDictionaryReference.DATA_TYPE_DOUBLE_BYTE_ARRAY));
		System.out.println("DataType: " + DataTypeFormatConversion.getDataTypeDisplayHelp(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR));
		System.out.println("DataType: " + DataTypeFormatConversion.getDataTypeDisplayHelp(DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR));
		System.out.println("DataType: " + DataTypeFormatConversion.getDataTypeDisplayHelp(DataTypeDictionaryReference.DATA_TYPE_STRING_BITS));
		System.out.println("DataType: " + DataTypeFormatConversion.getDataTypeDisplayHelp(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV6_ADDR));
		System.out.println("DataType: " + DataTypeFormatConversion.getDataTypeDisplayHelp(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV4_ADDR));
		
	}

}
