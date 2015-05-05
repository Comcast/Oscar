package com.comcast.oscar.cli.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.tlv.TlvDisassemble;

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

public class Specification {
	
	public static final String ERROR = "No specification found. Please set the specification and version. EX: -s d 1.1 (DOCSIS 1.1) / -s p 1.5 (PacketCable 1.5).";
	
	private int iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE;
	private String sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
	private boolean boolPacketCable = false;
	
	/**
	 * Set option parameters for command Specification
	 * @return Option
	 */
	public static final Option OptionParameters() 
	{
		OptionBuilder.withArgName("d{ocsis}|p{acketcable}|dp{oe}> <version");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.withLongOpt("spec");
		OptionBuilder.withDescription("Set specification and version of the file to be compiled/decompiled EX: -s d 1.1 (DOCSIS 1.1) / -s p 1.5 (PacketCable 1.5).");
    	return OptionBuilder.create("s");
	}
	
	/**
	 * Sets the ConfigurationFileType, TlvDisassemble and PacketCable boolean passed on user arguments.
	 * @param args
	 */
	public void setValues(String[] args) 
	{
		
		String sSpecification = "";
		String sVersion = "0";
		
		if (args.length > 0) 
		{
			sSpecification = args[0];
		}
		
		if (args.length > 1) 
		{
			sVersion = args[1];
		}
		
		if (sSpecification.equalsIgnoreCase("d") || sSpecification.equalsIgnoreCase("docsis")) 
		{ 
			this.iConfigurationFileType = -1;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
			
			if (sVersion.equalsIgnoreCase("1") || sVersion.equalsIgnoreCase("10") || sVersion.equalsIgnoreCase("1.0")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE;
			}
			if (sVersion.equalsIgnoreCase("1.1") || sVersion.equalsIgnoreCase("11")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_11_CONFIGURATION_TYPE;
			}
			if (sVersion.equalsIgnoreCase("2") || sVersion.equalsIgnoreCase("20") || sVersion.equalsIgnoreCase("2.0")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_20_CONFIGURATION_TYPE;
			}
			if (sVersion.equalsIgnoreCase("3") || sVersion.equalsIgnoreCase("30") || sVersion.equalsIgnoreCase("3.0")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_30_CONFIGURATION_TYPE;
			}
			if (sVersion.equalsIgnoreCase("3.1") || sVersion.equalsIgnoreCase("31")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE;
			}
		} 
		else if (sSpecification.equalsIgnoreCase("p") || sSpecification.equalsIgnoreCase("packetcable")) 
		{
			this.iConfigurationFileType = -1;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_PACKET_CABLE;
			this.boolPacketCable = true;
			
			if (sVersion.equalsIgnoreCase("1") || sVersion.equalsIgnoreCase("10") || sVersion.equalsIgnoreCase("1.0")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE;
			}
			if (sVersion.equalsIgnoreCase("1.5") || sVersion.equalsIgnoreCase("15")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.PKT_CABLE_15_CONFIGURATION_TYPE;
			}
			if (sVersion.equalsIgnoreCase("2") || sVersion.equalsIgnoreCase("20") || sVersion.equalsIgnoreCase("2.0")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE;
			}
		}
		else if (sSpecification.equalsIgnoreCase("de") || sSpecification.equalsIgnoreCase("dpoe")) 
		{
			this.iConfigurationFileType = -1;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
			this.boolPacketCable = true;
			
			if (sVersion.equalsIgnoreCase("1")|| sVersion.equalsIgnoreCase("1.0")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE;
			}
			if (sVersion.equalsIgnoreCase("2") || sVersion.equalsIgnoreCase("2.0")) 
			{
				this.iConfigurationFileType = ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE;
			}
		}
		else if (sSpecification.equalsIgnoreCase("dg") || sSpecification.equalsIgnoreCase("dpog")) 
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
			this.boolPacketCable = true;
		}
		else if (sSpecification.equalsIgnoreCase("d1") 
				|| sSpecification.equalsIgnoreCase("docsis1") 
				|| sSpecification.equalsIgnoreCase("d1.0") 
				|| sSpecification.equalsIgnoreCase("docsis1.0") 
				|| sSpecification.equalsIgnoreCase("d10") 
				|| sSpecification.equalsIgnoreCase("docsis10")) 
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
		} 
		else if (sSpecification.equalsIgnoreCase("d1.1") 
				|| sSpecification.equalsIgnoreCase("docsis1.1") 
				|| sSpecification.equalsIgnoreCase("d11") 
				|| sSpecification.equalsIgnoreCase("docsis11")) 
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_11_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
		} 
		else if (sSpecification.equalsIgnoreCase("d2") 
				|| sSpecification.equalsIgnoreCase("docsis2") 
				|| sSpecification.equalsIgnoreCase("d2.0") 
				|| sSpecification.equalsIgnoreCase("docsis2.0") 
				|| sSpecification.equalsIgnoreCase("d20") 
				|| sSpecification.equalsIgnoreCase("docsis20")) 
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_20_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
		} 
		else if (sSpecification.equalsIgnoreCase("d3") 
				|| sSpecification.equalsIgnoreCase("docsis3") 
				|| sSpecification.equalsIgnoreCase("d3.0") 
				|| sSpecification.equalsIgnoreCase("docsis3.0") 
				|| sSpecification.equalsIgnoreCase("d30") 
				|| sSpecification.equalsIgnoreCase("docsis30"))
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_30_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
		} 
		else if (sSpecification.equalsIgnoreCase("d3.1") 
				|| sSpecification.equalsIgnoreCase("docsis3.1") 
				|| sSpecification.equalsIgnoreCase("d31") 
				|| sSpecification.equalsIgnoreCase("docsis31")) 
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
		} 
		else if (sSpecification.equalsIgnoreCase("p1") 
				|| sSpecification.equalsIgnoreCase("packetcable1") 
				|| sSpecification.equalsIgnoreCase("p1.0") 
				|| sSpecification.equalsIgnoreCase("packetcable1.0") 
				|| sSpecification.equalsIgnoreCase("p10") 
				|| sSpecification.equalsIgnoreCase("packetcable10")) 
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_PACKET_CABLE;
			this.boolPacketCable = true;
		} 
		else if (sSpecification.equalsIgnoreCase("p1.5") 
				|| sSpecification.equalsIgnoreCase("packetcable1.5") 
				|| sSpecification.equalsIgnoreCase("packetcable15") 
				|| sSpecification.equalsIgnoreCase("packetcable15")) 
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.PKT_CABLE_15_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_PACKET_CABLE;
			this.boolPacketCable = true;
		} 
		else if (sSpecification.equalsIgnoreCase("p2") 
				|| sSpecification.equalsIgnoreCase("packetcable2") 
				|| sSpecification.equalsIgnoreCase("p2.0") 
				|| sSpecification.equalsIgnoreCase("packetcable2.0") 
				|| sSpecification.equalsIgnoreCase("p20") 
				|| sSpecification.equalsIgnoreCase("packetcable20"))
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_PACKET_CABLE;
			this.boolPacketCable = true;
		} 
		else if (sSpecification.equalsIgnoreCase("de1") 
				|| sSpecification.equalsIgnoreCase("de1.0") 
				|| sSpecification.equalsIgnoreCase("dpoe1") 
				|| sSpecification.equalsIgnoreCase("dpoe1.0"))
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
		}
		else if (sSpecification.equalsIgnoreCase("de2") 
				|| sSpecification.equalsIgnoreCase("de2.0") 
				|| sSpecification.equalsIgnoreCase("dpoe2") 
				|| sSpecification.equalsIgnoreCase("dpoe2.0"))
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
		}
		else if (sSpecification.equalsIgnoreCase("dg1") 
				|| sSpecification.equalsIgnoreCase("dg1.0") 
				|| sSpecification.equalsIgnoreCase("dpog1") 
				|| sSpecification.equalsIgnoreCase("dpog1.0"))
		{
			this.iConfigurationFileType = ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE;
			this.sTlvDisassemble = TlvDisassemble.TLV_TYPE_DOCSIS;
		}
		else {
			this.iConfigurationFileType = -1;
			this.sTlvDisassemble = null;
		}
	}
		
	/**
	 * Return ConfigurationFileType
	 * @return int
	 */
	public int getConfigurationFileType() 
	{
		return iConfigurationFileType;
	}
	
	/**
	 * Return TlvDisassemble
	 * @return String
	 */
	public String getTlvDisassemble() 
	{
		return sTlvDisassemble;
	}
	
	/**
	 * Return PacketCable boolean
	 * @return boolean
	 */
	public boolean isPacketCable() 
	{
		return boolPacketCable;
	}
}
