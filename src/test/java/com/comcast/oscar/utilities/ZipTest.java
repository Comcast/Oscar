/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;

/** Verify OSCAR archive output independently with the JDK ZIP reader. */
class ZipTest {
  @TempDir Path temporary;

  @Test
  void singleStreamPreservesEntryNameAndBinaryBytes() throws Exception {
    Path archive = temporary.resolve("single.zip");
    byte[] payload = new byte[1024];
    for (int index = 0; index < payload.length; index++) {
      payload[index] = (byte) index;
    }
    Zip.ZipFile(archive.toFile(), new File("config.bin"), payload);
    assertArchive(archive, Map.of("config.bin", payload));
  }

  @Test
  void multipleStreamsPreserveNamesEmptyEntriesAndText() throws Exception {
    Map<File, byte[]> entries = new LinkedHashMap<>();
    entries.put(
        new File("configuration.txt"),
        "Synthetic configuration\n".getBytes(StandardCharsets.UTF_8));
    entries.put(new File("empty.bin"), new byte[0]);
    entries.put(new File("bytes.bin"), new byte[] {0, 1, (byte) 255});
    Path destination = temporary.resolve("multiple");
    Zip.ZipFile(destination.toFile(), entries);
    Map<String, byte[]> expected = new LinkedHashMap<>();
    entries.forEach((name, bytes) -> expected.put(name.toString(), bytes));
    assertArchive(temporary.resolve("multiple.zip"), expected);
  }

  @Test
  void appendsAndReplacesEntriesWithoutLosingOtherFiles() throws Exception {
    Path archive = temporary.resolve("update.zip");
    Zip.ZipFile(archive.toFile(), new File("first.bin"), new byte[] {1});
    Zip.ZipFile(archive.toFile(), new File("nested/café.txt"), new byte[] {2, 3});
    Zip.ZipFile(archive.toFile(), new File("first.bin"), new byte[] {4, 5});
    assertArchive(
        archive, Map.of("first.bin", new byte[] {4, 5}, "nested/café.txt", new byte[] {2, 3}));
  }

  @Test
  void emptyMapDoesNotCreateArchive() {
    Path destination = temporary.resolve("empty");
    Zip.ZipFile(destination.toFile(), Map.of());
    assertFalse(Files.exists(temporary.resolve("empty.zip")));
  }

  @Test
  void preservesStoredEntriesAndArchiveComment() throws Exception {
    Path archive = temporary.resolve("stored.zip");
    byte[] payload = new byte[] {1, 2, 3};
    var checksum = new java.util.zip.CRC32();
    checksum.update(payload);
    try (var output = new java.util.zip.ZipOutputStream(Files.newOutputStream(archive))) {
      output.setComment("Synthetic archive");
      var entry = new java.util.zip.ZipEntry("stored.bin");
      entry.setMethod(java.util.zip.ZipEntry.STORED);
      entry.setSize(payload.length);
      entry.setCrc(checksum.getValue());
      output.putNextEntry(entry);
      output.write(payload);
      output.closeEntry();
    }
    Zip.ZipFile(archive.toFile(), new File("new.bin"), new byte[] {4});
    assertArchive(archive, Map.of("stored.bin", payload, "new.bin", new byte[] {4}));
    try (var stored = new java.util.zip.ZipFile(archive.toFile())) {
      assertEquals("Synthetic archive", stored.getComment());
      assertEquals(java.util.zip.ZipEntry.STORED, stored.getEntry("stored.bin").getMethod());
    }
  }

  @Test
  void failedUpdatePreservesDestinationAndRemovesTemporaryFile() throws Exception {
    Path archive = temporary.resolve("invalid.zip");
    byte[] original = new byte[] {1, 2, 3};
    Files.write(archive, original);
    Zip.ZipFile(archive.toFile(), new File("new.bin"), new byte[] {4});
    assertArrayEquals(original, Files.readAllBytes(archive));
    try (var files = Files.list(temporary)) {
      assertEquals(1, files.count());
    }
  }

  @Test
  @EnabledIfSystemProperty(named = "oscar.zip.scale", matches = "true")
  void tenThousandEntryBaseline() throws Exception {
    final int entryCount = 10_000;
    Map<File, byte[]> entries = new LinkedHashMap<>();
    Map<String, byte[]> expected = new LinkedHashMap<>();
    for (int index = 0; index < entryCount; index++) {
      byte[] payload = new byte[256];
      for (int offset = 0; offset < payload.length; offset++) {
        payload[offset] = (byte) (index + offset);
      }
      String name = "config-" + index + ".bin";
      entries.put(new File(name), payload);
      expected.put(name, payload);
    }
    Zip.ZipFile(temporary.resolve("warmup").toFile(), Map.of(new File("warm.bin"), new byte[256]));
    var samples = new ArrayList<String>();
    samples.add("iteration,entries,elapsed_ns,archive_bytes");
    for (int iteration = 1; iteration <= 3; iteration++) {
      Path destination = temporary.resolve("sample-" + iteration);
      long started = System.nanoTime();
      Zip.ZipFile(destination.toFile(), entries);
      long elapsed = System.nanoTime() - started;
      Path archive = temporary.resolve("sample-" + iteration + ".zip");
      assertArchive(archive, expected);
      samples.add(iteration + "," + entryCount + "," + elapsed + "," + Files.size(archive));
      Files.delete(archive);
      assertFalse(Files.exists(archive));
    }
    Path reports = Files.createDirectories(Path.of("target/zip-baseline"));
    Files.write(Files.createTempFile(reports, "sample-", ".csv"), samples, StandardCharsets.UTF_8);
  }

  private static void assertArchive(Path archive, Map<String, byte[]> expected) throws Exception {
    try (var zip = new java.util.zip.ZipFile(archive.toFile())) {
      assertEquals(expected.size(), zip.size());
      for (var entry : expected.entrySet()) {
        var stored = zip.getEntry(entry.getKey());
        assertNotNull(stored, entry.getKey());
        try (var input = zip.getInputStream(stored)) {
          assertArrayEquals(entry.getValue(), input.readAllBytes(), entry.getKey());
        }
      }
    }
  }
}
