/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

public class ConfigurationFileException extends Exception {

  private static final long serialVersionUID = 28977801456447351L;

  private Throwable cause;

  /**
   * @param message
   */
  public ConfigurationFileException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public ConfigurationFileException(Throwable cause) {
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
