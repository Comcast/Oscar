package com.comcast.oscar.cli.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.snmp4j.smi.SMIManagerServiceException;

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

public class MIBSCompile {

	private boolean boolSnmpSmiSpecCheck = true;
	private boolean boolMibCompileOutputVerbose = false;

	/**
	 * Set option parameters for command MIB Compile
	 * @return Option
	 */
	public static final Option OptionParameters() 
	{
    	OptionBuilder.withArgName("t{rue}|f{alse}> <v{erbose}");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("mibscompile");
        OptionBuilder.withDescription("Compile MIBS in the mibs directory. Set false to ignore check for SMI conformance (default true). Verbose optional.");
    	return OptionBuilder.create("M");
	}

	/**
	 * Sets the SNMP_SMI_SPEC_CHECK and MIB_COMPILE_OUTPUT_VERBOSE boolean passed on user arguments.
	 * @param args
	 */
	public void setValues(String[] args) 
	{
		if (args.length > 0) 
		{
			if (args[0].equals("true") || args[0].equals("t")) 
			{
				boolSnmpSmiSpecCheck = true;
			}
			else if (args[0].equals("false") || args[0].equals("f"))
			{
				boolSnmpSmiSpecCheck = false;
			}
		}
		
		if (args.length > 1) 
		{
			if (args[1].equals("verbose") || args[1].equals("v")) 
			{
				boolMibCompileOutputVerbose = true;
			}
		}
	}

	/**
	 * Compile MIBS
	 */
	public void mibsCompile() {

		/*THIS WILL STRICKLY CHECK SMI CONFORMANCE*/ 
		SMIManagerService.SNMP_SMI_SPEC_CHECK = boolSnmpSmiSpecCheck;
		
		SMIManagerService.MIB_COMPILE_OUTPUT_VERBOSE = boolMibCompileOutputVerbose;

		try {
			SMIManagerService.SmiManagerStart();
		} catch (SMIManagerServiceException e2) {
			e2.printStackTrace();
		}

		/*
		 * MIB.txt -> SNMP4J-MIB.bin
		 */
		SMIManagerService.CompileMIBS();
	}
	
}
