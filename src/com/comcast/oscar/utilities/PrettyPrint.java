/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/** Formats configuration syntax while preserving quoted values and statement order. */
public class PrettyPrint extends StringTokenizer {

  private static final String END_OF_CODE_BLOCK = "\\*EOCB*\\";

  /** Legacy default wrapping width; must be positive and non-null. */
  public static Integer PARAGRAPH_OFFSET = 80;

  private final String formattedCode;

  /**
   * Formats configuration text without changing quoted values or statement order. Empty
   * exporter-marked containers are removed at any nesting depth. Unmarked empty containers remain
   * intact. Indentation uses tabs and line endings use LF.
   *
   * <pre>
   * Input:  Docsis { NetworkAccess 1; }
   * Output: Docsis {
   *             NetworkAccess 1;
   *         }
   * </pre>
   *
   * @param inputCode non-null configuration text, optionally containing exporter markers
   * @throws IllegalArgumentException for unbalanced braces or unterminated strings/comments
   * @throws NullPointerException if inputCode is null
   */
  public PrettyPrint(String inputCode) {
    // Preserve the public superclass and its historically exhausted token stream.
    super("");
    formattedCode = render(removeEmptyContainers(scan(Objects.requireNonNull(inputCode))));
  }

  /**
   * Returns the formatted configuration, ending in LF when nonempty. For example, {@code
   * NetworkAccess 1;} produces {@code "NetworkAccess 1;\n"}.
   *
   * @return formatted text; an empty input produces an empty string
   */
  @Override
  public String toString() {
    return formattedCode;
  }

  /**
   * Wraps prose at whitespace using the legacy default width, without splitting words. For a width
   * of 7, {@code "one two three"} produces {@code "one two\nthree"}.
   *
   * @param text non-null prose; whitespace is normalized to single spaces
   * @return wrapped prose without leading/trailing whitespace or a trailing newline
   * @throws IllegalArgumentException if the default width is null or nonpositive
   * @throws NullPointerException if text is null
   */
  public static String ToParagraphForm(String text) {
    Integer width = PARAGRAPH_OFFSET;
    if (width == null) {
      throw new IllegalArgumentException("Paragraph width must be positive.");
    }
    return toParagraphForm(text, width);
  }

  /**
   * Wraps prose in one forward pass. Words longer than the width remain whole. For example, width 3
   * and {@code "a bb ccc"} produce {@code "a\nbb\nccc"}.
   *
   * @param text non-null prose
   * @param width positive maximum line width, except for indivisible long words
   * @return normalized, wrapped text, or an empty string for whitespace-only input
   * @throws IllegalArgumentException if width is nonpositive
   * @throws NullPointerException if text is null
   */
  public static String toParagraphForm(String text, int width) {
    Objects.requireNonNull(text);
    if (width <= 0) {
      throw new IllegalArgumentException("Paragraph width must be positive.");
    }
    StringBuilder output = new StringBuilder(text.length());
    int column = 0;
    for (int offset = 0; offset < text.length(); ) {
      if (Character.isWhitespace(text.charAt(offset))) {
        offset++;
        continue;
      }
      int start = offset;
      while (offset < text.length() && !Character.isWhitespace(text.charAt(offset))) {
        offset++;
      }
      int wordLength = offset - start;
      if (column > 0) {
        if ((long) column + 1 + wordLength > width) {
          output.append('\n');
          column = 0;
        } else {
          output.append(' ');
          column++;
        }
      }
      output.append(text, start, offset);
      column += wordLength;
    }
    return output.toString();
  }

  private enum Kind {
    WORD,
    QUOTED,
    COMMENT,
    OPEN,
    CLOSE,
    SEMICOLON,
    MARKER
  }

  private record Token(Kind kind, String text) {}

