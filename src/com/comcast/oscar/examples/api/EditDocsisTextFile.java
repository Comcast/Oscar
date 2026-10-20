/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples.api;

import com.comcast.oscar.configurationfile.CommonTlvInsertions;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;

public class EditDocsisTextFile {

  public static void main(String[] args) {

    /* Sample Configuration Does not contain TFTP SERVER ADDRESS*/
    String sDocsisConfiguration =
        "Docsis { NetworkAccess 1;	 /* TLV: [3]*/ BaselinePrivacyConfigSetting {"
            + " AuthorizeWaitTimeout 10;	 /* TLV: [17.1]*/ ReauthorizeWaitTimeout 10;	 /* TLV:"
            + " [17.2]*/ AuthorizationGraceTime 600;	 /* TLV: [17.3]*/ OperationalWaitTimeout 10;	"
            + " /* TLV: [17.4]*/ RekeyWaitTimeout 10;	 /* TLV: [17.5]*/ TEKGraceTime 600;	 /* TLV:"
            + " [17.6]*/ AuthorizeRejectWaitTimeout 60;	 /* TLV: [17.7]*/ SAMapWaitTimeout 1;	 /*"
            + " TLV: [17.8]*/ SAMapMaxRetries 4;	 /* TLV: [17.9]*/ } PrivacyEnable 1;	 /* TLV:"
            + " [29]*/ SNMPv1v2cCoexistenceConfig { SNMPv1v2cCommunityName private;	 /* TLV:"
            + " [53.1]*/ SNMPv1v2cTransportAddressAccess { SNMPv1v2cTransportAddress 0.0.0.0(0);	"
            + " /* TLV: [53.2.1]*/ SNMPv1v2cTransportMask 0.0.0.0(0);	 /* TLV: [53.2.2]*/ }"
            + " SNMPv1v2cAccessViewType 2;	 /* TLV: [53.3]*/ SNMPv1v2cAccessViewName"
            + " docsisManagerView;	 /* TLV: [53.4]*/ } CmMic"
            + " 96:1F:9E:C4:A4:CF:66:26:D6:DD:25:79:B2:90:5F:B1;	 /* TLV: [6]*/ CmtsMic"
            + " B0:34:97:66:6A:A5:7E:93:89:E6:93:CC:3B:22:A8:09;	 /* TLV: [7]*/ }";

    // Create a String Builder
    StringBuilder sbDocsisConfiguration = new StringBuilder(sDocsisConfiguration);

    // Import Configuration
    ConfigurationFileImport cfiDocsis = new ConfigurationFileImport(sbDocsisConfiguration);

    // Conver to a Configuration File Object
    ConfigurationFile cf =
        new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30, cfiDocsis.getTlvBuilder());

    // Add TFTP Server Address
    try {
      CommonTlvInsertions.insertTftpServerAddress("10.10.10.10", cf, true);
    } catch (ConfigurationFileException e) {
      e.printStackTrace();
    }

    ConfigurationFileExport cfe = new ConfigurationFileExport(cf);

    System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
  }
}
