/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Runtime metadata generated from Maven's project version; no independent version literal. */
public final class BuildInfo {
  private static final String VERSION =
      readVersion(BuildInfo.class.getResourceAsStream("/oscar-build.properties"));

  private BuildInfo() {}

  /**
   * Returns the version generated from pom.xml during Maven resource processing. This reads
   * packaged metadata, not files in the caller's working directory.
   *
   * @return nonempty Maven project version, for example a MAJOR.MINOR.PATCH release
   * @throws ExceptionInInitializerError if build metadata is absent, unfiltered or unreadable
   */
  public static String version() {
    return VERSION;
  }

  static String readVersion(InputStream input) {
    if (input == null)
      throw new IllegalStateException(
          "Missing OSCAR build metadata; run Maven process-resources or package");
    try (input) {
      Properties properties = new Properties();
      properties.load(input);
      String version = properties.getProperty("version", "").trim();
      if (version.isEmpty() || version.contains("${"))
        throw new IllegalStateException(
            "Invalid OSCAR build metadata; run Maven process-resources or package");
      return version;
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read OSCAR build metadata", e);
    }
  }
}
