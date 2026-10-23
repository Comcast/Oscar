/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.validation;

import com.comcast.oscar.ber.BERService;
import com.comcast.oscar.utilities.TlvTextString;
import java.util.Optional;
import java.util.regex.Pattern;

/** Side-effect-free SNMP text checks; never resolves MIBs, DNS names, or encodes a binary. */
final class SnmpValueFormat {
  private static final int MAXIMUM_IPV4_OCTET = (1 << Byte.SIZE) - 1;
  private static final long UNSIGNED_32_MAX = (1L << Integer.SIZE) - 1;
  // Four decimal octets; no hostnames, DNS lookup, IPv6 or abbreviated IP forms.
  private static final Pattern IPV4 = Pattern.compile("[0-9]{1,3}(?:\\.[0-9]{1,3}){3}");
  // Existing HexString converter accepts these separators around complete byte pairs.
  private static final Pattern HEX_SEPARATORS = Pattern.compile("[\\[\\](){}: ]");
  private static final Pattern HEX_BYTES = Pattern.compile("(?:[0-9a-fA-F]{2})*");

  private SnmpValueFormat() {}

  static Optional<String> problem(String type, String quotedValue) {
    String value = TlvTextString.decode(quotedValue);
    return switch (type) {
      case BERService.INTEGER, BERService.INTEGER32 ->
          numeric(type, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
      case BERService.COUNTER,
              BERService.COUNTER32,
              BERService.GAUGE,
              BERService.GAUGE32,
              BERService.TIMETICKS ->
          numeric(type, value, 0, UNSIGNED_32_MAX);
      case BERService.IPADDRESS -> ipv4(value);
      case BERService.HEXSTRING ->
          HEX_BYTES.matcher(HEX_SEPARATORS.matcher(value).replaceAll("")).matches()
              ? Optional.empty()
              : Optional.of("HexString requires complete hexadecimal byte pairs.");
      case BERService.OCTETSTRING -> Optional.empty();
      default ->
          throw new IllegalStateException("Grammar SNMP type lacks a format validator: " + type);
    };
  }

  private static Optional<String> numeric(String type, String value, long minimum, long maximum) {
    try {
      long number = Long.decode(value);
      if (number >= minimum && number <= maximum) {
        return Optional.empty();
      }
    } catch (NumberFormatException invalidInput) {
      // User value, not a programming failure. Preserve the same decimal/hex/octal forms as
      // encoding.
    }
    return Optional.of(type + " requires a whole number from " + minimum + " to " + maximum + ".");
  }

  private static Optional<String> ipv4(String value) {
    if (IPV4.matcher(value).matches()) {
      boolean valid = true;
      for (String octet : value.split("\\.")) {
        if (Integer.parseInt(octet) > MAXIMUM_IPV4_OCTET) {
          valid = false;
        }
      }
      if (valid) {
        return Optional.empty();
      }
    }
    return Optional.of("IpAddress requires four decimal IPv4 octets from 0 to 255.");
  }
}
