/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.validation;

import static com.comcast.oscar.validation.TextLintReport.*;

import com.comcast.oscar.parser.tlvLexer;
import com.comcast.oscar.parser.tlvParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

/** Recoverable formatting lint. It does not compile, resolve MIBs, or modify the supplied text. */
public final class TextLinter {
  private TextLinter() {}

  /**
   * Checks an editor buffer using bounded defaults, continuing after recoverable TLV errors.
   * Unknown Java failures propagate; only expected user syntax/value errors become diagnostics.
   * Example: Integer32 "abc" followed by NetworkAccess 1 yields a skipped SNMP statement and an
   * accepted NetworkAccess statement. No output binary is produced.
   *
   * @param text complete configuration text with its family wrapper; never null
   * @return immutable diagnostics and leaf outcomes in original-source coordinates
   * @throws NullPointerException if text is null
   */
  public static TextLintReport lint(String text) {
    return lint(text, TextLintOptions.defaults());
  }

  /**
   * Checks syntax and every grammar-supported SNMP value type with explicit resource limits.
   * Recovery uses semicolons, braces, newline after an unterminated quote, and the beginning of a
   * new statement after an otherwise complete statement missing its semicolon. Ambiguous damaged
   * input can cause a larger region to be skipped. Diagnostics never include raw values. This is
   * not registry, minimum-TLV, MIB-specific value, length, or MIC validation.
   *
   * @param text original buffer, unchanged
   * @param options positive work limits
   * @return report; fullyScanned is false if limits or an invalid wrapper prevent further checking
   * @throws NullPointerException if either argument is null
   */
  public static TextLintReport lint(String text, TextLintOptions options) {
    Objects.requireNonNull(text, "text");
    Objects.requireNonNull(options, "options");
    return new Scan(text, options).run();
  }

  private enum Kind {
    WORD,
    STRING,
    BROKEN_STRING,
    OPEN,
    CLOSE,
    SEMICOLON
  }

  private record Lexeme(Kind kind, int start, int end) {}

  /** Internal control flow only for expected ANTLR input errors, never a catch-all. */
  private static final class SyntaxFailure extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final int offset;

