/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.datatype;

public class DataTypeFormatException extends Exception {

  /** */
  private static final long serialVersionUID = 2378130621592836894L;

  private Throwable cause;

  /**
   * @param message
   */
  public DataTypeFormatException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public DataTypeFormatException(Throwable cause) {
    super(cause.getMessage());
    this.cause = cause;
  }

  /**
   * @return Throwable
   */
  @Override
  public Throwable getCause() {
    return this.cause;
  }
}
