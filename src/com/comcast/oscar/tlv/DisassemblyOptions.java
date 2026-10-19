package com.comcast.oscar.tlv;

import java.util.Objects;

/** Explicit framing and resource limits for read-only TLV diagnostics. */
public record DisassemblyOptions(
    Family family, int maxBytes, int maxDepth, int maxEntries, boolean stopAtFirstFailure) {
  /** Families with supported raw TLV-stream framing; no envelope autodetection is attempted. */
  public enum Family {
    DOCSIS,
    DPOE
  }

  /**
   * Creates limits for one diagnostic operation.
   *
   * @param family raw stream family, never null
   * @param maxBytes maximum input size in bytes
   * @param maxDepth maximum nesting depth, from one through 64
   * @param maxEntries maximum total entries, including markers
   * @param stopAtFirstFailure stop the entire traversal on structural errors or limits
   * @throws IllegalArgumentException if a limit is invalid
   */
  public DisassemblyOptions {
    Objects.requireNonNull(family, "family");
    if (maxBytes < 1 || maxDepth < 1 || maxDepth > 64 || maxEntries < 1) {
      throw new IllegalArgumentException("Invalid diagnostic limits");
    }
  }

  /**
   * Creates limits with recovery after bounded child failures enabled.
   *
   * @param family raw stream family
   * @param maxBytes maximum input bytes
   * @param maxDepth maximum nesting depth
   * @param maxEntries maximum entry count
   */
  public DisassemblyOptions(Family family, int maxBytes, int maxDepth, int maxEntries) {
    this(family, maxBytes, maxDepth, maxEntries, false);
  }

  /**
   * Returns conservative defaults for a raw DOCSIS TLV stream.
   *
   * @return limits of one MiB, 32 levels, and 10000 entries
   */
  public static DisassemblyOptions docsis() {
    return new DisassemblyOptions(Family.DOCSIS, 1024 * 1024, 32, 10000);
  }
}
