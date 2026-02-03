package com.comcast.oscar.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

public class BinaryConversion {

	public static final Integer BITS_PER_BYTE 	= 8;
	public static final Integer BIT_TRUE 		= 1;
	public static final Integer BIT_FALSE 		= 0;
	public static final Integer BASE_2			= 2;
	
	/**
	 * 
	 * @param bByte
	
	 * @return integer */
	public static int byteToUnsignedInteger (byte bByte) {
		return (bByte & 0x000000FF);
	}
	
	/**
	 * 
	 * @param sBitMask - (00000000)(11111111)...(10101010)..N
	
	
	 * @return byte array of BitString * @throws IllegalArgumentException */
	public static byte[] binaryMSBMaskToByteArray(String sBitMask) throws IllegalArgumentException {
		
		//Clean up any separation of bit array
		sBitMask = sBitMask.replaceAll("\\(|\\)", "");
		
		//Convert to string builder
	    StringBuilder sbBitMask = new StringBuilder(sBitMask);
	    
	    //Make sure that bits are in groups of 8 bits per byte
	    if ((sbBitMask.length()%8) != 0) {
	    	throw new IllegalArgumentException("BinaryConversion.binaryMSBMaskToByteArray() Binary String not Diviable by 8");
	    }
	    
	    ByteArrayOutputStream  baosBitMask = new ByteArrayOutputStream();
	    
	    for (int iEightBitGroup = 0 ; iEightBitGroup < sbBitMask.length() ; iEightBitGroup += BITS_PER_BYTE) {
	    	
	    	HexString hsEightBitGroup = new HexString();
	    	
	    	hsEightBitGroup.add(Integer.parseInt(sbBitMask.substring(iEightBitGroup, (iEightBitGroup + BITS_PER_BYTE)), BASE_2));
	    	
	    	try {
				baosBitMask.write(hsEightBitGroup.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

	    return baosBitMask.toByteArray();
	}
	
	/**
	 * 
	 * @param iStartPostion
	 * @param iLength
	 * @param bByteArray
	
	 * @return int
	 */
	public static int getUnsignedIntegerFromByteArrayRange(int iStartPostion , int iLength , byte[] bByteArray) {

		ByteArrayOutputStream  baosByteArray = new ByteArrayOutputStream();
		
		baosByteArray.write(bByteArray, iStartPostion, iLength);
		
		return byteArrayToInteger(baosByteArray.toByteArray());
		
	}
	
	/**
	 * 
	 * @param bByteArray
	
	 * @return int
	 */
	public static int byteArrayToInteger (byte[] bByteArray) {
		
		ByteArrayOutputStream  baosByteToInt = new ByteArrayOutputStream();
		
		while ((baosByteToInt.size() + bByteArray.length) < 4) {
			baosByteToInt.write(0x00);
		}

		try {
			baosByteToInt.write(bByteArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		byte[] bByteToInt = baosByteToInt.toByteArray();
		
		return 	bByteToInt[0] << 24 | 
				(bByteToInt[1] & 0xFF) << 16 | 
				(bByteToInt[2] & 0xFF) << 8 | 
				(bByteToInt[3] & 0xFF);		
	}
	
}
