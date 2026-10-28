/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import com.comcast.oscar.ber.BEROIDConversion;
import com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes;
import com.comcast.oscar.compiler.PacketCableConstants;
import com.comcast.oscar.datatype.DataTypeDictionaryReference;
import com.comcast.oscar.datatype.DataTypeFormatConversion;
import com.comcast.oscar.datatype.DataTypeFormatException;
import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Decodes TLV instances using an explicitly selected registry; does not authenticate MICs. */
public class TlvDisassemble extends TlvDictionary {
  private static final int ROOT_PATH_MARKER = -1;

  // Diagnostics never include provisioning payload bytes.
  private static final Logger logger = LogManager.getLogger(TlvDisassemble.class);

  private TlvBuilder tlvBuffer = null;

  private TlvVariableBinding variableBinding = null;

  public static final String TLV_TYPE_DOCSIS = TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME;
  public static final String TLV_TYPE_DOCSIS_40 = TlvDictionary.DOCSIS_40_DICTIONARY_TABLE_NAME;
  public static final String TLV_TYPE_PACKET_CABLE =
      TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME;
  public static final String TLV_TYPE_DPOG = TlvDictionary.DPOG_DICTIONARY_TABLE_NAME;
  public static final String TLV_TYPE_DPOE = TlvDictionary.DPOE_DICTIONARY_TABLE_NAME;

  /**
   * Retains a builder for decoding with the selected dictionary; no file is read or written.
   *
   * @param tlvBuffer non-null encoded builder; caller must not mutate it during decoding
   * @param dictionaryTable registry table name, e.g. TLV_TYPE_DOCSIS
   * @throws NullPointerException if the builder or table is null
   */
  public TlvDisassemble(TlvBuilder tlvBuffer, String dictionaryTable) {
    this.tlvBuffer = Objects.requireNonNull(tlvBuffer, "tlvBuffer");
    super.updateDictionaryTablename(Objects.requireNonNull(dictionaryTable, "dictionaryTable"));
  }

  /**
   * Retains a variable binding, including its length-field widths, for deferred decoding.
   *
   * @param variableBinding non-null encoded binding; caller must not mutate it during decoding
   * @param dictionaryTable registry table name, e.g. TLV_TYPE_DOCSIS
   * @throws NullPointerException if the binding or table is null
   */
  public TlvDisassemble(TlvVariableBinding variableBinding, String dictionaryTable) {
    this.variableBinding = Objects.requireNonNull(variableBinding, "variableBinding");
    super.updateDictionaryTablename(Objects.requireNonNull(dictionaryTable, "dictionaryTable"));
  }

