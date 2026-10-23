/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.validation;

/** Bounded work for potentially malformed editor buffers; limits count UTF-16 units. */
public record TextLintOptions(
    int maxCharacters, int maxStatementCharacters, int maxDepth, int maxDiagnostics) {
  private static final int MAXIMUM_DEPTH = 64;
  private static final int DEFAULT_CHARACTERS = 1024 * 1024;
  private static final int DEFAULT_STATEMENT_CHARACTERS = 65536;
  private static final int DEFAULT_DEPTH = 32;
  private static final int DEFAULT_DIAGNOSTICS = 256;

  /**
   * Validates limits before parsing. No input is read or mutated.
   *
   * @param maxCharacters maximum input UTF-16 length
   * @param maxStatementCharacters maximum single-statement UTF-16 length
   * @param maxDepth maximum container depth, including the root, at most 64
   * @param maxDiagnostics maximum findings, including a terminal LIMIT diagnostic
   * @throws IllegalArgumentException if a limit is nonpositive or depth exceeds 64
   */
  public TextLintOptions {
    if (maxCharacters < 1
        || maxStatementCharacters < 1
        || maxDepth < 1
        || maxDepth > MAXIMUM_DEPTH
        || maxDiagnostics < 1) {
      throw new IllegalArgumentException("Invalid text lint limits");
    }
  }

  /**
   * Returns conservative editor defaults: one MiB of UTF-16 units, 64 KiB statements, 32 nesting
   * levels and 256 diagnostics.
   *
   * @return immutable default limits
   */
  public static TextLintOptions defaults() {
    return new TextLintOptions(
        DEFAULT_CHARACTERS, DEFAULT_STATEMENT_CHARACTERS, DEFAULT_DEPTH, DEFAULT_DIAGNOSTICS);
  }
}
