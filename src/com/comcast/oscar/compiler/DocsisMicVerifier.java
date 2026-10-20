/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import static com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes.*;

import com.comcast.oscar.dictionary.TlvRegistry;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Read-only DOCSIS configuration MIC verification against original wire bytes. */
public final class DocsisMicVerifier {
  private static final int EXTENDED_PARAMETERS = 6;
  private static final int ALGORITHM = 1;
  private static final int BITMAP = 2;
  private static final int EXPLICIT_DIGEST = 3;
  private static final int VENDOR_ID = 8;
  private static final int HMAC_MD5 = 1;
  private static final int MMH = 2;
  private static final int MD5_BYTES = 16;
  private static final int MMH_BYTES = 8;

  /** Selects length-field definitions; a binary does not identify its DOCSIS edition. */
  public enum Profile {
    DOCSIS_31(TlvRegistry.DOCSIS_FAMILY, DocsisMicRules.Coverage.DOCSIS_31_AND_40),
    DOCSIS_40(TlvRegistry.DOCSIS_40_FAMILY, DocsisMicRules.Coverage.DOCSIS_31_AND_40),
    /** Historical OSCAR DOCSIS 1.0-3.0 coverage, not a new conformance claim. */
    LEGACY_DOCSIS(TlvRegistry.DOCSIS_FAMILY, DocsisMicRules.Coverage.LEGACY_COMPATIBILITY);

    private final String family;
    private final DocsisMicRules.Coverage coverage;

    Profile(String family, DocsisMicRules.Coverage coverage) {
      this.family = family;
      this.coverage = coverage;
    }
  }

  /** Independent digest outcome; NOT_PRESENT is not successful verification. */
  public enum Status {
    MATCH,
    MISMATCH,
    NOT_PRESENT,
    CANNOT_VERIFY
  }

  /** Fixed diagnostics contain neither input bytes nor supplied keys. */
  public enum Reason {
    NONE,
    MISSING_SECRET,
    MALFORMED_BINARY,
    UNSUPPORTED_ALGORITHM
  }

  /**
   * Independent results for each digest, without computed or supplied secret material.
   *
   * @param cm unkeyed CM digest result
   * @param cmts TLV 7 result (legacy or implicit extended)
   * @param extended explicit or implicit extended digest result
   * @param reason explanation for a result that cannot be calculated
   */
  public record Result(Status cm, Status cmts, Status extended, Reason reason) {}

  private record Element(int type, int start, int valueStart, int end) {}

  private DocsisMicVerifier() {}

  /**
   * Checks one supplied secret against all supported digests in an original binary.
   *
   * <p>No compilation, file writes, key guessing, or logging is performed. A mismatch can indicate
   * a wrong key or changed file data, not necessarily a wrong key.
   *
   * @param binary original configuration bytes, not null; never modified
   * @param profile DOCSIS edition used for TLV length fields, not null
   * @param secret shared key bytes; null or empty reports missing-key results
   * @return separate CM, CMTS and extended MIC outcomes
   */
  public static Result verify(byte[] binary, Profile profile, byte[] secret) {
    return verify(binary, profile, secret, secret);
  }

  /**
   * Checks legacy and extended digests with independently supplied secrets.
   *
   * <p>Input is copied for verification. Private key copies are cleared on return; callers own and
   * must clear their input arrays. No full provisioning or CMTS registration-validity claim follows
   * from matching digests.
   *
   * @param binary original binary bytes, not null
   * @param profile DOCSIS edition for length parsing, not null
   * @param legacySecret key for legacy HMAC-MD5, or null if unavailable
   * @param extendedSecret extended key, or null if unavailable
   * @return fixed status values; malformed input never produces a match
   */
  public static Result verify(
      byte[] binary, Profile profile, byte[] legacySecret, byte[] extendedSecret) {
    Objects.requireNonNull(binary);
    Objects.requireNonNull(profile);
    byte[] bytes = binary.clone();
    byte[] legacy = legacySecret == null ? new byte[0] : legacySecret.clone();
    byte[] extended = extendedSecret == null ? new byte[0] : extendedSecret.clone();
    try {
      return check(bytes, profile, legacy, extended);
    } catch (IllegalArgumentException failure) {
      return new Result(
          Status.CANNOT_VERIFY,
          Status.CANNOT_VERIFY,
          Status.CANNOT_VERIFY,
          Reason.MALFORMED_BINARY);
    } finally {
      Arrays.fill(legacy, (byte) 0);
      Arrays.fill(extended, (byte) 0);
      Arrays.fill(bytes, (byte) 0);
    }
  }

