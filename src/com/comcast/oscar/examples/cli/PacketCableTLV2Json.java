/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples.cli;

import com.comcast.oscar.cli.CommandRun;
import com.comcast.oscar.utilities.DirectoryStructure;

/** */
public class PacketCableTLV2Json {

  /**
   * Method main.
   *
   * @param args String[]
   */
  public static void main(String[] args) {
    /* Define arguments */
    String arguments[] = new String[4];
    arguments[0] = "-t2j";
    arguments[1] = "0B153013060B2B06010201450102010201400400000000";
    arguments[2] = "-s";
    arguments[3] = "p";

    DirectoryStructure.createDirectories();

    /* Run commands */
    CommandRun cmds = new CommandRun();
    cmds.run(arguments);

    /*String secondArguments[] = new String[4];
    secondArguments[0] = arguments[0];
    secondArguments[1] = "430101";
    secondArguments[2] = arguments[2];
    secondArguments[3] = arguments[3];

    cmds.runCommands(secondArguments);

    String thirdArguments[] = new String[4];
    thirdArguments[0] = arguments[0];
    thirdArguments[1] = "640101";
    thirdArguments[2] = arguments[2];
    thirdArguments[3] = arguments[3];

    cmds.runCommands(thirdArguments);*/
  }
}
