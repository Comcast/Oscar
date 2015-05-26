package com.comcast.oscar.ber;

import java.util.TreeMap;

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

public class OIDCollectionDuplicationValidation extends TreeMap<String,String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6171417894956345993L;

	/**
	 * 
	 */
	public OIDCollectionDuplicationValidation() {
		super();
	}
	
	/**
	 * 
	
	 * @param sObjectID	
	 * @param bocBEROIDConversion BEROIDConversion
	 * @return if true, no collision or duplication of OID, False indicates Collision or duplication of OID */
	public boolean add(BEROIDConversion bocBEROIDConversion ,String sObjectID) {
				
		boolean boolStatus = true;
		
		if (super.containsKey(bocBEROIDConversion.getOidDotNotaion())) {
			boolStatus = false;
		} else {
			super.put(bocBEROIDConversion.getOidDotNotaion(), sObjectID);
		}
		
		return boolStatus;		
	}
	
	/**
	 * This method does nothing
	 * @param bocBEROIDConversion BEROIDConversion
	 * @param sObjectID String
	 * @return sObjectID */
	public String put(BEROIDConversion bocBEROIDConversion ,String sObjectID) {
		return sObjectID;
	}
		
}
