/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.tlv.TlvDisassemblyReport.Status;
import org.junit.jupiter.api.Test;

class TlvDiagnosticTest {
  private TlvDisassemblyReport inspect(int... values) {
    byte[] bytes = new byte[values.length];
    for (int index = 0; index < values.length; index++) {
      bytes[index] = (byte) values[index];
    }
    return TlvDisassemble.diagnose(bytes, DisassemblyOptions.docsis());
  }

  @Test
  void retainsUnknownAndRepeatedEntriesInWireOrder() {
    var report = inspect(3, 1, 1, 45, 2, 17, 19, 3, 1, 0, 255, 0);
    assertEquals(5, report.entries().size());
    assertEquals(Status.UNKNOWN_TYPE, report.entries().get(1).status());
    assertEquals(2, report.entries().get(2).occurrence());
    assertEquals(7, report.entries().get(2).headerOffset());
    assertFalse(report.structurallyComplete());
  }

  @Test
  void boundsDamagedChildAndContinuesAfterParent() {
    var report = inspect(60, 3, 1, 5, 1, 3, 1, 1);
    var child = report.entries().get(0).children().get(0);
    assertEquals(Status.TRUNCATED_VALUE, child.status());
    assertEquals(5, child.endOffset());
    assertEquals(5, report.entries().get(1).headerOffset());
    assertEquals(Status.OK, report.entries().get(1).status());
  }

  @Test
  void reportsTruncationAndTrailingDataWithoutGuessing() {
    assertEquals(Status.TRUNCATED_HEADER, inspect(3).entries().get(0).status());
    assertEquals(Status.TRUNCATED_VALUE, inspect(3, 4, 1).entries().get(0).status());
    assertEquals(Status.TRAILING_DATA, inspect(255, 0, 1).entries().get(1).status());
    assertEquals(Status.EMPTY_INPUT, inspect().entries().get(0).status());
    assertEquals(Status.INVALID_LENGTH, inspect(3, 0).entries().get(0).status());
  }

  @Test
  void preservesOriginalBytesAndLimitsWork() {
    byte[] original = {3, 1, 1};
    var report = TlvDisassemble.diagnose(original, DisassemblyOptions.docsis());
    original[2] = 0;
    byte[] copied = report.copyBytes(0, 3);
    copied[2] = 0;
    assertEquals(1, report.copyBytes(0, 3)[2]);
    assertThrows(IndexOutOfBoundsException.class, () -> report.copyBytes(0, 4));
    assertThrows(UnsupportedOperationException.class, () -> report.entries().clear());
    assertTrue(report.structurallyComplete());
    var limited =
        TlvDisassemble.diagnose(
            new byte[] {3, 1, 1, 3, 1, 0},
            new DisassemblyOptions(DisassemblyOptions.Family.DOCSIS, 100, 8, 1));
    assertEquals(Status.ENTRY_LIMIT, limited.entries().get(1).status());
    assertThrows(
        IllegalArgumentException.class,
        () ->
            TlvDisassemble.diagnose(
                original, new DisassemblyOptions(DisassemblyOptions.Family.DOCSIS, 2, 8, 10)));
  }

  @Test
  void handlesCorrectedClassifierAndDepthLimitInBothFamilies() {
    byte[] nested = {60, 7, 10, 5, 3, 3, 4, 0, 0};
    for (var family : DisassemblyOptions.Family.values()) {
      var report = TlvDisassemble.diagnose(nested, new DisassemblyOptions(family, 100, 8, 10));
      assertTrue(report.structurallyComplete());
      assertEquals(3, report.entries().get(0).children().get(0).children().get(0).declaredLength());
      var limited = TlvDisassemble.diagnose(nested, new DisassemblyOptions(family, 100, 1, 10));
      assertEquals(Status.DEPTH_LIMIT, limited.entries().get(0).status());
    }
  }
}
