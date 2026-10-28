/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.parser;

import com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.dictionary.DictionaryTLV;
import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.tlv.TlvAssembler;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

  // Bounded container instances must not share mutable registry definitions: repeated
  // prefixes and SNMP access rows carry independent values in source order.
  private JSONObject containerTemplate;
  private final java.util.List<JSONObject> containerInstances = new java.util.ArrayList<>();

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

    if (ctx.getText().equals("DPoG")) {
      dsqTlvDictionary.updateDictionaryTablename(TlvDictionary.DPOG_DICTIONARY_TABLE_NAME);
      iConfigurationFileType = ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE;
    }

    logger.debug(() -> "TlvConfigurationFileParser.enterConfigType() " + ctx.getText());

    // Create a Map of String TypeName -> Type
    hmsiTopLevelTypeNameToType = dsqTlvDictionary.getAllTopLevelTypeNameToType();
  }

  /**
   * Starts an independent top-level definition for the parsed statement.
   *
   * <p>DOCSIS containers retain a read-only template and create independent child instances so
   * repeated containers preserve source order.
   *
   * @param ctx non-null parsed top-level TLV context
   * @throws IllegalArgumentException if the top-level name is unknown
   */
  @Override
  public void enterStartTLV(@NotNull tlvParser.StartTLVContext ctx) {

    logger.debug(() -> "TlvConfigurationFileParser.enterStartTLV() " + ctx.getStart().getText());

    Integer type = hmsiTopLevelTypeNameToType.get(ctx.getStart().getText());
    if (type == null) {
      throw new IllegalArgumentException("Unknown top-level TLV: " + ctx.getStart().getText());
    }
    joWorkingTLVDictionary = dsqTlvDictionary.getTlvDefinition(type);
    seenDefinitions.clear();
    containerInstances.clear();
    containerTemplate =
        joWorkingTLVDictionary.getBoolean(Dictionary.ARE_SUBTYPES)
                && (iConfigurationFileType == ConfigurationFile.DOCSIS_VER_31
                    || iConfigurationFileType == ConfigurationFile.DOCSIS_VER_40)
            ? new JSONObject(joWorkingTLVDictionary.toString())
            : null;
    if (containerTemplate != null) {
      joWorkingTLVDictionary.put(Dictionary.SUBTYPE_ARRAY, new JSONArray());
    }

    logger.debug("TlvConfigurationFileParser.enterStartTLV() {}", joWorkingTLVDictionary);

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

    logger.debug(() -> "TlvConfigurationFileParser.enterTlv() " + ctx.getStart().getText());

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

    logger.debug(() -> "TlvConfigurationFileParser.enterSnmp() " + ctx.getStart().getText());

    JSONObject definition = validateTlvName(ctx.getStart().getText());
    aqsSubTypeHierarchyStack.push(ctx.getStart().getText());

    logger.debug(
        "TlvConfigurationFileParser.enterSnmp(): ArrayDeque:  {}", aqsSubTypeHierarchyStack);

    DictionaryTLV.updateDictionaryValue(aqsSubTypeHierarchyStack, definition, ctx);

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

  private void validateDpogNumbers(JSONArray definitions) {
    for (int index = 0; index < definitions.length(); index++) {
      JSONObject definition = definitions.getJSONObject(index);
      if (definition.getBoolean(Dictionary.ARE_SUBTYPES)) {
        validateDpogNumbers(definition.getJSONArray(Dictionary.SUBTYPE_ARRAY));
      } else if (definition.getString(Dictionary.DATA_TYPE).equals("integer")) {
        String textValue = definition.optString(Dictionary.VALUE, "");
        int width = definition.getInt(Dictionary.LENGTH_MIN);
        if (width > 0 && !textValue.isEmpty()) {
          java.math.BigInteger number = new java.math.BigInteger(textValue);
          if (number.signum() < 0 || number.bitLength() > width * Byte.SIZE) {
            throw new IllegalArgumentException(
                "DPoG integer exceeds encoded width: " + definition.getString(Dictionary.TLV_NAME));
          }
        }
      }
    }
  }

  private JSONObject validateTlvName(String name) {
    if (containerTemplate != null) {
      return newContainerInstance(name);
    }
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

  private JSONObject newContainerInstance(String name) {
    int depth = aqsSubTypeHierarchyStack.size();
    JSONObject template = containerTemplate;
    var ancestors = aqsSubTypeHierarchyStack.descendingIterator();
    if (ancestors.hasNext()) ancestors.next();
    while (ancestors.hasNext()) template = findChildDefinition(template, ancestors.next());
    if (depth > 0) template = findChildDefinition(template, name);
    trackDefinition(template);
    JSONObject instance = depth == 0 ? joWorkingTLVDictionary : new JSONObject(template.toString());
    if (instance.getBoolean(Dictionary.ARE_SUBTYPES))
      instance.put(Dictionary.SUBTYPE_ARRAY, new JSONArray());
    if (depth > 0) {
      JSONObject parent = containerInstances.get(depth - 1);
      JSONArray children = parent.getJSONArray(Dictionary.SUBTYPE_ARRAY);
      // I12 C.1.1.18: the general extension VendorId must be first on the wire,
      // regardless of text or catalog order. Preserve the historical normalization.
      if (iConfigurationFileType == ConfigurationFile.DOCSIS_VER_40
          && depth == 1
          && parent.getInt(Dictionary.TYPE) == DocsisTlvTypes.DOC_EXT_FIELD
          && instance.getInt(Dictionary.TYPE) == DocsisTlvTypes.VENDOR_ID_ENC) {
        JSONArray ordered = new JSONArray().put(instance);
        for (Object child : children) ordered.put(child);
        parent.put(Dictionary.SUBTYPE_ARRAY, ordered);
      } else {
        children.put(instance);
      }
    }
    while (containerInstances.size() > depth) containerInstances.removeLast();
    containerInstances.add(instance);
    return instance;
  }

  private JSONObject valueTarget() {
    return containerTemplate == null
        ? joWorkingTLVDictionary
        : containerInstances.get(aqsSubTypeHierarchyStack.size() - 1);
  }

  private JSONObject trackDefinition(JSONObject definition) {
    if ((iConfigurationFileType == ConfigurationFile.DOCSIS_VER_40
            || iConfigurationFileType == ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE)
        && !seenDefinitions.add(definition)) {
      throw new IllegalArgumentException(
          "Repeated child TLV: " + definition.getString(Dictionary.TLV_NAME));
    }
    return definition;
  }

  private JSONObject findChildDefinition(JSONObject parent, String name) {
    JSONArray children = parent.optJSONArray(Dictionary.SUBTYPE_ARRAY);
    if (children != null) {
      for (int index = 0; index < children.length(); index++) {
        JSONObject child = children.getJSONObject(index);
        if (child.getString(Dictionary.TLV_NAME).equalsIgnoreCase(name)) {
          if (iConfigurationFileType == ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE) {
            // Visited children move to the end in source order; unvisited definitions emit no
            // bytes.
            children.remove(index);
            children.put(child);
          }
          return child;
        }
      }
    }
    if (containerTemplate != null) {
      JSONObject raw = com.comcast.oscar.tlv.DocsisVendorExtension.rawDefinition(parent, name);
      if (raw != null) {
        return raw;
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

    logger.debug(
        "TlvConfigurationFileParser.enterSubTypeValue(): ArrayDeque:  {}",
        aqsSubTypeHierarchyStack);
    logger.debug(
        () ->
            "TlvConfigurationFileParser.enterSubTypeValue(): SubValue:  "
                + ctx.getStart().getText());

    DictionaryTLV.updateDictionaryValue(
        aqsSubTypeHierarchyStack,
        valueTarget(),
        com.comcast.oscar.utilities.TlvTextString.decode(ctx.getStart().getText()));
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

    logger.debug(
        "TlvConfigurationFileParser.enterSubTypeValue(): ArrayDeque:  {}",
        aqsSubTypeHierarchyStack);
    logger.debug(
        () ->
            "TlvConfigurationFileParser.enterSubTypeValue(): Value:  " + ctx.getStart().getText());

    String value = com.comcast.oscar.utilities.TlvTextString.decode(ctx.getStart().getText());
    JSONObject target = valueTarget();
    if (iConfigurationFileType == ConfigurationFile.DOCSIS_VER_31
        && java.util.Set.of(201, 216, 219, 220, 221).contains(target.getInt(Dictionary.TYPE))) {
      String hex = value.replace(":", "");
      if (!hex.matches("(?:[0-9a-fA-F]{2})*")
          || hex.length() / 2 > target.getInt(Dictionary.LENGTH_MAX)) {
        throw new IllegalArgumentException("Invalid opaque eSAFE hexadecimal payload");
      }
    }
    DictionaryTLV.updateDictionaryValue(aqsSubTypeHierarchyStack, target, value);
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
   * Assembles populated document instances after validating DOCSIS vendor scopes.
   *
   * @return encoded TLV fragment with valueless top-level placeholders removed
   * @throws IllegalArgumentException if a populated vendor extension is malformed
   */
  public byte[] toByteArray() {

    if (iConfigurationFileType == ConfigurationFile.DOCSIS_VER_31
        || iConfigurationFileType == ConfigurationFile.DOCSIS_VER_40) {
      com.comcast.oscar.tlv.DocsisVendorExtension.validate(jaTlvDictionary);
    }
    byte[] baTlvAssembler = null;

    logger.debug("TlvConfigurationFileParser.toByteArray() {}", jaTlvDictionary);

    if (iConfigurationFileType == ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE) {
      validateDpogNumbers(jaTlvDictionary);
    }
    try {
      baTlvAssembler =
          TlvBuilder.stripZeroByteTLV(
              new TlvAssembler(jaTlvDictionary).toByteArray(), getTopLevelTypeToNumByteLength());
    } catch (TlvException e) {
      e.printStackTrace();
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
