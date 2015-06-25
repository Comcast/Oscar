package com.comcast.oscar.cli.commands;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
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
		
	private final String[] args;
	private boolean boolTopLevel = false;
	
	/**
	 * Get HexDisplay arguments
	 * @param args
	 */
	public HexDisplay(String[] args) {
		this.args = args;
		setDisplay();
	}
	
	/**
	 * Set option parameters for command Hexidecimal display
	 * @return Option
	 */
	public static final Option OptionParameters() {
		OptionBuilder.withArgName("t{oplevel}");
		OptionBuilder.hasArgs(1);
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("hex");
        OptionBuilder.withDescription("Display the hex of the input file. Option t creates a newline at the start of every top level TLV (binary files only).");
		return OptionBuilder.create("x");
	}
	/**
	 * Determine display preference (dump or toplevel)
	 * @return
	 */
	public void setDisplay() {
		if(this.args != null) {
			for (String string : this.args) {
				if (string.equalsIgnoreCase("t") || string.equalsIgnoreCase("toplevel")) {
					boolTopLevel = true;
				}
			}
		}
	}
	
	/**
	 * Print Hexadecimal display from binary file
	 * @param file
	 */
	public void printHexDisplayFromBinary(File file) {
		if (boolTopLevel) {
			System.out.println(TlvBuilder.tlvDump(HexString.fileToByteArray(file)));
		}
		else {
			System.out.println(HexDump.dumpHexString(HexString.fileToByteArray(file)));
		}
	}

	/**
	 * Print Hexidecimal display from text file
	 * @param file
	 * @param configurationFileType
	 */
	public void printHexDisplayFromText(File file, int configurationFileType) {
		ConfigurationFileImport cfi = null;
		try {
			cfi = new ConfigurationFileImport(file);
		} 
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} 
		catch (ConfigurationFileException e1) {
			e1.printStackTrace();
		}
		
		TlvBuilder tb = new TlvBuilder();
		try {
			tb.add(new HexString(cfi.toByteArray()));
		} 
		catch (TlvException e) {
			e.printStackTrace();
		}
		
		ConfigurationFile cf = new ConfigurationFile(configurationFileType, tb);	
		cf.commit(); 
		System.out.println(HexDump.dumpHexString(cf.toByteArray()));
	}
}
