/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes;
import com.comcast.oscar.compiler.PacketCableConstants;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.snmp4j.asn1.BER;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

public class TlvBuilder implements TlvBuild {

  private static final int ZERO_BYTE_LENGTH = 0;

  private static final int ONE_BYTE_LENGTH = 1;

  protected List<String> lsTlvBuffer = new ArrayList<String>();
  protected byte[] baTlvBuffer = null;
  protected List<byte[]> lbTlvBuilder = new ArrayList<byte[]>();
  protected Map<Integer, Integer> miiTlvTypeTpByteLength = new HashMap<Integer, Integer>();

  public static final byte TLV_LENGTH_POS_OFFSET = 0x01;
  public static final byte TLV_TYPE_OVERHEAD = 0x01;
  public static final byte TLV_TYPE_LENGTH_OVERHEAD = (TLV_LENGTH_POS_OFFSET + TLV_TYPE_OVERHEAD);

  /** Largest unsigned one-byte TLV type, including reserved marker types. */
  public static final int MAX_TLV_TYPE = 255;

  public static final int MAX_TLV_LENGTH = 254;
  public static final int END_OF_BYTE_ARRAY_INDEX = -1;
  public static final byte TLV_ONE_BYTE_LENGTH = 0x01;

  /**
   * Main Constructor TlvBuilder creates Type Length Value based on the Basic Encoding Rules for 1
   * Byte length or 255 Byte Value
   *
   * <p>Type: 3 , Length: 1 , Value: 1 -> 03:01:01 Type: 4 , Length: 3 , Value: 01:01:01 or 0x15 or
   * DEC:21 -> 41:03:01:01:01
   */
  public TlvBuilder() {
    super();
  }

