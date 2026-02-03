package com.comcast.oscar.compiler;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;

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


public class DPoECompiler extends DocsisCompiler {	
	private int iDPoEVersion = ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE;
	
	/**
	 * 
	 * @param iDPoEVersion
	 */
	public DPoECompiler(int iDPoEVersion) {		
		super(iDPoEVersion);
		this.iDPoEVersion = iDPoEVersion;
	}
	
	/**
	 * 
	 * @param sSharedSecretKey
	 * @param iDPoEVersion
	 */
	public DPoECompiler(String sSharedSecretKey , int iDPoEVersion) {		
		super(sSharedSecretKey , iDPoEVersion);
		this.iDPoEVersion = iDPoEVersion;
	}
	
	/**
	 * 
	 * @return OSCAR Internal Version tracking
	 */
	public int getDPoEVersion() {
		return this.iDPoEVersion;
	}
	
	/**
	 * This method removes TopLevel TLVs that are not defined the Dictionary
	 */
	public void removeNonDictionaryTopLevelTLV() {
		
		try {
			baTlvBuffer = TlvBuilder.stripTlv(17,
					TlvBuilder.stripTlv(Constants.CM_MIC, baTlvBuffer));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
	}

}
