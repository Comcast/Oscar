/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.compiler.PacketCableConstants;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class DigitmapInsert {

  public static final String ERROR = "No digitmap file found.";

  private final String[] args;

  private Map<String, byte[]> map = new HashMap<String, byte[]>();
  private String sDefaultOID = PacketCableConstants.PKT_CABLE_DIGIT_MAP_OID;

  /**
   * Get Digitmap Insert arguments
   *
   * @param args
   */
  public DigitmapInsert(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command Digitmap Insert
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("[<filename>][<OID>]");
    OptionBuilder.hasArgs();
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("digitmap");
    OptionBuilder.withDescription(
        "Insert this DigitMap into a file when compiling - PacketCable ONLY. "
            + "Multiple DigitMaps can be inserted simultaneously (space delimited). "
            + "OID optional.");
    return OptionBuilder.create("dm");
  }

  /**
   * Check for Digitmaps. Insert into map if found.
   *
   * @return boolean
   */
  public boolean hasDigitmap() {
    String sOID;
    File fDigitmap;
    boolean boolDigitmap = false;

    for (String string : this.args) {
      if (string.contains("|")) {
        String[] array = string.split("\\|");
        sOID = array[1];
        fDigitmap = new File(array[0]);
      } else {
        sOID = this.sDefaultOID;
        fDigitmap = new File(string);
      }

      if (fDigitmap.exists()) {
        this.map.put(sOID, HexString.fileToByteArray(fDigitmap));
        boolDigitmap = true;
      }
    }

    return boolDigitmap;
  }

  /**
   * Return Digitmap
   *
   * @return Map<String,byte[]>
   */
  public Map<String, byte[]> getDigitmap() {
    return this.map;
  }
}
