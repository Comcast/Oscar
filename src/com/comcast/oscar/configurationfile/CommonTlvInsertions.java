package com.comcast.oscar.configurationfile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comcast.oscar.ber.BERService;
import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.netsnmp.NetSNMP;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
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


public class CommonTlvInsertions {
	
	// TODO this is for later use
	@SuppressWarnings("unused")
	private boolean boolEnforceSpecificationCheck = false;
	
	private List<TlvBuilder> ltb;
	private Map<Integer,TlvBuilder> mitb;
	private static boolean debug = Boolean.FALSE;
	
	public static boolean FINALIZE_TRUE = true;
	public static boolean FINALIZE_FALSE= false;
	
	/**
	 * 
	 */
	public CommonTlvInsertions () {
		this.ltb = new ArrayList<TlvBuilder>();
		this.mitb = new HashMap<Integer,TlvBuilder>();
	}

	/**
	 * 
	 * @param sTLV
	 * @param cf
	 * @param boolFinalize
	 * @throws TlvException  * @throws ConfigrationFileException */
	public static void insertTLV (String sTLV, ConfigurationFile cf , boolean boolFinalize) throws TlvException {
		
		HexString hsTLV = new HexString(HexString.toByteArray(sTLV));
		
		TlvBuilder tb = new TlvBuilder();
		
		tb.add(hsTLV);
		
		cf.add(tb);
		
		if (boolFinalize)
			cf.commit();	
	}
	
	/**
	 * 
	 * @param sTLV
	 * @param overWriteTLV
	
	 * @throws TlvException */
	public void insertTLV (String sTLV , boolean overWriteTLV) throws TlvException {
		HexString hsTLV = new HexString(HexString.toByteArray(sTLV));
		
		TlvBuilder tb = new TlvBuilder();
		
		tb.add(hsTLV);
		
		ltb.add(tb);
		
		mitb.put(BinaryConversion.byteToUnsignedInteger(tb.toByteArray()[0]), tb);	
	}
	
