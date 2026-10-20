/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TlvBuild {

  /** Track all the added TLV Byte Arrays */
  List<byte[]> bTlvBuilder = new ArrayList<byte[]>();

  /** Track the TopLevel TLV to its Respected Num Of Byte Length */
  Map<Integer, Integer> miiTypeBytelength = new HashMap<Integer, Integer>();

  /**
   * Method getTopLevelTlvList.
   *
   * @param miiTypeByteLength Map<Integer,Integer>
   * @return List<Integer>
   */
  public List<Integer> getTopLevelTlvList(Map<Integer, Integer> miiTypeByteLength);

  /**
   * Method getTopLevelTlvList.
   *
   * @return List<Integer>
   */
  public List<Integer> getTopLevelTlvList();

  /**
   * Method toByteArray.
   *
   * @return byte[]
   */
  public byte[] toByteArray();

  /**
   * Method add.
   *
   * @param iTlvType int
   * @param bValue byte[]
   * @throws TlvException
   */
  public void add(int iTlvType, byte[] bValue) throws TlvException;

  /**
   * Method isEmpty.
   *
   * @return Boolean
   */
  public Boolean isEmpty();

  public void clear();

  /**
   * Method getMapTypeToByteLength.
   *
   * @return Map<Integer,Integer>
   */
  public Map<Integer, Integer> getMapTypeToByteLength();
}
