/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class TLVtoJSON {

  public static final String ERROR = "Incorrect TLV format.";

  private final String[] args;

  /**
   * Get TLV to JSON arguments
   *
   * @param args
   */
  public TLVtoJSON(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command TLV to JSON display
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("TLV");
    OptionBuilder.hasArgs();
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("tlv2json");
    OptionBuilder.withDescription(
        "View the JSON array of a TLV EX: 030101 (NetworkAccess: Type 3 Length 1 Value 1).");
    return OptionBuilder.create("t2j");
  }

  /**
   * Print JSON information from TLV
   *
   * @param tlvDisassemble
   */
  public void printJSON(String tlvDisassemble) {
    TlvBuilder tb = new TlvBuilder();

    try {
      tb.add(new HexString(HexString.toByteArray(this.args[0])));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    TlvDisassemble td = new TlvDisassemble(tb, tlvDisassemble);
    System.out.println(td.getTlvDictionary());
  }
}
