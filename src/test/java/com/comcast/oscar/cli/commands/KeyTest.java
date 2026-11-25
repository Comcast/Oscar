/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cli.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KeyTest {
  @TempDir Path directory;

  @Test
  void defaultsToNoKeyInsteadOfAPlaceholderSecret() {
    assertEquals("", new Key().getKey());
  }

  @Test
  void missingKeyFileIsRejected() {
    Key key = new Key();

    IllegalArgumentException error =
        assertThrows(
            IllegalArgumentException.class,
            () -> key.setKey(new String[] {directory.resolve("missing.key").toString()}));

    assertTrue(error.getMessage().contains("not a regular file"));
    assertEquals("", key.getKey());
  }

  @Test
  void directoryPathIsRejected() {
    Key key = new Key();

    IllegalArgumentException error =
        assertThrows(
            IllegalArgumentException.class, () -> key.setKey(new String[] {directory.toString()}));

    assertTrue(error.getMessage().contains("not a regular file"));
    assertEquals("", key.getKey());
  }

  @Test
  void emptyKeyFileIsRejected() throws Exception {
    Path keyFile = directory.resolve("empty.key");
    Files.write(keyFile, new byte[0]);
    Key key = new Key();

    IllegalArgumentException error =
        assertThrows(
            IllegalArgumentException.class, () -> key.setKey(new String[] {keyFile.toString()}));

    assertTrue(error.getMessage().contains("empty"));
    assertEquals("", key.getKey());
  }

  @Test
  void missingPathArgumentIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new Key().setKey(null));
    assertThrows(IllegalArgumentException.class, () -> new Key().setKey(new String[0]));
    assertThrows(IllegalArgumentException.class, () -> new Key().setKey(new String[] {""}));
  }

  @Test
  void regularKeyFileLoadsSharedSecret() throws Exception {
    Path keyFile = directory.resolve("shared-secret.key");
    Files.write(keyFile, "SHAREDSECRET".getBytes(StandardCharsets.US_ASCII));
    Key key = new Key();

    key.setKey(new String[] {keyFile.toString()});

    assertEquals("SHAREDSECRET", key.getKey());
  }

  @Test
  void failedReloadDoesNotRetainPreviousSecret() throws Exception {
    Path keyFile = directory.resolve("shared-secret.key");
    Files.write(keyFile, "SHAREDSECRET".getBytes(StandardCharsets.US_ASCII));
    Key key = new Key();
    key.setKey(new String[] {keyFile.toString()});

    assertThrows(
        IllegalArgumentException.class,
        () -> key.setKey(new String[] {directory.resolve("missing.key").toString()}));

    assertEquals("", key.getKey());
  }
}