/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.DigitMapOperation;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class DigitmapDisplay {

  /**
   * Set option parameters for command Digitmap display
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("displaydigitmap");
    OptionBuilder.withDescription("Display the DigitMap of the input file - PacketCable ONLY.");
    return OptionBuilder.create("ddm");
  }

  /**
   * Prints digitmap if file is binary
   *
   * @param file
   */
  public void printDigitmapDisplayFromBinary(File file) {
    ConfigurationFileExport cfePacketCable =
        new ConfigurationFileExport(file, ConfigurationFileExport.DOCSIS_PKTCBL);
    DigitMapOperation dmo = new DigitMapOperation(cfePacketCable);
    System.out.println(dmo.getDigitMap());
  }

  /**
   * Prints digitmap if file is text
   *
   * @param file
   */
  public void printDigitmapDisplayFromText(File file) {
    ConfigurationFileImport cfi = null;
    try {
      cfi = new ConfigurationFileImport(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ConfigurationFileException e) {
      e.printStackTrace();
    }

    if (cfi != null) {
      DigitMapOperation dmo = new DigitMapOperation(cfi);
      System.out.println(dmo.getDigitMap());
    }
  }
}
