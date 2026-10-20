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
public class Firmware {

  private final String[] args;

  /**
   * Get Firmware arguments
   *
   * @param args
   */
  public Firmware(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command Firmware
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("filename");
    OptionBuilder.hasArgs(1);
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("firmware");
    OptionBuilder.withDescription("Insert this firmware during file compilation.");
    return OptionBuilder.create("f");
  }

  /**
   * Return Firmware file
   *
   * @return String
   */
  public String getFirmware() {
    return this.args[0];
  }
}
