/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class MaxCPE {

  private final String[] args;

  /**
   * Get Maximum CPE arguments
   *
   * @param args
   */
  public MaxCPE(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command Maximum CPE
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("maximum CPEs");
    OptionBuilder.hasArgs(1);
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("maxCPE");
    OptionBuilder.withDescription("Insert the maximum CPEs during file compilation EX: 5.");
    return OptionBuilder.create("m");
  }

  /**
   * Return MaxCPE
   *
   * @return int
   */
  public int getMaxCPE() {
    if (this.args[0] != null) {
      return Integer.valueOf(this.args[0]);
    } else {
      return -1;
    }
  }
}
