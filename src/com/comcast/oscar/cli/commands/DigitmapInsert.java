package com.comcast.oscar.cli.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.compiler.PacketCableConstants;
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

public class DigitmapInsert {
	
	public static final String ERROR = "No digitmap file found.";
	
	private final String[] args;
	
	private Map<String, byte[]> map = new HashMap<String, byte[]>();
	private String sDefaultOID = PacketCableConstants.PKT_CABLE_DIGIT_MAP_OID;
	
	/**
	 * Get Digitmap Insert arguments
	 * @param args
	 */
	public DigitmapInsert(String[] args) {
		this.args = args;
	}
	
	/**
	 * Set option parameters for command Digitmap Insert
	 * @return Option
	 */
	public static final Option OptionParameters() {
		OptionBuilder.withArgName("[<filename>][<OID>]");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("digitmap");
        OptionBuilder.withDescription("Insert this DigitMap into a file when compiling - PacketCable ONLY. "
        		+ "Multiple DigitMaps can be inserted simultaneously (space delimited). "
        		+ "OID optional.");
    	return OptionBuilder.create("dm");
	}
	
	/**
	 * Check for Digitmaps. Insert into map if found.
	
	 * @return boolean
	 */
	public boolean hasDigitmap() {
		String sOID;
		File fDigitmap;
		boolean boolDigitmap = false;
		
		for (String string : this.args) {
			if (string.contains("|")) {
				String[] array = string.split("\\|");
				sOID = array[1];
				fDigitmap = new File(array[0]);
			} 
			else {
				sOID = this.sDefaultOID;
				fDigitmap = new File(string);
			}
			
			if (fDigitmap.exists()) {
				this.map.put(sOID, HexString.fileToByteArray(fDigitmap));
				boolDigitmap = true;
			}
		}
		
		return boolDigitmap;
	}
	
	/**
	 * Return Digitmap
	
	 * @return Map<String,byte[]>
	 */
	public Map<String, byte[]> getDigitmap() {
		return this.map;
	}
}


