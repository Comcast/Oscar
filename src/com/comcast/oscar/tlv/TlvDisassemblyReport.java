/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import java.util.Arrays;
import java.util.List;

/** Ordered structural observations; this is not an editable or validated configuration. */
public final class TlvDisassemblyReport {
  /** Structural outcomes. OK does not mean that the value codec or MIC was validated. */
  public enum Status {
    OK,
    UNKNOWN_TYPE,
    TRUNCATED_HEADER,
    TRUNCATED_VALUE,
    INVALID_LENGTH,
    DEPTH_LIMIT,
    ENTRY_LIMIT,
    END,
    PADDING,
    TRAILING_DATA,
    EMPTY_INPUT
  }

  /**
   * One occurrence in wire order. Offsets are absolute; endOffset is exclusive. A declaredLength of
   * -1 means that a complete length field was unavailable. Unknown definitions use the selected
   * family's one-byte framing assumption.
   */
  public record Entry(
      List<Integer> tlvPath,
      int occurrence,
      String name,
      int headerOffset,
      int valueOffset,
      int declaredLength,
      int endOffset,
      Status status,
      List<Entry> children) {
    /**
     * Copies collections so results cannot be altered by callers.
     *
     * @param tlvPath numeric path, not an identity for repeated instances
     * @param occurrence one-based occurrence among siblings with the same type
     * @param name catalog name or Unknown
     * @param headerOffset absolute header offset
     * @param valueOffset absolute value start, or end of incomplete header
     * @param declaredLength declared bytes, or -1 for an incomplete header
     * @param endOffset end of available bytes, exclusive
     * @param status structural result
     * @param children nested occurrences in wire order
     */
    public Entry {
      tlvPath = List.copyOf(tlvPath);
      children = List.copyOf(children);
    }
  }

  private final byte[] originalBytes;
  private final List<Entry> entries;

  TlvDisassemblyReport(byte[] originalBytes, List<Entry> entries) {
    this.originalBytes = originalBytes;
    this.entries = List.copyOf(entries);
  }

  /**
   * Returns occurrences without exposing configuration values in their string representation.
   *
   * @return immutable ordered root entries
   */
  public List<Entry> entries() {
    return entries;
  }

  /**
   * Copies original bytes on explicit request; these may contain credentials.
   *
   * @param startOffset inclusive byte offset
   * @param endOffset exclusive byte offset
   * @return a defensive copy of the requested range
   * @throws IndexOutOfBoundsException if the range falls outside the original input
   */
  public byte[] copyBytes(int startOffset, int endOffset) {
    java.util.Objects.checkFromToIndex(startOffset, endOffset, originalBytes.length);
    return Arrays.copyOfRange(originalBytes, startOffset, endOffset);
  }

  /**
   * Reports whether every observed entry has known, structurally complete framing.
   *
   * @return false for unknown entries, damage, empty input, or resource limits; true does not
   *     establish value validity, file completeness, or MIC correctness
   */
  public boolean structurallyComplete() {
    return complete(entries);
  }

  private static boolean complete(List<Entry> entries) {
    return entries.stream()
        .allMatch(
            entry ->
                (entry.status() == Status.OK
                        || entry.status() == Status.END
                        || entry.status() == Status.PADDING)
                    && complete(entry.children()));
  }
}
