/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples;

import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

/** */
public class Info {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    /*
     *
     * http://www.tutorialspoint.com/java/java_regular_expressions.htm
     *
     *
     *
     */

    TlvBuilder tbTLV = new TlvBuilder();

    String sTlvHex = "03010132080406020401020304";

    try {
      tbTLV.add(new HexString(HexString.toByteArray(sTlvHex)));
    } catch (TlvException e) {
      e.printStackTrace();
    }

    TlvDisassemble tlvDisassemble =
        new TlvDisassemble(tbTLV, TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME);

    System.out.println(tlvDisassemble.getTlvDictionary().toString());

    System.out.println(System.getProperty("user.dir"));
  }
}
