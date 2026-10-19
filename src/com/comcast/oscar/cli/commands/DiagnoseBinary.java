/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.tlv.DisassemblyOptions;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvDisassemblyReport;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/** Read-only, metadata-only binary diagnostic command. */
public final class DiagnoseBinary {
  private DiagnoseBinary() {}

  /**
   * Registers the standalone diagnostic options for parsing and help.
   *
   * @param options option collection to extend
   */
  public static void addOptions(Options options) {
    options.addOption(
        null,
        "diagnose-binary",
        true,
        "Inspect a binary file without editing; stop displayed tree at first structural failure.");
    options.addOption(
        null,
        "diagnostic-family",
        true,
        "Diagnostic framing: docsis (default), dpoe or dpog; use with --diagnose-binary.");
  }

  /**
   * Runs an isolated diagnostic command without legacy input normalization or output files.
   *
   * @param args standalone diagnostic arguments
   * @param output metadata report destination
   * @param errors usage and read failure destination; raw exceptions are never printed
   * @return 0 for structurally known input, 1 for diagnostic findings, 2 for usage/read errors
   */
  public static int run(String[] args, PrintStream output, PrintStream errors) {
    var options = new Options();
    addOptions(options);
    try {
      var line = new DefaultParser().parse(options, args);
      if (!line.hasOption("diagnose-binary")
          || !line.getArgList().isEmpty()
          || line.getOptions().length != (line.hasOption("diagnostic-family") ? 2 : 1)) {
        throw new IllegalArgumentException();
      }
      var family =
          DisassemblyOptions.Family.valueOf(
              line.getOptionValue("diagnostic-family", "docsis").toUpperCase(Locale.ROOT));
      var limits = new DisassemblyOptions(family, 1024 * 1024, 32, 10000, true);
      byte[] bytes;
      try (var input = Files.newInputStream(Path.of(line.getOptionValue("diagnose-binary")))) {
        bytes = input.readNBytes(limits.maxBytes() + 1);
      }
      var report = TlvDisassemble.diagnose(bytes, limits);
      output.println("Binary TLV Diagnostic");
      output.println("File size: " + bytes.length + " bytes; family: " + family);
      output.println("Structure only; values and MICs are not validated. Values are hidden.");
      output.println(
          "Unknown types assume one-byte lengths; subsequent boundaries are conditional.");
      output.println("OFFSET  TLV                  LENGTH  NAME / STATUS");
      int stoppedAt = printEntries(report.entries(), output);
      if (stoppedAt >= 0) {
        output.println(
            "Stopped at structural failure. Remaining bytes not inspected: "
                + stoppedAt
                + ".."
                + bytes.length
                + " (end exclusive).");
      } else {
        output.println("Reached end of input. Original file unchanged.");
      }
      return report.structurallyComplete() ? 0 : 1;
    } catch (ParseException | IllegalArgumentException exception) {
      errors.println("Usage: --diagnose-binary FILE [--diagnostic-family docsis|dpoe|dpog]");
      errors.println("Use this command alone; input must not exceed 1 MiB.");
      return 2;
    } catch (IOException exception) {
      errors.println("Cannot read diagnostic input file.");
      return 2;
    }
  }

  private static int printEntries(List<TlvDisassemblyReport.Entry> entries, PrintStream output) {
    for (var entry : entries) {
      String path = entry.tlvPath().stream().map(String::valueOf).collect(Collectors.joining("."));
      String indent = "  ".repeat(Math.max(0, entry.tlvPath().size() - 1));
      output.printf(
          Locale.ROOT,
          "%06d  %-20s %6d  %s%s [%s]%n",
          entry.headerOffset(),
          indent + path,
          entry.declaredLength(),
          indent,
          entry.name(),
          entry.status());
      switch (entry.status()) {
        case OK, UNKNOWN_TYPE, END, PADDING -> {}
        default -> {
          output.printf(
              Locale.ROOT,
              "  Failure at offset %d; declared=%d, available=%d bytes.%n",
              entry.headerOffset(),
              entry.declaredLength(),
              entry.endOffset() - entry.valueOffset());
          return entry.endOffset();
        }
      }
      int stoppedAt = printEntries(entry.children(), output);
      if (stoppedAt >= 0) {
        return stoppedAt;
      }
    }
    return -1;
  }
}
