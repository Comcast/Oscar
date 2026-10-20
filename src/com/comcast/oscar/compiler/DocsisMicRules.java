/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import static com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes.*;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Shared MIC coverage and cryptographic primitives; never logs bytes or keys. */
public final class DocsisMicRules {
  /** Selects MIC coverage independently of parsing and complete-file validation. */
  public enum Coverage {
    /** Preserve historical OSCAR DOCSIS 1.0-3.0 and DPoE behavior. */
    LEGACY_COMPATIBILITY,
    /** MULPI 3.1 I21 and 4.0 I12, Annex D.2.1. */
    DOCSIS_31_AND_40
  }

  // Wire type IDs, in digest order rather than numerical or physical file order.
  private static final List<Integer> COMPATIBILITY_ORDER =
      List.of(
          DS_FREQUENCY,
          US_CHANN_ID,
          NETWORK_ACC_CTRL_OBJ,
          DOC_10_CLASS_OF_SERV,
          BPI,
          DOC_EXT_FIELD,
          CM_MIC,
          MAX_CPES,
          TFTP_SRV_TIMESTAMP,
          TFTP_SRV_PROV_MODEM_IPV4_ADDR,
          US_PKT_CLASS,
          DS_PKT_CLASS,
          US_SERVICE_FLOW,
          DS_SERVICE_FLOW,
          MAX_NUM_CLASS,
          PRIVACY_ENABLE,
          PHS,
          SUB_MGMT_CTRL,
          SUB_MGMT_CPE_IPV4_LIST,
          SUB_MGMT_FILTER_GROUPS,
          ENABLE_TEST_MODES);
  private static final List<Integer> CURRENT_ORDER = currentOrder();

  private DocsisMicRules() {}

  /**
   * Returns the immutable CMTS MIC type order for the selected coverage profile. Repeated
   * occurrences of each type must retain their relative physical order. This does not describe the
   * E-MIC bitmap or require sorting the stored file.
   *
   * <p>For example, both profiles place type 6 before type 18; DOCSIS 3.1/4.0 includes type 59
   * after type 20, and excludes obsolete types 4 and 26.
   *
   * @param coverage non-null MIC coverage profile, not an application release version
   * @return immutable ordered wire type IDs
   * @throws NullPointerException if coverage is null
   */
  public static List<Integer> orderedTypes(Coverage coverage) {
    return switch (Objects.requireNonNull(coverage)) {
      case LEGACY_COMPATIBILITY -> COMPATIBILITY_ORDER;
      case DOCSIS_31_AND_40 -> CURRENT_ORDER;
    };
  }

  private static List<Integer> currentOrder() {
    var types = new ArrayList<>(COMPATIBILITY_ORDER);
    types.remove(Integer.valueOf(DOC_10_CLASS_OF_SERV));
    types.remove(Integer.valueOf(PHS));
    types.add(
        types.indexOf(TFTP_SRV_PROV_MODEM_IPV4_ADDR) + 1, TFTP_SRV_PROVISIONED_MODEMIPV6_ADDRESS);
    return List.copyOf(types);
  }

  // Share selection without requiring the verifier to reserialize original wire slices.
  static <T> List<T> orderLegacyParameters(
      List<T> parameters, ToIntFunction<T> typeOf, Coverage coverage) {
    var selected = new ArrayList<T>();
    for (int type : orderedTypes(coverage)) {
      for (T parameter : parameters) {
        if (typeOf.applyAsInt(parameter) == type) selected.add(parameter);
      }
    }
    return selected;
  }

  static byte[] hmac(byte[] secret, byte[] input) {
    try {
      Mac mac = Mac.getInstance("HmacMD5");
      mac.init(new SecretKeySpec(secret, "HmacMD5"));
      return mac.doFinal(input);
    } catch (GeneralSecurityException failure) {
      throw new IllegalStateException("HMAC-MD5 unavailable", failure);
    }
  }

  static byte[] md5(byte[] input) {
    try {
      return MessageDigest.getInstance("MD5").digest(Objects.requireNonNull(input));
    } catch (GeneralSecurityException failure) {
      throw new IllegalStateException("MD5 unavailable", failure);
    }
  }
}
