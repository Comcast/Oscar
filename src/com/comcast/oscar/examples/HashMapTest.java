package com.comcast.oscar.examples;
import java.util.HashMap;
import java.util.Map;

/*
	Copyright 2015 Comcast Cable Communications Management, LLC
	___________________________________________________________________
	Licensed under the Apache License, Version 2.0 (the "License")
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/

/**
 */
public class HashMapTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		HashMap<Integer, Integer> map1 = new HashMap<Integer, Integer>();
		map1.put(1, 1);
		map1.put(2, 1);
		map1.put(4, 1);
		HashMap<Integer, Integer> map3 = new HashMap<Integer, Integer>();
		map3.put(1, 3);
		map3.put(2, 3);
		map3.put(3, 1);
		HashMap<Integer, Integer> map2 = new HashMap<Integer, Integer>();
		map2.put(1, 2);
		map2.put(2, 2);		
		HashMap<Integer, Integer> map4 = new HashMap<Integer, Integer>();
		map4.putAll(map1);
		map4.putAll(map2);
		map4.putAll(map3);
		
		Map<Integer, Integer> miiMap;
		miiMap = map4;
		System.out.println("HM-MAP4: " + miiMap);
		
		
	}

}
