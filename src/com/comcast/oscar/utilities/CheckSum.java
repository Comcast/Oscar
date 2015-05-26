package com.comcast.oscar.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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

public class CheckSum {

	/**
	 * Method getMD5.
	 * @param bMessage ByteArray of the Message
	 * @return byte[] MD5 Hash of the Message
	 */
	public static byte[] getMD5(byte[] bMessage) {
		
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return md.digest(bMessage);
	}
	
	/**
	 * 
	 * @param bTlvArray ByteArray of the Message
	 * @return SHA-1 Hash of the Message
	 */
	public static byte[] getSHA1(byte[] bTlvArray) {
		
		MessageDigest mdSHA1 = null;
		
	    try {
	    	mdSHA1 = MessageDigest.getInstance("SHA-1");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    
	    return mdSHA1.digest(bTlvArray);		
	}
	
}
