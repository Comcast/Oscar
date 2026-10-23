/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.validation;

import static com.comcast.oscar.validation.TextLintReport.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class TextLinterTest {
  static Stream<Arguments> values() {
    return Stream.of(
        Arguments.of("Integer", "-2147483648", "2147483648"),
        Arguments.of("Integer32", "2147483647", "abc"),
        Arguments.of("Counter", "4294967295", "-1"),
        Arguments.of("Counter32", "0", "4294967296"),
        Arguments.of("Gauge", "42", "1.5"),
        Arguments.of("Gauge32", "0xffffffff", "4294967296"),
        Arguments.of("TimeTicks", "042", "abc"),
        Arguments.of("IpAddress", "192.0.2.1", "999.1.2.3"),
        Arguments.of("HexString", "00:ab:FF", "0:zz"));
  }

  @ParameterizedTest
  @MethodSource("values")
  void acceptsValidTypesAndRecoversAfterInvalidValues(String type, String valid, String invalid) {
    for (String name : List.of("Snmp11", "Snmp64")) {
      String input =
          "Docsis {\n  "
              + name
              + " 1.3.6.1.2.1.1.7.0 "
              + type
              + " \""
              + invalid
              + "\";\n"
              + "  "
              + name
              + " 1.3.6.1.2.1.1.7.0 "
              + type
              + " \""
              + valid
              + "\";\n  NetworkAccess 1;\n}";
      var result = TextLinter.lint(input);
      assertTrue(result.fullyScanned());
      assertEquals(1, result.diagnostics().size(), result.toString());
      assertEquals(Code.SNMP_VALUE, result.diagnostics().getFirst().code());
      assertEquals(2, result.diagnostics().getFirst().range().line());
      assertEquals(
          '"' + invalid + '"',
          input.substring(
              result.diagnostics().getFirst().range().startOffset(),
              result.diagnostics().getFirst().range().endOffset()));
      assertEquals(
          List.of(Status.SKIPPED, Status.ACCEPTED, Status.ACCEPTED),
          result.statements().stream().map(Statement::status).toList());
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Docsis",
        "Docsis-4.0",
        "Main",
        "DPoE",
        "DPoG",
        "PacketCable-1.0",
        "PacketCable-1.5",
        "PacketCable-2.0"
      })
  void supportsEveryWrapperWithoutCompilation(String header) {
    var result = TextLinter.lint(header + " { Snmp11 1.3.6.1.2.1.1.5.0 OctetString \"\"; }");
    assertFalse(result.hasErrors(), result.toString());
  }

  @Test
  void commentsEscapesAndPunctuationAreNotDelimiters() {
    var result =
        TextLinter.lint(
            "Docsis { /* { ; \" */ Snmp11 1.3.6.1.2.1.1.5.0 OctetString"
                + " \"https://test/;{}/*comment*/\\\"\\\\\\n"
                + "\"; /* tail */ NetworkAccess 1; }");
    assertFalse(result.hasErrors(), result.toString());
    assertEquals(2, result.statements().size());
  }

  @Test
  void collectsIndependentFormattingAndValueErrors() {
    String input =
        """
        Docsis {
          Snmp11 1.3.6.1.2.1.1.7.0 Integer32 "abc";
          Snmp11 1.3.6.1.2.1.1.7.0 IpAddress "999.2.3.4";
          Snmp11 1.3.6.1.2.1.1.5.0 OctetString "missing close;
          Snmp11 1.3.6.1.2.1.1.5.0 OctetString "missing semicolon"
          NetworkAccess 1;
        }
        """;
    var result = TextLinter.lint(input);
    assertEquals(
        List.of(Code.SNMP_VALUE, Code.SNMP_VALUE, Code.UNTERMINATED_STRING, Code.MISSING_SEMICOLON),
        result.diagnostics().stream().map(Diagnostic::code).toList());
    assertEquals(Status.ACCEPTED, result.statements().getLast().status());
    assertTrue(result.fullyScanned());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Snmp11 1.3.6.1.2.1.1.7.0 Integer32 abc;",
        "Snmp11 1.3.6.1.2.1.1.7.0 UnknownType \"42\";",
        "Snmp11 1.3.6.1.2.1.1.5.0 OctetString \"bad\\q\";",
        "Snmp11 1.3.6.1.2.1.1.7.0 Integer32 abc",
        "NetworkAccess 1"
      })
  void malformedStatementDoesNotConsumeNextLine(String broken) {
    var result = TextLinter.lint("Docsis {\n" + broken + "\nNetworkAccess 1;\n}");
    assertTrue(result.hasErrors());
    assertEquals(
        List.of("NetworkAccess"), result.statements().getLast().tlvNames(), result.toString());
    assertEquals(Status.ACCEPTED, result.statements().getLast().status());
  }

  @Test
  void missingSemicolonsRecoverOnSameLine() {
    var result = TextLinter.lint("Docsis { NetworkAccess 1 MaxCPEAllowed 2; }");
    assertEquals(Code.MISSING_SEMICOLON, result.diagnostics().getFirst().code());
    assertEquals(Status.ACCEPTED, result.statements().getLast().status());
  }

  @Test
  void nestedErrorDoesNotHideFollowingSiblingAndOffsetsUseUtf16() {
    String input =
        "Docsis {\r\n"
            + " Snmp11 1.3.6.1.2.1.1.5.0 OctetString \"😀\"; eSTBConfig {\r\n"
            + "  Snmp11 1.3.6.1.2.1.1.7.0 Integer32 \"bad\";\r\n"
            + " }\r\n"
            + " NetworkAccess 1;\r\n"
            + "}";
    var result = TextLinter.lint(input);
    var diagnostic = result.diagnostics().getFirst();
    assertEquals(List.of("eSTBConfig", "Snmp11"), diagnostic.tlvNames());
    assertEquals(input.indexOf("\"bad\""), diagnostic.range().startOffset());
    assertEquals(3, diagnostic.range().line());
    assertEquals(Status.ACCEPTED, result.statements().getLast().status());
  }

  @Test
  void limitsAndUnclosedStructuresAreExplicit() {
    assertEquals(
        Code.UNMATCHED_BRACE,
        TextLinter.lint("Docsis { NetworkAccess 1;").diagnostics().getLast().code());
    assertTrue(TextLinter.lint("Docsis { NetworkAccess 1; } }").hasErrors());
    assertTrue(
        TextLinter.lint("Docsis { /* unfinished").diagnostics().stream()
            .anyMatch(d -> d.code() == Code.UNTERMINATED_COMMENT));
    for (var result :
        List.of(
            TextLinter.lint("Docsis { NetworkAccess 1; }", new TextLintOptions(8, 8, 2, 3)),
            TextLinter.lint("Docsis { Parent { Child 1; } }", new TextLintOptions(200, 100, 1, 3)),
            TextLinter.lint("Docsis { Name \"long value\"; }", new TextLintOptions(200, 5, 4, 3)),
            TextLinter.lint("Docsis { A 1 B 2 C 3; }", new TextLintOptions(200, 100, 4, 1)))) {
      assertFalse(result.fullyScanned());
      assertEquals(Code.LIMIT, result.diagnostics().getLast().code());
    }
  }

  @Test
  void emptyContainersAreNotValidGrammar() {
    assertTrue(TextLinter.lint("Docsis {}").hasErrors());
    assertTrue(TextLinter.lint("Docsis { Parent {} }").hasErrors());
  }

  @Test
  void malformedCharactersNeverBecomeAcceptedStatements() {
    var result = TextLinter.lint("Docsis { NetworkAccess @1; MaxCPEAllowed 2; }");
    assertTrue(result.hasErrors());
    assertEquals(Status.SKIPPED, result.statements().getFirst().status());
    assertEquals(Status.ACCEPTED, result.statements().getLast().status());
  }

  @Test
  void validMultilineSnmpAndEmptyPlaceholdersRemainAccepted() {
    var result = TextLinter.lint("Docsis { Snmp11\n1.3.6.1\nInteger32\n\"42\"; NetworkAccess; }");
    assertFalse(result.hasErrors(), result.toString());
    assertEquals(2, result.statements().size());
  }

  @Test
  void reportsAreImmutableAndLintDoesNotPrintJavaErrors() {
    var captured = new ByteArrayOutputStream();
    var original = System.err;
    try (var errors = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
      System.setErr(errors);
      var result = TextLinter.lint("Docsis { Snmp11 1.3.6.1 Integer32 \"abc\"; NetworkAccess 1; }");
      assertThrows(UnsupportedOperationException.class, () -> result.diagnostics().clear());
      assertThrows(
          UnsupportedOperationException.class,
          () -> result.statements().getFirst().tlvNames().clear());
    } finally {
      System.setErr(original);
    }
    assertEquals("", captured.toString(StandardCharsets.UTF_8));
    assertThrows(NullPointerException.class, () -> TextLinter.lint(null));
  }
}
