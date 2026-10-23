/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.cli.commands.LintText;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LintTextCliTest {
  @TempDir Path directory;

  @Test
  void jsonContainsMultipleDiagnosticsAndNoRawValuesOrGeneratedFiles() throws Exception {
    var input = directory.resolve("bad.txt");
    String source =
        "Docsis {\n"
            + "Snmp11 1.3.6.1 Integer32 \"synthetic-private-value\";\n"
            + "NetworkAccess 1\n"
            + "MaxCPEAllowed 2;\n"
            + "}";
    Files.writeString(input, source);
    var output = new ByteArrayOutputStream();
    var errors = new ByteArrayOutputStream();
    try (var out = new PrintStream(output, true, StandardCharsets.UTF_8);
        var err = new PrintStream(errors, true, StandardCharsets.UTF_8)) {
      assertEquals(
          1, LintText.run(new String[] {"--lint-text", input.toString(), "--lint-json"}, out, err));
    }
    var json = new JSONObject(output.toString(StandardCharsets.UTF_8));
    assertTrue(json.getBoolean("fullyScanned"));
    assertTrue(json.getBoolean("hasErrors"));
    assertEquals(2, json.getJSONArray("diagnostics").length());
    assertEquals("ACCEPTED", json.getJSONArray("statements").getJSONObject(2).getString("status"));
    assertFalse(output.toString(StandardCharsets.UTF_8).contains("synthetic-private-value"));
    assertEquals("", errors.toString(StandardCharsets.UTF_8));
    assertEquals(source, Files.readString(input));
    try (var files = Files.list(directory)) {
      assertEquals(1, files.count());
    }
  }

  @Test
  void textOutputHasLocationAndExitCodesAreDistinct() throws Exception {
    var input = directory.resolve("test.txt");
    Files.writeString(input, "Docsis {\n Snmp11 1.3.6.1 Integer32 \"abc\";\n}");
    var output = new ByteArrayOutputStream();
    try (var stream = new PrintStream(output, true, StandardCharsets.UTF_8)) {
      assertEquals(1, LintText.run(new String[] {"--lint-text", input.toString()}, stream, stream));
      assertTrue(output.toString(StandardCharsets.UTF_8).contains(":2:"));
      assertFalse(output.toString(StandardCharsets.UTF_8).contains("Exception"));
      Files.writeString(input, "Docsis { NetworkAccess 1; }");
      assertEquals(0, LintText.run(new String[] {"--lint-text", input.toString()}, stream, stream));
      assertEquals(2, LintText.run(new String[] {"--lint-json"}, stream, stream));
      assertEquals(
          2, LintText.run(new String[] {"--lint-text", input.toString(), "-c"}, stream, stream));
      assertEquals(
          2,
          LintText.run(
              new String[] {"--lint-text", directory.resolve("absent").toString()},
              stream,
              stream));
    }
  }
}
