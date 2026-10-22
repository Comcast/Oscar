/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Creates and updates ordinary, unencrypted ZIP archives using OpenJDK. */
public class Zip {
  private static final Logger logger = LogManager.getLogger(Zip.class);

  /**
   * Adds or replaces one entry while retaining other entries in an existing archive.
   *
   * <p>For example, archive {@code configs.zip}, entry {@code modem.bin}, and bytes {@code {1, 2}}
   * produce an entry whose uncompressed contents are exactly those two bytes. I/O failures are
   * logged, matching this legacy void API; an existing archive is not replaced until the complete
   * new archive has been written. Inputs are not modified.
   *
   * @param archive destination archive, used as supplied without adding an extension
   * @param entryName entry path within the archive; path separators become forward slashes
   * @param contents uncompressed entry bytes
   * @throws NullPointerException if any argument is null
   */
  public static void ZipFile(File archive, File entryName, byte[] contents) {
    writeArchive(archive.toPath(), Map.of(entryName, contents));
  }

  /**
   * Adds or replaces several entries, appending {@code .zip} to the destination name.
   *
   * <p>For example, destination {@code configs} produces {@code configs.zip}. Existing entries not
   * supplied in the map remain in the archive. An empty map leaves the filesystem unchanged. I/O
   * failures are logged and an existing archive remains intact if writing fails. Entries use
   * DEFLATE compression without encryption. Inputs are not modified.
   *
   * @param archiveBase destination name before the appended {@code .zip} suffix
   * @param entries entry names and uncompressed contents, in the desired insertion order
   * @throws NullPointerException if the destination, map, or an entry name/value is null
   */
  public static void ZipFile(File archiveBase, Map<File, byte[]> entries) {
    writeArchive(Path.of(archiveBase.toString() + ".zip"), entries);
  }

  private static void writeArchive(Path destination, Map<File, byte[]> entries) {
    if (entries.isEmpty()) {
      return;
    }
    Map<String, byte[]> replacements = new LinkedHashMap<>();
    entries.forEach(
        (name, bytes) ->
            replacements.put(
                name.toString().replace('\\', '/'), java.util.Objects.requireNonNull(bytes)));
    Path archive = destination.toAbsolutePath();
    Path temporary = null;
    try {
      Files.createDirectories(archive.getParent());
      temporary = Files.createTempFile(archive.getParent(), "oscar-zip-", ".tmp");
      // Rewrite existing entries once, rather than reopening the archive for each new entry.
      try (var output = new ZipOutputStream(Files.newOutputStream(temporary))) {
        if (Files.exists(archive)) {
          copyExisting(archive, output, replacements);
        }
        for (var entry : replacements.entrySet()) {
          output.putNextEntry(new ZipEntry(entry.getKey()));
          output.write(entry.getValue());
          output.closeEntry();
        }
      }
      try {
        Files.move(
            temporary,
            archive,
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(temporary, archive, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      logger.error("Cannot write ZIP archive {}", archive, e);
    } finally {
      if (temporary != null) {
        try {
          Files.deleteIfExists(temporary);
        } catch (IOException e) {
          logger.error("Cannot remove temporary ZIP file {}", temporary, e);
        }
      }
    }
  }

  private static void copyExisting(
      Path archive, ZipOutputStream output, Map<String, byte[]> replacements) throws IOException {
    try (var existing = new java.util.zip.ZipFile(archive.toFile())) {
      output.setComment(existing.getComment());
      var entries = existing.entries();
      while (entries.hasMoreElements()) {
        var entry = entries.nextElement();
        if (!replacements.containsKey(entry.getName())) {
          var copy = new ZipEntry(entry);
          copy.setCompressedSize(-1); // DEFLATE may produce a different compressed size.
          output.putNextEntry(copy);
          try (var input = existing.getInputStream(entry)) {
            input.transferTo(output);
          }
          output.closeEntry();
        }
      }
    }
  }
}
