/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.utilities.HexString;
import java.io.File;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class Key {

  private String sKey = " ";

  /**
   * Set option parameters for command Key
   *
   * @return Option
   */
  public static Option OptionParameters() {
    OptionBuilder.withArgName("key filename");
    OptionBuilder.hasArgs(1);
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("key");
    OptionBuilder.withDescription("Use this sharedsecret to compile the file - DOCSIS ONLY.");
    return OptionBuilder.create("k");
  }

  /**
   * Set Key string from file
   *
   * @param args
   */
  public void setKey(String[] args) {
    if (new File(args[0]).exists()) {
      this.sKey = new HexString(HexString.fileToByteArray(new File(args[0]))).toASCII();
    }
  }

  /**
   * Return Key
   *
   * @return String
   */
  public String getKey() {
    return this.sKey;
  }
}
