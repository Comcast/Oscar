/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import com.comcast.oscar.ber.BEROIDConversion;
import com.comcast.oscar.compiler.PacketCableConstants;
import com.comcast.oscar.datatype.DataTypeDictionaryReference;
import com.comcast.oscar.datatype.DataTypeFormatConversion;
import com.comcast.oscar.datatype.DataTypeFormatException;
import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TlvDisassemble extends TlvDictionary {
  /**
   * Inspects a raw TLV stream without requiring construction of a valid configuration.
   *
   * <p>Preserves wire order, unknown entries, original bytes, and bounded partial results. Does not
   * decode credential-bearing values, validate MICs, repair input, or log bytes. Options select the
   * family independently of any legacy instance dictionary.
   *
   * @param configurationBytes raw DOCSIS or DPoE stream, never null
   * @param options explicit family and resource limits, never null
   * @return immutable structural report with explicitly accessible original bytes
   * @throws IllegalArgumentException if input exceeds the configured byte limit
   */
  public static TlvDisassemblyReport diagnose(
      byte[] configurationBytes, DisassemblyOptions options) {
    java.util.Objects.requireNonNull(configurationBytes, "configurationBytes");
    java.util.Objects.requireNonNull(options, "options");
    if (configurationBytes.length > options.maxBytes()) {
      throw new IllegalArgumentException("Diagnostic input exceeds byte limit");
    }
    return new TlvDiagnosticWalker(configurationBytes.clone(), options).inspect();
  }

  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(TlvDisassemble.class);

  private TlvBuilder tbTlvBuffer = null;

  private TlvVariableBinding tvbTlvBuffer = null;

  public static final String TLV_TYPE_DOCSIS = TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME;
  public static final String TLV_TYPE_DOCSIS_40 = "DOCSIS40_TLV_DEFINITION";
  public static final String TLV_TYPE_PACKET_CABLE =
      TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME;
  public static final String TLV_TYPE_DPOG = TlvDictionary.DPOG_DICTIONARY_TABLE_NAME;
  public static final String TLV_TYPE_DPOE = TlvDictionary.DPOE_DICTIONARY_TABLE_NAME;

  /**
   * @param tbTlvBuffer
   * @param sTlvType String
   */
  public TlvDisassemble(TlvBuilder tbTlvBuffer, String sTlvType) {

    super();

    logger.debug("TlvDisassemble(t,s) DICTIONARY-TABLE-NAME: {}", sTlvType);

    // Update local pointer
    this.tbTlvBuffer = tbTlvBuffer;

    logger.debug(() -> "TlvDisassemble(tb,s) TLV Buffer: " + tbTlvBuffer.toStringSeperation(":"));

    // Check the first byte to determine if it is a DOCSIS File or PacketCable File
    super.updateDictionaryTablename(sTlvType);
  }

  /**
   * @param tvbTlvBuffer
   * @param sTlvType String
   */
  public TlvDisassemble(TlvVariableBinding tvbTlvBuffer, String sTlvType) {

    super();

    logger.debug("TlvDisassemble(tvb,s) DICTIONARY-TABLE-NAME: {}", sTlvType);

    // Update local pointer
    this.tvbTlvBuffer = tvbTlvBuffer;

    logger.debug(() -> "TlvDisassemble(tvb,s) TLV Buffer: " + tvbTlvBuffer.toString());

    // Check the first byte to determine if it is a DOCSIS File or PacketCable File
    super.updateDictionaryTablename(sTlvType);
  }

  /**
   * @param fTlvBuffer
   * @param sTlvType String
   */
  public TlvDisassemble(File fTlvBuffer, String sTlvType) {

    super();

    logger.debug("TlvDisassemble(f,s) DICTIONARY-TABLE-NAME: {}", sTlvType);

    byte[] bTlvBuffer = new byte[(int) fTlvBuffer.length()];

    FileInputStream fisTlvBuffer;

    try {
      fisTlvBuffer = new FileInputStream(fTlvBuffer);
      fisTlvBuffer.read(bTlvBuffer);
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.tbTlvBuffer = new TlvBuilder();

    logger.debug(() -> "TlvDisassemble(f,s) TLV Buffer: " + tbTlvBuffer.toStringSeperation(":"));

    try {
      tbTlvBuffer.add(new HexString(bTlvBuffer));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    logger.debug(() -> "TlvDisassemble(f,s) TLV-BUFFER: " + tbTlvBuffer.toStringSeperation(":"));

    // Check the first byte to determine if it is a DOCSIS File or PacketCable File
    super.updateDictionaryTablename(sTlvType);
  }

  /**
   * @return JSONArray
   */
  public JSONArray getTlvDictionary() {
    if (TLV_TYPE_DPOG.equals(getDictionaryTableName())) {
      byte[] binary = tbTlvBuffer != null ? tbTlvBuffer.toByteArray() : tvbTlvBuffer.toByteArray();
      JSONArray decoded = new JSONArray();
      for (var element : DPoGTlvCodec.parse(binary)) {
        JSONObject dictionary = super.getTlvDictionary(element.definition().type());
        try {
          loadTlvValuesIntoTlvDictionary(element.encoded(), dictionary);
        } catch (TlvException exception) {
          throw new IllegalArgumentException(
              "Cannot decode DPoG TLV " + element.definition().path(), exception);
        }
        decoded.put(DPoGTlvCodec.orderDictionary(dictionary, element));
      }
      return decoded;
    }

    // Store the entire return
    JSONArray jaTlvDictionary = new JSONArray();

    // Store JSON Object temp for TLV Value insertion
    JSONObject joTlvDictionary;

    // ByteArrayOutputStream Temp
    ByteArrayOutputStream boasTlvBufferTemp = null;

    List<Integer> liTlvBuffer = null;

    // Check which Type of TLV Buffer we are using
    if (tbTlvBuffer != null) {

      liTlvBuffer = tbTlvBuffer.getTopLevelTlvList(getTopLevelByteLength());

    } else if (tvbTlvBuffer != null) {

      liTlvBuffer = tvbTlvBuffer.getTopLevelTlvList();
    }

    // Get Major TLV from tbTlvBuffer via Map Type -> Byte Length
    for (Integer iType : liTlvBuffer) {

      // mgarcia - Added 140103 - Need a new Buffer for each Type Look up
      boasTlvBufferTemp = new ByteArrayOutputStream();

      // Get TLV Dictionary via TLV Type
      joTlvDictionary = super.getTlvDictionary(iType);

      if (joTlvDictionary == null) {

        continue;
      }

      // Create Map to hold TYPE to Byte Length
      Map<Integer, Integer> miiTypeByteLength = new HashMap<Integer, Integer>();

      // need to account for VariableNumByteLength TLV's
      if (tvbTlvBuffer != null) {

        miiTypeByteLength.put(iType, tvbTlvBuffer.getMapTypeToByteLength().get(iType));

        // fetch Major TLV and store in BASO
        try {
          boasTlvBufferTemp.write(
              TlvBuilder.fetchTlv(
                  new ArrayList<Integer>(Arrays.asList(iType)), // Contains Type
                  miiTypeByteLength, // Contains Type -> ByteLength
                  tvbTlvBuffer.toByteArray())); // Contains TLV ByteArray
        } catch (IOException e) {
          e.printStackTrace();
        }

        try {

          // Update ByteLength for this TLV
          try {
            joTlvDictionary.put(Dictionary.BYTE_LENGTH, miiTypeByteLength.get(iType));
          } catch (JSONException e) {
            e.printStackTrace();
          }

          // Load JSON Object
          jaTlvDictionary.put(
              loadTlvValuesIntoTlvDictionary(tvbTlvBuffer.toByteArray(), joTlvDictionary));

        } catch (TlvException e) {
          e.printStackTrace();
        }

      } else if (tbTlvBuffer != null) {

        // Map to hold TYPE to Byte Length
        try {
          miiTypeByteLength.put(
              joTlvDictionary.getInt(Dictionary.TYPE),
              joTlvDictionary.getInt(Dictionary.BYTE_LENGTH));
        } catch (JSONException e1) {
          e1.printStackTrace();
        }

        // fetch Major TLV and store in BASO
        try {
          boasTlvBufferTemp.write(
              TlvBuilder.fetchTlv(
                  new ArrayList<Integer>(Arrays.asList(iType)), // Contains Type
                  miiTypeByteLength, // Contains Type -> ByteLength
                  tbTlvBuffer.toByteArray())); // Contains TLV ByteArray
        } catch (IOException e) {
          e.printStackTrace();
        }

        try {
          jaTlvDictionary.put(
              loadTlvValuesIntoTlvDictionary(boasTlvBufferTemp.toByteArray(), joTlvDictionary));
        } catch (TlvException e) {
          e.printStackTrace();
        }
      }
    }

    return jaTlvDictionary;
  }

  /**
   * @return List<byte[]>
   */
  public List<byte[]> toListByteArray() {
    return tbTlvBuffer.sortByTopLevelTlv();
  }

  /**
   * @param iTlvType
   * @return JSONArray
   */
  public JSONArray getTopLevelTLVJSON(int iTlvType) {

    JSONArray jaTLV = new JSONArray();

    JSONArray jaTlvDictionary = getTlvDictionary();

    JSONObject joTlvDictionary = null;

    for (int iIndex = 0; iIndex < jaTlvDictionary.length(); iIndex++) {

      try {
        joTlvDictionary = jaTlvDictionary.getJSONObject(iIndex);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      try {

        if (joTlvDictionary.getInt(Dictionary.TYPE) == iTlvType) {
          jaTLV.put(joTlvDictionary);
        }

      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return jaTLV;
  }

  /**
   * @param fConfiguraitonFile
   * @return Return the Type of File to determine DataBase Selection
   */
  public static String GetConfiguraitonFileType(File fConfiguraitonFile) {

    // Convert to Byte Array
    byte[] bTLV = HexString.fileToByteArray(fConfiguraitonFile);

    if (bTLV[0] == PacketCableConstants.FILE_MARKER) {
      return TLV_TYPE_PACKET_CABLE;
    } else {
      return TLV_TYPE_DOCSIS;
    }
  }

  /**
   * @param bTlvBuffer
   * @param jaTlvDictionary
   */
  private void loadTlvValuesIntoTlvDictionary(byte[] bTlvBuffer, JSONArray jaTlvDictionary) {

    // Cycle thru JSON Array and inspect each JSON Object

    /*!!!!!!!DO NOT REFACTOR THIS!!!!!!!*/
    int iLength = jaTlvDictionary.length();
    /*!!!!!!!DO NOT REFACTOR THIS!!!!!!!*/

    for (int iJsonArrayIndex = 0; iJsonArrayIndex < iLength; iJsonArrayIndex++) {

      JSONObject joTlvDictionary = null;

      try {
        joTlvDictionary = jaTlvDictionary.getJSONObject(iJsonArrayIndex);
      } catch (JSONException e1) {
        e1.printStackTrace();
      }

      try {

        // Check to see if this object have SubTypes , if so go into SubType Array
        if (joTlvDictionary.getBoolean(Dictionary.ARE_SUBTYPES)) {

          // If this is the start of the array, I need only advance 2 bytes to the next Type
          loadTlvValuesIntoTlvDictionary(
              bTlvBuffer, joTlvDictionary.getJSONArray(Dictionary.SUBTYPE_ARRAY));

        } else {

          // If this is the start of the array, I need only advance 2 bytes to the next Type
          try {

            JSONObject joCheckForMultiSubTLVInstance =
                loadTlvValuesIntoTlvDictionary(bTlvBuffer, joTlvDictionary);

            if (joCheckForMultiSubTLVInstance.has(Dictionary.MULTI_SUB_TLV_INSTANCE)) {

              JSONArray ja_ =
                  joCheckForMultiSubTLVInstance.getJSONArray(Dictionary.MULTI_SUB_TLV_INSTANCE);

              for (int iIndex = 0; iIndex < ja_.length(); iIndex++) {

                jaTlvDictionary.put(new JSONObject(ja_.get(iIndex).toString()));
              }
            }

          } catch (TlvException e) {

            e.printStackTrace();
          }
        }

      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  /** Decode one bounded container instance; repeated children produce independent JSON nodes. */
  private JSONObject decodeBoundedInstance(byte[] encoded, JSONObject template)
      throws TlvException {
    if (encoded.length < 2 || Byte.toUnsignedInt(encoded[1]) != encoded.length - 2)
      throw new IllegalArgumentException("Invalid embedded TLV length");
    JSONObject result = new JSONObject(template.toString());
    if (!result.getBoolean(Dictionary.ARE_SUBTYPES)) {
      int size = encoded.length - 2;
      int minimum = result.getInt(Dictionary.LENGTH_MIN);
      int maximum = result.getInt(Dictionary.LENGTH_MAX);
      if (size < minimum || (maximum > 0 && size > maximum))
        throw new IllegalArgumentException("Invalid embedded scalar width");
      if (result.getString(Dictionary.DATA_TYPE).equals("byte_array_inet_transport_address")
          && size != 6
          && size != 18)
        throw new IllegalArgumentException("Transport address must contain 6 or 18 bytes");
      JSONArray parentPath = result.getJSONArray(Dictionary.PARENT_TYPE_LIST);
      result.put(
          Dictionary.PARENT_TYPE_LIST, new JSONArray().put(-1).put(result.getInt(Dictionary.TYPE)));
      loadTlvValuesIntoTlvDictionary(encoded, result);
      result.put(Dictionary.PARENT_TYPE_LIST, parentPath);
      return result;
    }
    JSONArray children = new JSONArray();
    for (int offset = 2; offset < encoded.length; ) {
      if (offset + 2 > encoded.length)
        throw new IllegalArgumentException("Truncated embedded TLV header");
      int type = Byte.toUnsignedInt(encoded[offset]);
      int end = offset + 2 + Byte.toUnsignedInt(encoded[offset + 1]);
      if (end > encoded.length)
        throw new IllegalArgumentException("Embedded TLV exceeds parent length");
      JSONObject child = null;
      for (Object entry : template.getJSONArray(Dictionary.SUBTYPE_ARRAY)) {
        JSONObject candidate = (JSONObject) entry;
        if (candidate.getInt(Dictionary.TYPE) == type) {
          child = candidate;
          break;
        }
      }
      if (child == null) throw new IllegalArgumentException("Unknown embedded child TLV " + type);
      children.put(
          decodeBoundedInstance(java.util.Arrays.copyOfRange(encoded, offset, end), child));
      offset = end;
    }
    result.put(Dictionary.SUBTYPE_ARRAY, children);
    return result;
  }

  private JSONObject loadTlvValuesIntoTlvDictionary(byte[] bTlvBuffer, JSONObject joTlvDictionary)
      throws TlvException {

    // Registry children belong to individual wire containers. In particular, repeated
    // coexistence address rows and ASF/HQoS entries must not share values or bounds.
    if (TLV_TYPE_DOCSIS.equals(getDictionaryTableName())
        && joTlvDictionary.getBoolean(Dictionary.ARE_SUBTYPES)) {
      return decodeBoundedInstance(bTlvBuffer, joTlvDictionary);
    }

    JSONArray jaMultiSubTlvInstance = null;

    try {

      if (joTlvDictionary.getBoolean(Dictionary.ARE_SUBTYPES)) {

        loadTlvValuesIntoTlvDictionary(bTlvBuffer, new JSONArray().put(joTlvDictionary));

      } else {

        // Get ParentTypeList Array
        byte[] bParentChildTlvEncodeList =
            toParentTypeListByteArray(joTlvDictionary.getJSONArray(Dictionary.PARENT_TYPE_LIST));

        // Get Specific TLV if available
        int[] bPCTEPointer;
        if (TLV_TYPE_DOCSIS_40.equals(getDictionaryTableName())) {
          bPCTEPointer =
              findDocsis40Offsets(
                  bTlvBuffer, joTlvDictionary.getJSONArray(Dictionary.PARENT_TYPE_LIST));
        } else {
          byte[] indices =
              TlvVariableBinding.findTLVIndex(
                  bTlvBuffer, bParentChildTlvEncodeList, joTlvDictionary.getInt("ByteLength"));
          bPCTEPointer = new int[indices.length];
          for (int index = 0; index < indices.length; index++)
            bPCTEPointer[index] = Byte.toUnsignedInt(indices[index]);
        }

        String hsPCTEPointer = java.util.Arrays.toString(bPCTEPointer);

        if (bPCTEPointer.length != 0) {

          /* Need to create a special case when there are 2 of the same TLV - Example TLV 43 Vendor Specific */
          for (int iIndex = 0; iIndex < bPCTEPointer.length; iIndex++) {

            // Get Value
            byte[] bValue =
                TlvBuilder.getTlvValue(
                    bTlvBuffer,
                    bPCTEPointer[iIndex],
                    joTlvDictionary.getInt(Dictionary.BYTE_LENGTH));

            // Determine the Data Type of Value
            String sDataType = joTlvDictionary.getString(Dictionary.DATA_TYPE);

            // Convert Value to Proper Data Type
            if (sDataType.equals("unsigned_integer")) {
              joTlvDictionary.put(Dictionary.VALUE, new java.math.BigInteger(1, bValue));
            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_INTEGER)) {

              // Get Value
              int iTlvValue = new HexString(bValue).toInteger();

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, iTlvValue);

            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_OID)) {

              // Need to see if this is a BER byte array
              // bValue = BERService.cleanBEROIDPrefix(bValue);

              // Get Value
              BEROIDConversion bocOID = new BEROIDConversion(bValue);

              JSONArray jaOID = new JSONArray();

              jaOID.put(bocOID.toMap());

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, jaOID);

            }

            /*Will need to come back and apply to all DataTypes - Fix Issue38*/
            else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY)) {

              // Get Value
              String sTlvValue = new HexString(bValue).toString(":");

              if (iIndex > 0) {

                /*Create Array At the first Instance */
                if (iIndex == 1) {
                  jaMultiSubTlvInstance = new JSONArray();
                }

                /*Create a new Object for the new TLV Instance */
                JSONObject joNextTLVInstance = new JSONObject(joTlvDictionary.toString());

                /*Add New Value */
                joNextTLVInstance.put(Dictionary.VALUE, sTlvValue);

                /*Update Object and Array*/

                jaMultiSubTlvInstance.put(joNextTLVInstance);

                if (iIndex == bPCTEPointer.length - 1) {
                  joTlvDictionary.put(Dictionary.MULTI_SUB_TLV_INSTANCE, jaMultiSubTlvInstance);
                }

              } else {
                // Insert Value into JSON Object
                joTlvDictionary.put(Dictionary.VALUE, sTlvValue);
              }

            } else if (sDataType.equals(
                DataTypeDictionaryReference.DATA_TYPE_STRING_NULL_TERMINATED)) {

              // Get Value
              String sTlvValueASCII =
                  new HexString(HexString.stripNullTerminatedString(bValue)).toASCII();

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValueASCII);

            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING)) {

              // Get Value
              String sTlvValueASCII = new HexString(bValue).toASCII();

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValueASCII);

            } else if (sDataType.equals(
                DataTypeDictionaryReference.DATA_TYPE_MULTI_TLV_BYTE_ARRAY)) {

              // Get Value
              String sTlvValue = new HexString(bValue).toString(":");

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(
                DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR)) {

              String sTlvValue = null;

              try {
                sTlvValue = DataTypeFormatConversion.byteArrayToIPv4TransportAddress(bValue);
              } catch (DataTypeFormatException e) {
                e.printStackTrace();
              }

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(
                DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR)) {

              String sTlvValue = null;

              try {
                sTlvValue = DataTypeFormatConversion.byteArrayToIPv6TransportAddress(bValue);
              } catch (DataTypeFormatException e) {
                e.printStackTrace();
              }

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(
                DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_INET_ADDR)) {

              String sTlvValue = null;

              try {
                sTlvValue = DataTypeFormatConversion.byteArrayToInetTransportAddress(bValue);
              } catch (DataTypeFormatException e) {
                e.printStackTrace();
              }

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_DOUBLE_BYTE_ARRAY)) {

              String sTlvValue = null;

              try {
                sTlvValue = DataTypeFormatConversion.doubleByteArray(bValue);
              } catch (IllegalArgumentException e) {
                e.printStackTrace();
              }

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_STRING_BITS)) {

              StringBuilder sbTlvValue = null;

              sbTlvValue = DataTypeFormatConversion.byteArrayBinaryBitMaskToString(bValue, 8);

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sbTlvValue.toString());

            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_MAC_ADDRESS)) {

              String sTlvValue = null;

              sTlvValue = DataTypeFormatConversion.byteArrayToMacAddressFormat(bValue);

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV4_ADDR)
                || sDataType.equals("byte_array_inet_address")) {

              String sTlvValue = null;

              sTlvValue = DataTypeFormatConversion.inetAddressToString(bValue);

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(
                DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV6_ADDR)) {

              String sTlvValue = null;

              sTlvValue = DataTypeFormatConversion.inetAddressToString(bValue);

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_BYTE)) {

              String sTlvValue = null;

              sTlvValue = new HexString(bValue).toString();

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_OID_ASN1_OBJECT_6)) {

              String sObjectOnlyHex =
                  "30" + HexString.toHexString(bValue.length) + new HexString(bValue).toString();

              HexString hsHexOID = new HexString(HexString.toByteArray(sObjectOnlyHex));

              BEROIDConversion bocOidAsnObj5 = new BEROIDConversion(hsHexOID.toByteArray());

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, bocOidAsnObj5.getOidDotNotaion());
            }
          }
        }
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return joTlvDictionary;
  }

  /**
   * @param jaParentTypeList
   * @return byte[]
   */
  private static int[] findDocsis40Offsets(byte[] bytes, JSONArray parents) {
    java.util.List<Integer> path = new java.util.ArrayList<>();
    for (int index = 0; index < parents.length(); index++) {
      int type = parents.getInt(index);
      if (type >= 0) path.add(type);
    }
    java.util.List<Integer> offsets = new java.util.ArrayList<>();
    findDocsis40Offsets(bytes, 0, bytes.length, path, 0, offsets);
    return offsets.stream().mapToInt(Integer::intValue).toArray();
  }

  private static void findDocsis40Offsets(
      byte[] bytes,
      int start,
      int end,
      java.util.List<Integer> path,
      int depth,
      java.util.List<Integer> offsets) {
    int offset = start;
    while (offset < end) {
      int type = Byte.toUnsignedInt(bytes[offset]);
      if (depth == 0 && type == 0) {
        offset++;
        continue;
      }
      if (depth == 0 && type == 255) return;
      java.util.List<Integer> current = new java.util.ArrayList<>(path.subList(0, depth));
      current.add(type);
      var definition = com.comcast.oscar.dictionary.TlvRegistry.forFamily("docsis40").find(current);
      int width = definition == null ? 1 : definition.lengthBytes();
      if (width > end - offset - 1)
        throw new IllegalArgumentException("Truncated D4.0 header at " + current);
      int length = 0;
      for (int index = 1; index <= width; index++)
        length = (length << 8) | Byte.toUnsignedInt(bytes[offset + index]);
      int valueStart = offset + 1 + width;
      if (length > end - valueStart)
        throw new IllegalArgumentException("Truncated D4.0 value at " + current);
      if (type == path.get(depth)) {
        if (depth == path.size() - 1) offsets.add(offset);
        else findDocsis40Offsets(bytes, valueStart, valueStart + length, path, depth + 1, offsets);
      }
      offset = valueStart + length;
    }
  }

  private byte[] toParentTypeListByteArray(JSONArray jaParentTypeList) {

    ByteArrayOutputStream baosParentTypeList = new ByteArrayOutputStream();

    int iParentTypeListIndex = 0;

    try {

      // Check to see the first entry is a -1, this indicate the start of the Major TLV
      // Then increment
      if (jaParentTypeList.getInt(iParentTypeListIndex) == -1)

        // Cycle Thru all the elements and add them to the byte array output stream
        while (++iParentTypeListIndex < jaParentTypeList.length())

          // Add to output stream
          baosParentTypeList.write(jaParentTypeList.getInt(iParentTypeListIndex));

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return baosParentTypeList.toByteArray();
  }
}
