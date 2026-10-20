/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/** Checks the staged source catalog without activating it as a configuration family. */
class EmbeddedServiceCatalogTest {
  private JSONObject catalog() throws Exception {
    return new JSONObject(Files.readString(Path.of("res/tlv-registry/erouter.json")));
  }

  private JSONObject definition(JSONObject catalog, int... path) {
    String key = new JSONArray(path).toString();
    for (Object value : catalog.getJSONArray("definitions")) {
      JSONObject entry = (JSONObject) value;
      if (entry.getJSONArray("path").toString().equals(key)) return entry;
    }
    fail("Missing relative path " + key);
    return null;
  }

  @Test
  void relativePathsHaveUniqueNamesAndContainerParents() throws Exception {
    JSONObject catalog = catalog();
    assertEquals("embedded-service", catalog.getString("catalogType"));
    assertEquals("relative", catalog.getString("pathMode"));
    assertEquals("reviewed-subset-active", catalog.getString("status"));
    assertEquals(
        "[202]", catalog.getJSONObject("attachment").getJSONArray("parentPath").toString());
    Set<String> paths = new HashSet<>();
    Set<String> names = new HashSet<>();
    for (Object value : catalog.getJSONArray("definitions")) {
      JSONObject entry = (JSONObject) value;
      JSONArray path = entry.getJSONArray("path");
      assertFalse(path.isEmpty());
      assertTrue(paths.add(path.toString()));
      for (Object part : path) assertTrue((Integer) part >= 0 && (Integer) part <= 255);
      String name = entry.getString("name");
      assertFalse(name.isBlank());
      assertFalse(name.codePoints().anyMatch(Character::isWhitespace));
      int[] parent = new int[path.length() - 1];
      for (int i = 0; i < parent.length; i++) parent[i] = path.getInt(i);
      assertTrue(names.add(new JSONArray(parent) + name.toLowerCase(java.util.Locale.ROOT)));
      if (parent.length > 0)
        assertEquals("container", definition(catalog, parent).getString("kind"));
      assertTrue(entry.getString("sourceSection").startsWith("B.4."));
    }
  }

  @Test
  void preservesSourceBoundariesAndUnresolvedEncodings() throws Exception {
    JSONObject catalog = catalog();
    JSONObject interval = definition(catalog, 10);
    assertEquals(2, interval.getJSONObject("length").getInt("bytes"));
    assertEquals(3, interval.getInt("minimum"));
    assertEquals(1800, interval.getInt("maximum"));
    assertEquals(30, interval.getInt("default"));
    assertEquals("snmpVarBind", definition(catalog, 11).getString("kind"));
    assertTrue(definition(catalog, 11).getBoolean("repeatable"));
    assertEquals(
        "[6,18]",
        definition(catalog, 53, 2, 1)
            .getJSONObject("length")
            .getJSONArray("allowedBytes")
            .toString());
    assertEquals("unresolved", definition(catalog, 4).getJSONObject("length").getString("status"));
    assertEquals("unresolved", definition(catalog, 54, 2).getString("encodingStatus"));
    assertTrue(definition(catalog, 2, 4).getBoolean("sensitive"));
    assertTrue(definition(catalog, 53, 1).getBoolean("sensitive"));
  }
}
