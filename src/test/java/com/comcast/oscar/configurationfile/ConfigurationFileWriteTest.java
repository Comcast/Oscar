/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.tlv.TlvBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigurationFileWriteTest {
  @TempDir Path temporary;

  private ConfigurationFile configuration() throws Exception {
    var builder = new TlvBuilder();
    builder.add(3, new byte[] {1});
    return new ConfigurationFile(ConfigurationFile.DOCSIS_VER_31, builder);
  }

  @Test
  void failedOpenReturnsFalseInsteadOfThrowing() throws Exception {
    var configuration = configuration();
    configuration.setConfigurationFileName(temporary.toFile());
    assertFalse(configuration.writeToDisk());
    configuration.setConfigurationFileName(temporary.resolve("missing/output.cm").toFile());
    assertFalse(configuration.writeToDisk());
  }

  @Test
  void writesExactBytesUsingEitherDestinationForm() throws Exception {
    var configuration = configuration();
    var destination = temporary.resolve("file.cm");
    configuration.setConfigurationFileName(destination.toString());
    assertTrue(configuration.writeToDisk());
    assertArrayEquals(configuration.toByteArray(), Files.readAllBytes(destination));
    configuration.setConfigurationFileName(destination.toFile());
    assertTrue(configuration.writeToDisk());
    assertArrayEquals(configuration.toByteArray(), Files.readAllBytes(destination));
  }

  @Test
  void writeFailureDoesNotReportSuccess() throws Exception {
    org.junit.jupiter.api.Assumptions.assumeTrue(Files.exists(Path.of("/dev/full")));
    var configuration = configuration();
    configuration.setConfigurationFileName(new java.io.File("/dev/full"));
    assertFalse(configuration.writeToDisk());
  }
}
