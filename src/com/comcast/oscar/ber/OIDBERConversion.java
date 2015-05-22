package com.comcast.oscar.ber;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;

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

public class OIDBERConversion {
	
	private boolean debug = Boolean.FALSE;
	
	private Map<String,String> mssOidDataTypeValue;

	public final String OID = "OID";
	public final String DATA_TYPE = "DATA_TYPE";
	public final String VALUE = "VALUE";

	/**
	 * 
	 * @param mssOidDataTypeValue
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OIDBERConversion(Map mssOidDataTypeValue) {
		this.mssOidDataTypeValue = mssOidDataTypeValue;
	}

	/**
	 * 
	 * @return String
	 */
	@Override
	public String toString() {

		//Add BER Result
		try {
			mssOidDataTypeValue.put("BER", new HexString(getBER()).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 	mssOidDataTypeValue.toString();
	}

	/**
	 * 
	
	 * @return byte[]
	 */
	public byte[] getOIDBER () {

		ByteArrayOutputStream boasOID;

		boasOID = (ByteArrayOutputStream) BERService.encodeOID(mssOidDataTypeValue.get(OID));

		return boasOID.toByteArray();

	}

	/**
	 * 
	
	
	 * @return byte[]
	 * @throws Exception */
	public byte[] getBER() throws Exception {
		
		boolean localDebug = Boolean.FALSE;
				
		int iDataType = Integer.decode(mssOidDataTypeValue.get(DATA_TYPE));

		if (localDebug|debug) {
			System.out.println("OIDBERConversion.getBER() DT: " + iDataType);
			System.out.println("OIDBERConversion.getBER() VA: " + mssOidDataTypeValue.get(VALUE));	
		}
		
		if (BERService.isNumberDataType(iDataType)) {
			
			if (localDebug|debug) 
				System.out.println("OIDBERConversion.getBER() DT-INTEGER: " + iDataType);
			
			return BERService.setOIDEncodingToByteArray (	mssOidDataTypeValue.get(OID),
															(byte)iDataType,
															Long.decode(mssOidDataTypeValue.get(VALUE)));
			
		} else if (BERService.isStringDataType(iDataType)){
			
			if (localDebug|debug) 
				System.out.println("OIDBERConversion.getBER() DT-STRING: " + iDataType);
			
			return BERService.setOIDEncodingToByteArray (	mssOidDataTypeValue.get(OID),
															(byte)iDataType,
															mssOidDataTypeValue.get(VALUE));
		
		} else if (iDataType == BinaryConversion.byteToUnsignedInteger(BERService.HEX)) {
			
			if (localDebug|debug) {
				System.out.println("OIDBERConversion.getBER() DT-HEX: " + iDataType);
				System.out.println("OIDBERConversion.getBER() HEX:    " + mssOidDataTypeValue.get(VALUE));
			}
			
			return BERService.setOIDEncodingToByteArray (	mssOidDataTypeValue.get(OID),
															(byte)iDataType,
															mssOidDataTypeValue.get(VALUE));			
		} else {
			return null;
		}

	}
	
}
