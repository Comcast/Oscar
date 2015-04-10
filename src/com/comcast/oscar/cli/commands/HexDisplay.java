package com.comcast.oscar.cli.commands;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.configurationfile.ConfigrationFileException;
import com.comcast.oscar.configurationfile.ConfigrationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexDump;
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

public class HexDisplay {
	
	/**
	 * Set option parameters for command Hexidecimal display
	 * @return Option
	 */
	public static final Option OptionParameters() 
	{
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.withLongOpt("hex");
    	OptionBuilder.withDescription("Display the hex of the input file.");
    	return OptionBuilder.create("x");
	}
	
	/**
	 * Print Hexidecimal display from binary file
	 * @param file
	 */
	public void printHexDisplayFromBinary(File file) 
	{
		System.out.println(HexDump.dumpHexString(HexString.fileToByteArray(file)));
	}

	/**
	 * Print Hexidecimal display from text file
	 * @param file
	 * @param configurationFileType
	 */
	public void printHexDisplayFromText(File file, int configurationFileType) 
	{
		ConfigrationFileImport cfi = null;
		try 
		{
			cfi = new ConfigrationFileImport(file);
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch (ConfigrationFileException e1) 
		{
			e1.printStackTrace();
		}
		
		TlvBuilder tb = new TlvBuilder();
		try 
		{
			tb.add(new HexString(cfi.toByteArray()));
		} 
		catch (TlvException e) 
		{
			e.printStackTrace();
		}
		
		ConfigurationFile cf = new ConfigurationFile(configurationFileType, tb);	
		cf.commit(); 
		System.out.println(HexDump.dumpHexString(cf.toByteArray()));
	}
}
