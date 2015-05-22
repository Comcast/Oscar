package com.comcast.oscar.cli.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

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

public class TFTPServer {

	public static final String ERROR = "No address argument found. For IPv4 use this format for the argument: v4=<server address>. For IPv6 use this format for the argument: v6=<server address>.";
	
	private final String[] args;
	private String sIpv4Address;
	private String sIpv6Address;
	
	/**
	 * Get TFTP Server arguments
	 * @param args
	 */
	public TFTPServer(String[] args) {
		this.args = args;
	}
	
	/**
	 * Set option parameters for command TFTP Server Address
	 * @return Option
	 */
	public static final Option OptionParameters() {
		OptionBuilder.withArgName("v4/v6=<tftp address>");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
		OptionBuilder.withValueSeparator(' ');
    	OptionBuilder.withLongOpt("tftp");
    	OptionBuilder.withDescription("Add this TFTP server during file compilation. "
    			+ "For IPv4 use this format for the argument: v4=<server address>. "
    			+ "For IPv6 use this format for the argument: v6=<server address>. "
    			+ "Both address versions can be inserted simultaneously (space delimited).");
    	return OptionBuilder.create("T");
	}
	
	/**
	 * Checks for an IPv4 address
	 * @return boolean
	 */
	public boolean hasIpv4Address() {
		for (String string : this.args) {
			if (string.contains("=")) {
				String[] array = string.split("=");
				
				if (array[0].equalsIgnoreCase("v4") || array[0].equalsIgnoreCase("ipv4")) {
					this.sIpv4Address = array[1];
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Return IPv4 address
	 * @return String
	 */
	public String getIpv4Address() {
		return this.sIpv4Address;
	}
	
	/**
	 * Checks for IPv6 address
	 * @return boolean
	 */
	public boolean hasIpv6Address() {
		for (String string : this.args) {
			if (string.contains("=")) {
				String[] array = string.split("=");
				
				if (array[0].equalsIgnoreCase("v6") || array[0].equalsIgnoreCase("ipv6")) {
					this.sIpv6Address = array[1];
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Return IPv6 address
	 * @return String
	 */
	public String getIpv6Address() {
		return this.sIpv6Address;
	}
	
	/**
	 * Check if any address was given
	 * @return boolean
	 */
	public boolean hasAddress() {
		if (hasIpv4Address() || hasIpv6Address()) return true;
		return false;
	}
}