  private static Result check(byte[] bytes, Profile profile, byte[] legacy, byte[] extended) {
    var registry = TlvRegistry.forFamily(profile.family);
    List<Element> elements = parse(bytes, 0, bytes.length, registry);
    Element cm = unique(elements, CM_MIC);
    Element cmts = unique(elements, CMTS_MIC);
    requireLength(cm, MD5_BYTES);
    Element params = null;
    Element explicit = null;
    byte[] bitmap = null;
    int algorithm = HMAC_MD5;
    for (Element element : elements) {
      if (element.type() != DOC_EXT_FIELD) {
        continue;
      }
      var children = parse(bytes, element.valueStart(), element.end(), null);
      Element candidate = unique(children, EXTENDED_PARAMETERS);
      if (candidate == null) {
        continue;
      }
      if (params != null
          || children.stream()
              .anyMatch(
                  child -> child.type() != EXTENDED_PARAMETERS && child.type() != VENDOR_ID)) {
        throw new IllegalArgumentException();
      }
      params = candidate;
      var fields = parse(bytes, params.valueStart(), params.end(), null);
      Element algorithmField = unique(fields, ALGORITHM);
      Element bitmapField = unique(fields, BITMAP);
      if (algorithmField == null || bitmapField == null) {
        throw new IllegalArgumentException();
      }
      requireLength(algorithmField, 1);
      algorithm = Byte.toUnsignedInt(bytes[algorithmField.valueStart()]);
      bitmap = value(bytes, bitmapField);
      explicit = unique(fields, EXPLICIT_DIGEST);
      if (bitmap.length == 0
          || bitmap.length > 32
          || selected(bitmap, PAD)
          || selected(bitmap, CMTS_MIC)
          || selected(bitmap, END_OF_FILE)
          || (explicit != null && selected(bitmap, CM_MIC))) {
        throw new IllegalArgumentException();
      }
      if (fields.stream()
          .anyMatch(
              field ->
                  field.type() != ALGORITHM
                      && field.type() != BITMAP
                      && field.type() != EXPLICIT_DIGEST)) {
        return new Result(
            Status.CANNOT_VERIFY,
            Status.CANNOT_VERIFY,
            Status.CANNOT_VERIFY,
            Reason.UNSUPPORTED_ALGORITHM);
      }
    }
    var cmInput = new ByteArrayOutputStream();
    for (Element element : elements) {
      if (element.type() != CM_MIC && element.type() != CMTS_MIC) {
        append(cmInput, bytes, element);
      }
    }
    Status cmStatus = compare(bytes, cm, DocsisMicRules.md5(cmInput.toByteArray()));
    boolean implicit = params != null && explicit == null;
    Status cmtsStatus = Status.NOT_PRESENT;
    Reason reason = Reason.NONE;
    if (!implicit && cmts != null) {
      requireLength(cmts, MD5_BYTES);
      if (legacy.length == 0) {
        cmtsStatus = Status.CANNOT_VERIFY;
        reason = Reason.MISSING_SECRET;
      } else {
        var input = new ByteArrayOutputStream();
        for (Element element :
            DocsisMicRules.orderLegacyParameters(elements, Element::type, profile.coverage)) {
          append(input, bytes, element);
        }
        cmtsStatus = compare(bytes, cmts, DocsisMicRules.hmac(legacy, input.toByteArray()));
      }
    }
    if (params == null) {
      return new Result(cmStatus, cmtsStatus, Status.NOT_PRESENT, reason);
    }
    if (algorithm != HMAC_MD5 && algorithm != MMH) {
      return new Result(
          cmStatus,
          implicit ? Status.CANNOT_VERIFY : cmtsStatus,
          Status.CANNOT_VERIFY,
          Reason.UNSUPPORTED_ALGORITHM);
    }
    int digestLength = algorithm == MMH ? MMH_BYTES : MD5_BYTES;
    requireLength(explicit, digestLength);
    if (implicit) {
      requireLength(cmts, MD5_BYTES);
    }
    Element stored = implicit ? cmts : explicit;
    Status extendedStatus;
    if (stored == null) {
      extendedStatus = Status.NOT_PRESENT;
    } else if (extended.length == 0) {
      extendedStatus = Status.CANNOT_VERIFY;
      reason = Reason.MISSING_SECRET;
    } else {
      byte[] digestSource = bytes.clone();
      if (explicit != null) {
        Arrays.fill(digestSource, explicit.valueStart(), explicit.end(), (byte) 0);
      }
      var input = new ByteArrayOutputStream();
      // D.2.1: retain physical order and repeated instances; never reserialize TLVs.
      for (Element element : elements) {
        if (selected(bitmap, element.type())) {
          append(input, digestSource, element);
        }
      }
      byte[] digest =
          algorithm == MMH
              ? DocsisMmh.digest(extended, input.toByteArray())
              : DocsisMicRules.hmac(extended, input.toByteArray());
      if (implicit) {
        digest = Arrays.copyOf(digest, MD5_BYTES);
      }
      extendedStatus = compare(bytes, stored, digest);
    }
    return new Result(cmStatus, implicit ? extendedStatus : cmtsStatus, extendedStatus, reason);
  }

