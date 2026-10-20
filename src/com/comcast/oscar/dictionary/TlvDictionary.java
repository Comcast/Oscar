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

  /** Creates a DOCSIS dictionary backed by the bundled JSON registry; performs no file writes. */
  public TlvDictionary() {
    this(DOCSIS_DICTIONARY_TABLE_NAME);
  }

  /**
   * Creates a lookup for the selected family, for example DOCSIS_DICTIONARY_TABLE_NAME.
   *
   * @param table non-null family identifier declared by this class
   * @throws IllegalArgumentException if the identifier is unsupported
   * @throws NullPointerException if table is null
   */
  public TlvDictionary(String table) {
    updateDictionaryTablename(table);
  }

  /**
   * Switches this lookup to another bundled family without changing registry content. An invalid
   * identifier leaves the current selection unchanged.
   *
   * @param table non-null family identifier declared by this class
   * @throws IllegalArgumentException if the identifier is unsupported
   * @throws NullPointerException if table is null
   */
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

  /**
   * @return the selected family identifier; no database table is accessed
   */
  public String getDictionaryTableName() {
    return table;
  }

  /**
   * Builds an independent, mutable template for one top-level TLV and its children. For DOCSIS,
   * type 3 describes NetworkAccess. Template edits do not alter the registry.
   *
   * @param type numeric top-level TLV identifier
   * @return a fresh template, or an empty object when the type is unknown
   */
  public JSONObject getTlvDefinition(int type) {
    return registry.template(List.of(type));
  }

  /**
   * Alias for getTlvDefinition, retaining the existing editor/codec API.
   *
   * @param type numeric top-level TLV identifier
   * @return a fresh template, or an empty object when the type is unknown
   */
  public JSONObject getTlvDictionary(int type) {
    return getTlvDefinition(type);
  }

  /**
   * @return an unmodifiable list of top-level identifiers in catalog order
   */
  public List<Integer> getTopLevelTLV() {
    return registry.children(List.of()).stream().map(TlvDefinition::type).toList();
  }

  /**
   * Looks up encoded length-field widths, not value lengths, for the selected family.
   *
   * @return a fresh ordered map from top-level type to length-field byte count
   */
  public Map<Integer, Integer> getTopLevelByteLength() {
    Map<Integer, Integer> result = new LinkedHashMap<>();
    registry.children(List.of()).forEach(d -> result.put(d.type(), d.lengthBytes()));
    return result;
  }

  /**
   * Looks up length-field widths for another family without changing this lookup.
   *
   * @param table non-null family identifier declared by this class
   * @return a fresh ordered map of type to length-field byte count
   * @throws IllegalArgumentException if the identifier is unsupported
   * @throws NullPointerException if table is null
   */
  public Map<Integer, Integer> getTypeToByteLengthMap(String table) {
    return new TlvDictionary(table).getTopLevelByteLength();
  }

  /**
   * @return a fresh map of top-level names to numeric types, in catalog order
   */
  public Map<String, Integer> getAllTopLevelTypeNameToType() {
    Map<String, Integer> result = new LinkedHashMap<>();
    registry.children(List.of()).forEach(d -> result.put(d.name(), d.type()));
    return result;
  }

  /**
   * Builds independent lookup templates for the requested configuration family.
   *
   * @param configurationType a ConfigurationFileTypeConstants value
   * @return top-level templates with nested children in catalog order; does not change this lookup
   * @throws IllegalArgumentException when the configuration family is unsupported
   */
  public JSONArray getAllTlvDefinition(int configurationType) {
    String selected;
    if (configurationType == ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE) {
      selected = DOCSIS_40_DICTIONARY_TABLE_NAME;
    } else if (configurationType >= ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE
        && configurationType <= ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE) {
      selected = DOCSIS_DICTIONARY_TABLE_NAME;
    } else if (configurationType >= ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE
        && configurationType <= ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE) {
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
