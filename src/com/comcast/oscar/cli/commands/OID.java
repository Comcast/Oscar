/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.ber.BERService;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class OID {

  public static final String ERROR =
      "Missing parameter. Please specify all parameters in the following format"
          + " [<OID>][<value>][<data type>]";

  private final String[] args;

  private Map<String, String[]> map = new HashMap<String, String[]>();

  /**
   * Get OID arguments
   *
   * @param args
   */
  public OID(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command OID
   *
   * @return Option
   */
  public static Option OptionParameters() {
    OptionBuilder.withArgName("[<OID>][<value>][<data type>]");
    OptionBuilder.hasArgs();
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("OID");
    OptionBuilder.withDescription(
        "Insert this OID into a file when compiling. "
            + "Multiple OIDs can be inserted simultaneously (space delimited). "
            + "Applicable datatypes: "
            + BERService.getDataTypeStringList());
    return OptionBuilder.create("O");
  }

  /**
   * Check for any OID arguments
   *
   * @return boolean
   */
  public boolean hasOID() {
    boolean boolOID = false;

    for (String string : this.args) {
      if (string.contains("|")) {
        String[] array = string.split("\\|");

        if (array.length > 2) {
          String[] parameters = new String[10];
          parameters[0] = array[1];
          parameters[1] = array[2];
          this.map.put(array[0], parameters);
          boolOID = true;
        }
      }
    }

    return boolOID;
  }

  /**
   * Return all OIDs
   *
   * @return Map<String,String[]>
   */
  public Map<String, String[]> getMap() {
    return this.map;
  }
}
