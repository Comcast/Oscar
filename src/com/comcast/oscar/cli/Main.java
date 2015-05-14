package com.comcast.oscar.cli;

import com.comcast.oscar.constants.Constants;
import com.comcast.oscar.utilities.DirectoryStructure;

/**
 * 
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 * 
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

 */

public class Main {

	/**
	 * Main Class. Checks and creates necessary directories then run commands.
	 * @param args
	 */
	public static void main (String args[]) 
	{
		/* Determine path. For testing purposes. */
		DirectoryStructure.setFromJar(); 
		
		/* Create subdirectories if they do not exist */
		DirectoryStructure.createDirectories();
		
		/* Import files if they do not exist */
		DirectoryStructure ds = new DirectoryStructure();
		ds.exportFiles();
				
		/* Run commands */
		System.out.println(Constants.OSCAR_CLI_HEADER);
		
		if (args.length <= 0) 
		{
			System.out.println(Constants.OSCAR_CLI_NO_ARGS);
		}
		
		CommandRun cmds = new CommandRun();
		cmds.run(cleanUpARGS(args));
	}
	
	/**
	 * Clean up arguments for processing within the CLI API.
	 * @param args
	
	 * @return String[]
	 */
	private static String[] cleanUpARGS(String[] args) 
	{ 
		//Changes user input format (e.g. [<value>][<value>]) to an easier format for CLI (e.g. <value>|<value>).
        for (int index = 0 ; index < args.length; index++) 
        { 
        	if (args[index].contains("[")) 
        	{
        		args[index] = args[index].replaceAll("]\\[", "|").replaceAll("]|\\[", ""); 
        	}
        } 
        
        return args; 
	}
}
