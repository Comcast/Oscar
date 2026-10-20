/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/** TLV lookup API backed by JSON symbol tables. */
public class TlvDictionary {
  public static final String DOCSIS_40_DICTIONARY_TABLE_NAME = "DOCSIS40_TLV_DEFINITION";
  public static final String DOCSIS_DICTIONARY_TABLE_NAME = "DOCSIS_TLV_DEFINITION";
  public static final String PACKET_CABLE_DICTIONARY_TABLE_NAME = "PACKET_CABLE_TLV_DEFINITION";
  public static final String DPOE_DICTIONARY_TABLE_NAME = "DPOE_TLV_DEFINITION";
  public static final Integer CONFIGURATION_FILE_TYPE_DOCSIS =
      ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE;
  public static final Integer CONFIGURATION_FILE_TYPE_PACKET_CABLE =
      ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE;
  public static final Integer CONFIGURATION_FILE_TYPE_DPOE =
      ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE;
  public static final String DPOG_DICTIONARY_TABLE_NAME = "DPOG_TLV_DEFINITION";
  private String table;
  private TlvRegistry registry;

  public TlvDictionary() {
    this(DOCSIS_DICTIONARY_TABLE_NAME);
  }

  public TlvDictionary(String table) {
    updateDictionaryTablename(table);
  }

  public void updateDictionaryTablename(String table) {
    String family =
        switch (table) {
          case DOCSIS_40_DICTIONARY_TABLE_NAME -> "docsis40";
          case DOCSIS_DICTIONARY_TABLE_NAME -> "docsis";
          case PACKET_CABLE_DICTIONARY_TABLE_NAME -> "packetcable";
          case DPOE_DICTIONARY_TABLE_NAME -> "dpoe";
          case DPOG_DICTIONARY_TABLE_NAME -> "dpog";
          default -> throw new IllegalArgumentException("Unknown TLV dictionary: " + table);
        };
    registry = TlvRegistry.forFamily(family);
    this.table = table;
  }

  public String getDictionaryTableName() {
    return table;
  }

  public JSONObject getTlvDefinition(int type) {
    return registry.template(List.of(type));
  }

  public JSONObject getTlvDictionary(int type) {
    return getTlvDefinition(type);
  }

  public List<Integer> getTopLevelTLV() {
    return registry.children(List.of()).stream().map(TlvDefinition::type).toList();
  }

  public Map<Integer, Integer> getTopLevelByteLength() {
    Map<Integer, Integer> result = new LinkedHashMap<>();
    registry.children(List.of()).forEach(d -> result.put(d.type(), d.lengthBytes()));
    return result;
  }

  public Map<Integer, Integer> getTypeToByteLengthMap(String table) {
    return new TlvDictionary(table).getTopLevelByteLength();
  }

  public Map<String, Integer> getAllTopLevelTypeNameToType() {
    Map<String, Integer> result = new LinkedHashMap<>();
    registry.children(List.of()).forEach(d -> result.put(d.name(), d.type()));
    return result;
  }

  /**
   * Builds independent lookup templates for the requested configuration family.
   *
   * @param configurationType a ConfigurationFileTypeConstants value
   * @return top-level templates with nested children; DPoG returns its provisional catalog
   * @throws IllegalArgumentException when the configuration family is unsupported
   */
  public JSONArray getAllTlvDefinition(int configurationType) {
    String selected;
    if (configurationType == ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE) {
      selected = DOCSIS_40_DICTIONARY_TABLE_NAME;
    } else if (configurationType >= 0 && configurationType <= 4) {
      selected = DOCSIS_DICTIONARY_TABLE_NAME;
    } else if (configurationType >= 100 && configurationType <= 102) {
      selected = PACKET_CABLE_DICTIONARY_TABLE_NAME;
    } else if (configurationType == ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE
        || configurationType == ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE) {
      selected = DPOE_DICTIONARY_TABLE_NAME;
    } else if (configurationType == ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE) {
      selected = DPOG_DICTIONARY_TABLE_NAME;
    } else {
      throw new IllegalArgumentException("Unsupported configuration type: " + configurationType);
    }
    TlvDictionary dictionary = new TlvDictionary(selected);
    JSONArray result = new JSONArray();
    dictionary.getTopLevelTLV().forEach(type -> result.put(dictionary.getTlvDefinition(type)));
    return result;
  }
}
