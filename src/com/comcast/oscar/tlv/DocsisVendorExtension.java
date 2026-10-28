/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import static com.comcast.oscar.cablelabsdefinitions.DocsisTlvTypes.*;

import com.comcast.oscar.datatype.DataTypeDictionaryReference;
import com.comcast.oscar.dictionary.Dictionary;
import java.util.HexFormat;
import java.util.Set;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/** Vendor-scoped DOCSIS extension handling (MULPI 3.1 I25 / 4.0 I12 C.1.1.18). */
public final class DocsisVendorExtension {
  private static final int VENDOR_ID_BYTES = 3;
  private static final String GENERAL_VENDOR_ID = "ffffff";
  private static final String RAW_PREFIX = "RawTlv";
  // Canonical decimal identifiers, e.g. RawTlv242; the wire range is checked separately.
  private static final Pattern RAW_NAME = Pattern.compile("RawTlv(0|[1-9][0-9]{0,2})");
  private static final int HEADER_BYTES = 2;
  private static final Set<Integer> EXTENSION_PARENTS =
      Set.of(US_PKT_CLASS, DS_PKT_CLASS, US_SERVICE_FLOW, DS_SERVICE_FLOW, US_DROP_PKT_CLASS);

  private DocsisVendorExtension() {}

  /**
   * Identifies supported top-level or classifier/service-flow DOCSIS extension locations.
   *
   * @param definition registry-shaped dictionary node, never null
   * @return whether vendor-scoped decoding applies at this path
   */
  public static boolean applies(JSONObject definition) {
    JSONArray path = definition.getJSONArray(Dictionary.PARENT_TYPE_LIST);
    return definition.getInt(Dictionary.TYPE) == DOC_EXT_FIELD
        && (path.length() == 2
            || (path.length() == 3 && EXTENSION_PARENTS.contains(path.getInt(1))));
  }

  /**
   * Validates the leading Vendor ID in a bounded binary extension.
   *
   * @param encoded complete TLV 43 including its two-byte header
   * @return true for the general-extension Vendor ID, false for a private vendor
   * @throws IllegalArgumentException if the Vendor ID is missing, misplaced, or malformed
   */
  public static boolean isGeneral(byte[] encoded) {
    return isGeneralAt(encoded, HEADER_BYTES);
  }

  /**
   * Validates an opaque nested extension payload without interpreting vendor children.
   *
   * @param payload extension value bytes, excluding the outer TLV header
   * @throws IllegalArgumentException for missing/duplicate VendorId or truncated child framing
   */
  public static void validatePayload(byte[] payload) {
    isGeneralAt(payload, 0);
    for (int offset = 0; offset < payload.length; ) {
      if (payload.length - offset < HEADER_BYTES) {
        throw new IllegalArgumentException("Truncated vendor child header");
      }
      int end = offset + HEADER_BYTES + Byte.toUnsignedInt(payload[offset + 1]);
      if (end > payload.length) {
        throw new IllegalArgumentException("Vendor child exceeds extension length");
      }
      if (offset != 0 && Byte.toUnsignedInt(payload[offset]) == VENDOR_ID_ENC) {
        throw new IllegalArgumentException("Duplicate VendorId in DOCSIS extension");
      }
      offset = end;
    }
  }

  /**
   * Creates an instance-only opaque definition; never adds vendor identifiers to the registry.
   *
   * @param parent supported extension dictionary node
   * @param name canonical RawTlv followed by a decimal wire identifier
   * @return byte-array definition with a complete numeric path, or null for another name
   * @throws IllegalArgumentException if the raw identifier is invalid or aliases VendorId
   */
  public static JSONObject rawDefinition(JSONObject parent, String name) {
    if (!applies(parent) || !name.startsWith(RAW_PREFIX)) {
      return null;
    }
    var match = RAW_NAME.matcher(name);
    if (!match.matches()) {
      throw new IllegalArgumentException("Invalid raw extension identifier");
    }
    int type = Integer.parseInt(match.group(1));
    if (type > TlvBuilder.MAX_TLV_TYPE || type == VENDOR_ID_ENC) {
      throw new IllegalArgumentException("Invalid raw extension identifier");
    }
    JSONArray path =
        new JSONArray(parent.getJSONArray(Dictionary.PARENT_TYPE_LIST).toString()).put(type);
    return new JSONObject()
        .put(Dictionary.TYPE, type)
        .put(Dictionary.TLV_NAME, name)
        .put(Dictionary.DATA_TYPE, DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY)
        .put(Dictionary.ARE_SUBTYPES, false)
        .put(Dictionary.LENGTH_MIN, 0)
        .put(Dictionary.LENGTH_MAX, TlvBuilder.MAX_TLV_LENGTH)
        .put(Dictionary.BYTE_LENGTH, 1)
        .put(Dictionary.PARENT_TYPE_LIST, path)
        .put(
            Dictionary.TLV_DESCRIPTION,
            "Opaque DOCSIS extension bytes; vendor semantics unvalidated")
        .put(Dictionary.SUPPORTED_VERSIONS, parent.get(Dictionary.SUPPORTED_VERSIONS));
  }

