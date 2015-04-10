package com.comcast.oscar.cli.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
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

public class TLVtoJSON {
	
	public static final String ERROR = "Incorrect TLV format.";
	
	private final String[] args;
	
	/**
	 * Get TLV to JSON arguments
	 * @param args
	 */
	public TLVtoJSON(String[] args) 
	{
		this.args = args;
	}

	/**
	 * Set option parameters for command TLV to JSON display
	 * @return Option
	 */
	public static final Option OptionParameters() 
	{
		OptionBuilder.withArgName("TLV");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.withLongOpt("tlv2json");
		OptionBuilder.withDescription("View the JSON array of a TLV EX: 030101 (NetworkAccess: Type 3 Length 1 Value 1).");
    	return OptionBuilder.create("t2j");
	}
	
	/**
	 * Print JSON information from TLV
	 * @param tlvDisassemble
	 */
	public void printJSON(String tlvDisassemble) 
	{
		TlvBuilder tb = new TlvBuilder();	
		
		try 
		{
			tb.add(new HexString(HexString.toByteArray(this.args[0])));
		} 
		catch (TlvException e) 
		{
			e.printStackTrace();
		}
		
		TlvDisassemble td = new TlvDisassemble(tb, tlvDisassemble);
		System.out.println(td.getTlvDictionary());
	}
}
