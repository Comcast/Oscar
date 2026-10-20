/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

public class TlvException extends Exception {

  private static final long serialVersionUID = 2157067565372772158L;
  private Throwable cause;

  /**
   * @param message
   */
  public TlvException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public TlvException(Throwable cause) {
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
