/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.parserbuilder;

import com.comcast.oscar.utilities.DirectoryStructure;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

  public static void main(String[] args) {

    String sExecutableJar =
        "java -jar "
            + DirectoryStructureAntlr.fAntlrLibFile().getAbsolutePath()
            + " "
            + "-o "
            + DirectoryStructureAntlr.fAntlrParseSrcDir().getAbsolutePath()
            + " "
            + DirectoryStructureAntlr.fGrammarFile().getAbsolutePath();

    Process p = null;
    try {
      p = Runtime.getRuntime().exec(sExecutableJar);
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

    String sStd_IO = "";

    /*Read the output from the command If Any */
    try {
      while ((sStd_IO = stdInput.readLine()) != null) {
        System.out.println(sStd_IO);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      while ((sStd_IO = stdError.readLine()) != null) {
        System.out.println(sStd_IO);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** */
  public static class DirectoryStructureAntlr extends DirectoryStructure {

    /**
     * Directory for ANTLR
     *
     * @return ANTLR subdirectory
     */
    public static File fAntlrDir() {
      return new File(sBasePath() + File.separator + "ANTLR");
    }

    /**
     * Locates the generator matching the loaded ANTLR runtime, downloaded by RunTlvLex.sh.
     *
     * @return the complete tool JAR beneath target/antlr-generator; this does not download it
     */
    public static File fAntlrLibFile() {
      return new File(
          sBasePath(),
          "target/antlr-generator/antlr-"
              + org.antlr.v4.runtime.RuntimeMetaData.VERSION
              + "-complete.jar");
    }

    /**
     * @return
     */
    public static File fAntlrParseSrcDir() {
      return new File(
          sBasePath()
              + File.separator
              + "src"
              + File.separator
              + "com"
              + File.separator
              + "comcast"
              + File.separator
              + "oscar"
              + File.separator
              + "parser");
    }

    /**
     * @return
     */
    public static File fGrammarFile() {
      return new File(
          sBasePath()
              + File.separator
              + "ANTLR"
              + File.separator
              + "grammar"
              + File.separator
              + "tlv.g4");
    }
  }
}
