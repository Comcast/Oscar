package com.comcast.oscar.cli.commands;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

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

public class SNMP4JLicense {

	private final String[] args;
	private File fLicense;
 
	/**
	 * Get SNMP4J License arguments
	 * @param args
	 */
	public SNMP4JLicense(String[] args) 
	{
		this.args = args;
	}
	
	/**
	 * Set option parameters for command SNMP4J License
	 * @return Option
	 */
	public static final Option OptionParameters() 
	{
		OptionBuilder.withArgName("filename");
		OptionBuilder.hasArgs(1);
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
		OptionBuilder.withLongOpt("snmp4j");
        OptionBuilder.withDescription("Use this license for SNMP OID Name resolution.");
    	return OptionBuilder.create("s4j");
	}
	
	/**
	 * Check if argument is valid, if any
	 * @return boolean
	 */
	public boolean hasLicense() 
	{
		if (new File(this.args[0]).exists()) 
		{
			this.fLicense = new File(this.args[0]);
			return true;
		} 
		else if (new File(DirectoryStructure.sActivePath() + File.separator + this.args[0]).exists()) 
		{
			this.fLicense = new File(DirectoryStructure.sActivePath() + File.separator + this.args[0]);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Return License file
	 * @return File
	 */
	public File getLicense() 
	{
		return this.fLicense;
	}
}
