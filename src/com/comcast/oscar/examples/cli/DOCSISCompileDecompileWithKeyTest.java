/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples.cli;

import com.comcast.oscar.cli.CommandRun;
import com.comcast.oscar.utilities.DirectoryStructure;
import java.io.File;

/** */
public class DOCSISCompileDecompileWithKeyTest {

  /**
   * Method main.
   *
   * @param args String[]
   */
  public static void main(String[] args) {
    /* Define arguments */
    String arguments[] = new String[7];
    arguments[0] = "-c";
    arguments[1] = "-i";
    arguments[2] = "testfiles/DOCSIS-GOLDEN.txt";
    arguments[3] = "-o";
    arguments[4] = "output/DOCSIS-GOLDEN.bin";
    arguments[5] = "-k";
    arguments[6] = "testfiles/sharedsecret.txt";

    DirectoryStructure.createDirectories();

    /* Run commands */
    CommandRun cmds = new CommandRun();
    cmds.run(arguments);

    /* Checks */
    if (new File(arguments[4]).exists()) {
      System.out.println("OUTPUT FILE " + arguments[4] + " exists");
    } else {
      System.err.println("OUTPUT FILE " + arguments[4] + " does not exist");
    }

    /* Define arguments */
    String newArguments[] = new String[3];
    newArguments[0] = "-d";
    newArguments[1] = "-i";
    newArguments[2] = arguments[4];

    /* Run commands */
    cmds.run(newArguments);
  }
}
