package com.comcast.oscar.tlv;

import com.comcast.oscar.compiler.PacketCableConstants;
import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @bannerLicense Copyright 2015 Comcast Cable Communications Management, LLC<br>
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
public class TlvVariableBinding implements TlvBuild {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(TlvVariableBinding.class);

  protected List<byte[]> lbTlvBuffer = new ArrayList<byte[]>();

  protected byte[] bTlvBuffer = null;

  // Type , Number of Bytes Length
  protected Map<Integer, Integer> miiTypeBytelength = new HashMap<Integer, Integer>();

  public static final byte TLV_TYPE_OVERHEAD = 0x01;

  /** */
  public TlvVariableBinding() {
    super();

    if (logger.isDebugEnabled()) {
      logger.debug(
          "+-------------------------------------------------------------------------------------------------+");
      logger.debug("TlvVariableBinding()");
    }

    typeToMapInit();
  }

  /**
   * @param miiTypeBytelength
   * @param lbTlvBuffer
   * @param bTlvBuffer
   */
  private TlvVariableBinding(
      Map<Integer, Integer> miiTypeBytelength, List<byte[]> lbTlvBuffer, byte[] bTlvBuffer) {
    super();

    if (logger.isDebugEnabled()) {
      logger.debug(
          "+-------------------------------------------------------------------------------------------------+");
      logger.debug("TlvVariableBinding(m,l,b)");
    }

    updateMapsAndListsForCloning(miiTypeBytelength, lbTlvBuffer, bTlvBuffer);
  }

  /**
   * @param bTLV
   * @param miiTypeBytelength
   */
  public TlvVariableBinding(byte[] bTLV, Map<Integer, Integer> miiTypeBytelength) {

    this.miiTypeBytelength.putAll(miiTypeBytelength);

    this.lbTlvBuffer.add(bTLV);

    updateTlvByteBuffers(bTLV);

    if (logger.isDebugEnabled()) {
      logger.debug(
          "+-------------------------------------------------------------------------------------------------+");
      logger.debug("TlvVariableBinding(b,m).miiTypeBytelength" + miiTypeBytelength);
      logger.debug("TlvVariableBinding(b,m).bTLV.Size: " + bTLV.length);
      logger.debug("TlvVariableBinding(b,m).lbTLV.Size: " + lbTlvBuffer.size());
      logger.debug("TlvVariableBinding(b,m).toByteArray.Size: " + toByteArray().length);
    }
  }