  private static List<Element> parse(byte[] bytes, int start, int end, TlvRegistry registry) {
    var elements = new ArrayList<Element>();
    boolean ended = false;
    for (int offset = start; offset < end; ) {
      int type = Byte.toUnsignedInt(bytes[offset]);
      if (registry != null && type == END_OF_FILE) {
        ended = true;
        for (int tail = offset + 1; tail < end; tail++) {
          if (bytes[tail] != 0) {
            throw new IllegalArgumentException();
          }
        }
        break;
      }
      // PAD before the end marker is deliberately unsupported, not silently omitted.
      if (type == PAD) {
        throw new IllegalArgumentException();
      }
      int width = 1;
      if (registry != null) {
        var definition = registry.find(List.of(type));
        if (definition == null) {
          throw new IllegalArgumentException();
        }
        width = definition.lengthBytes();
      }
      if (width < 1 || width > 2 || end - offset < 1 + width) {
        throw new IllegalArgumentException();
      }
      int length = 0;
      for (int index = 1; index <= width; index++) {
        length = (length << 8) | Byte.toUnsignedInt(bytes[offset + index]);
      }
      int valueStart = offset + 1 + width;
      if (length > end - valueStart) {
        throw new IllegalArgumentException();
      }
      elements.add(new Element(type, offset, valueStart, valueStart + length));
      offset = valueStart + length;
    }
    if (registry != null && !ended) {
      throw new IllegalArgumentException();
    }
    return elements;
  }

  private static Element unique(List<Element> elements, int type) {
    Element found = null;
    for (Element element : elements) {
      if (element.type() == type) {
        if (found != null) {
          throw new IllegalArgumentException();
        }
        found = element;
      }
    }
    return found;
  }

  private static void requireLength(Element element, int length) {
    if (element != null && element.end() - element.valueStart() != length) {
      throw new IllegalArgumentException();
    }
  }

  private static byte[] value(byte[] bytes, Element element) {
    return Arrays.copyOfRange(bytes, element.valueStart(), element.end());
  }

  private static boolean selected(byte[] bitmap, int type) {
    return type / 8 < bitmap.length && (bitmap[type / 8] & (128 >>> (type % 8))) != 0;
  }

  private static void append(ByteArrayOutputStream output, byte[] bytes, Element element) {
    output.write(bytes, element.start(), element.end() - element.start());
  }

  private static Status compare(byte[] bytes, Element stored, byte[] calculated) {
    return stored == null
        ? Status.NOT_PRESENT
        : MessageDigest.isEqual(value(bytes, stored), calculated) ? Status.MATCH : Status.MISMATCH;
  }
}
