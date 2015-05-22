package com.comcast.oscar.cli.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class Decompile {

	private final String[] args;
		
	/**
	 * Get Decompile Insert arguments
	 * @param args
	 */
	public Decompile(String[] args) {
		this.args = args;
		checkVerbose();
		checkDotted();
	}
	
	/**
	 * Set option parameters for command Decompile
	 * @return Option
	 */
	public static final Option OptionParameters() {
		OptionBuilder.withArgName("v{erbose}> <d{otted}> <s{uppress}");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("decompile");
        OptionBuilder.withDescription("Decompile binary to text. Option v for full TLV display. Option d for dotted notation. Option s for TLV comment supression.");
		return OptionBuilder.create("d");	
	}
	
	public boolean checkVerbose() {
		if (this.args != null) {
	    	for (String string : this.args) {
	        	if (string.equalsIgnoreCase("v") || string.equalsIgnoreCase("verbose")) {
	        		return true;
	        	}
	    	}
		}
		
		return false;
	}
	
	public boolean checkDotted() {
		if (this.args != null) {
	    	for (String string : this.args) {
	        	if (string.equalsIgnoreCase("d") || string.equalsIgnoreCase("dotted")) {
	        		return true;
	        	}
	    	}
		}
		
		return false;
	}
	
	public boolean checkSuppressed() {
		if (this.args != null) {
	    	for (String string : this.args) {
	        	if (string.equalsIgnoreCase("s") || string.equalsIgnoreCase("suppress")) {
	        		return true;
	        	}
	    	}
		}
		
		return false;
	}
}
