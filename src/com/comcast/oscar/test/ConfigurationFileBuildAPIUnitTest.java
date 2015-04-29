package com.comcast.oscar.test;

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

import com.comcast.oscar.compiler.packetcablecompiler.PacketCableConstants;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.snmp4j.smi.SMIManagerServiceException;

/**
 */
public class ConfigurationFileBuildAPIUnitTest {

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {

		try {
			SMIManagerService.SmiManagerStart();
		} catch (SMIManagerServiceException e2) {
			e2.printStackTrace();
		}

		boolean DOCSIS 					= false;
		boolean PACKET_CABLE 			= false;
		boolean ULTILS 					= false;
		boolean BULK_BUILD				= false;

		boolean DOCSIS_TEXT_BINARY 		= true;
		boolean DOCSIS_BINARY_TEXT 		= true;
		boolean DOCSIS_BINARY_BINARY 	= false;

		boolean PKTC_TEXT_BINARY 		= true;
		boolean PKTC_BINARY_TEXT 		= true;
		boolean PKTC_BINARY_BINARY 		= false;		
		boolean PKTC_DIGIT_MAP 			= true;

		boolean BULK_BUILD_TEXT			= false;
		boolean BULK_BUILD_BINARY		= false;


		ConfigurationFileBuildAPITestMethods cfbatm = new ConfigurationFileBuildAPITestMethods();

		/* ***************************************************************
		 * 							DOCSIS
		 *****************************************************************/

				
		/*Text To Binary*/
		if (DOCSIS|DOCSIS_TEXT_BINARY) {

			cfbatm.TextToBinaryDOCSIS();

			cfbatm.TextToBinaryInsertManufactureCVC();

			cfbatm.TextToBinaryInsertCoSignCVC();

			cfbatm.TextToBinaryInsertDownstreamFrequecy();

			cfbatm.TextToBinaryInsertMaxCPE();

			cfbatm.TextToBinaryInsertTLV();
			
			cfbatm.TextToBinaryFullDOCSISConfiguration();
			
			cfbatm.TextToBinaryInsertTFTPServerAddress();
			
			cfbatm.TextToBinaryInsertOID();
			
		}

		/*Binary To Text*/
		if (DOCSIS|DOCSIS_BINARY_TEXT) {

			cfbatm.BinaryToTextDOCSIS();

			cfbatm.BinaryToTextDOCSISMaxCPE();

			cfbatm.BinaryToTextDOCSISFirmwareFileName();

			cfbatm.BinaryToTextDOCSISManCVC();

			cfbatm.BinaryToTextDOCSISCoSignCVC();

			cfbatm.BinaryToTextDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC();
			
			cfbatm.BinaryToTextDOCSISTFTPServerAddress();
			
			cfbatm.BinaryToTextInsertOID();
		}

		/*Binary To Binary*/
		if (DOCSIS|DOCSIS_BINARY_BINARY) {
			cfbatm.BinaryToBinaryDOCSIS();
		}

		/* ***************************************************************
		 * 							Packet Cable
		 *****************************************************************/

		/* Text To Binary */
		if (PACKET_CABLE|PKTC_TEXT_BINARY) {
			cfbatm.TextToBinaryPacketCable(ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE);

			cfbatm.TextToBinaryPacketCable(ConfigurationFileTypeConstants.PKT_CABLE_15_CONFIGURATION_TYPE);

			cfbatm.TextToBinaryPacketCable(ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE);
		}

		/* Binary To Text */
		if (PACKET_CABLE|PKTC_BINARY_TEXT) {

			cfbatm.BinaryToTextPacketCable();

			cfbatm.GetDigitMapFromBinary();
		}

		/* Binary To Binary */
		if (PACKET_CABLE|PKTC_BINARY_BINARY) {
			cfbatm.BinaryToBinaryPacketCable();
		}


		/* DigitMap Operations */
		if (PACKET_CABLE|PKTC_DIGIT_MAP) {

			cfbatm.TextToBinaryPacketCableInsertDigitMap();

			cfbatm.TextToBinaryInsertMultiDigitMap();

			cfbatm.BinaryToBinaryInsertMultiDigitMap();

			cfbatm.BinaryToTextInsertMultiDigitMap(); 
		}

		/* ***************************************************************
		 * 							ULTILITIES
		 *****************************************************************/		

		if (ULTILS) {

			cfbatm.PrintDefaultDOCSISConfigurationTemplate();

			cfbatm.PrintDefaultPacketCableConfigurationTemplate();

			cfbatm.PrintTlvDotNotationDOCSISDefinition();

			cfbatm.PrintTlvDotNotationPacketCableDefinition();

			cfbatm.PrintDOCSISJSONDump();

			cfbatm.PrintPacketCableJSONDump();

			cfbatm.PrintTlvToJSON();

			cfbatm.PrintJSONToTlv();
		}

		/* ***************************************************************
		 * 					BULK BUILD OPERATIONS
		 *****************************************************************/

		if (BULK_BUILD|BULK_BUILD_TEXT) {
			cfbatm.BulkBuildToText();
		}

		if (BULK_BUILD|BULK_BUILD_BINARY) {
			cfbatm.BulkBuildToBinary();	
		}

		/* END OF MAIN*/
	}
}
