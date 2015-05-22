package com.comcast.oscar.configurationfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import com.comcast.oscar.ber.BEROIDConversion;
import com.comcast.oscar.ber.OIDCollectionDuplicationValidation;
import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.compiler.DPoECompiler;
import com.comcast.oscar.compiler.DocsisCompiler;
import com.comcast.oscar.compiler.PacketCableCompiler;
import com.comcast.oscar.compiler.PacketCableConstants;
import com.comcast.oscar.dictionary.DictionarySQLQueries;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.tlv.TlvVariableBinding;
import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;

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


public class ConfigurationFile {
	
	private boolean debug = Boolean.FALSE;
	
	private int iConfigurationFileType;	
	private TlvBuilder tbConfigurationFile;	
	private DocsisCompiler dcConfigurationFile = null;
	private PacketCableCompiler pcConfigurationFile = null;
	private DPoECompiler dpoeConfigurationFile = null;
	private String sCmtsSharedSecretKey = "SHAREDSECRET";	
	private String sConfigurationFileName = "";	
	private File fConfigurationFileName = null;	
	private Map<Integer,Integer> miiTypeToByteLengh = null;
	
	public static final Integer DOCSIS_VER_10 	= ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE;
	public static final Integer DOCSIS_VER_11 	= ConfigurationFileTypeConstants.DOCSIS_11_CONFIGURATION_TYPE;
	public static final Integer DOCSIS_VER_20 	= ConfigurationFileTypeConstants.DOCSIS_20_CONFIGURATION_TYPE;
	public static final Integer DOCSIS_VER_30 	= ConfigurationFileTypeConstants.DOCSIS_30_CONFIGURATION_TYPE;
	public static final Integer DOCSIS_VER_31 	= ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE;
	
	public static final Integer PKT_CBL_VER_10 	= ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE;
	public static final Integer PKT_CBL_VER_15 	= ConfigurationFileTypeConstants.PKT_CABLE_15_CONFIGURATION_TYPE;
	public static final Integer PKT_CBL_VER_20 	= ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE;

	public static final Integer DPOE_VER_10 	= ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE;
	public static final Integer DPOE_VER_20 	= ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE;
	
	/**
	 * 
	 * @param iConfigurationFileType
	 * @param tbConfigurationFile
	 */
 	public ConfigurationFile(int iConfigurationFileType, TlvBuilder tbConfigurationFile) {
		
 		boolean localDebug = Boolean.FALSE;
 				
		this.iConfigurationFileType = iConfigurationFileType;
		
		this.tbConfigurationFile = tbConfigurationFile;
	
		//DumpTLV to STDOUT
		if (localDebug) {		
			System.out.println("ConfigurationFile(i,tb)");		
			System.out.println(TlvBuilder.tlvDump(tbConfigurationFile.toByteArray()));			
		}
		
		if (localDebug|debug) {
			System.out.println("ConfigurationFile(i,tb) " +
					" ConfigType: " + iConfigurationFileType +
					" CMTS-Shared-Secret: " + sCmtsSharedSecretKey +
					" ConfigFile-Length: " + tbConfigurationFile.length() +
					" ConfigFile-HEX: " + tbConfigurationFile.toStringSeperation(""));
		}
		
		init();
	}

 	/**
 	 * 
 	 * @param sConfigurationFileName
 	 * @param iConfigurationFileType
 	 * @param tbConfigurationFile
 	 */
 	public ConfigurationFile(String sConfigurationFileName, int iConfigurationFileType, TlvBuilder tbConfigurationFile) {
		
 		boolean localDebug = Boolean.FALSE;
 		
		this.iConfigurationFileType = iConfigurationFileType;
		
		this.tbConfigurationFile = tbConfigurationFile;
		
		setConfigurationFileName(sConfigurationFileName);

		if (localDebug|debug) {
			System.out.println("ConfigurationFile(s,i,tb) " +
					" ConfigType: " + iConfigurationFileType +
					" CMTS-Shared-Secret: " + sCmtsSharedSecretKey +
					" ConfigFile-Length: " + tbConfigurationFile.length() +
					" ConfigFile-HEX: " + tbConfigurationFile.toStringSeperation(""));
		}
		
		init();
	}
 	 
