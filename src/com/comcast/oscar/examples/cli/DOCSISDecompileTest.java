/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples.cli;

import com.comcast.oscar.cli.CommandRun;
import com.comcast.oscar.utilities.DirectoryStructure;

/** */
public class DOCSISDecompileTest {

  /**
   * Method main.
   *
   * @param args String[]
   */
  public static void main(String[] args) {
    /* Define arguments */
    String arguments[] = new String[3];
    arguments[0] = "-d";
    arguments[1] = "-i";
    arguments[2] = "testfiles/DOCSIS-GOLDEN.bin";

    DirectoryStructure.createDirectories();

    /* Run commands */
    CommandRun cmds = new CommandRun();
    cmds.run(arguments);
  }
}
