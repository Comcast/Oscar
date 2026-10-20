/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

/** Quoting for configuration strings; punctuation and whitespace remain value bytes. */
public final class TlvTextString {
  private TlvTextString() {}

  /** Decode a quoted token, for example {@code "https://acs.test/"}, without its delimiters. */
  public static String decode(String token) {
    if (!token.startsWith("\"")) return token;
    StringBuilder result = new StringBuilder();
    for (int i = 1; i < token.length() - 1; i++) {
      char c = token.charAt(i);
      if (c == '\\') {
        c = token.charAt(++i);
        c =
            switch (c) {
              case 'n' -> '\n';
              case 'r' -> '\r';
              case 't' -> '\t';
              case '"', '\\' -> c;
              default -> throw new IllegalArgumentException("Unsupported string escape");
            };
      }
      result.append(c);
    }
    return result.toString();
  }

  /** Encode a string with quotes and reversible escapes; URLs remain literal inside quotes. */
  public static String quote(String value) {
    return "\""
        + value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        + "\"";
  }

  /** Remove block comments outside quoted values without modifying literal string contents. */
  public static String withoutComments(String text) {
    StringBuilder result = new StringBuilder();
    boolean quoted = false;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (quoted && c == '\\' && i + 1 < text.length()) {
        result.append(c).append(text.charAt(++i));
      } else if (c == '"') {
        quoted = !quoted;
        result.append(c);
      } else if (!quoted && c == '/' && i + 1 < text.length() && text.charAt(i + 1) == '*') {
        int end = text.indexOf("*/", i + 2);
        if (end < 0) throw new IllegalArgumentException("Unterminated configuration comment");
        result.append(' ');
        i = end + 1;
      } else {
        result.append(c);
      }
    }
    return result.toString();
  }

  private static boolean isPlainValue(String text) {
    if (!text.matches("[a-zA-Z0-9_'$.-]+")) return false;
    var lexer =
        new com.comcast.oscar.parser.tlvLexer(new org.antlr.v4.runtime.ANTLRInputStream(text));
    int type = lexer.nextToken().getType();
    return (type == com.comcast.oscar.parser.tlvLexer.IDENTIFIER
            || type == com.comcast.oscar.parser.tlvLexer.INT)
        && lexer.nextToken().getType() == org.antlr.v4.runtime.Token.EOF;
  }

  /** Retain legacy plain identifiers; quote other string values, including empty strings. */
  public static String format(Object value, String codec) {
    String text = value.toString();
    if ((codec.equals("string") || codec.equals("string_null_terminated")) && !isPlainValue(text))
      return quote(text);
    if (codec.equals("byte_array") && (text.isEmpty() || text.matches("[A-Fa-f]{2}")))
      return quote(text);
    return text;
  }
}
