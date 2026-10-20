/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples;

import com.comcast.oscar.dictionary.TlvDictionary;

public class DictionaryToJSON {

  public static void main(String[] args) {

    TlvDictionary dsq = new TlvDictionary(TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME);

    System.out.println(
        dsq.getAllTlvDefinition(TlvDictionary.CONFIGURATION_FILE_TYPE_DOCSIS).toString());
  }
}
