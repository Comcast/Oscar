/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
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