  /**
   * Scans syntax without interpreting literal contents: {@code "a;b"} stays one QUOTED token.
   *
   * @param source non-null configuration text
   * @return ordered tokens with literal and comment contents unchanged
   * @throws IllegalArgumentException for unterminated strings or block comments
   */
  private static List<Token> scan(String source) {
    var tokens = new ArrayList<Token>();
    for (int offset = 0; offset < source.length(); ) {
      char character = source.charAt(offset);
      if (Character.isWhitespace(character)) {
        offset++;
        continue;
      }
      int start = offset;
      Kind kind;
      if (source.startsWith(END_OF_CODE_BLOCK, offset)) {
        kind = Kind.MARKER;
        offset += END_OF_CODE_BLOCK.length();
      } else if (source.startsWith("/*", offset)) {
        kind = Kind.COMMENT;
        int closing = source.indexOf("*/", offset + 2);
        if (closing < 0) {
          throw new IllegalArgumentException("Unterminated configuration comment.");
        }
        offset = closing + 2;
      } else if (character == '"') {
        kind = Kind.QUOTED;
        offset++;
        boolean closed = false;
        while (offset < source.length()) {
          char quotedCharacter = source.charAt(offset++);
          if (quotedCharacter == '\\' && offset < source.length()) {
            offset++;
          } else if (quotedCharacter == '"') {
            closed = true;
            break;
          }
        }
        if (!closed) {
          throw new IllegalArgumentException("Unterminated configuration string.");
        }
      } else if (character == '{' || character == '}' || character == ';') {
        kind = character == '{' ? Kind.OPEN : character == '}' ? Kind.CLOSE : Kind.SEMICOLON;
        offset++;
      } else {
        kind = Kind.WORD;
        while (offset < source.length()
            && !Character.isWhitespace(source.charAt(offset))
            && "{};\"".indexOf(source.charAt(offset)) < 0
            && !source.startsWith("/*", offset)
            && !source.startsWith(END_OF_CODE_BLOCK, offset)) {
          offset++;
        }
      }
      tokens.add(new Token(kind, source.substring(start, offset)));
    }
    return tokens;
  }

  /**
   * Prunes exporter-marked empty subtrees iteratively; {@code A {} marker} produces no tokens.
   * Unmarked containers and comments are retained; no recursion or depth cutoff is used.
   *
   * @param tokens ordered scanner output
   * @return retained tokens in source order, without exporter markers
   * @throws IllegalArgumentException for unmatched braces
   */
  private static List<Token> removeEmptyContainers(List<Token> tokens) {
    var result = new ArrayList<Token>();
    var openingIndexes = new ArrayDeque<Integer>();
    for (int index = 0; index < tokens.size(); index++) {
      Token token = tokens.get(index);
      if (token.kind() == Kind.OPEN) {
        openingIndexes.push(result.size());
      } else if (token.kind() == Kind.CLOSE) {
        if (openingIndexes.isEmpty()) {
          throw new IllegalArgumentException("Unbalanced configuration braces.");
        }
        int opening = openingIndexes.pop();
        boolean marked = index + 1 < tokens.size() && tokens.get(index + 1).kind() == Kind.MARKER;
        if (marked
            && result.size() == opening + 1
            && opening > 0
            && result.get(opening - 1).kind() == Kind.WORD) {
          result.subList(opening - 1, result.size()).clear();
          index++;
          continue;
        }
      } else if (token.kind() == Kind.MARKER) {
        continue;
      }
      result.add(token);
    }
    if (!openingIndexes.isEmpty()) {
      throw new IllegalArgumentException("Unbalanced configuration braces.");
    }
    return result;
  }

  /**
   * Emits tab-indented statements; input A containing B 1 produces {@code "A {\n\tB 1;\n}\n"}.
   * Comments immediately after a statement remain on that statement's line.
   *
   * @param tokens balanced tokens with exporter markers removed
   * @return formatted text ending in LF, or an empty string for no tokens
   */
  private static String render(List<Token> tokens) {
    StringBuilder output = new StringBuilder();
    int depth = 0;
    boolean lineStart = true;
    boolean statementEnded = false;
    for (Token token : tokens) {
      if (statementEnded && token.kind() != Kind.COMMENT) {
        output.append('\n');
        lineStart = true;
      }
      if (token.kind() == Kind.CLOSE) {
        depth--;
        if (!lineStart) {
          output.append('\n');
          lineStart = true;
        }
      }
      if (lineStart) {
        output.append("\t".repeat(depth));
      } else if (token.kind() != Kind.SEMICOLON) {
        output.append(' ');
      }
      output.append(token.text());
      lineStart = false;
      statementEnded = token.kind() == Kind.SEMICOLON || token.kind() == Kind.CLOSE;
      if (token.kind() == Kind.OPEN || token.kind() == Kind.COMMENT) {
        output.append('\n');
        lineStart = true;
        statementEnded = false;
        if (token.kind() == Kind.OPEN) {
          depth++;
        }
      }
    }
    if (!lineStart) {
      output.append('\n');
    }
    return output.toString();
  }
}