 	/**
 	 * 
 	 * @param sConfigurationFileName
 	 * @param iConfigurationFileType
 	 * @param tbConfigurationFile
 	 * @param sCmtsSharedSecretKey
 	 */
 	public ConfigurationFile(String sConfigurationFileName, int iConfigurationFileType, TlvBuilder tbConfigurationFile, String sCmtsSharedSecretKey) {
		
 		boolean localDebug = Boolean.FALSE;
 		
		this.iConfigurationFileType = iConfigurationFileType;
		
		this.tbConfigurationFile = tbConfigurationFile;
		
		this.sCmtsSharedSecretKey = sCmtsSharedSecretKey;
		
		setConfigurationFileName(sConfigurationFileName);

		if (localDebug|debug) {
			System.out.println("ConfigurationFile(s,i,tb) " +
					" ConfigType: " + iConfigurationFileType +
					" CMTS-Shared-Secret: " + sCmtsSharedSecretKey +
					" ConfigFile-Length: " + tbConfigurationFile.length() +
					" ConfigFile-HEX: " + tbConfigurationFile.toStringSeperation(""));
		}
		
		init();
	}
 	
	/**
	 * 
	 * @param iConfigurationFileType
	 * @param tbConfigurationFile
	 * @param sCmtsSharedSecretKey
	 */
	public ConfigurationFile(int iConfigurationFileType, TlvBuilder tbConfigurationFile, String sCmtsSharedSecretKey) {
		
		boolean localDebug = Boolean.FALSE;
		
		this.iConfigurationFileType = iConfigurationFileType;
		
		this.tbConfigurationFile = tbConfigurationFile;
		
		this.sCmtsSharedSecretKey = sCmtsSharedSecretKey;
		
		if (localDebug|debug) {
			System.out.println("ConfigurationFile(i,tb,s) " +
					" -> ConfigType: " + iConfigurationFileType +
					" -> CMTS-Shared-Secret: " + sCmtsSharedSecretKey +
					" -> ConfigFile-Length: " + tbConfigurationFile.length() +
					" -> ConfigFile-HEX: " + tbConfigurationFile.toStringSeperation(""));
		}
		
		init();
	}	
	
	/**
	 * 
	 * @param iConfigurationFileType
	 * @param tbConfigurationFile
	 * @param sCmtsSharedSecretKey
	 * @param sConfigurationFileName
	 */
	public ConfigurationFile(int iConfigurationFileType, TlvBuilder tbConfigurationFile, String sCmtsSharedSecretKey , String sConfigurationFileName) {
		
		this.iConfigurationFileType = iConfigurationFileType;
		
		this.tbConfigurationFile = tbConfigurationFile;
		
		this.sCmtsSharedSecretKey = sCmtsSharedSecretKey;
		
		setConfigurationFileName(sConfigurationFileName);
		
		init();
	}
	
	/**
	 * 
	
	 * @param tb TlvBuilder
	 */
	public void add(TlvBuilder tb) {
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug)
			System.out.println("ConfigurationFile.add(tb): - Hex: " + tb.toStringSeperation(":"));
				
