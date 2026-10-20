/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.configurationfile.DocsisVersion;
import com.comcast.oscar.dictionary.TlvDefinition;
import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Docsis40Compiler extends DocsisCompiler {
  private final TlvRegistry registry = TlvRegistry.forFamily("docsis40");
  private String secret;
  private byte[] finalized;

  private record Element(int type, byte[] value, int lengthBytes) {
    Element(int type, byte[] value) {
      this(type, value, 1);
    }

    byte[] encoded() {
      var output = new ByteArrayOutputStream();
      output.write(type);
      for (int index = lengthBytes - 1; index >= 0; index--)
        output.write(value.length >>> (index * 8));
      output.writeBytes(value);
      return output.toByteArray();
    }
  }

  public Docsis40Compiler(String secret) {
    super(secret, ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE);
    this.secret = secret;
  }

  @Override
  public void setSharedSecretKey(String secret) {
    this.secret = secret;
  }

  @Override
  public void clear() {
    super.clear();
    finalized = null;
  }

  @Override
  public byte[] toByteArray() {
    return finalized == null ? super.toByteArray() : finalized.clone();
  }

  @Override
  public String toString() {
    return finalized == null ? super.toString() : new HexString(finalized).toString();
  }

  @Override
  public String toString(String separation) {
    return finalized == null
        ? super.toString(separation)
        : new HexString(finalized).toString(separation);
  }

  @Override
  public boolean checkSpecification(Boolean report) {
    try {
      parameters();
      return true;
    } catch (IllegalArgumentException | UnsupportedOperationException failure) {
      return false;
    }
  }

  /**
   * Validates the current DOCSIS 4.0 parameters and generates MICs, End and padding. Includes the
   * shared top-level minimum and this compiler's D4-specific checks. Replaces cached finalized
   * bytes; does not write a file or modify the caller's source text.
   *
   * <p>Example: missing upstream TLV 24 prevents finalization; valid parameters produce a complete
   * binary returned by toByteArray().
   *
   * @throws IllegalArgumentException if required settings are missing, values/framing are invalid,
   *     or MIC generation lacks a valid key or required E-MIC coverage
   * @throws UnsupportedOperationException if parameters use an unsupported D4 encoding
   */
  @Override
  public void commit() {
    finalized = null;
    List<Element> parameters = parameters();
    if (parameters.stream().anyMatch(element -> element.type() == 91)) {
      boolean covered =
          parameters.stream()
              .filter(element -> element.type() == 43)
              .flatMap(element -> parse(element.value(), false).stream())
              .filter(child -> child.type() == 6)
              .anyMatch(child -> selected(required(parse(child.value(), false), 2).value(), 91));
      if (!covered) {
        throw new IllegalArgumentException(
            "Low Latency Disable requires E-MIC bitmap coverage for TLV 91");
      }
    }
    finalized = DocsisMicFinalizer.finish(encode(parameters), registry, secret);
    DocsisMinimumRequirements.validateComplete(finalized, DocsisVersion.DOCSIS_4_0);
  }

  private List<Element> parameters() {
    List<Element> parameters = parse(super.toByteArray(), true);
    parameters.removeIf(element -> element.type() == 6 || element.type() == 7);
    validate(parameters, List.of());
    DocsisMinimumRequirements.validateParameters(encode(parameters), DocsisVersion.DOCSIS_4_0);
    Map<Integer, Integer> directions = new HashMap<>();
    Map<Integer, Integer> qosSets = new HashMap<>();
    for (Element element : parameters) {
      if (element.type() != 24 && element.type() != 25) continue;
      var children = parse(element.value(), false);
      int reference = unsigned(required(children, 1).value());
      int qos = unsigned(required(children, 6).value());
      long sustained = optionalUnsigned(children, 8);
      long reserved = optionalUnsigned(children, 10);
      if (sustained != 0 && reserved > sustained) {
        throw new IllegalArgumentException(
            "Minimum reserved traffic rate exceeds maximum sustained rate");
      }
      if (sustained != 0
          && children.stream().anyMatch(child -> child.type() == 9)
          && optionalUnsigned(children, 9) < 1522) {
        throw new IllegalArgumentException("Maximum traffic burst must be at least 1522 bytes");
      }
      if (reference == 0 || qos == 0 || qos > 7) {
        throw new IllegalArgumentException("Invalid service-flow reference or QoS parameter set");
      }
      Integer previousDirection = directions.putIfAbsent(reference, element.type());
      int previousSets = qosSets.getOrDefault(reference, 0);
      if ((previousDirection != null && previousDirection != element.type())
          || (previousSets & qos) != 0) {
        throw new IllegalArgumentException("Conflicting service-flow reference " + reference);
      }
      qosSets.put(reference, previousSets | qos);
    }
    if (qosSets.values().stream().anyMatch(bits -> (bits & 1) == 0)) {
      throw new IllegalArgumentException(
          "Every service flow needs a provisioned QoS parameter set");
    }
    return parameters;
  }

  private void validate(List<Element> elements, List<Integer> parent) {
    Map<Integer, Integer> counts = new HashMap<>();
    for (Element element : elements) {
      List<Integer> path = new ArrayList<>(parent);
      path.add(element.type());
      TlvDefinition definition = registry.find(path);
      if (definition == null)
        throw new IllegalArgumentException("Unverified or excluded I12 TLV " + path);
      int count = counts.merge(element.type(), 1, Integer::sum);
      if (definition.maxInstances() != -1 && count > definition.maxInstances()) {
        throw new IllegalArgumentException("Duplicate I12 TLV " + path);
      }
      if (element.value().length < definition.lengthMin()
          || element.value().length > definition.lengthMax()) {
        throw new IllegalArgumentException("Invalid I12 length at " + path);
      }
      if (!registry.children(path).isEmpty()) {
        var children = parse(element.value(), false, path);
        validate(children, path);
        if (path.equals(List.of(43))
            && (children.getFirst().type() != 8
                || !Arrays.equals(required(children, 8).value(), new byte[] {-1, -1, -1}))) {
          throw new IllegalArgumentException(
              "General DOCSIS extension requires first child VendorId FF:FF:FF");
        }
        if (element.type() == 84 || element.type() == 96) {
          required(children, 1);
          required(children, 2);
          required(children, 3);
        }
      } else if (parent.equals(List.of(17))) {
        long value = new java.math.BigInteger(1, element.value()).longValue();
        int minimum = element.type() == 9 ? 0 : 1;
        int maximum =
            switch (element.type()) {
              case 1, 2 -> 30;
              case 3 -> 6047999;
              case 6 -> 302399;
              case 7 -> 600;
              default -> 10;
            };
        if (value < minimum || value > maximum) {
          throw new IllegalArgumentException("Invalid SEC4 Baseline Privacy parameter at " + path);
        }
      } else if (parent.equals(List.of(24, 40)) || parent.equals(List.of(25, 40))) {
        int value = unsigned(element.value());
        if ((element.type() == 1 && value > 1)
            || (element.type() == 3 && value > 2)
            || (element.type() == 5 && value > 25)) {
          throw new IllegalArgumentException("Reserved AQM value at " + path);
        }
        if (element.type() == 6) {
          byte[] edges = element.value();
          if (edges.length % 2 != 0) {
            throw new IllegalArgumentException(
                "AQM histogram requires two-octet bin edges at " + path);
          }
          int previous = -1;
          for (int offset = 0; offset < edges.length; offset += 2) {
            int edge =
                (Byte.toUnsignedInt(edges[offset]) << 8) | Byte.toUnsignedInt(edges[offset + 1]);
            if (edge <= previous) {
              throw new IllegalArgumentException(
                  "AQM histogram bin edges must increase at " + path);
            }
            previous = edge;
          }
        }
      } else if (parent.equals(List.of(103, 1))) {
        long value = new java.math.BigInteger(1, element.value()).longValue();
        if ((element.type() == 1 && value > 28800)
            || (element.type() == 2 && value > 86400)
            || (element.type() == 3 && value != 1)) {
          throw new IllegalArgumentException("Invalid SSH setting at " + path);
        }
      } else if (parent.equals(List.of(101))) {
        int value = unsigned(element.value());
        if (((element.type() == 1 || element.type() == 4) && (value < 1 || value > 2))
            || (element.type() == 2 && value > 3)) {
          throw new IllegalArgumentException("Reserved SYNC value at " + path);
        }
      } else if (parent.equals(List.of(102))) {
        if (element.type() != 3 && element.value().length != 4 && element.value().length != 16) {
          throw new IllegalArgumentException(
              "SYNC IP address must contain 4 or 16 octets at " + path);
        }
        if (element.type() == 3 && (element.value()[0] & 1) == 0) {
          throw new IllegalArgumentException("SYNC destination MAC address must be multicast");
        }
      } else if (path.size() == 1
          && java.util.Set.of(3, 29, 40, 55, 76, 91, 97).contains(element.type())) {
        if (unsigned(element.value()) > 1)
          throw new IllegalArgumentException("Reserved I12 value at " + path);
      } else if (path.equals(List.of(83))) {
        int value = unsigned(element.value());
        if (value < 1 || value > 2) throw new IllegalArgumentException("Reserved DTP mode");
      } else if (parent.equals(List.of(69))) {
        int maximum = element.type() == 1 ? 1 : 10;
        if (unsigned(element.value()) > maximum)
          throw new IllegalArgumentException("Invalid MAC learning value at " + path);
      } else if (parent.equals(List.of(84))) {
        int maximum =
            switch (element.type()) {
              case 1 -> 4;
              case 2 -> 1;
              default -> 2;
            };
        if (unsigned(element.value()) > maximum)
          throw new IllegalArgumentException("Reserved I12 diplexer value at " + path);
      } else if ((parent.equals(List.of(24)) || parent.equals(List.of(25)))
          && element.type() == 7
          && unsigned(element.value()) > 7) {
        throw new IllegalArgumentException("Reserved traffic priority at " + path);
      } else if ((parent.equals(List.of(24)) || parent.equals(List.of(25)))
          && element.type() == 41
          && unsigned(element.value()) > 3) {
        throw new IllegalArgumentException("Reserved data rate unit at " + path);
      } else if (definition.codec().equals("string_null_terminated")) {
        byte[] value = element.value();
        if (value[value.length - 1] != 0)
          throw new IllegalArgumentException("Missing string terminator at " + path);
        for (int index = 0; index < value.length - 1; index++) {
          if (value[index] <= 0)
            throw new IllegalArgumentException("Invalid ASCII string at " + path);
        }
      }
    }
  }

  private static Element required(List<Element> elements, int type) {
    return elements.stream()
        .filter(element -> element.type() == type)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Missing required I12 TLV " + type));
  }

  private static long optionalUnsigned(List<Element> elements, int type) {
    return elements.stream()
        .filter(element -> element.type() == type)
        .mapToLong(element -> new java.math.BigInteger(1, element.value()).longValue())
        .findFirst()
        .orElse(0);
  }

  private static List<Element> parse(byte[] bytes, boolean file) {
    return parse(bytes, file, List.of());
  }

  private static List<Element> parse(byte[] bytes, boolean file, List<Integer> parent) {
    List<Element> elements = new ArrayList<>();
    int offset = 0;
    while (offset < bytes.length) {
      int type = Byte.toUnsignedInt(bytes[offset++]);
      if (file && type == 0) continue;
      if (file && type == 255) {
        while (offset < bytes.length) {
          if (bytes[offset++] != 0)
            throw new IllegalArgumentException("Non-padding octet after I12 End marker");
        }
        break;
      }
      List<Integer> path = new ArrayList<>(parent);
      path.add(type);
      TlvDefinition definition = TlvRegistry.forFamily("docsis40").find(path);
      int width = definition == null ? 1 : definition.lengthBytes();
      if (width > bytes.length - offset)
        throw new IllegalArgumentException("Truncated I12 TLV header at " + path);
      int length = 0;
      for (int index = 0; index < width; index++)
        length = (length << 8) | Byte.toUnsignedInt(bytes[offset++]);
      if (length > bytes.length - offset)
        throw new IllegalArgumentException("Truncated I12 TLV value");
      elements.add(new Element(type, Arrays.copyOfRange(bytes, offset, offset + length), width));
      offset += length;
    }
    return elements;
  }

  private static int unsigned(byte[] bytes) {
    int value = 0;
    for (byte octet : bytes) value = (value << 8) | Byte.toUnsignedInt(octet);
    return value;
  }

  private static byte[] encode(List<Element> elements) {
    var output = new ByteArrayOutputStream();
    elements.forEach(element -> output.writeBytes(element.encoded()));
    return output.toByteArray();
  }

  private static boolean selected(byte[] bitmap, int type) {
    return type / 8 < bitmap.length && (bitmap[type / 8] & (128 >>> (type % 8))) != 0;
  }
}
