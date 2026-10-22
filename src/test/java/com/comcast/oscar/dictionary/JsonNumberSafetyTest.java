/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/** Keep dependency numeric safeguards covered without allocating dangerous inputs. */
class JsonNumberSafetyTest {
  // Upstream's default number limit is 1000 digits; use only one extra digit.
  private static final int OVER_DEFAULT_NUMBER_LIMIT = 1001;

  @Test
  void oversizedNumericLiteralRemainsText() {
    String digits = "9".repeat(OVER_DEFAULT_NUMBER_LIMIT);
    assertEquals(digits, new JSONObject("{\"value\":" + digits + "}").get("value"));
  }

  @Test
  void exponentExpansionIsBounded() {
    var object = new JSONObject("{\"value\":1e" + OVER_DEFAULT_NUMBER_LIMIT + "}");
    assertThrows(JSONException.class, () -> object.getBigInteger("value"));
    assertEquals(BigInteger.ZERO, object.optBigInteger("value", BigInteger.ZERO));
  }

  @Test
  void ordinaryUnsigned64BitValueRemainsExact() {
    String unsignedMaximum = "18446744073709551615";
    var object = new JSONObject("{\"value\":" + unsignedMaximum + "}");
    assertEquals(new BigInteger(unsignedMaximum), object.getBigInteger("value"));
    assertEquals(object.get("value"), new JSONObject(object.toString()).get("value"));
  }
}
