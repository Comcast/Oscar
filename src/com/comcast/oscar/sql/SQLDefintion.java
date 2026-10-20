/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.sql;

import java.util.HashMap;
import java.util.Map;

public class SQLDefintion {

  public static final Integer DB_TYPE_SQL_LITE = 0;

  private final String MYSQL_JDBC = "jdbc:mysql:";
  private final String SQL_LITE_JDBC = "jdbc:Sqlite:";

  private Map<String, String> mssDbDefinition = new HashMap<String, String>();

  private String sCurrentPath = "";

  /** */
  public SQLDefintion() {
    mssDbDefinition.put("DB_TYPE", Integer.toString(0));
    mssDbDefinition.put("HOST", sCurrentPath);
    mssDbDefinition.put("USER", "");
    mssDbDefinition.put("PASSWORD", "");
    mssDbDefinition.put("PORT", "");
    mssDbDefinition.put("JDBC", SQL_LITE_JDBC);
    mssDbDefinition.put("DRIVER", MYSQL_JDBC);
  }

  /**
   * @return SQL Definition
   */
  public Map<String, String> getDefinition() {
    return mssDbDefinition;
  }
}
