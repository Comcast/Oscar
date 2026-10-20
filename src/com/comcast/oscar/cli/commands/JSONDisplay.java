/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class JSONDisplay {

  /**
   * Set option parameters for command JSON display
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("json");
    OptionBuilder.withDescription("Display the JSON of the input file.");
    return OptionBuilder.create("j");
  }

  /**
   * Print JSON display from binary file
   *
   * @param file
   * @param tlvDisassemble
   */
  public void printJSONDisplayFromBinary(
      File file, String tlvDisassemble, int configurationFileType) {
    TlvDisassemble td = null;
    ConfigurationFileExport cfe = new ConfigurationFileExport(file, configurationFileType);

    try {
      td = new TlvDisassemble(cfe.getTlvBuilder(), tlvDisassemble);
    } catch (TlvException e) {
      e.printStackTrace();
    }

    System.out.println(td.getTlvDictionary().toString());
  }

  /**
   * Print JSON display from text file
   *
   * @param file
   * @param tlvDisassemble
   */
  public void printJSONDisplayFromText(File file, String tlvDisassemble) {
    TlvDisassemble td = null;
    ConfigurationFileImport cfi = null;

    try {
      cfi = new ConfigurationFileImport(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ConfigurationFileException e) {
      e.printStackTrace();
    }

    td = new TlvDisassemble(cfi.getTlvBuilder(), tlvDisassemble);
    System.out.println(td.getTlvDictionary().toString());
  }
}
