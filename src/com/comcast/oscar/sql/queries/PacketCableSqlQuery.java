package com.comcast.oscar.sql.queries;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.sql.SqlConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
public class PacketCableSqlQuery {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(PacketCableSqlQuery.class);

  private Connection sqlConnection;

  public static final Integer MAJOR_TLV = 0;

  /**
   * @param sqlConnection
   */
  public PacketCableSqlQuery(Connection sqlConnection) {
    this.sqlConnection = sqlConnection;
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
            + "PACKET_CABLE_TLV_DEFINITION "
            + "WHERE "
            + "TYPE = '"
            + iType
            + "'"
            + " AND "
            + "PARENT_ID = '0'";

    if (logger.isDebugEnabled()) logger.debug("getMajorTlvDefinition() SQL_QUERY: " + sqlQuery);

    try {
      statement = sqlConnection.createStatement();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {
      resultSet = statement.executeQuery(sqlQuery);

      if (logger.isDebugEnabled()) {

        while (resultSet.next()) {
          logger.debug("MySQL-Query->TLV-NAME: " + resultSet.getString("TLV_NAME"));
        }
      }

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * @param iType
   * @return JSONObject
   */
  public JSONObject getTlvDefinition(Integer iType) {

    JSONObject jsonObj = null;

    try {
      jsonObj =
          recursiveTlvDefinitionBuilder(
              getMajorParentTlvRowId(iType), MAJOR_TLV, new ArrayList<Integer>());
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

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
          logger.debug("TLV: " + tlvCounter + " -> JSON-TLV-DICT: " + getTlvDefinition(tlvCounter));
        }
      }

    } else if (ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE
        == iCableLabsConfigType) {

    } else if (ConfigurationFileTypeConstants.PKT_CABLE_15_CONFIGURATION_TYPE
        == iCableLabsConfigType) {

    } else if (ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE
        == iCableLabsConfigType) {

    }

    return jsonArrTlvDictionary;
  }

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
            + "PACKET_CABLE_TLV_DEFINITION "
            + "WHERE "
            + "TYPE = '"
            + iType
            + "'"
            + "AND "
            + "PARENT_ID = '0'";

    if (logger.isDebugEnabled()) logger.debug("getMajorTlvDefinition() SQL_QUERY: " + sqlQuery);

    try {
      statement = sqlConnection.createStatement();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {

      resultSet = statement.executeQuery(sqlQuery);

      while (resultSet.next()) iPrimaryKey = resultSet.getInt("ID");

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return iPrimaryKey;
  }

  /**
   * @param iRowID
   * @return Integer
   */
  private Integer getTypeFromRowID(int iRowID) {
    Statement statement = null;
    ResultSet resultSet = null;
    Integer iPrimaryKey = -1;

    String sqlQuery =
        "SELECT DISTINCT "
            + "TYPE "
            + "FROM "
            + "PACKET_CABLE_TLV_DEFINITION "
            + "WHERE "
            + "ID = '"
            + iRowID
            + "'";

    if (logger.isDebugEnabled()) logger.debug("getMajorTlvDefinition() SQL_QUERY: " + sqlQuery);

    try {
      statement = sqlConnection.createStatement();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try {

      resultSet = statement.executeQuery(sqlQuery);

      while (resultSet.next()) iPrimaryKey = resultSet.getInt("TYPE");

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return iPrimaryKey;
  }

  /*
  *

  *
  */

  /**
   * 4 variable ClassOfService subtypes 7 1.0 (4,'4',0,'ClassOfService','',0,variable,integer,1.0);
   * s 1 1 ClassId uint8 1.0 (5,'1',4,'ClassId','',0,1,integer,1.0); s 2 4 MaxDownstreamRate uint32
   * 1.0 (6,'2',4,'MaxDownstreamRate','',0,4,integer,1.0); s 3 4 MaxUpstreamRate uint32 1.0
   * (7,'3',4,'MaxUpstreamRate','',0,4,integer,1.0); s 4 1 UpstreamChannelPriority uint8 1.0
   * (8,'4',4,'UpstreamChannelPriority','',0,1,integer,1.0); s 5 4 MinUpstreamRate uint32 1.0
   * (9,'5',4,'MinUpstreamRate','',0,4,integer,1.0); s 6 2 MaxUpstreamBurst uint16 1.0
   * (10,'6',4,'MaxUpstreamBurst','',0,2,integer,1.0); s 7 1 CoSPrivacyEnable boolean 1.0
   * (11,'7',4,'CoSPrivacyEnable','',0,1,integer,1.0);
   *
   * <p>24 variable UpstreamServiceFlow subtypes 28 1.1
   * (151,'24',0,'UpstreamServiceFlow','',0,variable,integer,1.1); s 1 2 SfReference uint16 1.1
   * (152,'1',151,'SfReference','',0,2,integer,1.1); s 4 variable SfClassName null_terminated_string
   * 1.1 (153,'4',151,'SfClassName','',0,variable,integer,1.1); s 5 variable SfErrorEncoding
   * subtypes 3 1.1 (154,'5',151,'SfErrorEncoding','',0,variable,integer,1.1); s s 1 1
   * ErroredParameter uint8 1.1 (155,'1',154,'ErroredParameter','',0,1,integer,1.1); s s 2 1
   * ErrorCode uint8 1.1 (156,'2',154,'ErrorCode','',0,1,integer,1.1); s s 3 variable ErrorMessage
   * null_terminated_string 1.1 (157,'3',154,'ErrorMessage','',0,variable,integer,1.1); s 6 1
   * SfQosSetType uint8 1.1 (158,'6',151,'SfQosSetType','',0,1,integer,1.1);
   *
   * <p>+------------------------------+----------------------+------+-----+---------+----------------+
   * | Field | Type | Null | Key | Default | Extra |
   * +------------------------------+----------------------+------+-----+---------+----------------+
   * | ID | int(10) unsigned | NO | PRI | NULL | auto_increment | | TYPE | smallint(5) unsigned | NO
   * | | NULL | | | PARENT_ID | int(10) unsigned | YES | | NULL | | | TLV_NAME | varchar(50) | NO |
   * | NULL | | | TLV_DESCRIPTION | text | NO | | NULL | | | LENGTH_MIN | smallint(5) unsigned | NO
   * | | NULL | | | LENGTH_MAX | smallint(5) unsigned | NO | | NULL | | | DATA_TYPE | varchar(50) |
   * YES | | NULL | | | BYTE_LENGTH | tinyint(3) unsigned | NO | | NULL | | |
   * MIN_SUPPORTED_DOCSIS_VERSION | varchar(50) | NO | | NULL | |
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

    aliTlvEncodeHistory.add(getTypeFromRowID(iParentID));

    String sqlQuery;

    JSONObject tlvJsonObj = null;

    // This query will check for child rows that belong to a parent row
    sqlQuery =
        "SELECT "
            + "	ID ,"
            + "	TYPE ,"
            + "	TLV_NAME,"
            + "	PARENT_ID "
            + "FROM "
            + "	PACKET_CABLE_TLV_DEFINITION "
            + "WHERE "
            + "	PARENT_ID = '"
            + iRowID
            + "'";

    try {

      // Create statement
      parentCheckStatement = sqlConnection.createStatement();

      // Get Result Set of Query
      resultSetParentCheck = parentCheckStatement.executeQuery(sqlQuery);

    } catch (SQLException e) {
      e.printStackTrace();
    }

    /* ******************************************************************************************************
     * If resultSet return an empty, this means that the row does not have a child and is not a Major TLV
     * *****************************************************************************************************/

    try {

      if ((SqlConnection.getRowCount(resultSetParentCheck) == 0) && (iParentID != MAJOR_TLV)) {

        // This query will check for child rows that belong to a parent row
        sqlQuery = "SELECT * FROM 	PACKET_CABLE_TLV_DEFINITION 	WHERE ID = '" + iRowID + "'";

        // Create statement to get Rows from ROW ID's
        getRowDefinitionStatement = sqlConnection.createStatement();

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
        tlvJsonObj.put(PacketCableSqlQueryConstants.PARENT_TYPE_LIST, aliTlvEncodeHistory);

        if (logger.isDebugEnabled()) logger.debug(tlvJsonObj.toString());

      } else if (iParentID == MAJOR_TLV) {

        // This query will check for child rows that belong to a parent row
        sqlQuery = "SELECT * FROM 	DOCSIS_TLV_DEFINITION 	WHERE ID = '" + iRowID + "'";

        getRowDefinitionStatement = sqlConnection.createStatement();

        resultSetGetRowDefinition = getRowDefinitionStatement.executeQuery(sqlQuery);

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
        tlvJsonObj.put(PacketCableSqlQueryConstants.PARENT_TYPE_LIST, aliTlvEncodeHistory);

        if (logger.isDebugEnabled()) logger.debug(tlvJsonObj.toString());
      }

      if (SqlConnection.getRowCount(resultSetParentCheck) > 0) {

        // This query will check for child rows that belong to a parent row
        sqlQuery =
            "SELECT "
                + "	TYPE , "
                + "	TLV_NAME,"
                + "	PARENT_ID"
                + " FROM "
                + "	DOCSIS_TLV_DEFINITION "
                + "	WHERE ID = '"
                + iRowID
                + "'";

        try {

          Integer iParentIdTemp = null;

          JSONArray tlvJsonArray = new JSONArray();

          while (resultSetParentCheck.next()) {

            iParentIdTemp = resultSetParentCheck.getInt("PARENT_ID");

            ArrayList<Integer> aliTlvEncodeHistoryNext = new ArrayList<Integer>();

            aliTlvEncodeHistoryNext.addAll(aliTlvEncodeHistory);

            if (logger.isDebugEnabled())
              logger.debug("aliTlvEncodeHistoryNext: " + aliTlvEncodeHistoryNext);

            aliTlvEncodeHistoryNext.remove(aliTlvEncodeHistoryNext.size() - 1);

            // Keep processing each row using recursion until you get to to the bottom of the tree
            tlvJsonArray.put(
                recursiveTlvDefinitionBuilder(
                    resultSetParentCheck.getInt(Dictionary.DB_TBL_COL_ID),
                    iParentIdTemp,
                    aliTlvEncodeHistoryNext));
          }

          // Get Parent Definition
          tlvJsonObj = getRowDefinitionViaRowId(iParentIdTemp);

          tlvJsonObj.put(PacketCableSqlQueryConstants.SUBTYPE_ARRAY, tlvJsonArray);

        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return tlvJsonObj;
  }

  /**
   * @param iRowID
   * @return boolean
   */
  @SuppressWarnings("unused")
  private boolean checkForChild(Integer iRowID) {

    boolean foundChild = false;

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
            + "	DOCSIS_TLV_DEFINITION "
            + "WHERE "
            + "	PARENT_ID = '"
            + iRowID
            + "'";

    try {
      parentCheckStatement = sqlConnection.createStatement();

      resultSetParentCheck = parentCheckStatement.executeQuery(sqlQuery);

      resultSetParentCheck.next();

      if (resultSetParentCheck.getRow() != 0) {
        foundChild = true;
      }

    } catch (SQLException e) {
      // TODO Auto-generated catch block
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
        "SELECT * FROM " + "	DOCSIS_TLV_DEFINITION " + " WHERE " + "	ID = '" + iRowID + "'";

    try {
      parentCheckStatement = sqlConnection.createStatement();

      resultSetParentCheck = parentCheckStatement.executeQuery(sqlQuery);

      resultSetParentCheck.next();

      /*************************************************
       * 				Assemble JSON OBJECT
       *************************************************/

      tlvJsonObj = new JSONObject();

      tlvJsonObj.put(
          PacketCableSqlQueryConstants.TYPE,
          resultSetParentCheck.getString(Dictionary.DB_TBL_COL_TYPE));
      tlvJsonObj.put(
          PacketCableSqlQueryConstants.TLV_NAME,
          resultSetParentCheck.getString(Dictionary.DB_TBL_COL_TLV_NAME));
      tlvJsonObj.put(
          PacketCableSqlQueryConstants.LENGTH_MIN,
          resultSetParentCheck.getInt(Dictionary.DB_TBL_COL_LENGTH_MIN));
      tlvJsonObj.put(
          PacketCableSqlQueryConstants.LENGTH_MAX,
          resultSetParentCheck.getInt(Dictionary.DB_TBL_COL_LENGTH_MAX));
      tlvJsonObj.put(
          PacketCableSqlQueryConstants.SUPPORTED_DOCSIS_VERSIONS,
          resultSetParentCheck.getString(Dictionary.DB_TBL_COL_SUPPORTED_VERSIONS));
      tlvJsonObj.put(
          PacketCableSqlQueryConstants.DATA_TYPE,
          resultSetParentCheck.getString(Dictionary.DB_TBL_COL_DATA_TYPE));
      tlvJsonObj.put(PacketCableSqlQueryConstants.ARE_SUBTYPES, true);
      tlvJsonObj.put(
          PacketCableSqlQueryConstants.BYTE_LENGTH,
          resultSetParentCheck.getString(Dictionary.DB_TBL_COL_BYTE_LENGTH));

      if (logger.isDebugEnabled()) logger.debug(tlvJsonObj.toString());

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return tlvJsonObj;
  }
}
