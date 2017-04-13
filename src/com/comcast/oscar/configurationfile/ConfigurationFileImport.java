package com.comcast.oscar.configurationfile;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.parser.TlvConfigurationFileParser;
import com.comcast.oscar.parser.tlvLexer;
import com.comcast.oscar.parser.tlvParser;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.tlv.TlvVariableBinding;
import com.comcast.oscar.utilities.HexString;

/**
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


 * @author Maurice Garcia (maurice.garcia.2015@gmail.com)
 */

/**
 * ConfigrationFileImport takes a OSCAR ASCII Compliant configuration file and convert to its JSON TLV Dictionary
 * 
 */
public class ConfigurationFileImport {
	private StringBuilder sbConfiguration = null;	
	private TlvConfigurationFileParser tcfpConfig = null;	
	private JSONArray jaTlvDefinition = new JSONArray();	
	private int iConfigurationFileType = -1;
	
	public static final Integer DOCSIS_VER_10 = ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE;
	public static final Integer DOCSIS_VER_11 = ConfigurationFileTypeConstants.DOCSIS_11_CONFIGURATION_TYPE;
	public static final Integer DOCSIS_VER_20 = ConfigurationFileTypeConstants.DOCSIS_20_CONFIGURATION_TYPE;
	public static final Integer DOCSIS_VER_30 = ConfigurationFileTypeConstants.DOCSIS_30_CONFIGURATION_TYPE;
	public static final Integer DOCSIS_VER_31 = ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE;
	
	public static final Integer PKT_CBL_VER_10 = ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE;
	public static final Integer PKT_CBL_VER_15 = ConfigurationFileTypeConstants.PKT_CABLE_15_CONFIGURATION_TYPE;
	public static final Integer PKT_CBL_VER_20 = ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE;
	
	public static final Integer DPOE_VER_10 = ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE;
	public static final Integer DPOE_VER_20 = ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE;
	
	/**
	 * 
	 * @param sbConfiguration
	 */
 	public ConfigurationFileImport(StringBuilder sbConfiguration) {

		this.sbConfiguration = sbConfiguration;
		
		processConfigurationFile(sbConfiguration);
	}
	
	/**
	 * 
	 * @param fConfigfile
	
	 * @throws FileNotFoundException * @throws ConfigrationFileException
	 */
	public ConfigurationFileImport (File fConfigfile) throws FileNotFoundException , ConfigurationFileException {
	
		//Check to see if file is a ASCII
		if (!HexString.verifyAsciiPlainText(HexString.fileToByteArray(fConfigfile)))  {
			throw new ConfigurationFileException("ConfigrationFileImport() - File is not a text File: " + fConfigfile.getName());
		}
		
		@SuppressWarnings("resource")
		String output = new Scanner(fConfigfile).useDelimiter("\\Z").next();
		
		sbConfiguration = new StringBuilder(output);
		
	    processConfigurationFile(sbConfiguration);
	    
	}
	
	/**
	 * 
	 * @param bConfigfile
	
	
	 * @throws NullPointerException * @throws ConfigrationFileException  */
	public ConfigurationFileImport (byte[] bConfigfile) throws NullPointerException, ConfigurationFileException {
		
		if (bConfigfile == null)
			throw new NullPointerException("ConfigrationFileImport() - ByteArray is null");
		
		if (!HexString.verifyAsciiPlainText(bConfigfile))
			throw new ConfigurationFileException("ConfigrationFileImport () - Input ByteArray is not of Text Characters");
		
		this.sbConfiguration = new StringBuilder(new HexString(bConfigfile).toASCII());
		
		processConfigurationFile(sbConfiguration);
	}
		
	/**
	 * 
	
	 * @return byte[]
	 */
 	public byte[] toByteArray() {
		return this.tcfpConfig.toByteArray();
	}

	/**
	 * 
	
	 * @return int
	 */
	public int length () {
		return tcfpConfig.toByteArray().length;
	}

	/**
	 * 
	 * @return String
	 */
	@Override
	public String toString () {
		return tcfpConfig.toString();
	}

	/**
	 * 
	 * @param sSeperation
	
	 * @return String
	 */
	public String toString (String sSeperation) {
		return tcfpConfig.toString(sSeperation);
	}
	
	/**
	 * 
	
	 * @return TlvBuilder
	 */
	public TlvBuilder getTlvBuilder() {
		
		TlvBuilder tb = new TlvBuilder();
		
		if (iConfigurationFileType > DOCSIS_VER_31) {
			
			Map<Integer,Integer> miiTypeToByteLength = new HashMap<Integer,Integer>(64,2);
			
			TlvVariableBinding tvb = new TlvVariableBinding(toByteArray(),miiTypeToByteLength);
			
			tb.add(tvb);
			
		} else {
			try {
				tb.add(new HexString(toByteArray()));
			} catch (TlvException e) {
				e.printStackTrace();
			}			
		}
			
		return tb;		
	}
	
	/**
	 * 
	
	 * @return the configuration file type from ConfigurationFile() field statics */
	public int getConfigurationFileType () {
		return this.iConfigurationFileType;
	}
	
	/**
	 * 
	 * @param iTlvType - Top Level TLV
	
	 * @return JSONArray
	 */
 	public JSONArray getTopLevelTLVJSON(int iTlvType) {
		
		JSONArray jaTopLevelTLV = new JSONArray();
		
		JSONObject joTopLevelTLV = null;
		
		for (int iIndex = 0 ; iIndex < jaTlvDefinition.length(); iIndex++) {
						
			try {
				joTopLevelTLV = jaTlvDefinition.getJSONObject(iIndex);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		
			try {
				
				if (joTopLevelTLV.getInt(Dictionary.TYPE) == iTlvType) {
					jaTopLevelTLV.put(joTopLevelTLV);	
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return jaTopLevelTLV;
	}
 	
	/**
	 * 
	 * @param sbConfigurationFile
	 */
	private void processConfigurationFile (StringBuilder sbConfigurationFile) {

		// create a CharStream that reads from standard input
		ANTLRInputStream input = new ANTLRInputStream(sbConfigurationFile.toString()); 

		// create a Lexer that feeds off of input CharStream
		tlvLexer tlConfigLexer = new tlvLexer(input); 

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream ctsConfigTokens = new CommonTokenStream(tlConfigLexer);

		tlvParser tpConfigParser = new tlvParser(ctsConfigTokens);

		ParseTree ptConfigParseTree = tpConfigParser.begin(); 

		ParseTreeWalker walker = new ParseTreeWalker();

		tcfpConfig = new TlvConfigurationFileParser();

		walker.walk(tcfpConfig, ptConfigParseTree);
		
		//Update local JSONArray
		this.jaTlvDefinition = tcfpConfig.getTLVDefinitions();
		
		this.iConfigurationFileType = tcfpConfig.getConfigurationFileType();

	}
	
	
}