		addTlvBuilder(tb);
	}

	/**
	 * 
	 * @param tb
	 * @param liTlvToStrip - List of TLV that are to be striped from Class Buffer
	 */
	public void add(TlvBuilder tb , List<Integer> liTlvToStrip) {
	 	
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("ConfigurationFile.add(tb,li): - Hex: " + tb.toStringSeperation(":"));
			System.out.println("ConfigurationFile.add(tb,li): - TlvToStripFromBufferCLassList: " + liTlvToStrip);
		}	
		
		addTlvBuilder(tb,liTlvToStrip);
	}
	
	/**
	 * 
	
	 * @return byte[]
	 */
 	public byte[] toByteArray () {
		
		if (dcConfigurationFile != null) {
		
			return dcConfigurationFile.toByteArray();
		
		} else if (pcConfigurationFile != null) {
			
			return pcConfigurationFile.toByteArray();
		
		} else if (dpoeConfigurationFile != null) {
			return dpoeConfigurationFile.toByteArray();
		}
		
		return null;		
	}
	
	/**
	 * 
	
	 * @return int
	 */
	public int getConfigurationFileType () {
		return iConfigurationFileType;
	}
	
	/**
	 * 
	
	 * @return Configuration file byte size */
	public int size() {
		
		int iSize = 0;
		
		if (dcConfigurationFile != null) {
			iSize =  dcConfigurationFile.toByteArray().length;
		} else if (pcConfigurationFile != null) {
			iSize =  pcConfigurationFile.toByteArray().length;				
		}
		
		return iSize;
	}

	/**
	 * Clear Class Buffer Array
	 */
	public void clear() {
		
		this.tbConfigurationFile.clear();
		
		if (dcConfigurationFile != null) {
			dcConfigurationFile.clear();
		} else if (pcConfigurationFile != null) {
			pcConfigurationFile.clear();				
		}
	}
	
	/**
	 * This method will calculate the appropriate Security HASH
	 * CM : CM and CMTS HASH
	 * PC: SHA1 HASH
	 */
	public void commit() {
 		
 		boolean localDebug = Boolean.FALSE;
 				
		if (dcConfigurationFile != null) {
			
			if (localDebug|debug) 
				System.out.println("ConfigurationFile.commit() - DOCSIS - ByteLength: " + dcConfigurationFile.length());
			
			removeZeroLengthTopLevelTLV ();
			
			dcConfigurationFile.commit();
				
		} else if (pcConfigurationFile != null) {
			
			if (localDebug|debug) 
				System.out.println("ConfigurationFile.commit() - PACKET-CABLE - ByteLength: " + pcConfigurationFile.length());
			
			pcConfigurationFile.commit();
			
		} else if (dpoeConfigurationFile != null) {
			
			if (localDebug|debug) 
				System.out.println("ConfigurationFile.commit() - DPoE-CABLE - ByteLength: " + dpoeConfigurationFile.length());
			
			dpoeConfigurationFile.commit();
		}
		
		
		 		
	}

 	/**
 	
 	 * @see NotWorking */
 	public void removeZeroLengthTopLevelTLV () {
 		 		
 	}
 	
 	/**
 	 * 
 	 * 
 	
 	 * @return a true if no OID duplications are found */
 	public boolean checkOIDDuplication() {
 		
 		//boolean localDebug = Boolean.FALSE;
 		
 		boolean boolStatus = false;
 		
 		//Right now I am going to hard code this part, will need to refer to Dictionary
 		Map<Integer,Integer> miiOidLength = new HashMap<Integer,Integer>();
 		miiOidLength.put(11, 1);  	// TLV 11 1 byteLength encoding
 		miiOidLength.put(64, 2);	// TLV 64 2 byteLength encoding
 		
 		BEROIDConversion boc = null;
 		
 		List<byte[]> lbOIDTlv = new ArrayList<byte[]>();
 		
 		OIDCollectionDuplicationValidation ocavCheckOidCollision = new OIDCollectionDuplicationValidation();
 			
 		if (dcConfigurationFile == null) {
 			lbOIDTlv = pcConfigurationFile.sortByTopLevelTlv(miiOidLength);
 		} else if (pcConfigurationFile == null) {
 			lbOIDTlv = dcConfigurationFile.sortByTopLevelTlv(miiOidLength);
 		}
 		
 		//Cycle thru the list of BER and find a collision
 		for (byte[] bTLV : lbOIDTlv) {
 			
 			//TLV 11 uses 1 byte Length Encoding
 			if (BinaryConversion.byteToUnsignedInteger(bTLV[0]) == 11) {
 							
				try {
					boc = new BEROIDConversion(TlvBuilder.getTlvValue (bTLV , 0, 1));
				} catch (TlvException e) {
					e.printStackTrace();
				}
	 		
			//TLV 64 uses 2 byte Length Encoding
 			} else if (BinaryConversion.byteToUnsignedInteger(bTLV[0]) == 64) {
 				
				try {
					boc = new BEROIDConversion(TlvBuilder.getTlvValue (bTLV , 0, 2));
				} catch (TlvException e) {
					e.printStackTrace();
				}
 		 				
 			}
 			
 			String sObjectID = boc.getOidDotNotaion();

 			//Found a collision, break and return false
 			if (!ocavCheckOidCollision.add(boc,sObjectID)) {
 				boolStatus = true;
 				break;
 			}

 		}
 		 				
 		return boolStatus;
 	}
 	
	/**
	
	 * @return the sConfigurationFileName */
	public String getConfigurationFileName() {
		return sConfigurationFileName;
	}
	
	/**
	 * @param sConfigurationFileName the sConfigurationFileName to set
	 */
	public void setConfigurationFileName(String sConfigurationFileName) {
		this.sConfigurationFileName = sConfigurationFileName;
	}

	/**
	 * 
	 * @param fConfigurationFileName
	 */
	public void setConfigurationFileName(File fConfigurationFileName) {
		this.fConfigurationFileName = fConfigurationFileName;
	}
		
	/**
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		HashMap<String,String> hmssPrettyPrint = new HashMap<String,String>();
		
		hmssPrettyPrint.put("FileName", sConfigurationFileName);
		hmssPrettyPrint.put("SharedSecret", this.sCmtsSharedSecretKey);
		hmssPrettyPrint.put("ByteLength", Integer.toString((size()))); 
		hmssPrettyPrint.put("ConfigVersion", Integer.toString((iConfigurationFileType)));
		
		return hmssPrettyPrint.toString();
	}
	
	/**
	 * 
	 * @return boolean
	 */
	public boolean writeToDisk () {
		
		boolean localDebug = Boolean.FALSE;
		
		if (toByteArray() == null) {

			if (debug|localDebug) {
				System.out.println("ConfigurationFile.writeToDisk() - NULL ByteArray");
			}
			
			return false;
		}
		
		if (debug|localDebug) {
			System.out.println("ConfigurationFile.writeToDisk() " +
									" - Total Byte Count: " + toByteArray().length +
									" - FileName: " + sConfigurationFileName );
		}
		
		OutputStream out = null;
		
		if (this.fConfigurationFileName != null) {
			
			try {
				out = new FileOutputStream(fConfigurationFileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			if (debug|localDebug) {
				System.out.println("BuildFile.writeToDisk() " +
										" - Total Byte Count: " + toByteArray().length +
										" - FileName: " + fConfigurationFileName );
			}
			
		} else if (this.sConfigurationFileName.length() != 0) {
			
			try {

				out = new FileOutputStream(sConfigurationFileName);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			
			File fCf = null;
			
			try {
				fCf = new File(new java.io.File( "." ).getCanonicalPath() 
						+ File.separatorChar + "testfiles" + File.separatorChar +  "Configuration.cm");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				out = new FileOutputStream(fCf);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			if (debug|localDebug) {
				System.out.println("BuildFile.writeToDisk() " +
										" - Total Byte Count: " + toByteArray().length +
										" - FileName: " + fCf );
			}
		}
				
		try {
			out.write(toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		    	
		return true;
	}
		
	/**
	 *  Need to work on this
	 * 
	
	 * @return boolean
	 */
	public boolean writeToZip () {	
		@SuppressWarnings("unused")
		ZipParameters zp = initZipParameters();
		return true;
	}
	
	/**
	 * 
	 * This will remove the List of TLV from Buffer and update Class Buffer
	 * 
	 * @param liStripTLV
	
	 */
	public void stripTlv(List<Integer> liStripTLV) {
		
		boolean localDebug = Boolean.FALSE;
			
		//Copy Class TLV Buffer
		byte[] baTLV = toByteArray();

		if (debug|localDebug) {
			System.out.println("ConfigurationFile.stripTlv(li,bool): - BufferSize: " + baTLV.length);
		}
		
		for (int iType : liStripTLV) {
			
			try {
				baTLV = TlvBuilder.stripTlv (iType , baTLV , this.miiTypeToByteLengh);
			} catch (TlvException e) {
				e.printStackTrace();
			}
			
			if (debug|localDebug) {
				System.out.println("ConfigurationFile.stripTlv(li,bool): - BufferSize: " + baTLV.length);
			}
		}
		
		TlvVariableBinding tvb = new TlvVariableBinding(baTLV,this.getTypeToByteLengthMap());
		
		//Clears Compiler buffers  and local buffer array
		clear();
		
		this.updateConfigurationFileTlvBuilder(tvb);

		updateCompiler(tvb);	
		
	}
	
	/**
	 * Removal of CM/CMTS MIC MD5 and BASIC.1 SHA-1 HASHS
	 */
	public void removeAllSecurityHash() {
		
	}
	
	/**
	 * 
	 * @return a new TlvBuilder Object
	 */
	public TlvBuilder toTlvBuilder() {
		TlvBuilder tb = new TlvBuilder();
		try {
			tb.add(new HexString(toByteArray()));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		return tb;
		
	}
	
	/**
	 * 
	 * @param bTLV - ByteArray of TLV File
	 * @return true = DOCSIS , false if PacketCable * @throws NullPointerException */
	public static boolean configurationFileTypeCheck(byte[] bTLV) throws NullPointerException{
		
		boolean boolIsPacketCable = false;
		
		if (bTLV == null) {
			throw new NullPointerException("ConfigurationFile.configurationFileTypeCheck(): Input is a null");
		}
		
		if (bTLV[0] == PacketCableConstants.FILE_MARKER) {
			boolIsPacketCable =  true;
		}
		
		return boolIsPacketCable;
		
	}
		
	/**
	 * 
	 * @param tbTLV
	 * @param iConfigurationFileType
	
	
	 * @return TlvBuilder
	 * @throws TlvException */
	public static TlvBuilder stripFinalize(TlvBuilder tbTLV , int iConfigurationFileType) throws TlvException {

		TlvBuilder tb = new TlvBuilder();

		//Lets Strip TLV 6,7,255 and PADDs
		byte[] bTLV = null;

		if (iConfigurationFileType <= ConfigurationFile.DOCSIS_VER_31) {

			if (tbTLV != null) {

				try {
					bTLV = TlvBuilder.stripTlv(6, tbTLV.toByteArray());
					bTLV = TlvBuilder.stripTlv(7, bTLV);
					bTLV = TlvBuilder.stripTlv(255, bTLV);
					bTLV = TlvBuilder.stripTlv(0, bTLV);
				} catch (TlvException e) {
					e.printStackTrace();
				}

				tb.add(new HexString(bTLV));

			} 
		
		//TODO - will need to write a striping Hashing Method	
		} else if (iConfigurationFileType > ConfigurationFile.DOCSIS_VER_31) {
			
			//Need to pass any Snmp64 Type TLVs, copying over the byteArray may break Encoding
			tb.add(tbTLV);
		}

		return tb;

	}
	
	/* ******************************************************************************************
	 * 									Private Method
	 ********************************************************************************************/
	
	/**
	 *
	 */
	private void init() {
		
		boolean localDebug = Boolean.FALSE;
		
		if ((iConfigurationFileType >= ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE) && 
				(iConfigurationFileType <= ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE)) {
						
			this.miiTypeToByteLengh = new DictionarySQLQueries().getTypeToByteLengthMap(DictionarySQLQueries.DOCSIS_DICTIONARY_TABLE_NAME);
			
			dcConfigurationFile = new DocsisCompiler(sCmtsSharedSecretKey,iConfigurationFileType);
			
			TlvBuilder tbNoFinalize = null;
			
			//Update without CM/CMST/EOF and PADDs
			try {
				tbNoFinalize = stripFinalize();
			} catch (TlvException e) {
				e.printStackTrace();
			}

			//Clear ConfigurationFile TlvBuilder 		
			tbConfigurationFile.clear();

			updateConfigurationFileTlvBuilder(tbNoFinalize);
			
			//Update DOCSIS Compiler
			updateCompiler(this.tbConfigurationFile);
			
			if (debug|localDebug)
				System.out.println(
						"ConfigurationFile.init() - DOCSIS-TYPE " +
						" -> TlvBuilder: " + this.tbConfigurationFile.toString() +
						" -> DocsisCompiler: " + dcConfigurationFile.toString() +
						" -> DocsisCompiler-Length: " + dcConfigurationFile.toByteArray().length
				);
			
		} else if ((iConfigurationFileType >= ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE) && 
				(iConfigurationFileType <= ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE)) {
			
			this.miiTypeToByteLengh = new DictionarySQLQueries().getTypeToByteLengthMap(DictionarySQLQueries.PACKET_CABLE_DICTIONARY_TABLE_NAME);
			
			pcConfigurationFile = new PacketCableCompiler(iConfigurationFileType);
			
			updateCompiler(this.tbConfigurationFile);
						
			if (debug|localDebug)
				System.out.println(
						"ConfigurationFile.init() - PACKET-CABLE-TYPE " +
						" -> TlvBuilder: " + tbConfigurationFile.toString()
				);
			
		} else if ((iConfigurationFileType >= ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE) && 
				(iConfigurationFileType <= ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE)) {

			this.miiTypeToByteLengh = new DictionarySQLQueries().getTypeToByteLengthMap(DictionarySQLQueries.DPOE_DICTIONARY_TABLE_NAME);
			
			dpoeConfigurationFile = new DPoECompiler(sCmtsSharedSecretKey, iConfigurationFileType);
						
			TlvBuilder tbNoFinalize = null;
			
			//Update without CM/CMST/EOF and PADDs
			try {
				tbNoFinalize = stripFinalize(dpoeConfigurationFile);
			} catch (TlvException e) {
				e.printStackTrace();
			}

			//Clear ConfigurationFile TlvBuilder 		
			tbConfigurationFile.clear();

			updateConfigurationFileTlvBuilder(tbNoFinalize);
			
			if (localDebug)
				System.out.println("ConfigurationFile.init() -> " + TlvBuilder.tlvDump(tbConfigurationFile.toByteArray()));
			
			//Update DOCSIS Compiler
			updateCompiler(this.tbConfigurationFile);
			
			if (debug|localDebug)
				System.out.println(
						"ConfigurationFile.init()" + "\n" + 
						" -> DPOE-TYPE " + "\n" +
						" -> ConfigurationFileTlvBuilder: " + tbConfigurationFile.toString() + "\n" +
						" -> DPoECompiler: \t\t " + dpoeConfigurationFile.toString() + "\n" +
						" -> ConfigurationFileTlvBuilder - ByteArray Length: " + tbConfigurationFile.toByteArray().length + "\n" +
						" -> DPoECompiler - ByteArray Length:                " + dpoeConfigurationFile.toByteArray().length + "\n"
				);		
		}
		
	}
			
	/**
	 * 
	 * This method will finalize after the adding a TlvBuilder Object
	 * 
	 * @param tbTLV TlvBuilder
	 */
	private void addTlvBuilder(TlvBuilder tbTLV) {
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug)
			System.out.println("ConfigurationFile.addFinalize() - ARG-Length: " + tbTLV.toString());

		if (debug|localDebug)
			System.out.println("ConfigurationFile.addFinalize(): Length-BeforeAdd: " + tbConfigurationFile.length() + " - " + tbConfigurationFile.toString());
				
		//Strip Finalize
		try {
			stripFinalize();
		} catch (TlvException e) {
			e.printStackTrace();
		}
				
		//Update Local TlvBuilder
		updateConfigurationFileTlvBuilder(tbTLV);
				
		if (debug|localDebug)
			System.out.println("ConfigurationFile.addFinalize(): Length-After-Add: " 	+ tbConfigurationFile.length() 
																						+ " - " + tbConfigurationFile.toString());
				
		updateCompiler(tbTLV);
		
	}
	
	/**
	 * 
	 * @param tbTLV TlvBuilder
	 */
	@SuppressWarnings("unused")
	private void addFirstTlvBuilder(TlvBuilder tbTLV) {
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug)
			System.out.println("ConfigurationFile.addFirstTlvBuilder() - ARG-Length: " + tbTLV.toString());

		if (debug|localDebug)
			System.out.println("ConfigurationFile.addFirstTlvBuilder(): Length-BeforeAdd: " + tbConfigurationFile.length() + " - " + tbConfigurationFile.toString());
				
		//Strip Finalize
		try {
			stripFinalize();
		} catch (TlvException e) {
			e.printStackTrace();
		}
				
		//Update Local TlvBuilder
		updateConfigurationFileTlvBuilder(tbTLV);
				
		if (debug|localDebug)
			System.out.println("ConfigurationFile.addFirstTlvBuilder(): Length-After-Add: " 	+ tbConfigurationFile.length() 
																								+ " - " + tbConfigurationFile.toString());
				
		updateCompiler(tbTLV);
	}

	/**
	 * 
	 * @param tbTLV
	 * @param liStripTlvList*/
	private void addTlvBuilder(TlvBuilder tbTLV, List<Integer> liStripTlvList) {
		
		boolean localDebug = Boolean.FALSE;

		if (debug|localDebug) {
			System.out.println("ConfigurationFile.addTlvBuilder(tb,li): - Hex: " + tbTLV.toStringSeperation(":"));
			System.out.println("ConfigurationFile.addTlvBuilder(tb,li): - TlvToStripFromBufferCLassList: " + liStripTlvList);
		}
			
		stripTlv(liStripTlvList);
		
		addTlvBuilder(tbTLV);
			
	}
	
	/**
	 * @deprecated - Will need to replace as needed - Will no longer support this method
	 * @return TlvBuilder
	 * @throws TlvException */
	private TlvBuilder stripFinalize() throws TlvException {
	
		TlvBuilder tb = new TlvBuilder();
			
		//Lets Strip TLV 6,7,255 and PADDs
		byte[] bTLV = null;
			
		if (dcConfigurationFile != null) {
			
			//140109 - Added miiSnmp64TwoByteLength
			try {
				bTLV = TlvBuilder.stripTlv(Constants.CM_MIC, this.tbConfigurationFile.toByteArray(),this.getTypeToByteLengthMap());
				bTLV = TlvBuilder.stripTlv(Constants.CMTS_MIC, bTLV,this.getTypeToByteLengthMap());
				bTLV = TlvBuilder.stripTlv(Constants.END_OF_FILE, bTLV,this.getTypeToByteLengthMap());
				bTLV = TlvBuilder.stripTlv(Constants.PAD, bTLV,this.getTypeToByteLengthMap());
			} catch (TlvException e) {
				e.printStackTrace();
			}
			
			tb.add(new HexString(bTLV));
		
		} else if (pcConfigurationFile != null) {}
		
		return tb;
		
	}
	
	/**
	 * 
	 * @param oCompiler - ConfigurationFileCompiler
	 * @return A new TlvBuilder object of the Configuration File.
	 * @throws TlvException */
	private TlvBuilder stripFinalize(Object oCompiler) throws TlvException {
		
		TlvBuilder tb = new TlvBuilder();
			
		//Lets Strip TLV 6,7,255 and PADDs
		byte[] bTLV = null;
			
		/* DocsisCompiler Super Classes Support this Striping of TLV -> DPoE and DPoG Configuration File Types*/
		if (oCompiler.getClass().getSuperclass().getSimpleName().equalsIgnoreCase("DocsisCompiler")) {
		
			try {
				bTLV = TlvBuilder.stripTlv(Constants.CM_MIC, this.tbConfigurationFile.toByteArray(),this.getTypeToByteLengthMap());
				bTLV = TlvBuilder.stripTlv(Constants.CMTS_MIC, bTLV,this.getTypeToByteLengthMap());
				bTLV = TlvBuilder.stripTlv(Constants.END_OF_FILE, bTLV,this.getTypeToByteLengthMap());
				bTLV = TlvBuilder.stripTlv(Constants.PAD, bTLV,this.getTypeToByteLengthMap());
			} catch (TlvException e) {
				e.printStackTrace();
			} 
			
			tb.add(new HexString(bTLV));
			
		/* All other Configuration file Type bypass */	
		} else {
			tb.add(this.tbConfigurationFile.clone());
		}

		return tb;
		
	}
		
	/**
	 * 
	 * @param tb*/
	private void updateCompiler(TlvBuilder tb) {
		
		if ((iConfigurationFileType >= ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE) && 
				(iConfigurationFileType <= ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE)) {
			this.dcConfigurationFile.add(tb);
		} else if ((iConfigurationFileType >= ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE) && 
				(iConfigurationFileType <= ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE)) {
			this.pcConfigurationFile.add(tb);
		} else if ((iConfigurationFileType >= ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE) && 
				(iConfigurationFileType <= ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE)) {
			this.dpoeConfigurationFile.add(tb);
		}
		
	}
	
	/**
	 * 
	 * @param tvb*/
	private void updateCompiler(TlvVariableBinding tvb) {
		
		if ((iConfigurationFileType >= ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE) && 
				(iConfigurationFileType <= ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE)) {
			this.dcConfigurationFile.add(tvb);
		} else if ((iConfigurationFileType >= ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE) && 
				(iConfigurationFileType <= ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE)) {
			this.pcConfigurationFile.add(tvb);
		} else if ((iConfigurationFileType >= ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE) && 
				(iConfigurationFileType <= ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE)) {
			this.dpoeConfigurationFile.add(tvb);
		}
		
	}
	
	/**
	 * 	
	 * @return Type to ByteLength Map */
	private Map<Integer,Integer> getTypeToByteLengthMap() {
		return miiTypeToByteLengh;
	}
		
	/**
	 * 
	 * @param tb
	 */
	private void updateConfigurationFileTlvBuilder(TlvBuilder tb) {
		this.tbConfigurationFile.add(tb);
	}
	
	/**
	 * 
	 * @param tvb
	 */
	private void updateConfigurationFileTlvBuilder(TlvVariableBinding tvb) {
		this.tbConfigurationFile.add(tvb);
	}

	/**
	 * 
	 * @return ZipParameters
	 */
	private ZipParameters initZipParameters() {
				
		// Initiate Zip Parameters which define various properties such
		// as compression method, etc. More parameters are explained in other
		// examples
		ZipParameters zpParameters = new ZipParameters();
		
		// set compression method to deflate compression
		zpParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); 
		
		/* 
		 Set the compression level. This value has to be in between 0 to 9
		 Several predefined compression levels are available
		 DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
		 DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
		 DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
		 DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
		 DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
		*/
		zpParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); 
		
		return zpParameters;
	}
	
	/**
	 * 
	
	 * @param zp ZipParameters
	 * @param fFileRootDir File
	 */
	@SuppressWarnings("unused")
	private void setZipFolderRoot(ZipParameters zp ,File fFileRootDir) {
		zp.setRootFolderInZip(fFileRootDir.getPath() + File.separator);
	}

	/**
	 * Need to work on this
	 * @param f
	
	
	 * @return ZipFile
	 * @throws ZipException */
	@SuppressWarnings("unused")
	private ZipFile ZipFileFactory(File f) throws ZipException {		
		return null;
	}
	
}
