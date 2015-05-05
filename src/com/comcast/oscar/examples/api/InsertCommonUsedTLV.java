package com.comcast.oscar.examples.api;

import com.comcast.oscar.configurationfile.CommonTlvInsertions;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

/**
 */
public class InsertCommonUsedTLV {

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
	public static void main(String[] args) throws ConfigurationFileException {

		/*DOCSIS Configuration*/
		String sDocsisConfigFileHex = "03:01:01:18:20:01:02:00:01:04:04:68:73:64:00:06:01:07:08:04:00:0B:B8:00:09:04:00:98:96:80:0E:02:17:C8:0F:01:02:19:13:01:02:00:02:06:01:07:08:04:00:86:47:00:09:04:01:31:2D:00:0B:18:30:16:06:08:2B:06:01:02:01:01:04:00:04:0A:54:65:73:74:53:74:72:69:6E:67:06:10:3F:CE:39:77:E9:F1:CE:08:D7:16:6E:5E:24:20:90:0E:07:10:32:88:B7:8F:05:A3:8D:D1:32:9C:6C:BD:49:4E:1D:5A:FF:00:00:00";
	
		/*Create TLV Builder*/
		TlvBuilder tbDCF = new TlvBuilder();
		
		/*Add Configuration to TLV Builder*/
		try {
			tbDCF.add(new HexString(HexString.toByteArray(sDocsisConfigFileHex)));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30,tbDCF);
		
		/*
		 * Add Downstream Frequency
		 *   
		 */
		CommonTlvInsertions.insertDownstreamFrequency(723000000, cf, true);
		CommonTlvInsertions.insertDownstreamFrequency(729000000, cf, true);
	
		ConfigurationFileExport cfe = new ConfigurationFileExport(cf);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		
	}
}
