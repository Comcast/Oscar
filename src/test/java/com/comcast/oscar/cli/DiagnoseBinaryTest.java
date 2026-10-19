package com.comcast.oscar.cli;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.cli.commands.DiagnoseBinary;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DiagnoseBinaryTest {
  @TempDir Path directory;

  @Test
  void stopsAfterNestedFailureAndDoesNotRewriteFile() throws Exception {
    Path file = directory.resolve("input [test].cfg");
    byte[] bytes = {60, 3, 1, 5, 1, 3, 1, 1};
    Files.write(file, bytes);
    var buffer = new ByteArrayOutputStream();
    try (var output = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
      assertEquals(
          1,
          DiagnoseBinary.run(new String[] {"--diagnose-binary", file.toString()}, output, output));
    }
    String text = buffer.toString(StandardCharsets.UTF_8);
    assertTrue(text.contains("TRUNCATED_VALUE"));
    assertFalse(text.contains("NetworkAccess"));
    assertTrue(text.contains("5..8"));
    assertArrayEquals(bytes, Files.readAllBytes(file));
  }

  @Test
  void printsUnknownMetadataButNotValuesAndRejectsOtherCommands() throws Exception {
    Path file = directory.resolve("input.cfg");
    Files.write(file, new byte[] {45, 6, 83, 69, 67, 82, 69, 84, (byte) 255});
    var buffer = new ByteArrayOutputStream();
    try (var output = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
      assertEquals(
          1,
          DiagnoseBinary.run(new String[] {"--diagnose-binary", file.toString()}, output, output));
      assertTrue(buffer.toString(StandardCharsets.UTF_8).contains("UNKNOWN_TYPE"));
      assertFalse(buffer.toString(StandardCharsets.UTF_8).contains("SECRET"));
      assertEquals(
          2,
          DiagnoseBinary.run(
              new String[] {"--diagnose-binary", file.toString(), "--output", "x"},
              output,
              output));
      assertEquals(2, DiagnoseBinary.run(new String[] {"--diagnose-binary"}, output, output));
      assertEquals(
          2,
          DiagnoseBinary.run(
              new String[] {
                "--diagnose-binary", file.toString(), "--diagnostic-family", "packetcable"
              },
              output,
              output));
      Files.write(file, new byte[] {3, 1, 1, (byte) 255, 0});
      assertEquals(
          0,
          DiagnoseBinary.run(new String[] {"--diagnose-binary", file.toString()}, output, output));
    }
  }
}
