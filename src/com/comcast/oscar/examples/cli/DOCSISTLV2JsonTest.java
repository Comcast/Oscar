/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples.cli;

import com.comcast.oscar.cli.CommandRun;
import com.comcast.oscar.utilities.DirectoryStructure;

/** */
public class DOCSISTLV2JsonTest {

  /**
   * Method main.
   *
   * @param args String[]
   */
  public static void main(String[] args) {
    /* Define arguments */
    String arguments[] = new String[2];
    arguments[0] = "-t2j";
    arguments[1] = "030101";

    DirectoryStructure.createDirectories();

    /* Run commands */
    CommandRun cmds = new CommandRun();
    cmds.run(arguments);

    String secondArguments[] = new String[2];
    secondArguments[0] = arguments[0];
    secondArguments[1] = "010101";

    cmds.run(secondArguments);

    String thirdArguments[] = new String[2];
    thirdArguments[0] = arguments[0];
    thirdArguments[1] = "020101";

    cmds.run(thirdArguments);
  }
}
