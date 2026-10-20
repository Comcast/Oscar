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
public class TLV {

  private final String[] args;

  /**
   * Get TLV arguments
   *
   * @param args
   */
  public TLV(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command TLV
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("TLV");
    OptionBuilder.hasArgs(1);
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("tlv");
    OptionBuilder.withDescription("Insert this TLV during file compilation.");
    return OptionBuilder.create("t");
  }

  /**
   * Return TLV
   *
   * @return String
   */
  public String getTLV() {
    return this.args[0];
  }
}