  /**
   * Reads a complete binary file into a private builder and selects its decoding dictionary. The
   * input file is not modified; failed reads do not leave a partially initialized decoder.
   *
   * @param configurationFile non-null readable binary file
   * @param dictionaryTable registry table name, e.g. TLV_TYPE_DOCSIS
   * @throws IllegalArgumentException if the file cannot be read or represented as a TLV stream
   * @throws NullPointerException if the file or table is null
   */
  public TlvDisassemble(File configurationFile, String dictionaryTable) {
    Objects.requireNonNull(configurationFile, "configurationFile");
    this.tlvBuffer = new TlvBuilder();
    try {
      tlvBuffer.add(new HexString(Files.readAllBytes(configurationFile.toPath())));
    } catch (IOException | TlvException exception) {
      throw new IllegalArgumentException("Cannot read binary configuration", exception);
    }
    super.updateDictionaryTablename(Objects.requireNonNull(dictionaryTable, "dictionaryTable"));
  }

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
    Objects.requireNonNull(configurationBytes, "configurationBytes");
    Objects.requireNonNull(options, "options");
    if (configurationBytes.length > options.maxBytes()) {
      throw new IllegalArgumentException("Diagnostic input exceeds byte limit");
    }
    return new TlvDiagnosticWalker(configurationBytes.clone(), options).inspect();
  }

  /**
   * Decodes stored TLVs into independent dictionary instances with values and numeric paths. DOCSIS
   * vendor children use VendorId-scoped metadata. This does not validate a complete provisioning
   * profile or authenticate MICs, and does not write files.
   *
   * @return decoded instance array; legacy family paths can omit unsupported entries
   * @throws IllegalArgumentException if bounded DOCSIS/DPoG framing or values are malformed
   */
  public JSONArray getTlvDictionary() {
    if (TLV_TYPE_DPOG.equals(getDictionaryTableName())) {
      byte[] binary = tlvBuffer != null ? tlvBuffer.toByteArray() : variableBinding.toByteArray();
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
    if (tlvBuffer != null) {

      liTlvBuffer = tlvBuffer.getTopLevelTlvList(getTopLevelByteLength());

    } else if (variableBinding != null) {

      liTlvBuffer = variableBinding.getTopLevelTlvList();
    }

    // Get Major TLV from tlvBuffer via Map Type -> Byte Length
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
      if (variableBinding != null) {

        miiTypeByteLength.put(iType, variableBinding.getMapTypeToByteLength().get(iType));

        // fetch Major TLV and store in BASO
        try {
          boasTlvBufferTemp.write(
              TlvBuilder.fetchTlv(
                  new ArrayList<Integer>(Arrays.asList(iType)), // Contains Type
                  miiTypeByteLength, // Contains Type -> ByteLength
                  variableBinding.toByteArray())); // Contains TLV ByteArray
        } catch (IOException e) {
          logger.error("Unable to decode a configuration field; inspect the input structure");
        }

        try {

          // Update ByteLength for this TLV
          try {
            joTlvDictionary.put(Dictionary.BYTE_LENGTH, miiTypeByteLength.get(iType));
          } catch (JSONException e) {
            logger.error("Unable to decode a configuration field; inspect the input structure");
          }

          // Load JSON Object
          jaTlvDictionary.put(
              loadTlvValuesIntoTlvDictionary(variableBinding.toByteArray(), joTlvDictionary));

        } catch (TlvException e) {
          logger.error("Unable to decode a configuration field; inspect the input structure");
        }

      } else if (tlvBuffer != null) {

        // Map to hold TYPE to Byte Length
        try {
          miiTypeByteLength.put(
              joTlvDictionary.getInt(Dictionary.TYPE),
              joTlvDictionary.getInt(Dictionary.BYTE_LENGTH));
        } catch (JSONException e1) {
          logger.error("Unable to decode a configuration field; inspect the input structure");
        }

        // fetch Major TLV and store in BASO
        try {
          boasTlvBufferTemp.write(
              TlvBuilder.fetchTlv(
                  new ArrayList<Integer>(Arrays.asList(iType)), // Contains Type
                  miiTypeByteLength, // Contains Type -> ByteLength
                  tlvBuffer.toByteArray())); // Contains TLV ByteArray
        } catch (IOException e) {
          logger.error("Unable to decode a configuration field; inspect the input structure");
        }

        try {
          jaTlvDictionary.put(
              loadTlvValuesIntoTlvDictionary(boasTlvBufferTemp.toByteArray(), joTlvDictionary));
        } catch (TlvException e) {
          logger.error("Unable to decode a configuration field; inspect the input structure");
        }
      }
    }

    return jaTlvDictionary;
  }

  /**
   * Groups builder-backed input by top-level type using the existing builder ordering.
   *
   * @return encoded groups; this is not a wire-order-preserving export
   * @throws NullPointerException if constructed from a variable binding rather than a builder/file
   */
  public List<byte[]> toListByteArray() {
    return tlvBuffer.sortByTopLevelTlv();
  }

  /**
   * Returns decoded top-level instances matching a numeric wire type, without changing input.
   *
   * @param iTlvType wire identifier to select, e.g. 3 for NetworkAccess
   * @return matching JSON instances, or an empty array if absent
   * @throws IllegalArgumentException if decoding encounters malformed bounded data
   */
  public JSONArray getTopLevelTLVJSON(int iTlvType) {
    JSONArray jaTLV = new JSONArray();

    JSONArray jaTlvDictionary = getTlvDictionary();

    JSONObject joTlvDictionary = null;

    for (int iIndex = 0; iIndex < jaTlvDictionary.length(); iIndex++) {

      try {
        joTlvDictionary = jaTlvDictionary.getJSONObject(iIndex);
      } catch (JSONException e) {
        logger.error("Unable to decode a configuration field; inspect the input structure");
      }

      try {

        if (joTlvDictionary.getInt(Dictionary.TYPE) == iTlvType) {
          jaTLV.put(joTlvDictionary);
        }

      } catch (JSONException e) {
        logger.error("Unable to decode a configuration field; inspect the input structure");
      }
    }

    return jaTLV;
  }

  /**
   * Applies the legacy first-byte PacketCable-versus-DOCSIS heuristic to a file. This does not
   * identify a DOCSIS revision or prove configuration validity.
   *
   * @param fConfiguraitonFile non-null readable, nonempty binary file; not modified
   * @return PacketCable table for its file marker, otherwise the DOCSIS table
   * @throws IndexOutOfBoundsException if the legacy reader returns an empty buffer
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
        logger.error("Unable to decode a configuration field; inspect the input structure");
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

            logger.error("Unable to decode a configuration field; inspect the input structure");
          }
        }

      } catch (JSONException e) {
        logger.error("Unable to decode a configuration field; inspect the input structure");
      }
    }
  }

  /** Decode one bounded container instance; repeated children produce independent JSON nodes. */
  private JSONObject decodeBoundedInstance(byte[] encoded, JSONObject template)
      throws TlvException {
    int lengthBytes = template.getInt(Dictionary.BYTE_LENGTH);
    int headerBytes = TlvBuilder.TLV_TYPE_OVERHEAD + lengthBytes;
    if (encoded.length < headerBytes) {
      throw new IllegalArgumentException("Truncated container header");
    }
    int payloadLength = 0;
    for (int index = 1; index < headerBytes; index++) {
      payloadLength = (payloadLength << Byte.SIZE) | Byte.toUnsignedInt(encoded[index]);
    }
    if (payloadLength != encoded.length - headerBytes) {
      throw new IllegalArgumentException("Invalid container TLV length");
    }
    JSONObject result = new JSONObject(template.toString());
    if (!result.getBoolean(Dictionary.ARE_SUBTYPES)) {
      if (DocsisVendorExtension.applies(result)) {
        DocsisVendorExtension.validatePayload(
            Arrays.copyOfRange(encoded, headerBytes, encoded.length));
      }
      int size = encoded.length - headerBytes;
      int minimum = result.getInt(Dictionary.LENGTH_MIN);
      int maximum = result.getInt(Dictionary.LENGTH_MAX);
      if (size < minimum || (maximum > 0 && size > maximum)) {
        throw new IllegalArgumentException("Invalid embedded scalar width");
      }
      if (result.getString(Dictionary.DATA_TYPE).equals("byte_array_inet_transport_address")
          && size != DataTypeFormatConversion.IPV4_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH
          && size != DataTypeFormatConversion.IPV6_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH) {
        throw new IllegalArgumentException("Transport address must contain 6 or 18 bytes");
      }
      JSONArray parentPath = result.getJSONArray(Dictionary.PARENT_TYPE_LIST);
      result.put(
          Dictionary.PARENT_TYPE_LIST,
          new JSONArray().put(ROOT_PATH_MARKER).put(result.getInt(Dictionary.TYPE)));
      loadTlvValuesIntoTlvDictionary(encoded, result);
      result.put(Dictionary.PARENT_TYPE_LIST, parentPath);
      return result;
    }
    boolean extension = DocsisVendorExtension.applies(template);
    boolean general = !extension || DocsisVendorExtension.isGeneral(encoded);
    JSONArray children = new JSONArray();
    for (int offset = headerBytes; offset < encoded.length; ) {
      if (offset + TlvBuilder.TLV_TYPE_LENGTH_OVERHEAD > encoded.length) {
        throw new IllegalArgumentException("Truncated embedded TLV header");
      }
      int type = Byte.toUnsignedInt(encoded[offset]);
      int end =
          offset + TlvBuilder.TLV_TYPE_LENGTH_OVERHEAD + Byte.toUnsignedInt(encoded[offset + 1]);
      if (end > encoded.length) {
        throw new IllegalArgumentException("Embedded TLV exceeds parent length");
      }
      JSONObject child = null;
      for (Object entry : template.getJSONArray(Dictionary.SUBTYPE_ARRAY)) {
        JSONObject candidate = (JSONObject) entry;
        if (candidate.getInt(Dictionary.TYPE) == type) {
          child = candidate;
          break;
        }
      }
      if (extension
          && type == com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes.VENDOR_ID_ENC
          && offset != headerBytes) {
        throw new IllegalArgumentException("Duplicate VendorId in DOCSIS extension");
      }
      if (extension
          && type != com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes.VENDOR_ID_ENC
          && (!general || child == null)) {
        child = DocsisVendorExtension.rawDefinition(template, type);
        child.put(
            Dictionary.VALUE,
            HexFormat.ofDelimiter(":")
                .formatHex(encoded, offset + TlvBuilder.TLV_TYPE_LENGTH_OVERHEAD, end));
        children.put(child);
        offset = end;
        continue;
      }
      if (child == null) {
        throw new IllegalArgumentException("Unknown embedded child TLV " + type);
      }
      children.put(decodeBoundedInstance(Arrays.copyOfRange(encoded, offset, end), child));
      offset = end;
    }
    result.put(Dictionary.SUBTYPE_ARRAY, children);
    return result;
  }

  private JSONObject loadTlvValuesIntoTlvDictionary(byte[] bTlvBuffer, JSONObject joTlvDictionary)
      throws TlvException {

    // Registry children belong to individual wire containers. In particular, repeated
    // coexistence address rows and ASF/HQoS entries must not share values or bounds.
    if ((TLV_TYPE_DOCSIS.equals(getDictionaryTableName())
            || TLV_TYPE_DOCSIS_40.equals(getDictionaryTableName()))
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
                  bTlvBuffer,
                  bParentChildTlvEncodeList,
                  joTlvDictionary.getInt(Dictionary.BYTE_LENGTH));
          bPCTEPointer = new int[indices.length];
          for (int index = 0; index < indices.length; index++) {
            bPCTEPointer[index] = Byte.toUnsignedInt(indices[index]);
          }
        }

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
              joTlvDictionary.put(Dictionary.VALUE, new BigInteger(1, bValue));
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
                logger.error("Unable to decode a configuration field; inspect the input structure");
              }

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(
                DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR)) {

              String sTlvValue = null;

              try {
                sTlvValue = DataTypeFormatConversion.byteArrayToIPv6TransportAddress(bValue);
              } catch (DataTypeFormatException e) {
                logger.error("Unable to decode a configuration field; inspect the input structure");
              }

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(
                DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_INET_ADDR)) {

              String sTlvValue = null;

              try {
                sTlvValue = DataTypeFormatConversion.byteArrayToInetTransportAddress(bValue);
              } catch (DataTypeFormatException e) {
                logger.error("Unable to decode a configuration field; inspect the input structure");
              }

              // Insert Value into JSON Object
              joTlvDictionary.put(Dictionary.VALUE, sTlvValue);

            } else if (sDataType.equals(DataTypeDictionaryReference.DATA_TYPE_DOUBLE_BYTE_ARRAY)) {

              String sTlvValue = null;

              try {
                sTlvValue = DataTypeFormatConversion.doubleByteArray(bValue);
              } catch (IllegalArgumentException e) {
                logger.error("Unable to decode a configuration field; inspect the input structure");
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
      logger.error("Unable to decode a configuration field; inspect the input structure");
    }

    return joTlvDictionary;
  }

  /** Finds absolute offsets for a registry path without crossing enclosing container bounds. */
  private static int[] findDocsis40Offsets(byte[] bytes, JSONArray parents) {
    List<Integer> path = new ArrayList<>();
    for (int index = 0; index < parents.length(); index++) {
      int type = parents.getInt(index);
      if (type >= 0) {
        path.add(type);
      }
    }
    List<Integer> offsets = new ArrayList<>();
    findDocsis40Offsets(bytes, 0, bytes.length, path, 0, offsets);
    return offsets.stream().mapToInt(Integer::intValue).toArray();
  }

  private static void findDocsis40Offsets(
      byte[] bytes, int start, int end, List<Integer> path, int depth, List<Integer> offsets) {
    int offset = start;
    while (offset < end) {
      int type = Byte.toUnsignedInt(bytes[offset]);
      if (depth == 0 && type == DocsisTlvTypes.PAD) {
        offset++;
        continue;
      }
      if (depth == 0 && type == DocsisTlvTypes.END_OF_FILE) {
        return;
      }
      List<Integer> current = new ArrayList<>(path.subList(0, depth));
      current.add(type);
      var definition = TlvRegistry.forFamily(TlvRegistry.DOCSIS_40_FAMILY).find(current);
      int width = definition == null ? TlvBuilder.TLV_ONE_BYTE_LENGTH : definition.lengthBytes();
      if (width > end - offset - 1) {
        throw new IllegalArgumentException("Truncated D4.0 header at " + current);
      }
      int length = 0;
      for (int index = 1; index <= width; index++) {
        length = (length << Byte.SIZE) | Byte.toUnsignedInt(bytes[offset + index]);
      }
      int valueStart = offset + 1 + width;
      if (length > end - valueStart) {
        throw new IllegalArgumentException("Truncated D4.0 value at " + current);
      }
      if (type == path.get(depth)) {
        if (depth == path.size() - 1) {
          offsets.add(offset);
        } else {
          findDocsis40Offsets(bytes, valueStart, valueStart + length, path, depth + 1, offsets);
        }
      }
      offset = valueStart + length;
    }
  }

  private byte[] toParentTypeListByteArray(JSONArray jaParentTypeList) {
    ByteArrayOutputStream baosParentTypeList = new ByteArrayOutputStream();

    int iParentTypeListIndex = 0;

    try {

      // The registry's root sentinel is metadata, not a wire type.
      if (jaParentTypeList.getInt(iParentTypeListIndex) == ROOT_PATH_MARKER) {
        while (++iParentTypeListIndex < jaParentTypeList.length()) {
          baosParentTypeList.write(jaParentTypeList.getInt(iParentTypeListIndex));
        }
      }

    } catch (JSONException e) {
      logger.error("Unable to decode a configuration field; inspect the input structure");
    }

    return baosParentTypeList.toByteArray();
  }
}
