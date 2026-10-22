/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.utilities.Zip;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

/** CLI argument parsing, on-disk bulk outputs, merge combinations and ZIP interoperability. */
class BulkCliTest {
  private static final String LAB_KEY = "public-bulk-test-key";
  private static final int SCALE_FILE_COUNT = 10_000;
  private static final int SCALE_MERGE_SIDE = 100;
  private static final int WARMUP_FILE_COUNT = 20;
  private static final Duration PROCESS_TIMEOUT = Duration.ofMinutes(10);
  private static final String MINIMUM =
      """
      NetworkAccess 1;
      UpstreamServiceFlow { ServiceFlowReference 1; QualityOfServiceParameterSetType 07; }
      DownstreamServiceFlow { ServiceFlowReference 2; QualityOfServiceParameterSetType 07; }
      """;

  @TempDir(cleanup = CleanupMode.ALWAYS)
  Path temporary;

  @Test
  void bulkAndMergeCommandsPreserveEveryFileAndZipEntry() throws Exception {
    exercise(12, 2, 3, false);
  }

  @Test
  @EnabledIfSystemProperty(named = "oscar.bulk.scale", matches = "true")
  void tenThousandFilesTimingBaseline() throws Exception {
    exercise(SCALE_FILE_COUNT, SCALE_MERGE_SIDE, SCALE_MERGE_SIDE, true);
  }

  @Test
  void emptyMissingAndMalformedInputsAreChecked() throws Exception {
    Path root = Files.createDirectory(temporary.resolve("invalid-inputs"));
    Files.writeString(root.resolve("lab.key"), LAB_KEY);
    Path empty = Files.createDirectory(root.resolve("empty"));
    Path output = Files.createDirectory(root.resolve("output"));
    var emptyResult = runCli(root, "empty", "-b", "bin", empty.toString(), output.toString());
    assertEquals(0, emptyResult.exitCode(), emptyResult.log());
    assertFileCount(output, 0);
    var missing =
        runCli(root, "missing", "-b", "bin", root.resolve("absent").toString(), output.toString());
    assertTrue(missing.log().contains("does not exist"), missing.log());
    // Characterize the legacy status defect; a diagnostic is not successful conversion.
    assertEquals(
        0,
        missing.exitCode(),
        "Legacy missing-directory status has changed; review the documented gap");
    Path broken = Files.createDirectory(root.resolve("broken"));
    Files.writeString(broken.resolve("bad.txt"), "Docsis { NetworkAccess 1; }");
    var malformed = runCli(root, "malformed", "-b", "bin", broken.toString(), output.toString());
    assertNotEquals(0, malformed.exitCode(), malformed.log());
    assertTrue(malformed.log().contains("Missing required DOCSIS top-level TLVs"), malformed.log());
    assertFileCount(output, 0);
    var noMergeInput =
        runCli(root, "merge-no-input", "--mergebulkbuild", "o=" + output, "e=.cfg", "b");
    assertNotEquals(0, noMergeInput.exitCode(), noMergeInput.log());
    assertTrue(noMergeInput.log().contains("No input directory specified"), noMergeInput.log());
    assertFileCount(output, 0);
  }

  @Test
  void recordsLegacySpaceInDirectoryArgumentDefect() throws Exception {
    Path root = Files.createDirectory(temporary.resolve("space-case"));
    Files.writeString(root.resolve("lab.key"), LAB_KEY);
    Path input = Files.createDirectory(root.resolve("input files"));
    Path output = Files.createDirectory(root.resolve("output"));
    Files.writeString(input.resolve("valid.txt"), source(0));
    var result = runCli(root, "spaces", "-b", "bin", input.toString(), output.toString());
    assertTrue(result.log().contains("does not exist"), result.log());
    assertFileCount(output, 0);
    assertEquals(
        0,
        result.exitCode(),
        "Legacy space-path defect changed; replace with a successful-output regression");
  }

