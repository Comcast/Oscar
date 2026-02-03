package com.comcast.oscar.cli;

import com.comcast.oscar.cli.commands.CVC;
import com.comcast.oscar.cli.commands.Decompile;
import com.comcast.oscar.cli.commands.DigitmapDisplay;
import com.comcast.oscar.cli.commands.DigitmapInsert;
import com.comcast.oscar.cli.commands.DownstreamFrequency;
import com.comcast.oscar.cli.commands.Firmware;
import com.comcast.oscar.cli.commands.FullTLVDisplay;
import com.comcast.oscar.cli.commands.HexDisplay;
import com.comcast.oscar.cli.commands.Input;
import com.comcast.oscar.cli.commands.JSONDisplay;
import com.comcast.oscar.cli.commands.JSONtoTLV;
import com.comcast.oscar.cli.commands.Key;
import com.comcast.oscar.cli.commands.MaxCPE;
import com.comcast.oscar.cli.commands.MergeBulk;
import com.comcast.oscar.cli.commands.OID;
import com.comcast.oscar.cli.commands.Output;
import com.comcast.oscar.cli.commands.Specification;
import com.comcast.oscar.cli.commands.TFTPServer;
import com.comcast.oscar.cli.commands.TLV;
import com.comcast.oscar.cli.commands.TLVDescription;
import com.comcast.oscar.cli.commands.TLVtoJSON;
import com.comcast.oscar.cli.commands.Translate;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public class BuildOptions {

  /**
   * Build all usable options with LongOpt, Description and any viable arguments
   *
   * @param options
   */
  public static Options run(Options options) {
    options.addOption("h", "help", false, "View all commands with descriptions.");
    options.addOption("version", false, "Display current version.");
    options.addOption(CVC.OptionParameters());
    options.addOption(Decompile.OptionParameters());
    options.addOption(DigitmapDisplay.OptionParameters());
    options.addOption(DigitmapInsert.OptionParameters());
    options.addOption(DownstreamFrequency.OptionParameters());
    options.addOption(Firmware.OptionParameters());
    options.addOption(FullTLVDisplay.OptionParameters());
    options.addOption(HexDisplay.OptionParameters());
    options.addOption(Input.OptionParameters());
    options.addOption(JSONDisplay.OptionParameters());
    options.addOption(JSONtoTLV.OptionParameters());
    options.addOption(Key.OptionParameters());
    options.addOption(MaxCPE.OptionParameters());
    options.addOption(MergeBulk.OptionParameters());
    options.addOption(OID.OptionParameters());
    options.addOption(Output.OptionParameters());
    options.addOption(Specification.OptionParameters());
    options.addOption(Translate.OptionParameters());
    options.addOption(TFTPServer.OptionParameters());
    options.addOption(TLV.OptionParameters());
    options.addOption(TLVDescription.OptionParameters());
    options.addOption(TLVtoJSON.OptionParameters());

    options.addOption("c", "compile", false, "Compile text to binary.");

    OptionBuilder.withArgName("bin/txt> <input dir> <*output dir");
    OptionBuilder.hasArgs();
    OptionBuilder.hasOptionalArgs();
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("bulk");
    OptionBuilder.withDescription(
        "Compile all files to binary from the input directory. Output directory optional.");
    options.addOption(OptionBuilder.create("b"));
    return options;
  }
}
