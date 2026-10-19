/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.tlv;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.dictionary.TlvDefinition;
import com.comcast.oscar.dictionary.TlvRegistry;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.smi.VariableBinding;

/** Bounded DPoG TLV decoding, shared by compilation and text export. */
public final class DPoGTlvCodec {
  private static final TlvRegistry REGISTRY = TlvRegistry.forFamily("dpog");
  private static final int SERVICE_FLOW_REFERENCE = 1;
  private static final int QOS_PARAMETER_SET = 6;
  private static final int PROVISIONED_ADMITTED_ACTIVE = 7;

  /** An ordered TLV with independent encoded bytes and decoded child boundaries. */
  public record Element(TlvDefinition definition, byte[] encoded, List<Element> children) {
    /** Copies bytes and children so callers cannot mutate the parsed configuration. */
    public Element {
      encoded = encoded.clone();
      children = List.copyOf(children);
    }

    /**
     * @return independent type/length/value bytes for this element
     */
    @Override
    public byte[] encoded() {
      return encoded.clone();
    }
  }

  private DPoGTlvCodec() {}

  /**
   * Parses a DPoG TLV stream, retaining encounter order and rejecting unsupported content.
   * End/padding bytes are accepted only at file level and omitted from the returned list.
   *
   * @param binary raw bytes, with or without final end marker
   * @return ordered elements; for example 03 01 01 yields NetworkAccess=1
   * @throws IllegalArgumentException for malformed lengths or values
   * @throws UnsupportedOperationException for unknown TLVs or unrepresentable repeated children
   */
  public static List<Element> parse(byte[] binary) {
    return parse(binary, List.of());
  }

  /**
   * Reorders a decoded dictionary to match the original binary's child order.
   *
   * @param dictionary decoded values for the supplied element
   * @param element original parsed TLV
   * @return the same dictionary, with child arrays in binary order
   */
  public static JSONObject orderDictionary(JSONObject dictionary, Element element) {
    if (!element.children().isEmpty()) {
      JSONArray existing = dictionary.getJSONArray(Dictionary.SUBTYPE_ARRAY);
      JSONArray ordered = new JSONArray();
      for (Element child : element.children()) {
        JSONObject decodedChild = null;
        for (int index = 0; index < existing.length(); index++) {
          JSONObject candidate = existing.getJSONObject(index);
          if (candidate.getInt(Dictionary.TYPE) == child.definition().type()) {
            decodedChild = candidate;
            break;
          }
        }
        if (decodedChild == null) {
          throw new IllegalArgumentException(
              "Missing decoded DPoG child " + child.definition().path());
        }
        ordered.put(orderDictionary(decodedChild, child));
      }
      dictionary.put(Dictionary.SUBTYPE_ARRAY, ordered);
    }
    return dictionary;
  }

  private static List<Element> parse(byte[] binary, List<Integer> parentPath) {
    List<Element> elements = new ArrayList<>();
    Set<Integer> seenTypes = new HashSet<>();
    int offset = 0;
    while (offset < binary.length) {
      int start = offset;
      int tlvType = Byte.toUnsignedInt(binary[offset++]);
      if (parentPath.isEmpty() && tlvType == Constants.END_OF_FILE) {
        while (offset < binary.length) {
          if (binary[offset++] != Constants.PAD) {
            throw new IllegalArgumentException("Non-padding byte after DPoG end marker");
          }
        }
        break;
      }
      List<Integer> path = new ArrayList<>(parentPath);
      path.add(tlvType);
      TlvDefinition definition = REGISTRY.find(path);
      if (definition == null || tlvType == Constants.PAD) {
        throw new UnsupportedOperationException("Unsupported DPoG TLV " + path);
      }
      if (!seenTypes.add(tlvType)
          && (!parentPath.isEmpty()
              || tlvType == Constants.NETWORK_ACC_CTRL_OBJ
              || tlvType == Constants.CM_MIC
              || tlvType == Constants.CMTS_MIC)) {
        throw new UnsupportedOperationException(
            "Repeated DPoG child cannot be exported losslessly: " + path);
      }
      int lengthWidth = definition.lengthBytes();
      if (binary.length - offset < lengthWidth) {
        throw new IllegalArgumentException("Truncated DPoG header " + path);
      }
      int valueLength = 0;
      for (int lengthIndex = 0; lengthIndex < lengthWidth; lengthIndex++) {
        valueLength = (valueLength << Byte.SIZE) | Byte.toUnsignedInt(binary[offset++]);
      }
      if (valueLength > binary.length - offset) {
        throw new IllegalArgumentException("Truncated DPoG value " + path);
      }
      byte[] value = Arrays.copyOfRange(binary, offset, offset + valueLength);
      List<Element> children = REGISTRY.children(path).isEmpty() ? List.of() : parse(value, path);
      if (REGISTRY.children(path).isEmpty()) {
        if (definition.lengthMin() > 0 && valueLength < definition.lengthMin()
            || definition.lengthMax() > 0 && valueLength > definition.lengthMax()) {
          throw new IllegalArgumentException("Invalid DPoG length for " + path);
        }
        validateValue(path, value);
      }
      offset += valueLength;
      elements.add(new Element(definition, Arrays.copyOfRange(binary, start, offset), children));
    }
    return List.copyOf(elements);
  }

  private static void validateValue(List<Integer> path, byte[] value) {
    if (path.equals(List.of(Constants.NETWORK_ACC_CTRL_OBJ))
        && (value.length != 1 || Byte.toUnsignedInt(value[0]) > 1)) {
      throw new IllegalArgumentException("DPoG NetworkAccess must be 0 or 1");
    }
    if (path.size() == 2
        && (path.getFirst().equals(Constants.US_SERVICE_FLOW)
            || path.getFirst().equals(Constants.DS_SERVICE_FLOW))) {
      if (path.getLast() == QOS_PARAMETER_SET
          && (value.length != 1 || value[0] != PROVISIONED_ADMITTED_ACTIVE)) {
        throw new IllegalArgumentException("DPoG QoS parameter set must be 0x07 (Table 10)");
      }
      if (path.getLast() == SERVICE_FLOW_REFERENCE
          && Arrays.equals(value, new byte[value.length])) {
        throw new IllegalArgumentException("DPoG service-flow reference must be nonzero");
      }
    }
    if (path.equals(List.of(Constants.SNMP_MIB_OBJ))) {
      try {
        BERInputStream input = new BERInputStream(ByteBuffer.wrap(value));
        new VariableBinding().decodeBER(input);
        if (input.available() != 0) {
          throw new IllegalArgumentException("Trailing data in DPoG SNMP binding");
        }
      } catch (IOException exception) {
        throw new IllegalArgumentException("Invalid DPoG SNMP binding", exception);
      }
    }
  }
}