    SyntaxFailure(int offset) {
      this.offset = offset;
    }
  }

  private static final class InputErrors extends BaseErrorListener {
    private boolean failed;
    private int offset;

    /**
     * Collects a syntax failure while allowing ANTLR default recovery; never prints source values.
     *
     * @param recognizer lexer or parser reporting the error
     * @param offendingSymbol token, when available
     * @param line ANTLR source line
     * @param position ANTLR column
     * @param message ANTLR diagnostic, intentionally not echoed because it may contain secrets
     * @param cause recognition failure, when available
     */
    @Override
    public void syntaxError(
        Recognizer<?, ?> recognizer,
        Object offendingSymbol,
        int line,
        int position,
        String message,
        RecognitionException cause) {
      if (!failed) {
        offset =
            offendingSymbol instanceof Token token
                ? token.getStartIndex()
                : ((org.antlr.v4.runtime.Lexer) recognizer)._tokenStartCharIndex;
      }
      failed = true;
    }
  }

  private static tlvParser parser(String text) {
    var lexer = new tlvLexer(CharStreams.fromString(text));
    lexer.removeErrorListeners();
    var errors = new InputErrors();
    lexer.addErrorListener(errors);
    var parser = new tlvParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(errors);
    return parser;
  }

  private static tlvParser.StartTLVContext statement(String text) {
    var parser = parser(text);
    var parsed = parser.startTLV();
    if (parser.getNumberOfSyntaxErrors() != 0
        || parser.getErrorListeners().stream()
            .anyMatch(listener -> listener instanceof InputErrors errors && errors.failed)
        || parser.getInputStream().LA(1) != Token.EOF) {
      var errors = (InputErrors) parser.getErrorListeners().getFirst();
      int codePoint = errors.failed ? errors.offset : parser.getCurrentToken().getStartIndex();
      throw new SyntaxFailure(
          text.offsetByCodePoints(
              0, Math.max(0, Math.min(codePoint, text.codePointCount(0, text.length())))));
    }
    return parsed;
  }

  private static boolean complete(String text) {
    try {
      statement(text + ";");
      return true;
    } catch (SyntaxFailure invalidInput) {
      return false;
    }
  }

  private static final class Scan {
    private final String text;
    private final TextLintOptions options;
    private final List<Lexeme> tokens = new ArrayList<>();
    private final List<Diagnostic> diagnostics = new ArrayList<>();
    private final List<Statement> statements = new ArrayList<>();
    private final int[] lineStarts;
    private int cursor;
    private boolean fullyScanned = true;
    private boolean stopped;

    Scan(String text, TextLintOptions options) {
      this.text = text;
      this.options = options;
      var lines = new ArrayList<Integer>();
      lines.add(0);
      // Line index is bounded even when the caller supplies an oversized editor buffer.
      for (int offset = 0; offset < Math.min(text.length(), options.maxCharacters()); offset++) {
        if (text.charAt(offset) == '\r') {
          if (offset + 1 < text.length() && text.charAt(offset + 1) == '\n') {
            offset++;
          }
          lines.add(offset + 1);
        } else if (text.charAt(offset) == '\n') {
          lines.add(offset + 1);
        }
      }
      lineStarts = lines.stream().mapToInt(Integer::intValue).toArray();
    }

    TextLintReport run() {
      if (text.length() > options.maxCharacters()) {
        limit(0, "Text exceeds the configured input length limit.", List.of());
        return report();
      }
      lex();
      if (stopped) {
        return report();
      }
      if (tokens.size() < 2 || tokens.get(1).kind() != Kind.OPEN || !validHeader()) {
        error(
            Code.SYNTAX,
            "Expected a supported configuration wrapper followed by '{'.",
            0,
            tokens.isEmpty() ? 0 : tokens.get(0).end(),
            List.of());
        fullyScanned = false;
        return report();
      }
      cursor = 2;
      body(List.of(), 1);
      if (!stopped && cursor < tokens.size()) {
        error(
            Code.SYNTAX,
            "Unexpected input after the configuration closing brace.",
            tokens.get(cursor).start(),
            text.length(),
            List.of());
      }
      return report();
    }

    private boolean validHeader() {
      try {
        var parser = parser(value(tokens.get(0)));
        parser.configType();
        return parser.getNumberOfSyntaxErrors() == 0 && parser.getInputStream().LA(1) == Token.EOF;
      } catch (SyntaxFailure invalidHeader) {
        return false;
      }
    }

    private TextLintReport report() {
      diagnostics.sort(Comparator.comparingInt(diagnostic -> diagnostic.range().startOffset()));
      return new TextLintReport(diagnostics, statements, fullyScanned);
    }

    private void body(List<String> ancestors, int depth) {
      if (cursor < tokens.size() && tokens.get(cursor).kind() == Kind.CLOSE) {
        Lexeme closing = tokens.get(cursor);
        error(
            Code.SYNTAX,
            "Expected at least one TLV inside this container.",
            closing.start(),
            closing.end(),
            ancestors);
      }
      while (cursor < tokens.size() && !stopped) {
        Lexeme first = tokens.get(cursor);
        if (first.kind() == Kind.CLOSE) {
          cursor++;
          return;
        }
        if (first.kind() != Kind.WORD) {
          error(Code.SYNTAX, "Expected a TLV name.", first.start(), first.end(), ancestors);
          cursor++;
          continue;
        }
        var path = new ArrayList<>(ancestors);
        path.add(value(first));
        if (cursor + 1 < tokens.size() && tokens.get(cursor + 1).kind() == Kind.OPEN) {
          if (depth >= options.maxDepth()) {
            limit(first.start(), "Container nesting exceeds the configured depth limit.", path);
            return;
          }
          try {
            statement(value(first) + " { Placeholder; }");
          } catch (SyntaxFailure invalidName) {
            error(Code.SYNTAX, "Invalid container name.", first.start(), first.end(), path);
          }
          cursor += 2;
          body(path, depth + 1);
        } else {
          leaf(path);
        }
      }
      if (!stopped) {
        error(
            Code.UNMATCHED_BRACE,
            "Missing closing '}' for this container.",
            text.length(),
            text.length(),
            ancestors);
      }
    }

    private void leaf(List<String> path) {
      int firstIndex = cursor;
      Lexeme first = tokens.get(cursor++);
      if (first.end() - first.start() > options.maxStatementCharacters()) {
        limit(first.start(), "TLV exceeds the configured statement length limit.", path);
        return;
      }
      Lexeme previous = first;
      while (cursor < tokens.size() && !stopped) {
        Lexeme next = tokens.get(cursor);
        if (next.end() - first.start() > options.maxStatementCharacters()) {
          limit(first.start(), "TLV exceeds the configured statement length limit.", path);
          return;
        }
        if (next.kind() == Kind.BROKEN_STRING) {
          cursor++;
          if (cursor < tokens.size() && tokens.get(cursor).kind() == Kind.SEMICOLON) {
            cursor++;
          }
          error(
              Code.UNTERMINATED_STRING,
              "Invalid quoted value; check the closing quote and supported escapes before the line"
                  + " ends.",
              next.start(),
              next.end(),
              path);
          skipped(path, first.start(), tokens.get(cursor - 1).end());
          return;
        }
        if (next.kind() == Kind.SEMICOLON) {
          cursor++;
          check(path, firstIndex, cursor);
          return;
        }
        boolean nextLineStatement =
            next.kind() == Kind.WORD
                && cursor > firstIndex + 1
                && lineIndex(next.start()) > lineIndex(previous.end())
                && startsStatement(cursor);
        if (nextLineStatement
            || next.kind() == Kind.OPEN
            || next.kind() == Kind.CLOSE
            || (next.kind() == Kind.WORD
                && cursor > firstIndex + 1
                && (!(value(first).equals("Snmp11") || value(first).equals("Snmp64"))
                    || previous.kind() == Kind.STRING)
                && complete(text.substring(first.start(), previous.end())))) {
          error(
              Code.MISSING_SEMICOLON,
              "Expected ';' after this TLV; statement skipped.",
              previous.end(),
              previous.end(),
              path);
          skipped(path, first.start(), previous.end());
          return;
        }
        previous = next;
        cursor++;
      }
      if (!stopped) {
        error(
            Code.MISSING_SEMICOLON,
            "Expected ';' before end of input; statement skipped.",
            previous.end(),
            previous.end(),
            path);
        skipped(path, first.start(), previous.end());
      }
    }

    private boolean startsStatement(int index) {
      Lexeme first = tokens.get(index);
      if (index + 1 < tokens.size() && tokens.get(index + 1).kind() == Kind.OPEN) {
        try {
          statement(value(first) + " { Placeholder; }");
          return true;
        } catch (SyntaxFailure invalidName) {
          return false;
        }
      }
      for (int end = index + 1; end < tokens.size(); end++) {
        Lexeme token = tokens.get(end);
        if (token.end() - first.start() > options.maxStatementCharacters()
            || token.kind() == Kind.OPEN
            || token.kind() == Kind.CLOSE
            || token.kind() == Kind.BROKEN_STRING) {
          return false;
        }
        if (token.kind() == Kind.SEMICOLON) {
          try {
            statement(text.substring(first.start(), token.end()));
            return true;
          } catch (SyntaxFailure invalidInput) {
            return false;
          }
        }
      }
      return false;
    }

    private void check(List<String> path, int firstIndex, int endIndex) {
      Lexeme first = tokens.get(firstIndex);
      int end = tokens.get(endIndex - 1).end();
      try {
        var parsed = statement(text.substring(first.start(), end));
        if (parsed.snmp() != null) {
          var snmp = parsed.snmp();
          var problem = SnmpValueFormat.problem(snmp.dataType().getText(), snmp.value().getText());
          if (problem.isPresent()) {
            Lexeme value = tokens.get(endIndex - 2);
            error(Code.SNMP_VALUE, problem.get(), value.start(), value.end(), path);
            skipped(path, first.start(), end);
            return;
          }
        }
        statements.add(new Statement(path, range(first.start(), end), Status.ACCEPTED));
      } catch (SyntaxFailure invalidInput) {
        error(
            Code.SYNTAX,
            "Invalid TLV syntax; check the name, value, SNMP type and double quotes.",
            Math.min(end, first.start() + invalidInput.offset),
            Math.min(end, first.start() + invalidInput.offset),
            path);
        skipped(path, first.start(), end);
      }
    }

    private void skipped(List<String> path, int start, int end) {
      statements.add(new Statement(path, range(start, end), Status.SKIPPED));
    }

    private void lex() {
      // ANTLR owns tokenization. This adapter only bounds recovery: the legacy wildcard OID
      // rule otherwise consumes subsequent TLVs before DefaultErrorStrategy sees an error.
      var lexer = new tlvLexer(CharStreams.fromString(text));
      lexer.removeErrorListeners();
      var lexicalErrors = new InputErrors();
      lexer.addErrorListener(lexicalErrors);
      int[] offsets = new int[text.codePointCount(0, text.length()) + 1];
      int utf16 = 0;
      for (int index = 0; index < offsets.length - 1; index++) {
        offsets[index] = utf16;
        utf16 += Character.charCount(text.codePointAt(utf16));
      }
      offsets[offsets.length - 1] = text.length();
      int skipUntil = 0;
      Token token;
      while (!stopped && (token = lexer.nextToken()).getType() != Token.EOF) {
        int start = offsets[token.getStartIndex()];
        int end = offsets[token.getStopIndex() + 1];
        if (start < skipUntil) {
          lexicalErrors.failed = false;
          continue;
        }
        if (lexicalErrors.failed) {
          int errorStart = offsets[Math.min(lexicalErrors.offset, offsets.length - 1)];
          error(
              Code.SYNTAX,
              "Unrecognized character in input.",
              errorStart,
              errorStart + 1,
              List.of());
          lexicalErrors.failed = false;
        }
        Kind kind =
            switch (token.getType()) {
              case tlvLexer.LBRACE -> Kind.OPEN;
              case tlvLexer.RBRACE -> Kind.CLOSE;
              case tlvLexer.SEMICOLON -> Kind.SEMICOLON;
              case tlvLexer.QUOTED_STRING -> Kind.STRING;
              case tlvLexer.DOUBLE_QUOTE -> Kind.BROKEN_STRING;
              default -> Kind.WORD;
            };
        if (kind == Kind.BROKEN_STRING) {
          // A standalone quote is ANTLR's indication that QUOTED_STRING did not match.
          // Quoted values cannot span lines. Resume at the next line, not a guessed token.
          while (end < text.length() && text.charAt(end) != '\r' && text.charAt(end) != '\n') {
            end++;
          }
          skipUntil = end;
        }
        tokens.add(new Lexeme(kind, start, end));
      }
      if (lexicalErrors.failed) {
        int start = offsets[Math.min(lexicalErrors.offset, offsets.length - 1)];
        if (start >= skipUntil) {
          boolean comment = text.startsWith("/*", start);
          error(
              comment ? Code.UNTERMINATED_COMMENT : Code.SYNTAX,
              comment
                  ? "Missing closing '*/'; remaining input cannot be checked."
                  : "Unrecognized or incomplete token at end of input.",
              start,
              text.length(),
              List.of());
        }
      }
    }

    private String value(Lexeme token) {
      return text.substring(token.start(), token.end());
    }

    private SourceRange range(int start, int end) {
      int startLine = lineIndex(start);
      int endLine = lineIndex(end);
      return new SourceRange(
          start,
          end,
          startLine + 1,
          start - lineStarts[startLine] + 1,
          endLine + 1,
          end - lineStarts[endLine] + 1);
    }

    private int lineIndex(int offset) {
      int found = Arrays.binarySearch(lineStarts, offset);
      return found >= 0 ? found : -found - 2;
    }

    private void error(Code code, String message, int start, int end, List<String> path) {
      if (diagnostics.size() >= options.maxDiagnostics() - 1) {
        limit(start, "Diagnostic limit reached; remaining input was not checked.", path);
      } else {
        diagnostics.add(new Diagnostic(Severity.ERROR, code, message, range(start, end), path));
      }
    }

    private void limit(int offset, String message, List<String> path) {
      if (!stopped) {
        diagnostics.add(
            new Diagnostic(Severity.ERROR, Code.LIMIT, message, range(offset, offset), path));
      }
      fullyScanned = false;
      stopped = true;
    }
  }
}
