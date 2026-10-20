/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples.api;

import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;

/** */
public class FetchJSONTemplateFromTLV {

  public static void main(String[] args) {

    /*NetworkAccess - ENABLE*/
    TlvBuilder tb = new TlvBuilder();

    try {
      tb.add(3, 1);
    } catch (TlvException e) {
      e.printStackTrace();
    }

    TlvDisassemble td = new TlvDisassemble(tb, TlvDisassemble.TLV_TYPE_DOCSIS);

    System.out.println(td.getTlvDictionary());
  }
}
