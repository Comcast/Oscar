/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples.api;

import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

public class TLVToJSONTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    /*Network Access TLV*/
    String sTLV = "030101";

    TlvBuilder tbTLV = new TlvBuilder();

    try {
      tbTLV.add(new HexString(HexString.toByteArray(sTLV)));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    TlvDisassemble tdTLV = new TlvDisassemble(tbTLV, TlvDisassemble.TLV_TYPE_DOCSIS);

    System.out.println("JSON: " + tdTLV.getTlvDictionary());
  }
}
