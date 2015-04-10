package com.comcast.oscar.parser;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.misc.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.sql.queries.DictionarySQLQueries;
import com.comcast.oscar.tlv.TlvAssembler;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.tlv.dictionary.Dictionary;
import com.comcast.oscar.tlv.dictionary.DictionaryTLV;
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


public class TlvConfigurationFileParser extends tlvBaseListener {

	private DictionarySQLQueries dsqTlvDictionary = null;
	
	private HashMap<String,Integer> hmsiTopLevelTypeNameToType = null;
	
	private Map<Integer,Integer> miiTopLevelTypeToNumByteLength = new HashMap<Integer,Integer>();
	
	private JSONArray jaTlvDictionary = new JSONArray();
	
	private JSONObject joWorkingTLVDictionary;
	
	private boolean debug = Boolean.FALSE;
	
	private ArrayDeque<String> aqsSubTypeHierarchyStack = new ArrayDeque<String>();
	
	private int iConfigurationFileType = -1;
	
	public TlvConfigurationFileParser () {
		
		//Create Dictionary Object
		this.dsqTlvDictionary = new DictionarySQLQueries();
				
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.ConfigTypeContext
	 */
	@Override public void enterConfigType(@NotNull tlvParser.ConfigTypeContext ctx) { 
		
		boolean localDebug = Boolean.FALSE;
		
		//Determine which Dictionary to select
		if (ctx.getText().equals("Docsis")) {
			dsqTlvDictionary.updateDictionaryTablename(DictionarySQLQueries.DOCSIS_QUERY_TYPE);
			iConfigurationFileType = ConfigurationFile.DOCSIS_VER_31;
		} else if (ctx.getText().equals("PacketCable-1.0")) {
			dsqTlvDictionary.updateDictionaryTablename(DictionarySQLQueries.PACKET_CABLE_QUERY_TYPE);
			iConfigurationFileType = ConfigurationFile.PKT_CBL_VER_10;
		} else if (ctx.getText().equals("PacketCable-1.5")) {
			dsqTlvDictionary.updateDictionaryTablename(DictionarySQLQueries.PACKET_CABLE_QUERY_TYPE);
			iConfigurationFileType = ConfigurationFile.PKT_CBL_VER_15;
		} else if (ctx.getText().equals("PacketCable-2.0")) {
			dsqTlvDictionary.updateDictionaryTablename(DictionarySQLQueries.PACKET_CABLE_QUERY_TYPE);
			iConfigurationFileType = ConfigurationFile.PKT_CBL_VER_20;
		} 
		
		// Added DPoE as a Configuration File Type
		else if (ctx.getText().equals("DPoE")) {
			dsqTlvDictionary.updateDictionaryTablename(DictionarySQLQueries.DOCSIS_QUERY_TYPE);
			iConfigurationFileType = ConfigurationFile.DOCSIS_VER_31;
		}   
		
		if (debug|localDebug) 
			System.out.println("TlvConfigurationFileParser.enterConfigType() " + ctx.getText());
		
		//Create a Map of String TypeName -> Type
		hmsiTopLevelTypeNameToType = (HashMap<String, Integer>) dsqTlvDictionary.getAllTopLevelTypeNameToType();
		
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.StartTLVContext
	 */
	@Override public void enterStartTLV(@NotNull tlvParser.StartTLVContext ctx) { 
	
		boolean localDebug = Boolean.FALSE;

		if (debug|localDebug) { 
			System.out.println("TlvConfigurationFileParser.enterStartTLV() " + ctx.getStart().getText());
		}
		
		joWorkingTLVDictionary = dsqTlvDictionary.getTlvDefinition(hmsiTopLevelTypeNameToType.get(ctx.getStart().getText()));

		if (debug|localDebug) 
			System.out.println("TlvConfigurationFileParser.enterStartTLV() " + joWorkingTLVDictionary);
				
		jaTlvDictionary.put(joWorkingTLVDictionary);
		
		try {
			miiTopLevelTypeToNumByteLength.put(joWorkingTLVDictionary.getInt(Dictionary.TYPE), joWorkingTLVDictionary.getInt(Dictionary.BYTE_LENGTH));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.TlvContext
	 */
	@Override public void enterTlv(@NotNull tlvParser.TlvContext ctx) { 
		
		boolean localDebug = Boolean.FALSE;

		if (debug|localDebug) 
			System.out.println("TlvConfigurationFileParser.enterTlv() " + ctx.getStart().getText());
	
		aqsSubTypeHierarchyStack.push(ctx.getStart().getText());	
	}
	
	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.SnmpContext
	 */
	@Override public void enterSnmp(@NotNull tlvParser.SnmpContext ctx) { 
		
		boolean localDebug = Boolean.FALSE;

		if (debug|localDebug) 
			System.out.println("TlvConfigurationFileParser.enterSnmp() " + ctx.getStart().getText());
	
		aqsSubTypeHierarchyStack.push(ctx.getStart().getText());
		
		if (debug|localDebug) 
			System.out.println("TlvConfigurationFileParser.enterSnmp(): ArrayDeque:  " + aqsSubTypeHierarchyStack);

		DictionaryTLV.updateDictionaryValue(aqsSubTypeHierarchyStack, joWorkingTLVDictionary, ctx);
		
		try {
			miiTopLevelTypeToNumByteLength.put(joWorkingTLVDictionary.getInt(Dictionary.TYPE), joWorkingTLVDictionary.getInt(Dictionary.BYTE_LENGTH));
		} catch (JSONException e) {
			e.printStackTrace();
		}
				
	}
		
	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.SubContext
	 */
	@Override public void enterSub(@NotNull tlvParser.SubContext ctx) { 
		aqsSubTypeHierarchyStack.push(ctx.getStart().getText());
	}
	
	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.SubContext
	 */
	@Override public void exitSub(@NotNull tlvParser.SubContext ctx) { 
		aqsSubTypeHierarchyStack.pop();
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.STlvContext
	 */
	@Override public void enterSTlv(@NotNull tlvParser.STlvContext ctx) { 
		aqsSubTypeHierarchyStack.push(ctx.getStart().getText());
	}
	
	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.STlvContext
	 */
	@Override public void exitSTlv(@NotNull tlvParser.STlvContext ctx) { 
		aqsSubTypeHierarchyStack.pop();
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.SubTypeValueContext
	 */
	@Override public void enterSubTypeValue(@NotNull tlvParser.SubTypeValueContext ctx) { 
		
		boolean localDebug = Boolean.FALSE;
				
		if (debug|localDebug) {
			System.out.println("TlvConfigurationFileParser.enterSubTypeValue(): ArrayDeque:  " + aqsSubTypeHierarchyStack);
			System.out.println("TlvConfigurationFileParser.enterSubTypeValue(): SubValue:  " + ctx.getStart().getText());
		}
		
		DictionaryTLV.updateDictionaryValue(aqsSubTypeHierarchyStack, joWorkingTLVDictionary, ctx.getStart().getText());

	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.TypeValueContext
	 */
	@Override public void enterTypeValue(@NotNull tlvParser.TypeValueContext ctx) { 
		
		boolean localDebug = Boolean.FALSE;
		
		if (debug|localDebug) {
			System.out.println("TlvConfigurationFileParser.enterSubTypeValue(): ArrayDeque:  " + aqsSubTypeHierarchyStack);
			System.out.println("TlvConfigurationFileParser.enterSubTypeValue(): Value:  " + ctx.getStart().getText());		
		}
		
		DictionaryTLV.updateDictionaryValue(aqsSubTypeHierarchyStack, joWorkingTLVDictionary, ctx.getStart().getText());		
	}
	
	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.TlvContext
	 */
	@Override public void exitTlv(@NotNull tlvParser.TlvContext ctx) { 
		aqsSubTypeHierarchyStack.pop();
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation does nothing.
	 * @param ctx tlvParser.SnmpContext
	 */
	@Override public void exitSnmp(@NotNull tlvParser.SnmpContext ctx) { 
		aqsSubTypeHierarchyStack.pop();
	}
	
	/**
	 * 
	
	 * @return byte[] */
	public byte[] toByteArray() {
		
		boolean localDebug = Boolean.FALSE;

		byte[] baTlvAssembler = null;
		
		if (debug|localDebug) 
			System.out.println("TlvConfigurationFileParser.toByteArray() " + jaTlvDictionary);	
		
		try {
			baTlvAssembler = TlvBuilder.stripZeroByteTLV(new TlvAssembler(jaTlvDictionary).toByteArray(), getTopLevelTypeToNumByteLength());
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		if (debug|localDebug)
			for (HexString hs : TlvBuilder.topLevelTlvToHexStringList(baTlvAssembler, getTopLevelTypeToNumByteLength())) {
				System.out.println("TlvConfigurationFileParser.toByteArray() " + hs.toString(":"));
			}
		
		return baTlvAssembler;
	}
	
	/**
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		return new TlvAssembler(jaTlvDictionary).toString();
	}

	/**
	 * 
	 * @param sSeperation
	
	 * @return String
	 */
	public String toString(String sSeperation) {		
		return new TlvAssembler(jaTlvDictionary).toStringSeperation(sSeperation);
	}
	
	/**
	 * 
	
	 * @return Map<Type,NumByteLength> */
	public Map<Integer,Integer> getTopLevelTypeToNumByteLength() {		
		return miiTopLevelTypeToNumByteLength;
	}
	
	/**
	 * 
	
	 * @return JSON Array of TLV Definitions Found */
	public JSONArray getTLVDefinitions() {
		return jaTlvDictionary;
	}
	
	/**
	 * 
	
	 * @return ConfiguationFileType per ConfiguationFile() field statics */
	public int getConfigurationFileType () {
		return this.iConfigurationFileType;
	}
	
}
