/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.buildbulk.MergeBulkBuild;
import com.comcast.oscar.utilities.DirectoryStructure;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 */
public class MergeBulk {

  public static final String ERROR = "No input directory specified.";

  private final String[] args;
  private File fOutputDir = new File(DirectoryStructure.sBasePath());
  private String sExtension;
  private boolean boolIsBinary = true;
  private List<File> list = new ArrayList<File>();
  private MergeBulkBuild mbb;

  public MergeBulk(String[] args) {
    this.args = args;
  }

  /**
   * Set option parameters for command Maximum CPE
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withArgName("input dirs> <o=<output dir>> <e=<extension>> <b{inary}/t{ext}");
    OptionBuilder.hasArgs();
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("mergebulkbuild");
    OptionBuilder.withDescription(
        "Merge multiple text files from directories into one binary. EX: -mbb inputDirectoryModel"
            + " inputDirectoryTier inputDirectoryCPE o=outputDirectory e=bin text. Output"
            + " directory, extension and b{inary}/t{ext} are optional.");
    return OptionBuilder.create("mbb");
  }

  /**
   * Check for any OID arguments
   *
   * @return boolean
   */
  public boolean hasInputDir() {
    boolean boolInputDir = false;

    for (String string : this.args) {
      if (string.contains("=")) {
        String[] array = string.split("=");

        if (array[0].equalsIgnoreCase("o")) {
          if (new File(array[1]).getParentFile() != null) {
            this.fOutputDir = new File(array[1] + File.separator);
          } else {
            this.fOutputDir =
                new File(
                    DirectoryStructure.sBasePath() + File.separator + array[1] + File.separator);
          }

          if (this.fOutputDir != null) {
            this.fOutputDir.mkdirs();
          }
        }

        if (array[0].equalsIgnoreCase("e")) {
          this.sExtension = array[1];
        }
      } else if (string.equalsIgnoreCase("b")) {
        this.boolIsBinary = true;
      } else if (string.equalsIgnoreCase("t")) {
        this.boolIsBinary = false;
      } else {
        this.list.add(new File(string));
        boolInputDir = true;
      }
    }

    return boolInputDir;
  }

  /**
   * Merge input files into output file
   *
   * @param configurationFileType
   * @param key
   */
  public void mergeFiles(int configurationFileType, String key) {
    this.mbb = new MergeBulkBuild(configurationFileType, this.fOutputDir, this.boolIsBinary, key);

    this.mbb.FILENAME_EXTENSION = this.sExtension;

    for (File file : this.list) {
      this.mbb.addInputDirectory(file);
    }

    this.mbb.start();
  }

  /**
   * Return output directory
   *
   * @return
   */
  public File getOutputDir() {
    return this.fOutputDir;
  }

  /**
   * Return input files
   *
   * @return
   */
  public List<File> getInputDir() {
    return this.list;
  }
}