  /**
   * @param iTlvType
   * @param bValue
   * @throws TlvException * @see com.comcast.oscar.tlv.TlvBuild#add(int, byte[])
   */
  public void add(int iTlvType, byte[] bValue) throws TlvException {

    if (iTlvType < 0) throw new TlvException("TlvVariableBinding.add() Type Less than 0");

    // Create BAOS
    ByteArrayOutputStream baosVariableBindingTLV = new ByteArrayOutputStream();

    // Write TYPE
    baosVariableBindingTLV.write(iTlvType);

    // Calculate Number of bytes
    HexString hsBL = new HexString();
    hsBL.add(bValue.length);

    try {
      baosVariableBindingTLV.write(hsBL.toByteArray());
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    // Write Value length;
    try {
      baosVariableBindingTLV.write(bValue);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // UpdateTLV Buffers
    updateTlvByteBuffers(baosVariableBindingTLV.toByteArray());

    updateTlvByteLength(iTlvType, hsBL.toByteArray().length);

    if (logger.isDebugEnabled()) {
      logger.debug("TlvVariableBinding.add() ByteLength: " + bValue.length);
      logger.debug("TlvVariableBinding.add() miiTypeBytelength: " + miiTypeBytelength);
    }
  }

  /**
   * @param iTlvType
   * @param bValue
   * @param iNumByteLength
   * @throws TlvException
   */
  public void add(int iTlvType, byte[] bValue, int iNumByteLength) throws TlvException {

    if (iNumByteLength < 0)
      throw new TlvException("TlvVariableBinding.add() Number of Byte Length less than 0");

    if (iTlvType < 0) throw new TlvException("TlvVariableBinding.add() Type Less than 0");

    // Create BAOS
    ByteArrayOutputStream baosVariableBindingTLV = new ByteArrayOutputStream();

    baosVariableBindingTLV.write(iTlvType); // Write TYPE

    HexString hsByteLength = new HexString();
    hsByteLength.add(bValue.length);
    hsByteLength.prefixNullPaddToLength(iNumByteLength);

    // Set bytes to length padded if necessary
    try {
      baosVariableBindingTLV.write(hsByteLength.toByteArray());
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    try {
      baosVariableBindingTLV.write(bValue); // Write Value length;
    } catch (IOException e) {
      e.printStackTrace();
    }

    // UpdateTLV Buffers
    updateTlvByteBuffers(baosVariableBindingTLV.toByteArray());

    updateTlvByteLength(iTlvType, iNumByteLength);

    if (logger.isDebugEnabled()) {
      logger.debug("TlvVariableBinding.add(i,b,i) ByteLength: " + bValue.length);
      logger.debug("TlvVariableBinding.add(i,b,i) miiTypeBytelength: " + miiTypeBytelength);
    }
  }

  /**
   * @return byte[]
   * @see com.comcast.oscar.tlv.TlvBuild#toByteArray()
   */
  public byte[] toByteArray() {
    return bTlvBuffer;
  }

  /**
   * Does Nothing
   *
   * @return TlvVariableBinding
   */
  @Override
  public TlvVariableBinding clone() {

    TlvVariableBinding tvb = new TlvVariableBinding(miiTypeBytelength, lbTlvBuffer, bTlvBuffer);

    return tvb;
  }

  /**
   * True if Empty
   *
   * @return Boolean
   * @see com.comcast.oscar.tlv.TlvBuild#isEmpty()
   */
  public Boolean isEmpty() {

    boolean boolIsEmpty = false;

    if (bTlvBuffer.length == 0) boolIsEmpty = true;

    return boolIsEmpty;
  }

  /**
   * Return the number of bytes
   *
   * @return int
   */
  public int length() {
    return bTlvBuffer.length;
  }

  /**
   * @return String
   */
  @Override
  public String toString() {
    return new HexString(toByteArray()).toString(":");
  }

  /**
   * @see com.comcast.oscar.tlv.TlvBuild#clear()
   */
  public void clear() {

    if (logger.isDebugEnabled()) logger.debug("TlvVariableBinding.clear(): " + miiTypeBytelength);

    miiTypeBytelength.clear();
    lbTlvBuffer.clear();
    bTlvBuffer = new HexString().toByteArray();

    typeToMapInit();
  }

  /**
   * @param miiTypeByteLength Map<Integer,Integer>
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

    if (logger.isDebugEnabled())
      logger.debug("TlvVariableBinding.getTopLevelTlvList() : " + miiTypeByteLength);

    // Create a list of found TopLevel TLV's
    List<Integer> liTopLevelTlvList = new ArrayList<Integer>();

    // Convert to ByteArray
    byte[] _bTlvBuffer = toByteArray();

    // Size of byteLength
    int iTypeByteLength = 0;

    // Cycle thru the Byte Array to find the TopLevel TLV's
    for (int iIndex = 0; iIndex < toByteArray().length; ) {

      if (logger.isDebugEnabled())
        logger.debug(
            "TlvVariableBinding.getTopLevelTlvList: -> Type: "
                + BinaryConversion.byteToUnsignedInteger(_bTlvBuffer[iIndex])
                + " Index: "
                + iIndex
                + " of "
                + toByteArray().length);

      // Record the TYPE that was found
      liTopLevelTlvList.add(BinaryConversion.byteToUnsignedInteger(_bTlvBuffer[iIndex]));

      // Fetch Type Byte Length Size to determine the next TopLevel TLV
      iTypeByteLength =
          miiTypeByteLength.get(BinaryConversion.byteToUnsignedInteger(_bTlvBuffer[iIndex]));

      try {
        iIndex = TlvBuilder.nextTLVIndex(_bTlvBuffer, iIndex, iTypeByteLength);
      } catch (TlvException e) {
        e.printStackTrace();
      }
    }

    return liTopLevelTlvList;
  }

  /**
   * @return List<Integer>
   * @see com.comcast.oscar.tlv.TlvBuild#getTopLevelTlvList()
   */
  public List<Integer> getTopLevelTlvList() {
    return getTopLevelTlvList(miiTypeBytelength);
  }

  /**
   * @return Map<Integer,Integer>
   * @see com.comcast.oscar.tlv.TlvBuild#getMapTypeToByteLength()
   */
  public Map<Integer, Integer> getMapTypeToByteLength() {

    if (logger.isDebugEnabled()) {
      logger.debug("TlvVariableBinding.getMapTypeToByteLength(): " + this.miiTypeBytelength);

      // Thread.dumpStack();
    }
    return this.miiTypeBytelength;
  }

  /**
   * @return List<byte[]>
   */
  public List<byte[]> getByteListTlvBuffer() {
    return lbTlvBuffer;
  }

  /**
   * This Method assumes that each TLV Level that it is searching is of the same Number of Byte
   * Length
   *
   * @param bParentChildTlvEncodeList
   * @param _bTlvBuffer byte[]
   * @param iTopLevelTlvNumByteLength int
   * @return byte[]
   * @throws TlvException
   */
  public static byte[] findTLVIndex(
      byte[] _bTlvBuffer, byte[] bParentChildTlvEncodeList, int iTopLevelTlvNumByteLength)
      throws TlvException {

    if (_bTlvBuffer == null)
      throw new TlvException("TlvVariableBinding.findTLVIndex(b,b,i) TLV Byte Array is NULL");

    if (bParentChildTlvEncodeList == null)
      throw new TlvException(
          "TlvVariableBinding.findTLVIndex(b,b,i) bParentChildTlvEncodeList is NULL");

    if (iTopLevelTlvNumByteLength < 0)
      throw new TlvException(
          "TlvVariableBinding.findTLVIndex(b,b,i) iTopLevelTlvNumByteLength less than 0");

    ByteArrayOutputStream baosTlvTypeIndexList = new ByteArrayOutputStream();

    if (logger.isDebugEnabled()) {
      logger.debug(
          "TlvVariableBinding.findTLVIndex(b,b,i) TlvBufferLength: : " + _bTlvBuffer.length);
      logger.debug(
          "TlvVariableBinding.findTLVIndex(b,b,i) Hex: " + new HexString(_bTlvBuffer).toString());
      logger.debug(
          "TlvVariableBinding.findTLVIndex(b,b,i) bParentChildTlvEncodeList:"
              + new HexString(bParentChildTlvEncodeList).toString());
      logger.debug(
          "TlvVariableBinding.findTLVIndex(b,b,i) iTopLevelTlvNumByteLength: "
              + iTopLevelTlvNumByteLength);
    }

    // Need to make sure that length is at least 1 + N bytes
    if (_bTlvBuffer.length < (TLV_TYPE_OVERHEAD + iTopLevelTlvNumByteLength)) {

      if (logger.isDebugEnabled())
        logger.debug(
            "TlvVariableBinding.findTLVIndex(b,b,i) -> (TLV_TYPE_OVERHEAD +"
                + " iTopLevelTlvNumByteLength): : "
                + (TLV_TYPE_OVERHEAD + iTopLevelTlvNumByteLength));

      return baosTlvTypeIndexList.toByteArray();
    }

    // Jump ahead to the second TLV Type
    int iStartIndexEncodeList = 0;

    // Start into Sub TLV Array
    int iStartIndex = 0;

    while ((iStartIndex < _bTlvBuffer.length)
        && (iStartIndexEncodeList < bParentChildTlvEncodeList.length)) {

      if (logger.isDebugEnabled())
        logger.debug(
            "TlvVariableBinding.findTLVIndex(b,b,i) bParentChildTlvEncodeList: "
                + new HexString(bParentChildTlvEncodeList).toString()
                + " - iStartIndex: "
                + iStartIndex
                + " - bTlvBuffer.length: "
                + _bTlvBuffer.length
                + " - Type: "
                + BinaryConversion.byteToUnsignedInteger(_bTlvBuffer[iStartIndex])
                + " - iStartIndexEncodeList: "
                + iStartIndexEncodeList
                + " - bParentChildTlvEncodeList: "
                + BinaryConversion.byteToUnsignedInteger(
                    bParentChildTlvEncodeList[iStartIndexEncodeList])
                + " - baosTlvTypeIndexList: "
                + new HexString(baosTlvTypeIndexList.toByteArray()).toHexStringList());

      // Found a Matching Type, next step is to see if we reached the end of the
      // ParentChildTlvEncodeList
      if (_bTlvBuffer[iStartIndex] == bParentChildTlvEncodeList[iStartIndexEncodeList]) {

        // If we get to this point, we reached the end of the ParentChildTlvEncodeList
        if (iStartIndexEncodeList == (bParentChildTlvEncodeList.length - 1)) {

          // Save Index
          try {
            baosTlvTypeIndexList.write(HexString.intToByteArray(iStartIndex));
          } catch (IOException e) {
            e.printStackTrace();
          }

          // Finish looking thru Buffer in-case there are other TLVs
          iStartIndex = nextTLVIndex(_bTlvBuffer, iStartIndex, iTopLevelTlvNumByteLength);

        } else {

          // We havn't reached the end of ParentChildTlvEncodeList, got next index
          iStartIndexEncodeList++;

          // Jump over length byte and into the next TLV Type
          iStartIndex += (TLV_TYPE_OVERHEAD + iTopLevelTlvNumByteLength);
        }

      } else {
        // If there is Type Match, goto next TLV Encode
        iStartIndex = TlvBuilder.nextTLVIndex(_bTlvBuffer, iStartIndex);
      }

      // If the index exceeds buffer length, we reached the end of the loop
      if (iStartIndex >= _bTlvBuffer.length) break;
    }

    return baosTlvTypeIndexList.toByteArray();
  }

  /**
   * miiTlvNumByteLength<Type,NumberOfByteLength>
   *
   * @param hsTLV
   * @param miiTlvNumByteLength
   * @return boolean
   */
  public boolean isTLVConstruct(HexString hsTLV, Map<Integer, Integer> miiTlvNumByteLength) {

    boolean returnStatus = true;

    byte[] bTLV = hsTLV.toByteArray();

    int iIndex = 0, iType = 0, iNumOfByteLength = 0;

    // Check to see of there is any length
    if (bTLV.length == 0) {

      if (logger.isDebugEnabled())
        logger.debug("TlvVariableBinding.isTLVConstruct() -> ByteArray is of 0 Length");

      return Boolean.FALSE;
    }

    // cycle thru Byte Array
    while (iIndex < bTLV.length) {

      iType = BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]);

      iNumOfByteLength = miiTlvNumByteLength.get(iType);

      if (logger.isDebugEnabled())
        logger.debug(
            "TlvVariableBinding.isTLVConstruct() -> "
                + "Type: "
                + iType
                + " -> "
                + "Index: "
                + iIndex
                + " -> "
                + "NumOfByteLength: "
                + iNumOfByteLength
                + " -> "
                + " bTLV.length: "
                + bTLV.length);

      // Look for Next TLV Index
      try {
        iIndex = nextTLVIndex(bTLV, iIndex, iNumOfByteLength);
      } catch (TlvException e) {
        e.printStackTrace();
      }

      // Check for EOF for DOCSIS due to PADDING
      if ((iIndex < bTLV.length) && (BinaryConversion.byteToUnsignedInteger(bTLV[iIndex]) == 255)) {

        if (logger.isDebugEnabled()) logger.debug("TlvBuilder.isTLVConstruct() -> FOUND-EOF");

        return true;

        // This index would be out of bound if past total TLV Length
      } else if (iIndex > bTLV.length) {

        if (logger.isDebugEnabled())
          logger.debug(
              "TlvVariableBinding.isTLVConstruct() -> "
                  + " Index Exceeds Length"
                  + " -> "
                  + "iIndex: "
                  + iIndex
                  + " -> "
                  + "bTLV.length: "
                  + bTLV.length);

        return Boolean.FALSE;

        // Just for DEBUG Purpose
      } else {

        if (logger.isDebugEnabled())
          logger.debug(
              "TlvVariableBinding.isTLVConstruct() -> iIndex: "
                  + iIndex
                  + " -> bTLV.length: "
                  + bTLV.length);
      }
    }

    return returnStatus;
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

    if (iInitalPosition < 0)
      throw new TlvException("TlvVariableBinding.nextTLVIndex() Type Less than 0");

    if (bTlvBuffer == null)
      throw new TlvException("TlvVariableBinding.nextTLVIndex() Byte Array is NULL");

    if (iByteLength < 0)
      throw new TlvException("TlvVariableBinding.nextTLVIndex() Byte Length Less than 0");

    ByteArrayOutputStream baosTlvByteLength = new ByteArrayOutputStream();

    baosTlvByteLength.write(bTlvBuffer, iInitalPosition + 1, iByteLength);

    int iTlvLength = new HexString(baosTlvByteLength.toByteArray()).toInteger();

    if (logger.isDebugEnabled())
      logger.debug("TlvVariableBinding.nextTLVIndex(b,i,i) -> TLV-LENGTH: " + iTlvLength);

    return (iInitalPosition + (iTlvLength + iByteLength) + TLV_TYPE_OVERHEAD);
  }

  /**
   * @param _bTLV
   */
  private void updateTlvByteBuffers(byte[] _bTLV) {
    ByteArrayOutputStream baosTlv = new ByteArrayOutputStream();

    // Need to make sure if this is a Null when the constructor calls this method
    if (bTlvBuffer != null) {
      try {
        baosTlv.write(this.bTlvBuffer);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      baosTlv.write(_bTLV);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Update lbTlvBuffer
    this.lbTlvBuffer.add(_bTLV);

    // Update bTlvBuffer
    this.bTlvBuffer = baosTlv.toByteArray();
  }

  /**
   * @param iType
   * @param iNumberByteLength
   */
  private void updateTlvByteLength(int iType, int iNumberByteLength) {
    this.miiTypeBytelength.put(iType, iNumberByteLength);
  }

  /**************************
   * For Deep Copy Cloning
   * @param miiTypeBytelength Map<Integer,Integer>
   * @param lbTlvBuffer List<byte[]>
   * @param bTlvBuffer byte[]
   *************************/
  /**
   * @param miiTypeBytelength
   * @param lbTlvBuffer
   * @param bTlvBuffer
   */
  private void updateMapsAndListsForCloning(
      Map<Integer, Integer> miiTypeBytelength, List<byte[]> lbTlvBuffer, byte[] bTlvBuffer) {

    this.miiTypeBytelength.putAll(miiTypeBytelength);

    this.lbTlvBuffer.addAll(lbTlvBuffer);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    if (this.bTlvBuffer != null) {
      try {
        baos.write(bTlvBuffer);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (bTlvBuffer != null) {

      try {
        baos.write(bTlvBuffer);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    this.bTlvBuffer = baos.toByteArray();
  }

  /** */
  private void typeToMapInit() {
    miiTypeBytelength.put((int) PacketCableConstants.SNMP_TLV_64, 2);
  }
}
