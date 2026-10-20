/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

public class DictionarySQLQueriesException extends Exception {
  /** */
  private static final long serialVersionUID = -5175865963413644345L;

  private Throwable cause;

  /**
   * @param message
   */
  public DictionarySQLQueriesException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public DictionarySQLQueriesException(Throwable cause) {
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
