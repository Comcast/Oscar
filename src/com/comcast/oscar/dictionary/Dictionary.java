/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import org.json.JSONArray;
import org.json.JSONObject;

public interface Dictionary {

  public static final String DB_TBL_COL_ID = "ID";
  public static final String DB_TBL_COL_TYPE = "TYPE";
  public static final String DB_TBL_COL_PARENT_ID = "PARENT_ID";
  public static final String DB_TBL_COL_TLV_NAME = "TLV_NAME";
  public static final String DB_TBL_COL_DATA_TYPE = "DATA_TYPE";
  public static final String DB_TBL_COL_TLV_DESCRIPTION = "TLV_DESCRIPTION";
  public static final String DB_TBL_COL_LENGTH_MIN = "LENGTH_MIN";
  public static final String DB_TBL_COL_LENGTH_MAX = "LENGTH_MAX";
  public static final String DB_TBL_COL_SUPPORTED_VERSIONS = "MIN_SUPPORTED_CONFIG_FILE_TYPE";
  public static final String DB_TBL_COL_BYTE_LENGTH = "BYTE_LENGTH";

  public static final String TYPE = "Type";
  public static final String TLV_NAME = "TlvName";
  public static final String DATA_TYPE = "DataType";
  public static final String TLV_DESCRIPTION = "TlvDescription";
  public static final String LENGTH_MIN = "LengthMin";
  public static final String LENGTH_MAX = "LengthMax";
  public static final String SUPPORTED_VERSIONS = "SupportedVersion";
  public static final String ARE_SUBTYPES = "AreSubTypes";
  public static final String SUBTYPE_ARRAY = "SubTypeArray";
  public static final String PARENT_TYPE_LIST = "ParentTypeList";
  public static final String VALUE = "Value";
  public static final String BYTE_LENGTH = "ByteLength";

  public static final Integer DOCSIS = 0;
  public static final Integer PACKET_CABLE = 1;
  public static final Integer DPOE = 2;

  /* Needed to Fix an Issue where More than 1 SubTLV is found */
  public static final String MULTI_SUB_TLV_INSTANCE = "MULTI_SUB_TLV_INSTANCE";

  /* *****************************************************************************
   *  								Methods
   ******************************************************************************/

  /**
   * @param iType
   */
  public void getMajorTlvDefinition(Integer iType);

  /**
   * @param iType
   * @return JSONObject
   */
  public JSONObject getTlvDefinition(Integer iType);

  /**
   * @param iCableLabsConfigType
   * @return JSONArray
   */
  public JSONArray getAllTlvDefinition(int iCableLabsConfigType);
}
