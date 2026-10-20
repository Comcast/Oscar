/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.constants;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;

class BuildInfoTest {
  @Test
  void unreadableMetadataFailsAndClosesTheStream() {
    var closed = new java.util.concurrent.atomic.AtomicBoolean();
    var input =
        new java.io.InputStream() {
          @Override
          public int read() throws java.io.IOException {
            throw new java.io.IOException("synthetic read failure");
          }

          @Override
          public void close() {
            closed.set(true);
          }
        };
    assertThrows(IllegalStateException.class, () -> BuildInfo.readVersion(input));
    assertTrue(closed.get());
  }

  @Test
  void runtimeAndBannerUseMavenVersion() throws Exception {
    var root =
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(Path.of("pom.xml").toFile())
            .getDocumentElement();
    String expected = root.getElementsByTagName("version").item(0).getTextContent().trim();
    assertEquals(expected, BuildInfo.version());
    assertEquals(expected, Constants.OSCAR_VERSION);
    assertTrue(Constants.OSCAR_BANNER.endsWith("v" + expected));
  }

  @Test
  void missingOrUnfilteredMetadataIsAnError() {
    assertThrows(IllegalStateException.class, () -> BuildInfo.readVersion(null));
    for (String text : new String[] {"", "version=", "version=${project.version}"}) {
      assertThrows(
          IllegalStateException.class,
          () ->
              BuildInfo.readVersion(
                  new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
    }
  }
}
