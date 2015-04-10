package com.comcast.oscar.interpreter;

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


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.comcast.oscar.cli.CommandRun;
import com.comcast.oscar.constants.Constants;

/**
 */
public class Interpreter {

	public String sPrompt = "?> ";
	
	private CommandRun cmds;
	
	Scanner scanIn;
	
	private List<String> lsUserHistory = new ArrayList<String>();

	/**
	 * 
	 */
	public Interpreter() {		
		cmds = new CommandRun();
	}
	
	/**
	 * 
	 */
	public void start() {
		
		scanIn = new Scanner(System.in);
				
		System.out.println("\n\n" + Constants.APACHE_20_LICENCE_DISCLAIMER + "\n\n");
		
		while(true) {
			
			System.out.print(sPrompt);
			
			String sUserInput = scanIn.nextLine();

			lsUserHistory.add(sUserInput);
						
			String[] saUserInput = sUserInput.split("\\s+");
						
			cmds.run(saUserInput);
			
		}
		
	}
	
	/**
	 * 
	 */
	public void printUserHistory() {
		for (String sUserHistory : lsUserHistory)
			System.out.println(sUserHistory);
	}


	
}
