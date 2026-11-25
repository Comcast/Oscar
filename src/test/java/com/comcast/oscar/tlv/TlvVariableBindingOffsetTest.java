/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TlvVariableBindingOffsetTest {

  @Test
  void findsOffsetsPastOneByte() throws TlvException {
    byte[] buffer = new byte[304];
    buffer[0] = (byte) 0xFA;
    buffer[1] = (byte) 254;
    buffer[256] = (byte) 0xFB;
    buffer[257] = 42;
    buffer[300] = 0x22;
    buffer[301] = 2;
    buffer[302] = 0x01;
    buffer[303] = 0;

    int[] offsets = TlvVariableBinding.findTlvOffsets(buffer, new byte[] {0x22, 0x01}, 1);

    assertArrayEquals(new int[] {302}, offsets);
  }

  @Test
  void findsSmallOffsets() throws TlvException {
    byte[] buffer = {0x22, 0x02, 0x01, 0x00};

    int[] offsets = TlvVariableBinding.findTlvOffsets(buffer, new byte[] {0x22, 0x01}, 1);

    assertArrayEquals(new int[] {2}, offsets);
  }

  @Test
  void findsEveryMatchInEncounterOrderAcrossTheOneByteBoundary() throws TlvException {
    byte[] buffer = new byte[506];
    buffer[0] = 9;
    buffer[1] = (byte) 248;
    buffer[250] = 0x22;
    buffer[251] = (byte) 254;
    buffer[252] = 0x01;
    buffer[253] = 0;
    buffer[254] = 9;
    buffer[255] = (byte) 248;
    buffer[504] = 0x01;
    buffer[505] = 0;

    int[] offsets = TlvVariableBinding.findTlvOffsets(buffer, new byte[] {0x22, 0x01}, 1);

    assertArrayEquals(new int[] {252, 504}, offsets);
  }

  @Test
  void legacyEncodingKeepsSmallOffsetCompatibility() throws TlvException {
    byte[] buffer = {0x22, 0x02, 0x01, 0x00};

    byte[] encoded = TlvVariableBinding.findTLVIndex(buffer, new byte[] {0x22, 0x01}, 1);

    assertArrayEquals(new byte[] {2}, encoded);
  }
}