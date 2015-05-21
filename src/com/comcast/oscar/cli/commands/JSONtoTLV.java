package com.comcast.oscar.cli.commands;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.json.JSONArray;
import org.json.JSONException;

import com.comcast.oscar.tlv.TlvAssembler;
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

public class JSONtoTLV {
	
	public static final String ERROR = "JSON file does not exist";
	
	private final String[] args;
	
	/**
	 * Get JSON to TLV arguments
	 * @param args
	 */
	public JSONtoTLV(String[] args) {
		this.args = args;
	}

	/**
	 * Set option parameters for command JSON to TLV display
	 * @return Option
	 */
	public static final Option OptionParameters() {
		OptionBuilder.withArgName("filename");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.withLongOpt("json2tlv");
		OptionBuilder.withDescription("View the TLV number of a JSON array within the file.");
    	return OptionBuilder.create("j2t");
	}
	
	/**
	 * Check if file exists
	 * @return boolean
	 */
	public boolean fileExists() {
		File file = new File(this.args[0]);
		
		if(file.exists()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Print TLV information from JSON input file
	 */
	public void printTLV() {
		File file = new File(this.args[0]);
		byte[] ba = HexString.fileToByteArray(file);
		HexString hs = new HexString(ba);	
		TlvAssembler ta = null;	
		
		try {
			ta = new TlvAssembler(new JSONArray(hs.toASCII()));
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println(ta.toString());
	}
}
