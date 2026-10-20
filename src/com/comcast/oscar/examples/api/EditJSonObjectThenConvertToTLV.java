/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples.api;

import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.tlv.TlvAssembler;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditJSonObjectThenConvertToTLV {

  /**
   * Method main.
   *
   * @param args String[]
   */
  public static void main(String[] args) {

    TlvBuilder tb = new TlvBuilder();

    try {
      tb.add(3, 0);
    } catch (TlvException e) {
      e.printStackTrace();
    }

    System.out.println("TLV: " + tb.toString());

    TlvDisassemble td = new TlvDisassemble(tb, TlvDisassemble.TLV_TYPE_DOCSIS);

    System.out.println("JSONArray: " + td.getTlvDictionary());

    /******************
     * Edit JSON Array
     *****************/
    /*Get JSON Dictionary*/
    JSONArray ja = td.getTlvDictionary();

    /*Obtain JSON Refernece Object*/
    JSONObject jo = null;
    try {
      jo = ja.getJSONObject(0);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    /*Change Value from 0 to 1*/
    try {
      jo.put(Dictionary.VALUE, 1);
    } catch (JSONException e) {
      e.printStackTrace();
    } finally {
      jo = new JSONObject();
    }

    System.out.println("JSONObject: " + jo.toString());

    /*Convert Back to TLV*/
    TlvAssembler ta = null;

    ta = new TlvAssembler(ja);

    System.out.println("TLV: " + ta.toString());
  }
}
