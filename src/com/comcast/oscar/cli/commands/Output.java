/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import java.io.File;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class Output {

  public static final String ERROR =
      "Output file not found. Please use -o <filename> to specify an output file.";

  private final String[] args;
  private File fOutput;

  /**
   * Get Output arguments
   *
   * @param args
   */
  public Output(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command Output
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("filename");
    OptionBuilder.hasArgs(1);
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("output");
    OptionBuilder.withDescription("Compile the input file to this output file.");
    return OptionBuilder.create("o");
  }

  /**
   * Return Output file
   *
   * @return File
   */
  public File getOutput() {
    this.fOutput = new File(this.args[0]);

    if (this.fOutput.getParentFile() != null) {
      this.fOutput.getParentFile().mkdirs();
    }

    return this.fOutput;
  }
}
