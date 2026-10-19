/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DocsisLabExamplesTest {
  @ParameterizedTest
  @ValueSource(strings = {"3.1", "4.0"})
  void publishedExamplesMatchBytesMicsAndRoundTrips(String version) throws Exception {
    Path directory = Path.of("src/com/comcast/oscar/examples/testfiles");
    String key = Files.readString(directory.resolve("DOCSIS-LAB.key"), StandardCharsets.US_ASCII);
    assertEquals("SHAREDSECRET", key.strip());
    String name = "DOCSIS-" + version + "-LAB";
    boolean docsis40 = version.equals("4.0");
    int type =
        docsis40
            ? ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE
            : ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE;
    var imported =
        new ConfigurationFileImport(
            new StringBuilder(Files.readString(directory.resolve(name + ".txt"))));
    var file = new ConfigurationFile(type, imported.getTlvBuilder(), key);
    file.commit();
    byte[] bytes = Files.readAllBytes(directory.resolve(name + ".bin"));
    assertArrayEquals(bytes, file.toByteArray());
    assertEquals(0, bytes.length % 4);
    Map<Integer, byte[]> elements = elements(bytes, true);
    assertEquals(List.of(3, 24, 25, 6, 7), List.copyOf(elements.keySet()));
    assertArrayEquals(new byte[] {3, 1, 1}, elements.get(3));
    var md5 = MessageDigest.getInstance("MD5");
    for (int tag : List.of(3, 24, 25)) md5.update(elements.get(tag));
    assertArrayEquals(md5.digest(), value(elements, 6));
    var hmac = Mac.getInstance("HmacMD5");
    hmac.init(new SecretKeySpec(key.getBytes(StandardCharsets.US_ASCII), "HmacMD5"));
    for (int tag : List.of(3, 6, 24, 25)) hmac.update(elements.get(tag));
    assertArrayEquals(hmac.doFinal(), value(elements, 7));
    var upstream = elements(value(elements, 24), false);
    var downstream = elements(value(elements, 25), false);
    assertArrayEquals(
        HexFormat.of().parseHex(docsis40 ? "00000001" : "05f5e100"), value(upstream, 8));
    assertArrayEquals(
        HexFormat.of().parseHex(docsis40 ? "0000000a" : "3b9aca00"), value(downstream, 8));
    if (docsis40) {
      assertArrayEquals(new byte[] {3}, value(upstream, 41));
      assertArrayEquals(new byte[] {3}, value(downstream, 41));
      assertArrayEquals(
          HexFormat.of().parseHex("000a006403e8"),
          value(elements(value(downstream, 40), false), 6));
    }
    var decoded =
        new ConfigurationFileImport(
            new StringBuilder(new ConfigurationFileExport(file).toPrettyPrint(false)));
    var rebuilt = new ConfigurationFile(type, decoded.getTlvBuilder(), key);
    rebuilt.commit();
    assertArrayEquals(bytes, rebuilt.toByteArray());
  }

  private static Map<Integer, byte[]> elements(byte[] bytes, boolean complete) {
    Map<Integer, byte[]> result = new LinkedHashMap<>();
    int offset = 0;
    boolean ended = false;
    while (offset < bytes.length) {
      int tag = Byte.toUnsignedInt(bytes[offset]);
      if (complete && tag == 255) {
        ended = true;
        for (int index = offset + 1; index < bytes.length; index++) assertEquals(0, bytes[index]);
        break;
      }
      assertTrue(offset + 1 < bytes.length);
      int end = offset + 2 + Byte.toUnsignedInt(bytes[offset + 1]);
      assertTrue(end <= bytes.length);
      assertNull(result.put(tag, Arrays.copyOfRange(bytes, offset, end)));
      offset = end;
    }
    if (complete) assertTrue(ended);
    return result;
  }

  private static byte[] value(Map<Integer, byte[]> elements, int tag) {
    byte[] encoded = elements.get(tag);
    assertNotNull(encoded, "Missing tag " + tag);
    return Arrays.copyOfRange(encoded, 2, encoded.length);
  }
}
