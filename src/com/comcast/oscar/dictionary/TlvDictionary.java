package com.comcast.oscar.dictionary;

import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/** Compatibility-shaped lookup API backed by JSON symbol tables, not SQL. */
public class TlvDictionary {
  public static final String DOCSIS_DICTIONARY_TABLE_NAME =
      DictionarySQLConstants.DOCSIS_DICTIONARY_TABLE_NAME;
  public static final String PACKET_CABLE_DICTIONARY_TABLE_NAME =
      DictionarySQLConstants.PACKET_CABLE_DICTIONARY_TABLE_NAME;
  public static final String DPOE_DICTIONARY_TABLE_NAME =
      DictionarySQLConstants.DPOE_DICTIONARY_TABLE_NAME;
  public static final Integer CONFIGURATION_FILE_TYPE_DOCSIS =
      ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE;
  public static final Integer CONFIGURATION_FILE_TYPE_PACKET_CABLE =
      ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE;
  public static final Integer CONFIGURATION_FILE_TYPE_DPOE =
      ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE;
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
          case DOCSIS_DICTIONARY_TABLE_NAME -> "docsis";
          case PACKET_CABLE_DICTIONARY_TABLE_NAME -> "packetcable";
          case DPOE_DICTIONARY_TABLE_NAME -> "dpoe";
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

  public JSONArray getAllTlvDefinition(int configurationType) {
    String selected;
    if (configurationType >= 0 && configurationType <= 4) selected = DOCSIS_DICTIONARY_TABLE_NAME;
    else if (configurationType >= 100 && configurationType <= 102)
      selected = PACKET_CABLE_DICTIONARY_TABLE_NAME;
    else if (configurationType == 200 || configurationType == 201)
      selected = DPOE_DICTIONARY_TABLE_NAME;
    else throw new IllegalArgumentException("Unsupported configuration type: " + configurationType);
    TlvDictionary dictionary = new TlvDictionary(selected);
    JSONArray result = new JSONArray();
    dictionary.getTopLevelTLV().forEach(type -> result.put(dictionary.getTlvDefinition(type)));
    return result;
  }
}
