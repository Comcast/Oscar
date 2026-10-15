package com.comcast.oscar.parser;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.dictionary.DictionaryTLV;
import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.tlv.TlvAssembler;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @bannerLicense Copyright 2015 - 2026 Comcast Cable Communications Management, LLC<br>
 *     ___________________________________________________________________<br>
 *     Licensed under the Apache License, Version 2.0 (the "License")<br>
 *     you may not use this file except in compliance with the License.<br>
 *     You may obtain a copy of the License at<br>
 *     http://www.apache.org/licenses/LICENSE-2.0<br>
 *     Unless required by applicable law or agreed to in writing, software<br>
 *     distributed under the License is distributed on an "AS IS" BASIS,<br>
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
 *     See the License for the specific language governing permissions and<br>
 *     limitations under the License.<br>
 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */
public class TlvConfigurationFileParser extends tlvBaseListener {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(TlvConfigurationFileParser.class);

  private TlvDictionary dsqTlvDictionary = null;

  private Map<String, Integer> hmsiTopLevelTypeNameToType = null;

  private Map<Integer, Integer> miiTopLevelTypeToNumByteLength = new HashMap<Integer, Integer>();

  private JSONArray jaTlvDictionary = new JSONArray();

  private JSONObject joWorkingTLVDictionary;

  private ArrayDeque<String> aqsSubTypeHierarchyStack = new ArrayDeque<String>();

  private int iConfigurationFileType = -1;

