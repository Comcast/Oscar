/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes;
import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import java.util.List;
import java.util.Objects;

/** Version-specific structural checks for standalone PacketCable fragments, not MIB conformance. */
public final class PacketCablePayloadValidation {
  private static final int OUI_SUBOPTION = DocsisTlvTypes.VENDOR_ID_ENC;
  private static final int ASCII_OUI_BYTES = 6;

  private PacketCablePayloadValidation() {}

  /**
   * Checks bounded top-level fields and the PacketCable 2.0 vendor OUI representation. For example,
   * type 43 payload 08:06:30:30:30:35:43:41 uses ASCII OUI 0005CA. Does not require start/end
   * markers, interpret vendor data, authenticate a file, or check mandatory MIB objects. Legacy 1.0
   * retains compatibility behavior.
   *
   * @param encoded non-null encoded standalone TLV fragment; never modified
   * @param version non-null explicit protocol edition; cannot be inferred from bytes
   * @throws IllegalArgumentException for unknown top-level fields, truncation, or an invalid 2.0
   *     OUI
   * @throws NullPointerException if an argument is null
   */
  public static void validate(byte[] encoded, PacketCableVersion version) {
    Objects.requireNonNull(encoded, "encoded");
    Objects.requireNonNull(version, "version");
    var registry = TlvRegistry.forPacketCable(version);
    for (int offset = 0; offset < encoded.length; ) {
      int type = Byte.toUnsignedInt(encoded[offset]);
      var definition = registry.find(List.of(type));
      if (definition == null) {
        throw new IllegalArgumentException("Unknown PacketCable TLV type: " + type);
      }
      int headerBytes = TlvBuilder.TLV_TYPE_OVERHEAD + definition.lengthBytes();
      if (encoded.length - offset < headerBytes) {
        throw new IllegalArgumentException("Truncated PacketCable TLV header");
      }
      try {
        int length = TlvBuilder.getTlvLength(encoded, offset, definition.lengthBytes());
        if (length > encoded.length - offset - headerBytes) {
          throw new IllegalArgumentException("Truncated PacketCable TLV payload");
        }
        if (type == DocsisTlvTypes.DOC_EXT_FIELD && version == PacketCableVersion.PACKETCABLE_2_0) {
          validateAsciiOui(encoded, offset + headerBytes, length);
        }
        offset += headerBytes + length;
      } catch (TlvException exception) {
        throw new IllegalArgumentException("Invalid PacketCable TLV framing", exception);
      }
    }
  }

  private static void validateAsciiOui(byte[] encoded, int start, int length) {
    int end = start + length;
    for (int offset = start; offset < end; ) {
      if (end - offset < TlvBuilder.TLV_TYPE_LENGTH_OVERHEAD) {
        throw new IllegalArgumentException("Truncated vendor sub-option header");
      }
      int type = Byte.toUnsignedInt(encoded[offset]);
      int size = Byte.toUnsignedInt(encoded[offset + 1]);
      offset += TlvBuilder.TLV_TYPE_LENGTH_OVERHEAD;
      if (size > end - offset) {
        throw new IllegalArgumentException("Truncated vendor sub-option payload");
      }
      if (type == OUI_SUBOPTION) {
        if (size != ASCII_OUI_BYTES) {
          throw new IllegalArgumentException(
              "PacketCable 2.0 vendor OUI must contain six ASCII hex characters");
        }
        // OUI syntax is a protocol rule. Hex conversion remains owned by HexString/HexFormat.
        String oui = new String(encoded, offset, size, java.nio.charset.StandardCharsets.US_ASCII);
        java.util.HexFormat.of().parseHex(oui);
      }
      offset += size;
    }
  }
}
