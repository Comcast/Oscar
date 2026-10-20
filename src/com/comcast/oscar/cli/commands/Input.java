/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.utilities.DirectoryStructure;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class Input {

  public static final String ERROR =
      "Input file not found. Please use -i <filename> to specify an input file.";

  private final String[] args;
  private File fInput;

  /**
   * Get Input arguments
   *
   * @param args
   */
  public Input(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command Input
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("filename");
    OptionBuilder.hasArgs(1);
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("input");
    OptionBuilder.withDescription("File to analyze and/or compile/decompile.");
    return OptionBuilder.create("i");
  }

  /**
   * Check if argument is valid, if any
   *
   * @return boolean
   */
  public boolean hasInput() {
    if (new File(this.args[0]).exists()) {
      this.fInput = new File(this.args[0]);
      return true;
    } else if (new File(DirectoryStructure.sActivePath() + File.separator + this.args[0])
        .exists()) {
      this.fInput = new File(DirectoryStructure.sActivePath() + File.separator + this.args[0]);
      return true;
    }

    return false;
  }

  /**
   * Return Input file
   *
   * @return File
   */
  public File getInput() {
    return this.fInput;
  }

  /**
   * Check if file is binary
   *
   * @return boolean
   */
  public boolean isBinary() {
    return !(HexString.verifyAsciiPlainText(HexString.fileToByteArray(this.fInput)));
  }
}
