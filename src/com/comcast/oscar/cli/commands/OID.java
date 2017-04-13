package com.comcast.oscar.cli.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.ber.BERService;

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

public class OID {
	
	public static final String ERROR = "Missing parameter. Please specify all parameters in the following format [<OID>][<value>][<data type>]";
	
	private final String[] args;
	
	private Map<String, String[]> map = new HashMap<String, String[]>();
		
	/**
	 * Get OID arguments
	 * @param args
	 */
	public OID(String[] args) {
		this.args = args;
	}
	
	/**
	 * Set option parameters for command OID
	 * @return Option
	 */
	public static Option OptionParameters() {
		OptionBuilder.withArgName("[<OID>][<value>][<data type>]");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("OID");
        OptionBuilder.withDescription("Insert this OID into a file when compiling. "
        		+ "Multiple OIDs can be inserted simultaneously (space delimited). "
        		+ "Applicable datatypes: " + BERService.getDataTypeStringList());
    	return OptionBuilder.create("O");
	}
	
	/**
	 * Check for any OID arguments
	 * @return boolean
	 */
	public boolean hasOID() {
		boolean boolOID = false;

		for (String string : this.args) {
			if (string.contains("|")) {
				String[] array = string.split("\\|");
				
				if(array.length > 2) {
					String[] parameters = new String[10];
					parameters[0] = array[1];
					parameters[1] = array[2];
					this.map.put(array[0], parameters);
					boolOID = true;
				}
			}
		}
		
		return boolOID;
	}
	
	/**
	 * Return all OIDs
	 * @return Map<String,String[]>
	 */
	public Map<String, String[]> getMap() {
		return this.map;
	}
}
