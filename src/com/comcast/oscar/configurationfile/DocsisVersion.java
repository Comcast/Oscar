/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.*;

/** DOCSIS protocol revisions, distinct from OSCAR release versions and other file families. */
public enum DocsisVersion {
  DOCSIS_1_0(DOCSIS_10_CONFIGURATION_TYPE),
  DOCSIS_1_1(DOCSIS_11_CONFIGURATION_TYPE),
  DOCSIS_2_0(DOCSIS_20_CONFIGURATION_TYPE),
  DOCSIS_3_0(DOCSIS_30_CONFIGURATION_TYPE),
  DOCSIS_3_1(DOCSIS_31_CONFIGURATION_TYPE),
  DOCSIS_4_0(DOCSIS_40_CONFIGURATION_TYPE);

  private final int configurationFileType;

  DocsisVersion(int configurationFileType) {
    this.configurationFileType = configurationFileType;
  }

  /**
   * Returns OSCAR's legacy configuration-file type code for integration with existing APIs. This is
   * not a wire TLV type or the enum ordinal. Does not change state.
   *
   * <p>Example: DOCSIS_3_1.configurationFileType() returns DOCSIS_31_CONFIGURATION_TYPE.
   *
   * @return the corresponding ConfigurationFileTypeConstants DOCSIS code
   */
  public int configurationFileType() {
    return configurationFileType;
  }

  /**
   * Converts an existing OSCAR configuration-file type code to a DOCSIS protocol revision. No file
   * is read or changed. Other families cannot be represented as a DocsisVersion.
   *
   * <p>Example: fromConfigurationFileType(DOCSIS_31_CONFIGURATION_TYPE) returns DOCSIS_3_1.
   *
   * @param configurationFileType a DOCSIS code from ConfigurationFileTypeConstants, not a release
   *     number
   * @return the matching DOCSIS revision
   * @throws IllegalArgumentException if the code is unknown or belongs to another file family
   */
  public static DocsisVersion fromConfigurationFileType(int configurationFileType) {
    for (DocsisVersion docsisVersion : values()) {
      if (docsisVersion.configurationFileType == configurationFileType) return docsisVersion;
    }
    throw new IllegalArgumentException(
        "Not a DOCSIS configuration-file type: " + configurationFileType);
  }
}
