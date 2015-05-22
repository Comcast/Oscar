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
	
	 @author Allen Flickinger (allen.flickinger@gmail.com)

*/

import java.io.File;
import java.util.Arrays;

import com.comcast.oscar.ber.BERService;
import com.comcast.oscar.cli.CommandRun;
import com.comcast.oscar.utilities.DirectoryStructure;

/**
 */
public class ConfigurationFileBuildCLITestMethods implements TestMethod {

	public void PrintVAR() {
		/* Start Banner */
		System.out.println("+---------------PrintVAR Start---------------+");
		
		/* Variables */
		System.out.println("Active Directory: " + DirectoryStructure.sActivePath());
		System.out.println("Base Directory: " + DirectoryStructure.sBasePath());
		System.out.println("Certificate Bulk Directory: " + DirectoryStructure.fCertificatesDir());
		
		System.out.println("Test Active Directory: " + DirectoryStructure.sActivePath());
		System.out.println("Test Base Directory: " + DirectoryStructure.sBasePath());
		System.out.println("Test Input Bulk Directory: " + TestDirectoryStructure.inputBulkDir());
		System.out.println("Test Input Directory: " + TestDirectoryStructure.inputDir());
		System.out.println("Test Output Bulk Directory: " + TestDirectoryStructure.outputBulkDir());
		System.out.println("Test Output Directory: " + TestDirectoryStructure.outputDir());
		
		/* End Banner */
		System.out.println("+---------------PrintVAR End-----------------+");
	}
	
	/**
	 * Method BinaryToBinaryDOCSIS.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToBinaryDOCSIS()
	 */
	public void BinaryToBinaryDOCSIS() {
		/* Start Banner */
		System.out.println("+---------------BinaryToBinaryDOCSIS Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.bin.bin").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}
		
