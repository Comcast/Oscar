/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.sql.queries;

import java.sql.Connection;

public class DPoESqlQuery extends DocsisSqlQuery {

  /**
   * Constructor for DPoESqlQuery.
   *
   * @param sqlConnection Connection
   */
  public DPoESqlQuery(Connection sqlConnection) {
    super(sqlConnection);
  }

  /** */
  public void commit() {}
}
