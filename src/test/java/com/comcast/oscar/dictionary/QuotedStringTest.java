/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.utilities.TlvTextString;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class QuotedStringTest {
  private ConfigurationFileImport parse(String text) {
    return new ConfigurationFileImport(new StringBuilder(text));
  }

  private void roundTrip(String text) throws Exception {
    var input = parse(text);
    String exported =
        new ConfigurationFileExport(input.getTlvBuilder(), input.getConfigurationFileType())
            .toPrettyPrint(false, true);
    assertArrayEquals(input.toByteArray(), parse(exported).toByteArray(), exported);
  }

  @Test
  void urlUsesLiteralBytesWithoutQuoteDelimiters() throws Exception {
    String url = "https://acs.example.test:7547/cwmp?unit=lab&mode=1#agent";
    var input =
        parse(
            "Docsis { eRouterConfig { TR-069ManagementServer { URL "
                + TlvTextString.quote(url)
                + "; } } }");
    byte[] value = url.getBytes(StandardCharsets.US_ASCII);
    byte[] expected = new byte[value.length + 6];
    expected[0] = (byte) 202;
    expected[1] = (byte) (value.length + 4);
    expected[2] = 2;
    expected[3] = (byte) (value.length + 2);
    expected[4] = 2;
    expected[5] = (byte) value.length;
    System.arraycopy(value, 0, expected, 6, value.length);
    assertArrayEquals(expected, input.toByteArray());
    roundTrip(
        "Docsis { eRouterConfig { TR-069ManagementServer { URL "
            + TlvTextString.quote(url)
            + "; } } }");
  }

  @Test
  void stringsPreserveEscapesWhitespaceAndCommentDelimiters() throws Exception {
    for (String value :
        new String[] {
          "two words", "Docsis", "a", "a\"b\\c", "line\nnext\tend\r", "a;{b}/*literal*/"
        }) {
      assertEquals(value, TlvTextString.decode(TlvTextString.quote(value)));
      roundTrip("Docsis { SoftwareUpgradeFilename " + TlvTextString.quote(value) + "; }");
      roundTrip(
          "Docsis { eRouterConfig { TR-069ManagementServer { Password "
              + TlvTextString.quote(value)
              + "; } } }");
      roundTrip(
          "Docsis { Snmp11 .1.3.6.1.2.1.1.4.0 OctetString " + TlvTextString.quote(value) + "; }");
    }
    roundTrip("Docsis { SoftwareUpgradeFilename legacy-file.bin; NetworkAccess 1; }");
  }

  @Test
  void rejectsInvalidEscapesAndUnterminatedQuotes() {
    assertThrows(
        IllegalArgumentException.class,
        () -> parse("Docsis { SoftwareUpgradeFilename \"bad\\q\"; }"));
    assertThrows(
        IllegalArgumentException.class,
        () -> parse("Docsis { SoftwareUpgradeFilename \"unfinished; }"));
  }
}
