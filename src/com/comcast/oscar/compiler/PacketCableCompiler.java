package com.comcast.oscar.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.asn1.BER;
import org.snmp4j.smi.OID;

import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.dictionary.DictionarySQLQueries;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.tlv.TlvVariableBinding;
import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.CheckSum;
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


 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */


public class PacketCableCompiler extends TlvBuilder {
	//Log4J2 logging
    private static final Logger logger = LogManager.getLogger(PacketCableCompiler.class);
	
	private Integer iPacketCableVersion = 0;
	
	private static boolean debug = Boolean.FALSE;
	
	private ByteArrayOutputStream baosTlvFinalizeArray = new ByteArrayOutputStream();
	
	/**
	 * 
	 * @param iPacketCableVersion
	 */
	public PacketCableCompiler (Integer iPacketCableVersion) {
		super();
		this.iPacketCableVersion = iPacketCableVersion;
	}
		
	/**
	 * this methods creates the SHA-1 Hash required for BASIC.1 file integrity check based on the PacketCable Version
	 */
	public void commit () {
		
		boolean localDebug = Boolean.FALSE;
		
		byte[] bTlvFileMarker = null;
		
		byte[] bSHA1 = null;
		
		//Create new ByteArrayOutputStream object
		if (baosTlvFinalizeArray.size() != 0) {
			baosTlvFinalizeArray = new ByteArrayOutputStream();
		}
	
		//Strip FILE Markers Just in case they are already there
		try {
			bTlvFileMarker = stripTlv(BinaryConversion.byteToUnsignedInteger(PacketCableConstants.FILE_MARKER), super.toByteArray());
		} catch (TlvException e2) {
			e2.printStackTrace();
		}
		
		if (localDebug|debug)
			logger.debug("PacketCableCompiler.finalize() = STRIP-FILE-MRK:        " + new HexString(bTlvFileMarker).toString());
		
		//Insert StartOfFileMarker + EndOfFileMarker
		try {
			bTlvFileMarker = setEndOfFileMarker(setStartOfFileMarker(bTlvFileMarker));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (localDebug|debug)
			logger.debug("PacketCableCompiler.finalize() = SET-FILE-MRK:          " + new HexString(bTlvFileMarker).toString());

		//Calculate SHA-1 HASH with File Markers
		bSHA1 = CheckSum.getSHA1(bTlvFileMarker);

		if (localDebug|debug)
			logger.debug("PacketCableCompiler.finalize() = SHA1-HASH-HEX:         " + new HexString(bSHA1).toString());
		
		//Strip FILE Markers
		try {
			bTlvFileMarker = stripTlv(BinaryConversion.byteToUnsignedInteger(PacketCableConstants.FILE_MARKER), bTlvFileMarker);
		} catch (TlvException e2) {
			e2.printStackTrace();
		}
		
		if (localDebug|debug)
			logger.debug("PacketCableCompiler.finalize() = STRIP-FILE-MRK-HEX:    " + new HexString(bTlvFileMarker).toString());
		
		//Insert SHA1 HASH BASIC.1 Authentication
		try {
			bTlvFileMarker = insertSHA1Hash(bTlvFileMarker,bSHA1);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (localDebug|debug)
			logger.debug("PacketCableCompiler.finalize() = ADD-SHA1-OID-HASH-HEX: " + new HexString(bTlvFileMarker).toString());
	
		//Insert StartOfFileMarker + EndOfFileMarker		
		try {
			bTlvFileMarker = setEndOfFileMarker(setStartOfFileMarker (bTlvFileMarker));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		if (localDebug|debug)
			logger.debug("PacketCableCompiler.finalize() = SET-FILE-MRK:          " + new HexString(bTlvFileMarker).toString());
		
		try {
			this.baosTlvFinalizeArray.write(bTlvFileMarker);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @return byte[]
	 * @see com.comcast.oscar.tlv.TlvBuild#toByteArray()
	 */
	@Override
	public byte[] toByteArray () {
		
		if (baosTlvFinalizeArray.size() == 0) {
			return super.toByteArray();
		} else {
			return baosTlvFinalizeArray.toByteArray();
		}
		
	}
	
	/**
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		
		//If this is 0, then finalized was never called
		if (baosTlvFinalizeArray.size() == 0) {
			return super.toString();
		} else {
			return new HexString(baosTlvFinalizeArray.toByteArray()).toString();				
		}
		
	}

	/**
	 * 
	 * @param sSeperation
	
	 * @return String
	 */
	public String toString(String sSeperation) {
		
		//If this is 0, then finalized was never called
		if (baosTlvFinalizeArray.size() == 0) {
			return super.toString();
		} else {
			return new HexString(baosTlvFinalizeArray.toByteArray()).toString(":");				
		}
		
	}	
		
	/**
	 * 
	 * @param iType
	 * @param bTlvByteArray
	
	
	 * @return the byteArray without the Type you specified * @throws TlvException */
	public static byte[] stripTlv (int iType , byte[] bTlvByteArray) throws TlvException {

		boolean localDebug = Boolean.FALSE;
		
		if ((iType < 0) || (iType > 255))
			throw new TlvException("TlvBuilder.stripTlv() Out Of Range: " + iType);
		
		if (bTlvByteArray ==  null)
			throw new TlvException("TlvBuilder.stripTlv() Byte Array is NULL");
		
		if (localDebug) {
			logger.debug("PacketCableCompiler.stripTlv(i,b) -> StripType: " + iType + " - ByteArray Size: " + bTlvByteArray.length);
			logger.debug("PacketCableCompiler.stripTlv(i,b) -> Hex: " + new HexString(bTlvByteArray).toString(":"));
		}
		
		ByteArrayOutputStream  baosStripedTlvByteArray = new ByteArrayOutputStream();

		int iIndex = 0;
		int iTypeLength = 0;
		int iByteLengthOffSet = 0;
		
		while (iIndex < bTlvByteArray.length) {
			
			if (localDebug) {
				logger.debug("+----------------------------------------------------------------------------+");
				logger.debug("PacketCableCompiler.stripTlv(i,b)" +
						" -> Type: " + BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]) + 
						" - Index: " + iIndex + 
						" - ByteArray Size: " + bTlvByteArray.length);
			}
			
			//Check for the DoubleByte Length TLV - Total Hack!!! - Next Version, I will use the Dictionary
			if (bTlvByteArray[iIndex] == PacketCableConstants.SNMP_TLV_64) {

				if (localDebug)
					logger.debug("PacketCableCompiler.stripTlv(i,b) -> Found: Type64 - Index: " + iIndex );
												
				ByteArrayOutputStream  baosMultiByteLength = new ByteArrayOutputStream();
				
				//Create a ByteArray of ByteLength
				baosMultiByteLength.write(	bTlvByteArray,
											(iIndex+TLV_LENGTH_POS_OFFSET),
											2);
				
				//NumByteLength + 1
				iByteLengthOffSet = 3;
				
				//Get Integer from ByteArray
				iTypeLength = new HexString(baosMultiByteLength.toByteArray()).toInteger();
				
				if (localDebug) {
					logger.debug("PacketCableCompiler.stripTlv(i,b) -> Found: Type64 - Index: " + iIndex + " - TypeLength: " + iTypeLength);
				}
				
			} else {
				
				iTypeLength = (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex+TLV_LENGTH_POS_OFFSET])) + 
								TLV_TYPE_LENGTH_OVERHEAD;
				
				iByteLengthOffSet = 0;
			}
			
			//If no type match, Copy TLV to ByteArray , I have to clean this up
			if (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]) != iType) {
					
				baosStripedTlvByteArray.write(bTlvByteArray, iIndex, (iTypeLength + iByteLengthOffSet));
				
				if (localDebug) {
					logger.debug("PacketCableCompiler.stripTlv(i,b) -> Copying Bytes to ByteArray - Index: " + iIndex + " - TypeLength: " + iTypeLength);
					logger.debug("PacketCableCompiler.stripTlv(i,b) -> Hex: " + new HexString(baosStripedTlvByteArray.toByteArray()).toString(":"));
				}
			}

			//Goto Next TLV Index
			iIndex += (iByteLengthOffSet + iTypeLength);

			if (localDebug)
				logger.debug("PacketCableCompiler.stripTlv(i,b) -> NextIndex: " + iIndex + " - ByteArray Size: " + bTlvByteArray.length);

			
		}		