  /**
   * @param cfi - ConfigurationFileImport
   */
  public void add(ConfigurationFileImport cfi) {

    try {
      add(new HexString(cfi.toByteArray()));
    } catch (TlvException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method assumes that each Type is of 1 byte Length
   *
   * @param hsObject Add HexString object, will throw exception if Bytes are not of a TLV Construct
   * @throws TlvException
   */
  public void add(HexString hsObject) throws TlvException {

    if (hsObject == null) {
      throw new NullPointerException("HexString.add(hs): hsObject is a NULL");
    }

    // Make sure that TLV Variable is of proper Length
    if (isTLVConstruct(hsObject)) {
      lsTlvBuffer.add(hsObject.toString());
      bTlvBuilder.add(hsObject.toByteArray());
    } else {
      throw new TlvException("HexString Object not in a TLV Format\n" + hsObject.toString(":"));
    }
  }

  /**
   * @param iTlvType Type integer 0>Type<256
   * @param iValue
   * @throws TlvException
   */
  public void add(int iTlvType, Integer iValue) throws TlvException {

    if (iTlvType < 0) throw new TlvException("TlvBuilder.add() Type Less than 0");

    if (iTlvType > MAX_TLV_TYPE) throw new TlvException("TLV type must not exceed " + MAX_TLV_TYPE);

    String sValue = HexString.toHexString(iValue);

    lsTlvBuffer.add(
        HexString.toHexString(iTlvType)
            + HexString.toHexString(HexString.getHexByteLength(sValue))
            + sValue);

    updateMapTypeToByteLength(iTlvType, ONE_BYTE_LENGTH);
  }

  /**
   * @param iTlvType Type integer 0>Type<256
   * @param hsValue = HexString of the Value, length is automatically calculated
   * @throws TlvException
   */
  public void add(int iTlvType, HexString hsValue) throws TlvException {

    if (iTlvType < 0) throw new TlvException("TlvBuilder.add() Type Must be greater than 0");

    if (iTlvType > MAX_TLV_TYPE) throw new TlvException("TLV type must not exceed " + MAX_TLV_TYPE);

    String sValue = hsValue.hexCompressed();

    lsTlvBuffer.add(
        HexString.toHexString(iTlvType)
            + HexString.toHexString(HexString.getHexByteLength(sValue))
            + sValue);

    updateMapTypeToByteLength(iTlvType, ONE_BYTE_LENGTH);
  }

  /**
   * @param tbObject TlvBuilder Object
   */
  public void add(TlvBuilder tbObject) {

    lsTlvBuffer.add(tbObject.toString());

    updateMapTypeToByteLength(tbObject);
  }

  /**
   * Groups one-byte-length top-level TLVs by ascending or descending type.
   *
   * <p>Preserves encounter order within each repeated type and does not mutate the source. Retains
   * the legacy type range 1 through 254, excluding padding and end markers. This helper does not
   * regenerate integrity fields and must not be used to reorder a finalized signed configuration.
   * For example, types 3, 1, 3 become 1, 3, 3 ascending.
   *
   * @param ascending true for ascending type order, false for descending
   * @param source non-null builder containing well-formed one-byte-length TLVs
   * @return independent builder containing the reordered bytes
   * @throws TlvException if the reordered bytes cannot be added to the result
   */
  public static TlvBuilder reorderTLV(boolean ascending, TlvBuilder source) throws TlvException {
    List<Integer> orderedTypes = new ArrayList<>();
    for (int tlvType = 1; tlvType < MAX_TLV_TYPE; tlvType++) {
      orderedTypes.add(tlvType);
    }
    if (!ascending) {
      java.util.Collections.reverse(orderedTypes);
    }
    TlvBuilder reordered = new TlvBuilder();
    reordered.add(new HexString(fetchTlv(orderedTypes, source.toByteArray())));
    return reordered;
  }

  /**
   * @param iTlvType - Must be 0 - 255
   * @param bValue byte array containing a value of 1 byte or greater
   * @throws TlvException
   * @see com.comcast.oscar.tlv.TlvBuild#add(int, byte[])
   */
  public void add(int iTlvType, byte[] bValue) throws TlvException {

    if (iTlvType < 0) throw new TlvException("TlvBuilder.add() Type Less than 0");

    if (bValue == null) throw new TlvException("TlvBuilder.add() Byte Array is NULL");

    if (iTlvType > MAX_TLV_TYPE) throw new TlvException("TLV type must not exceed " + MAX_TLV_TYPE);

    // Set the index to 0
    int iIndex = 0;

    // if the length is more than MAX_TLV_LENGTH=254, need to segment the TLV
    while ((bValue.length - iIndex) > MAX_TLV_LENGTH) {

      // Create BAOS
      ByteArrayOutputStream baosTopLevelTlvList = new ByteArrayOutputStream();

      // Write to BAOS
      baosTopLevelTlvList.write(bValue, iIndex, MAX_TLV_LENGTH);

      // Add segmented TLV
      add(iTlvType, new HexString(baosTopLevelTlvList.toByteArray()));

      // Advance pointer to the next 255 position
      iIndex += MAX_TLV_LENGTH;
    }

    // if there are no more bytes, skip this part
    if ((bValue.length - iIndex) != 0) {
      // Create BAOS
      ByteArrayOutputStream baosTopLevelTlvList = new ByteArrayOutputStream();

      // Write to BAOS
      baosTopLevelTlvList.write(bValue, iIndex, (bValue.length - iIndex));

      // Add last segmented TLV
      add(iTlvType, new HexString(baosTopLevelTlvList.toByteArray()));
    }

    updateMapTypeToByteLength(iTlvType, ONE_BYTE_LENGTH);
  }

  /**
   * @param iTlvType Must be 0>Type<256
   * @param bValue byte array containing a value of 1 byte or greater
   * @param booleanStripExistingTlv = True = Strip Existing TLV ; False = Do Not Strip TLV
   * @throws TlvException
   */
  public void add(int iTlvType, byte[] bValue, boolean booleanStripExistingTlv)
      throws TlvException {

    // Strip Incoming TLV
    if (booleanStripExistingTlv) {

      // Without Striped TLV
      byte[] baNewTLV = stripTlv(iTlvType, toByteArray());

      // Clear class Buffer
      clear();

      // ReAdd Buffer
      add(new HexString(baNewTLV));
    }

    // Add New TLV
    add(iTlvType, bValue);
  }

  /**
   * Appends a numeric SNMP VarBind without changing the requested TLV type.
   *
   * @param snmpTlvType Snmp11 or Snmp64 wire type; selects one or two length bytes
   * @param objectId non-null valid SNMP OID; never modified
   * @param berDataType INTEGER, COUNTER32, COUNTER64, GAUGE32 or TIMETICKS
   * @param number signed Integer32, unsigned 32-bit value, or Counter64 raw long bits
   * @throws IllegalArgumentException if inputs or encoded size are invalid; builder is unchanged
   */
  public void add(int snmpTlvType, OID objectId, byte berDataType, long number) {
    Variable value;
    if (berDataType == BER.INTEGER) {
      if (number < Integer.MIN_VALUE || number > Integer.MAX_VALUE) {
        throw new IllegalArgumentException("SNMP Integer32 is outside its signed range");
      }
      value = new Integer32((int) number);
    } else if (berDataType == BER.COUNTER64) {
      value = new Counter64(number);
    } else {
      if (number < 0 || number > Integer.toUnsignedLong(-1)) {
        throw new IllegalArgumentException("SNMP value is outside its unsigned 32-bit range");
      }
      value =
          switch (berDataType) {
            case BER.COUNTER32 -> new Counter32(number);
            case BER.GAUGE32 -> new Gauge32(number);
            case BER.TIMETICKS -> new TimeTicks(number);
            default -> throw new IllegalArgumentException("Unsupported numeric SNMP BER type");
          };
    }
    appendSnmpBinding(snmpTlvType, objectId, value);
  }

  /**
   * Appends a text/IP SNMP VarBind, counting the complete encoded BER bytes rather than characters.
   * For example, Snmp64 always emits a two-byte length even for a one-character value.
   *
   * @param snmpTlvType Snmp11 or Snmp64 wire type; never automatically promoted
   * @param objectId non-null valid SNMP OID; never modified
   * @param berDataType OCTETSTRING or IPADDRESS
   * @param value non-null text using SNMP4J's OctetString encoding or IPv4 address syntax
   * @throws IllegalArgumentException if inputs or encoded size are invalid; builder is unchanged
   */
  public void add(int snmpTlvType, OID objectId, byte berDataType, String value) {
    if (value == null) throw new IllegalArgumentException("SNMP value must not be null");
    Variable variable =
        switch (berDataType) {
          case BER.OCTETSTRING -> new OctetString(value);
          case BER.IPADDRESS -> new IpAddress(value);
          default -> throw new IllegalArgumentException("Unsupported text SNMP BER type");
        };
    appendSnmpBinding(snmpTlvType, objectId, variable);
  }

  /**
   * Appends an octet-string VarBind without splitting or modifying the supplied bytes.
   *
   * @param snmpTlvType Snmp11 or Snmp64 wire type; selects one or two length bytes
   * @param objectId non-null valid SNMP OID; never modified
   * @param berDataType must be OCTETSTRING
   * @param value non-null octets, including empty arrays
   * @throws IllegalArgumentException if inputs or encoded size are invalid; builder is unchanged
   */
  public void add(int snmpTlvType, OID objectId, byte berDataType, byte[] value) {
    if (value == null) throw new IllegalArgumentException("SNMP value must not be null");
    if (berDataType != BER.OCTETSTRING) {
      throw new IllegalArgumentException("Byte-array SNMP values require OCTETSTRING");
    }
    appendSnmpBinding(snmpTlvType, objectId, new OctetString(value));
  }

  // Encode and validate locally before committing bytes or length metadata to this builder.
  private void appendSnmpBinding(int snmpTlvType, OID objectId, Variable value) {
    int lengthWidth =
        switch (snmpTlvType) {
          case PacketCableConstants.SNMP_TLV_11 ->
              PacketCableConstants.PACKET_CABLE_STANDARD_BYTE_LENGTH;
          case PacketCableConstants.SNMP_TLV_64 -> PacketCableConstants.SNMP_TLV_64_BYTE_LENGTH;
          default -> throw new IllegalArgumentException("SNMP TLV type must be Snmp11 or Snmp64");
        };
    if (objectId == null || !objectId.isValid()) {
      throw new IllegalArgumentException("A valid SNMP OID is required");
    }
    var encoded = new ByteArrayOutputStream();
    try {
      new VariableBinding(objectId, value).encodeBER(encoded);
    } catch (IOException exception) {
      throw new IllegalArgumentException("Unable to encode SNMP VarBind");
    }
    byte[] payload = encoded.toByteArray();
    int maximumLength = (1 << (Byte.SIZE * lengthWidth)) - 1;
    if (payload.length > maximumLength) {
      throw new IllegalArgumentException("SNMP VarBind exceeds the selected TLV length capacity");
    }
    var entry = new ByteArrayOutputStream();
    entry.write(snmpTlvType);
    for (int shift = Byte.SIZE * (lengthWidth - 1); shift >= 0; shift -= Byte.SIZE) {
      entry.write(payload.length >>> shift);
    }
    entry.writeBytes(payload);
    lsTlvBuffer.add(new HexString(entry.toByteArray()).hexCompressed());
    updateMapTypeToByteLength(snmpTlvType, lengthWidth);
  }

  /**
   * This option is used when TLVs are greater than 1 byte Length or a length of 254 bytes
   *
   * @param tvbObject TlvVariableBinding Object
   */
  public void add(TlvVariableBinding tvbObject) {

    this.lsTlvBuffer.add(new HexString(tvbObject.toByteArray()).toString());

    this.miiTlvTypeTpByteLength.putAll(tvbObject.getMapTypeToByteLength());

    this.lbTlvBuilder.addAll(tvbObject.getByteListTlvBuffer());
  }

  /**
   * @return String if a List Array String Style of the buffer in Hex
   */
  public String toStringArray() {
    return lsTlvBuffer.toString();
  }

  /**
   * @return String Style of the buffer in Hex
   */
  @Override
  public String toString() {
    return lsTlvBuffer.toString().replaceAll("\\[|\\]|\\,|\\s", "");
  }

  /**
   * @param sSeperation
   * @return String Style of the buffer in Hex with a defined separation character
   */
  public String toStringSeperation(String sSeperation) {
    return new HexString(toByteArray()).toString(sSeperation);
  }

  /**
   * @return int length of buffer in bytes
   */
  public int length() {
    return toByteArray().length;
  }

  /**
   * @param iTlvType Must be 0 thru 255
   *     <p>Encapsulate a TLV with the current TLV in the Buffer
   *     <p>Example: iTlvType = 1
   *     <p>TLV 030101 -> 0103030101
   * @throws TlvException
   */
  public void encapsulateTlv(int iTlvType) throws TlvException {

    if (iTlvType < 0) throw new TlvException("TlvBuilder.encapsulateTlv() Type Less than 0");

    if (iTlvType > MAX_TLV_TYPE) throw new TlvException("TLV type must not exceed " + MAX_TLV_TYPE);

    lsTlvBuffer.add(0, HexString.toHexString(iTlvType) + HexString.toHexString(length()));
  }

  /**
   * @param iTlvType Must be 0 thru 255
   * @param iLength
   *     <p>Encapsulate a TLV with the current TLV in the Buffer
   *     <p>Example: iTlvType = 1 iLength = 3 TLV 030101 -> 0103030101
   * @throws TlvException
   */
  public void encapsulateTlv(int iTlvType, int iLength) throws TlvException {

    if (iTlvType < 0) throw new TlvException("TlvBuilder.encapsulateTlv() Type Less than 0");

    if (iLength < 0) throw new TlvException("TlvBuilder.encapsulateTlv() Length is less than 0");

    if (iTlvType > MAX_TLV_TYPE) throw new TlvException("TLV type must not exceed " + MAX_TLV_TYPE);

    lsTlvBuffer.add(0, HexString.toHexString(iTlvType) + HexString.toHexString(iLength));
  }

  /**
   * @see com.comcast.oscar.tlv.TlvBuild#clear()
   *     <p>Clear buffer
   */
  public void clear() {
    lsTlvBuffer.clear();
  }

  /**
   * @return TLV in a new byte array, not a pointer to buffer
   */
  public byte[] toByteArray() {
    return HexString.toByteArray(this.toString());
  }

  /**
   * @return a list of TopLevel TLV * @see com.comcast.oscar.tlv.TlvBuild#getTopLevelTlvList()
   */
  public List<Integer> getTopLevelTlvList() {

    List<Integer> liTopLevelTlvList = new ArrayList<Integer>();

    byte[] bTlvBuffer = toByteArray();

    // Cycle thru the Byte Array to find the TopLevel TLV's
    for (int iIndex = 0; iIndex < toByteArray().length; ) {

      // When you reach EOF, add to list and break out of loop
      if (BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex])
          == DocsisTlvTypes.END_OF_FILE) {
        liTopLevelTlvList.add(BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]));
        break;
      }
      // If you see padding, advance one more index till you see a TYPE
      else if (BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]) == Constants.PAD) {
        iIndex++;
        continue;
      }

      // Add TYPE to List
      liTopLevelTlvList.add(BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]));

      // Point to next TLV TYPE
      iIndex +=
          (BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex + TLV_LENGTH_POS_OFFSET]))
              + TLV_TYPE_LENGTH_OVERHEAD;
    }

    return liTopLevelTlvList;
  }

  /**
   * @param miiTypeByteLength -
   * @return List<Integer>
   * @see com.comcast.oscar.tlv.TlvBuild#getTopLevelTlvList(Map<Integer,Integer>)
   */
  public List<Integer> getTopLevelTlvList(Map<Integer, Integer> miiTypeByteLength) {

    if (miiTypeByteLength == null) {
      try {
        throw new TlvException("miiTypeByteLength is NULL");
      } catch (TlvException e) {
        e.printStackTrace();
      }
    }

    // Create a list of found TopLevel TLV's
    List<Integer> liTopLevelTlvList = new ArrayList<Integer>();

    // Convert to ByteArray
    byte[] bTlvBuffer = toByteArray();

    // Size of byteLength
    int iTypeByteLength = 0;

    // Cycle thru the Byte Array to find the TopLevel TLV's
    for (int iIndex = 0; iIndex < toByteArray().length; ) {

      // Record the TYPE that was found
      liTopLevelTlvList.add(BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]));

      // Fetch Type Byte Length Size to determine the next TopLevel TLV
      iTypeByteLength =
          miiTypeByteLength.get(BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iIndex]));

      try {
        iIndex = nextTLVIndex(bTlvBuffer, iIndex, iTypeByteLength);
      } catch (TlvException e) {
        e.printStackTrace();
      }
    }

    return liTopLevelTlvList;
  }

  /**
   * @return a Clone of the TLV Buffer
   */
  public List<String> cloneTlvBuffer() {

    List<String> lCloneTlvBuffer = new ArrayList<String>(lsTlvBuffer.size());

    for (String item : lsTlvBuffer) lCloneTlvBuffer.add(item);

    return lCloneTlvBuffer;
  }

  /**
   * @return This method returns a TlvBuild of the bytes contained
   */
  @Override
  public TlvBuilder clone() {

    TlvBuilder tbClone = new TlvBuilder();

    try {
      tbClone.add(new HexString(this.toByteArray()));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    return tbClone;
  }

  /**
   * @return List<byte[]>
   */
  public List<byte[]> sortByTopLevelTlv() {

    ByteArrayOutputStream baosTopLevelTlvList;

    List<byte[]> lbTopLevelTlvList = new ArrayList<byte[]>();

    byte[] bMajorTlv = toByteArray();

    int iIndex = 0, iNextIndex = 0;

    // Cycle thru Byte Array
    while (iIndex < (bMajorTlv.length - 1)) {

      // Create new BAOS
      baosTopLevelTlvList = new ByteArrayOutputStream();

      // Find the next TLV Index
      try {

        iNextIndex = nextTLVIndex(bMajorTlv, iIndex);

      } catch (TlvException e) {
        e.printStackTrace();
      }

      // Write only the Major TLV in this Cycle
      baosTopLevelTlvList.write(
          bMajorTlv,
          iIndex, // Index
          ((iNextIndex - iIndex))); // Calculate Length

      // Add TLV to List
      lbTopLevelTlvList.add(baosTopLevelTlvList.toByteArray());

      // Update to the new Index
      iIndex = iNextIndex;
    }

    return lbTopLevelTlvList;
  }

  /**
   * Unless specified in the Map, this method assumes each TopLevel is 1 byte of length
   *
   * @param miiTopLevelTLVByteLength <Type,NumByteLength>
   * @return list of TLV Bytes
   */
  public List<byte[]> sortByTopLevelTlv(Map<Integer, Integer> miiTopLevelTLVByteLength) {

    // Create a ByteArrayOutputStream
    ByteArrayOutputStream baosTopLevelTlvList;

    // Create a list to contain the TopLevel TLVs
    List<byte[]> lbTopLevelTlvList = new ArrayList<byte[]>();

    // Get ByteArray from This Object
    byte[] bMajorTlv = toByteArray();

    int iIndex = 0, iNextIndex = 0;

    int iByteLength = 1;

    // Cycle thru Byte Array
    while (iIndex < bMajorTlv.length) {

      // Create new BAOS
      baosTopLevelTlvList = new ByteArrayOutputStream();

      int iTypeFound = BinaryConversion.byteToUnsignedInteger(bMajorTlv[iIndex]);

      // Get byte Length Via TYPE

      // Workaround for building Default Configuration File Default Settings
      if (!miiTopLevelTLVByteLength.containsKey(iTypeFound)) {

        iIndex += TLV_TYPE_LENGTH_OVERHEAD;
        continue;
      }

      iByteLength = miiTopLevelTLVByteLength.get(iTypeFound);

      // Find the next TLV Index
      try {
        iNextIndex = nextTLVIndex(bMajorTlv, iIndex, iByteLength);
      } catch (TlvException e) {
        e.printStackTrace();
      }

      // Write only the Major TLV in this Cycle
      baosTopLevelTlvList.write(
          bMajorTlv,
          iIndex, // Index
          ((iNextIndex - iIndex))); // Calculate Length

      // Add TLV to List
      lbTopLevelTlvList.add(baosTopLevelTlvList.toByteArray());

      // Update to the new Index
      iIndex = iNextIndex;
    }

    return lbTopLevelTlvList;
  }

  /**
   * @return Map<Integer,Integer>
   * @see com.comcast.oscar.tlv.TlvBuild#getMapTypeToByteLength()
   */
  public Map<Integer, Integer> getMapTypeToByteLength() {

    return miiTlvTypeTpByteLength;
  }

  /**
   * @return Boolean
   * @see com.comcast.oscar.tlv.TlvBuild#isEmpty()
   */
  public Boolean isEmpty() {
    return lsTlvBuffer.isEmpty();
  }

  /**
   * @return HexString
   */
  public HexString getHexString() {
    return new HexString(toByteArray());
  }

  /**
   * @param baTlv
   * @param miiTypeToByteLength
   * @return list of TLVs in Groups of TopLevel TLV
   */
  public static List<HexString> topLevelTlvToHexStringList(
      byte[] baTlv, Map<Integer, Integer> miiTypeToByteLength) {

    List<HexString> lhs = new ArrayList<HexString>();

    for (int iIndex = 1; iIndex <= MAX_TLV_TYPE; iIndex++) {

      List<Integer> liType = new ArrayList<Integer>();

      liType.add(iIndex);

      byte[] baTlvFound = fetchTlv(liType, miiTypeToByteLength, baTlv);

      if (baTlvFound.length > 0) {
        lhs.add(new HexString(baTlvFound));
      }
    }

    return lhs;
  }

  /**
   * This method assumes each type has a 1 byte length encoding
   *
   * @param iType Must be 0 thru 255
   * @param bTlvByteArray Value of TLV in a ByteArray
   * @return byte[]
   * @throws TlvException
   */
  public static byte[] stripTlv(int iType, byte[] bTlvByteArray) throws TlvException {

    if (iType < 0) throw new TlvException("TlvBuilder.stripTlv() Type Less than 0");

    if (bTlvByteArray == null) throw new TlvException("TlvBuilder.stripTlv() Byte Array is NULL");

    if (iType > MAX_TLV_TYPE) throw new TlvException("TLV type must not exceed " + MAX_TLV_TYPE);

    ByteArrayOutputStream baosStripedTlvByteArray = new ByteArrayOutputStream();

    int iIndex = 0;
    int iTypeLength = 0;

    // The -1 is so that this loops assume that there is no next final type
    while (iIndex < bTlvByteArray.length - 1) {

      iTypeLength =
          (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex + TLV_LENGTH_POS_OFFSET]))
              + TLV_TYPE_LENGTH_OVERHEAD;

      // If no type match, Copy TLV to ByteArray
      if (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]) != iType) {

        /*if (bTlvByteArray != null)*/
        baosStripedTlvByteArray.write(bTlvByteArray, iIndex, iTypeLength);
      }

      // Goto Next TLV Index
      iIndex += iTypeLength;
    }

    return baosStripedTlvByteArray.toByteArray();
  }

  /**
   * @param iType - Integer 0>Type<255
   * @param bTlvByteArray ByteArray containing TLV Buffer
   * @param miiTypeToByteLength Map Containing a Type to Bytelength association
   * @return byte[] ByteArray that do not contain the specified Type
   * @throws TlvException
   */
  public static byte[] stripTlv(
      int iType, byte[] bTlvByteArray, Map<Integer, Integer> miiTypeToByteLength)
      throws TlvException {

    if ((iType < 0) && (iType > MAX_TLV_TYPE))
      throw new TlvException(
          "TlvBuilder.stripTlv() Type Less than 0 or Greater than " + MAX_TLV_TYPE);

    if (bTlvByteArray == null) throw new TlvException("TlvBuilder.stripTlv() Byte Array is NULL");

    if (miiTypeToByteLength == null)
      throw new TlvException("TlvBuilder.stripTlv() No Type To ByteLength Map Found");

    ByteArrayOutputStream baosStripedTlvByteArray = new ByteArrayOutputStream();

    int iIndex = 0;
    int iTypeLength = 0;
    int iTypeFound = 0;
    int iTypeByteLength = 0;

    // The -1 is so that this loops assume that there is no next final type
    while (iIndex < bTlvByteArray.length - 1) {

      iTypeFound = BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]);

      if (miiTypeToByteLength.containsKey(iTypeFound)) {
        iTypeByteLength = miiTypeToByteLength.get(iTypeFound);

        iTypeLength =
            (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex + TLV_LENGTH_POS_OFFSET]))
                + iTypeByteLength
                + TLV_ONE_BYTE_LENGTH;

      } else {

        iTypeLength =
            (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex + TLV_LENGTH_POS_OFFSET]))
                + TLV_TYPE_LENGTH_OVERHEAD;
      }

      // If no type match, Copy TLV to ByteArray
      if (iTypeFound != iType) {

        /*if (bTlvByteArray != null)*/
        baosStripedTlvByteArray.write(bTlvByteArray, iIndex, iTypeLength);
      }

      // Goto Next TLV Index
      iIndex += iTypeLength;
    }

    return baosStripedTlvByteArray.toByteArray();
  }

  /**
   * @param liType - List of integers that contain the Types that are to be fetched from TLV
   *     ByteArray
   * @param bTlvByteArray Byte Array that contains TLV, MUST be in TLV Construct
   * @return byte[]
   */
  public static byte[] fetchTlv(List<Integer> liType, byte[] bTlvByteArray) {

    ByteArrayOutputStream baosStripedTlvByteArray = new ByteArrayOutputStream();

    for (int iType : liType) {

      int iIndex = 0;
      int iTypeLength = 0;

      while (iIndex < bTlvByteArray.length) {

        // Look ahead to see if we exceed Byte Array Length
        if ((iIndex + TLV_LENGTH_POS_OFFSET) >= bTlvByteArray.length) {

          break;
        }

        iTypeLength =
            (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex + TLV_LENGTH_POS_OFFSET]))
                + TLV_TYPE_LENGTH_OVERHEAD;

        // If no type match, Copy TLV to ByteArray
        if (BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]) == iType) {

          baosStripedTlvByteArray.write(bTlvByteArray, iIndex, iTypeLength);
        }

        // Goto Next TLV Index
        iIndex += iTypeLength;
      }
    }

    return baosStripedTlvByteArray.toByteArray();
  }

  /**
   * If a Type to Byte Length is not defined, this method assumes that the type is of 1 byte Length
   *
   * @param liType - List of integers that contain the Types that are to be fetched from TLV
   *     ByteArray
   * @param bTlvByteArray Byte Array that contains TLV, MUST be in TLV Construct
   * @param _miiTypeByteLength Map<Integer,Integer>
   * @return byte[]
   */
  public static byte[] fetchTlv(
      List<Integer> liType, Map<Integer, Integer> _miiTypeByteLength, byte[] bTlvByteArray) {

    ByteArrayOutputStream baosStripedTlvByteArray = new ByteArrayOutputStream();

    for (int iType : liType) {

      int iIndex = 0;
      int iTypeLength = 0, iTypeByteLength = 0, iTypeFound = 0;

      // This Will Assume 1 byte if no Type has need registered in Map
      if (_miiTypeByteLength.containsKey(iType)) {

        iTypeByteLength = _miiTypeByteLength.get(iType);

      } else {
        iTypeByteLength = TLV_LENGTH_POS_OFFSET;
      }

      while (iIndex < bTlvByteArray.length - 1) {

        iTypeFound = BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]);

        try {
          iTypeLength = getTlvLength(bTlvByteArray, iIndex, iTypeByteLength);
        } catch (TlvException e) {
          e.printStackTrace();
        }

        // Determine Length of TLV Value

        // If the TypeLength is 0 , goto next Index
        if (iTypeLength == 0) {
          iIndex++;
          continue;
        }

        // If no type match, Copy TLV to ByteArray
        if (iTypeFound == iType) {

          baosStripedTlvByteArray.write(
              bTlvByteArray,
              iIndex,
              (iTypeLength + TLV_TYPE_LENGTH_OVERHEAD + (iTypeByteLength - 1)));
        }

        // Goto Next TLV Index
        iIndex += (iTypeLength + iTypeByteLength + TLV_TYPE_OVERHEAD);
      }
    }

    return baosStripedTlvByteArray.toByteArray();
  }

  /**
   * @param bTlvBuffer
   * @param iInitalPosition
   * @return -1 = End of ByteArray Index * @throws TlvException
   */
  public static int nextTLVIndex(byte[] bTlvBuffer, int iInitalPosition) throws TlvException {

    if (iInitalPosition < 0) throw new TlvException("TlvBuilder.nextTLVIndex() Type Less than 0");

    if (bTlvBuffer == null) throw new TlvException("TlvBuilder.nextTLVIndex() Byte Array is NULL");

    if ((iInitalPosition + TLV_LENGTH_POS_OFFSET) >= bTlvBuffer.length) {

      // Added this to indicate that pointer exceeded byte array
      return END_OF_BYTE_ARRAY_INDEX;
    }

    return (iInitalPosition
        + BinaryConversion.byteToUnsignedInteger(
            bTlvBuffer[iInitalPosition + TLV_LENGTH_POS_OFFSET])
        + TLV_TYPE_LENGTH_OVERHEAD);
  }

  /**
   * @param bTlvBuffer
   * @param iInitalPosition
   * @param iByteLength
   * @return int
   * @throws TlvException
   */
  public static int nextTLVIndex(byte[] bTlvBuffer, int iInitalPosition, int iByteLength)
      throws TlvException {

    if (iInitalPosition < 0) throw new TlvException("TlvBuilder.nextTLVIndex() Type Less than 0");

    if (bTlvBuffer == null) throw new TlvException("TlvBuilder.nextTLVIndex() Byte Array is NULL");

    if (iByteLength < 0)
      throw new TlvException("TlvBuilder.nextTLVIndex() Byte Length Less than 0");

    ByteArrayOutputStream baosTlvByteLength = new ByteArrayOutputStream();

    baosTlvByteLength.write(bTlvBuffer, (iInitalPosition + 1), iByteLength);

    int iTlvLength = new HexString(baosTlvByteLength.toByteArray()).toInteger();

    return (iInitalPosition + (iTlvLength + iByteLength) + TLV_TYPE_OVERHEAD);
  }

  /**
   * @param bTlvBuffer
   * @param bParentChildTlvEncodeList
   * @return byte[]
   * @throws TlvException
   */
  public static byte[] findTLVIndex(byte[] bTlvBuffer, byte[] bParentChildTlvEncodeList)
      throws TlvException {

    if (bTlvBuffer == null)
      throw new TlvException("TlvBuilder.findTLVIndex() TLV Byte Array is NULL");

    if (bParentChildTlvEncodeList == null)
      throw new TlvException("TlvBuilder.findTLVIndex() bParentChildTlvEncodeList is NULL");

    ByteArrayOutputStream baosTlvTypeIndexList = new ByteArrayOutputStream();

    // Need to make sure that length is at least 2 bytes
    if (bTlvBuffer.length < TLV_TYPE_LENGTH_OVERHEAD) {
      return baosTlvTypeIndexList.toByteArray();
    }

    // Jump ahead to the second TLV Type
    int iStartIndexEncodeList = 0;

    // Start into Sub TLV Array
    int iStartIndex = 0;

    while ((iStartIndex < bTlvBuffer.length)
        && (iStartIndexEncodeList < bParentChildTlvEncodeList.length)) {

      // Found a Matching Type, next step is to see if we reached the end of the
      // ParentChildTlvEncodeList
      if (bTlvBuffer[iStartIndex] == bParentChildTlvEncodeList[iStartIndexEncodeList]) {

        // If we get to this point, we reached the end of the ParentChildTlvEncodeList
        if (iStartIndexEncodeList == bParentChildTlvEncodeList.length - 1) {

          // Save Index
          baosTlvTypeIndexList.write(iStartIndex);

          // Finish looking thru Buffer in-case there are other TLVs
          iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex);

        } else {

          // We havn't reached the end of ParentChildTlvEncodeList, got next index
          iStartIndexEncodeList++;

          // Jump over length byte and into the next TLV Type
          iStartIndex += TLV_TYPE_LENGTH_OVERHEAD;
        }

      } else {
        // If there is Type Match, goto next TLV Encode
        iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex);
      }

      // If the index exceeds buffer length, we reached the end of the loop
      if (iStartIndex >= bTlvBuffer.length) break;
    }

    return baosTlvTypeIndexList.toByteArray();
  }

  /**
   * @param bTlvBuffer
   * @param iType
   * @return the index where the Type is found * @throws TlvException
   */
  public static int findTLVIndex(byte[] bTlvBuffer, int iType) throws TlvException {

    if (bTlvBuffer == null)
      throw new TlvException("TlvBuilder.findTLVIndex() TLV Byte Array is NULL");

    // Jump ahead to the second TLV Type
    int iStartIndexEncodeList = 0;

    // Start into Sub TLV Array
    int iStartIndex = 0;

    while (iStartIndex < bTlvBuffer.length) {

      // Found a Matching Type, next step is to see if we reached the end of the
      // ParentChildTlvEncodeList
      if (BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iStartIndex]) != iType) {

        // Finish looking thru Buffer in-case there are other TLVs
        iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex);

      } else {
        break;
      }
    }

    return iStartIndex;
  }

  /**
   * @param bTlvBuffer
   * @param bParentChildTlvEncodeList
   * @param miiTypeByteLength
   * @return byte[]
   * @throws TlvException
   */
  public static byte[] findTLVIndex(
      byte[] bTlvBuffer, byte[] bParentChildTlvEncodeList, Map<Integer, Integer> miiTypeByteLength)
      throws TlvException {

    if (bTlvBuffer == null)
      throw new TlvException("TlvBuilder.findTLVIndex() TLV Byte Array is NULL");

    if (bParentChildTlvEncodeList == null)
      throw new TlvException("TlvBuilder.findTLVIndex() bParentChildTlvEncodeList is NULL");

    ByteArrayOutputStream baosTlvTypeIndexList = new ByteArrayOutputStream();

    // Need to make sure that length is at least 2 bytes
    if (bTlvBuffer.length < TLV_TYPE_LENGTH_OVERHEAD) {
      return baosTlvTypeIndexList.toByteArray();
    }

    // Jump ahead to the second TLV Type
    int iStartIndexEncodeList = 0;

    // Start into Sub TLV Array
    int iStartIndex = 0;

    // Type ByteLength
    int iTypeByteLength = 0;

    while ((iStartIndex < bTlvBuffer.length)
        && (iStartIndexEncodeList < bParentChildTlvEncodeList.length)) {

      // Found a Matching Type, next step is to see if we reached the end of the
      // ParentChildTlvEncodeList
      if (bTlvBuffer[iStartIndex] == bParentChildTlvEncodeList[iStartIndexEncodeList]) {

        // If we get to this point, we reached the end of the ParentChildTlvEncodeList
        if (iStartIndexEncodeList == bParentChildTlvEncodeList.length - 1) {

          // Save Index
          baosTlvTypeIndexList.write(iStartIndex);

          // Lookup the Byte Length via Type
          iTypeByteLength =
              miiTypeByteLength.get(
                  BinaryConversion.byteToUnsignedInteger(bTlvBuffer[iStartIndex]));

          // Finish looking thru Buffer in-case there are other TLVs
          iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex, iTypeByteLength);

        } else {

          // We havn't reached the end of ParentChildTlvEncodeList, got next index
          iStartIndexEncodeList++;

          // Jump over length byte and into the next TLV Type
          iStartIndex += TLV_TYPE_LENGTH_OVERHEAD;
        }

      } else {
        // If there is Type Match, goto next TLV Encode
        iStartIndex = TlvBuilder.nextTLVIndex(bTlvBuffer, iStartIndex);
      }

      // If the index exceeds buffer length, we reached the end of the loop
      if (iStartIndex >= bTlvBuffer.length) break;
    }

    return baosTlvTypeIndexList.toByteArray();
  }

  /**
   * @param bTlvBuffer
   * @return byte[]
   * @throws TlvException
   */
  public static byte[] getTlvValue(byte[] bTlvBuffer) throws TlvException {

    if (bTlvBuffer == null) throw new TlvException("TlvBuilder.getTlvValue() bTlvBuffer is NULL");

    ByteArrayOutputStream baosTlvBuffer = new ByteArrayOutputStream();

    baosTlvBuffer.write(
        bTlvBuffer,
        TLV_TYPE_LENGTH_OVERHEAD,
        BinaryConversion.byteToUnsignedInteger(bTlvBuffer[TLV_LENGTH_POS_OFFSET]));

    return baosTlvBuffer.toByteArray();
  }

  /**
   * @param bTlvBuffer
   * @param iInitalPosition
   * @return byte[]
   * @throws TlvException
   */
  public static byte[] getTlvValue(byte[] bTlvBuffer, int iInitalPosition) throws TlvException {

    if (bTlvBuffer == null) throw new TlvException("TlvBuilder.getTlvValue() bTlvBuffer is NULL");

    if (iInitalPosition < 0)
      throw new TlvException("TlvBuilder.getTlvValue() iInitalPosition than 0");

    ByteArrayOutputStream baosTlvBuffer = new ByteArrayOutputStream();

    // Write the value to OutputStream
    baosTlvBuffer.write(
        bTlvBuffer,
        iInitalPosition + TLV_TYPE_LENGTH_OVERHEAD,
        BinaryConversion.byteToUnsignedInteger(
            bTlvBuffer[iInitalPosition + TLV_LENGTH_POS_OFFSET]));

    return baosTlvBuffer.toByteArray();
  }

  /**
   * @param bTlvBuffer
   * @param iInitalPosition
   * @param iByteLength
   * @return byte[]
   * @throws TlvException
   */
  public static byte[] getTlvValue(byte[] bTlvBuffer, int iInitalPosition, int iByteLength)
      throws TlvException {

    if (bTlvBuffer == null)
      throw new TlvException("TlvBuilder.getTlvValue(b,i,i) bTlvBuffer is NULL");

    if (iInitalPosition < 0)
      throw new TlvException("TlvBuilder.getTlvValue(b,i,i) iInitalPosition than 0");

    if (iByteLength < 0) throw new TlvException("TlvBuilder.getTlvValue(b,i,i) iByteLength than 0");

    // Create ByteArrayOutputStream
    ByteArrayOutputStream baosTlvBuffer = new ByteArrayOutputStream();

    // Determine TLV Length
    int iTlvValueLength = getTlvLength(bTlvBuffer, iInitalPosition, iByteLength);

    // Write the value to OutputStream
    baosTlvBuffer.write(
        bTlvBuffer, (iInitalPosition + iByteLength + TLV_TYPE_OVERHEAD), iTlvValueLength);

    return baosTlvBuffer.toByteArray();
  }

  /**
   * @param bTlvBuffer
   * @param iInitalPosition
   * @param iByteLength
   * @return int
   * @throws TlvException
   */
  public static int getTlvLength(byte[] bTlvBuffer, int iInitalPosition, int iByteLength)
      throws TlvException {

    if (bTlvBuffer == null) throw new TlvException("TlvBuilder.getTlvValue() bTlvBuffer is NULL");

    if (iInitalPosition < 0)
      throw new TlvException("TlvBuilder.getTlvValue() iInitalPosition than 0");

    if (iByteLength < 0) throw new TlvException("TlvBuilder.getTlvValue() iByteLength than 0");

    if (iByteLength == 0) return iByteLength;

    ByteArrayOutputStream baosTlvByteLength = new ByteArrayOutputStream();

    baosTlvByteLength.write(bTlvBuffer, iInitalPosition + 1, iByteLength);

    return new HexString(baosTlvByteLength.toByteArray()).toInteger();
  }

  /**
   * This method take a multiple single byte length TLV, strip the TopLevel TLV byte and length and
   * concatenate all the values
   *
   * <p>
   *
   * @param tbTLV
   * @return byte[]
   */
  public static byte[] coupleMultipleTopLevelTlvValues(TlvBuilder tbTLV) {

    HexString hsTLV = new HexString();

    for (byte[] bTLV : tbTLV.sortByTopLevelTlv()) {
      try {
        hsTLV.add(getTlvValue(bTLV));
      } catch (TlvException e) {
        e.printStackTrace();
      }
    }

    return hsTLV.toByteArray();
  }

  /**
   * This method assumes all TopLevel TLV are 1 Byte Length
   *
   * @param bTLV
   * @return List<byte[]>
   * @throws TlvException
   */
  public static List<byte[]> getTlvHexByType(byte[] bTLV) throws TlvException {

    List<byte[]> lbTLV = new ArrayList<byte[]>();

    byte[] bFetchTLV = null;

    int iNextIndex = 0;

    while (iNextIndex < bTLV.length) {

      int iTypeFound = BinaryConversion.byteToUnsignedInteger(bTLV[iNextIndex]);

      // Added this to make sure index does not excess byteArray and cause a NPE
      if (iNextIndex == END_OF_BYTE_ARRAY_INDEX) {

        break;
      }

      // Create a new list
      List<Integer> li = new ArrayList<Integer>();

      // Add type to list
      li.add(iTypeFound);

      // Get TLV from Type
      bFetchTLV = fetchTlv(li, bTLV);

      // Load ByteArray of TLV
      lbTLV.add(bFetchTLV);

      // Index will be The TYPE
      iNextIndex = TlvBuilder.nextTLVIndex(bTLV, iNextIndex);
    }

    return lbTLV;
  }

  /**
   * @param bTLV
   * @param miiTypeToByteLength
   * @return List<byte[]>
   * @throws TlvException
   */
  public static List<byte[]> getTlvHexByType(byte[] bTLV, Map<Integer, Integer> miiTypeToByteLength)
      throws TlvException {

    List<byte[]> lbTLV = new ArrayList<byte[]>();

    byte[] bFetchTLV = null;

    int iNextIndex = 0;

    while (iNextIndex < bTLV.length) {

      // Added this to make sure index does not excess byteArray and cause a NPE
      if (iNextIndex == END_OF_BYTE_ARRAY_INDEX) {

        break;
      }

      List<Integer> li = new ArrayList<Integer>();

      li.add(BinaryConversion.byteToUnsignedInteger(bTLV[iNextIndex]));

      // Get TLV from Type
      bFetchTLV = fetchTlv(li, miiTypeToByteLength, bTLV);

      // Load ByteArray of TLV
      lbTLV.add(bFetchTLV);

      // Index will be The TYPE
      iNextIndex = TlvBuilder.nextTLVIndex(bTLV, iNextIndex);
    }

    return lbTLV;
  }

  /**
   * @return boolean
   */
  public boolean isTLVConstruct() {

    boolean returnStatus = true;

    byte[] bTLV = toByteArray();

    int iIndex = 0, iType = 0, iNumOfByteLength = 0;

    // Check to see of there is any length
    if (bTLV.length == 0) {

      return false;
    }

    // cycle thru Byte Array
    while (iIndex < bTLV.length) {

      // Current Type
      iType = BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]);

      // Checks to see if there is a mapping, if one does not exists, it will assume that it is 1
      // byte Length
      if (miiTlvTypeTpByteLength.containsKey(iType)) {
        // Number of bytes for the Length of the TLV
        iNumOfByteLength = miiTlvTypeTpByteLength.get(iType);
      } else {
        iNumOfByteLength = 1;
      }

      // Look for Next TLV Index
      try {
        iIndex = nextTLVIndex(bTLV, iIndex, iNumOfByteLength);
      } catch (TlvException e) {
        e.printStackTrace();
      }

      // Check for EOF for DOCSIS due to PADDING
      if ((iIndex < bTLV.length)
          && (BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]) == DocsisTlvTypes.END_OF_FILE)) {

        return true;

        // This index would be out of bound if past total TLV Length
      } else if (iIndex > bTLV.length) {

        return false;
      }
    }

    return returnStatus;
  }

  /**
   * @param bTLV
   * @param miiTlvTypeTpByteLength
   * @return boolean
   */
  public static boolean isTLVConstruct(byte[] bTLV, Map<Integer, Integer> miiTlvTypeTpByteLength) {

    boolean returnStatus = true;

    int iIndex = 0, iType = 0, iNumOfByteLength = 0;

    // If the user does not want to provide a Map, it will assume that TLV is 1 byte encoded
    if (miiTlvTypeTpByteLength == null) {
      miiTlvTypeTpByteLength = new HashMap<Integer, Integer>();
    }

    // Check to see of there is any length
    if (bTLV.length == 0) {

      return Boolean.FALSE;
    }

    // cycle thru Byte Array
    while (iIndex < bTLV.length) {

      // Current Type
      iType = BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]);

      // Checks to see if there is a mapping, if one does not exists, it will assume that it is 1
      // byte Length
      if (miiTlvTypeTpByteLength.containsKey(iType)) {
        // Number of bytes for the Length of the TLV
        iNumOfByteLength = miiTlvTypeTpByteLength.get(iType);
      } else {
        iNumOfByteLength = 1;
      }

      // Look for Next TLV Index
      try {
        iIndex = nextTLVIndex(bTLV, iIndex, iNumOfByteLength);
      } catch (TlvException e) {
        e.printStackTrace();
      }

      // Check for EOF for DOCSIS due to PADDING
      if ((iIndex < bTLV.length)
          && (BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]) == DocsisTlvTypes.END_OF_FILE)) {

        return true;

        // This index would be out of bound if past total TLV Length
      } else if (iIndex > bTLV.length) {

        return false;
      }
    }

    return returnStatus;
  }

  /**
   * @param iTlvType
   * @param baValue
   * @return byte[]
   * @throws TlvException
   */
  public static byte[] encapsulateTlv(int iTlvType, byte[] baValue) throws TlvException {

    if (iTlvType < 0) throw new TlvException("TlvBuilder.encapsulateTlv() Type Less than 0");

    if (iTlvType > 254) throw new TlvException("TlvBuilder.encapsulateTlv() Type Greater than 254");

    if (baValue == null) throw new TlvException("TlvBuilder.encapsulateTlv() - ByteArray is Null");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    int iByteArrayLength = baValue.length;

    baos.write(iTlvType);
    baos.write(iByteArrayLength);

    try {
      baos.write(baValue);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return baos.toByteArray();
  }

  /**
   * This method will remove any TLV that contain a Zero Byte Length
   *
   * @param bTlvByteArray
   * @param miiTypeToByteLength Map<Integer,Integer>
   * @return byte[]
   * @throws TlvException
   */
  public static byte[] stripZeroByteTLV(
      byte[] bTlvByteArray, Map<Integer, Integer> miiTypeToByteLength) throws TlvException {

    if (bTlvByteArray == null)
      throw new TlvException("TlvBuilder.stripZeroByteTLV(): bTlvByteArray is NULL");

    if (bTlvByteArray.length < 3)
      throw new TlvException(
          "TlvBuilder.stripZeroByteTLV(): ByteArray is less than 3 Bytes, not a TLV Encoded Array");

    ByteArrayOutputStream baosStripZeroByteTLV = new ByteArrayOutputStream();

    int iIndex = 0, iTypeLength = -1, iTypeFound = -1, iTypeByteLength = 0;

    while (iIndex < bTlvByteArray.length) {

      // Current Type Found
      iTypeFound = BinaryConversion.byteToUnsignedInteger(bTlvByteArray[iIndex]);

      // Determine the number of byte length
      if (miiTypeToByteLength.containsKey(iTypeFound)) {
        iTypeByteLength = miiTypeToByteLength.get(iTypeFound);
      } else {
        iTypeByteLength = ONE_BYTE_LENGTH;
      }

      // Look ahead to see if we exceed Byte Array Length
      if (iTypeByteLength < 1 || iTypeByteLength > bTlvByteArray.length - iIndex - 1) {
        throw new TlvException("TlvBuilder.stripZeroByteTLV(): Truncated length field");
      }

      iTypeLength = TlvBuilder.getTlvLength(bTlvByteArray, iIndex, iTypeByteLength);

      // Check for 0 byte Length if so move to next TLV Index
      if (iTypeLength == ZERO_BYTE_LENGTH) {

        // Advance to the next Type
        iIndex += iTypeByteLength + TLV_TYPE_OVERHEAD;

        continue;
      }

      // Get Value Length
      if (iTypeLength < 0 || iTypeLength > bTlvByteArray.length - iIndex - iTypeByteLength - 1) {
        throw new TlvException("TlvBuilder.stripZeroByteTLV(): Truncated value");
      }

      // Write TLV into ByteArray
      baosStripZeroByteTLV.write(
          bTlvByteArray, iIndex, (iTypeLength + iTypeByteLength + TLV_TYPE_OVERHEAD));

      // Goto Next TLV Index
      iIndex += (iTypeLength + iTypeByteLength + TLV_TYPE_OVERHEAD);
    }

    return baosStripZeroByteTLV.toByteArray();
  }

  /**
   * @param ba
   * @return - return in Hex TopLevel TLV Dump separated by newline
   */
  public static String tlvDump(byte[] ba) {

    String sTlvDump = "";

    HexString hs = new HexString(ba);

    TlvBuilder tb = new TlvBuilder();

    try {
      tb.add(hs);
    } catch (TlvException e) {
      e.printStackTrace();
    }

    ArrayList<byte[]> alb = (ArrayList<byte[]>) tb.sortByTopLevelTlv();

    for (byte[] ba1 : alb) {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      baos.write(ba1[0]);
      int iType = HexString.toInteger(baos.toByteArray());

      HexString hsTlv = new HexString(ba1);
      sTlvDump += ("[Type:" + iType + "] " + hsTlv.toString(":") + "\n");
    }

    return sTlvDump;
  }

  /* *****************************************************************************
   * 							Private Methods
   *******************************************************************************/
  /**
   * @param iType
   * @param iTypeLength
   */
  private void updateMapTypeToByteLength(int iType, int iTypeLength) {

    if (!miiTlvTypeTpByteLength.containsKey(iType)) {
      miiTlvTypeTpByteLength.put(iType, iTypeLength);
    }
  }

  /**
   * @param tbObject
   */
  private void updateMapTypeToByteLength(TlvBuilder tbObject) {

    HashMap<Integer, Integer> hmTlvTypeTpByteLength = new HashMap<Integer, Integer>();

    // Original Map
    hmTlvTypeTpByteLength.putAll(miiTlvTypeTpByteLength);

    // Overwrite Map
    hmTlvTypeTpByteLength.putAll(tbObject.getMapTypeToByteLength());

    // Update Map Pointer
    miiTlvTypeTpByteLength = hmTlvTypeTpByteLength;

    ArrayList<byte[]> albTlvBuffer = new ArrayList<byte[]>();
    albTlvBuffer.addAll(bTlvBuilder);

    HashMap<Integer, Integer> hmiiTlvTypeToByteLength = new HashMap<Integer, Integer>();
    hmiiTlvTypeToByteLength.putAll(miiTlvTypeTpByteLength);
  }

  /**
   * @param hsTLV
   * @return boolean
   */
  private boolean isTLVConstruct(HexString hsTLV) {

    boolean returnStatus = true;

    byte[] bTLV = hsTLV.toByteArray();

    int iIndex = 0, iType = 0, iNumOfByteLength = 0;

    // Check to see of there is any length
    if (bTLV.length == 0) {

      return Boolean.FALSE;
    }

    // cycle thru Byte Array
    while (iIndex < bTLV.length) {

      // Current Type
      iType = BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]);

      // Checks to see if there is a mapping, if one does not exists, it will assume that it is 1
      // byte Length
      if (miiTlvTypeTpByteLength.containsKey(iType)) {
        // Number of bytes for the Length of the TLV
        iNumOfByteLength = miiTlvTypeTpByteLength.get(iType);
      } else {
        iNumOfByteLength = 1;
      }

      if (bTLV.length - iIndex - 1 < iNumOfByteLength) {
        return false;
      }

      // Look for Next TLV Index
      try {
        iIndex = nextTLVIndex(bTLV, iIndex, iNumOfByteLength);
      } catch (TlvException e) {
        return false;
      }

      // Check for EOF for DOCSIS due to PADDING
      if ((iIndex < bTLV.length)
          && (BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]) == DocsisTlvTypes.END_OF_FILE)) {

        return true;

        // This index would be out of bound if past total TLV Length
      } else if (iIndex > bTLV.length) {

        return false;
      }
    }

    return returnStatus;
  }
}
