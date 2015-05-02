package com.comcast.oscar.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.comcast.oscar.cablelabsdefinitions.Constants;
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


public class DocsisCompiler extends TlvBuilder {
	
	boolean debug = Boolean.FALSE;
	
	private String sSharedSecretKey = " ";

	private int iDocsisVersion;
	
	private final byte EOF = (byte) 0xFF;
	private final byte NULL = (byte) 0x00;
	
	private ByteArrayOutputStream  baosTlvFinalizeArray = new ByteArrayOutputStream();
	
	/* Use for Ext. CMTS MIC */
	@SuppressWarnings("unused")
	private int iExtCmtsHashTechnique = -1;
	private HexString hsTopLevelTlvBitMAsk = null;
	
	/**
	 * 
	 * @param iDocsisVersion
	 */
	public DocsisCompiler (Integer iDocsisVersion) {
		this.iDocsisVersion = iDocsisVersion;
	}
	
	/**
	 * 
	 * @param sSharedSecretKey
	 * @param iDocsisVersion
	 */
	public DocsisCompiler (String sSharedSecretKey , Integer iDocsisVersion) {
		this.sSharedSecretKey = sSharedSecretKey;
		this.iDocsisVersion = iDocsisVersion;
	}
	
	/**
	 * @param sSharedSecretKey String
	 * @param iDocsisVersion Integer
	 * @param iExtCmtsHashTechnique int
	 * @param hsTopLevelTlvBitMAsk HexString
	 */
	public DocsisCompiler (String sSharedSecretKey , Integer iDocsisVersion , int iExtCmtsHashTechnique , HexString hsTopLevelTlvBitMAsk) {
		this.sSharedSecretKey = sSharedSecretKey;
		this.iDocsisVersion = iDocsisVersion;
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
	
	 * @return returns byte array of the configuration file * @see com.comcast.oscar.tlv.TlvBuild#toByteArray()
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
	
	 * @return DOCSIS Version */
	public int getDocsisVersion () {
		return iDocsisVersion;
	}
		
	/**
	 * 
	 * When commit() is called this process will:
	 * 	+ calculate the CM/CMTS MIC
	 * 	+ Add EOF Marker
	 * 	+ Add Null Pad so that the byte array is divisible by 4
	 * 
	 */
	public void commit() {
		
 		boolean localDebug = Boolean.TRUE;
 		
		byte[] bTlvFinalizeArray = null;
		
		//Reseting to new ByteArray
		this.baosTlvFinalizeArray = new ByteArrayOutputStream();
		
		if (debug|localDebug) {
			System.out.println("DocsisCompiler.commit() LengthOfConfig: " + toByteArray().length);
		}
		
		/**
		 * Place holder for now
		 * 
		 * Doing this so that I do not have to strip the Configuration every time this method is called
		 * 
		 */
		if (hsTopLevelTlvBitMAsk != null) {
			try {
				baosTlvFinalizeArray.write(setExtendedCMTSMic(toByteArray()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Get TLV Byte Array then append NEW CM + CMTS MIC
		byte[] bTlvWithCmCmtsMic = setCmCmtsMIC(toByteArray());
			
		//Append EOF + PADD
		try {
			bTlvFinalizeArray = paddTlvBuffer(appendEOF(bTlvWithCmCmtsMic));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.baosTlvFinalizeArray.write(bTlvFinalizeArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 	
 	/**
 	 * 
 	 * @param boolReportStdOut
 	
 	 * @return boolean
 	 */
 	public boolean checkSpecification(Boolean boolReportStdOut) {
 		
 		boolean boolStatus = true;
 		
 		List<Integer> liTopLevelTLV = super.getTopLevelTlvList();
 		
 		for(Integer iTopLevel : Constants.DOCSIS_MIN_TLV)
 			
 			if (liTopLevelTLV.contains(iTopLevel)) {
 				
 				if (boolReportStdOut)
 					System.out.println("Missing Required TLV: " + iTopLevel);
 				
 				return false;
 			}
 		
 		return boolStatus;
 	}
 	
 	/**
 	 * 	C.1.1.18.1.6 Extended CMTS MIC Configuration Setting
			The Extended CMTS MIC Configuration Setting parameter is a multi-part encoding that configures how the CMTS
			performs message integrity checking. This is used to detect unauthorized modification or corruption of the CM
			configuration file, using techniques which are not possible using the pre-3.0 DOCSIS CMTS MIC, in particular,
			using more advanced hashing techniques, or requiring different TLVs to be included in the HMAC calculation. This
			TLV cannot be contained within an instance of TLV type 43 which contains other subtypes (excluding subtype 8).
			Type Length Value
			43.6 n Extended CMTS MIC Parameter Encoding subtype/length/value tuples
		
		C.1.1.18.1.6.1 Extended CMTS MIC HMAC type
			The Extended CMTS MIC HMAC type parameter is a single byte encoding that identifies the type of hashing
			algorithm used in the CMTS MIC hash TLV. This subtype is always included within an Extended CMTS MIC
			Configuration Setting TLV; the instance of the CMTS MIC Hash within the configuration file will use the HMAC
			technique described by the value of this TLV.
			The CMTS SHOULD support a configuration that can require all REG-REQ or REG-REQ-MP messages to contain
			an Extended CMTS MIC Encoding with a particular CMTS MIC algorithm.
			Type Length Value
			43.6.1 1 Enumeration
				1 - MD5 HMAC [RFC 2104]
				2 - MMH16-£m-n HMAC [DOCSIS SECv3.0]
				43 - vendor specific

		
		C.1.1.18.1.6.2 Extended CMTS MIC Bitmap451
			The Extended CMTS MIC Bitmap is a multi byte encoding that is a bitmask representing specified TLV types in a
			CM configuration file, REG-REQ, or REG-REQ-MP message, Annex D.2. This TLV is always present, and the
			TLVs to be included within the digest calculation are those whose top level types correspond to bits which are set in
			this value. For example, to require the Downstream Frequency Configuration Setting to be included in the digest
			calculation, set bit 1 in the value of this TLV. This TLV uses the BITS Encoding convention where bit positions are
			numbered starting with bit #0 as the most significant bit.
			Type Length Value
			43.6.2 n BITS Encoding - Each bit position in this string represents a top-level
			TLV	bit position 0 is reserved and is always set to a value of 0.
		
		C.1.1.18.1.6.3 Explicit Extended CMTS MIC Digest Subtype452
			This subtype explicitly provides the calculated extended MIC digest value over all TLVs reported in REG-REQ or
			REG-REQ-MP for which bits are set in the Extended CMTS MIC Bitmap. If the Extended CMTS MIC Bitmap
			indicates TLV 43 is to be included in the calculation of the Extended CMTS MIC digest, this subtype (with the
			value 0) is to be included in that calculation, see Annexes D.1.3 and D.2.1. A valid Explicit Extended CMTS MIC
			Digest does NOT contain the CM MIC value.
			When this subtype is present, the CMTS MIC Configuration Setting in TLV7 is calculated using the set of TLVs as
			specified for DOCSIS 2.0, in Annex D.2.1.
			If this subtype is omitted from an Extended CMTS MIC Encoding, the extended CMTS MIC is implicitly provided
			in the CMTS MIC Configuration Setting of TLV 7.
			When the Explicit Extended CMTS MIC Digest Subtype is present, if the CMTS fails the Extended CMTS MIC
			Digest verification but passes the pre-3.0 DOCSIS CMTS MIC digest verification of TLV7, then the CMTS MUST
			NOT consider the CM to have failed authentication. Instead, the CMTS MUST silently ignore all TLVs in REGREQ
			or REG-REQ-MP which were marked as protected by the Extended CMTS MIC Bitmap but are not included
			in the set of TLVs protected by the pre-3.0 DOCSIS CMTS MIC (TLV7) calculation.
			Type Length 453 Value
			43.6.3 n Calculated MIC digest using the CMTS MIC HMAC Type algorithm
 	 * @param baTLV byte[]
 	 * @return byte[]
 	 */
 	private byte[] setExtendedCMTSMic(byte[] baTLV) {
 		boolean localDebug = Boolean.FALSE;
 		
 		ByteArrayOutputStream baosExtMicTlv43_6 = new ByteArrayOutputStream();
 		
 		if (debug|localDebug)
 			System.out.println("DocsisCompiler.setExtendedCMTSMic()");
 		
 		return baosExtMicTlv43_6.toByteArray();
 	}
 	
	/**
	 * 
	 * @param sSharedSecretKey
	 */
	public void setSharedSecretKey (String sSharedSecretKey) {
		this.sSharedSecretKey = sSharedSecretKey;
	}
	
	/**
	 * 
	 * @param bTlvArray
	
	 * @return the TLV byte array with the CM/CMTS MICs */
	private byte[] setCmCmtsMIC(byte[] bTlvArray) {
		
		boolean localDebug = Boolean.TRUE;
		
		byte[] bTlvArrayNoCmCmtsMIC = null;
		
		if (debug|localDebug) {
			System.out.println("DocsisCompiler.setCmCmtsMIC() LengthOfConfig-Before: " + bTlvArray.length);
		}
				
		// Strip TLV6 + TLV7 from TLV Buffer
		try {
			bTlvArrayNoCmCmtsMIC = TlvBuilder.stripTlv(	Constants.CMTS_MIC,
														TlvBuilder.stripTlv(Constants.CM_MIC, bTlvArray));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		if (debug|localDebug) {
			System.out.println("DocsisCompiler.setCmCmtsMIC() LengthOfConfig-AfterCmCmtsStrip: " + bTlvArrayNoCmCmtsMIC.length);
		}

		// Strip EOF TLV Buffer		
		bTlvArrayNoCmCmtsMIC = stripEOFPADD(bTlvArrayNoCmCmtsMIC);

		if (debug|localDebug) {
			System.out.println("DocsisCompiler.setCmCmtsMIC() LengthOfConfig-AfterEOF-PADD: " + bTlvArrayNoCmCmtsMIC.length);
		}
		
		//Create TlvBuilder to add CM + CMTS MIC Hash
		TlvBuilder tbTlvArrayCmCmtsMIC = new TlvBuilder();
				
		//Add bTlvArrayNoCmCmtsMIC to TlvBuilder
		try {
			tbTlvArrayCmCmtsMIC.add(new HexString(bTlvArrayNoCmCmtsMIC));
		} catch (TlvException e1) {
			e1.printStackTrace();
		}
		
		// Calculate CM MIC + Append CM-MIC to TLV Buffer
		try {
			tbTlvArrayCmCmtsMIC.add(Constants.CM_MIC,getCmMIC(tbTlvArrayCmCmtsMIC.toByteArray()));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		if (debug|localDebug) {
			System.out.println("DocsisCompiler.setCmCmtsMIC() LengthOfConfig-After-Adding-CM-MIC: " + tbTlvArrayCmCmtsMIC.length());
			System.out.println("DocsisCompiler.setCmCmtsMIC() WITH-CM-MIC: " + tbTlvArrayCmCmtsMIC);
		}
		
		// Calculate CMTS MIC + Append CMTS MIC to TLV Buffer
		try {
			tbTlvArrayCmCmtsMIC.add(Constants.CMTS_MIC,getCmtsMIC(sSharedSecretKey , tbTlvArrayCmCmtsMIC.toByteArray()));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		if (debug|localDebug) {
			System.out.println("DocsisCompiler.setCmCmtsMIC() LengthOfConfig-After-2: " + tbTlvArrayCmCmtsMIC.length());
			System.out.println("DocsisCompiler.setCmCmtsMIC() CMTS-CM-MIC: " + tbTlvArrayCmCmtsMIC);
		}		
		
		return tbTlvArrayCmCmtsMIC.toByteArray();
	}
	
	/**
	 * 
	
	 * 
	 * @param bTlvArray
	
	 * @return the CM MIC HASH of the TLV Byte Array * @see
	 * D.1.3.1 CM MIC Calculation
		The CM message integrity check configuration setting MUST be calculated by performing an MD5 digest over the
		bytes of the configuration setting fields. It is calculated over the bytes of these settings as they appear in the TFTPed
		image, without regard to TLV ordering or contents.
		There are two TLVs which are not included in the CM MIC calculation:
		• The bytes of the CM MIC TLV itself are omitted from the calculation. This includes the type, length, and value
		fields;
		• The bytes of the CMTS MIC TLV are omitted from the calculation. This includes the type, length, and value
		fields.
		These TLVs are the last TLVs in the CM configuration file. */
	public  byte[] getCmMIC(byte[] bTlvArray) {
		
		boolean localDebug = Boolean.FALSE;
		
		MessageDigest mdMD5 = null;
		
	    try {
	    	mdMD5 = MessageDigest.getInstance("MD5");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	     
		if (debug|localDebug) {
			System.out.println("DocsisCompiler.setCmCmtsMIC() CM-MIC-HASH: " + new HexString(mdMD5.digest(bTlvArray)).toString());
		}
	    
	    return mdMD5.digest(bTlvArray);		
	}
	
	/**
	 * 
	 * @param sCmtsSharedSecret
	 * @param bTlvArray
	
	 * @return the CMTS MIC HASH of the TLV Byte Array */
	private byte[] getCmtsMIC(String sCmtsSharedSecret , byte[] bTlvArray) {
		
		boolean localDebug = Boolean.FALSE;
		
		byte[] bCmtsMicTlv = fetchTlv (Constants.DOCSIS_CMTS_MIC_TLV_LIST , bTlvArray);
       
		byte[] bHmacMD5 = null;
				
		SecretKeySpec keySpec = new SecretKeySpec(sCmtsSharedSecret.getBytes(),"HmacMD5");

        Mac mHmacMD5 = null;
        
		try {
			mHmacMD5 = Mac.getInstance("HmacMD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        
        try {
			mHmacMD5.init(keySpec);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
 
        bHmacMD5 = mHmacMD5.doFinal(bCmtsMicTlv);
        
        if (localDebug|debug) {
        	System.out.println("DocsisCompiler.getCmtsMIC() - SharedSecret: " + sCmtsSharedSecret);
        	System.out.println("DocsisCompiler.getCmtsMIC() - CMTS-MIC-HASH: " + new HexString(bHmacMD5).toString());     	
        }
        
        return bHmacMD5;
	}
	
	/**
	 * 
	 * @param bTlvArray
	
	
	 * @return Adds 0xFF 0x00 at End Of File * @throws IOException */
	private byte[] appendEOF (byte[] bTlvArray) throws IOException {
		
		ByteArrayOutputStream  baosbTlvArray = new ByteArrayOutputStream();
		
		baosbTlvArray.write(bTlvArray);
		baosbTlvArray.write(EOF);
		baosbTlvArray.write(NULL);
		
		return baosbTlvArray.toByteArray();
	}
	
	/**
	 * 
	 * @param bTlvArray
	
	 * @return returns a byte array padded with nulls if needed */
	private byte[] paddTlvBuffer (byte[] bTlvArray) {
		
		boolean localDebug = Boolean.FALSE;
		
		ByteArrayOutputStream  baosbTlvArray = new ByteArrayOutputStream();
		
		try {
			baosbTlvArray.write(bTlvArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Make sure TLV Buffer is divisible by 4
		while ((baosbTlvArray.size()%4) != 0) {
			
			if (debug|localDebug)
				System.out.println("DocsisCompiler.paddTlvBuffer() - Adding Padding");
			
			baosbTlvArray.write(NULL);
		}
		
		return baosbTlvArray.toByteArray();
	}
	
	/**
	 * 
	 * @param bTlvArray
	
	 * @return byte[]
	 */
	private byte[] stripEOFPADD (byte[] bTlvArray) {
		
		ByteArrayOutputStream  baosStripEOFWithPadd = new ByteArrayOutputStream();
		
		int iIndex = 0;
		
		//Locate EOF Index
		try {
			iIndex = TlvBuilder.findTLVIndex(bTlvArray, BinaryConversion.byteToUnsignedInteger(EOF));
		} catch (TlvException e) {
			e.printStackTrace();
		}
				
		baosStripEOFWithPadd.write(bTlvArray, 0 , iIndex);

		//System.out.println("B-HEX: " + new HexString(bTlvArray).toString(":"));		
		//System.out.println("A-HEX: " + new HexString(baosStripEOFWithPadd.toByteArray()).toString(":"));
		
		return baosStripEOFWithPadd.toByteArray();
	}
	
}
