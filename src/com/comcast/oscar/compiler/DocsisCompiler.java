/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.configurationfile.DocsisVersion;
import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DocsisCompiler extends TlvBuilder {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(DocsisCompiler.class);

  private String sSharedSecretKey = " ";

  private int iDocsisVersion;

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
    baosTlvFinalizeArray.reset();
    byte[] parameters = HexString.toByteArray(super.toString());
    boolean docsis = iDocsisVersion <= ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE;
    if (docsis) {
      DocsisMinimumRequirements.validateParameters(
          parameters, DocsisVersion.fromConfigurationFileType(iDocsisVersion));
    }
    var coverage =
        iDocsisVersion == ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE
                || iDocsisVersion == ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE
            ? DocsisMicRules.Coverage.DOCSIS_31_AND_40
            : DocsisMicRules.Coverage.LEGACY_COMPATIBILITY;
    String family = TlvRegistry.DOCSIS_FAMILY;
    if (iDocsisVersion == ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE) {
      family = TlvRegistry.DOCSIS_40_FAMILY;
    } else if (iDocsisVersion == ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE
        || iDocsisVersion == ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE) {
      family = TlvRegistry.DPOE_FAMILY;
    }
    byte[] finalized =
        DocsisMicFinalizer.finish(
            parameters, TlvRegistry.forFamily(family), sSharedSecretKey, coverage);
    if (docsis) {
      DocsisMinimumRequirements.validateComplete(
          finalized, DocsisVersion.fromConfigurationFileType(iDocsisVersion));
    }
    baosTlvFinalizeArray.writeBytes(finalized);
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
   * Computes the unkeyed 16-octet MD5 digest of exactly the supplied parameter bytes. This
   * compatibility helper does not strip MICs, End or padding: callers must supply only the intended
   * CM MIC input. It neither modifies the input nor changes compiler state.
   *
   * <p>For example, an empty input produces d41d8cd98f00b204e9800998ecf8427e.
   *
   * @param parameters non-null encoded parameters, excluding CM/CMTS MICs, End and padding
   * @return a new 16-octet digest
   * @throws NullPointerException if parameters is null
   * @throws IllegalStateException if the MD5 provider is unavailable
   */
  public byte[] getCmMIC(byte[] parameters) {
    return DocsisMicRules.md5(parameters);
  }
}
