/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.ber;

import java.util.TreeMap;

public class OIDCollectionDuplicationValidation extends TreeMap<String, String> {

  /** */
  private static final long serialVersionUID = -6171417894956345993L;

  /** */
  public OIDCollectionDuplicationValidation() {
    super();
  }

  /**
   * @param sObjectID
   * @param bocBEROIDConversion BEROIDConversion
   * @return if true, no collision or duplication of OID, False indicates Collision or duplication
   *     of OID
   */
  public boolean add(BEROIDConversion bocBEROIDConversion, String sObjectID) {

    boolean boolStatus = true;

    if (super.containsKey(bocBEROIDConversion.getOidDotNotaion())) {
      boolStatus = false;
    } else {
      super.put(bocBEROIDConversion.getOidDotNotaion(), sObjectID);
    }

    return boolStatus;
  }

  /**
   * This method does nothing
   *
   * @param bocBEROIDConversion BEROIDConversion
   * @param sObjectID String
   * @return sObjectID
   */
  public String put(BEROIDConversion bocBEROIDConversion, String sObjectID) {
    return sObjectID;
  }
}
