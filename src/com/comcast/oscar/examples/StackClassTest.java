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
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/


import java.util.Stack;


/**
 */
public class StackClassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Stack<String> sksStackTest = new Stack<String>();
		
		sksStackTest.push("Test-1");
		sksStackTest.push("Test-2");
		sksStackTest.push("Test-3");
		sksStackTest.push("Test-4");
		
		System.out.println("Stack-Info: " + sksStackTest);
		
		Object[] oStackTest = sksStackTest.toArray();
		
		System.out.println("String[]-Info: " + oStackTest[0] + " -> " + oStackTest.length);
	}

}
