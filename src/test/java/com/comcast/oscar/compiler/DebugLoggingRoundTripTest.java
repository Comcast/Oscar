/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Logging must not affect configuration bytes or binary/text round trips. */
class DebugLoggingRoundTripTest {
  @ParameterizedTest
  @ValueSource(strings = {"3.1", "4.0"})
  void sameBinaryWithDebugEnabledAndDisabled(String revision) throws Exception {
    String text = Files.readString(Path.of("examples/docsis", revision, "erouter/tr069-lab.txt"));
    String loggerName = "com.comcast.oscar";
    Level previous = LogManager.getLogger(loggerName).getLevel();
    byte[] expected = null;
    try {
      for (Level level : new Level[] {Level.OFF, Level.DEBUG}) {
        Configurator.setLevel(loggerName, level);
        var imported = new ConfigurationFileImport(new StringBuilder(text));
        var file =
            new ConfigurationFile(
                imported.getConfigurationFileType(), imported.getTlvBuilder(), "SHAREDSECRET");
        file.commit();
        if (expected == null) expected = file.toByteArray().clone();
        assertArrayEquals(expected, file.toByteArray());
        var decoded =
            new ConfigurationFileImport(
                new StringBuilder(new ConfigurationFileExport(file).toPrettyPrint(false)));
        var rebuilt =
            new ConfigurationFile(
                decoded.getConfigurationFileType(), decoded.getTlvBuilder(), "SHAREDSECRET");
        rebuilt.commit();
        assertArrayEquals(expected, rebuilt.toByteArray());
      }
    } finally {
      Configurator.setLevel(loggerName, previous);
    }
  }
}
