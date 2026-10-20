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
public class DownstreamFrequency {

  private final String[] args;

  /**
   * Get Downstream Frequency arguments
   *
   * @param args
   */
  public DownstreamFrequency(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command Downstream Frequency
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("downstream frequency");
    OptionBuilder.hasArgs(1);
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("dsfreq");
    OptionBuilder.withDescription(
        "Insert this downstream frequency during file compilation EX: 723000000.");
    return OptionBuilder.create("df");
  }

  /**
   * Return Downstream Frequency file
   *
   * @return int
   */
  public int getDownstreamFrequency() {
    if (this.args[0] != null) {
      return Integer.valueOf(this.args[0]);
    } else {
      return -1;
    }
  }
}
