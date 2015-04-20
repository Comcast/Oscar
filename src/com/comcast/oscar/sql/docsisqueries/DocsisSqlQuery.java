package com.comcast.oscar.sql.docsisqueries;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.compiler.packetcablecompiler.PacketCableConstants;
import com.comcast.oscar.sql.SqlConnection;
import com.comcast.oscar.tlv.dictionary.Dictionary;

/**
 * 
 * @help
 	DOCSIS_TLV_DEFINITION<br>
	+------------------------------+----------------------+------+-----+---------+----------------+<br>
	| Field                        | Type                 | Null | Key | Default | Extra          |<br>
	+------------------------------+----------------------+------+-----+---------+----------------+<br>
	| ID                           | int(10) unsigned     | NO   | PRI | NULL    | auto_increment |<br> 
	| TYPE                         | smallint(5) unsigned | NO   |     | NULL    |                |<br> 
	| PARENT_ID                    | int(10) unsigned     | YES  |     | NULL    |                |<br>
	| TLV_NAME                     | varchar(50)          | NO   |     | NULL    |                |<br> 
	| TLV_DESCRIPTION              | text                 | NO   |     | NULL    |                |<br> 
	| LENGTH_MIN                   | smallint(5) unsigned | NO   |     | NULL    |                |<br> 
	| LENGTH_MAX                   | smallint(5) unsigned | NO   |     | NULL    |                |<br> 
	| DATA_TYPE                    | varchar(50)          | YES  |     | NULL    |                |<br> 
	| BYTE_LENGTH                  | tinyint(3) unsigned  | NO   |     | NULL    |                |<br> 
	| MIN_SUPPORTED_DOCSIS_VERSION | varchar(50)          | NO   |     | NULL    |                |<br> 
	+------------------------------+----------------------+------+-----+---------+----------------+<br>
	
 	PACKET_CABLE_TLV_DEFINITION<br>
	+------------------------------+----------------------+------+-----+---------+----------------+<br>
	| Field                        | Type                 | Null | Key | Default | Extra          |<br>
	+------------------------------+----------------------+------+-----+---------+----------------+<br>
	| ID                           | int(10) unsigned     | NO   | PRI | NULL    | auto_increment |<br> 
	| TYPE                         | smallint(5) unsigned | NO   |     | NULL    |                |<br> 
	| PARENT_ID                    | int(10) unsigned     | YES  |     | NULL    |                |<br> 
	| TLV_NAME                     | varchar(50)          | NO   |     | NULL    |                |<br> 
	| TLV_DESCRIPTION              | text                 | NO   |     | NULL    |                |<br> 
	| LENGTH_MIN                   | smallint(5) unsigned | NO   |     | NULL    |                |<br> 
	| LENGTH_MAX                   | smallint(5) unsigned | NO   |     | NULL    |                |<br> 
	| DATA_TYPE                    | varchar(50)          | YES  |     | NULL    |                |<br> 
	| BYTE_LENGTH                  | tinyint(3) unsigned  | NO   |     | NULL    |                |<br> 
	| MIN_SUPPORTED_DOCSIS_VERSION | varchar(50)          | NO   |     | NULL    |                |<br> 
	+------------------------------+----------------------+------+-----+---------+----------------+<br>
	
 JSON Formatter
	http://jsonviewer.stack.hu/
	http://jsonformatter.curiousconcept.com/
 *
 */

/**
 * @bannerLicense
	Copyright 2015 Comcast Cable Communications Management, LLC<br>
	___________________________________________________________________<br>
	Licensed under the Apache License, Version 2.0 (the "License")<br>
	you may not use this file except in compliance with the License.<br>
	You may obtain a copy of the License at<br>
	http://www.apache.org/licenses/LICENSE-2.0<br>
	Unless required by applicable law or agreed to in writing, software<br>
	distributed under the License is distributed on an "AS IS" BASIS,<br>
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
	See the License for the specific language governing permissions and<br>
	limitations under the License.<br>


 * @author Maurice Garcia (maurice.garcia.2015@gmail.com)
 */

