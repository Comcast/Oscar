/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TlvDisassembleOffsetTest {

  @Test
  void decodesFieldAfterContainerLargerThanOneByte() throws TlvException {
    TlvVariableBinding binding = new TlvVariableBinding();
    binding.add(9, new byte[254], 1);
    binding.add(18, new byte[] {7}, 1);

    var rows = new TlvDisassemble(binding, TlvDisassemble.TLV_TYPE_DPOE).getTopLevelTLVJSON(18);

    assertEquals(1, rows.length());
    assertEquals(7, rows.getJSONObject(0).getInt("Value"));
  }

  @Test
  void decodesFieldAtSmallOffset() throws TlvException {
    TlvVariableBinding binding = new TlvVariableBinding();
    binding.add(9, new byte[0], 1);
    binding.add(18, new byte[] {7}, 1);

    var rows = new TlvDisassemble(binding, TlvDisassemble.TLV_TYPE_DPOE).getTopLevelTLVJSON(18);

    assertEquals(1, rows.length());
    assertEquals(7, rows.getJSONObject(0).getInt("Value"));
  }
}