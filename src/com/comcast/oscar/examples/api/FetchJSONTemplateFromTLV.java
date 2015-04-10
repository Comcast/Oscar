package com.comcast.oscar.examples.api;

import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;

/**
 */
public class FetchJSONTemplateFromTLV {

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
	public static void main(String[] args) {

		
		/*NetworkAccess - ENABLE*/
		TlvBuilder tb = new TlvBuilder();	
		
		try {
			tb.add(3, 1);
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		TlvDisassemble td = new TlvDisassemble(tb, TlvDisassemble.TLV_TYPE_DOCSIS);
		
		System.out.println(td.getTlvDictionary());
		
	}

}
