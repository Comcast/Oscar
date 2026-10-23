/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.validation;

import java.util.List;

/**
 * Immutable formatting observations. A clean report is not proof of compilability or device
 * acceptance.
 */
public record TextLintReport(
    List<Diagnostic> diagnostics, List<Statement> statements, boolean fullyScanned) {
  /** Stable categories for CLI and editor integrations; messages are not machine identifiers. */
  public enum Code {
    SYNTAX,
    MISSING_SEMICOLON,
    UNTERMINATED_STRING,
    UNTERMINATED_COMMENT,
    UNMATCHED_BRACE,
    SNMP_VALUE,
    LIMIT
  }

  /** Severity is explicit so editor clients need not infer it from prose. */
  public enum Severity {
    ERROR
  }

  /**
   * Accepted means formatting checked, not compiled. Skipped statements must not be treated as
   * valid.
   */
  public enum Status {
    ACCEPTED,
    SKIPPED
  }

  /**
   * Original-source location: offsets are zero-based UTF-16, end exclusive; lines and UTF-16
   * columns are one-based. CRLF counts as one line break. An insertion point has equal offsets.
   *
   * @param startOffset first UTF-16 offset
   * @param endOffset exclusive UTF-16 offset
   * @param line starting line
   * @param column starting UTF-16 column
   * @param endLine ending line
   * @param endColumn ending UTF-16 column
   */
  public record SourceRange(
      int startOffset, int endOffset, int line, int column, int endLine, int endColumn) {}

  /**
   * One input diagnostic. Names are a lexical TLV ancestry, not resolved numeric registry paths.
   * Messages omit configuration values; the caller can highlight its own original text by range.
   *
   * @param severity diagnostic severity
   * @param code stable diagnostic category
   * @param message human-readable expectation, without raw values
   * @param range original-source location
   * @param tlvNames lexical container/leaf names; offsets distinguish repeated occurrences
   */
  public record Diagnostic(
      Severity severity, Code code, String message, SourceRange range, List<String> tlvNames) {
    /**
     * Copies the ancestry to keep diagnostics immutable.
     *
     * @param severity diagnostic severity
     * @param code category
     * @param message explanation
     * @param range original-source range
     * @param tlvNames lexical ancestry
     */
    public Diagnostic {
      tlvNames = List.copyOf(tlvNames);
    }
  }

  /**
   * One leaf occurrence checked or skipped; no configuration values or partial binary are retained.
   *
   * @param tlvNames lexical ancestry
   * @param range complete statement range, including its semicolon when present
   * @param status formatting outcome
   */
  public record Statement(List<String> tlvNames, SourceRange range, Status status) {
    /**
     * Copies the ancestry for immutable editor results.
     *
     * @param tlvNames lexical ancestry
     * @param range statement range
     * @param status formatting outcome
     */
    public Statement {
      tlvNames = List.copyOf(tlvNames);
    }
  }

  /**
   * Copies findings for safe sharing with asynchronous UI consumers.
   *
   * @param diagnostics ordered input diagnostics
   * @param statements leaf occurrences, including rejected ones
   * @param fullyScanned false when a limit or invalid wrapper prevented further checking
   */
  public TextLintReport {
    diagnostics = List.copyOf(diagnostics);
    statements = List.copyOf(statements);
  }

  /**
   * Reports whether formatting errors occurred; false does not validate registry rules or
   * compilation.
   *
   * @return true when any error diagnostic exists
   */
  public boolean hasErrors() {
    return !diagnostics.isEmpty();
  }
}
