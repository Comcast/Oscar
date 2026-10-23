/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import com.comcast.oscar.validation.TextLintOptions;
import com.comcast.oscar.validation.TextLintReport;
import com.comcast.oscar.validation.TextLinter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

/** Read-only text lint entry point, independent of legacy compilation and directory creation. */
public final class LintText {
  private static final int JSON_SCHEMA_VERSION = 1;

  private LintText() {}

  /**
   * Registers standalone formatting-lint options in help or a dedicated parser.
   *
   * @param options options collection to extend
   */
  public static void addOptions(Options options) {
    options.addOption(
        null,
        "lint-text",
        true,
        "Check text syntax and SNMP value formats; continue after recoverable errors without"
            + " compiling.");
    options.addOption(
        null, "lint-json", false, "Emit structured text-lint JSON; use with --lint-text.");
  }

  /**
   * Checks a UTF-8 file without producing binaries, changing source, or resolving MIB/DNS names.
   * Known input errors produce diagnostics; unexpected Java failures propagate normally.
   *
   * @param args standalone arguments: --lint-text FILE [--lint-json]
   * @param output diagnostic text or JSON destination; values and source lines are not printed
   * @param errors usage/file-read failures
   * @return 0 for clean formatting, 1 for findings/incomplete scan, 2 for usage/read failures
   */
  public static int run(String[] args, PrintStream output, PrintStream errors) {
    var options = new Options();
    addOptions(options);
    String filename;
    boolean json;
    try {
      var command = new DefaultParser().parse(options, args);
      if (!command.hasOption("lint-text")
          || !command.getArgList().isEmpty()
          || command.getOptions().length != (command.hasOption("lint-json") ? 2 : 1)) {
        errors.println("Usage: --lint-text FILE [--lint-json]");
        return 2;
      }
      filename = command.getOptionValue("lint-text");
      json = command.hasOption("lint-json");
    } catch (ParseException invalidArguments) {
      errors.println("Usage: --lint-text FILE [--lint-json]");
      return 2;
    }
    String text;
    var limits = TextLintOptions.defaults();
    try (var reader = Files.newBufferedReader(Path.of(filename), StandardCharsets.UTF_8)) {
      char[] buffer = new char[limits.maxCharacters() + 1];
      int used = 0;
      while (used < buffer.length) {
        int count = reader.read(buffer, used, buffer.length - used);
        if (count < 0) {
          break;
        }
        used += count;
      }
      text = new String(buffer, 0, used);
    } catch (IOException | InvalidPathException invalidFile) {
      errors.println("Cannot read text lint input as UTF-8: " + filename);
      return 2;
    }
    var report = TextLinter.lint(text, limits);
    if (json) {
      output.println(json(filename, report).toString(2));
    } else {
      for (var diagnostic : report.diagnostics()) {
        output.printf(
            "%s:%d:%d: error [%s]: %s%n",
            filename,
            diagnostic.range().line(),
            diagnostic.range().column(),
            diagnostic.code(),
            diagnostic.message());
      }
      long skipped =
          report.statements().stream()
              .filter(s -> s.status() == TextLintReport.Status.SKIPPED)
              .count();
      output.printf(
          "%d diagnostic(s); %d leaf TLV(s) checked, %d skipped; scan %s.%n",
          report.diagnostics().size(),
          report.statements().size(),
          skipped,
          report.fullyScanned() ? "complete" : "incomplete");
      output.println(
          "Formatting and SNMP value types only; no compilation or output file. Values are"
              + " hidden.");
    }
    return report.hasErrors() || !report.fullyScanned() ? 1 : 0;
  }

  private static JSONObject json(String filename, TextLintReport report) {
    var diagnostics = new JSONArray();
    for (var diagnostic : report.diagnostics()) {
      diagnostics.put(
          new JSONObject()
              .put("severity", diagnostic.severity().name())
              .put("code", diagnostic.code().name())
              .put("message", diagnostic.message())
              .put("range", range(diagnostic.range()))
              .put("tlvNames", new JSONArray(diagnostic.tlvNames())));
    }
    var statements = new JSONArray();
    for (var statement : report.statements()) {
      statements.put(
          new JSONObject()
              .put("tlvNames", new JSONArray(statement.tlvNames()))
              .put("status", statement.status().name())
              .put("range", range(statement.range())));
    }
    return new JSONObject()
        .put("schemaVersion", JSON_SCHEMA_VERSION)
        .put("source", filename)
        .put("scope", "syntax-and-snmp-value-format")
        .put("fullyScanned", report.fullyScanned())
        .put("hasErrors", report.hasErrors())
        .put("diagnostics", diagnostics)
        .put("statements", statements);
  }

  private static JSONObject range(TextLintReport.SourceRange range) {
    return new JSONObject()
        .put("startOffset", range.startOffset())
        .put("endOffset", range.endOffset())
        .put("line", range.line())
        .put("column", range.column())
        .put("endLine", range.endLine())
        .put("endColumn", range.endColumn());
  }
}
