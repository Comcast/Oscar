package com.comcast.oscar.cli.commands;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.utilities.HexString;

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

public class Key {

	private String sKey = " ";
	
	/**
	 * Set option parameters for command Key
	 * @return Option
	 */
	public static Option OptionParameters() {
		OptionBuilder.withArgName("key filename");
		OptionBuilder.hasArgs(1);
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("key");
        OptionBuilder.withDescription("Use this sharedsecret to compile the file - DOCSIS ONLY.");
		return OptionBuilder.create("k");
	}
	
	/**
	 * Set Key string from file
	 * @param args
	 */
	public void setKey(String[] args) {
		if (new File(args[0]).exists()) {
			this.sKey = new HexString(HexString.fileToByteArray(new File(args[0]))).toASCII();
		}
	}
	
	/**
	 * Return Key
	 * @return String
	 */
	public String getKey() {
		return this.sKey;
	}
}
