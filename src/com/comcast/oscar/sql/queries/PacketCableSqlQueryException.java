/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.sql.queries;

public class PacketCableSqlQueryException extends Exception {

  private static final long serialVersionUID = -2747516882743788218L;

  private Throwable cause;

  /**
   * Constructor for PacketCableSqlQueryException.
   *
   * @param message String
   */
  public PacketCableSqlQueryException(String message) {
    super(message);
  }

  /**
   * Constructor for PacketCableSqlQueryException.
   *
   * @param cause Throwable
   */
  public PacketCableSqlQueryException(Throwable cause) {
    super(cause.getMessage());
    this.cause = cause;
  }

  /**
   * Method getCause.
   *
   * @return Throwable
   */
  @Override
  public Throwable getCause() {
    return this.cause;
  }
}