		return baosStripedTlvByteArray.toByteArray();
	}

	/**
	 * 
	 * This method will find each TopLevel TLV and put it into a HexString Object
	 * 
	 * @param baTlv
	
	
	 * @return list of HexString Objects * @throws TlvException  */
	public static List<HexString> getTopLevelTlvToHexStringList (byte[] baTlv) throws TlvException {

		boolean localDebug = Boolean.FALSE;

		if (localDebug|debug)
			logger.debug("+==============================================================================================================+");
		
		List<HexString> lhs = new ArrayList<HexString>();

		int iLength = 0;
		int iNumByteLength = 0;

		for (int iIndex = 0 ; iIndex < baTlv.length ;) {

			ByteArrayOutputStream baosTopLevelTLV = new ByteArrayOutputStream();

			if (baTlv[iIndex] == PacketCableConstants.SNMP_TLV_64) {

				iNumByteLength = PacketCableConstants.SNMP_TLV_64_BYTE_LENGTH;

				iLength = TlvBuilder.getTlvLength(baTlv, iIndex, iNumByteLength);

			} else { // 1 byte Length

				iNumByteLength = PacketCableConstants.PACKET_CABLE_STANDARD_BYTE_LENGTH;

				iLength = TlvBuilder.getTlvLength(baTlv, iIndex, iNumByteLength);

			}

			//Build ByteArray
			baosTopLevelTLV.write(baTlv, iIndex, (iLength + iNumByteLength + TlvBuilder.TLV_TYPE_OVERHEAD));

			//Create HexString Object
			HexString hsTLV = new HexString(baosTopLevelTLV.toByteArray());

			if (localDebug|debug)
				logger.debug("PacketCableCompiler.getTopLevelTlvToHexStringList() - TLV -> " + hsTLV.toString(":"));

			//Load HexString Object to List
			lhs.add(hsTLV);
			
			iIndex += (iLength + iNumByteLength + TlvBuilder.TLV_TYPE_OVERHEAD);
		}

		return lhs;

	}

	/**
	 * 
	 * @param baTLV	
	 * @return TlvBuilder
	 * @throws NullPointerException */
	public static TlvBuilder stripFileMarkers (byte[] baTLV) throws NullPointerException {
		
		@SuppressWarnings("unused")
		boolean localDebug = Boolean.TRUE;
		
		if (baTLV == null)
			throw new NullPointerException("PacketCableCompiler.stripFileMarkers() - Null Byte Array");
		
		byte[] baTlvTemp = null;
		
		try {
			baTlvTemp = stripTlv(BinaryConversion.byteToUnsignedInteger(PacketCableConstants.FILE_MARKER), baTLV);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		DictionarySQLQueries dsq = new DictionarySQLQueries(DictionarySQLQueries.PACKET_CABLE_DICTIONARY_TABLE_NAME);
		
		TlvBuilder tb = new TlvBuilder();
		
		tb.add(new TlvVariableBinding(baTlvTemp,dsq.getTopLevelByteLength()));
		
		return tb;
		
	}
	
	/**
	 * 
	 * This Method will remove the existing File Markers and re-add them to the ByteArray
	 * 
	 * @param baTLV
	
	
	 * @return TlvBuilder
	 * @throws NullPointerException */
	public static TlvBuilder setFileMarkers (byte[] baTLV) throws NullPointerException {
		
		
		TlvBuilder tb = null;
		
		//Get TlvBuilder without FileMarkets
		tb =  stripFileMarkers (baTLV);
		
		//Add file markers to the ByteArrya
		byte[] baPcTlv = null;
		try {
			baPcTlv = setEndOfFileMarker(setStartOfFileMarker (tb.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		//Get Type to Byte Length Mapping
		DictionarySQLQueries dsq = new DictionarySQLQueries(DictionarySQLQueries.PACKET_CABLE_DICTIONARY_TABLE_NAME);
			
		//Get TlvVariableBinding due to variable byte Length TLVs
		TlvVariableBinding tvb = new TlvVariableBinding(baPcTlv,dsq.getTopLevelByteLength());
		
		//Create new TlvBuilder
		tb = new TlvBuilder();
		
		//Add TlvVariableBinding
		tb.add(tvb);
				
		return tb;
		
	}
	
	/* *********************************************************************************
	 * 									Private Methods
	  **********************************************************************************/
	
	
	/**
	 * 
	 * @param bTlvArray
	
	
	 * @return byte[]
	 * @throws IOException */
	private static byte[] setStartOfFileMarker (byte[] bTlvArray) throws IOException {
		
		ByteArrayOutputStream  baosbTlvArray = new ByteArrayOutputStream();
		
		byte[] bSOF = {PacketCableConstants.FILE_MARKER, 0x01, PacketCableConstants.START_OF_FILE};
		
		baosbTlvArray.write(bSOF);
		
		baosbTlvArray.write(bTlvArray);
		
		return baosbTlvArray.toByteArray();		
	}
	
	/**
	 * 
	 * @param bTlvArray
	
	
	 * @return byte[]
	 * @throws IOException */
	private static byte[] setEndOfFileMarker (byte[] bTlvArray) throws IOException {
		
		ByteArrayOutputStream  baosbTlvArray = new ByteArrayOutputStream();
		
		byte[] bEOF = {PacketCableConstants.FILE_MARKER, 0x01, PacketCableConstants.END_OF_FILE};
		
		baosbTlvArray.write(bTlvArray);

		baosbTlvArray.write(bEOF);

		return baosbTlvArray.toByteArray();
	}
	
	/**
	 * 
	 * @param bTlvArray
	 * @param bSHA1	
	 * @return byte[]
	 * @throws IOException */
	private byte[] insertSHA1Hash(byte[] bTlvArray, byte[] bSHA1) throws IOException {
		
		ByteArrayOutputStream  baosbTlvArray = new ByteArrayOutputStream();
		
		//Write into Array
		baosbTlvArray.write(bTlvArray);
				
		//Create SHA-1 TLV
		TlvBuilder tbTlvSHA1 = new TlvBuilder();
		
		//Select Correct Packet Cable Version
		if (iPacketCableVersion == ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE) {
			
			tbTlvSHA1.add(	PacketCableConstants.SNMP_TLV_11, 
							new OID(PacketCableConstants.PKT_CABLE_10_BASIC_AUTH_OID), 
							BER.OCTETSTRING, bSHA1);
		
		} else if (iPacketCableVersion == ConfigurationFileTypeConstants.PKT_CABLE_15_CONFIGURATION_TYPE) {
		
			tbTlvSHA1.add(	PacketCableConstants.SNMP_TLV_11, 
							new OID(PacketCableConstants.PKT_CABLE_15_BASIC_AUTH_OID), 
							BER.OCTETSTRING, bSHA1);
		
		} else if (iPacketCableVersion == ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE) {
		
			tbTlvSHA1.add(	PacketCableConstants.SNMP_TLV_11, 
							new OID(PacketCableConstants.PKT_CABLE_20_BASIC_AUTH_OID), 
							BER.OCTETSTRING, bSHA1);			
		
		}
		
		//Added SHA1 Hash BASIC.1 Authentication
		baosbTlvArray.write(tbTlvSHA1.toByteArray());

		return baosbTlvArray.toByteArray();	
	}
		
}
