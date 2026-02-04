package com.comcast.oscar.dictionary;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.sql.SqlConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class DictionarySQLQueries extends SqlConnection {

  private static final Logger logger = LogManager.getLogger(DictionarySQLQueries.class);

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

  private static final Integer TOP_LEVEL_TLV = 0;

  private Connection commSqlConnection;

  private String sDictionaryTableName = null;

  private Map<Integer, Integer> miiTypeByteLength = new HashMap<Integer, Integer>();

  private Map<String, Integer> msiTopLevelTypeNameToType = new HashMap<String, Integer>();

  /*	**************************************************************************************************
   * 										Constructors
   * **************************************************************************************************/

  /**
   * @param commSqlConnection
   * @param sDictionaryTableName
   */
  public DictionarySQLQueries(Connection commSqlConnection, String sDictionaryTableName) {

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries(comm,s) -> " + sDictionaryTableName);

    this.commSqlConnection = commSqlConnection;

    this.sDictionaryTableName = sDictionaryTableName;

    init();
  }

  /**
   * @param sDictionaryTableName
   */
  public DictionarySQLQueries(String sDictionaryTableName) {

    if (logger.isDebugEnabled()) logger.debug("DictionarySQLQueries(s) -> " + sDictionaryTableName);

    this.commSqlConnection = super.getConnectionID();

    this.sDictionaryTableName = sDictionaryTableName;

    init();
  }

  /** */
  public DictionarySQLQueries() {

    if (logger.isDebugEnabled()) logger.debug("DictionarySQLQueries() -> " + sDictionaryTableName);

    this.commSqlConnection = super.getConnectionID();

    this.sDictionaryTableName = "";

    init();
  }

  /*	**************************************************************************************************
   * 													Public
   * **************************************************************************************************/

  /**
   * @param iType
   * @return JSONObject
   */
  public JSONObject getTlvDictionary(int iType) {
    return getTlvDefinition(iType);
  }

  /**
   * @param iType
   */
  public void getMajorTlvDefinition(Integer iType) {

    Statement statement = null;
    ResultSet resultSet = null;

    String sqlQuery =
        "SELECT * "
            + "FROM "
            + this.sDictionaryTableName
            + " "
            + "WHERE "
            + "TYPE = '"
            + iType
            + "'"
            + " AND "
            + "PARENT_ID = '0'";

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.getMajorTlvDefinition() SQL_QUERY: " + sqlQuery);

    try {
      statement = commSqlConnection.createStatement();
    } catch (SQLException e) {

      e.printStackTrace();
    }

    try {
      resultSet = statement.executeQuery(sqlQuery);

      if (logger.isDebugEnabled()) {

        while (resultSet.next()) {
          logger.debug(
              "DictionarySQLQueries.getMajorTlvDefinition()-> TLV-NAME: "
                  + resultSet.getString("TLV_NAME"));
        }
      }

    } catch (SQLException e) {

      e.printStackTrace();
    }
  }

  /**
   * @param iType
   * @return JSONObject
   */
  public JSONObject getTlvDefinition(int iType) {

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.getTlvDefinition() -> TLV-TYPE: " + iType);

    JSONObject jsonObj = null;

    ArrayList<Integer> aliType = new ArrayList<Integer>();
    aliType.add(-1);

    try {
      jsonObj =
          recursiveTlvDefinitionBuilder(getMajorParentTlvRowId(iType), TOP_LEVEL_TLV, aliType);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.getTlvDefinition() -> JSON-Object: " + jsonObj);

    return jsonObj;
  }

  /**
   * @param iCableLabsConfigType
   * @return JSONArray
   */
  public JSONArray getAllTlvDefinition(int iCableLabsConfigType) {

    JSONArray jsonArrTlvDictionary = new JSONArray();

    if (Constants.CONFIGURATION_FILE_TYPE_DOCSIS == iCableLabsConfigType) {

      for (int tlvCounter = Constants.DOCSIS_TLV_MIN;
          tlvCounter <= Constants.DOCSIS_TLV_MAX;
          tlvCounter++) {

        if (logger.isDebugEnabled()) {
          logger.debug(
              "+------------------------------------------------------------------------+");
          logger.debug(
              "DictionarySQLQueries.getAllTlvDefinition() TLV: "
                  + tlvCounter
                  + " -> JSON-TLV-DICT: "
                  + getTlvDefinition(tlvCounter));
        }

        jsonArrTlvDictionary.put(getTlvDefinition(tlvCounter));
      }

    } else if (ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE
        == iCableLabsConfigType) {

      for (int tlvCounter = Constants.DOCSIS_TLV_MIN;
          tlvCounter <= Constants.DOCSIS_TLV_MAX;
          tlvCounter++) {

        if (logger.isDebugEnabled()) {
          logger.debug(
              "+------------------------------------------------------------------------+");
          logger.debug(
              "DictionarySQLQueries.getAllTlvDefinition() TLV: "
                  + tlvCounter
                  + " -> JSON-TLV-DICT: "
                  + getTlvDefinition(tlvCounter));
        }

        jsonArrTlvDictionary.put(getTlvDefinition(tlvCounter));
      }

    } else if (ConfigurationFileTypeConstants.PKT_CABLE_15_CONFIGURATION_TYPE
        == iCableLabsConfigType) {

      for (int tlvCounter = Constants.DOCSIS_TLV_MIN;
          tlvCounter <= Constants.DOCSIS_TLV_MAX;
          tlvCounter++) {

        if (logger.isDebugEnabled()) {
          logger.debug(
              "+------------------------------------------------------------------------+");
          logger.debug(
              "DictionarySQLQueries.getAllTlvDefinition() TLV: "
                  + tlvCounter
                  + " -> JSON-TLV-DICT: "
                  + getTlvDefinition(tlvCounter));
        }

        jsonArrTlvDictionary.put(getTlvDefinition(tlvCounter));
      }

    } else if (ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE
        == iCableLabsConfigType) {

      for (int tlvCounter = Constants.DOCSIS_TLV_MIN;
          tlvCounter <= Constants.DOCSIS_TLV_MAX;
          tlvCounter++) {

        if (logger.isDebugEnabled()) {
          logger.debug(
              "+------------------------------------------------------------------------+");
          logger.debug(
              "DictionarySQLQueries.getAllTlvDefinition() TLV: "
                  + tlvCounter
                  + " -> JSON-TLV-DICT: "
                  + getTlvDefinition(tlvCounter));
        }

        jsonArrTlvDictionary.put(getTlvDefinition(tlvCounter));
      }
    } else if (ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE == iCableLabsConfigType) {

      for (int tlvCounter = Constants.DOCSIS_TLV_MIN;
          tlvCounter <= Constants.DOCSIS_TLV_MAX;
          tlvCounter++) {

        if (logger.isDebugEnabled()) {
          logger.debug(
              "+------------------------------------------------------------------------+");
          logger.debug(
              "DictionarySQLQueries.getAllTlvDefinition() TLV: "
                  + tlvCounter
                  + " -> JSON-TLV-DICT: "
                  + getTlvDefinition(tlvCounter));
        }

        jsonArrTlvDictionary.put(getTlvDefinition(tlvCounter));
      }
    }

    return jsonArrTlvDictionary;
  }

  /**
   * @return List of TopLevel TLV in Integers
   */
  public List<Integer> getTopLevelTLV() {
    Statement statement = null;
    ResultSet resultSet = null;

    List<Integer> liTopLevelTLV = new ArrayList<Integer>();

    String sqlQuery =
        "SELECT * "
            + "FROM "
            + this.sDictionaryTableName
            + " "
            + "WHERE "
            + Dictionary.DB_TBL_COL_PARENT_ID
            + " = '0'";

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.getTopLevelTLV() SQL_QUERY: " + sqlQuery);

    try {
      statement = commSqlConnection.createStatement();
    } catch (SQLException e) {

      e.printStackTrace();
    }

    try {
      resultSet = statement.executeQuery(sqlQuery);

      while (resultSet.next()) {

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.getTopLevelTLV()-> TLV-NAME: "
                  + resultSet.getString("TLV_NAME"));

        liTopLevelTLV.add(resultSet.getInt(Dictionary.DB_TBL_COL_TYPE));
      }

    } catch (SQLException e) {

      e.printStackTrace();
    }

    return liTopLevelTLV;
  }

  /**
   * @return Map<Integer,Integer>
   */
  public Map<Integer, Integer> getTopLevelByteLength() {

    // To reduce DB lookup, if a Map has already been created return
    if (!miiTypeByteLength.isEmpty()) return miiTypeByteLength;

    Statement statement = null;
    ResultSet resultSet = null;

    String sqlQuery =
        "SELECT * " + "FROM " + this.sDictionaryTableName + " " + "WHERE " + "PARENT_ID = '0'";

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.getTopLevelByteLength() SQL_QUERY: " + sqlQuery);

    try {
      statement = commSqlConnection.createStatement();
    } catch (SQLException e) {

      e.printStackTrace();
    }

    try {
      resultSet = statement.executeQuery(sqlQuery);

      while (resultSet.next()) {

        miiTypeByteLength.put(
            resultSet.getInt(Dictionary.DB_TBL_COL_TYPE),
            resultSet.getInt(Dictionary.DB_TBL_COL_BYTE_LENGTH));

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.getTopLevelByteLength() -> "
                  + "TYPE: "
                  + resultSet.getInt("TYPE")
                  + " -> "
                  + "BYTE_LENGTH: "
                  + resultSet.getInt("BYTE_LENGTH"));
      }

    } catch (SQLException e) {

      e.printStackTrace();
    }

    return miiTypeByteLength;
  }

  /**
   * @return Map<String,Integer>
   */
  public Map<String, Integer> getAllTopLevelTypeNameToType() {

    // To reduce DB lookup, if this List has already been created return
    if (!msiTopLevelTypeNameToType.isEmpty()) return msiTopLevelTypeNameToType;

    Statement statement = null;
    ResultSet resultSet = null;

    String sqlQuery =
        "SELECT * " + "FROM " + this.sDictionaryTableName + " " + "WHERE " + "PARENT_ID = '0'";

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.getAllTopLevelTypeNameToType() SQL_QUERY: " + sqlQuery);

    try {
      statement = commSqlConnection.createStatement();
    } catch (SQLException e) {

      e.printStackTrace();
    }

    try {
      resultSet = statement.executeQuery(sqlQuery);

      while (resultSet.next()) {

        msiTopLevelTypeNameToType.put(resultSet.getString("TLV_NAME"), resultSet.getInt("TYPE"));

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.getAllTopLevelType() -> "
                  + "TYPE: "
                  + resultSet.getInt("TYPE"));
      }

    } catch (SQLException e) {

      e.printStackTrace();
    }

    return msiTopLevelTypeNameToType;
  }

  /**
   * @return Get DictionaryTableName
   */
  public String getDictionaryTableName() {
    return sDictionaryTableName;
  }

  /* ******************************************************************************************************
   * 										Private Methods
   *******************************************************************************************************/

  /**
   * @param iType
   * @return Integer
   */
  private Integer getMajorParentTlvRowId(Integer iType) {

    Statement statement = null;
    ResultSet resultSet = null;
    Integer iPrimaryKey = -1;

    String sqlQuery =
        "SELECT ID "
            + "FROM "
            + this.sDictionaryTableName
            + " "
            + "WHERE "
            + "TYPE = '"
            + iType
            + "'"
            + " AND "
            + "PARENT_ID = '0'";

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.getMajorParentTlvRowId() SQL_QUERY: " + sqlQuery);

    try {
      statement = commSqlConnection.createStatement();
    } catch (SQLException e) {
      logger.debug("DictionarySQLQueries.getMajorParentTlvRowId() SQLException: " + e.getMessage());
      e.printStackTrace();
    }

    try {

      resultSet = statement.executeQuery(sqlQuery);

      while (resultSet.next()) iPrimaryKey = resultSet.getInt("ID");

    } catch (SQLException e) {

      e.printStackTrace();
    }

    if (logger.isDebugEnabled())
      logger.debug(
          "DictionarySQLQueries.getMajorParentTlvRowId() SQL_QUERY-PRI-KEY-RTN: " + iPrimaryKey);

    return iPrimaryKey;
  }

  /**
   * @param iRowID
   * @return Integer
   */
  @SuppressWarnings("unused")
  private Integer getTypeFromRowID(int iRowID) {

    Statement statement = null;

    ResultSet resultSet = null;

    Integer iPrimaryKey = -1;

    String sqlQuery =
        "SELECT DISTINCT "
            + "TYPE "
            + "FROM "
            + this.sDictionaryTableName
            + " "
            + "WHERE "
            + "ID = '"
            + iRowID
            + "'";

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.getTypeFromRowID() SQL_QUERY: " + sqlQuery);

    try {
      statement = commSqlConnection.createStatement();
    } catch (SQLException e) {

      e.printStackTrace();
    }

    try {

      resultSet = statement.executeQuery(sqlQuery);

      while (resultSet.next()) iPrimaryKey = resultSet.getInt("TYPE");

    } catch (SQLException e) {

      e.printStackTrace();
    }

    return iPrimaryKey;
  }

  /*
  *

  *
  */

  /**
   * +------------------------------+----------------------+------+-----+---------+----------------+
   * | Field | Type | Null | Key | Default | Extra |
   * +------------------------------+----------------------+------+-----+---------+----------------+
   * | ID | int(10) unsigned | NO | PRI | NULL | auto_increment | | TYPE | smallint(5) unsigned | NO
   * | | NULL | | | PARENT_ID | int(10) unsigned | YES | | NULL | | | TLV_NAME | varchar(50) | NO |
   * | NULL | | | TLV_DESCRIPTION | text | NO | | NULL | | | LENGTH_MIN | smallint(5) unsigned | NO
   * | | NULL | | | LENGTH_MAX | smallint(5) unsigned | NO | | NULL | | | DATA_TYPE | varchar(50) |
   * YES | | NULL | | | BYTE_LENGTH | tinyint(3) unsigned | NO | | NULL | | |
   * DB_TBL_COL_SUPPORTED_VERSIONS | varchar(50) | NO | | NULL | |
   * +------------------------------+----------------------+------+-----+---------+----------------+
   *
   * <p>
   *
   * @param iRowID
   * @param iParentID
   * @param aliTlvEncodeHistory
   * @return JSONObject
   * @throws JSONException
   */
  private JSONObject recursiveTlvDefinitionBuilder(
      Integer iRowID, Integer iParentID, ArrayList<Integer> aliTlvEncodeHistory)
      throws JSONException {

    Statement parentCheckStatement = null, getRowDefinitionStatement = null;

    ResultSet resultSetParentCheck = null, resultSetGetRowDefinition = null;

    if (logger.isDebugEnabled())
      logger.debug(
          "DictionarySQLQueries.recursiveTlvDefinitionBuilder(START) -> "
              + "aliTlvEncodeHistory: "
              + aliTlvEncodeHistory);

    String sqlQuery;

    JSONObject tlvJsonObj = null;

    // This query will check for child rows that belong to a parent row
    String sParentCheckQuery =
        "SELECT "
            + "	ID ,"
            + "	TYPE ,"
            + "	TLV_NAME,"
            + "	PARENT_ID "
            + "FROM "
            + this.sDictionaryTableName
            + " "
            + "WHERE "
            + "	PARENT_ID = '"
            + iRowID
            + "'";

    try {

      // Create statement
      parentCheckStatement = commSqlConnection.createStatement();

      // Get Result Set of Query
      resultSetParentCheck = parentCheckStatement.executeQuery(sParentCheckQuery);

    } catch (SQLException e) {
      e.printStackTrace();
    }

    /* ******************************************************************************************************
     * If resultSet return an empty, this means that the row does not have a child and is not a Major TLV
     * *****************************************************************************************************/

    try {

      if (
      /*(SqlConnection.getRowCount(resultSetParentCheck) == 0) && */ (iParentID != TOP_LEVEL_TLV)) {

        // This query will check for child rows that belong to a parent row
        sqlQuery = "SELECT * FROM " + this.sDictionaryTableName + " 	WHERE ID = '" + iRowID + "'";

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.recursiveTlvDefinitionBuilder(SUB-TLV-BEFORE) -> "
                  + "ROW-ID: "
                  + iRowID);

        // Create statement to get Rows from ROW ID's
        getRowDefinitionStatement = commSqlConnection.createStatement();

        // Get Result Set
        resultSetGetRowDefinition = getRowDefinitionStatement.executeQuery(sqlQuery);

        // Advance to next index in result set, this is needed
        resultSetGetRowDefinition.next();

        /*************************************************
         * 				Assemble JSON OBJECT
         *************************************************/

        tlvJsonObj = new JSONObject();

        tlvJsonObj.put(
            Dictionary.TYPE, resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_TYPE));
        tlvJsonObj.put(
            Dictionary.TLV_NAME,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_TLV_NAME));
        tlvJsonObj.put(
            Dictionary.TLV_DESCRIPTION,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_TLV_DESCRIPTION));
        tlvJsonObj.put(
            Dictionary.LENGTH_MIN,
            resultSetGetRowDefinition.getInt(Dictionary.DB_TBL_COL_LENGTH_MIN));
        tlvJsonObj.put(
            Dictionary.LENGTH_MAX,
            resultSetGetRowDefinition.getInt(Dictionary.DB_TBL_COL_LENGTH_MAX));
        tlvJsonObj.put(
            Dictionary.SUPPORTED_VERSIONS,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_SUPPORTED_VERSIONS));
        tlvJsonObj.put(
            Dictionary.DATA_TYPE,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_DATA_TYPE));
        tlvJsonObj.put(Dictionary.ARE_SUBTYPES, false);
        tlvJsonObj.put(
            Dictionary.BYTE_LENGTH,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_BYTE_LENGTH));

        if (checkForChild(iRowID)) {

          if (logger.isDebugEnabled())
            logger.debug(
                "DictionarySQLQueries.recursiveTlvDefinitionBuilder(SUB-TLV-BEFORE) -> "
                    + "ROW-ID: "
                    + iRowID
                    + " -> CHILD-FOUND");

          aliTlvEncodeHistory.add(resultSetGetRowDefinition.getInt("TYPE"));

        } else {

          if (logger.isDebugEnabled())
            logger.debug(
                "DictionarySQLQueries.recursiveTlvDefinitionBuilder(SUB-TLV-BEFORE) -> "
                    + "ROW-ID: "
                    + iRowID
                    + " -> CHILD-NOT-FOUND");

          tlvJsonObj.put(Dictionary.PARENT_TYPE_LIST, aliTlvEncodeHistory);

          return tlvJsonObj;
        }

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.recursiveTlvDefinitionBuilder(SUB-TLV-BEFORE) -> "
                  + "aliTlvEncodeHistory: "
                  + aliTlvEncodeHistory);

        tlvJsonObj.put(Dictionary.PARENT_TYPE_LIST, aliTlvEncodeHistory);

        aliTlvEncodeHistory.remove(aliTlvEncodeHistory.size() - 1);

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.recursiveTlvDefinitionBuilder(SUB-TLV-AFTER) -> "
                  + " aliTlvEncodeHistory: "
                  + aliTlvEncodeHistory);

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.recursiveTlvDefinitionBuilder(SUB-TLV) "
                  + tlvJsonObj.toString());

      } else if (iParentID == TOP_LEVEL_TLV) {

        // This query will check for child rows that belong to a parent row
        sqlQuery = "SELECT * FROM " + this.sDictionaryTableName + "	WHERE ID = '" + iRowID + "'";

        getRowDefinitionStatement = commSqlConnection.createStatement();

        resultSetGetRowDefinition = getRowDefinitionStatement.executeQuery(sqlQuery);

        /*************************************************
         * 				Assemble JSON OBJECT
         *************************************************/

        tlvJsonObj = new JSONObject();

        if (!resultSetGetRowDefinition.next()) {
          return tlvJsonObj;
        }

        tlvJsonObj.put(
            Dictionary.TYPE, resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_TYPE));
        tlvJsonObj.put(
            Dictionary.TLV_NAME,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_TLV_NAME));
        tlvJsonObj.put(
            Dictionary.TLV_DESCRIPTION,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_TLV_DESCRIPTION));
        tlvJsonObj.put(
            Dictionary.LENGTH_MIN,
            resultSetGetRowDefinition.getInt(Dictionary.DB_TBL_COL_LENGTH_MIN));
        tlvJsonObj.put(
            Dictionary.LENGTH_MAX,
            resultSetGetRowDefinition.getInt(Dictionary.DB_TBL_COL_LENGTH_MAX));
        tlvJsonObj.put(
            Dictionary.SUPPORTED_VERSIONS,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_SUPPORTED_VERSIONS));
        tlvJsonObj.put(
            Dictionary.DATA_TYPE,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_DATA_TYPE));
        tlvJsonObj.put(Dictionary.ARE_SUBTYPES, false);
        tlvJsonObj.put(
            Dictionary.BYTE_LENGTH,
            resultSetGetRowDefinition.getString(Dictionary.DB_TBL_COL_BYTE_LENGTH));

        aliTlvEncodeHistory.add(resultSetGetRowDefinition.getInt(Dictionary.DB_TBL_COL_TYPE));
        tlvJsonObj.put(Dictionary.PARENT_TYPE_LIST, aliTlvEncodeHistory);

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.recursiveTlvDefinitionBuilder(TOP_LEVEL_TLV) "
                  + tlvJsonObj.toString());
      }

      if ((SqlConnection.getRowCount(sParentCheckQuery) > 0)) {

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.recursiveTlvDefinitionBuilder(END) -> "
                  + "resultSetParentCheck Fetch Size: "
                  + resultSetParentCheck.getFetchSize()
                  + " -> "
                  +
                  // "SqlConnection.getRowCount: " + SqlConnection.getRowCount(resultSetParentCheck)
                  // + " -> " +
                  "aliTlvEncodeHistory: "
                  + aliTlvEncodeHistory);

        try {

          Integer iParentIdTemp = null;

          JSONArray tlvJsonArray = new JSONArray();

          while (resultSetParentCheck.next()) {

            iParentIdTemp = resultSetParentCheck.getInt(Dictionary.DB_TBL_COL_PARENT_ID);

            aliTlvEncodeHistory.add(resultSetParentCheck.getInt(Dictionary.DB_TBL_COL_TYPE));

            if (logger.isDebugEnabled())
              logger.debug(
                  "DictionarySQLQueries.recursiveTlvDefinitionBuilder(FINAL) -> "
                      + "resultSetParentCheck Fetch Size: "
                      + resultSetParentCheck.getFetchSize()
                      + " -> "
                      + "aliTlvEncodeHistory: "
                      + aliTlvEncodeHistory);

            // Keep processing each row using recursion until you get to to the bottom of the tree
            tlvJsonArray.put(
                recursiveTlvDefinitionBuilder(
                    resultSetParentCheck.getInt("ID"), iParentIdTemp, aliTlvEncodeHistory));

            aliTlvEncodeHistory.remove(aliTlvEncodeHistory.size() - 1);
          }

          // Get Parent Definition
          tlvJsonObj = getRowDefinitionViaRowId(iParentIdTemp);

          tlvJsonObj.put(Dictionary.SUBTYPE_ARRAY, tlvJsonArray);

        } catch (SQLException e) {

          e.printStackTrace();
        }
      }

    } catch (SQLException e) {

      e.printStackTrace();
    }

    return tlvJsonObj;
  }

  /**
   * @param iRowID
   * @return boolean
   */
  private boolean checkForChild(Integer iRowID) {

    boolean foundChild = true;

    Statement parentCheckStatement = null;

    ResultSet resultSetParentCheck = null;

    // This query will check for child rows that belong to a parent row
    String sqlQuery =
        "SELECT "
            + "	ID ,"
            + "	TYPE ,"
            + "	TLV_NAME,"
            + "	PARENT_ID "
            + "FROM "
            + this.sDictionaryTableName
            + " WHERE "
            + " PARENT_ID = '"
            + iRowID
            + "'";

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.checkForChild() -> SQL: " + sqlQuery);

    try {
      parentCheckStatement = commSqlConnection.createStatement();

      resultSetParentCheck = parentCheckStatement.executeQuery(sqlQuery);

      resultSetParentCheck.next();

      if (resultSetParentCheck.getRow() == 0) {
        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.checkForChild() -> NO-CHILD-FOUND FOR ROW-ID: " + iRowID);
        return false;
      } else {
        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.checkForChild() -> NO-CHILD-FOUND FOR ROW-ID: " + iRowID);
      }

    } catch (SQLException e) {

      e.printStackTrace();
    }

    return foundChild;
  }

  /**
   * @param iRowID
   * @return JSONObject
   * @throws JSONException
   */
  private JSONObject getRowDefinitionViaRowId(Integer iRowID) throws JSONException {

    Statement parentCheckStatement = null;

    ResultSet resultSetParentCheck = null;

    JSONObject tlvJsonObj = null;

    // This query will check for child rows that belong to a parent row
    String sqlQuery =
        "SELECT * FROM " + this.sDictionaryTableName + "  WHERE 	ID = '" + iRowID + "'";

    try {
      parentCheckStatement = commSqlConnection.createStatement();

      resultSetParentCheck = parentCheckStatement.executeQuery(sqlQuery);

      // resultSetParentCheck.next();

      if (!resultSetParentCheck.next()) {
        return tlvJsonObj;
      }

      /*************************************************
       * 				Assemble JSON OBJECT
       *************************************************/

      tlvJsonObj = new JSONObject();

      tlvJsonObj.put(Dictionary.TYPE, resultSetParentCheck.getString(Dictionary.DB_TBL_COL_TYPE));
      tlvJsonObj.put(
          Dictionary.TLV_NAME, resultSetParentCheck.getString(Dictionary.DB_TBL_COL_TLV_NAME));
      tlvJsonObj.put(
          Dictionary.TLV_DESCRIPTION,
          resultSetParentCheck.getString(Dictionary.DB_TBL_COL_TLV_DESCRIPTION));
      tlvJsonObj.put(
          Dictionary.LENGTH_MIN, resultSetParentCheck.getInt(Dictionary.DB_TBL_COL_LENGTH_MIN));
      tlvJsonObj.put(
          Dictionary.LENGTH_MAX, resultSetParentCheck.getInt(Dictionary.DB_TBL_COL_LENGTH_MAX));
      tlvJsonObj.put(
          Dictionary.SUPPORTED_VERSIONS,
          resultSetParentCheck.getString(Dictionary.DB_TBL_COL_SUPPORTED_VERSIONS));
      tlvJsonObj.put(
          Dictionary.DATA_TYPE, resultSetParentCheck.getString(Dictionary.DB_TBL_COL_DATA_TYPE));
      tlvJsonObj.put(Dictionary.ARE_SUBTYPES, true);
      tlvJsonObj.put(
          Dictionary.BYTE_LENGTH,
          resultSetParentCheck.getString(Dictionary.DB_TBL_COL_BYTE_LENGTH));

      if (logger.isDebugEnabled()) logger.debug(tlvJsonObj.toString());

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tlvJsonObj;
  }

  /**
   * @param sCableLabsDictionaryTableName
   */
  public void updateDictionaryTablename(String sCableLabsDictionaryTableName) {

    if (logger.isDebugEnabled()) {
      logger.debug(
          "DictionarySQLQueries.updateDictionaryTablename() -> " + sCableLabsDictionaryTableName);
    }

    this.sDictionaryTableName = sCableLabsDictionaryTableName;
  }

  /**
   * @param sQueryType String
   * @return Map<Integer,Integer>
   */
  public Map<Integer, Integer> getTypeToByteLengthMap(String sQueryType) {

    Map<Integer, Integer> miiGenericTypeToByteLength = new HashMap<Integer, Integer>();

    Statement statement = null;
    ResultSet resultSet = null;

    String sqlQuery = "SELECT * " + "FROM " + sQueryType + " " + "WHERE " + "PARENT_ID = '0'";

    if (logger.isDebugEnabled())
      logger.debug("DictionarySQLQueries.getTypeToByteLengthMap() SQL_QUERY: " + sqlQuery);

    try {
      statement = commSqlConnection.createStatement();
    } catch (SQLException e) {

      e.printStackTrace();
    }

    try {
      resultSet = statement.executeQuery(sqlQuery);

      while (resultSet.next()) {

        miiGenericTypeToByteLength.put(
            resultSet.getInt(Dictionary.DB_TBL_COL_TYPE),
            resultSet.getInt(Dictionary.DB_TBL_COL_BYTE_LENGTH));

        if (logger.isDebugEnabled())
          logger.debug(
              "DictionarySQLQueries.getTypeToByteLengthMap() -> "
                  + "TYPE: "
                  + resultSet.getInt("TYPE")
                  + " -> "
                  + "BYTE_LENGTH: "
                  + resultSet.getInt("BYTE_LENGTH"));
      }

    } catch (SQLException e) {

      e.printStackTrace();
    }

    return miiGenericTypeToByteLength;
  }

  /** */
  private void init() {

    if (miiTypeByteLength == null) miiTypeByteLength = new HashMap<Integer, Integer>();
  }
}
