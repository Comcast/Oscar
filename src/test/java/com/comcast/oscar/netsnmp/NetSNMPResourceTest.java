package com.comcast.oscar.netsnmp;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NetSNMPResourceTest {
  @Test
  void initializesMissingAndEmptyCachesFromBundledMap(@TempDir Path directory) throws Exception {
    Path cache = directory.resolve("res").resolve(Constants.DOTTED_TEXTUAL_NetSNMP_MAP_FILE);
    try (var bundled =
        getClass().getResourceAsStream("/" + Constants.DOTTED_TEXTUAL_NetSNMP_MAP_FILE)) {
      assertNotNull(bundled);
      byte[] expected = bundled.readAllBytes();
      NetSNMP.initializeCache(cache.toFile());
      assertArrayEquals(expected, Files.readAllBytes(cache));
      Files.write(cache, new byte[0]);
      NetSNMP.initializeCache(cache.toFile());
      assertArrayEquals(expected, Files.readAllBytes(cache));
    }
    assertFalse(Files.exists(directory.resolve("db")));
    assertNull(getClass().getResource("/dictionary.sqlite"));
  }

  @Test
  void preservesExistingCache(@TempDir Path directory) throws Exception {
    Path cache = directory.resolve(Constants.DOTTED_TEXTUAL_NetSNMP_MAP_FILE);
    String existing = "{\"customName\": \".1.3.6.1.4.1\"}";
    Files.writeString(cache, existing);
    NetSNMP.initializeCache(cache.toFile());
    assertEquals(existing, Files.readString(cache));
  }

  @Test
  void resourceMapSupportsBidirectionalLookups() {
    assertEquals("res", NetSNMP.DirectoryStructureNetSNMP.fNetSNMPJSON().getParentFile().getName());
    assertEquals(".1.3.6.1.2.1.69.1.2.1.2.1", NetSNMP.toDottedOID("docsDevNmAccessIp.1"));
    assertEquals("docsDevNmAccessIp.1", NetSNMP.toTextualOID("1.3.6.1.2.1.69.1.2.1.2.1"));
  }
}