public class DocsisSqlQuery implements Dictionary {

	private Connection sqlConnection;
	
	protected Boolean debug = Boolean.FALSE;

	public static Integer MAJOR_TLV = 0;

	/**
	 * 
	 * @param sqlConnection
	 */
	public DocsisSqlQuery (Connection sqlConnection) {
		this.sqlConnection = sqlConnection;
	}

	/**
	 * 
	 * @param iType
	 * @see com.comcast.oscar.tlv.dictionary.Dictionary#getMajorTlvDefinition(Integer)
	 */
	public void getMajorTlvDefinition (Integer iType) {

		Statement statement = null;
		ResultSet resultSet = null;

		String sqlQuery = 
				"SELECT * " +
						"FROM " +
						"DOCSIS_TLV_DEFINITION " +
						"WHERE " +
						"TYPE = '" + iType + "'" +
						" AND " +
						"PARENT_ID = '0'";

		if (debug) 
			System.out.println("getMajorTlvDefinition() SQL_QUERY: " + sqlQuery);

		try {
			statement = sqlConnection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			resultSet = statement.executeQuery(sqlQuery);

			if (debug) {

				while (resultSet.next()) {
					System.out.println("MySQL-Query->TLV-NAME: " + resultSet.getString("TLV_NAME"));	
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param iType
	
	 * @return JSONObject
	 * @see com.comcast.oscar.tlv.dictionary.Dictionary#getTlvDefinition(Integer)
	 */
	public JSONObject getTlvDefinition (Integer iType) {

		JSONObject jsonObj = null;

		try {
			jsonObj = recursiveTlvDefinitionBuilder(getMajorParentTlvRowId(iType) , 
					MAJOR_TLV , 
					new ArrayList<Integer>());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObj;

	}

	/**
	 * 
	 * @param iCableLabsConfigType
	
	 * @return JSONArray
	 * @see com.comcast.oscar.tlv.dictionary.Dictionary#getAllTlvDefinition(int)
	 */
	public JSONArray getAllTlvDefinition(int iCableLabsConfigType) {

		JSONArray jsonArrTlvDictionary = new JSONArray();

		if (Constants.CONFIGURATION_FILE_TYPE_DOCSIS == iCableLabsConfigType) {

			for (int tlvCounter = Constants.DOCSIS_TLV_MIN ; tlvCounter <= Constants.DOCSIS_TLV_MAX ; tlvCounter++) {
				if (debug) {
					System.out.println("+------------------------------------------------------------------------+");
					System.out.println("TLV: " + tlvCounter + " -> JSON-TLV-DICT: " + getTlvDefinition(tlvCounter));
				}
			}

		} else if (PacketCableConstants.CONFIG_FILE_TYPE_PKT_CABLE_10 == iCableLabsConfigType) {

		} else if (PacketCableConstants.CONFIG_FILE_TYPE_PKT_CABLE_15 == iCableLabsConfigType) {

		} else if (PacketCableConstants.CONFIG_FILE_TYPE_PKT_CABLE_20 == iCableLabsConfigType) {

		}

		return jsonArrTlvDictionary;

	}

	/**
	 * 
	 * @param iType
	
	 * @return Integer
	 */
	private Integer getMajorParentTlvRowId (Integer iType) {

		Statement statement = null;
		ResultSet resultSet = null;
		Integer iPrimaryKey = -1;

		String sqlQuery = 
				"SELECT ID " +
						"FROM " +
						"DOCSIS_TLV_DEFINITION " +
						"WHERE " +
						"TYPE = '" + iType + "'" + 
						"AND " +
						"PARENT_ID = '0'";

		if (debug) 
			System.out.println("getMajorTlvDefinition() SQL_QUERY: " + sqlQuery);

		try {
			statement = sqlConnection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			resultSet = statement.executeQuery(sqlQuery);

			while (resultSet.next())
				iPrimaryKey = resultSet.getInt("ID");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return iPrimaryKey;
	}

	/**
	 * 
	 * @param iRowID
	
	 * @return Integer
	 */
	private Integer getTypeFromRowID (int iRowID) {
		Statement statement = null;
		ResultSet resultSet = null;
		Integer iPrimaryKey = -1;

		String sqlQuery = 
				"SELECT DISTINCT " +
						"TYPE " +
						"FROM " +
						"DOCSIS_TLV_DEFINITION " +
						"WHERE " +
						"ID = '" + iRowID + "'";

		if (debug) 
			System.out.println("getMajorTlvDefinition() SQL_QUERY: " + sqlQuery);

		try {
			statement = sqlConnection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			resultSet = statement.executeQuery(sqlQuery);

			while (resultSet.next())
				iPrimaryKey = resultSet.getInt("TYPE");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return iPrimaryKey;		
	}

	/*
	 * 
		+------------------------------+----------------------+------+-----+---------+----------------+
		| Field                        | Type                 | Null | Key | Default | Extra          |
		+------------------------------+----------------------+------+-----+---------+----------------+
		| ID                           | int(10) unsigned     | NO   | PRI | NULL    | auto_increment | 
		| TYPE                         | smallint(5) unsigned | NO   |     | NULL    |                | 
		| PARENT_ID                    | int(10) unsigned     | YES  |     | NULL    |                | 
		| TLV_NAME                     | varchar(50)          | NO   |     | NULL    |                | 
		| TLV_DESCRIPTION              | text                 | NO   |     | NULL    |                | 
		| LENGTH_MIN                   | smallint(5) unsigned | NO   |     | NULL    |                | 
		| LENGTH_MAX                   | smallint(5) unsigned | NO   |     | NULL    |                | 
		| DATA_TYPE                    | varchar(50)          | YES  |     | NULL    |                | 
		| BYTE_LENGTH                  | tinyint(3) unsigned  | NO   |     | NULL    |                | 
		| MIN_SUPPORTED_DOCSIS_VERSION | varchar(50)          | NO   |     | NULL    |                | 
		+------------------------------+----------------------+------+-----+---------+----------------+
	 * 
	 */

	/**
	 * 
	 * @param iRowID
	 * @param iParentID
	 * @param aliTlvEncodeHistory	
	 * @return JSONObject
	 * @throws JSONException */
	private JSONObject recursiveTlvDefinitionBuilder (Integer iRowID , Integer iParentID , ArrayList<Integer> aliTlvEncodeHistory) throws JSONException {

		Statement	parentCheckStatement = null, 
					getRowDefinitionStatement = null;

		ResultSet 	resultSetParentCheck = null ,  
					resultSetGetRowDefinition = null;

		aliTlvEncodeHistory.add(getTypeFromRowID(iParentID));

		String sqlQuery;

		JSONObject 	tlvJsonObj = null;

		// This query will check for child rows that belong to a parent row
		sqlQuery = 
				"SELECT " +
						"	ID ," +
						"	TYPE ," +		
						"	TLV_NAME," +
						"	PARENT_ID " +
						"FROM " +
						"	DOCSIS_TLV_DEFINITION " +
						"WHERE " +
						"	PARENT_ID = '" + iRowID + "'" ;

		try {

			//Create statement
			parentCheckStatement = sqlConnection.createStatement();

			//Get Result Set of Query
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
				sqlQuery = 
						"SELECT * FROM " +
								"	DOCSIS_TLV_DEFINITION " +
								"	WHERE ID = '" + iRowID + "'" ;

				//Create statement to get Rows from ROW ID's
				getRowDefinitionStatement = sqlConnection.createStatement();

				//Get Result Set
				resultSetGetRowDefinition = getRowDefinitionStatement.executeQuery(sqlQuery);

				//Advance to next index in result set, this is needed
				resultSetGetRowDefinition.next();

				/*************************************************
				 * 				Assemble JSON OBJECT
				 *************************************************/

				tlvJsonObj = new JSONObject();

				tlvJsonObj.put(Dictionary.TYPE, resultSetGetRowDefinition.getString("TYPE"));
				tlvJsonObj.put(Dictionary.TLV_NAME, resultSetGetRowDefinition.getString("TLV_NAME"));
				//			tlvJsonObj.put(DocsisSqlQueryConstants.TLV_DESCRIPTION, resultSetGetRowDefinition.getString("TLV_DESCRIPTION"));
				tlvJsonObj.put(Dictionary.LENGTH_MIN, resultSetGetRowDefinition.getInt("LENGTH_MIN"));
				tlvJsonObj.put(Dictionary.LENGTH_MAX, resultSetGetRowDefinition.getInt("LENGTH_MAX"));
				tlvJsonObj.put(Dictionary.SUPPORTED_VERSIONS, resultSetGetRowDefinition.getString("MIN_SUPPORTED_VERSION"));
				tlvJsonObj.put(Dictionary.DATA_TYPE, resultSetGetRowDefinition.getString("DATA_TYPE"));
				tlvJsonObj.put(Dictionary.ARE_SUBTYPES, false);
				tlvJsonObj.put(Dictionary.BYTE_LENGTH, resultSetGetRowDefinition.getString("BYTE_LENGTH"));

				aliTlvEncodeHistory.add(resultSetGetRowDefinition.getInt("TYPE"));				
				tlvJsonObj.put(Dictionary.PARENT_TYPE_LIST, aliTlvEncodeHistory);

				if (debug)
					System.out.println(tlvJsonObj.toString());

			} else if (iParentID == MAJOR_TLV) {

				// This query will check for child rows that belong to a parent row
				sqlQuery = 
						"SELECT * FROM " +
								"	DOCSIS_TLV_DEFINITION " +
								"	WHERE ID = '" + iRowID + "'" ;

				getRowDefinitionStatement = sqlConnection.createStatement();

				resultSetGetRowDefinition = getRowDefinitionStatement.executeQuery(sqlQuery);

				resultSetGetRowDefinition.next();


				/*************************************************
				 * 				Assemble JSON OBJECT
				 *************************************************/

				tlvJsonObj = new JSONObject();

				tlvJsonObj.put(Dictionary.TYPE, resultSetGetRowDefinition.getString("TYPE"));
				tlvJsonObj.put(Dictionary.TLV_NAME, resultSetGetRowDefinition.getString("TLV_NAME"));
				//			tlvJsonObj.put(DocsisSqlQueryConstants.TLV_DESCRIPTION, resultSetGetRowDefinition.getString("TLV_DESCRIPTION"));
				tlvJsonObj.put(Dictionary.LENGTH_MIN, resultSetGetRowDefinition.getInt("LENGTH_MIN"));
				tlvJsonObj.put(Dictionary.LENGTH_MAX, resultSetGetRowDefinition.getInt("LENGTH_MAX"));
				tlvJsonObj.put(Dictionary.SUPPORTED_VERSIONS, resultSetGetRowDefinition.getString("MIN_SUPPORTED_VERSION"));
				tlvJsonObj.put(Dictionary.DATA_TYPE, resultSetGetRowDefinition.getString("DATA_TYPE"));
				tlvJsonObj.put(Dictionary.ARE_SUBTYPES, false);
				tlvJsonObj.put(Dictionary.BYTE_LENGTH, resultSetGetRowDefinition.getString("BYTE_LENGTH"));

				aliTlvEncodeHistory.add(resultSetGetRowDefinition.getInt("TYPE"));
				tlvJsonObj.put(Dictionary.PARENT_TYPE_LIST, aliTlvEncodeHistory);

				if (debug)
					System.out.println(tlvJsonObj.toString());				
			}

			if  (SqlConnection.getRowCount(resultSetParentCheck) > 0) {

				// This query will check for child rows that belong to a parent row
				sqlQuery = 
						"SELECT " +
								"	TYPE , " +
								"	TLV_NAME," +
								"	PARENT_ID" +
								" FROM " +
								"	DOCSIS_TLV_DEFINITION " +
								"	WHERE ID = '" + iRowID + "'" ;

				try {

					Integer iParentIdTemp = null;

					JSONArray tlvJsonArray = new JSONArray();

					while (resultSetParentCheck.next()) {

						iParentIdTemp = resultSetParentCheck.getInt("PARENT_ID");

						ArrayList<Integer> aliTlvEncodeHistoryNext = new ArrayList<Integer>();

						aliTlvEncodeHistoryNext.addAll(aliTlvEncodeHistory);

						if (debug)
							System.out.println("aliTlvEncodeHistoryNext: " + aliTlvEncodeHistoryNext);

						aliTlvEncodeHistoryNext.remove(aliTlvEncodeHistoryNext.size()-1);

						//Keep processing each row using recursion until you get to to the bottom of the tree
						tlvJsonArray.put(recursiveTlvDefinitionBuilder(	resultSetParentCheck.getInt("ID"), 
								iParentIdTemp ,
								aliTlvEncodeHistoryNext));
					}

					//Get Parent Definition
					tlvJsonObj = getRowDefinitionViaRowId(iParentIdTemp);

					tlvJsonObj.put(Dictionary.SUBTYPE_ARRAY, tlvJsonArray);


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
	 * 
	 * @param iRowID
	
	 * @return boolean
	 */
	@SuppressWarnings("unused")
	private boolean checkForChild (Integer iRowID) {

		boolean foundChild = false;

		Statement 	parentCheckStatement = null;

		ResultSet 	resultSetParentCheck = null;

		// This query will check for child rows that belong to a parent row
		String sqlQuery = 
				"SELECT " +
						"	ID ," +
						"	TYPE ," +		
						"	TLV_NAME," +
						"	PARENT_ID " +
						"FROM " +
						"	DOCSIS_TLV_DEFINITION " +
						"WHERE " +
						"	PARENT_ID = '" + iRowID + "'" ;

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
	 * 
	 * @param iRowID
	
	
	 * @return JSONObject
	 * @throws JSONException */
	private JSONObject getRowDefinitionViaRowId (Integer iRowID) throws JSONException {

		Statement 	parentCheckStatement = null;

		ResultSet 	resultSetParentCheck = null;

		JSONObject tlvJsonObj = null;

		// This query will check for child rows that belong to a parent row
		String sqlQuery = 
				"SELECT * FROM " +
						"	DOCSIS_TLV_DEFINITION " +
						" WHERE " +
						"	ID = '" + iRowID + "'" ;

		try {
			parentCheckStatement = sqlConnection.createStatement();

			resultSetParentCheck = parentCheckStatement.executeQuery(sqlQuery);

			resultSetParentCheck.next();

			/*************************************************
			 * 				Assemble JSON OBJECT
			 *************************************************/

			tlvJsonObj = new JSONObject();

			tlvJsonObj.put(Dictionary.TYPE, resultSetParentCheck.getString("TYPE"));
			tlvJsonObj.put(Dictionary.TLV_NAME, resultSetParentCheck.getString("TLV_NAME"));
			//	tlvJsonObj.put(DocsisSqlQueryConstants.TLV_DESCRIPTION, resultSetParentCheck.getString("TLV_DESCRIPTION"));
			tlvJsonObj.put(Dictionary.LENGTH_MIN, resultSetParentCheck.getInt("LENGTH_MIN"));
			tlvJsonObj.put(Dictionary.LENGTH_MAX, resultSetParentCheck.getInt("LENGTH_MAX"));
			tlvJsonObj.put(Dictionary.SUPPORTED_VERSIONS, resultSetParentCheck.getString("MIN_SUPPORTED_VERSION"));
			tlvJsonObj.put(Dictionary.DATA_TYPE, resultSetParentCheck.getString("DATA_TYPE"));
			tlvJsonObj.put(Dictionary.ARE_SUBTYPES, true);
			tlvJsonObj.put(Dictionary.BYTE_LENGTH, resultSetParentCheck.getString("BYTE_LENGTH"));

			if (debug)
				System.out.println(tlvJsonObj.toString());			


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		return tlvJsonObj;		
	}

}
