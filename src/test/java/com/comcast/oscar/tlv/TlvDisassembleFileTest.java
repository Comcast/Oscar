/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TlvDisassembleFileTest {
  @TempDir Path directory;

  @Test
  void rejectsUnreadableFilesWithoutCreatingAPartialDecoder() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new TlvDisassemble(
                directory.resolve("missing.cfg").toFile(), TlvDisassemble.TLV_TYPE_DOCSIS));
  }

  @Test
  void readsACompleteFileWithoutChangingItsBytes() throws Exception {
    byte[] bytes = {3, 1, 1};
    Path file = Files.write(directory.resolve("input.cfg"), bytes);
    var decoder = new TlvDisassemble(file.toFile(), TlvDisassemble.TLV_TYPE_DOCSIS);
    assertEquals(1, decoder.getTopLevelTLVJSON(3).length());
    assertArrayEquals(bytes, Files.readAllBytes(file));
  }
}
