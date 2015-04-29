package com.comcast.oscar.compiler.dpoe;

import com.comcast.oscar.compiler.docsiscompiler.DocsisCompiler;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;

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
	
	
}
