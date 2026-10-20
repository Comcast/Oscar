/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;

public class DPoECompiler extends DocsisCompiler {
  private int iDPoEVersion = ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE;

  /**
   * @param iDPoEVersion
   */
  public DPoECompiler(int iDPoEVersion) {
    super(iDPoEVersion);
    this.iDPoEVersion = iDPoEVersion;
  }

  /**
   * @param sSharedSecretKey
   * @param iDPoEVersion
   */
  public DPoECompiler(String sSharedSecretKey, int iDPoEVersion) {
    super(sSharedSecretKey, iDPoEVersion);
    this.iDPoEVersion = iDPoEVersion;
  }

  /**
   * @return OSCAR Internal Version tracking
   */
  public int getDPoEVersion() {
    return this.iDPoEVersion;
  }

  /** This method removes TopLevel TLVs that are not defined the Dictionary */
  public void removeNonDictionaryTopLevelTLV() {

    try {
      baTlvBuffer = TlvBuilder.stripTlv(17, TlvBuilder.stripTlv(Constants.CM_MIC, baTlvBuffer));
    } catch (TlvException e) {
      e.printStackTrace();
    }
  }
}
