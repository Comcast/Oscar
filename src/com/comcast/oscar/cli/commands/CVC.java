/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.utilities.DirectoryStructure;
import java.io.File;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class CVC {

  public static final String ERROR =
      "No cvc filename argument found. For CoSigner use this format for the argument: c=<filename>."
          + " For Manufacturer use this format for the argument: m=<filename>.";

  private final String[] args;
  private File fCoSigner;
  private File fManufacturer;

  /**
   * Get CVC arguments
   *
   * @param args
   */
  public CVC(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command CVC
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("c/m=<filename>");
    OptionBuilder.hasArgs();
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("certificate");
    OptionBuilder.withDescription(
        "Add this CVC during file compilation. "
            + "For CoSigner use this format for the argument: c=<filename>. "
            + "For Manufacturer use this format for the argument: m=<filename>. "
            + "Both CVCs can be inserted simultaneously (space delimited).");
    return OptionBuilder.create("cvc");
  }

  /**
   * Check for a CoSigner CVC. Insert into fCoSigner if found.
   *
   * @return boolean
   */
  public boolean hasCoSigner() {
    for (String string : this.args) {
      if (string.contains("=")) {
        String[] array = string.split("=");
        if (array[0].equalsIgnoreCase("c") || array[0].equalsIgnoreCase("co")) {
          if (new File(DirectoryStructure.fCertificatesDir(), array[1]).exists()) {
            this.fCoSigner = new File(DirectoryStructure.fCertificatesDir(), array[1]);
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Return CoSigner file
   *
   * @return File
   */
  public File getCoSigner() {
    return this.fCoSigner;
  }

  /**
   * Check for a Manufacturer CVC. Insert into fManufacturer if found.
   *
   * @return boolean
   */
  public boolean hasManufacturer() {
    for (String string : this.args) {
      if (string.contains("=")) {
        String[] array = string.split("=");
        if (array[0].equalsIgnoreCase("m") || array[0].equalsIgnoreCase("man")) {
          if (new File(DirectoryStructure.fCertificatesDir(), array[1]).exists()) {
            System.out.println(new File(DirectoryStructure.fCertificatesDir(), array[1]));
            this.fManufacturer = new File(DirectoryStructure.fCertificatesDir(), array[1]);
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Return Manufacturer file
   *
   * @return File
   */
  public File getManufacturer() {
    return this.fManufacturer;
  }

  /**
   * Check if a CVC was specified
   *
   * @return boolean
   */
  public boolean hasCVC() {
    if (hasCoSigner() || hasManufacturer()) return true;
    return false;
  }
}
