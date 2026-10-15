package com.comcast.oscar.dictionary;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/** A startup-loaded symbol table, independent of SQLite and configuration instance values. */
public final class TlvRegistry {
  private static final Set<String> CODECS =
      Set.of(
          "integer",
          "string",
          "string_bits",
          "string_null_terminated",
          "byte_array",
          "double_byte_array",
          "multi_tlv_byte_array",
          "oid",
          "oid_asn1_object6",
          "byte_array_ipv4_address",
          "byte_array_ipv6_address",
          "byte",
          "mac_address",
          "transport_address_ipv4_address",
          "transport_address_ipv6_address",
          "byte_array_inet_transport_address",
          "byte_array_inet_address");

  private record Catalog(
      int formatVersion,
      String family,
      String edition,
      String sourceSha256,
      List<TlvDefinition> definitions) {}

  private final Map<List<Integer>, TlvDefinition> definitions;
  private final Map<List<Integer>, List<TlvDefinition>> children;

  private TlvRegistry(List<TlvDefinition> entries, String source) {
    Map<List<Integer>, TlvDefinition> byPath = new LinkedHashMap<>();
    Map<List<Integer>, List<TlvDefinition>> byParent = new HashMap<>();
    Map<List<Integer>, Set<String>> names = new HashMap<>();
    for (TlvDefinition entry : entries) {
      if (entry.path().isEmpty()
          || entry.path().stream().anyMatch(n -> n < 0 || n > 255)
          || entry.name() == null
          || entry.name().isBlank()
          || entry.description() == null
          || entry.supportedVersion() == null
          || !CODECS.contains(entry.codec())
          || entry.lengthMin() < 0
          || entry.lengthMax() < entry.lengthMin()
          || entry.lengthBytes() < 0
          || entry.lengthBytes() > 2
          || entry.maxInstances() == 0
          || entry.maxInstances() < -1) {
        throw new IllegalArgumentException(source + ": invalid definition at " + entry.path());
      }
      if (byPath.putIfAbsent(entry.path(), entry) != null) {
        throw new IllegalArgumentException(source + ": duplicate path " + entry.path());
      }
      if (!names
          .computeIfAbsent(entry.parent(), k -> new java.util.HashSet<>())
          .add(entry.name().toLowerCase(java.util.Locale.ROOT))) {
        throw new IllegalArgumentException(source + ": duplicate sibling name at " + entry.path());
      }
      byParent.computeIfAbsent(entry.parent(), k -> new ArrayList<>()).add(entry);
    }
    for (TlvDefinition entry : entries) {
      if (!entry.parent().isEmpty() && !byPath.containsKey(entry.parent())) {
        throw new IllegalArgumentException(source + ": missing parent for " + entry.path());
      }
    }
    // Preserve catalog order: the legacy codecs serialize templates in this order.
    byParent.replaceAll((key, value) -> List.copyOf(value));
    definitions = Map.copyOf(byPath);
    children = Map.copyOf(byParent);
  }

  /** Load once for each supported family; failures prevent use of a partial registry. */
  private static class Catalogs {
    private static final Map<String, TlvRegistry> ALL =
        Map.of("docsis", load("docsis"), "packetcable", load("packetcable"), "dpoe", load("dpoe"));
  }

  public static TlvRegistry forFamily(String family) {
    TlvRegistry registry = Catalogs.ALL.get(family);
    if (registry == null) throw new IllegalArgumentException("Unknown TLV family: " + family);
    return registry;
  }

  private static TlvRegistry load(String family) {
    String directory = System.getProperty("oscar.tlv.directory");
    String source =
        directory == null
            ? "/tlv-registry/" + family + ".json"
            : Path.of(directory, family + ".json").toString();
    try (InputStream input =
        directory == null
            ? TlvRegistry.class.getResourceAsStream(source)
            : Files.newInputStream(Path.of(source))) {
      if (input == null) throw new IOException("Missing catalog " + source);
      return read(input, family, source);
    } catch (IOException | IllegalArgumentException e) {
      throw new IllegalStateException("Cannot load TLV catalog " + source, e);
    }
  }

  static TlvRegistry read(InputStream input, String family, String source) throws IOException {
    ObjectMapper mapper =
        new ObjectMapper()
            .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
    Catalog catalog = mapper.readValue(input, Catalog.class);
    if (catalog.formatVersion() != 1
        || !family.equals(catalog.family())
        || catalog.edition().isBlank()
        || catalog.definitions().isEmpty()) {
      throw new IllegalArgumentException(source + ": invalid catalog header");
    }
    return new TlvRegistry(catalog.definitions(), source);
  }

  public TlvDefinition find(List<Integer> path) {
    return definitions.get(path);
  }

  public List<TlvDefinition> children(List<Integer> parent) {
    return children.getOrDefault(parent, List.of());
  }

  /** Fresh mutable compatibility template for the existing parser and codecs. */
  public JSONObject template(List<Integer> path) {
    TlvDefinition definition = find(path);
    if (definition == null) return new JSONObject();
    List<TlvDefinition> nested = children(path);
    JSONArray parents = new JSONArray().put(-1);
    path.forEach(parents::put);
    JSONObject result =
        new JSONObject()
            .put(Dictionary.TYPE, Integer.toString(definition.type()))
            .put(Dictionary.TLV_NAME, definition.name())
            .put(Dictionary.TLV_DESCRIPTION, definition.description())
            .put(Dictionary.DATA_TYPE, definition.codec())
            .put(Dictionary.LENGTH_MIN, definition.lengthMin())
            .put(Dictionary.LENGTH_MAX, definition.lengthMax())
            .put(Dictionary.BYTE_LENGTH, Integer.toString(definition.lengthBytes()))
            .put(Dictionary.SUPPORTED_VERSIONS, definition.supportedVersion())
            .put(Dictionary.PARENT_TYPE_LIST, parents)
            .put(Dictionary.ARE_SUBTYPES, !nested.isEmpty());
    if (!nested.isEmpty()) {
      JSONArray array = new JSONArray();
      nested.forEach(child -> array.put(template(child.path())));
      result.put(Dictionary.SUBTYPE_ARRAY, array);
    }
    return result;
  }
}
