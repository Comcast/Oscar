package com.comcast.oscar.cli.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.netsnmp.NetSNMP;

public class Translate {
	
	private final String[] args;
	
	/**
	 * Get Key arguments
	 * @param args
	 */
	public Translate(String[] args) {
		this.args = args;
	}
	
	/**
	 * Set option parameters for command Key
	 * @return Option
	 */
	public static Option OptionParameters() {
		OptionBuilder.withArgName("OID");
		OptionBuilder.hasArgs(1);
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("translate");
        OptionBuilder.withDescription("Get the description of an OID. EX: -tr docsDev OR -tr 1.3.6.1.2.1.69.");
		return OptionBuilder.create("tr");
	}
	
	/**
	 * Print description
	 * @return String
	 */
	public void translate() {
		System.out.println(NetSNMP.getDescription(this.args[0]));
	}
}
