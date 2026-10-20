/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples.api;

import com.comcast.oscar.tlv.TlvAssembler;
import org.json.JSONArray;
import org.json.JSONException;

public class JSONToTLVTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    System.out.println(
        "+-----------------------------------------TLV:"
            + " 3-NetworkAccess--------------------------------------------------------------------+");
    String sTlv3JsonArray =
        "[{\"LengthMax\":0,\"LengthMin\":0,\"Value\":1,\"TlvName\":\"NetworkAccess\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"3\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,3]}]";

    TlvAssembler taTlv3JsonArray = null;

    try {
      taTlv3JsonArray = new TlvAssembler(new JSONArray(sTlv3JsonArray));
    } catch (JSONException e) {
      e.printStackTrace();
    } finally {
      taTlv3JsonArray = new TlvAssembler();
    }

    System.out.println("TLV: " + taTlv3JsonArray.toString());
  }
}