  private final java.util.Set<JSONObject> seenDefinitions =
      java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());

  public TlvConfigurationFileParser() {

    // Create Dictionary Object
    this.dsqTlvDictionary = new TlvDictionary();
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.ConfigTypeContext
   */
  @Override
  public void enterConfigType(@NotNull tlvParser.ConfigTypeContext ctx) {

    // Determine which Dictionary to select
    if (ctx.getText().equals("Docsis")) {
      dsqTlvDictionary.updateDictionaryTablename(TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME);
      iConfigurationFileType = ConfigurationFile.DOCSIS_VER_31;
    } else if (ctx.getText().equals("Docsis-4.0")) {
      dsqTlvDictionary.updateDictionaryTablename(TlvDictionary.DOCSIS_40_DICTIONARY_TABLE_NAME);
      iConfigurationFileType =
          com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants
              .DOCSIS_40_CONFIGURATION_TYPE;
    } else if (ctx.getText().equals("PacketCable-1.0")) {
      dsqTlvDictionary.updateDictionaryTablename(TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME);
      iConfigurationFileType = ConfigurationFile.PKT_CBL_VER_10;
    } else if (ctx.getText().equals("PacketCable-1.5")) {
      dsqTlvDictionary.updateDictionaryTablename(TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME);
      iConfigurationFileType = ConfigurationFile.PKT_CBL_VER_15;
    } else if (ctx.getText().equals("PacketCable-2.0")) {
      dsqTlvDictionary.updateDictionaryTablename(TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME);
      iConfigurationFileType = ConfigurationFile.PKT_CBL_VER_20;
    }

    // Added DPoE as a Configuration File Type
    else if (ctx.getText().equals("DPoE")) {
      dsqTlvDictionary.updateDictionaryTablename(TlvDictionary.DPOE_DICTIONARY_TABLE_NAME);
      iConfigurationFileType = ConfigurationFile.DPOE_VER_20;
    }

    if (logger.isDebugEnabled())
      logger.debug("TlvConfigurationFileParser.enterConfigType() " + ctx.getText());

    // Create a Map of String TypeName -> Type
    hmsiTopLevelTypeNameToType = dsqTlvDictionary.getAllTopLevelTypeNameToType();
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.StartTLVContext
   */
  @Override
  public void enterStartTLV(@NotNull tlvParser.StartTLVContext ctx) {

    if (logger.isDebugEnabled()) {
      logger.debug("TlvConfigurationFileParser.enterStartTLV() " + ctx.getStart().getText());
    }

    Integer type = hmsiTopLevelTypeNameToType.get(ctx.getStart().getText());
    if (type == null) {
      throw new IllegalArgumentException("Unknown top-level TLV: " + ctx.getStart().getText());
    }
    joWorkingTLVDictionary = dsqTlvDictionary.getTlvDefinition(type);
    seenDefinitions.clear();

    if (logger.isDebugEnabled())
      logger.debug("TlvConfigurationFileParser.enterStartTLV() " + joWorkingTLVDictionary);

    jaTlvDictionary.put(joWorkingTLVDictionary);

    try {
      miiTopLevelTypeToNumByteLength.put(
          joWorkingTLVDictionary.getInt(Dictionary.TYPE),
          joWorkingTLVDictionary.getInt(Dictionary.BYTE_LENGTH));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.TlvContext
   */
  @Override
  public void enterTlv(@NotNull tlvParser.TlvContext ctx) {

    if (logger.isDebugEnabled())
      logger.debug("TlvConfigurationFileParser.enterTlv() " + ctx.getStart().getText());

    validateTlvName(ctx.getStart().getText());
    aqsSubTypeHierarchyStack.push(ctx.getStart().getText());
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.SnmpContext
   */
  @Override
  public void enterSnmp(@NotNull tlvParser.SnmpContext ctx) {

    if (logger.isDebugEnabled())
      logger.debug("TlvConfigurationFileParser.enterSnmp() " + ctx.getStart().getText());

    validateTlvName(ctx.getStart().getText());
    aqsSubTypeHierarchyStack.push(ctx.getStart().getText());

    if (logger.isDebugEnabled())
      logger.debug(
          "TlvConfigurationFileParser.enterSnmp(): ArrayDeque:  " + aqsSubTypeHierarchyStack);

    DictionaryTLV.updateDictionaryValue(aqsSubTypeHierarchyStack, joWorkingTLVDictionary, ctx);

    try {
      miiTopLevelTypeToNumByteLength.put(
          joWorkingTLVDictionary.getInt(Dictionary.TYPE),
          joWorkingTLVDictionary.getInt(Dictionary.BYTE_LENGTH));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.SubContext
   */
  @Override
  public void enterSub(@NotNull tlvParser.SubContext ctx) {
    JSONObject definition = validateTlvName(ctx.getStart().getText());
    if (!definition.getBoolean(Dictionary.ARE_SUBTYPES)) {
      throw new IllegalArgumentException(
          "TLV " + ctx.getStart().getText() + " cannot contain child TLVs");
    }
    aqsSubTypeHierarchyStack.push(ctx.getStart().getText());
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.SubContext
   */
  @Override
  public void exitSub(@NotNull tlvParser.SubContext ctx) {
    aqsSubTypeHierarchyStack.pop();
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.STlvContext
   */
  @Override
  public void enterSTlv(@NotNull tlvParser.STlvContext ctx) {
    validateTlvName(ctx.getStart().getText());
    aqsSubTypeHierarchyStack.push(ctx.getStart().getText());
  }

  @Override
  public void enterEmptyTLV(@NotNull tlvParser.EmptyTLVContext ctx) {
    validateTlvName(ctx.getStart().getText());
  }

  private JSONObject validateTlvName(String name) {
    JSONObject definition = joWorkingTLVDictionary;
    var ancestors = aqsSubTypeHierarchyStack.descendingIterator();
    if (!ancestors.hasNext()) {
      if (!definition.getString(Dictionary.TLV_NAME).equalsIgnoreCase(name)) {
        throw new IllegalArgumentException("Unknown TLV: " + name);
      }
      return trackDefinition(definition);
    }
    ancestors.next();
    while (ancestors.hasNext()) {
      definition = findChildDefinition(definition, ancestors.next());
    }
    return trackDefinition(findChildDefinition(definition, name));
  }

  private JSONObject trackDefinition(JSONObject definition) {
    if (iConfigurationFileType == ConfigurationFile.DOCSIS_VER_40
        && !seenDefinitions.add(definition)) {
      throw new IllegalArgumentException(
          "Repeated DOCSIS 4.0 child TLV: " + definition.getString(Dictionary.TLV_NAME));
    }
    return definition;
  }

  private JSONObject findChildDefinition(JSONObject parent, String name) {
    JSONArray children = parent.optJSONArray(Dictionary.SUBTYPE_ARRAY);
    if (children != null) {
      for (int index = 0; index < children.length(); index++) {
        JSONObject child = children.getJSONObject(index);
        if (child.getString(Dictionary.TLV_NAME).equalsIgnoreCase(name)) {
          return child;
        }
      }
    }
    throw new IllegalArgumentException(
        "Unknown TLV " + name + " under " + parent.getString(Dictionary.TLV_NAME));
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.STlvContext
   */
  @Override
  public void exitSTlv(@NotNull tlvParser.STlvContext ctx) {
    aqsSubTypeHierarchyStack.pop();
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.SubTypeValueContext
   */
  @Override
  public void enterSubTypeValue(@NotNull tlvParser.SubTypeValueContext ctx) {

    if (logger.isDebugEnabled()) {
      logger.debug(
          "TlvConfigurationFileParser.enterSubTypeValue(): ArrayDeque:  "
              + aqsSubTypeHierarchyStack);
      logger.debug(
          "TlvConfigurationFileParser.enterSubTypeValue(): SubValue:  " + ctx.getStart().getText());
    }

    DictionaryTLV.updateDictionaryValue(
        aqsSubTypeHierarchyStack, joWorkingTLVDictionary, ctx.getStart().getText());
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.TypeValueContext
   */
  @Override
  public void enterTypeValue(@NotNull tlvParser.TypeValueContext ctx) {

    if (logger.isDebugEnabled()) {
      logger.debug(
          "TlvConfigurationFileParser.enterSubTypeValue(): ArrayDeque:  "
              + aqsSubTypeHierarchyStack);
      logger.debug(
          "TlvConfigurationFileParser.enterSubTypeValue(): Value:  " + ctx.getStart().getText());
    }

    DictionaryTLV.updateDictionaryValue(
        aqsSubTypeHierarchyStack, joWorkingTLVDictionary, ctx.getStart().getText());
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.TlvContext
   */
  @Override
  public void exitTlv(@NotNull tlvParser.TlvContext ctx) {
    aqsSubTypeHierarchyStack.pop();
  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   *
   * @param ctx tlvParser.SnmpContext
   */
  @Override
  public void exitSnmp(@NotNull tlvParser.SnmpContext ctx) {
    aqsSubTypeHierarchyStack.pop();
  }

  /**
   * @return byte[]
   */
  public byte[] toByteArray() {

    byte[] baTlvAssembler = null;

    if (logger.isDebugEnabled())
      logger.debug("TlvConfigurationFileParser.toByteArray() " + jaTlvDictionary);

    try {
      baTlvAssembler =
          TlvBuilder.stripZeroByteTLV(
              new TlvAssembler(jaTlvDictionary).toByteArray(), getTopLevelTypeToNumByteLength());
    } catch (TlvException e) {
      e.printStackTrace();
    }

    if (logger.isDebugEnabled())
      for (HexString hs :
          TlvBuilder.topLevelTlvToHexStringList(baTlvAssembler, getTopLevelTypeToNumByteLength())) {
        logger.debug("TlvConfigurationFileParser.toByteArray() " + hs.toString(":"));
      }

    return baTlvAssembler;
  }

  /**
   * @return String
   */
  @Override
  public String toString() {
    return new TlvAssembler(jaTlvDictionary).toString();
  }

  /**
   * @param sSeperation
   * @return String
   */
  public String toString(String sSeperation) {
    return new TlvAssembler(jaTlvDictionary).toStringSeperation(sSeperation);
  }

  /**
   * @return Map<Type,NumByteLength>
   */
  public Map<Integer, Integer> getTopLevelTypeToNumByteLength() {
    return miiTopLevelTypeToNumByteLength;
  }

  /**
   * @return JSON Array of TLV Definitions Found
   */
  public JSONArray getTLVDefinitions() {
    return jaTlvDictionary;
  }

  /**
   * @return ConfiguationFileType per ConfiguationFile() field statics
   */
  public int getConfigurationFileType() {
    return this.iConfigurationFileType;
  }
}
