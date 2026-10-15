package com.comcast.oscar.compiler;

import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.dictionary.TlvDefinition;
import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Docsis40Compiler extends DocsisCompiler {
  private static final List<Integer> LEGACY_MIC_ORDER =
      List.of(1, 2, 3, 17, 43, 6, 18, 19, 20, 59, 22, 23, 24, 25, 28, 29, 35, 36, 37, 40);
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

  @Override
  public void commit() {
    finalized = null;
    if (secret == null || secret.isEmpty()) {
      throw new IllegalArgumentException("DOCSIS 4.0 compilation requires a nonempty CMTS key");
    }
    List<Element> parameters = parameters();
    Element extension = null;
    for (Element element : parameters) {
      if (element.type() == 43) {
        if (extension != null) throw new IllegalArgumentException("Duplicate Extended CMTS MIC");
        extension = element;
      }
    }
    byte[] bitmap = null;
    boolean explicit = false;
    int algorithm = 1;
    if (extension != null) {
      var fields = parse(required(parse(extension.value(), false), 6).value(), false);
      algorithm = unsigned(required(fields, 1).value());
      if (algorithm != 1 && algorithm != 2) {
        throw new UnsupportedOperationException(
            "Unverified vendor or reserved E-MIC algorithm " + algorithm);
      }
      bitmap = required(fields, 2).value();
      explicit = fields.stream().anyMatch(field -> field.type() == 3);
      if ((bitmap[0] & 128) != 0 || selected(bitmap, 7) || (explicit && selected(bitmap, 6))) {
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
        byte[] digest = extendedDigest(select(parameters, bitmap), algorithm);
        replaceExtension(parameters, replaceDigest(extension, digest));
      }
    }
    if (parameters.stream().anyMatch(element -> element.type() == 91)
        && (bitmap == null || !selected(bitmap, 91))) {
      throw new IllegalArgumentException(
          "Low Latency Disable requires E-MIC bitmap coverage for TLV 91");
    }
    parameters.add(new Element(6, md5(encode(parameters))));
    byte[] cmts;
    if (bitmap != null && !explicit) {
      cmts = Arrays.copyOf(extendedDigest(select(parameters, bitmap), algorithm), 16);
    } else {
      var selected = new ByteArrayOutputStream();
      for (int type : LEGACY_MIC_ORDER) {
        for (Element element : parameters) {
          if (element.type() == type) selected.writeBytes(element.encoded());
        }
      }
      cmts = hmac(selected.toByteArray());
    }
    parameters.add(new Element(7, cmts));
    var output = new ByteArrayOutputStream();
    output.writeBytes(encode(parameters));
    output.write(255);
    while (output.size() % 4 != 0) output.write(0);
    finalized = output.toByteArray();
  }

  private List<Element> parameters() {
    List<Element> parameters = parse(super.toByteArray(), true);
    parameters.removeIf(element -> element.type() == 6 || element.type() == 7);
    validate(parameters, List.of());
    required(parameters, 3);
    required(parameters, 24);
    required(parameters, 25);
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
    parameters.replaceAll(element -> element.type() == 43 ? extension : element);
  }

  private byte[] hmac(byte[] bytes) {
    try {
      Mac mac = Mac.getInstance("HmacMD5");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacMD5"));
      return mac.doFinal(bytes);
    } catch (GeneralSecurityException failure) {
      throw new IllegalStateException("HMAC-MD5 unavailable", failure);
    }
  }

  private byte[] extendedDigest(byte[] bytes, int algorithm) {
    return algorithm == 2
        ? DocsisMmh.digest(secret.getBytes(StandardCharsets.UTF_8), bytes)
        : hmac(bytes);
  }

  private static byte[] md5(byte[] bytes) {
    try {
      return MessageDigest.getInstance("MD5").digest(bytes);
    } catch (GeneralSecurityException failure) {
      throw new IllegalStateException("MD5 unavailable", failure);
    }
  }
}