  /**
   * Recognizes a dynamically decoded opaque child without consulting a global registry.
   *
   * @param definition dictionary instance, never null
   * @return true for a canonical opaque child name
   */
  public static boolean isRaw(JSONObject definition) {
    return RAW_NAME.matcher(definition.getString(Dictionary.TLV_NAME)).matches();
  }

  /**
   * Checks populated extension instances before assembly; valueless templates remain permitted.
   *
   * @param definitions document-instance dictionary array, never null
   * @throws IllegalArgumentException for malformed vendor scope or opaque hexadecimal values
   */
  public static void validate(JSONArray definitions) {
    for (Object entry : definitions) {
      JSONObject node = (JSONObject) entry;
      if (!node.getBoolean(Dictionary.ARE_SUBTYPES)) {
        if (applies(node) && node.has(Dictionary.VALUE)) {
          validatePayload(hex(node.getString(Dictionary.VALUE)));
        }
        if (isRaw(node) && node.has(Dictionary.VALUE)) {
          hex(node.getString(Dictionary.VALUE));
        }
        continue;
      }
      JSONArray children = node.getJSONArray(Dictionary.SUBTYPE_ARRAY);
      if (applies(node)) {
        JSONArray populated = new JSONArray();
        for (Object child : children) {
          if (hasValue((JSONObject) child)) {
            populated.put(child);
          }
        }
        if (!populated.isEmpty()) {
          JSONObject first = populated.getJSONObject(0);
          if (first.getInt(Dictionary.TYPE) != VENDOR_ID_ENC
              || hex(first.getString(Dictionary.VALUE)).length != VENDOR_ID_BYTES) {
            throw new IllegalArgumentException(
                "DOCSIS extension requires a three-byte VendorId first");
          }
          boolean general =
              HexFormat.of()
                  .formatHex(hex(first.getString(Dictionary.VALUE)))
                  .equals(GENERAL_VENDOR_ID);
          for (int index = 1; index < populated.length(); index++) {
            JSONObject child = populated.getJSONObject(index);
            if (child.getInt(Dictionary.TYPE) == VENDOR_ID_ENC) {
              throw new IllegalArgumentException("Duplicate VendorId in DOCSIS extension");
            }
            if (!general && !isRaw(child)) {
              throw new IllegalArgumentException(
                  "Private vendor children require RawTlv identifiers");
            }
          }
        }
      }
      validate(children);
    }
  }

  private static boolean isGeneralAt(byte[] encoded, int vendorOffset) {
    if (encoded.length - vendorOffset < HEADER_BYTES + VENDOR_ID_BYTES
        || Byte.toUnsignedInt(encoded[vendorOffset]) != VENDOR_ID_ENC
        || Byte.toUnsignedInt(encoded[vendorOffset + 1]) != VENDOR_ID_BYTES) {
      throw new IllegalArgumentException("DOCSIS extension requires a three-byte VendorId first");
    }
    return HexFormat.of()
        .formatHex(
            encoded, vendorOffset + HEADER_BYTES, vendorOffset + HEADER_BYTES + VENDOR_ID_BYTES)
        .equals(GENERAL_VENDOR_ID);
  }

  private static boolean hasValue(JSONObject node) {
    if (node.has(Dictionary.VALUE)) {
      return true;
    }
    for (Object child : node.optJSONArray(Dictionary.SUBTYPE_ARRAY, new JSONArray())) {
      if (hasValue((JSONObject) child)) {
        return true;
      }
    }
    return false;
  }

  private static byte[] hex(String text) {
    String compact = text.strip();
    if (compact.startsWith("[") && compact.endsWith("]")) {
      compact = compact.substring(1, compact.length() - 1);
    }
    compact = compact.replace(":", "").replace(" ", "");
    if (!compact.matches("(?:[0-9a-fA-F]{2})*")
        || compact.length() / 2 > TlvBuilder.MAX_TLV_LENGTH) {
      throw new IllegalArgumentException("Invalid opaque extension hexadecimal value");
    }
    return HexFormat.of().parseHex(compact);
  }
}
