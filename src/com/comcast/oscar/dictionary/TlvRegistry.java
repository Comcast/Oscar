/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
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
          "unsigned_integer",
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

  private final List<TlvDefinition> entries;
  private final Map<List<Integer>, TlvDefinition> definitions;
  private final Map<List<Integer>, List<TlvDefinition>> children;

  private TlvRegistry(List<TlvDefinition> entries, String source) {
    this.entries = List.copyOf(entries);
    Map<List<Integer>, TlvDefinition> byPath = new LinkedHashMap<>();
    Map<List<Integer>, List<TlvDefinition>> byParent = new HashMap<>();
    Map<List<Integer>, Set<String>> names = new HashMap<>();
    for (TlvDefinition entry : entries) {
      if (entry.path().isEmpty()
          || entry.path().stream().anyMatch(n -> n < 0 || n > 255)
          || entry.name() == null
          || entry.name().isBlank()
          || entry.name().codePoints().anyMatch(Character::isWhitespace)
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
        Map.of(
            "docsis",
            load("docsis"),
            "packetcable",
            load("packetcable"),
            "dpoe",
            load("dpoe"),
            "dpog",
            load("dpog"));
  }

  public static TlvRegistry forFamily(String family) {
    if (family.equals("docsis40")) return Docsis40Catalog.INSTANCE;
    TlvRegistry registry = Catalogs.ALL.get(family);
    if (registry == null) throw new IllegalArgumentException("Unknown TLV family: " + family);
    return registry;
  }

  private static class Docsis40Catalog {
    private static final TlvRegistry INSTANCE = load("docsis40");
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
      return read(input, family, source, TlvRegistry::openCatalog);
    } catch (IOException | IllegalArgumentException e) {
      throw new IllegalStateException("Cannot load TLV catalog " + source, e);
    }
  }

  static TlvRegistry read(InputStream input, String family, String source) throws IOException {
    return read(input, family, source, null);
  }

  static TlvRegistry read(InputStream input, String family, String source, CatalogSource resolver)
      throws IOException {
    ObjectMapper mapper =
        new ObjectMapper()
            .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
    com.fasterxml.jackson.databind.node.ObjectNode document =
        mapper.readValue(input, com.fasterxml.jackson.databind.node.ObjectNode.class);
    JSONArray references = new JSONArray();
    for (var node : document.withArray("definitions")) {
      var definition = (com.fasterxml.jackson.databind.node.ObjectNode) node;
      var reference = definition.remove("reference");
      if (reference != null) {
        if (!reference.isTextual()) throw new IllegalArgumentException("Invalid reference");
        references.put(
            new JSONObject()
                .put("parentPath", new JSONArray(definition.get("path").toString()))
                .put("reference", reference.asText()));
      }
    }
    Catalog catalog = mapper.treeToValue(document, Catalog.class);
    if (catalog.formatVersion() != 1
        || !family.equals(catalog.family())
        || catalog.edition().isBlank()
        || catalog.definitions().isEmpty()) {
      throw new IllegalArgumentException(source + ": invalid catalog header");
    }
    TlvRegistry base = new TlvRegistry(catalog.definitions(), source);
    if (references.isEmpty()) return base;
    if (resolver == null)
      throw new IllegalArgumentException(source + ": reference resolver required");
    return compose(base, references, resolver, family);
  }

  @FunctionalInterface
  interface CatalogSource {
    InputStream open(String name) throws IOException;
  }

  private static InputStream openCatalog(String name) throws IOException {
    if (!name.matches("[a-zA-Z0-9.-]+") || name.contains("..")) {
      throw new IOException("Invalid catalog filename: " + name);
    }
    String directory = System.getProperty("oscar.tlv.directory");
    if (directory != null) {
      Path root = Path.of(directory).toRealPath();
      if (!root.resolve(name).toRealPath().startsWith(root))
        throw new IOException("Catalog escapes configured root: " + name);
    }
    InputStream input =
        directory == null
            ? TlvRegistry.class.getResourceAsStream("/tlv-registry/" + name)
            : Files.newInputStream(Path.of(directory, name));
    if (input == null) throw new IOException("Missing catalog " + name);
    return input;
  }

  /**
   * Resolve parent references to complete reviewed service catalogs without changing configuration
   * instance order.
   */
  static TlvRegistry compose(
      TlvRegistry base, JSONArray references, CatalogSource source, String family)
      throws IOException {
    List<TlvDefinition> combined = new ArrayList<>(base.entries);
    for (Object item : references) {
      JSONObject attachment = (JSONObject) item;
      List<Integer> parent = path(attachment.getJSONArray("parentPath"));
      TlvDefinition container = base.find(parent);
      if (container == null || container.lengthMin() != 0)
        throw new IllegalArgumentException("Attachment requires a base container: " + parent);
      String name = attachment.getString("reference");
      if (!name.matches("[a-zA-Z0-9.-]+") || name.contains(".."))
        throw new IllegalArgumentException("Invalid catalog filename: " + name);
      com.fasterxml.jackson.databind.node.ObjectNode document;
      try (InputStream input = source.open(name)) {
        document =
            new ObjectMapper()
                .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                .readValue(input, com.fasterxml.jackson.databind.node.ObjectNode.class);
      }
      var declaredParent = document.remove("parentPath");
      if (declaredParent == null
          || !declaredParent.isArray()
          || !parent.equals(path(new JSONArray(declaredParent.toString())))
          || document.has("attachments"))
        throw new IllegalArgumentException("Invalid service attachment " + name);
      String serviceFamily = document.path("family").asText();
      TlvRegistry service;
      try (InputStream input =
          new java.io.ByteArrayInputStream(
              document.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
        service = read(input, serviceFamily, name);
      }
      for (TlvDefinition entry : service.entries) {
        List<Integer> relative = entry.path();
        List<Integer> full = new ArrayList<>(parent);
        full.addAll(relative);
        combined.add(
            new TlvDefinition(
                full,
                entry.name(),
                entry.codec(),
                entry.lengthMin(),
                entry.lengthMax(),
                entry.lengthBytes(),
                container.supportedVersion(),
                entry.maxInstances(),
                entry.description()));
      }
    }
    return new TlvRegistry(combined, family + " composed profile");
  }

  private static List<Integer> path(JSONArray values) {
    List<Integer> path = new ArrayList<>();
    for (int i = 0; i < values.length(); i++) {
      Object value = values.get(i);
      if (!(value instanceof Integer number) || number < 0 || number > 255)
        throw new IllegalArgumentException("Invalid path " + values);
      path.add((Integer) value);
    }
    if (path.isEmpty()) throw new IllegalArgumentException("Empty path");
    return List.copyOf(path);
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
