/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import com.comcast.oscar.dictionary.TlvRegistry;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** Shared generation per MULPI 3.1 I21 and 4.0 I12, D.1.3 and D.2.1. */
final class DocsisMicFinalizer {
  private DocsisMicFinalizer() {}

  private record Element(int type, byte[] value, int lengthBytes) {
    Element(int type, byte[] value) {
      this(type, value, 1);
    }

    byte[] encoded() {
      var output = new ByteArrayOutputStream();
      output.write(type);
      for (int index = lengthBytes - 1; index >= 0; index--) {
        output.write(value.length >>> (index * 8));
      }
      output.writeBytes(value);
      return output.toByteArray();
    }
  }

  static byte[] finish(byte[] binary, TlvRegistry registry, String secret) {
    return finish(binary, registry, secret, DocsisMicRules.Coverage.DOCSIS_31_AND_40);
  }

  static byte[] finish(
      byte[] binary, TlvRegistry registry, String secret, DocsisMicRules.Coverage coverage) {
    if (secret == null || secret.isEmpty()) {
      throw new IllegalArgumentException("MIC generation requires a nonempty CMTS key");
    }
    List<Element> parameters = parse(binary, true, registry);
    parameters.removeIf(element -> element.type() == 6 || element.type() == 7);
    Element extension = null;
    for (Element element : parameters) {
      if (element.type() == 43
          && parse(element.value(), false).stream().anyMatch(child -> child.type() == 6)) {
        if (extension != null) {
          throw new IllegalArgumentException("Duplicate Extended CMTS MIC");
        }
        extension = element;
      }
    }
    byte[] bitmap = null;
    boolean explicit = false;
    int algorithm = 1;
    if (extension != null) {
      var children = parse(extension.value(), false);
      if (children.stream().anyMatch(child -> child.type() != 6 && child.type() != 8)
          || children.getFirst().type() != 8
          || !Arrays.equals(required(children, 8).value(), new byte[] {-1, -1, -1})) {
        throw new IllegalArgumentException(
            "Extended MIC requires VendorId FF:FF:FF and no other subtypes");
      }
      var fields = parse(required(children, 6).value(), false);
      if (fields.stream().anyMatch(field -> field.type() < 1 || field.type() > 3)
          || required(fields, 1).value().length != 1) {
        throw new IllegalArgumentException("Invalid extended MIC fields");
      }
      algorithm = unsigned(required(fields, 1).value());
      if (algorithm != 1 && algorithm != 2) {
        throw new UnsupportedOperationException(
            "Unverified vendor or reserved E-MIC algorithm " + algorithm);
      }
      bitmap = required(fields, 2).value();
      if (bitmap.length == 0 || bitmap.length > 32) {
        throw new IllegalArgumentException("Extended MIC bitmap must contain 1-32 octets");
      }
      explicit = fields.stream().anyMatch(field -> field.type() == 3);
      if ((bitmap[0] & 128) != 0
          || selected(bitmap, 7)
          || selected(bitmap, 255)
          || (explicit && selected(bitmap, 6))) {
        throw new IllegalArgumentException(
            "Invalid E-MIC bitmap: reserved bit 0 or self-referential MIC selection");
      }
      parameters.sort(Comparator.comparingInt(Element::type));
      if (explicit) {
        int digestLength = algorithm == 2 ? 8 : 16;
        if (required(fields, 3).value().length != digestLength) {
          throw new IllegalArgumentException(
              "ExplicitDigest must contain " + digestLength + " octets for algorithm " + algorithm);
        }
        extension = replaceDigest(extension, new byte[digestLength]);
        replaceExtension(parameters, extension);
        byte[] digest = extendedDigest(select(parameters, bitmap), algorithm, secret);
        replaceExtension(parameters, replaceDigest(extension, digest));
      }
    }
    parameters.add(new Element(6, DocsisMicRules.md5(encode(parameters))));
    byte[] cmts;
    if (bitmap != null && !explicit) {
      cmts = Arrays.copyOf(extendedDigest(select(parameters, bitmap), algorithm, secret), 16);
    } else {
      cmts =
          DocsisMicRules.hmac(
              secret.getBytes(StandardCharsets.UTF_8),
              encode(DocsisMicRules.orderLegacyParameters(parameters, Element::type, coverage)));
    }
    parameters.add(new Element(7, cmts));
    var output = new ByteArrayOutputStream();
    output.writeBytes(encode(parameters));
    output.write(255);
    while (output.size() % 4 != 0) {
      output.write(0);
    }
    return output.toByteArray();
  }

  private static List<Element> parse(byte[] bytes, boolean file) {
    return parse(bytes, file, null);
  }

  private static List<Element> parse(byte[] bytes, boolean file, TlvRegistry registry) {
    List<Element> elements = new ArrayList<>();
    int offset = 0;
    while (offset < bytes.length) {
      int type = Byte.toUnsignedInt(bytes[offset++]);
      if (file && type == 255) {
        while (offset < bytes.length) {
          if (bytes[offset++] != 0) {
            throw new IllegalArgumentException("Non-padding octet after End marker");
          }
        }
        break;
      }
      if (file && type == 0) {
        throw new IllegalArgumentException("PAD before End is unsupported");
      }
      var definition = registry == null ? null : registry.find(List.of(type));
      int width = definition == null ? 1 : definition.lengthBytes();
      if (bytes.length - offset < width) {
        throw new IllegalArgumentException("Truncated TLV header");
      }
      int length = 0;
      for (int index = 0; index < width; index++) {
        length = (length << 8) | Byte.toUnsignedInt(bytes[offset++]);
      }
      if (length > bytes.length - offset) {
        throw new IllegalArgumentException("Truncated TLV value");
      }
      elements.add(new Element(type, Arrays.copyOfRange(bytes, offset, offset + length), width));
      offset += length;
    }
    return elements;
  }

  private static Element required(List<Element> elements, int type) {
    var matches = elements.stream().filter(element -> element.type() == type).toList();
    if (matches.size() != 1) {
      throw new IllegalArgumentException("Expected one MIC parameter " + type);
    }
    return matches.getFirst();
  }

  private static int unsigned(byte[] bytes) {
    int value = 0;
    for (byte octet : bytes) {
      value = (value << 8) | Byte.toUnsignedInt(octet);
    }
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

  private static byte[] select(List<Element> elements, byte[] bitmap) {
    return encode(elements.stream().filter(element -> selected(bitmap, element.type())).toList());
  }

  private static Element replaceDigest(Element extension, byte[] digest) {
    var outer = parse(extension.value(), false);
    var inner = parse(required(outer, 6).value(), false);
    inner.replaceAll(field -> field.type() == 3 ? new Element(3, digest) : field);
    outer.replaceAll(field -> field.type() == 6 ? new Element(6, encode(inner)) : field);
    return new Element(43, encode(outer));
  }

  private static void replaceExtension(List<Element> parameters, Element extension) {
    parameters.replaceAll(
        element ->
            element.type() == 43
                    && parse(element.value(), false).stream().anyMatch(child -> child.type() == 6)
                ? extension
                : element);
  }

  private static byte[] extendedDigest(byte[] bytes, int algorithm, String secret) {
    return algorithm == 2
        ? DocsisMmh.digest(secret.getBytes(StandardCharsets.UTF_8), bytes)
        : DocsisMicRules.hmac(secret.getBytes(StandardCharsets.UTF_8), bytes);
  }
}