		/* End Banner */
		System.out.println("+---------------BinaryToBinaryDOCSIS End-----------------+");
	}

	/**
	 * Method BinaryToBinaryDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToBinaryDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC()
	 */
	public void BinaryToBinaryDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC() {
		/* Start Banner */
		System.out.println("+---------------BinaryToBinaryDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[11];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.bin.TlvFirmwareManCoSignCVC.bin").toString();
		arguments[5] = "-m";
		arguments[6] = "32";
		arguments[7] = "-f";
		arguments[8] = TestDirectoryStructure.fInputDirFileName("generic-firmware.txt").toString();
		arguments[9] = "-cvc";
		arguments[10] = "c=COMCAST_CVC.der";
		
		/* Print arguments */
		System.out.println(Arrays.toString(arguments));
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("OUTPUT FILE " + arguments[4] + " exists");
		} else {
			System.err.println("OUTPUT FILE " + arguments[4] + " does not exist");
		}
		
		/* End Banner */
		System.out.println("+---------------BinaryToBinaryDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC End-----------------+");
	}

	/**
	 * Method BinaryToBinaryPacketCable.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToBinaryPacketCable()
	 */
	public void BinaryToBinaryPacketCable() {
		/* Start Banner */
		System.out.println("+---------------BinaryToBinaryPacketCable Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.bin").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("IMS-PKT-CABLE-CONFIG.bin.bin").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}		
		
		/* End Banner */
		System.out.println("+---------------BinaryToBinaryPacketCable End-----------------+");
	}

	 
	/**
	 * Method BinaryToTextDOCSIS.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSIS()
	 */
	public void BinaryToTextDOCSIS() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextDOCSIS Start---------------+");
		
		
		/* Define arguments */
		String arguments[] = new String[3];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------BinaryToTextDOCSIS End-----------------+");
	}

	/**
	 * Method BinaryToTextDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC()
	 */
	public void BinaryToTextDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[9];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
		arguments[3] = "-m";
		arguments[4] = "32";
		arguments[5] = "-f";
		arguments[6] = TestDirectoryStructure.fInputDirFileName("generic-firmware.txt").toString();
		arguments[7] = "-cvc";
		arguments[8] = "c=COMCAST_CVC.der";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------BinaryToTextDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC End-----------------+");
	}

	/**
	 * Method BinaryToTextDOCSISCoSignCVC.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISCoSignCVC()
	 */
	public void BinaryToTextDOCSISCoSignCVC() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextDOCSISCoSignCVC Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
		arguments[3] = "-cvc";
		arguments[4] = "c=COMCAST_CVC.der";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------BinaryToTextDOCSISCoSignCVC End-----------------+");
	}

	/**
	 * Method BinaryToTextDOCSISFirmwareFileName.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISFirmwareFileName()
	 */
	public void BinaryToTextDOCSISFirmwareFileName() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextDOCSISFirmwareFileName Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
		arguments[3] = "-f";
		arguments[4] = TestDirectoryStructure.fInputDirFileName("generic-firmware.txt").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------BinaryToTextDOCSISFirmwareFileName End-----------------+");
	}

	/**
	 * Method BinaryToTextDOCSISManCVC.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISManCVC()
	 */
	public void BinaryToTextDOCSISManCVC() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextDOCSISManCVC Start---------------+");	
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
		arguments[3] = "-cvc";
		arguments[4] = "c=COMCAST_CVC.der";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------BinaryToTextDOCSISManCVC End-----------------+");
	}

	/**
	 * Method BinaryToTextDOCSISMaxCPE.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISMaxCPE()
	 */
	public void BinaryToTextDOCSISMaxCPE() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextDOCSISMaxCPE Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
		arguments[3] = "-m";
		arguments[4] = "32";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------BinaryToTextDOCSISMaxCPE End-----------------+");
	}

	/**
	 * Method BinaryToTextPacketCable.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextPacketCable()
	 */
	public void BinaryToTextPacketCable() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextPacketCable Start---------------+");
		
		
		/* Define arguments */
		String arguments[] = new String[3];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.bin").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------BinaryToTextPacketCable End-----------------+");
	}

	/**
	 * Method BinaryToTextPacketCableInsertDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextPacketCableInsertDigitMap()
	 */
	public void BinaryToTextPacketCableInsertDigitMap() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextPacketCableInsertDigitMap Start---------------+");

		/* Define arguments */
		String arguments[] = new String[8];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG-NO-DIGITMAP.bin").toString();
		arguments[3] = "-dm";
		arguments[4] = TestDirectoryStructure.fInputDirFileName("DigitMap.txt").toString();
		arguments[5] = "-s";
		arguments[6] = "p";
		arguments[7] = "2.0";
		

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* End Banner */
		System.out.println("+---------------BinaryToTextPacketCableInsertDigitMap End-----------------+");
	}

	/**
	 * Method GetDigitMapFromBinary.
	 * @see com.comcast.oscar.test.TestMethod#GetDigitMapFromBinary()
	 */
	public void GetDigitMapFromBinary() {
		/* Start Banner */
		System.out.println("+---------------GetDigitMapFromBinary Start---------------+");
		
		
		/* Define arguments */
		String arguments[] = new String[3];
		arguments[0] = "-ddm";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.bin").toString();

				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------GetDigitMapFromBinary End-----------------+");
	}

	/**
	 * Method PrintBinaryToTextDOCSISHexDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextDOCSISHexDump()
	 */
	public void PrintBinaryToTextDOCSISHexDump() {
		/* Start Banner */
		System.out.println("+---------------PrintBinaryToTextDOCSISHexDump Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[3];
		arguments[0] = "-x";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintBinaryToTextDOCSISHexDump End-----------------+");
	}
	
	/**
	 * Method PrintBinaryToTextDOCSISJSONDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextDOCSISJSONDump()
	 */
	public void PrintBinaryToTextDOCSISJSONDump() {
		/* Start Banner */
		System.out.println("+---------------PrintBinaryToTextDOCSISJSONDump Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[3];
		arguments[0] = "-j";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintBinaryToTextDOCSISJSONDump End-----------------+");
	}

	/**
	 * Method PrintBinaryToTextPacketCableDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextPacketCableDigitMap()
	 */
	public void PrintBinaryToTextPacketCableDigitMap() {
		/* Start Banner */
		System.out.println("+---------------PrintBinaryToTextPacketCableDigitMap Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[6];
		arguments[0] = "-ddm";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.bin").toString();
		arguments[3] = "-s";
		arguments[4] = "p";
		arguments[5] = "2.0";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintBinaryToTextPacketCableDigitMap End-----------------+");
	}
	
	/**
	 * Method PrintBinaryToTextPacketCableHexDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextPacketCableHexDump()
	 */
	public void PrintBinaryToTextPacketCableHexDump() {
		/* Start Banner */
		System.out.println("+---------------PrintBinaryToTextPacketCableHexDump Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-x";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.bin").toString();
		arguments[3] = "-s";
		arguments[4] = "p2";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintBinaryToTextPacketCableHexDump End-----------------+");
	}

	/**
	 * Method PrintBinaryToTextPacketCableJSONDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextPacketCableJSONDump()
	 */
	public void PrintBinaryToTextPacketCableJSONDump() {
		/* Start Banner */
		System.out.println("+---------------PrintBinaryToTextPacketCableJSONDump Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[6];
		arguments[0] = "-j";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.bin").toString();
		arguments[3] = "-s";
		arguments[4] = "p";
		arguments[5] = "2.0";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintBinaryToTextPacketCableJSONDump End-----------------+");
	}

	/**
	 * Method PrintDefaultDOCSISConfigurationTemplate.
	 * @see com.comcast.oscar.test.TestMethod#PrintDefaultDOCSISConfigurationTemplate()
	 */
	public void PrintDefaultDOCSISConfigurationTemplate() {
		/* Start Banner */
		System.out.println("+---------------PrintDefaultDOCSISConfigurationTemplate Start---------------+");
		
		
		/* Define arguments */
		String arguments[] = new String[4];
		arguments[0] = "-ftd";
		arguments[1] = "-s";
		arguments[2] = "d";
		arguments[3] = "2.0";
		
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintDefaultDOCSISConfigurationTemplate End-----------------+");		
	}

	/**
	 * Method PrintDefaultPacketCableConfigurationTemplate.
	 * @see com.comcast.oscar.test.TestMethod#PrintDefaultPacketCableConfigurationTemplate()
	 */
	public void PrintDefaultPacketCableConfigurationTemplate() {
		/* Start Banner */
		System.out.println("+---------------PrintDefaultPacketCableConfigurationTemplate Start---------------+");
		
		
		/* Define arguments */
		String arguments[] = new String[4];
		arguments[0] = "-ftd";
		arguments[1] = "-s";
		arguments[2] = "p";
		arguments[3] = "2.0";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintDefaultPacketCableConfigurationTemplate End-----------------+");
	}

	/**
	 * Method PrintDOCSISTlvToJSON.
	 * @see com.comcast.oscar.test.TestMethod#PrintDOCSISTlvToJSON()
	 */
	public void PrintDOCSISTlvToJSON() {
		/* Start Banner */
		System.out.println("+---------------PrintDOCSISTlvToJSON Start---------------+");
				
		/* Define arguments */
		String arguments[] = new String[2];
		arguments[0] = "-t2j";
		arguments[1] = "030101";
		
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		String secondArguments[] = new String[2];
		secondArguments[0] = arguments[0];
		secondArguments[1] = "010101";
		
		cmds.run(secondArguments);
		
		String thirdArguments[] = new String[2];
		thirdArguments[0] = arguments[0];
		thirdArguments[1] = "020101";
		
		cmds.run(thirdArguments);
		
		/* End Banner */
		System.out.println("+---------------PrintDOCSISTlvToJSON End-----------------+");	
	}

	 
	/**
	 * Method PrintJSONToTlv.
	 * @see com.comcast.oscar.test.TestMethod#PrintJSONToTlv()
	 */
	public void PrintJSONToTlv() {
		/* Start Banner */
		System.out.println("+---------------PrintJSONToTlv Start---------------+");
				
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-j2t";
		arguments[1] = TestDirectoryStructure.fInputDirFileName("JSON-TO-TLV.txt").toString();
		arguments[2] = "-s";
		arguments[3] = "p";
		arguments[4] = "2.0";
		
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintJSONToTlv End-----------------+");	
	}

	 
	/**
	 * Method PrintPacketCableTlvToJSON.
	 * @see com.comcast.oscar.test.TestMethod#PrintPacketCableTlvToJSON()
	 */
	public void PrintPacketCableTlvToJSON() {
		/* Start Banner */
		System.out.println("+---------------PrintPacketCableTlvToJSON Start---------------+");
				
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-t2j";
		arguments[1] = "0B153013060B2B06010201450102010201400400000000";
		arguments[2] = "-s";
		arguments[3] = "p";
		arguments[4] = "2.0";
		
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintPacketCableTlvToJSON End-----------------+");
	}

	 
	/**
	 * Method PrintTextToBinaryDOCSISHexDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryDOCSISHexDump()
	 */
	public void PrintTextToBinaryDOCSISHexDump() {
		/* Start Banner */
		System.out.println("+---------------PrintTextToBinaryDOCSISHexDump Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[3];
		arguments[0] = "-x";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintTextToBinaryDOCSISHexDump End-----------------+");
	}
	
	 
	/**
	 * Method PrintTextToBinaryDOCSISJSONDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryDOCSISJSONDump()
	 */
	public void PrintTextToBinaryDOCSISJSONDump() {
		/* Start Banner */
		System.out.println("+---------------PrintTextToBinaryDOCSISJSONDump Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[3];
		arguments[0] = "-j";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintTextToBinaryDOCSISJSONDump End-----------------+");
	}
	
	 
	/**
	 * Method PrintTextToBinaryPacketCableDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryPacketCableDigitMap()
	 */
	public void PrintTextToBinaryPacketCableDigitMap() {
		/* Start Banner */
		System.out.println("+---------------PrintTextToBinaryPacketCableDigitMap Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[6];
		arguments[0] = "-ddm";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.txt").toString();
		arguments[3] = "-s";
		arguments[4] = "p";
		arguments[5] = "2.0";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintTextToBinaryPacketCableDigitMap End-----------------+");
	}
	
	 
	/**
	 * Method PrintTextToBinaryPacketCableHexDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryPacketCableHexDump()
	 */
	public void PrintTextToBinaryPacketCableHexDump() {
		/* Start Banner */
		System.out.println("+---------------PrintTextToBinaryPacketCableHexDump Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[6];
		arguments[0] = "-x";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.txt").toString();
		arguments[3] = "-s";
		arguments[4] = "p";
		arguments[5] = "2.0";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintTextToBinaryPacketCableHexDump End-----------------+");
	}
	
	 
	/**
	 * Method PrintTextToBinaryPacketCableJSONDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryPacketCableJSONDump()
	 */
	public void PrintTextToBinaryPacketCableJSONDump() {
		/* Start Banner */
		System.out.println("+---------------PrintTextToBinaryPacketCableJSONDump Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[6];
		arguments[0] = "-j";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.txt").toString();
		arguments[3] = "-s";
		arguments[4] = "p";
		arguments[5] = "2.0";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintTextToBinaryPacketCableJSONDump End-----------------+");
	}

	 
	/**
	 * Method PrintTlvDotNotationDOCSISDefinition.
	 * @see com.comcast.oscar.test.TestMethod#PrintTlvDotNotationDOCSISDefinition()
	 */
	public void PrintTlvDotNotationDOCSISDefinition() {
		/* Start Banner */
		System.out.println("+---------------PrintTlvDotNotationDOCSISDefinition Start---------------+");
				
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-td";
		arguments[1] = "1";
		arguments[2] = "-s";
		arguments[3] = "d";
		arguments[4] = "2.0";
		
		/* Run commands */
		System.out.println("+---------------Test 1 Start---------------+");

		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		System.out.println("+---------------Test 1 End-----------------+");

		
		String secondArguments[] = new String[2];
		secondArguments[0] = arguments[0];
		secondArguments[1] = "26.1";
		
		System.out.println("+---------------Test 26.1 Start---------------+");
		cmds.run(secondArguments);
		System.out.println("+---------------Test 26.1 End-----------------+");

		
		String thirdArguments[] = new String[2];
		thirdArguments[0] = arguments[0];
		thirdArguments[1] = "254";
		
		System.out.println("+---------------TLV 254 Start---------------+");
		cmds.run(thirdArguments);
		System.out.println("+---------------TLV 254 End-----------------+");

		
		/* End Banner */
		System.out.println("+---------------PrintTlvDotNotationDOCSISDefinition End-----------------+");
	}

	 
	/**
	 * Method PrintTlvDotNotationPacketCableDefinition.
	 * @see com.comcast.oscar.test.TestMethod#PrintTlvDotNotationPacketCableDefinition()
	 */
	public void PrintTlvDotNotationPacketCableDefinition() {
		/* Start Banner */
		System.out.println("+---------------PrintTlvDotNotationPacketCableDefinition Start---------------+");
				
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-td";
		arguments[1] = "11";
		arguments[2] = "-s";
		arguments[3] = "p";
		arguments[4] = "2.0";
		
		/* Run commands */
		System.out.println("+---------------Test 11 Start---------------+");

		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		System.out.println("+---------------Test 11 End-----------------+");

		
		String secondArguments[] = new String[2];
		secondArguments[0] = arguments[0];
		secondArguments[1] = "64";
		
		System.out.println("+---------------Test 64 Start---------------+");
		cmds.run(secondArguments);
		System.out.println("+---------------Test 64 End-----------------+");

		
		String thirdArguments[] = new String[2];
		thirdArguments[0] = arguments[0];
		thirdArguments[1] = "254";
		
		System.out.println("+---------------TLV 254 Start---------------+");
		cmds.run(thirdArguments);
		System.out.println("+---------------TLV 254 End-----------------+");

		
		/* End Banner */
		System.out.println("+---------------PrintTlvDotNotationPacketCableDefinition End-----------------+");
	}

	 
	/**
	 * Method TextToBinaryDOCSIS.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryDOCSIS()
	 */
	public void TextToBinaryDOCSIS() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryDOCSIS Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.bin").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}
		
		/* End Banner */
		System.out.println("+---------------TextToBinaryDOCSIS End-----------------+");
	}

	 
	/**
	 * Method TextToBinaryInsertCoSignCVC.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertCoSignCVC()
	 */
	public void TextToBinaryInsertCoSignCVC() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryInsertCoSignCVC Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[7];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN-NO-MAN-CVC.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.bin.ManCVC.bin").toString();
		arguments[5] = "-cvc";
		arguments[6] = "c=COMCAST_CVC.der";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}
		
		/* End Banner */
		System.out.println("+---------------TextToBinaryInsertCoSignCVC End-----------------+");
	}

	 
	/**
	 * Method TextToBinaryInsertDownstreamFrequecy.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertDownstreamFrequecy()
	 */
	public void TextToBinaryInsertDownstreamFrequecy() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryInsertDownstreamFrequecy Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[7];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN-INSERT-DS-FREQ-723000000.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.bin.DownstreamFrequency.bin").toString();
		arguments[5] = "-df";
		arguments[6] = "723000000";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}
		
		/* End Banner */
		System.out.println("+---------------TextToBinaryInsertDownstreamFrequecy End-----------------+");
	}
	
	 
	/**
	 * Method TextToBinaryInsertManufactureCVC.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertManufactureCVC()
	 */
	public void TextToBinaryInsertManufactureCVC() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryInsertManufactureCVC Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[7];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN-NO-MAN-CVC.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.bin.ManCVC.bin").toString();
		arguments[5] = "-cvc";
		arguments[6] = "c=COMCAST_CVC.der";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}
		
		/* End Banner */
		System.out.println("+---------------TextToBinaryInsertManufactureCVC End-----------------+");
	}

	 
	/**
	 * Method TextToBinaryInsertMaxCPE.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertMaxCPE()
	 */
	public void TextToBinaryInsertMaxCPE() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryInsertMaxCPE Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[7];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN-INSERT-MAX-CPE-32.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.bin.MaxCPE.bin").toString();
		arguments[5] = "-m";
		arguments[6] = "32";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}
		
		/* End Banner */
		System.out.println("+---------------TextToBinaryInsertMaxCPE End-----------------+");
	}
	 
	/**
	 * Method TextToBinaryInsertTLV.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertTLV()
	 */
	public void TextToBinaryInsertTLV() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryInsertMaxCPE Start---------------+");

		/* Define arguments */
		String arguments[] = new String[7];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.bin.INSERT-TLV-020101.bin").toString();
		arguments[5] = "-t";
		arguments[6] = "020101";

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}

		/* End Banner */
		System.out.println("+---------------TextToBinaryInsertMaxCPE End-----------------+");
	}
	 
	/**
	 * Method TextToBinaryPacketCable.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryPacketCable()
	 */
	public void TextToBinaryPacketCable() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryPacketCable Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG.bin.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("IMS-PKT-CABLE-CONFIG.bin").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}		
		
		/* End Banner */
		System.out.println("+---------------TextToBinaryPacketCable End-----------------+");
	}

	 
	/**
	 * Method TextToBinaryPacketCableInsertDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryPacketCableInsertDigitMap()
	 */
	public void TextToBinaryPacketCableInsertDigitMap() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryPacketCableInsertDigitMap Start---------------+");

		/* Define arguments */
		String arguments[] = new String[10];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG-NO-DIGITMAP.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("IMS-PKT-CABLE-CONFIG.txt.InsertDigitMap.bin").toString();
		arguments[5] = "-dm";
		arguments[6] = TestDirectoryStructure.fInputDirFileName("DigitMap.txt").toString();
		arguments[7] = "-s";
		arguments[8] = "p";
		arguments[9] = "2.0";
		

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}

		/* End Banner */
		System.out.println("+---------------TextToBinaryPacketCableInsertDigitMap End-----------------+");
	}

	/**
	 * Method TextToBinaryPacketCable.
	 * @param iPacketCableVersion int
	 */
	public void TextToBinaryPacketCable(int iPacketCableVersion) {
		
	}

	public void PrintDOCSISJSONDump() {
		
	}

	public void PrintPacketCableJSONDump() {
		
	}

	public void PrintTlvToJSON() {
		
	}

	public void BulkBuildToText() {
		/* Start Banner */
		System.out.println("+---------------BulkBuildToText Start---------------+");

		/* Define arguments */
		String arguments[] = new String[6];
		arguments[0] = "-b";
		arguments[1] = "t";
		arguments[2] = TestDirectoryStructure.inputBulkDir().toString();
		arguments[3] = TestDirectoryStructure.outputBulkDir().toString();
		arguments[4] = "-df";
		arguments[5] = "723000000";
		arguments[4] = "-dm";
		arguments[5] = TestDirectoryStructure.inputDir().toString() + File.separator + "DigitMap.txt";

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* End Banner */
		System.out.println("+---------------BulkBuildToText End-----------------+");
	}

	public void BulkBuildToBinary() {
		/* Start Banner */
		System.out.println("+---------------BulkBuildToText Start---------------+");

		/* Define arguments */
		String arguments[] = new String[4];
		arguments[0] = "-b";
		arguments[1] = "b";
		arguments[2] = TestDirectoryStructure.inputBulkDir().toString();
		arguments[3] = TestDirectoryStructure.outputBulkDir().toString();

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* End Banner */
		System.out.println("+---------------BulkBuildToText End-----------------+");
	}

	 
	/**
	 * Method BinaryToBinaryInsertMultiDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToBinaryInsertMultiDigitMap()
	 */
	public void BinaryToBinaryInsertMultiDigitMap() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryPacketCableInsertDigitMap Start---------------+");

		/* Define arguments */
		String arguments[] = new String[10];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG-NO-DIGITMAP.bin").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("IMS-PKT-CABLE-CONFIG.bin.InsertDigitMap.bin").toString();
		arguments[5] = "-dm";
		arguments[6] = TestDirectoryStructure.fInputDirFileName("DigitMap.txt").toString() + "|1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.1" + " " + TestDirectoryStructure.fInputDirFileName("DigitMap.txt").toString() + "|1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.12";
		arguments[7] = "-s";
		arguments[8] = "p";
		arguments[9] = "2.0";

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}

		/* End Banner */
		System.out.println("+---------------TextToBinaryPacketCableInsertDigitMap End-----------------+");
	}

	 
	/**
	 * Method BinaryToTextInsertMultiDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextInsertMultiDigitMap()
	 */
	public void BinaryToTextInsertMultiDigitMap() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextInsertMultiDigitMap Start---------------+");

		/* Define arguments */
		String arguments[] = new String[8];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG-NO-DIGITMAP.bin").toString();
		arguments[3] = "-dm";
		arguments[4] = TestDirectoryStructure.fInputDirFileName("DigitMap.txt").toString() + "|1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.1" + " " + TestDirectoryStructure.fInputDirFileName("DigitMap.txt").toString() + "|1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.12";
		arguments[5] = "-s";
		arguments[6] = "p";
		arguments[7] = "2.0";
		
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* End Banner */
		System.out.println("+---------------BinaryToTextInsertMultiDigitMap End-----------------+");		
	}

	 
	/**
	 * Method TextToBinaryInsertMultiDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertMultiDigitMap()
	 */
	public void TextToBinaryInsertMultiDigitMap() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryInsertMultiDigitMap Start---------------+");

		/* Define arguments */
		String arguments[] = new String[10];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("IMS-PKT-CABLE-CONFIG-NO-DIGITMAP.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("IMS-PKT-CABLE-CONFIG.txt.InsertDigitMap.bin").toString();
		arguments[5] = "-dm";
		arguments[6] = TestDirectoryStructure.fInputDirFileName("DigitMap.txt").toString() + "|1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.1" + " " + TestDirectoryStructure.fInputDirFileName("DigitMap.txt").toString() + "|1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.12";
		arguments[7] = "-s";
		arguments[8] = "p";
		arguments[9] = "2.0";

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}

		/* End Banner */
		System.out.println("+---------------TextToBinaryInsertMultiDigitMap End-----------------+");
	}

	 
	/**
	 * Method TextToBinaryFullDOCSISConfiguration.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryFullDOCSISConfiguration()
	 */
	public void TextToBinaryFullDOCSISConfiguration() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Method TextToBinaryInsertTFTPServerAddress.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertTFTPServerAddress()
	 */
	public void TextToBinaryInsertTFTPServerAddress() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryInsertTFTPServerAddress Start---------------+");

		String sIPv4 = "v4=10.10.10.10";
		String sIPv6 = "v6=2001:558:ff2e:bacc:221:9bff:fe99:c7a2";
		
		/* Define arguments */
		String arguments[] = new String[8];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.txt.InsertTFTPv4.bin").toString();
		arguments[5] = "-T";
		arguments[6] = sIPv4;
		arguments[7] = "";

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}
		
		/* Define arguments */
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.txt.InsertTFTPv6.bin").toString();
		arguments[6] = sIPv6;

		/* Run commands */
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}
		
		/* Define arguments */
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.txt.InsertTFTPv4.InsertTFTPv6.bin").toString();
		arguments[6] = sIPv4;
		arguments[7] = sIPv6;

		/* Run commands */
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}


		/* End Banner */
		System.out.println("+---------------TextToBinaryInsertTFTPServerAddress End-----------------+");		
	}

	/**
	 * Method BinaryToTextDOCSISTFTPServerAddress.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISTFTPServerAddress()
	 */
	public void BinaryToTextDOCSISTFTPServerAddress() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextDOCSISTFTPServerAddress Start---------------+");
		
		String sIPv4 = "v4=10.10.10.10";
		String sIPv6 = "v6=2001:558:ff2e:bacc:221:9bff:fe99:c7a2";
		
		/* Define arguments */
		String arguments[] = new String[6];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
		arguments[3] = "-T";
		arguments[4] = sIPv4;
		arguments[5] = "";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* Define arguments */
		arguments[4] = sIPv6;
		
		/* Run commands */
		cmds.run(arguments);
		
		/* Define arguments */
		arguments[4] = sIPv4;
		arguments[5] = sIPv6;
		
		/* Run commands */
		cmds.run(arguments);
	
		/* End Banner */
		System.out.println("+---------------BinaryToTextDOCSISTFTPServerAddress End-----------------+");
	}

	/**
	 * Method TextToTextMergeTemplateDOCSIS.
	 * @see com.comcast.oscar.test.TestMethod#TextToTextMergeTemplateDOCSIS()
	 */
	public void TextToTextMergeTemplateDOCSIS() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Method BinaryToTextInsertOID.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextInsertOID()
	 */
	public void BinaryToTextInsertOID() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextInsertOID Start---------------+");
		
		String sOID = "sysContact.0";
		String sValue = "StringValueSnmpInsert";
		String sDataType = BERService.OCTETSTRING;
		//String sOID1 = "sysContact.1";
		//String sOID2 = "sysContact.2";
		
		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
		arguments[3] = "-O";
		arguments[4] = sOID + "|" + sValue + "|" + sDataType 
				//+ " " + sOID1 + "|" + sValue + "|" + sDataType 
				//+ " " + sOID2 + "|" + sValue + "|" + sDataType
				;
		
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* End Banner */
		System.out.println("+---------------BinaryToTextInsertOID End-----------------+");		
	}

	/**
	 * Method TextToBinaryInsertOID.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertOID()
	 */
	public void TextToBinaryInsertOID() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryInsertOID Start---------------+");

		String sOID = "sysContact.0";
		String sValue = "StringValueSnmpInsert";
		String sDataType = BERService.OCTETSTRING;
		//String sOID1 = "sysContact.1";
		//String sOID2 = "sysContact.2";
		
		
		/* Define arguments */
		String arguments[] = new String[7];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN.txt-SnmpOID-Insertion.bin").toString();
		arguments[5] = "-O";
		arguments[6] = sOID + "|" + sValue + "|" + sDataType 
				//+ " " + sOID1 + "|" + sValue + "|" + sDataType 
				//+ " " + sOID2 + "|" + sValue + "|" + sDataType
				;
		
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}

		/* End Banner */
		System.out.println("+---------------TextToBinaryInsertOID End-----------------+");
		
	}
	
	/**
	 * Method MIBSCompile.
	 * @see com.comcast.oscar.test.TestMethod#MIBSCompile()
	 */
	public void MIBSCompileDefault() {
		/* Start Banner */
		System.out.println("+---------------MIBSCompileDefault Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[1];
		arguments[0] = "-M";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------MIBSCompileDefault End-----------------+");
	}
	
	public void MIBSCompileTrueSet() {
		/* Start Banner */
		System.out.println("+---------------MIBSCompileTrueSet Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[2];
		arguments[0] = "-M";
		arguments[1] = "t";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------MIBSCompileTrueSet End-----------------+");
	}
	
	public void MIBSCompileFalseSet() {
		/* Start Banner */
		System.out.println("+---------------MIBSCompileFalseSet Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[2];
		arguments[0] = "-M";
		arguments[1] = "f";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------MIBSCompileFalseSet End-----------------+");
	}
	
	public void MIBSCompileTrueSetVerbose() {
		/* Start Banner */
		System.out.println("+---------------MIBSCompileTrueSetVerbose Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[3];
		arguments[0] = "-M";
		arguments[1] = "t";
		arguments[2] = "v";
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------MIBSCompileTrueSetVerbose End-----------------+");
	}
	
	/**
	 * Method PrintBinaryToTextDOCSISHexDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextDOCSISHexDump()
	 */
	public void PrintBinaryToTextDOCSISHexDumpToplevelTLV() {
		/* Start Banner */
		System.out.println("+---------------PrintBinaryToTextDOCSISHexDumpToplevelTLV Start---------------+");
		
		/* Define arguments */
		String arguments[] = new String[4];
		arguments[0] = "-x";
		arguments[1] = "t";
		arguments[2] = "-i";
		arguments[3] = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin").toString();
				
		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);
		
		/* End Banner */
		System.out.println("+---------------PrintBinaryToTextDOCSISHexDumpToplevelTLV End-----------------+");
	}
	
	/**
	 * Method TextToBinaryInsertTLV.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertTLV()
	 */
	public void BinaryToBinaryDPoE() {
		/* Start Banner */
		System.out.println("+---------------BinaryToBinaryDPoE Start---------------+");

		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("d11_m_fte6083_encap1q_3950.cm").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("d11_m_fte6083_encap1q_3950.cm").toString();

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}

		/* End Banner */
		System.out.println("+---------------BinaryToBinaryDPoE End-----------------+");
	}
	
	/**
	 * Method BinaryToTextDPoE.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDPoE()
	 */
	public void BinaryToTextDPoE() {
		/* Start Banner */
		System.out.println("+---------------BinaryToTextDPoE Start---------------+");

		/* Define arguments */
		String arguments[] = new String[6];
		arguments[0] = "-d";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("d11_m_fte6083_encap1q_3950.cm").toString();
		arguments[3] = "-s";
		arguments[4] = "de";
		arguments[5] = "2.0";
		

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* End Banner */
		System.out.println("+---------------BinaryToTextDPoE End-----------------+");
	}
	
	/**
	 * Method TextToBinaryDPoE.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryDPoE()
	 */
	public void TextToBinaryDPoE() {
		/* Start Banner */
		System.out.println("+---------------TextToBinaryDPoE Start---------------+");

		/* Define arguments */
		String arguments[] = new String[5];
		arguments[0] = "-c";
		arguments[1] = "-i";
		arguments[2] = TestDirectoryStructure.fInputDirFileName("d11_m_fte6083_encap1q_3950.cm.txt").toString();
		arguments[3] = "-o";
		arguments[4] = TestDirectoryStructure.fOutputDirFileName("d11_m_fte6083_encap1q_3950.cm").toString();

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* Checks */
		if(new File(arguments[4]).exists()) {
			System.out.println("Output file " + arguments[4] + " exists");
		} else {
			System.err.println("Output file " + arguments[4] + " does not exist");
		}

		/* End Banner */
		System.out.println("+---------------TextToBinaryDPoE End-----------------+");
	}
	
	/**
	 * Method MergeBulk.
	 * @see com.comcast.oscar.test.TestMethod#MergeBulk()
	 */
	public void MergeBulk() {
		/* Start Banner */
		System.out.println("+---------------MergeBulk Start---------------+");

		/* Define arguments */
		String arguments[] = new String[6];
		arguments[0] = "-mbb";
		arguments[1] = "b";
		arguments[2] = TestDirectoryStructure.fTestDirPath + File.separator + "inputmerge" + File.separator + "Model";
		arguments[3] = TestDirectoryStructure.fTestDirPath + File.separator + "inputmerge" + File.separator + "Tier";
		arguments[4] = TestDirectoryStructure.fTestDirPath + File.separator + "inputmerge" + File.separator + "CPE";
		arguments[5] = "o=outputTest";

		/* Run commands */
		CommandRun cmds = new CommandRun();
		cmds.run(arguments);

		/* End Banner */
		System.out.println("+---------------MergeBulk End-----------------+");
	}
}
