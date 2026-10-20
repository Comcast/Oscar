/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.*;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.DocsisVersion;
import com.comcast.oscar.dictionary.TlvRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Top-level presence checks for complete DOCSIS configurations, independent of UI and MIC
 * generation. MULPI 3.1 I21 D.1.2 requires 3, 24, 25, 6, 7 and End; DOCSIS 1.0 uses Class of
 * Service. This does not validate nested QoS parameters, multiplicity, MIC authenticity or device
 * acceptance.
 */
public final class DocsisMinimumRequirements {
  private DocsisMinimumRequirements() {}

  /** Explicit input representation; independent of filename extension or DOCSIS revision. */
  public enum InputFormat {
    TEXT,
    BINARY
  }

  /**
   * Checks a text file's provisioned minimum or a binary file's finalized minimum for a DOCSIS
   * protocol revision. Reads the whole file without changing it or requiring a shared secret.
   * Success means minimum top-level presence, not full conformance or MIC authentication.
   *
   * <p>Example: validateFile(Path.of("profile.txt"), InputFormat.TEXT, DocsisVersion.DOCSIS_3_1)
   * returns normally when parsing succeeds and nonempty top-level 3, 24 and 25 are present.
   *
   * @param file non-null path to a readable configuration file
   * @param format non-null TEXT or BINARY selection; never inferred from the filename
   * @param docsisVersion non-null DOCSIS protocol revision, not an OSCAR release version
   * @throws IOException if the file cannot be read or text cannot be decoded as UTF-8
   * @throws IllegalArgumentException if text parsing/family matching fails, top-level framing is
   *     malformed, or required TLVs are missing
   * @throws NullPointerException if any argument is null
   */
  public static void validateFile(Path file, InputFormat format, DocsisVersion docsisVersion)
      throws IOException {
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(format, "format");
    Objects.requireNonNull(docsisVersion, "docsisVersion");
    switch (format) {
      case TEXT -> validateText(Files.readString(file), docsisVersion);
      case BINARY -> validateComplete(Files.readAllBytes(file), docsisVersion);
    }
  }

  /**
   * Parses complete editor text and checks its family and provisioned top-level minimum. Does not
   * modify text, write files or generate integrity fields. A Docsis wrapper supports revisions
   * 1.0–3.1; Docsis-4.0 requires DOCSIS_4_0.
   *
   * <p>Example: validateText("Docsis { NetworkAccess 1; }", DocsisVersion.DOCSIS_3_1) throws
   * IllegalArgumentException listing missing TLVs 24 and 25.
   *
   * @param text non-null complete OSCAR configuration text, including its wrapper
   * @param docsisVersion non-null target DOCSIS protocol revision
   * @throws IllegalArgumentException if parsing/family matching fails or the minimum is absent
   * @throws NullPointerException if either argument is null
   */
  public static void validateText(String text, DocsisVersion docsisVersion) {
    Objects.requireNonNull(text, "text");
    Objects.requireNonNull(docsisVersion, "docsisVersion");
    var imported = new ConfigurationFileImport(new StringBuilder(text));
    int actual = imported.getConfigurationFileType();
    if (actual != DOCSIS_31_CONFIGURATION_TYPE && actual != DOCSIS_40_CONFIGURATION_TYPE
        || (actual == DOCSIS_40_CONFIGURATION_TYPE)
            != (docsisVersion == DocsisVersion.DOCSIS_4_0)) {
      throw new IllegalArgumentException("Text header does not match the selected DOCSIS family");
    }
    validateParameters(imported.toByteArray(), docsisVersion);
  }

  /**
   * Checks assembled provisioned bytes before MIC generation without changing them. Requires
   * nonempty top-level 3/24/25 for DOCSIS 1.1–4.0, or 3/4 for DOCSIS 1.0. Fragments can still be
   * imported/exported without invoking this complete-profile check.
   *
   * <p>Example: bytes 03 01 01 at DOCSIS_3_1 cause an exception listing missing 24 and 25. Returns
   * normally when minimum presence and top-level framing checks pass.
   *
   * @param binary non-null assembled TLV bytes; MICs and End are not required at this stage
   * @param docsisVersion non-null target DOCSIS protocol revision
   * @throws IllegalArgumentException if framing is malformed or required parameters are absent
   * @throws NullPointerException if either argument is null
   */
  public static void validateParameters(byte[] binary, DocsisVersion docsisVersion) {
    validate(binary, docsisVersion, false);
  }

  /**
   * Checks finalized bytes for the provisioned minimum plus CM MIC, CMTS MIC and End. Input bytes
   * are unchanged; no secret is needed. A successful call establishes presence and top-level
   * framing only, not a valid digest, nested QoS settings or device acceptance.
   *
   * <p>Example: a DOCSIS_3_1 binary containing 3/24/25/6/7/255 returns normally; removing End
   * causes an exception identifying missing TLV 255.
   *
   * @param binary non-null finalized DOCSIS file bytes, including End and optional trailing padding
   * @param docsisVersion non-null target DOCSIS protocol revision
   * @throws IllegalArgumentException if framing is malformed or required settings are absent
   * @throws NullPointerException if either argument is null
   */
  public static void validateComplete(byte[] binary, DocsisVersion docsisVersion) {
    validate(binary, docsisVersion, true);
  }

  private static void validate(byte[] binary, DocsisVersion docsisVersion, boolean complete) {
    Objects.requireNonNull(binary, "binary");
    Objects.requireNonNull(docsisVersion, "docsisVersion");
    var registry =
        TlvRegistry.forFamily(
            docsisVersion == DocsisVersion.DOCSIS_4_0
                ? TlvRegistry.DOCSIS_40_FAMILY
                : TlvRegistry.DOCSIS_FAMILY);
    Set<Integer> present = new HashSet<>();
    for (int offset = 0; offset < binary.length; ) {
      int type = Byte.toUnsignedInt(binary[offset++]);
      if (type == Constants.END_OF_FILE) {
        present.add(type);
        while (offset < binary.length) {
          if (binary[offset++] != 0)
            throw new IllegalArgumentException("Non-padding data after End marker");
        }
        break;
      }
      if (type == Constants.PAD) continue;
      var definition = registry.find(List.of(type));
      int width = definition == null ? 1 : definition.lengthBytes();
      if (binary.length - offset < width)
        throw new IllegalArgumentException("Truncated TLV " + type + " header");
      int length = 0;
      for (int index = 0; index < width; index++)
        length = (length << 8) | Byte.toUnsignedInt(binary[offset++]);
      if (length < 0 || length > binary.length - offset)
        throw new IllegalArgumentException("Truncated TLV " + type + " value");
      // Empty reference-template placeholders do not satisfy required settings.
      if (length > 0) present.add(type);
      offset += length;
    }
    var required = new ArrayList<Integer>();
    required.add(Constants.NETWORK_ACC_CTRL_OBJ);
    required.addAll(
        docsisVersion == DocsisVersion.DOCSIS_1_0
            ? Constants.DOCSIS_10_GTR_MIN_TLV
            : Constants.DOCSIS_11_GTR_MIN_TLV);
    if (complete) required.addAll(Constants.DOCSIS_MIN_TLV);
    List<String> missing =
        required.stream()
            .distinct()
            .filter(type -> !present.contains(type))
            .map(type -> type + " (" + registry.find(List.of(type)).name() + ")")
            .toList();
    if (!missing.isEmpty())
      throw new IllegalArgumentException(
          "Missing required DOCSIS top-level TLVs: " + String.join(", ", missing));
  }
}
