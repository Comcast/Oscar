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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.comcast.oscar.ber.BERService;
import com.comcast.oscar.buildbulk.BulkBuild;
import com.comcast.oscar.compiler.packetcablecompiler.PacketCableCompiler;
import com.comcast.oscar.compiler.packetcablecompiler.PacketCableConstants;
import com.comcast.oscar.configurationfile.CommonTlvInsertions;
import com.comcast.oscar.configurationfile.ConfigrationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigrationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.DigitMapOperation;
import com.comcast.oscar.sql.queries.DictionarySQLQueries;
import com.comcast.oscar.tlv.TlvAssembler;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.CheckSum;
import com.comcast.oscar.utilities.Disk;
import com.comcast.oscar.utilities.HexString;

/**
 */
public class ConfigurationFileBuildAPITestMethods implements TestMethod {

	public String SHARED_SECRET = "SHAREDSECRET";
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryDOCSIS()
	 */
	public void TextToBinaryDOCSIS() {
		String sFileName = "DOCSIS-GOLDEN.txt";
		String sTestMD5 = "2D:4E:12:9B:1E:9F:10:AF:BC:2D:4A:57:96:26:BB:CC";

		System.out.println("Testing Text Configuration File to Binary Configuation File - DOCSIS");
		System.out.println("--------------------------------------------------------------------");


		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31,cfi.getTlvBuilder(),SHARED_SECRET);

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + ".bin"));

		cf.writeToDisk();	
	}
	
	/**
	 * 
	 * @param iPacketCableVersion
	 */
	public void TextToBinaryPacketCable (int iPacketCableVersion) {
		String sFileName = "IMS-PKT-CABLE-CONFIG.txt";
		
		String sTestMD5 = "";
		
		if (iPacketCableVersion == PacketCableConstants.PKT_CABLE_10_CONFIGURATION_TYPE) {
			 sTestMD5 = "D0:CE:7D:E6:01:E0:92:1A:0F:EA:FE:96:B7:91:0A:D1";
		} else if(iPacketCableVersion == PacketCableConstants.PKT_CABLE_15_CONFIGURATION_TYPE) {
			 sTestMD5 = "D0:CE:7D:E6:01:E0:92:1A:0F:EA:FE:96:B7:91:0A:D1";
		} else if(iPacketCableVersion == PacketCableConstants.PKT_CABLE_20_CONFIGURATION_TYPE) {
			 sTestMD5 = "B8:65:65:3D:0C:35:17:D1:07:BD:C1:C3:C8:98:09:5C";
		}
		
		System.out.println("Testing Text Configuration File to Binary Configuation File - PACKET-CABLE-" + iPacketCableVersion);
		System.out.println("-----------------------------------------------------------------------------");
		
		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(iPacketCableVersion,cfi.getTlvBuilder());

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + "-" + iPacketCableVersion + ".bin"));

		cf.writeToDisk();
	}	
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertManufactureCVC()
	 */
	public void TextToBinaryInsertManufactureCVC () {
		
		String sFileName = "DOCSIS-GOLDEN-NO-MAN-CVC.txt";
		String sTestMD5 = "91:38:BD:C1:D4:38:43:1E:78:F0:78:48:25:F9:D4:39";

		System.out.println("Testing Text Configuration File to Binary Configuation File - DOCSIS - Insert - Manufacture CVC");
		System.out.println("-----------------------------------------------------------------------------------------------");

		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ConfigrationFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31,cfi.getTlvBuilder(),SHARED_SECRET);

		try {

			CommonTlvInsertions.insertManufactureCVC(TestDirectoryStructure.fCertificateDirFileName("COMCAST_CVC.der"), cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + ".bin"));

		cf.writeToDisk();
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertCoSignCVC()
	 */
	public void TextToBinaryInsertCoSignCVC () {
		String sFileName = "DOCSIS-GOLDEN-NO-COSIGN-CVC.txt";
		String sTestMD5 = "14:F3:BA:95:1C:B2:19:48:35:CC:DF:6C:EC:E0:6B:C5";

		System.out.println("Testing Text Configuration File to Binary Configuation File - DOCSIS - Insert - CoSign CVC");
		System.out.println("------------------------------------------------------------------------------------------");


		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ConfigrationFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31,cfi.getTlvBuilder(),SHARED_SECRET);

		try {

			CommonTlvInsertions.insertCoSignCVC(TestDirectoryStructure.fCertificateDirFileName("COMCAST_CVC.der"), cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + ".bin"));

		cf.writeToDisk();
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertDownstreamFrequecy()
	 */
	public void TextToBinaryInsertDownstreamFrequecy () {
		
		String sFileName = "DOCSIS-GOLDEN.txt";
		String sTestMD5 = "69:10:4B:EE:D4:E5:B7:BA:DC:E2:87:EC:90:A2:11:39";

		System.out.println("Testing Text Configuration File to Binary Configuation File - DOCSIS - Insert - Downstream Frequency");
		System.out.println("----------------------------------------------------------------------------------------------------");

		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ConfigrationFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31,cfi.getTlvBuilder(),SHARED_SECRET);

		try {
			CommonTlvInsertions.insertDownstreamFrequency(723000000, cf , false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN-INSERT-DS-FREQ-723000000 " + ".bin"));

		cf.writeToDisk();	
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertMaxCPE()
	 */
	public void TextToBinaryInsertMaxCPE () {
		
		String sFileName = "DOCSIS-GOLDEN.txt";
		String sTestMD5 = "5D:F2:E0:2B:53:F8:54:10:FB:D1:A9:50:BB:3A:DD:7F";

		System.out.println("Testing Text Configuration File to Binary Configuation File - DOCSIS - Insert - Max CPE - 32");
		System.out.println("--------------------------------------------------------------------------------------------");

		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ConfigrationFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31,cfi.getTlvBuilder(),SHARED_SECRET);

		try {
			CommonTlvInsertions.insertMaxCPE(32, cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN-MAX-CPE-32" + ".bin"));

		cf.writeToDisk();	
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertTLV()
	 */
	public void TextToBinaryInsertTLV () {
		String sFileName = "DOCSIS-GOLDEN.txt";
		String sTestMD5 = "66:1D:EF:5A:A9:0A:94:F8:5E:20:0C:2C:C1:AE:37:C3";

		System.out.println("Testing Text Configuration File to Binary Configuation File - DOCSIS - Insert - TLV - 020101");
		System.out.println("--------------------------------------------------------------------------------------------");

		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ConfigrationFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31,cfi.getTlvBuilder(),SHARED_SECRET);

		try {
			CommonTlvInsertions.insertTLV("020101", cf, false);
		} catch (TlvException e) {
			e.printStackTrace();
		}

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + "-INSERT-TLV-020101.bin"));

		cf.writeToDisk();

	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryPacketCableInsertDigitMap()
	 */
	public void TextToBinaryPacketCableInsertDigitMap () {
		
		String sFileName = "IMS-PKT-CABLE-CONFIG-DIGIT-MAP.txt";
		String sDigitMap = "digitMap.txt";
		
		int iPacketCableVersion = PacketCableConstants.PKT_CABLE_20_CONFIGURATION_TYPE;
		
		String sTestMD5 = "BB:77:BC:71:9C:4A:4A:59:D7:3B:C4:B5:16:5A:8B:7E";
		
		System.out.println("Testing Text Configuration File to Binary Configuation File - PACKET-CABLE-20");
		System.out.println("-----------------------------------------------------------------------------");
		
		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(iPacketCableVersion,cfi.getTlvBuilder());
		
		//Add DigitMap To configuration file 
		cf.add(DigitMapOperation.getDigitMapTlvBuilder(TestDirectoryStructure.fDigitMapDirFileName(sDigitMap)));
		
		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + "-" + iPacketCableVersion + ".bin"));

		cf.writeToDisk();
	}

	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSIS()
	 */
	public void BinaryToTextDOCSIS() {
		
		String sFileName = "DOCSIS-GOLDEN.bin";
		String sTestMD5 = "3B:11:7A:CE:67:00:52:CA:E0:11:FD:9F:41:38:A1:F0";

		System.out.println("Testing Binary Configuration File to Text Configuation File - DOCSIS");
		System.out.println("--------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + ".txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + ".txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextPacketCable()
	 */
	public void BinaryToTextPacketCable() {
		
		String sFileName = "IMS-PKT-CABLE-CONFIG.bin";
		String sTestMD5 = "B4:66:0C:22:22:8F:2B:3A:86:DE:E8:9E:10:49:7A:B8";

		System.out.println("Testing Binary Configuration File to Text Configuation File - PacketCable");
		System.out.println("--------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + ".txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + ".txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}	

	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#GetDigitMapFromBinary()
	 */
	public void GetDigitMapFromBinary() {
		
		
		String sFileName = "IMS-PKT-CABLE-CONFIG.bin";
		String sTestMD5 = "A8:32:42:73:70:91:F6:A2:7D:FA:79:0F:B7:73:60:FC";

		System.out.println("Testing Binary Configuration File Get DigitMap - PacketCable");
		System.out.println("------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		DigitMapOperation dmo = new DigitMapOperation(cfe);

		HexString hsDigitMap = new HexString();
		hsDigitMap.add(dmo.getDigitMap());
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(hsDigitMap.toByteArray());

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		 Disk.writeToDisk(hsDigitMap.toByteArray() , TestDirectoryStructure.fOutputDirFileName("DigitMap.txt"));
		
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#BinaryToBinaryDOCSIS()
	 */
	public void BinaryToBinaryDOCSIS() {
		String sFileName = "DOCSIS-GOLDEN.bin";
		String sTestMD5 = "0C:46:6C:78:ED:BE:A4:2B:D8:00:FE:3D:FD:24:29:38";

		System.out.println("Testing Binary Configuration File to Binary Configuation File - DOCSIS");
		System.out.println("----------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
				
		//Build Configuration file
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(),cfe.getTlvBuilder(),SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + "-BinToBin.bin"));

		cf.writeToDisk();	
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#BinaryToBinaryPacketCable()
	 */
	public void BinaryToBinaryPacketCable() {
		String sFileName = "IMS-PKT-CABLE-CONFIG.bin";
		String sTestMD5 = "B8:65:65:3D:0C:35:17:D1:07:BD:C1:C3:C8:98:09:5C";

		System.out.println("Testing Binary Configuration File to Binary Configuation File - PacketCable");
		System.out.println("----------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
				
		//Build Configuration file
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(),cfe.getTlvBuilder(),SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + "-BinToBin.bin"));

		cf.writeToDisk();	
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISMaxCPE()
	 */
	public void BinaryToTextDOCSISMaxCPE() {
		
		String sFileName = "DOCSIS-GOLDEN.bin";
		String sTestMD5 = "2F:17:34:3E:89:6E:C2:F9:1F:C1:A1:65:32:FC:BB:6E";

		System.out.println("Testing Binary Configuration File to Text Configuation File - DOCSIS-MAX-CPE");
		System.out.println("----------------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder(),SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		try {
			CommonTlvInsertions.insertMaxCPE(32, cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		cfe = new ConfigurationFileExport(cf);
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + ".MaxCPE.txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + ".MaxCPE.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
	}

	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISFirmwareFileName()
	 */
	public void BinaryToTextDOCSISFirmwareFileName() {
		
		String sFileName = "DOCSIS-GOLDEN.bin";
		String sTestMD5 = "CB:FE:14:2A:3B:C6:09:CF:7E:55:DC:DD:C7:EB:19:23";

		System.out.println("Testing Binary Configuration File to Text Configuation File - DOCSIS-FIRMWARE");
		System.out.println("-----------------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder(),SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		try {
			CommonTlvInsertions.insertFirmwareFileName(sFileName, cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		cfe = new ConfigurationFileExport(cf);
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + ".FirmwareFilename.txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + ".FirmwareFilename.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISManCVC()
	 */
	public void BinaryToTextDOCSISManCVC() {
		
		String sFileName = "DOCSIS-GOLDEN.bin";
		String sTestMD5 = "26:42:B0:1F:E0:2A:CA:99:71:73:78:5B:86:EF:43:08";

		System.out.println("Testing Binary Configuration File to Text Configuation File - DOCSIS-MAN-CVC");
		System.out.println("----------------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder(),SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		try {
			CommonTlvInsertions.insertManufactureCVC(TestDirectoryStructure.fCertificateDirFileName("COMCAST_CVC.der"), cf, true);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		cfe = new ConfigurationFileExport(cf);
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + ".ManCVC.txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + ".ManCVC.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISCoSignCVC()
	 */
	public void BinaryToTextDOCSISCoSignCVC() {
		
		String sFileName = "DOCSIS-GOLDEN.bin";
		String sTestMD5 = "CF:54:B6:E0:01:19:7F:78:92:FF:D6:5D:A7:84:0E:40";

		System.out.println("Testing Binary Configuration File to Text Configuation File - DOCSIS-CO-SIGN-CVC");
		System.out.println("--------------------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder(),SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		try {
			CommonTlvInsertions.insertCoSignCVC(TestDirectoryStructure.fCertificateDirFileName("COMCAST_CVC.der"), cf, true);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		cfe = new ConfigurationFileExport(cf);
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + ".CoSignCVC.txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + ".CoSignCVC.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
	}
	
	/**
	 * This test has an issue where if you insert the CoSign then Manufacture CVC it will not insert Both, Typicaly you would not do both at the same time in production
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC()
	 */
	public void BinaryToTextDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC() {
		
		boolean localDebug = Boolean.FALSE;
		
		String sFileName = "DOCSIS-GOLDEN.bin";
		String sTestMD5 = "8A:9D:8F:31:4B:AB:D9:BB:25:AC:53:DD:C8:52:B6:9A";

		System.out.println("Testing Binary Configuration File to Text Configuation File - DOCSIS-TLV-FIRMWARE-MAN-CO-SIGN-CVC");
		System.out.println("-------------------------------------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder(),SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		try {
			CommonTlvInsertions.insertCoSignCVC(TestDirectoryStructure.fCertificateDirFileName("COMCAST_CVC.der"), cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
	
		try {
			CommonTlvInsertions.insertMaxCPE(32, cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}

		try {
			CommonTlvInsertions.insertTLV("020101", cf, false);
		} catch (TlvException e) {
			e.printStackTrace();
		}

		try {
			CommonTlvInsertions.insertFirmwareFileName("FirmwareFileName.bin", cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		try {
			CommonTlvInsertions.insertManufactureCVC(TestDirectoryStructure.fCertificateDirFileName("COMCAST_CVC.der"), cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		cf.commit();
		
		List<HexString> lhs = TlvBuilder.topLevelTlvToHexStringList(cf.toByteArray(), new HashMap<Integer,Integer>(1,1));
		
		if (localDebug) {
			for(HexString hs : lhs) 
				System.out.println("TestAPI: " + hs.toString(":"));
		}
		
		cfe = new ConfigurationFileExport(cf);
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + ".TlvFirmwareManCoSignCVC.txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + ".TlvFirmwareManCoSignCVC.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#BinaryToBinaryDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC()
	 */
	public void BinaryToBinaryDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC() {
	
		String sFileName = "DOCSIS-GOLDEN.bin";
		String sTestMD5 = "8A:9D:8F:31:4B:AB:D9:BB:25:AC:53:DD:C8:52:B6:9A";

		System.out.println("Testing Binary Configuration File to Text Configuation File - DOCSIS-TLV-FIRMWARE-MAN-CO-SIGN-CVC");
		System.out.println("-------------------------------------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder(),SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		try {
			CommonTlvInsertions.insertCoSignCVC(TestDirectoryStructure.fCertificateDirFileName("COMCAST_CVC.der"), cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}

		try {
			CommonTlvInsertions.insertManufactureCVC(TestDirectoryStructure.fCertificateDirFileName("COMCAST_CVC.der"), cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
	
		try {
			CommonTlvInsertions.insertMaxCPE(32, cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}

		try {
			CommonTlvInsertions.insertTLV("020101", cf, false);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		//Set the FileName
		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + ".TlvFirmwareManCoSignCVC.bin"));
			
		cf.writeToDisk();
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + ".TlvFirmwareManCoSignCVC.bin")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#PrintDefaultDOCSISConfigurationTemplate()
	 */
	public void PrintDefaultDOCSISConfigurationTemplate() {
		
		String sFileName = "DOCSIS-FULL-CONFIGURATION.txt";
		String sTestMD5 = "3A:C1:13:C5:BD:F2:65:B1:F1:0D:AC:7D:53:34:55:7C";

		System.out.println("Testing Printing Full DOCSIS Configuration File");
		System.out.println("-----------------------------------------------");
		
		ConfigurationFileExport cfr = new ConfigurationFileExport(ConfigurationFileExport.DOCSIS_VER_31);
				
		Disk.writeToDisk(cfr.toPrettyPrint(0), TestDirectoryStructure.fOutputDirFileName(sFileName));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName)));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}

	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#PrintDefaultPacketCableConfigurationTemplate()
	 */
	public void PrintDefaultPacketCableConfigurationTemplate() {

		String sFileName = "PACKET-CABLE-FULL-CONFIGURATION.tx";
		String sTestMD5 = "1E:F5:D6:D3:09:70:77:5D:1F:A6:FB:2C:13:E3:06:77";

		System.out.println("Testing Printing Full Packet Cable Configuration File");
		System.out.println("-----------------------------------------------------");
		
		ConfigurationFileExport cfr = new ConfigurationFileExport(ConfigurationFileExport.PKT_CBL_VER_20);
				
		Disk.writeToDisk(cfr.toPrettyPrint(1), TestDirectoryStructure.fOutputDirFileName(sFileName));
	
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName)));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}

	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#PrintTlvDotNotationDOCSISDefinition()
	 */
	public void PrintTlvDotNotationDOCSISDefinition() {
		
		String sTestMD5 = "81:A6:D2:6A:F0:E1:1A:1E:37:A6:E1:2C:D0:C0:D7:59";

		System.out.println("Testing Printing TLV Dot Notation DOCSIS Definition");
		System.out.println("---------------------------------------------------");

		
		ConfigurationFileExport cfe = new ConfigurationFileExport(ConfigurationFileExport.DOCSIS_VER_31);
		
		String sTlvDotNotation = "60.10.3.11.2";
		
		String sFileName = "TLV-" + sTlvDotNotation + "-DEF.txt";
		
		Disk.writeToDisk(cfe.getTlvDefintion(sTlvDotNotation), TestDirectoryStructure.fOutputDirFileName(sFileName));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName)));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#PrintTlvDotNotationPacketCableDefinition()
	 */
	public void PrintTlvDotNotationPacketCableDefinition() {

		String sTestMD5 = "39:54:36:37:92:1A:81:AF:8E:61:A5:FB:B5:08:54:61";

		System.out.println("Testing Printing TLV Dot Notation Packet Cable Definition");
		System.out.println("---------------------------------------------------------");
		
		ConfigurationFileExport cfe = new ConfigurationFileExport(ConfigurationFileExport.PKT_CBL_VER_20);
		
		String sTlvDotNotation = "38.1";
		
		String sFileName = "TLV-" + sTlvDotNotation + ".txt";
		
		Disk.writeToDisk(cfe.getTlvDefintion(sTlvDotNotation), TestDirectoryStructure.fOutputDirFileName(sFileName));

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName)));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}

	/**
	 * 
	 */
	public void PrintDOCSISJSONDump() {

		String sFileName = "DOCSIS-GOLDEN.txt";
		String sTestMD5 = "68:FC:FB:5D:AE:BA:50:B9:2C:CD:D4:E9:C0:01:8D:68";

		System.out.println("Testing DOCSIS JSON Dump");
		System.out.println("------------------------");

		
		ConfigrationFileImport cfi = null;
		
		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TlvDisassemble td = new TlvDisassemble(cfi.getTlvBuilder(),TlvDisassemble.TLV_TYPE_DOCSIS);

		Disk.writeToDisk(td.getTlvDictionary().toString(), TestDirectoryStructure.fOutputDirFileName(sFileName + "-JSON.txt"));
			
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + "-JSON.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}		
			
	}

	/**
	 * 
	 */
	public void PrintPacketCableJSONDump() {

		String sFileName = "IMS-PKT-CABLE-CONFIG.txt";
		String sTestMD5 = "16:A0:18:AF:EA:DE:24:C0:57:31:60:17:2D:6B:86:89";

		System.out.println("Testing Packet Cable JSON Dump");
		System.out.println("------------------------------");
		
		ConfigrationFileImport cfi = null;
		
		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TlvDisassemble td = new TlvDisassemble(cfi.getTlvBuilder(),DictionarySQLQueries.PACKET_CABLE_DICTIONARY_TABLE_NAME);

		Disk.writeToDisk(td.getTlvDictionary().toString(), TestDirectoryStructure.fOutputDirFileName(sFileName + "-JSON.txt"));
			
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + "-JSON.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}

	/**
	 * 
	 */
	public void PrintTlvToJSON() {

		String sTLV = "180401020001";
		String sTestMD5 = "68:7D:5A:12:83:36:4E:AE:EA:5C:01:EF:FB:67:E6:87";

		System.out.println("Testing Print TLV TO JSON");
		System.out.println("-------------------------");
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(new HexString(HexString.toByteArray(sTLV)));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		TlvDisassemble td = new TlvDisassemble(tb,TlvDisassemble.TLV_TYPE_DOCSIS);
		
		Disk.writeToDisk(td.getTlvDictionary().toString(), TestDirectoryStructure.fOutputDirFileName(sTLV + "-JSON.txt"));
			
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sTLV + "-JSON.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}
	
	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#PrintJSONToTlv()
	 */
	public void PrintJSONToTlv() {

		String sTLV = "JSON-TO-TLV.txt";
		String sTestMD5 = "4A:9A:67:DF:8A:D0:96:BB:A6:C4:AD:69:6D:75:EA:AA";

		System.out.println("Testing Print JSON TO TLV");
		System.out.println("-------------------------");
		
		byte[] baTLV = HexString.fileToByteArray(TestDirectoryStructure.fInputDirFileName(sTLV));
		
		HexString hsTLV = new HexString(baTLV);
			
		TlvAssembler ta = null;
		
		try {
			ta = new TlvAssembler(new JSONArray(hsTLV.toASCII()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Disk.writeToDisk(ta.toString(), TestDirectoryStructure.fOutputDirFileName(sTLV));
			
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sTLV)));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}
	
	/**
	 * 
	 */
	public void BulkBuildToText() {
		
		boolean boolOVERWRITE_TLV = true;

		CommonTlvInsertions cti = new CommonTlvInsertions();

		cti = new CommonTlvInsertions();

		/* Firmware Filename */
		String sFirmwareFileName = "firmware.bin";		
		cti.insertFirmwareFileName(sFirmwareFileName,boolOVERWRITE_TLV);

		/* Manufacture Certificate */		
		File fCertificate = null;

		try {
			fCertificate  = new File(new java.io.File( "." ).getCanonicalPath() + File.separatorChar + "certificate" + File.separatorChar + "COMCAST_CVC.der");
		} catch (IOException e) {
			e.printStackTrace();
		}

		cti.insertCoSignCVC(fCertificate,boolOVERWRITE_TLV);

		/* MaxCPE */
		int iMaxCPE = 1;
		cti.insertMaxCPE(iMaxCPE,boolOVERWRITE_TLV);

		/* DownStream Frequency */
		int iDownstreamFreq = 723000000;
		cti.insertDownstreamFrequency(iDownstreamFreq,boolOVERWRITE_TLV);

		/*Direct TLV Insertion - UpstreamChannelID TLV: [2]*/
		String sTLV = "020101";
		try {
			cti.insertTLV(sTLV, boolOVERWRITE_TLV);
		} catch (TlvException e) {
			e.printStackTrace();
		}

		BulkBuild bb = new BulkBuild(	TestDirectoryStructure.inputBulkDir(), 
										TestDirectoryStructure.outputBulkDir(),
										BulkBuild.TEXT_OUTPUT,
										SHARED_SECRET,
										cti);

		bb.start();	
		
	}

	/**
	 * 
	 */
	public void BulkBuildToBinary() {
		boolean boolOVERWRITE_TLV = true;

		CommonTlvInsertions cti = new CommonTlvInsertions();

		/* Firmware Filename */
		String sFirmwareFileName = "firmware.bin";		
		cti.insertFirmwareFileName(sFirmwareFileName,boolOVERWRITE_TLV);

		/* Manufacture Certificate */		
		File fCertificate = null;

		try {
			fCertificate  = new File(new java.io.File( "." ).getCanonicalPath() + File.separatorChar + "certificate" + File.separatorChar + "COMCAST_CVC.der");
		} catch (IOException e) {
			e.printStackTrace();
		}

		cti.insertCoSignCVC(fCertificate,boolOVERWRITE_TLV);

		/* MaxCPE */
		int iMaxCPE = 1;
		cti.insertMaxCPE(iMaxCPE,boolOVERWRITE_TLV);

		/* DownStream Frequency */
		int iDownstreamFreq = 723000000;
		cti.insertDownstreamFrequency(iDownstreamFreq,boolOVERWRITE_TLV);

		/*Direct TLV Insertion - UpstreamChannelID TLV: [2]*/
		String sTLV = "020101";
		try {
			cti.insertTLV(sTLV, boolOVERWRITE_TLV);
		} catch (TlvException e) {
			e.printStackTrace();
		}

		BulkBuild bb = new BulkBuild(	TestDirectoryStructure.inputBulkDir(), 
										TestDirectoryStructure.outputBulkDir(),
										BulkBuild.BINARY_OUTPUT,
										SHARED_SECRET,
										cti);

		bb.start();			
	}

	
	/**
	 * Method TextToBinaryPacketCable.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryPacketCable()
	 */
	public void TextToBinaryPacketCable() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintTextToBinaryPacketCableJSONDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryPacketCableJSONDump()
	 */
	public void PrintTextToBinaryPacketCableJSONDump() {
		// TODO Auto-generated method stub
		
	}


	/**
	 * Method PrintTextToBinaryDOCSISJSONDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryDOCSISJSONDump()
	 */
	public void PrintTextToBinaryDOCSISJSONDump() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintBinaryToTextPacketCableJSONDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextPacketCableJSONDump()
	 */
	public void PrintBinaryToTextPacketCableJSONDump() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintBinaryToTextDOCSISJSONDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextDOCSISJSONDump()
	 */
	public void PrintBinaryToTextDOCSISJSONDump() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintDOCSISTlvToJSON.
	 * @see com.comcast.oscar.test.TestMethod#PrintDOCSISTlvToJSON()
	 */
	public void PrintDOCSISTlvToJSON() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintPacketCableTlvToJSON.
	 * @see com.comcast.oscar.test.TestMethod#PrintPacketCableTlvToJSON()
	 */
	public void PrintPacketCableTlvToJSON() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method BinaryToTextPacketCableInsertDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextPacketCableInsertDigitMap()
	 */
	public void BinaryToTextPacketCableInsertDigitMap() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintTextToBinaryPacketCableHexDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryPacketCableHexDump()
	 */
	public void PrintTextToBinaryPacketCableHexDump() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintTextToBinaryDOCSISHexDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryDOCSISHexDump()
	 */
	public void PrintTextToBinaryDOCSISHexDump() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintBinaryToTextDOCSISHexDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextDOCSISHexDump()
	 */
	public void PrintBinaryToTextDOCSISHexDump() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintBinaryToTextPacketCableHexDump.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextPacketCableHexDump()
	 */
	public void PrintBinaryToTextPacketCableHexDump() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintBinaryToTextPacketCableDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#PrintBinaryToTextPacketCableDigitMap()
	 */
	public void PrintBinaryToTextPacketCableDigitMap() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Method PrintTextToBinaryPacketCableDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#PrintTextToBinaryPacketCableDigitMap()
	 */
	public void PrintTextToBinaryPacketCableDigitMap() {		
	}

	
	/**
	 * Method BinaryToBinaryInsertMultiDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToBinaryInsertMultiDigitMap()
	 */
	public void BinaryToBinaryInsertMultiDigitMap() {
		
		String sFileName = "IMS-PKT-CABLE-CONFIG.bin";

		String sDigitMap = "digitMap.txt";
		
		String sDigitMapOID1 = "1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.1";
		String sDigitMapOID2 = "1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.2";
		
		String sTestMD5 = "5A:D4:37:BF:C0:0D:53:81:A5:04:D3:62:E8:5E:81:37";

		System.out.println("Testing Binary Configuration File to Binary Configuation File - PacketCable");
		System.out.println("----------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
				
		//Build Configuration file
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(),cfe.getTlvBuilder(),SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}

		//Add DigitMap To Index 1
		cf.add(DigitMapOperation.getDigitMapTlvBuilder(HexString.fileToByteArray(TestDirectoryStructure.fDigitMapDirFileName(sDigitMap)), sDigitMapOID1));
		
		//Add DigitMap To Index 2
		cf.add(DigitMapOperation.getDigitMapTlvBuilder(HexString.fileToByteArray(TestDirectoryStructure.fDigitMapDirFileName(sDigitMap)), sDigitMapOID2));
			
		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + ".bin"));

		cf.writeToDisk();
		
	}
	
	
	/**
	 * Method BinaryToTextInsertMultiDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextInsertMultiDigitMap()
	 */
	public void BinaryToTextInsertMultiDigitMap() {
		
		String sFileName = "IMS-PKT-CABLE-CONFIG.bin";
		
		String sDigitMap = "digitMap.txt";
		
		String sDigitMapOID1 = "1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.1";
		String sDigitMapOID2 = "1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.2";

		String sTestMD5 = "89:72:52:39:F9:7A:B1:91:6F:65:DE:51:E2:95:CA:BA";

		System.out.println("Testing Binary Configuration File to Text Configuation File - PacketCable");
		System.out.println("--------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		TlvBuilder tb = null;
		
		try {
			tb = cfe.getTlvBuilder();
		} catch (TlvException e1) {
			e1.printStackTrace();
		}
				
		//Add DigitMap To Index 1
		tb.add(DigitMapOperation.getDigitMapTlvBuilder(HexString.fileToByteArray(TestDirectoryStructure.fDigitMapDirFileName(sDigitMap)), sDigitMapOID1));
		
		//Add DigitMap To Index 2
		tb.add(DigitMapOperation.getDigitMapTlvBuilder(HexString.fileToByteArray(TestDirectoryStructure.fDigitMapDirFileName(sDigitMap)), sDigitMapOID2));

		//ReSet FileMarkers because the DigitMaps are Appended to the end of the config file
		tb = PacketCableCompiler.setFileMarkers(tb.toByteArray());
		
		cfe = new ConfigurationFileExport(tb);
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + ".txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + ".txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass|Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}
	

	/**
	 * Method TextToBinaryInsertMultiDigitMap.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertMultiDigitMap()
	 */
	public void TextToBinaryInsertMultiDigitMap() {
		
		String sFileName = "IMS-PKT-CABLE-CONFIG-DIGIT-MAP.txt";
		String sDigitMap = "digitMap.txt";
		
		String sDigitMapOID1 = "1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.1";
		String sDigitMapOID2 = "1.3.6.1.4.1.4491.2.2.8.2.1.1.3.1.1.2.2";
		
		
		int iPacketCableVersion = PacketCableConstants.PKT_CABLE_20_CONFIGURATION_TYPE;
		
		String sTestMD5 = "FA:4E:3F:01:D3:CA:F1:09:5D:E2:6A:0E:30:A7:58:44";
		
		System.out.println("Testing Text Configuration File to Binary Configuation File - PACKET-CABLE-20");
		System.out.println("-----------------------------------------------------------------------------");
		
		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(iPacketCableVersion,cfi.getTlvBuilder());
		
		//Add DigitMap To Index 1
		cf.add(DigitMapOperation.getDigitMapTlvBuilder(HexString.fileToByteArray(TestDirectoryStructure.fDigitMapDirFileName(sDigitMap)), sDigitMapOID1));
		
		//Add DigitMap To Index 2
		cf.add(DigitMapOperation.getDigitMapTlvBuilder(HexString.fileToByteArray(TestDirectoryStructure.fDigitMapDirFileName(sDigitMap)), sDigitMapOID2));
		
		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + "-" + iPacketCableVersion + ".bin"));

		cf.writeToDisk();
		
	}

	/**
	 * 
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryFullDOCSISConfiguration()
	 */
	public void TextToBinaryFullDOCSISConfiguration() {
		String sFileName = "FULL-DOCSIS-CONFIG.txt";
		String sTestMD5 = "2D:4E:12:9B:1E:9F:10:AF:BC:2D:4A:57:96:26:BB:CC";

		System.out.println("Testing Full DOCSIS Text Configuration File to Binary Configuation File - DOCSIS");
		System.out.println("--------------------------------------------------------------------------------");


		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigrationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31,cfi.getTlvBuilder(),SHARED_SECRET);

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + ".bin"));

		cf.writeToDisk();	
		
	}

	
	/**
	 * Method TextToBinaryInsertTFTPServerAddress.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertTFTPServerAddress()
	 */
	public void TextToBinaryInsertTFTPServerAddress() {
		
		String sIPv4 = "10.10.10.10";
		String sIPv6 = "2001:558:ff2e:bacc:221:9bff:fe99:c7a2";
		
		String sFileName = "DOCSIS-GOLDEN.txt";
		String sTestMD5 = "91:38:BD:C1:D4:38:43:1E:78:F0:78:48:25:F9:D4:39";

		System.out.println("Testing Text Configuration File to Binary Configuation File - DOCSIS - Insert - TFTP Server IPv4 & IPv6 Address");
		System.out.println("---------------------------------------------------------------------------------------------------------------");

		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (ConfigrationFileException e1) {
			e1.printStackTrace();
		}

		//Build Configuration file
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31,cfi.getTlvBuilder(),SHARED_SECRET);

		try {
			CommonTlvInsertions.insertTftpServerAddress(sIPv4, cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}

		try {
			CommonTlvInsertions.insertTftpServerAddress(sIPv6, cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + "-TFTP-SERVER.bin"));

		cf.writeToDisk();
		
	}
	
	
	/**
	 * Method BinaryToTextDOCSISTFTPServerAddress.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextDOCSISTFTPServerAddress()
	 */
	public void BinaryToTextDOCSISTFTPServerAddress() {
		
		String sIPv4 = "10.10.10.10";
		String sIPv6 = "2001:558:ff2e:bacc:221:9bff:fe99:c7a2";
		
		String sFileName = "DOCSIS-GOLDEN.bin";
		String sTestMD5 = "2F:17:34:3E:89:6E:C2:F9:1F:C1:A1:65:32:FC:BB:6E";

		System.out.println("Testing Binary Configuration File to Text Configuation File - DOCSIS - Insert - TFTP Server IPv4 & IPv6 Address");
		System.out.println("---------------------------------------------------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		ConfigurationFile cf = null;
		try {
			cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder(), SHARED_SECRET);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		try {
			CommonTlvInsertions.insertTftpServerAddress(sIPv4, cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		try {
			CommonTlvInsertions.insertTftpServerAddress(sIPv6, cf, false);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		cfe = new ConfigurationFileExport(cf);
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + "-TFTP-SERVER.txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + "-TFTP-SERVER.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}


	/**
	 * Method TextToTextMergeTemplateDOCSIS.
	 * @see com.comcast.oscar.test.TestMethod#TextToTextMergeTemplateDOCSIS()
	 */
	public void TextToTextMergeTemplateDOCSIS() {

		//Template File
		ConfigrationFileImport cfiTemplate = null;
		ConfigurationFileExport cfeTemplate = null;
		TlvBuilder tbTemplate = null;
		
		//Input File
		ConfigrationFileImport cfiInput = null;
		ConfigurationFileExport cfeInput = null;
		TlvBuilder tbInput = null;
		
		int 	iCFTInput 		= -1 , 
				iCFTTemplate 	= -2;
		
		//Input File - Check for Text or Binary
		File fInput = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.txt");
		
		//Check for ACSII ONLY File
		if (HexString.verifyAsciiPlainText(HexString.fileToByteArray(fInput))){
			
			try {
				cfiInput = new ConfigrationFileImport(fInput);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ConfigrationFileException e) {
				e.printStackTrace();
			}
			
			iCFTInput = cfiInput.getConfigurationFileType();
			
			tbInput = cfiInput.getTlvBuilder();
		
		//Must be a Binary File
		} else {
			
			cfeInput = new ConfigurationFileExport(fInput);
			
			try {
				tbInput = cfeInput.getTlvBuilder();
			} catch (TlvException e) {
				e.printStackTrace();
			}
			
			iCFTInput = cfeInput.getConfigurationFileType();
		}
			
		//Template File - Check for Text or Binary
		File fTemplate = TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN-TEMPLATE-MERGE.txt");
		
		if (HexString.verifyAsciiPlainText(HexString.fileToByteArray(fTemplate))){
			
			try {
				cfiTemplate = new ConfigrationFileImport(fTemplate);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ConfigrationFileException e) {
				e.printStackTrace();
			}
			
			tbTemplate = cfiTemplate.getTlvBuilder();
			
			iCFTTemplate = cfeInput.getConfigurationFileType();
			
		} else {
			
			cfeTemplate = new ConfigurationFileExport(fTemplate);
			
			try {
				tbTemplate = cfeTemplate.getTlvBuilder();
			} catch (TlvException e) {
				e.printStackTrace();
			}
			
			iCFTTemplate = cfeInput.getConfigurationFileType();
		}
		
		//If the Configuration File Type Do not Match Exit
		if (iCFTInput != iCFTTemplate) {		
			System.out.println("InputFile: " + fInput.getName() + " Template File: " + fTemplate.getName() + " are not of the same ConfigurationFile Type");
			return;
		}
		
		//Check to see what kind of Configuration file
		ConfigurationFile cf = null;
		if (iCFTInput > ConfigurationFile.DOCSIS_VER_31) {
			//Create Configuration File - Packet Cable
			cf = new ConfigurationFile(iCFTInput,tbInput);			
		} else {
			//Create Configuration File - DOCSIS
			cf = new ConfigurationFile(iCFTInput,tbInput,SHARED_SECRET);			
		}
	
		//Add Template to Configuration File
		cf.add(tbTemplate);
		
		//Finalize file with Security Hashes
		cf.commit();
		
		File fOutput = TestDirectoryStructure.fOutputDirFileName("DOCSIS-GOLDEN-TEMPLATE-MERGE-OUT.txt");
		
		ConfigurationFileExport cfeMerge = null;
		
		cfeMerge = new ConfigurationFileExport(cf);
		
		//Write Merge file to TEXT
		cfeMerge.writeToDisk(fOutput);
		
	}

	
	/**
	 * Method BinaryToTextInsertOID.
	 * @see com.comcast.oscar.test.TestMethod#BinaryToTextInsertOID()
	 */
	public void BinaryToTextInsertOID() {
		
		String sFileName = "DOCSIS-GOLDEN.bin";
		
		String sValue = "StringValueSnmpInsert";
		
		String sOID = "sysContact.0";

		String sTestMD5 = "89:72:52:39:F9:7A:B1:91:6F:65:DE:51:E2:95:CA:BA";

		System.out.println("Testing Binary Configuration File to Text Configuation File - Insert SNMP OID");
		System.out.println("-----------------------------------------------------------------------------");

		ConfigurationFileExport cfe = new ConfigurationFileExport(TestDirectoryStructure.fInputDirFileName(sFileName));
		
		ConfigurationFile cf = null;
		
		if (cfe.getConfigurationFileType() > ConfigurationFileExport.DOCSIS_VER_31) {

			try {
				cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder());
			} catch (TlvException e) {
				e.printStackTrace();
			}
			
		} else {
			try {
				cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder(),SHARED_SECRET);
			} catch (TlvException e) {
				e.printStackTrace();
			}
		}
		
		CommonTlvInsertions.insertSnmpOID(sOID , BERService.berStringDataTypeToByte(BERService.OCTETSTRING) , sValue , cf, false);
							
		cfe = new ConfigurationFileExport(cf);
		
		cfe.writeToDisk(TestDirectoryStructure.fOutputDirFileName(sFileName + "-Snmp-OID-Insertion.txt"));
		
		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(HexString.fileToByteArray(TestDirectoryStructure.fOutputDirFileName(sFileName + "-Snmp-OID-Insertion.txt")));

		System.out.println("Text Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass|Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}
		
	}

	/**
	 * Method TextToBinaryInsertOID.
	 * @see com.comcast.oscar.test.TestMethod#TextToBinaryInsertOID()
	 */
	public void TextToBinaryInsertOID() {
		
		String sFileName = "DOCSIS-GOLDEN.txt";
		String sTestMD5 = "91:38:BD:C1:D4:38:43:1E:78:F0:78:48:25:F9:D4:39";

		String sValue = "StringValueSnmpInsert";
		
		String sOID = "sysContact.0";
		
		System.out.println("Testing Text Configuration File to Binary Configuation File - DOCSIS - Insert - SNMP-OID");
		System.out.println("-----------------------------------------------------------------------------------------------");

		ConfigrationFileImport cfi = null;

		try {
			cfi = new ConfigrationFileImport(TestDirectoryStructure.fInputDirFileName(sFileName));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (ConfigrationFileException e1) {
			e1.printStackTrace();
		}

		ConfigurationFile cf = null;
		
		if (cfi.getConfigurationFileType() > ConfigrationFileImport.DOCSIS_VER_31) {
			cf = new ConfigurationFile(cfi.getConfigurationFileType(), cfi.getTlvBuilder());		
		} else {
			cf = new ConfigurationFile(cfi.getConfigurationFileType(), cfi.getTlvBuilder(),SHARED_SECRET);
		}
		
		CommonTlvInsertions.insertSnmpOID(sOID , BERService.berStringDataTypeToByte(BERService.OCTETSTRING) , sValue , cf, false);

		//Finalize File with its proper Hashing
		cf.commit();

		//Get ByteArray for MD5 HashCheck
		byte[] bMD5_HASH_INSERT = CheckSum.getMD5(cf.toByteArray());

		System.out.println("Binary Output MD5 Hash: " + new HexString(bMD5_HASH_INSERT).toString(":"));

		//Pass/Fail Test
		if (Arrays.equals(bMD5_HASH_INSERT, HexString.toByteArray(sTestMD5))) {
			System.out.println("\nPASS\n");	
		} else {
			System.out.println("\nFAIL\n");	
		}

		cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName(sFileName + "-SnmpOID-Insertion.bin"));

		cf.writeToDisk();
		
	}
	
}
