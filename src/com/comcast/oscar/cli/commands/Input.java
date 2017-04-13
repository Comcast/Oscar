package com.comcast.oscar.cli.commands;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.utilities.DirectoryStructure;
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

public class Input {

	public static final String ERROR = "Input file not found. Please use -i <filename> to specify an input file.";
	
	private final String[] args;
	private File fInput;
	
	/**
	 * Get Input arguments
	 * @param args
	 */
	public Input(String[] args) {
		this.args = args;
	}
	
	/**
	 * Set option parameters for command Input
	 * @return Option
	 */
	public static final Option OptionParameters() {
		OptionBuilder.withArgName("filename");
		OptionBuilder.hasArgs(1);
		OptionBuilder.hasOptionalArgs();
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.withLongOpt("input");
		OptionBuilder.withDescription("File to analyze and/or compile/decompile.");
    	return OptionBuilder.create("i");
	}
	
	/**
	 * Check if argument is valid, if any
	 * @return boolean
	 */
	public boolean hasInput() {
		if (new File(this.args[0]).exists()) {
			this.fInput = new File(this.args[0]);
			return true;
		} 
		else if (new File(DirectoryStructure.sActivePath() + File.separator + this.args[0]).exists()) {
			this.fInput = new File(DirectoryStructure.sActivePath() + File.separator + this.args[0]);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Return Input file
	 * @return File
	 */
	public File getInput() {
		return this.fInput;
	}
	
	/**
	 * Check if file is binary
	 * @return boolean
	 */
	public boolean isBinary() {
		return !(HexString.verifyAsciiPlainText(HexString.fileToByteArray(this.fInput)));
	}

}
