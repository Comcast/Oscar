package com.comcast.oscar.examples;

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
	
	@author Maurice Garcia (mgarcia01752@outlook.com)

*/


/**
 */
public class StringArgTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		String[] sArg = new String[5];
		
		sArg[2] = "-OID";
		sArg[2] = "[1.3.5.6.3.4][1234546][OctecString]";
		
		
		for (int index = 0 ; index < sArg.length ; index++) {
			System.out.print(sArg[index] + " ");
		}

		CharSequence csLB = new String();
		csLB = "]";
		
		for (int index = 0 ; index < sArg.length ; index++) {
			if ((sArg[index] != null) && (sArg[index].contains(csLB))) {
				sArg[index].replaceAll("(][)", "&").replaceAll("]|[", " ");
			}
		}		
	
		for (int index = 0 ; index < sArg.length ; index++) {
			System.out.print(sArg[index] + " ");
		}
		
	}

}
