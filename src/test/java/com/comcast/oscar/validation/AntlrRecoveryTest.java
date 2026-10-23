/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.parser.tlvLexer;
import com.comcast.oscar.parser.tlvParser;
import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

class AntlrRecoveryTest {
  @Test
  void recoversAfterMalformedSnmp() {
    var lexer =
        new tlvLexer(
            CharStreams.fromString("Docsis { Snmp11 1.3.6.1 Integer32 abc; NetworkAccess 1; }"));
    var parser = new tlvParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    var root = parser.begin();
    assertTrue(parser.getNumberOfSyntaxErrors() > 0);
    // Characterize the existing wildcard OID: default recovery consumes the sibling.
    assertFalse(
        root.startTLV().stream()
            .anyMatch(
                t -> t.tlv() != null && t.tlv().IDENTIFIER().getText().equals("NetworkAccess")),
        root.toStringTree(parser));
    var report = TextLinter.lint("Docsis { Snmp11 1.3.6.1 Integer32 abc; NetworkAccess 1; }");
    assertEquals(java.util.List.of("NetworkAccess"), report.statements().getLast().tlvNames());
    assertEquals(TextLintReport.Status.ACCEPTED, report.statements().getLast().status());
  }
}
