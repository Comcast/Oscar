/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.utilities;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import org.junit.jupiter.api.Test;

class PrettyPrintTest {
  @Test
  void formatsWithoutCommentsAndPreservesOrder() {
    assertEquals(
        "Docsis {\n\tMaxCPEAllowed 3;\n\tNetworkAccess 1;\n}\n",
        new PrettyPrint("Docsis{MaxCPEAllowed 3;NetworkAccess 1;}").toString());
  }

  @Test
  void preservesQuotedSyntaxAndComments() {
    String literal = "\"a  b; { } /* literal */ \\*EOCB*\\ \\\"quoted\\\"\"";
    String output =
        new PrettyPrint("Docsis { Value " + literal + "; /* keep  spaces */ }").toString();
    assertTrue(output.contains(literal + "; /* keep  spaces */\n"));
    assertEquals(output, new PrettyPrint(output).toString());
  }

  @Test
  void handlesDeepContainersAndPrunesMarkedEmptyAncestors() {
    String marker = "\\*EOCB*\\";
    String nested = "Leaf 1;";
    String empty = "";
    for (int depth = 0; depth < 1000; depth++) {
      nested = "Parent { " + nested + " } " + marker;
      empty = "Parent { " + empty + " } " + marker;
    }
    String output = new PrettyPrint("Docsis { " + nested + " }").toString();
    assertTrue(output.contains("\t".repeat(1001) + "Leaf 1;"));
    assertFalse(output.contains(marker));
    assertEquals("Docsis {\n}\n", new PrettyPrint("Docsis { " + empty + " }").toString());
    assertEquals("Parent {\n}\n", new PrettyPrint("Parent {}").toString());
  }

  @Test
  void rejectsMalformedInputWithoutIncludingValuesInErrors() {
    for (String input : new String[] {"A {", "}", "A \"secret", "/* secret"}) {
      var error = assertThrows(IllegalArgumentException.class, () -> new PrettyPrint(input));
      assertFalse(error.getMessage().contains("secret"));
    }
  }

  @Test
  void wrapsProseAndValidatesWidth() {
    assertEquals("one two\nthree", PrettyPrint.toParagraphForm(" one  two\nthree ", 7));
    assertEquals("abcdefgh\nx", PrettyPrint.toParagraphForm("abcdefgh x", 3));
    assertEquals("", PrettyPrint.toParagraphForm(" \n ", 3));
    assertThrows(IllegalArgumentException.class, () -> PrettyPrint.toParagraphForm("a", 0));
    Integer original = PrettyPrint.PARAGRAPH_OFFSET;
    try {
      PrettyPrint.PARAGRAPH_OFFSET = null;
      assertThrows(IllegalArgumentException.class, () -> PrettyPrint.ToParagraphForm("a"));
      PrettyPrint.PARAGRAPH_OFFSET = -1;
      assertThrows(IllegalArgumentException.class, () -> PrettyPrint.ToParagraphForm("a"));
    } finally {
      PrettyPrint.PARAGRAPH_OFFSET = original;
    }
  }

  @Test
  void exportedQuotedValuesRecompileToIdenticalBytes() throws Exception {
    var original =
        new ConfigurationFileImport(
            new StringBuilder("Docsis { Snmp11 1.3.6.1.2.1.1.5.0 OctetString \"lab-modem;a\"; }"));
    var exporter =
        new ConfigurationFileExport(original.getTlvBuilder(), original.getConfigurationFileType());
    exporter.setDotTextOIDOutputFormat(ConfigurationFileExport.DOTTED_OID_FORMAT);
    for (boolean suppressComments : new boolean[] {false, true}) {
      String text = exporter.toPrettyPrint(false, suppressComments);
      assertTrue(text.contains("\"lab-modem;a\""), text);
      assertArrayEquals(
          original.toByteArray(),
          new ConfigurationFileImport(new StringBuilder(text)).toByteArray());
    }
  }
}