	/**
	
	 * @param iMaxCPE
	 * @param cf
	 * @param boolFinalize
	
	 * @throws ConfigurationFileException */
	public static void insertMaxCPE(int iMaxCPE , ConfigurationFile cf , boolean boolFinalize) throws ConfigurationFileException {
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31) 
			throw new ConfigurationFileException ("Invalid ConfigurationFileType: " + cf.getConfigurationFileType());
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.MAX_CPES, iMaxCPE);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		//Strip and ADD TLV
		List<Integer> li = new ArrayList<Integer>();
		li.add(Constants.MAX_CPES);
		
		//Strip and ADD TLV
		cf.add(tb ,li);
		
		if (boolFinalize)
			cf.commit();	
	}

	/**
	 * 
	 * @param iMaxCPE
	 * @param overWriteTLV - If TRUE it will overwrite TLV found in file
	 */
	public void insertMaxCPE(int iMaxCPE, boolean overWriteTLV) {
			
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.MAX_CPES, iMaxCPE);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ltb.add(tb);
		mitb.put(Constants.MAX_CPES, tb);
	}
	
	/**
	 * 
	 * @param iDownstreamFrequency
	 * @param cf
	 * @param boolFinalize
	
	 * @throws ConfigurationFileException */
	public static void insertDownstreamFrequency(int iDownstreamFrequency , ConfigurationFile cf , boolean boolFinalize) throws ConfigurationFileException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31) 
			throw new ConfigurationFileException ("Invalid ConfigurationFileType: " + cf.getConfigurationFileType());
		
		if (debug|localDebug)
			System.out.println("CommonTlvInsertions.insertDownstreamFrequency() - DS: " + iDownstreamFrequency);
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.DS_FREQUENCY, iDownstreamFrequency);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		if (debug|localDebug) { 
			System.out.println("CommonTlvInsertions.insertDownstreamFrequency() - Hex: " + new HexString(tb.toByteArray()).toString(":"));
			System.out.println("CommonTlvInsertions.insertDownstreamFrequency() - ConfigFileSize: " + cf.size());
		}			
		
		cf.add(tb);

		if (debug|localDebug) { 
			System.out.println("CommonTlvInsertions.insertDownstreamFrequency() - ConfigFileSize: " + cf.size());
		}
		
		if (boolFinalize)
			cf.commit();	
	}
	
	/**
	 * 
	 * @param iDownstreamFrequency
	 * @param overWriteTLV
	 */
	public void insertDownstreamFrequency(int iDownstreamFrequency, boolean overWriteTLV) {
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.DS_FREQUENCY, iDownstreamFrequency);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ltb.add(tb);
		mitb.put(Constants.DS_FREQUENCY, tb);
	}
	
	/**
	
	 * @param sFileName
	 * @param cf
	
	 * @param boolFinalize boolean
	 * @throws ConfigurationFileException if not a DOCSIS ConfigurationFile */
	public static void insertFirmwareFileName(String sFileName, ConfigurationFile cf , boolean boolFinalize) throws ConfigurationFileException {
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31) 
			throw new ConfigurationFileException ("Invalid ConfigurationFileType: " + cf.getConfigurationFileType());
		
		TlvBuilder tb = new TlvBuilder();
		
		HexString hsFileName = new HexString();
		
		hsFileName.add(sFileName);
		
		try {
			tb.add(Constants.SW_UPGRADE_FILENAME, hsFileName);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		//Strip and ADD TLV
		List<Integer> li = new ArrayList<Integer>();
		li.add(Constants.SW_UPGRADE_FILENAME);
		
		//Strip and ADD TLV
		cf.add(tb ,li);
		
		if (boolFinalize)
			cf.commit();
		
	}
	
	/**
	
	 * @param fCertificate
	 * @param cf
	
	 * @param boolFinalize boolean
	 * @throws ConfigurationFileException if not a DOCSIS ConfigurationFile  */
	public static void insertManufactureCVC(File fCertificate, ConfigurationFile cf , boolean boolFinalize) throws ConfigurationFileException {

		boolean localDebug = Boolean.FALSE;
		
		if (localDebug|debug) {
			System.out.println("CommonTlvInsertions.insertManufactureCVC(ba,cf,bool)");
			//Thread.dumpStack();
		}
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31) 
			throw new ConfigurationFileException ("Invalid ConfigurationFileType: " + cf.getConfigurationFileType());

		TlvBuilder tb = new TlvBuilder();
				
		try {
			tb.add(Constants.MANUFACTURER_CVC, HexString.fileToByteArray(fCertificate));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		//Strip and ADD TLV
		List<Integer> li = new ArrayList<Integer>();
		li.add(Constants.MANUFACTURER_CVC);
		
		//Strip and ADD TLV
		cf.add(tb ,li);
		
		if (boolFinalize)
			cf.commit();
	}
	
	/**
	
	
	 * @param cf
	
	 * @param bCertificate byte[]
	 * @param boolFinalize boolean
	 * @throws ConfigurationFileException if not a DOCSIS ConfigurationFile  */
	public static void insertManufactureCVC(byte[] bCertificate, ConfigurationFile cf , boolean boolFinalize) throws ConfigurationFileException {

		boolean localDebug = Boolean.FALSE;
		
		if (localDebug|debug) {
			System.out.println("CommonTlvInsertions.insertManufactureCVC(ba,cf,bool)");
			//Thread.dumpStack();
		}
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31) 
			throw new ConfigurationFileException ("Invalid ConfigurationFileType: " + cf.getConfigurationFileType());
	
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.MANUFACTURER_CVC, bCertificate);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		//Strip and ADD TLV
		List<Integer> li = new ArrayList<Integer>();
		li.add(Constants.MANUFACTURER_CVC);
		
		//Strip and ADD TLV
		cf.add(tb ,li);
		
		if (boolFinalize)
			cf.commit();
	}

	/**
	 * 
	
	 * @param fCertificate
	 * @param cf
	
	 * @param boolFinalize boolean
	 * @throws ConfigurationFileException if not a DOCSIS ConfigurationFile  */
	public static void insertCoSignCVC(File fCertificate, ConfigurationFile cf , boolean boolFinalize) throws ConfigurationFileException {
		
		boolean localDebug = Boolean.FALSE;
		
		if (localDebug|debug) {
			System.out.println("CommonTlvInsertions.insertCoSignCVC(f,cf,bool)");
			//Thread.dumpStack();
		}
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31) 
			throw new ConfigurationFileException ("Invalid ConfigurationFileType: " + cf.getConfigurationFileType());
	
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.CO_SIGNER_CVC, HexString.fileToByteArray(fCertificate));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		List<Integer> li = new ArrayList<Integer>();
		li.add(Constants.CO_SIGNER_CVC);
		
		//Strip and ADD TLV
		cf.add(tb ,li);
		
		if (boolFinalize)
			cf.commit();
	}
	
	/**
	
	
	 * @param cf
	
	 * @param bCertificate byte[]
	 * @param boolFinalize boolean
	 * @throws ConfigurationFileException if not a DOCSIS ConfigurationFile  */
	public static void insertCoSignCVC(byte[] bCertificate, ConfigurationFile cf, boolean boolFinalize) throws ConfigurationFileException {
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31) 
			throw new ConfigurationFileException ("Invalid ConfigurationFileType: " + cf.getConfigurationFileType());
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.CO_SIGNER_CVC, bCertificate);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		List<Integer> li = new ArrayList<Integer>();
		li.add(Constants.CO_SIGNER_CVC);
		
		//Strip and ADD TLV
		cf.add(tb ,li);
		
		if (boolFinalize)
			cf.commit();
		
	}
	
	/**
	
	 * @param sFileName
	 * @param overWriteTLV - If TRUE it will overwrite TLV found in file
	 */
	public void insertFirmwareFileName(String sFileName , boolean overWriteTLV) {
		
		TlvBuilder tb = new TlvBuilder();
		
		HexString hsFileName = new HexString();
		
		hsFileName.add(sFileName);
		
		try {
			tb.add(Constants.SW_UPGRADE_FILENAME, hsFileName);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ltb.add(tb);
		
	}

	/**
	
	 * @param fCertificate
	 * @param overWriteTLV - If TRUE it will overwrite TLV found in file
	 */
	public void insertManufactureCVC(File fCertificate , boolean overWriteTLV) {
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.MANUFACTURER_CVC, HexString.fileToByteArray(fCertificate));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ltb.add(tb);
		mitb.put(Constants.MANUFACTURER_CVC, tb);
	}
	
	/**
	 * 
	 * @param bCertificate
	 * @param overWriteTLV - If TRUE it will overwrite TLV found in file
	
	 * @throws NullPointerException */
	public void insertManufactureCVC(byte[] bCertificate , boolean overWriteTLV) throws NullPointerException {
		
		if (bCertificate == null)
			throw new NullPointerException("CommonTlvInsertions.insertManufactureCVC() - Byte Array is Null" );

		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.MANUFACTURER_CVC, bCertificate);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ltb.add(tb);
		mitb.put(Constants.MANUFACTURER_CVC, tb);
	}
	
	/**
	 * 
	 * @param fCertificate
	 * @param overWriteTLV - If TRUE it will overwrite TLV found in file
	 */
	public void insertCoSignCVC(File fCertificate , boolean overWriteTLV) {
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.CO_SIGNER_CVC, HexString.fileToByteArray(fCertificate));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ltb.add(tb);
		mitb.put(Constants.CO_SIGNER_CVC, tb);
	}
	
	/**
	 * 
	 * @param bCertificate
	 * @param overWriteTLV - If TRUE it will overwrite TLV found in file
	
	 * @throws NullPointerException */
	public void insertCoSignCVC(byte[] bCertificate , boolean overWriteTLV) throws NullPointerException {
		
		if (bCertificate == null)
			throw new NullPointerException("CommonTlvInsertions.insertManufactureCVC() - Byte Array is Null" );
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(Constants.CO_SIGNER_CVC, bCertificate);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ltb.add(tb);
		mitb.put(Constants.CO_SIGNER_CVC, tb);
		
	}

	/**
	 * 
	 * @param baDigitMap
	 * @param sOID
	 * 
	 * This method can be used multiple time, and the number of instances can be found in the getTlvBuilderList()
	 */
	public void insertDigitMap(byte[] baDigitMap, String sOID) {
				
		if (sOID == null) {
			ltb.add(DigitMapOperation.getDigitMapTlvBuilder(baDigitMap,DigitMapOperation.DEFAULT_DIGIT_MAP_OID));	
		} else {
			ltb.add(DigitMapOperation.getDigitMapTlvBuilder(baDigitMap,sOID));
		}
		
	}
	
	/**
	 * 
	 * @param baDigitMap
	 * @param sOID = If null, will insert the Default DIGIT Map OID = DigitMapOperation.DEFAULT_DIGIT_MAP_OID
	 * @param cf
	 * @param boolFinalize
	
	 * @throws ConfigurationFileException */
	public static void insertDigitMap(byte[] baDigitMap, String sOID , ConfigurationFile cf, boolean boolFinalize) throws ConfigurationFileException {
		
		if (cf.getConfigurationFileType() > ConfigurationFile.PKT_CBL_VER_20) 
			throw new ConfigurationFileException ("Invalid ConfigurationFileType: " + cf.getConfigurationFileType());
		
		if (sOID == null) {
			cf.add(DigitMapOperation.getDigitMapTlvBuilder(baDigitMap, DigitMapOperation.DEFAULT_DIGIT_MAP_OID));	
		} else {
			cf.add(DigitMapOperation.getDigitMapTlvBuilder(baDigitMap, sOID));	
		}
			
		if (boolFinalize)
			cf.commit();
	}

	/**
	 * 
	 * @param sOID
	 * @param bDataType
	 * @param lValue
	 * @param cf
	 * @param boolFinalize
	 */
	public static void insertSnmpOID(String sOID , byte bDataType , long lValue , ConfigurationFile cf, boolean boolFinalize) {
		
		byte[] baSnmpOID = null;
		try {
			baSnmpOID = BERService.setOIDEncodingToByteArray(sOID, bDataType, lValue);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31)
			try {
				baSnmpOID = TlvBuilder.encapsulateTlv(11, baSnmpOID);
			} catch (TlvException e) {
				e.printStackTrace();
			}
		else
			try {
				baSnmpOID = TlvBuilder.encapsulateTlv(11, baSnmpOID);
			} catch (TlvException e) {
				e.printStackTrace();
			}
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(new HexString(baSnmpOID));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		cf.add(tb);
					
		if (boolFinalize)
			cf.commit();
	}

	/**
	 * 
	 * @param sOID
	 * @param bDataType
	 * @param sValue
	 * @param cf
	 * @param boolFinalize
	 */
	public static void insertSnmpOID(String sOID , byte bDataType , String sValue , ConfigurationFile cf, boolean boolFinalize) {
		
		
		byte[] baSnmpOID = null;
		try {
			baSnmpOID = BERService.setOIDEncodingToByteArray(sOID, bDataType, sValue);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31)
			try {
				baSnmpOID = TlvBuilder.encapsulateTlv(Constants.SNMP_MIB_OBJ, baSnmpOID);
			} catch (TlvException e) {
				e.printStackTrace();
			}
		else
			try {
				baSnmpOID = TlvBuilder.encapsulateTlv(Constants.SNMP_MIB_OBJ, baSnmpOID);
			} catch (TlvException e) {
				e.printStackTrace();
			}
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(new HexString(baSnmpOID));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		cf.add(tb);
					
		if (boolFinalize)
			cf.commit();
	}
	
	/**
	 * 
	
	 * @return List<TlvBuilder>
	 */
	public List<TlvBuilder> getTlvBuilderList() {
		return this.ltb;
	}
	
	/**
	 * 
	
	 * @return Map<Integer,TlvBuilder>
	 */
	public Map<Integer,TlvBuilder> getTlvBuilderMap() {
		return this.mitb;
	}
	
	/**
	 * 
	 * @param sInetAddress
	 * @param cf
	 * @param boolFinalize
	
	 * @throws ConfigurationFileException */
	public static void insertTftpServerAddress(String sInetAddress , ConfigurationFile cf , boolean boolFinalize) throws ConfigurationFileException {
		
		if (cf.getConfigurationFileType() > ConfigurationFile.DOCSIS_VER_31) 
			throw new ConfigurationFileException ("Invalid ConfigurationFileType: " + cf.getConfigurationFileType());
		
		TlvBuilder tb = new TlvBuilder();
		
		List<Integer> li = null;
		
		byte[] baInetAddress = HexString.inetAddressToByteArray(sInetAddress); 
		
		if (baInetAddress.length == 4) {
			try {
				tb.add(Constants.SW_UPGRADE_IPV4_TFTP_SRV, baInetAddress);
			} catch (TlvException e) {
				e.printStackTrace();
			}
			
			//Strip TLV
			li = new ArrayList<Integer>();
			li.add(Constants.SW_UPGRADE_IPV4_TFTP_SRV);
						
		} else {
			
			try {
				tb.add(Constants.SW_UPGRADE_IPV6_TFTP_SRV, baInetAddress);
			} catch (TlvException e) {
				e.printStackTrace();			
			}
			
			//Strip TLV
			li = new ArrayList<Integer>();
			li.add(Constants.SW_UPGRADE_IPV6_TFTP_SRV);
			
		}
				
		//ADD TLV
		cf.add(tb ,li);
		
		if (boolFinalize)
			cf.commit();	
	}

	/**
	 * 
	 * @param sInetAddress
	 * @param overWriteTLV
	 */
	public void insertTftpServerAddress(String sInetAddress, boolean overWriteTLV) {

		TlvBuilder tb = new TlvBuilder();

		byte[] baInetAddress = HexString.inetAddressToByteArray(sInetAddress); 
				
		if (baInetAddress.length == 4) {
			try {
				tb.add(Constants.SW_UPGRADE_IPV4_TFTP_SRV, baInetAddress);
			} catch (TlvException e) {
				e.printStackTrace();
			}
			
			ltb.add(tb);
			
			mitb.put(Constants.SW_UPGRADE_IPV4_TFTP_SRV, tb);
			
		} else {
			
			try {
				tb.add(Constants.SW_UPGRADE_IPV6_TFTP_SRV, baInetAddress);
			} catch (TlvException e) {
				e.printStackTrace();
			
			}
			
			ltb.add(tb);
			
			mitb.put(Constants.SW_UPGRADE_IPV6_TFTP_SRV, tb);		
		}
		

	}
	
	/**
	 * 
	 * @param sOID
	 * @param bDataType
	 * @param sValue
	 */
	public void insertSnmpOID(String sOID , byte bDataType , String sValue) {
		
		
		byte[] baSnmpOID = null;
		
		try {
			baSnmpOID = BERService.setOIDEncodingToByteArray(sOID, bDataType, sValue);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			baSnmpOID = TlvBuilder.encapsulateTlv(Constants.SNMP_MIB_OBJ, baSnmpOID);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(new HexString(baSnmpOID));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ltb.add(tb);
		
		mitb.put(Constants.SNMP_MIB_OBJ, tb);	
		
	}	

	/**
	 * 
	 * @param sOID
	 * @param bDataType
	 * @param lValue
	 */
	public void insertSnmpOID(String sOID , byte bDataType , long lValue) {
		
		
		byte[] baSnmpOID = null;
		
		try {
			baSnmpOID = BERService.setOIDEncodingToByteArray(sOID, bDataType, lValue);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			baSnmpOID = TlvBuilder.encapsulateTlv(Constants.SNMP_MIB_OBJ, baSnmpOID);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		TlvBuilder tb = new TlvBuilder();
		
		try {
			tb.add(new HexString(baSnmpOID));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ltb.add(tb);
		
		mitb.put(Constants.SNMP_MIB_OBJ, tb);	
		
	}
	
}
