/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class Key {

  /** Empty until a regular key file is read; never a placeholder signing secret. */
  private String sKey = "";

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
   * Loads the shared secret from a regular, readable, nonempty key file. An omitted {@code -k}
   * leaves the key empty; an explicitly supplied key must load a secret instead of falling back to
   * unsigned or placeholder output.
   *
   * @param args single-element array with the key file path; must not be null or empty
   * @throws IllegalArgumentException if no path is supplied, the path is not a regular file, the
   *     file cannot be read, or it contains no key text
   */
  public void setKey(String[] args) {
    this.sKey = "";

    if (args == null || args.length == 0 || args[0] == null || args[0].isBlank()) {
      throw new IllegalArgumentException("Key filename is required after -k");
    }

    File keyFile = new File(args[0]);

    if (!keyFile.isFile()) {
      throw new IllegalArgumentException("Key file is not a regular file: " + args[0]);
    }

    byte[] keyBytes;

    try {
      keyBytes = Files.readAllBytes(keyFile.toPath());
    } catch (IOException exception) {
      throw new IllegalArgumentException("Cannot read key file: " + args[0], exception);
    }

    String key = new HexString(keyBytes).toASCII();

    if (key.isEmpty()) {
      throw new IllegalArgumentException("Key file is empty: " + args[0]);
    }

    this.sKey = key;
  }

  /**
   * Returns the loaded shared secret, or an empty string when {@code -k} was not supplied.
   *
   * @return key text; empty only when no key file was requested
   */
  public String getKey() {
    return this.sKey;
  }
}
