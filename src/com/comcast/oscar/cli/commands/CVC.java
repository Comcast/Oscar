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

public class CVC {
	
	public static final String ERROR = "No cvc filename argument found. For CoSigner use this format for the argument: c=<filename>. For Manufacturer use this format for the argument: m=<filename>.";
	
	private final String[] args;
	private File fCoSigner;
	private File fManufacturer;
	
	/**
	 * Get CVC arguments
	 * @param args
	 */
	public CVC(String[] args) {
		this.args = args;
	}
	
	/**
	 * Set option parameters for command CVC
	 * @return Option
	 */
	public static final Option OptionParameters() {
		OptionBuilder.withArgName("c/m=<filename>");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.withLongOpt("certificate");
    	OptionBuilder.withDescription("Add this CVC during file compilation. "
    			+ "For CoSigner use this format for the argument: c=<filename>. "
    			+ "For Manufacturer use this format for the argument: m=<filename>. "
    			+ "Both CVCs can be inserted simultaneously (space delimited).");
    	return OptionBuilder.create("cvc");
	}
	
	/**
	 * Check for a CoSigner CVC. Insert into fCoSigner if found.
	 * @return boolean
	 */
	public boolean hasCoSigner() {
		for (String string : this.args) {
			if (string.contains("=")) {
				String[] array = string.split("=");
				if (array[0].equalsIgnoreCase("c") || array[0].equalsIgnoreCase("co")) {
					if (new File(DirectoryStructure.fCertificatesDir(), array[1]).exists()) {
						this.fCoSigner = new File(DirectoryStructure.fCertificatesDir(), array[1]);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Return CoSigner file
	
	 * @return File
	 */
	public File getCoSigner() {
		return this.fCoSigner;
	}
	
	/**
	 * Check for a Manufacturer CVC. Insert into fManufacturer if found.
	
	 * @return boolean
	 */
	public boolean hasManufacturer() {
		for (String string : this.args) {
			if (string.contains("=")) {
				String[] array = string.split("=");
				if (array[0].equalsIgnoreCase("m") || array[0].equalsIgnoreCase("man")) {
					if (new File(DirectoryStructure.fCertificatesDir(), array[1]).exists()) {
						System.out.println(new File(DirectoryStructure.fCertificatesDir(), array[1]));
						this.fManufacturer = new File(DirectoryStructure.fCertificatesDir(), array[1]);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Return Manufacturer file
	
	 * @return File
	 */
	public File getManufacturer() {
		return this.fManufacturer;
	}
	
	/**
	 * Check if a CVC was specified
	
	 * @return boolean
	 */
	public boolean hasCVC() {
		if(hasCoSigner() || hasManufacturer()) return true;
		return false;
	}
}
