/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TlvBuilderOrderingTest {
  @Test
  void sortsBothDirectionsWithoutChangingSourceOrRepeatedValueOrder() throws Exception {
    var source = new TlvBuilder();
    source.add(3, new byte[] {10});
    source.add(1, new byte[] {20});
    source.add(3, new byte[] {30});
    byte[] original = source.toByteArray();
    assertArrayEquals(
        new byte[] {1, 1, 20, 3, 1, 10, 3, 1, 30},
        TlvBuilder.reorderTLV(true, source).toByteArray());
    assertArrayEquals(
        new byte[] {3, 1, 10, 3, 1, 30, 1, 1, 20},
        TlvBuilder.reorderTLV(false, source).toByteArray());
    assertArrayEquals(original, source.toByteArray());
  }
}
