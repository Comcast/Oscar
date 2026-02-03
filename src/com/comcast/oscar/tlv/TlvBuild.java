package com.comcast.oscar.tlv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public interface TlvBuild {
		
	/**
	 * Track all the added TLV Byte Arrays
	 */
	List<byte[]> bTlvBuilder = new ArrayList<byte[]>();
	
	/**
	 * Track the TopLevel TLV to its Respected Num Of Byte Length
	 */
	Map<Integer,Integer> miiTypeBytelength = new HashMap<Integer,Integer>();
	
	/**
	 * Method getTopLevelTlvList.
	 * @param miiTypeByteLength Map<Integer,Integer>
	 * @return List<Integer>
	 */
	public List<Integer> getTopLevelTlvList (Map<Integer,Integer> miiTypeByteLength);
	
	/**
	 * Method getTopLevelTlvList.
	 * @return List<Integer>
	 */
	public List<Integer> getTopLevelTlvList ();
	
	/**
	 * Method toByteArray.
	 * @return byte[]
	 */
	public byte[] toByteArray();
	
	/**
	 * Method add.
	 * @param iTlvType int
	 * @param bValue byte[]
	 * @throws TlvException
	 */
	public void add (int iTlvType , byte[] bValue) throws TlvException;
	
	/**
	 * Method isEmpty.
	 * @return Boolean
	 */
	public Boolean isEmpty();
	
	public void clear();
	
	/**
	 * Method getMapTypeToByteLength.
	 * @return Map<Integer,Integer>
	 */
	public Map<Integer,Integer> getMapTypeToByteLength();
	
	
}