  private void exercise(int fileCount, int modelCount, int tierCount, boolean record)
      throws Exception {
    Path root = Files.createDirectory(temporary.resolve("bulk-workspace"));
    var timings = new ArrayList<String>();
    timings.add("phase,files,elapsed_ns");
    try {
      Path input = Files.createDirectory(root.resolve("input"));
      Path binary = Files.createDirectory(root.resolve("binary"));
      Path text = Files.createDirectory(root.resolve("text"));
      Path rebuilt = Files.createDirectory(root.resolve("rebuilt"));
      Path key = Files.writeString(root.resolve("lab.key"), LAB_KEY, StandardCharsets.UTF_8);
      Map<String, byte[]> expected = new LinkedHashMap<>();
      // Setup and independent reference compilation are outside measured work.
      for (int index = 0; index < fileCount; index++) {
        String name = "config-" + index + ".cfg";
        String source = source(index);
        Files.writeString(input.resolve(name), source, StandardCharsets.UTF_8);
        expected.put(name, compile(source));
      }
      timedCli(
          root,
          "bulk-text-to-binary",
          fileCount,
          timings,
          "-k",
          key.toString(),
          "--bulk",
          "bin",
          input.toString(),
          binary.toString());
      assertBinaryFiles(binary, expected);
      timedCli(
          root,
          "bulk-binary-to-text",
          fileCount,
          timings,
          "-k",
          key.toString(),
          "-b",
          "txt",
          binary.toString(),
          text.toString());
      assertFileCount(text, fileCount);
      for (var entry : expected.entrySet()) {
        assertArrayEquals(
            entry.getValue(),
            compile(Files.readString(text.resolve(entry.getKey()))),
            entry.getKey());
      }
      timedCli(
          root,
          "bulk-text-recompile",
          fileCount,
          timings,
          "-k",
          key.toString(),
          "-b",
          "bin",
          text.toString(),
          rebuilt.toString());
      assertBinaryFiles(rebuilt, expected);
      Path copied = Files.createDirectory(root.resolve("binary-copy"));
      timedCli(
          root,
          "bulk-binary-to-binary",
          fileCount,
          timings,
          "-k",
          key.toString(),
          "-b",
          "bin",
          binary.toString(),
          copied.toString());
      assertBinaryFiles(copied, expected);
      checkZip(root, binary, expected, timings);
      checkMerge(root, key, modelCount, tierCount, timings);
    } finally {
      // Cleanup runs even after a failed assertion or child-process timeout.
      try (var paths = Files.walk(root)) {
        for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
          Files.delete(path);
        }
      }
      assertFalse(Files.exists(root), "Generated bulk workspace must be removed");
    }
    if (record) {
      Path reports = Files.createDirectories(Path.of("target/bulk-baseline"));
      Path report = Files.createTempFile(reports, "sample-", ".csv");
      Files.write(report, timings, StandardCharsets.UTF_8);
      System.out.println("Bulk baseline: " + report + " (generated files removed)");
    }
  }

  private void checkMerge(Path root, Path key, int models, int tiers, List<String> timings)
      throws Exception {
    Path modelDir = Files.createDirectory(root.resolve("models"));
    Path tierDir = Files.createDirectory(root.resolve("tiers"));
    Map<String, byte[]> expected = new LinkedHashMap<>();
    for (int model = 0; model < models; model++) {
      Files.writeString(modelDir.resolve("model-" + model + ".txt"), source(model));
    }
    for (int tier = 0; tier < tiers; tier++) {
      Files.writeString(
          tierDir.resolve("tier-" + tier + ".txt"), "Docsis { MaxCPEAllowed " + (tier + 1) + "; }");
      for (int model = 0; model < models; model++) {
        String merged = source(model);
        merged =
            merged.substring(0, merged.lastIndexOf('}')) + "MaxCPEAllowed " + (tier + 1) + "; }";
        expected.put("model-" + model + "tier-" + tier + ".cfg", compile(merged));
      }
    }
    Path binary = Files.createDirectory(root.resolve("merge-binary"));
    timedCli(
        root,
        "merge-binary",
        expected.size(),
        timings,
        "-s",
        "d",
        "3.1",
        "-k",
        key.toString(),
        "--mergebulkbuild",
        modelDir.toString(),
        tierDir.toString(),
        "o=" + binary,
        "e=.cfg",
        "b");
    assertBinaryFiles(binary, expected);
    Path text = Files.createDirectory(root.resolve("merge-text"));
    timedCli(
        root,
        "merge-text",
        expected.size(),
        timings,
        "-s",
        "d",
        "3.1",
        "-k",
        key.toString(),
        "-mbb",
        modelDir.toString(),
        tierDir.toString(),
        "o=" + text,
        "e=.txt",
        "t");
    assertFileCount(text, expected.size());
    for (var entry : expected.entrySet()) {
      String name = entry.getKey().replace(".cfg", ".txt");
      assertArrayEquals(entry.getValue(), compile(Files.readString(text.resolve(name))), name);
    }
  }

  private void checkZip(Path root, Path binary, Map<String, byte[]> expected, List<String> timings)
      throws Exception {
    Map<File, byte[]> contents = new LinkedHashMap<>();
    for (String name : expected.keySet()) {
      contents.put(new File(name), Files.readAllBytes(binary.resolve(name)));
    }
    Map<File, byte[]> warmup = new LinkedHashMap<>();
    contents.entrySet().stream()
        .limit(WARMUP_FILE_COUNT)
        .forEach(e -> warmup.put(e.getKey(), e.getValue()));
    Zip.ZipFile(root.resolve("warmup-archive").toFile(), warmup);
    System.out.println("Starting ZIP creation for " + contents.size() + " files");
    long started = System.nanoTime();
    Zip.ZipFile(root.resolve("configurations").toFile(), contents);
    timings.add("zip-create," + contents.size() + "," + (System.nanoTime() - started));
    started = System.nanoTime();
    try (var zip = new java.util.zip.ZipFile(root.resolve("configurations.zip").toFile())) {
      assertEquals(expected.size(), zip.size());
      for (var entry : expected.entrySet()) {
        var stored = zip.getEntry(entry.getKey());
        assertNotNull(stored, entry.getKey());
        try (var stream = zip.getInputStream(stored)) {
          assertArrayEquals(entry.getValue(), stream.readAllBytes(), entry.getKey());
        }
      }
    }
    timings.add("zip-verify," + contents.size() + "," + (System.nanoTime() - started));
  }

  private void timedCli(
      Path root, String phase, int count, List<String> timings, String... arguments)
      throws Exception {
    System.out.println("Starting " + phase + " for " + count + " files");
    var result = runCli(root, phase, arguments);
    assertEquals(0, result.exitCode(), phase + ": " + result.log());
    assertTrue(result.elapsedNanos() >= 0, phase + " must finish");
    timings.add(phase + "," + count + "," + result.elapsedNanos());
  }

  private record CliResult(int exitCode, String log, long elapsedNanos) {}

  private CliResult runCli(Path root, String phase, String... arguments) throws Exception {
    String classpath =
        Arrays.stream(
                System.getProperty("java.class.path")
                    .split(java.util.regex.Pattern.quote(File.pathSeparator)))
            .map(p -> Path.of(p).toAbsolutePath().toString())
            .collect(java.util.stream.Collectors.joining(File.pathSeparator));
    Path report = root.resolve(phase + ".time");
    Path log = root.resolve(phase + ".log");
    var command =
        new ArrayList<>(
            List.of(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-Xmx512m",
                "-Doscar.snmp.cache.path=" + root.resolve("cache.json"),
                "-cp",
                classpath,
                CliProcess.class.getName(),
                report.toString()));
    command.addAll(List.of(arguments));
    Process process =
        new ProcessBuilder(command)
            .directory(root.toFile())
            .redirectErrorStream(true)
            .redirectOutput(log.toFile())
            .start();
    try {
      assertTrue(
          process.waitFor(PROCESS_TIMEOUT.toSeconds(), TimeUnit.SECONDS), phase + " timed out");
      return new CliResult(
          process.exitValue(),
          readLog(log),
          Files.exists(report) ? Long.parseLong(Files.readString(report)) : -1);
    } finally {
      if (process.isAlive()) {
        process.destroyForcibly();
        process.waitFor();
      }
    }
  }

  private static String readLog(Path path) {
    try {
      return Files.readString(path);
    } catch (IOException e) {
      return e.toString();
    }
  }

  private static String source(int index) {
    return "Docsis { " + MINIMUM + "DownstreamFrequency " + (100_000_000 + index) + "; }";
  }

  private static byte[] compile(String source) throws Exception {
    var imported = new ConfigurationFileImport(new StringBuilder(source));
    var file =
        new ConfigurationFile(
            imported.getConfigurationFileType(), imported.getTlvBuilder(), LAB_KEY);
    file.commit();
    return file.toByteArray();
  }

  private static void assertFileCount(Path directory, int expected) throws IOException {
    try (var files = Files.list(directory)) {
      assertEquals(expected, files.count(), directory.toString());
    }
  }

  private static void assertBinaryFiles(Path directory, Map<String, byte[]> expected)
      throws IOException {
    assertFileCount(directory, expected.size());
    for (var entry : expected.entrySet()) {
      assertArrayEquals(
          entry.getValue(), Files.readAllBytes(directory.resolve(entry.getKey())), entry.getKey());
    }
  }

  /** Separate JVM confines CLI exits and relative paths to the disposable workspace. */
  public static class CliProcess {
    /**
     * Warms the bulk compiler with synthetic inputs, then times the CLI argument dispatcher.
     *
     * @param args timing destination followed by the actual CLI arguments
     * @throws Exception if setup, dispatch, or timing output fails
     */
    public static void main(String[] args) throws Exception {
      Path report = Path.of(args[0]);
      Path input = Files.createDirectory(report.resolveSibling(report.getFileName() + "-warm-in"));
      Path output =
          Files.createDirectory(report.resolveSibling(report.getFileName() + "-warm-out"));
      Path key = report.resolveSibling("lab.key");
      for (int index = 0; index < WARMUP_FILE_COUNT; index++) {
        Files.writeString(input.resolve("warm-" + index), source(index));
      }
      new CommandRun()
          .run(
              new String[] {
                "-k", key.toString(), "-b", "bin", input.toString(), output.toString()
              });
      long started = System.nanoTime();
      new CommandRun().run(Arrays.copyOfRange(args, 1, args.length));
      Files.writeString(report, Long.toString(System.nanoTime() - started));
    }
  }
}
