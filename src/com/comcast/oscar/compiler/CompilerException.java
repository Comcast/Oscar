/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

public class CompilerException extends Exception {
  private static final long serialVersionUID = -4911433822248330700L;

  private Throwable cause;

  /**
   * @param message
   */
  public CompilerException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public CompilerException(Throwable cause) {
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
