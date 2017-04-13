package com.comcast.oscar.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.comcast.oscar.utilities.DirectoryStructure;

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


public class SqlConnection {
	//Log4J2 logging
    private static final Logger logger = LogManager.getLogger(SqlConnection.class);
	
    private String sqliteURL;
    private String user = "user";
    private String password = "password";  
	private Boolean debug = Boolean.FALSE;	
	private static Connection sqlConnection;
	
	/**
	 * 
	 */
	public SqlConnection() {
				
		if (debug)
			logger.debug("SqlConnection(): " + DirectoryStructure.fDictionaryFile());
		
		//SQLite
		String sCurrentPath = DirectoryStructure.fDictionaryFile().toString();

		if (debug)
			logger.debug("SQLite DB Path: " + sCurrentPath);
		
		this.sqliteURL = "jdbc:Sqlite:" + sCurrentPath;
		
		if (sqlConnection == null) {
			
			if (debug) 
				logger.debug("SqlConnection() -> First Time Connected to DB");
			
			connect();	
		
		} else {
			
			if (debug) 
				logger.debug("SqlConnection() -> Already Connected to DB");
		
		}
	}
	
	
	/**
	 * 
	
	 * @return Connection
	 */
	public Connection getConnectionID () {
		return sqlConnection;
	}
	
	/**
	 * 
	 * @param rsRsultSet
	
	
	 * @return Integer
	 * @throws SQLException */
	public static Integer getRowCount (ResultSet rsRsultSet) throws SQLException {
		
		int rowCount = 0;
		
		while (rsRsultSet.next())  
		    rowCount++;  
		
		rsRsultSet.beforeFirst();
		
		return rowCount;
	}

/**
 * 
 * @param sQuery


 * @return RowCount * @throws SQLException */
	public static Integer getRowCount (String sQuery) throws SQLException {
		
		int rowCount = 0;
		
		//Create statement
		Statement statSQLStatement = sqlConnection.createStatement();
		
		//Get Result Set of Query
		ResultSet rsRsultSet = statSQLStatement.executeQuery(sQuery);
		
		while (rsRsultSet.next())  
		    rowCount++;
		
		return rowCount;
	}
	
	/**
	 * 
	 */
	private void connect () {
		try {

			//SQLite
			Class.forName("org.sqlite.JDBC").newInstance();
			
			if (debug) 
				logger.debug("SqlConnection.connect() -> URL: " + sqliteURL);
			
			sqlConnection = DriverManager.getConnection(sqliteURL, user, password);
			
			if (debug) 
				logger.debug("SqlConnection.connect() -> Is Connected");
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
