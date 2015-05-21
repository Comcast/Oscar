package com.comcast.oscar.cli.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;

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

public class TLVDescription {
	
	public static final String ERROR = "Missing parameter. Please specify the TLV (EX: 26.1)";

	private final String[] args;
	
	/**
	 * Get TLV Description arguments
	 * @param args
	 */
	public TLVDescription(String[] args) {
		this.args = args;
	}
	
	/**
	 * Set option parameters for command TLV description
	 * @return Option
	 */
	public static final Option OptionParameters() {
		OptionBuilder.withArgName("TLV dot notation");
		OptionBuilder.hasArgs(1);
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("tlvdescription");
        OptionBuilder.withDescription("Display the TLV description (EX: 26.1).");
		return OptionBuilder.create("td");
	}
	
	/**
	 * Print TLV description
	 * @param configurationFileType
	 */
	public void printTLVDescription(int configurationFileType) {
		ConfigurationFileExport cfe = new ConfigurationFileExport(configurationFileType);
		System.out.println(cfe.getTlvDefintion(this.args[0]));
	}
}
