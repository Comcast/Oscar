/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.configurationfile.DocsisVersion;
import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DocsisCompiler extends TlvBuilder {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(DocsisCompiler.class);

  private String sSharedSecretKey = " ";

  private int iDocsisVersion;

  private final byte EOF = (byte) 0xFF;
  private final byte NULL = (byte) 0x00;

  private ByteArrayOutputStream baosTlvFinalizeArray = new ByteArrayOutputStream();

  /**
   * @param iDocsisVersion
   */
  public DocsisCompiler(Integer iDocsisVersion) {
    this.iDocsisVersion = iDocsisVersion;
  }

  /**
   * @param sSharedSecretKey
   * @param iDocsisVersion
   */
  public DocsisCompiler(String sSharedSecretKey, Integer iDocsisVersion) {
    this.sSharedSecretKey = sSharedSecretKey;
    this.iDocsisVersion = iDocsisVersion;
  }

  /**
   * @param sSharedSecretKey String
   * @param iDocsisVersion Integer
   * @param iExtCmtsHashTechnique int
   * @param hsTopLevelTlvBitMAsk HexString
   */
  public DocsisCompiler(
      String sSharedSecretKey,
      Integer iDocsisVersion,
      int iExtCmtsHashTechnique,
      HexString hsTopLevelTlvBitMAsk) {
    this.sSharedSecretKey = sSharedSecretKey;
    this.iDocsisVersion = iDocsisVersion;
  }

  /**
   * @return String
   */
  @Override
  public String toString() {

    // If this is 0, then finalized was never called
    if (baosTlvFinalizeArray.size() == 0) {
      return super.toString();
    } else {
      return new HexString(baosTlvFinalizeArray.toByteArray()).toString();
    }
  }

  /**
   * @param sSeperation
   * @return String
   */
  public String toString(String sSeperation) {

    // If this is 0, then finalized was never called
    if (baosTlvFinalizeArray.size() == 0) {
      return super.toString();
    } else {
      return new HexString(baosTlvFinalizeArray.toByteArray()).toString(":");
    }
  }

  /**
   * @return returns byte array of the configuration file * @see
   *     com.comcast.oscar.tlv.TlvBuild#toByteArray()
   */
  @Override
  public byte[] toByteArray() {

    if (baosTlvFinalizeArray.size() == 0) {
      return super.toByteArray();
    } else {
      return baosTlvFinalizeArray.toByteArray();
    }
  }

  /**
   * Clears editable parameters and cached finalized bytes without writing files. After clear(),
   * toByteArray() returns an empty array and a DOCSIS minimum check fails until the required
   * parameters are added again.
   */
  @Override
  public void clear() {
    super.clear();
    baosTlvFinalizeArray.reset();
  }

  /**
   * @return DOCSIS Version
   */
  public int getDocsisVersion() {
    return iDocsisVersion;
  }

  /**
   * Finalizes the assembled configuration using this compiler's selected family and shared secret.
   * For DOCSIS, checks required top-level parameters before generating CM/CMTS MICs, End and
   * padding, then verifies finalized minimum presence. Legacy DPoE subclasses retain their own
   * family rules. Does not write a file; successful output is available through toByteArray().
   * Previous finalized bytes are discarded before validating the current parameters.
   *
   * <p>Example: a DOCSIS 3.1 draft with only NetworkAccess fails with missing TLVs 24 and 25; a
   * complete parameter set produces binary bytes containing MICs and End.
   *
   * @throws IllegalArgumentException if required DOCSIS parameters are absent or finalization fails
   *     its framing, integrity-parameter or key checks
   */
  public void commit() {

    byte[] bTlvFinalizeArray = null;

    // Reseting to new ByteArray
    this.baosTlvFinalizeArray = new ByteArrayOutputStream();

    if (logger.isDebugEnabled()) {
      logger.debug("DocsisCompiler.commit() LengthOfConfig: " + toByteArray().length);
    }

    if (iDocsisVersion <= ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE) {
      DocsisMinimumRequirements.validateParameters(
          HexString.toByteArray(super.toString()),
          DocsisVersion.fromConfigurationFileType(iDocsisVersion));
    }

    if (iDocsisVersion == ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE) {
      baosTlvFinalizeArray.writeBytes(
          DocsisMicFinalizer.finish(
              super.toByteArray(), TlvRegistry.forFamily("docsis"), sSharedSecretKey));
      DocsisMinimumRequirements.validateComplete(
          baosTlvFinalizeArray.toByteArray(),
          DocsisVersion.fromConfigurationFileType(iDocsisVersion));
      return;
    }

    // Get TLV Byte Array then append NEW CM + CMTS MIC
    byte[] bTlvWithCmCmtsMic = setCmCmtsMIC(toByteArray());

    // Append EOF + PADD
    try {
      bTlvFinalizeArray = paddTlvBuffer(appendEOF(bTlvWithCmCmtsMic));
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      if (iDocsisVersion <= ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE) {
        DocsisMinimumRequirements.validateComplete(
            bTlvFinalizeArray, DocsisVersion.fromConfigurationFileType(iDocsisVersion));
      }
      this.baosTlvFinalizeArray.write(bTlvFinalizeArray);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks the current provisioned DOCSIS parameters without generating MICs or changing state.
   * This is a minimum-presence/framing check, not full conformance or MIC authentication.
   *
   * <p>Example: a DOCSIS 3.1 draft with only NetworkAccess returns false; after adding nonempty
   * upstream and downstream service flows it returns true.
   *
   * @param boolReportStdOut true to log validation failure at debug level; false or null to stay
   *     silent
   * @return true when current parameters meet the selected DOCSIS minimum; false for incomplete,
   *     malformed or non-DOCSIS input
   */
  public boolean checkSpecification(Boolean boolReportStdOut) {
    try {
      DocsisMinimumRequirements.validateParameters(
          HexString.toByteArray(super.toString()),
          DocsisVersion.fromConfigurationFileType(iDocsisVersion));
      return true;
    } catch (IllegalArgumentException failure) {
      if (Boolean.TRUE.equals(boolReportStdOut)) logger.debug(failure.getMessage());
      return false;
    }
  }

  /**
   * @param sSharedSecretKey
   */
  public void setSharedSecretKey(String sSharedSecretKey) {
    this.sSharedSecretKey = sSharedSecretKey;
  }

  /**
   * @param bTlvArray
   * @return the TLV byte array with the CM/CMTS MICs
   */
  private byte[] setCmCmtsMIC(byte[] bTlvArray) {

    byte[] bTlvArrayNoCmCmtsMIC = null;

    if (logger.isDebugEnabled()) {
      logger.debug("DocsisCompiler.setCmCmtsMIC() LengthOfConfig-Before: " + bTlvArray.length);
    }

    // Strip TLV6 + TLV7 from TLV Buffer
    try {
      bTlvArrayNoCmCmtsMIC =
          TlvBuilder.stripTlv(Constants.CMTS_MIC, TlvBuilder.stripTlv(Constants.CM_MIC, bTlvArray));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    if (logger.isDebugEnabled()) {
      logger.debug(
          "DocsisCompiler.setCmCmtsMIC() LengthOfConfig-AfterCmCmtsStrip: "
              + bTlvArrayNoCmCmtsMIC.length);
    }

    // Strip EOF TLV Buffer
    bTlvArrayNoCmCmtsMIC = stripEOFPADD(bTlvArrayNoCmCmtsMIC);

    if (logger.isDebugEnabled()) {
      logger.debug(
          "DocsisCompiler.setCmCmtsMIC() LengthOfConfig-AfterEOF-PADD: "
              + bTlvArrayNoCmCmtsMIC.length);
    }

    // Create TlvBuilder to add CM + CMTS MIC Hash
    TlvBuilder tbTlvArrayCmCmtsMIC = new TlvBuilder();

    // Add bTlvArrayNoCmCmtsMIC to TlvBuilder
    try {
      tbTlvArrayCmCmtsMIC.add(new HexString(bTlvArrayNoCmCmtsMIC));
    } catch (TlvException e1) {
      e1.printStackTrace();
    }

    // Calculate CM MIC + Append CM-MIC to TLV Buffer
    try {
      tbTlvArrayCmCmtsMIC.add(Constants.CM_MIC, getCmMIC(tbTlvArrayCmCmtsMIC.toByteArray()));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    if (logger.isDebugEnabled()) {
      logger.debug(
          "DocsisCompiler.setCmCmtsMIC() LengthOfConfig-After-Adding-CM-MIC: "
              + tbTlvArrayCmCmtsMIC.length());
      logger.debug("DocsisCompiler.setCmCmtsMIC() WITH-CM-MIC: " + tbTlvArrayCmCmtsMIC);
    }

    // Calculate CMTS MIC + Append CMTS MIC to TLV Buffer
    try {
      tbTlvArrayCmCmtsMIC.add(
          Constants.CMTS_MIC, getCmtsMIC(sSharedSecretKey, tbTlvArrayCmCmtsMIC.toByteArray()));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    if (logger.isDebugEnabled()) {
      logger.debug(
          "DocsisCompiler.setCmCmtsMIC() LengthOfConfig-After-2: " + tbTlvArrayCmCmtsMIC.length());
      logger.debug("DocsisCompiler.setCmCmtsMIC() CMTS-CM-MIC: " + tbTlvArrayCmCmtsMIC);
    }

    return tbTlvArrayCmCmtsMIC.toByteArray();
  }

  /**
   * @param bTlvArray
   * @return the CM MIC HASH of the TLV Byte Array * @see D.1.3.1 CM MIC Calculation The CM message
   *     integrity check configuration setting MUST be calculated by performing an MD5 digest over
   *     the bytes of the configuration setting fields. It is calculated over the bytes of these
   *     settings as they appear in the TFTPed image, without regard to TLV ordering or contents.
   *     There are two TLVs which are not included in the CM MIC calculation: The bytes of the CM
   *     MIC TLV itself are omitted from the calculation. This includes the type, length, and value
   *     fields; The bytes of the CMTS MIC TLV are omitted from the calculation. This includes the
   *     type, length, and value fields. These TLVs are the last TLVs in the CM configuration file.
   */
  public byte[] getCmMIC(byte[] bTlvArray) {

    MessageDigest mdMD5 = null;

    try {
      mdMD5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    if (logger.isDebugEnabled()) {
      logger.debug(
          "DocsisCompiler.setCmCmtsMIC() CM-MIC-HASH: "
              + new HexString(mdMD5.digest(bTlvArray)).toString());
    }

    return mdMD5.digest(bTlvArray);
  }

  /**
   * @param sCmtsSharedSecret
   * @param bTlvArray
   * @return the CMTS MIC HASH of the TLV Byte Array
   */
  private byte[] getCmtsMIC(String sCmtsSharedSecret, byte[] bTlvArray) {

    byte[] bCmtsMicTlv = fetchTlv(Constants.DOCSIS_CMTS_MIC_TLV_LIST, bTlvArray);

    byte[] bHmacMD5 = null;

    SecretKeySpec keySpec = new SecretKeySpec(sCmtsSharedSecret.getBytes(), "HmacMD5");

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

    if (logger.isDebugEnabled()) {
      logger.debug("DocsisCompiler.getCmtsMIC() - SharedSecret: " + sCmtsSharedSecret);
      logger.debug(
          "DocsisCompiler.getCmtsMIC() - CMTS-MIC-HASH: " + new HexString(bHmacMD5).toString());
    }

    return bHmacMD5;
  }

  /**
   * @param bTlvArray
   * @return Adds 0xFF 0x00 at End Of File * @throws IOException
   */
  private byte[] appendEOF(byte[] bTlvArray) throws IOException {

    ByteArrayOutputStream baosbTlvArray = new ByteArrayOutputStream();

    baosbTlvArray.write(bTlvArray);
    baosbTlvArray.write(EOF);
    baosbTlvArray.write(NULL);

    return baosbTlvArray.toByteArray();
  }

  /**
   * @param bTlvArray
   * @return returns a byte array padded with nulls if needed
   */
  private byte[] paddTlvBuffer(byte[] bTlvArray) {

    ByteArrayOutputStream baosbTlvArray = new ByteArrayOutputStream();

    try {
      baosbTlvArray.write(bTlvArray);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Make sure TLV Buffer is divisible by 4
    while ((baosbTlvArray.size() % 4) != 0) {

      if (logger.isDebugEnabled()) logger.debug("DocsisCompiler.paddTlvBuffer() - Adding Padding");

      baosbTlvArray.write(NULL);
    }

    return baosbTlvArray.toByteArray();
  }

  /**
   * @param bTlvArray
   * @return byte[]
   */
  private byte[] stripEOFPADD(byte[] bTlvArray) {

    ByteArrayOutputStream baosStripEOFWithPadd = new ByteArrayOutputStream();

    int iIndex = 0;

    // Locate EOF Index
    try {
      iIndex = TlvBuilder.findTLVIndex(bTlvArray, BinaryConversion.byteToUnsignedInteger(EOF));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    baosStripEOFWithPadd.write(bTlvArray, 0, iIndex);

    // logger.debug("B-HEX: " + new HexString(bTlvArray).toString(":"));
    // logger.debug("A-HEX: " + new HexString(baosStripEOFWithPadd.toByteArray()).toString(":"));

    return baosStripEOFWithPadd.toByteArray();
  }
}
