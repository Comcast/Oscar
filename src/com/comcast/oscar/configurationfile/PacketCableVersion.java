/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.*;

/** Standalone PacketCable protocol selection; binary framing alone cannot identify the edition. */
public enum PacketCableVersion {
  LEGACY_1_0(PKT_CABLE_10_CONFIGURATION_TYPE),
  PACKETCABLE_1_5(PKT_CABLE_15_CONFIGURATION_TYPE),
  PACKETCABLE_2_0(PKT_CABLE_20_CONFIGURATION_TYPE);

  private final int configurationFileType;

  PacketCableVersion(int configurationFileType) {
    this.configurationFileType = configurationFileType;
  }

  /**
   * Returns the existing OSCAR file-type code without modifying state.
   *
   * @return ConfigurationFileTypeConstants code, not a wire identifier or enum ordinal
   */
  public int configurationFileType() {
    return configurationFileType;
  }

  /**
   * Converts a legacy API file-type code to a protocol selection without reading files. For
   * example, PKT_CABLE_15_CONFIGURATION_TYPE selects PACKETCABLE_1_5.
   *
   * @param configurationFileType existing OSCAR file-type code
   * @return matching PacketCable protocol selection
   * @throws IllegalArgumentException for other families or unknown codes
   */
  public static PacketCableVersion fromConfigurationFileType(int configurationFileType) {
    for (var version : values()) {
      if (version.configurationFileType == configurationFileType) {
        return version;
      }
    }
    throw new IllegalArgumentException("Not a PacketCable file type: " + configurationFileType);
  }
}
