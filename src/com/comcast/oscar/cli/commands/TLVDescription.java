/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class TLVDescription {

  public static final String ERROR = "Missing parameter. Please specify the TLV (EX: 26.1)";

  private final String[] args;

  /**
   * Get TLV Description arguments
   *
   * @param args
   */
  public TLVDescription(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command TLV description
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("TLV dot notation");
    OptionBuilder.hasArgs(1);
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("tlvdescription");
    OptionBuilder.withDescription("Display the TLV description (EX: 26.1).");
    return OptionBuilder.create("td");
  }

  /**
   * Print TLV description
   *
   * @param configurationFileType
   */
  public void printTLVDescription(int configurationFileType) {
    ConfigurationFileExport cfe = new ConfigurationFileExport(configurationFileType);
    System.out.println(cfe.getTlvDefintion(this.args[0]));
  }
}
