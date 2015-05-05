package com.comcast.oscar.examples.api;

import com.comcast.oscar.configurationfile.CommonTlvInsertions;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;

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

public class EditDocsisTextFile {

	public static void main(String[] args) {

		/* Sample Configuration Does not contain TFTP SERVER ADDRESS*/
		String sDocsisConfiguration = "Docsis { NetworkAccess 1;	 /* TLV: [3]*/ BaselinePrivacyConfigSetting { AuthorizeWaitTimeout 10;	 /* TLV: [17.1]*/ ReauthorizeWaitTimeout 10;	 /* TLV: [17.2]*/ AuthorizationGraceTime 600;	 /* TLV: [17.3]*/ OperationalWaitTimeout 10;	 /* TLV: [17.4]*/ RekeyWaitTimeout 10;	 /* TLV: [17.5]*/ TEKGraceTime 600;	 /* TLV: [17.6]*/ AuthorizeRejectWaitTimeout 60;	 /* TLV: [17.7]*/ SAMapWaitTimeout 1;	 /* TLV: [17.8]*/ SAMapMaxRetries 4;	 /* TLV: [17.9]*/ } PrivacyEnable 1;	 /* TLV: [29]*/ SNMPv1v2cCoexistenceConfig { SNMPv1v2cCommunityName ReadOnly;	 /* TLV: [53.1]*/ SNMPv1v2cTransportAddressAccess { SNMPv1v2cTransportAddress 0.0.0.0(0);	 /* TLV: [53.2.1]*/ SNMPv1v2cTransportMask 0.0.0.0(0);	 /* TLV: [53.2.2]*/ } SNMPv1v2cAccessViewType 2;	 /* TLV: [53.3]*/ SNMPv1v2cAccessViewName docsisManagerView;	 /* TLV: [53.4]*/ } CmMic 96:1F:9E:C4:A4:CF:66:26:D6:DD:25:79:B2:90:5F:B1;	 /* TLV: [6]*/ CmtsMic B0:34:97:66:6A:A5:7E:93:89:E6:93:CC:3B:22:A8:09;	 /* TLV: [7]*/ }";

		//Create a String Builder
		StringBuilder sbDocsisConfiguration = new StringBuilder(sDocsisConfiguration);
		
		//Import Configuration
		ConfigurationFileImport cfiDocsis = new ConfigurationFileImport(sbDocsisConfiguration);
		
		//Conver to a Configuration File Object
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30,cfiDocsis.getTlvBuilder());

		//Add TFTP Server Address
		try {
			CommonTlvInsertions.insertTftpServerAddress("10.10.10.10", cf, true);
		} catch (ConfigurationFileException e) {
			e.printStackTrace();
		}
		
		ConfigurationFileExport cfe = new ConfigurationFileExport(cf);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

	}



}
