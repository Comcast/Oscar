/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.ber;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.validation.TextLinter;
import java.util.Locale;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.snmp4j.asn1.BER;

class SnmpDatatypeAliasTest {
  @ParameterizedTest
  @CsvSource({"Integer,Integer32", "Counter,Counter32", "Gauge,Gauge32"})
  void aliasesResolveToTheirCanonicalBerTags(String alias, String canonical) {
    byte expected =
        switch (canonical) {
          case "Integer32" -> BER.INTEGER32;
          case "Counter32" -> BER.COUNTER32;
          case "Gauge32" -> BER.GAUGE32;
          default -> throw new AssertionError(canonical);
        };
    assertEquals(expected, BERService.berStringDataTypeToByte(alias));
    assertEquals(expected, BERService.berStringDataTypeToByte(alias.toLowerCase(Locale.ROOT)));
    assertEquals(expected, BERService.berStringDataTypeToByte(alias.toUpperCase(Locale.ROOT)));
  }

  @ParameterizedTest
  @CsvSource({
    "Integer,Integer32,-2147483648",
    "Integer,Integer32,2147483647",
    "Counter,Counter32,0",
    "Counter,Counter32,4294967295",
    "Gauge,Gauge32,0",
    "Gauge,Gauge32,4294967295"
  })
  void lintAcceptedAliasesCompileToIdenticalBytes(String alias, String canonical, String value) {
    for (String tlv : new String[] {"Snmp11", "Snmp64"}) {
      String header = tlv.equals("Snmp64") ? "PacketCable-1.5" : "Docsis";
      String source =
          header + " { " + tlv + " 1.3.6.1.2.1.1.7.0 " + alias + " \"" + value + "\"; }";
      assertFalse(TextLinter.lint(source).hasErrors());
      byte[] expected =
          new ConfigurationFileImport(new StringBuilder(source.replace(alias, canonical)))
              .toByteArray();
      assertNotNull(expected);
      assertTrue(expected.length > 0);
      byte[] actual = new ConfigurationFileImport(new StringBuilder(source)).toByteArray();
      assertArrayEquals(expected, actual, tlv + " " + alias + " " + value);
    }
  }
}
