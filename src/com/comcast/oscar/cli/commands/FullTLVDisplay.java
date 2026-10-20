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
public class FullTLVDisplay {

  /**
   * Set option parameters for command Full TLV display
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("fulltlvdisplay");
    OptionBuilder.withDescription(
        "Display all TLVs available in the dictionary for the defined specification.");
    return OptionBuilder.create("ftd");
  }

  /**
   * Print Full TLV Display
   *
   * @param configurationFileType
   */
  public void printFullTLVDisplay(int configurationFileType) {
    ConfigurationFileExport cfe = new ConfigurationFileExport(configurationFileType);
    System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_DEFAULT_TLV));
  }
}
