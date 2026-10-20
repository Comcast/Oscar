/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.sql;

import com.comcast.oscar.utilities.DirectoryStructure;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SqlConnection {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(SqlConnection.class);

  private String sqliteURL;
  private String user = "user";
  private String password = "password";
  private static Connection sqlConnection;

  /** */
  public SqlConnection() {

    logger.debug(() -> "SqlConnection(): " + DirectoryStructure.fDictionaryFile());

    // SQLite
    String sCurrentPath = DirectoryStructure.fDictionaryFile().toString();

    logger.debug("SQLite DB Path: {}", sCurrentPath);

    this.sqliteURL = "jdbc:Sqlite:" + sCurrentPath;

    if (sqlConnection == null) {

      logger.debug("SqlConnection() -> First Time Connected to DB");

      connect();

    } else {

      logger.debug("SqlConnection() -> Already Connected to DB");
    }
  }

  /**
   * @return Connection
   */
  public Connection getConnectionID() {
    return sqlConnection;
  }

  /**
   * @param rsRsultSet
   * @return Integer
   * @throws SQLException
   */
  public static Integer getRowCount(ResultSet rsRsultSet) throws SQLException {

    int rowCount = 0;

    while (rsRsultSet.next()) rowCount++;

    rsRsultSet.beforeFirst();

    return rowCount;
  }

  /**
   * @param sQuery
   * @return RowCount * @throws SQLException
   */
  public static Integer getRowCount(String sQuery) throws SQLException {

    int rowCount = 0;

    // Create statement
    Statement statSQLStatement = sqlConnection.createStatement();

    // Get Result Set of Query
    ResultSet rsRsultSet = statSQLStatement.executeQuery(sQuery);

    while (rsRsultSet.next()) rowCount++;

    return rowCount;
  }

  /** */
  private void connect() {
    try {

      // SQLite
      Class.forName("org.sqlite.JDBC").newInstance();

      logger.debug("SqlConnection.connect() -> URL: {}", sqliteURL);

      sqlConnection = DriverManager.getConnection(sqliteURL, user, password);

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
