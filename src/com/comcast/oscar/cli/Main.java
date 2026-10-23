/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli;

import com.comcast.oscar.constants.Constants;
import com.comcast.oscar.utilities.DirectoryStructure;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class Main {

  /**
   * Runs standalone diagnostics without creating directories, or initializes the legacy CLI. Lint
   * exits with 0 for clean formatting, 1 for findings, and 2 for usage/read failures. Unexpected
   * Java errors propagate normally.
   *
   * @param args command-line arguments, including standalone --lint-text FILE [--lint-json]
   */
  public static void main(String args[]) {
    if (java.util.Arrays.stream(args)
        .anyMatch(
            argument ->
                argument.equals("--lint-text")
                    || argument.startsWith("--lint-text=")
                    || argument.equals("--lint-json"))) {
      System.exit(com.comcast.oscar.cli.commands.LintText.run(args, System.out, System.err));
      return;
    }
    // Keep diagnostics independent of directory creation, banner output, and bracket rewriting.
    if (java.util.Arrays.stream(args)
        .anyMatch(
            argument ->
                argument.equals("--diagnose-binary")
                    || argument.startsWith("--diagnose-binary=")
                    || argument.equals("--diagnostic-family")
                    || argument.startsWith("--diagnostic-family="))) {
      System.exit(com.comcast.oscar.cli.commands.DiagnoseBinary.run(args, System.out, System.err));
      return;
    }
    /* Determine path. Remove for testing purposes. */
    DirectoryStructure.setFromJar();

    /* Create sub-directories if they do not exist */
    DirectoryStructure.createDirectories();

    /* Load and validate immutable TLV catalogs before running commands. */
    com.comcast.oscar.dictionary.TlvRegistry.forFamily("docsis");

    /* Run commands */
    System.out.println(Constants.OSCAR_CLI_HEADER);

    if (args.length <= 0) {
      System.out.println(Constants.OSCAR_CLI_NO_ARGS);
    }

    CommandRun cmds = new CommandRun();
    cmds.run(cleanUpARGS(args));
  }

  /**
   * Clean up arguments for processing within the CLI API.
   *
   * @param args
   * @return String[]
   */
  private static String[] cleanUpARGS(String[] args) {
    // Changes user input format (e.g. [<value>][<value>]) to an easier format for CLI (e.g.
    // <value>|<value>).
    for (int index = 0; index < args.length; index++) {
      if (args[index].contains("[")) {
        args[index] = args[index].replaceAll("]\\[", "|").replaceAll("]|\\[", "");
      }
    }

    return args;
  }
}
