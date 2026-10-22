/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import com.comcast.oscar.ber.BERService;
import com.comcast.oscar.compiler.DocsisConstants;
import com.comcast.oscar.compiler.PacketCableConstants;
import com.comcast.oscar.constants.Constants;
import com.comcast.oscar.datatype.DataTypeDictionaryReference;
import com.comcast.oscar.datatype.DataTypeFormatConversion;
import com.comcast.oscar.dictionary.Dictionary;
import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.netsnmp.NetSNMP;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.tlv.TlvVariableBinding;
import com.comcast.oscar.utilities.HexString;
import com.comcast.oscar.utilities.PrettyPrint;
import com.comcast.oscar.utilities.TlvTextString;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.snmp4j.asn1.BER;

public class ConfigurationFileExport {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(ConfigurationFileExport.class);

  // Decoded instance state; export options retain their historical per-instance behavior.
  private byte[] binary;
  private TemplateBannerLevel templateBannerLevel = TemplateBannerLevel.FULL;

  private ArrayList<JSONObject> decodedEntries;
  private static final int NO_PENDING_CERTIFICATE = -1;
  private static final int ROOT_CHILD_DEPTH = 1;
  private static final byte FIRST_TEMPLATE_TYPE = 1;
  private static final byte TEMPLATE_VALUE = 0;
  private static final int TEMPLATE_ENTRY_COUNT = 255;
  // Accept 25 or 202.2.1; reject empty components, signs and nondecimal identifiers.
  private static final Pattern TLV_PATH_PATTERN = Pattern.compile("[0-9]+(?:\\.[0-9]+)*");
  private static final String BER_OID_FIELD = "OID";
  private static final String BER_TYPE_FIELD = "DATA_TYPE";
  private static final String BER_VALUE_FIELD = "VALUE";
  private static final String SNMP_STANDARD_NAME = "Snmp11";
  private static final String SNMP_EXTENDED_NAME = "Snmp64";
  private static final Map<Integer, String> BER_DATA_TYPE =
      Map.of(
          (int) BER.COUNTER32,
          "Counter32",
          (int) BER.COUNTER64,
          "Counter64",
          (int) BER.GAUGE32,
          "Gauge32",
          (int) BER.INTEGER32,
          "Integer32",
          (int) BER.TIMETICKS,
          "TimeTicks",
          (int) BER.IPADDRESS,
          "IpAddress",
          (int) BER.OCTETSTRING,
          "OctetString",
          Byte.toUnsignedInt(BERService.HEX),
          "HexString");
  private TlvDictionary dictionary = null;
  private String rootName;
  private final int configurationType;
  private boolean includeDefaults = true;
  private boolean textualOids = true;
  private boolean suppressComments = false;

  public final String END_OF_CODE_BLOCK = "\\*EOCB*\\";

  public static final Integer DOCSIS_PKTCBL = -1;
  public static final Integer DOCSIS_VER_10 =
      ConfigurationFileTypeConstants.DOCSIS_10_CONFIGURATION_TYPE;
  public static final Integer DOCSIS_VER_11 =
      ConfigurationFileTypeConstants.DOCSIS_11_CONFIGURATION_TYPE;
  public static final Integer DOCSIS_VER_20 =
      ConfigurationFileTypeConstants.DOCSIS_20_CONFIGURATION_TYPE;
  public static final Integer DOCSIS_VER_30 =
      ConfigurationFileTypeConstants.DOCSIS_30_CONFIGURATION_TYPE;
  public static final Integer DOCSIS_VER_31 =
      ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE;
  public static final Integer DOCSIS_VER_40 =
      ConfigurationFileTypeConstants.DOCSIS_40_CONFIGURATION_TYPE;

  public static final Integer PKT_CBL_VER_10 =
      ConfigurationFileTypeConstants.PKT_CABLE_10_CONFIGURATION_TYPE;
  public static final Integer PKT_CBL_VER_15 =
      ConfigurationFileTypeConstants.PKT_CABLE_15_CONFIGURATION_TYPE;
  public static final Integer PKT_CBL_VER_20 =
      ConfigurationFileTypeConstants.PKT_CABLE_20_CONFIGURATION_TYPE;

  public static final Integer DPOE_VER_10 =
      ConfigurationFileTypeConstants.DPOE_10_CONFIGURATION_TYPE;
  public static final Integer DPOE_VER_20 =
      ConfigurationFileTypeConstants.DPOE_20_CONFIGURATION_TYPE;

  public static final Boolean EXPORT_DEFAULT_TLV = true;
  public static final Boolean EXPORT_FOUND_TLV = false;

  public static final Boolean TEXTUAL_OID_FORMAT = true;
  public static final Boolean DOTTED_OID_FORMAT = false;

  public static final Boolean SUPPRESS_TLV_COMMENT = true;

  // Input adapters: read bytes once, then select the catalog and decode.

  /**
   * Auto-detects DOCSIS versus PacketCable from a nonempty file; cannot infer its edition.
   *
   * @param file non-null binary file
   * @throws IllegalArgumentException if the file is empty
   * @throws UncheckedIOException if the file cannot be read
   * @deprecated use the explicit configuration-type overload
   */
  @Deprecated
  public ConfigurationFileExport(File file) {
    this(readBinary(file), DOCSIS_PKTCBL);
  }

  /**
   * Reads a binary file once and decodes it using the selected configuration family.
   *
   * @param file non-null readable binary file
   * @param configurationType ConfigurationFileTypeConstants family/revision code, or DOCSIS_PKTCBL
   *     for legacy marker detection
   * @throws IllegalArgumentException for empty input or an unsupported family
   * @throws UncheckedIOException if the file cannot be read
   */
  public ConfigurationFileExport(File file, int configurationType) {
    this(readBinary(file), configurationType);
  }

  /**
   * Auto-detects the family from nonempty builder bytes without modifying the builder.
   *
   * @param builder non-null binary source
   * @throws IllegalArgumentException if the source is empty
   * @deprecated use the explicit configuration-type overload
   */
  @Deprecated
  public ConfigurationFileExport(TlvBuilder builder) {
    this(Objects.requireNonNull(builder).toByteArray(), DOCSIS_PKTCBL);
  }

  /**
   * Decodes builder bytes without modifying the builder or writing a file.
   *
   * @param builder non-null binary source
   * @param configurationType ConfigurationFileTypeConstants family/revision code
   * @throws IllegalArgumentException for empty input or an unsupported family
   */
  public ConfigurationFileExport(TlvBuilder builder, int configurationType) {
    this(Objects.requireNonNull(builder).toByteArray(), configurationType);
  }

  /**
   * Optionally strips finalization fields before legacy family detection and decoding.
   *
   * @param builder non-null binary source; not modified
   * @param stripFinalize true to remove the family's finalization fields
   * @throws IllegalArgumentException for empty or unstrippable input
   * @deprecated use the explicit configuration-type overload
   */
  @Deprecated
  public ConfigurationFileExport(TlvBuilder builder, boolean stripFinalize) {
    this(legacyBytes(builder, stripFinalize), DOCSIS_PKTCBL);
  }

  /**
   * Decodes the configuration's current bytes using its declared family, without modifying it.
   *
   * @param configuration non-null source; may contain unfinalized parameters
   * @throws IllegalArgumentException for empty input or an unsupported family
   */
  public ConfigurationFileExport(ConfigurationFile configuration) {
    this(
        Objects.requireNonNull(configuration).toByteArray(),
        configuration.getConfigurationFileType());
  }

  private ConfigurationFileExport(byte[] bytes, int configurationType) {
    if (bytes.length == 0)
      throw new IllegalArgumentException("Cannot export an empty binary configuration");
    this.binary = bytes.clone();
    this.configurationType = configurationType;
    selectFormat();
    if (configurationType == ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE) {
      com.comcast.oscar.compiler.DPoGCompiler.validateEncoding(binary);
    } else if (configurationType >= DPOE_VER_10 && configurationType <= DPOE_VER_20) {
      removeNonDictionaryTopLevelTLV();
    }
    decodeBinaryEntries();
  }

  /**
   * Builds a reference template containing the selected catalog's definitions, not valid values.
   *
   * @param configurationType explicit ConfigurationFileTypeConstants family/revision code
   * @throws IllegalArgumentException if the family is unsupported or auto-detection was requested
   */
  public ConfigurationFileExport(int configurationType) {
    if (configurationType < DOCSIS_VER_10)
      throw new IllegalArgumentException("Templates require an explicit configuration type");
    this.configurationType = configurationType;
    selectFormat();
    // Retain the historical getTlvBuilder() representation of reference templates.
    binary =
        configurationType <= DOCSIS_VER_40
            ? docsisTemplateBytes()
            : configurationType == ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE
                ? new byte[0]
                : packetCableTemplateBytes();
    if (configurationType >= DPOE_VER_10 && configurationType <= DPOE_VER_20) {
      removeNonDictionaryTopLevelTLV();
    }
    copyTemplateEntries(dictionary.getAllTlvDefinition(configurationType));
  }

  private static byte[] readBinary(File file) {
    try {
      return Files.readAllBytes(Objects.requireNonNull(file).toPath());
    } catch (IOException failure) {
      throw new UncheckedIOException("Cannot read binary configuration", failure);
    }
  }

  private static byte[] legacyBytes(TlvBuilder builder, boolean strip) {
    byte[] bytes = Objects.requireNonNull(builder).toByteArray();
    if (bytes.length == 0)
      throw new IllegalArgumentException("Cannot export an empty binary configuration");
    if (!strip) return bytes;
    int type = bytes[0] == PacketCableConstants.FILE_MARKER ? PKT_CBL_VER_20 : DOCSIS_VER_31;
    try {
      return ConfigurationFile.stripFinalize(builder, type).toByteArray();
    } catch (TlvException failure) {
      throw new IllegalArgumentException("Cannot strip configuration finalization fields", failure);
    }
  }

  private void selectFormat() {
    if (configurationType <= DOCSIS_PKTCBL) {
      boolean packetCable = binary[0] == PacketCableConstants.FILE_MARKER;
      dictionary =
          new TlvDictionary(
              packetCable
                  ? TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME
                  : TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME);
      rootName = packetCable ? "PacketCable-X.X" : "Docsis";
    } else if (configurationType <= DOCSIS_VER_40) {
      boolean docsis40 = configurationType == DOCSIS_VER_40;
      dictionary =
          new TlvDictionary(
              docsis40
                  ? TlvDictionary.DOCSIS_40_DICTIONARY_TABLE_NAME
                  : TlvDictionary.DOCSIS_DICTIONARY_TABLE_NAME);
      rootName = docsis40 ? "Docsis-4.0" : "Docsis";
    } else if (configurationType >= PKT_CBL_VER_10 && configurationType <= PKT_CBL_VER_20) {
      dictionary = new TlvDictionary(TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME);
      rootName =
          configurationType == PKT_CBL_VER_10
              ? "PacketCable-1.0"
              : configurationType == PKT_CBL_VER_15 ? "PacketCable-1.5" : "PacketCable-2.0";
    } else if (configurationType >= DPOE_VER_10 && configurationType <= DPOE_VER_20) {
      dictionary = new TlvDictionary(TlvDictionary.DPOE_DICTIONARY_TABLE_NAME);
      rootName = "DPoE";
    } else if (configurationType == ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE) {
      dictionary = new TlvDictionary(TlvDictionary.DPOG_DICTIONARY_TABLE_NAME);
      rootName = "DPoG";
    } else {
      throw new IllegalArgumentException("Unsupported configuration type: " + configurationType);
    }
  }

  /**
   * Selects the banner for subsequent text exports; new exporters default to FULL. This option does
   * not change TLV contents or comment suppression.
   *
   * @param level FULL for help, SPDX for license, SPDX_AUTHOR for attribution, or DISABLE for none
   * @throws NullPointerException if level is null; the existing selection is retained
   */
  public void setTemplateBannerLevel(TemplateBannerLevel level) {
    templateBannerLevel = Objects.requireNonNull(level, "Template banner level");
  }

  // Text rendering: emit syntax and annotations; PrettyPrint owns final indentation.

  /**
   * Returns the legacy unnormalized text representation, including exporter block markers.
   *
   * @param indentation historically ignored; retained for source compatibility
   * @return banner and raw configuration text
   * @deprecated use the boolean overloads for normalized tab indentation and LF line endings
   */
  @Deprecated
  public String toPrettyPrint(int indentation) {
    return banner().append(renderBody()).append('\n').toString();
  }

  /**
   * Renders configuration text with tabs, LF endings and the current comment-suppression setting.
   * Values, repeated occurrences and decoded order are retained; no bytes or files are modified.
   * For example, NetworkAccess is emitted as a tab-indented statement with TLV path [3].
   *
   * @param includeDefaults true to include valueless template entries (except incomplete SNMP
   *     objects)
   * @return banner followed by formatted configuration text
   * @throws IllegalArgumentException if decoded entries cannot be rendered completely
   */
  public String toPrettyPrint(boolean includeDefaults) {
    this.includeDefaults = includeDefaults;
    String formatted = new PrettyPrint(renderBody()).toString();
    return banner()
        .append(suppressComments ? TlvTextString.withoutComments(formatted) : formatted)
        .toString();
  }

  /**
   * Renders text and retains the requested annotation setting for subsequent single-argument calls.
   * The selected banner is independent of TLV comment suppression; DISABLE omits it.
   *
   * @param includeDefaults true to include valueless template entries, except incomplete SNMP
   *     objects
   * @param suppressComments true to omit TLV annotations; quoted comment-like values are preserved
   * @return banner and formatted configuration text, using tabs and LF endings
   * @throws IllegalArgumentException if decoded entries cannot be rendered completely
   */
  public String toPrettyPrint(boolean includeDefaults, boolean suppressComments) {
    this.suppressComments = suppressComments;
    return toPrettyPrint(includeDefaults);
  }

  private String renderBody() {
    var output = new StringBuilder(rootName).append(" {\n");
    try {
      for (JSONObject entry : decodedEntries) appendEntry(output, entry, ROOT_CHILD_DEPTH);
    } catch (JSONException failure) {
      throw new IllegalArgumentException("Cannot render complete configuration text", failure);
    }
    return output.append("}\n").toString();
  }

  /**
   * Legacy unimplemented placeholder retained for API compatibility.
   *
   * @return null; use getTlvBuilder() to obtain the source bytes
   */
  public Map<Integer, String> toHex() {
    return null;
  }

  /**
   * Returns a new builder containing this exporter's binary representation, without formatting.
   * Template exporters retain their historical synthetic byte representation.
   *
   * @return a new builder; modifying it does not modify this exporter
   * @throws TlvException if the stored representation cannot be added to a builder
   */
  public TlvBuilder getTlvBuilder() throws TlvException {
    TlvBuilder tb = new TlvBuilder();

    tb.add(new HexString(this.binary));

    return tb;
  }

  // Definition help: bounded path traversal, independent of configuration instance values.

  /**
   * Returns the name, wrapped description and input hint for one catalog path, such as 25.1.
   * Container paths are supported as well as leaves; no configuration state is modified.
   *
   * @param path non-null dot-separated decimal TLV identifiers
   * @return human-readable definition and input hint
   * @throws IllegalArgumentException if the path is malformed or absent from the selected catalog
   */
  public String getTlvDefintion(String path) {
    Objects.requireNonNull(path, "path");
    if (!TLV_PATH_PATTERN.matcher(path).matches())
      throw new IllegalArgumentException("Invalid TLV path: " + path);
    String[] parts = path.split("\\.");
    JSONObject definition = dictionary.getTlvDefinition(Integer.parseInt(parts[0]));
    for (int index = 1; index < parts.length && !definition.isEmpty(); index++) {
      JSONArray children = definition.optJSONArray(Dictionary.SUBTYPE_ARRAY);
      JSONObject match = new JSONObject();
      if (children != null) {
        int type = Integer.parseInt(parts[index]);
        for (int child = 0; child < children.length(); child++) {
          JSONObject candidate = children.getJSONObject(child);
          if (candidate.getInt(Dictionary.TYPE) == type) {
            match = candidate;
            break;
          }
        }
      }
      definition = match;
    }
    if (definition.isEmpty()) throw new IllegalArgumentException("Unknown TLV path: " + path);
    return "\n\n"
        + definition.getString(Dictionary.TLV_NAME)
        + ":\n\n"
        + PrettyPrint.ToParagraphForm(definition.getString(Dictionary.TLV_DESCRIPTION))
        + "\n\nString Format:\n"
        + getDisplayHint(definition);
  }

  /**
   * Returns all decoded top-level occurrences of a type in export order.
   *
   * @param type numeric top-level wire identifier
   * @return a new array containing the original mutable entry objects; edits affect later exports
   */
  public JSONArray getTopLevelTLVJSON(int type) {
    var matches = new JSONArray();
    for (JSONObject entry : decodedEntries) {
      if (entry.getInt(Dictionary.TYPE) == type) matches.put(entry);
    }
    return matches;
  }

  // File output: serialize text once, using a deterministic charset and managed resources.

  /**
   * Writes found TLVs as UTF-8 text, replacing the destination file.
   *
   * @param output non-null destination file
   * @return true after a successful write; false for an I/O failure
   * @throws IllegalArgumentException if text rendering fails
   */
  public boolean writeToDisk(File output) {
    return writeToDisk(output, EXPORT_FOUND_TLV);
  }

  /**
   * Writes the same text returned by toPrettyPrint, using UTF-8 and LF line endings.
   *
   * @param output non-null destination file; an existing file is replaced
   * @param includeDefaults true to include template placeholders
   * @return true after a successful write; false if opening or writing fails
   * @throws IllegalArgumentException if rendering fails before the file is opened
   */
  public boolean writeToDisk(File output, boolean includeDefaults) {
    Objects.requireNonNull(output, "output");
    String text = toPrettyPrint(includeDefaults);
    try {
      Files.writeString(output.toPath(), text, StandardCharsets.UTF_8);
      return true;
    } catch (IOException failure) {
      logger.warn("Cannot write configuration text ({})", failure.getClass().getSimpleName());
      return false;
    }
  }

  /**
   * @return the ConfigurationFile Type per the ConfigurationFile() field statics
   */
  public int getConfigurationFileType() {
    if (configurationType >= 0) return configurationType;
    if (binary[0] == PacketCableConstants.FILE_MARKER) {
      return ConfigurationFile.PKT_CBL_VER_20;
    } else {
      return ConfigurationFile.DOCSIS_VER_31;
    }
  }

  /**
   * @param textualOids TRUE = Textual OID Output , FALSE = Dotted OID Output
   */
  public void setDotTextOIDOutputFormat(boolean textualOids) {
    this.textualOids = textualOids;
  }

  /**
   * DEFAULT = true;
   *
   * @deprecated Use {@link #toPrettyPrint(boolean)} to control export verbosity.
   * @param includeDefaults
   */
  @Deprecated
  public void setExportVerbose(boolean includeDefaults) {
    this.includeDefaults = includeDefaults;
  }

  // Binary decoding and contiguous certificate fragment assembly.

  /** Decodes entries in wire order and joins only contiguous certificate fragments. */
  private void decodeBinaryEntries() {
    if (configurationType == ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE) {
      decodedEntries = new ArrayList<>();
      var binding = new TlvVariableBinding(binary, dictionary.getTopLevelByteLength());
      var decoded =
          new TlvDisassemble(binding, TlvDictionary.DPOG_DICTIONARY_TABLE_NAME).getTlvDictionary();
      for (int index = 0; index < decoded.length(); index++) {
        decodedEntries.add(decoded.getJSONObject(index));
      }
      return;
    }

    String sDictionaryTableName = dictionary.getDictionaryTableName();
    decodedEntries = new ArrayList<>();
    TlvBuilder tbTLV = new TlvBuilder();

    // Build Map to determine the Type to ByteLength Mapping
    Map<Integer, Integer> miiTopLevelTLV = dictionary.getTopLevelByteLength();

    // Add TLV Byte Array from Constructor for later processing
    tbTLV.add(new TlvVariableBinding(binary, miiTopLevelTLV));

    logger.debug(
        () ->
            "ConfigrationFileExport.decodeBinaryEntries() -> "
                + "miiTopLevelTLV.size(): "
                + miiTopLevelTLV.size()
                + " -> "
                + "tbTLV.length(): "
                + tbTLV.length()
                + " -> "
                + "binary.length: "
                + binary.length);

    int pendingCertificateType = NO_PENDING_CERTIFICATE;
    ByteArrayOutputStream pendingCertificate = new ByteArrayOutputStream();

    for (byte[] encodedTlv : tbTLV.sortByTopLevelTlv(miiTopLevelTLV)) {
      int tlvType = Byte.toUnsignedInt(encodedTlv[0]);
      if (pendingCertificateType != NO_PENDING_CERTIFICATE && tlvType != pendingCertificateType) {
        appendCertificate(
            pendingCertificateType, pendingCertificate.toByteArray(), sDictionaryTableName);
        pendingCertificate.reset();
        pendingCertificateType = NO_PENDING_CERTIFICATE;
      }
      if (tlvType == Byte.toUnsignedInt(DocsisConstants.EOF)) {
        break;
      }
      JSONObject definition = dictionary.getTlvDefinition(tlvType);
      if (DataTypeDictionaryReference.DATA_TYPE_MULTI_TLV_BYTE_ARRAY.equals(
          definition.getString(Dictionary.DATA_TYPE))) {
        pendingCertificateType = tlvType;
        try {
          pendingCertificate.writeBytes(TlvBuilder.getTlvValue(encodedTlv));
        } catch (TlvException error) {
          throw new IllegalArgumentException("Cannot read certificate TLV " + tlvType, error);
        }
      } else {
        var binding = new TlvVariableBinding(encodedTlv, miiTopLevelTLV);
        decodedEntries.add(
            new TlvDisassemble(binding, sDictionaryTableName).getTlvDictionary().getJSONObject(0));
      }
    }
    // Raw API input can end with certificate fragments without an EOF marker.
    if (pendingCertificateType != NO_PENDING_CERTIFICATE) {
      appendCertificate(
          pendingCertificateType, pendingCertificate.toByteArray(), sDictionaryTableName);
    }
  }

  /**
   * Decode one contiguous certificate fragment group into one text-export entry.
   *
   * @param certificateType numeric TLV type identifying this certificate
   * @param certificateBytes concatenated payload, in original fragment order
   * @param dictionaryName catalog used to decode the certificate
   */
  private void appendCertificate(
      int certificateType, byte[] certificateBytes, String dictionaryName) {
    var binding = new TlvVariableBinding();
    try {
      binding.add(certificateType, certificateBytes);
    } catch (TlvException error) {
      throw new IllegalArgumentException("Cannot decode certificate TLV " + certificateType, error);
    }
    decodedEntries.add(
        new TlvDisassemble(binding, dictionaryName).getTlvDictionary().getJSONObject(0));
  }

  // Container and leaf rendering share one buffer. Markers let PrettyPrint drop empty templates.

  private void appendEntry(StringBuilder output, JSONObject entry, int depth) {
    if (entry.isEmpty()) return;
    if (entry.getBoolean(Dictionary.ARE_SUBTYPES)) {
      if (depth == ROOT_CHILD_DEPTH) output.append('\n');
      output.append("\t".repeat(depth)).append(entry.getString(Dictionary.TLV_NAME)).append(" {\n");
      JSONArray children = entry.getJSONArray(Dictionary.SUBTYPE_ARRAY);
      for (int index = 0; index < children.length(); index++)
        appendEntry(output, children.getJSONObject(index), depth + 1);
      output.append("\t".repeat(depth)).append("} ").append(END_OF_CODE_BLOCK).append("\n\n");
      return;
    }
    if (!includeDefaults && !entry.has(Dictionary.VALUE)) return;
    String name = entry.getString(Dictionary.TLV_NAME);
    if (!entry.has(Dictionary.VALUE)
        && (name.equals(SNMP_STANDARD_NAME) || name.equals(SNMP_EXTENDED_NAME))) return;
    output.append("\t".repeat(depth)).append(name).append(' ');
    if (entry.opt(Dictionary.VALUE) instanceof JSONArray varbinds) {
      appendSnmpValue(output, varbinds.getJSONObject(0));
    } else if (entry.has(Dictionary.VALUE)) {
      output.append(
          TlvTextString.format(entry.get(Dictionary.VALUE), entry.optString(Dictionary.DATA_TYPE)));
    }
    output.append(';');
    appendTlvAnnotation(output, entry);
  }

  private void appendSnmpValue(StringBuilder output, JSONObject varbind) {
    String type = BER_DATA_TYPE.get(Integer.decode(varbind.getString(BER_TYPE_FIELD)));
    if (type == null) throw new IllegalArgumentException("Unsupported BER type in text export");
    output
        .append(NetSNMP.toOIDFormat(varbind.getString(BER_OID_FIELD), textualOids))
        .append(' ')
        .append(type)
        .append(' ')
        .append(TlvTextString.quote(varbind.getString(BER_VALUE_FIELD)));
  }

  private void appendTlvAnnotation(StringBuilder output, JSONObject entry) {
    output
        .append("\t /* TLV: ")
        .append(
            entry.get(Dictionary.PARENT_TYPE_LIST).toString().replace("-1,", "").replace(',', '.'))
        .append("*/\n");
  }

  // Reference-template byte representations retained for the legacy builder API.

  /**
   * @return byte[]
   */
  private byte[] docsisTemplateBytes() {
    ByteArrayOutputStream baosTLV = new ByteArrayOutputStream();

    byte[] bByte = {FIRST_TEMPLATE_TYPE, Byte.BYTES, TEMPLATE_VALUE};

    for (int iIndex = 0; iIndex < TEMPLATE_ENTRY_COUNT; iIndex++) {
      baosTLV.writeBytes(bByte);

      bByte[0]++;
    }

    return baosTLV.toByteArray();
  }

  /**
   * @return byte[]
   */
  private byte[] packetCableTemplateBytes() {
    ByteArrayOutputStream baosTLV = new ByteArrayOutputStream();

    byte[] bByte = {PacketCableConstants.FILE_MARKER, Byte.BYTES, TEMPLATE_VALUE};

    for (int iIndex = 0; iIndex < TEMPLATE_ENTRY_COUNT; iIndex++) {
      baosTLV.writeBytes(bByte);

      if (bByte[0] == PacketCableConstants.FILE_MARKER) {
        bByte[0] = FIRST_TEMPLATE_TYPE;
      } else {
        bByte[0]++;
      }
    }

    return baosTLV.toByteArray();
  }

  /** Copies catalog entries in their original order; malformed entries fail immediately. */
  private void copyTemplateEntries(JSONArray definitions) {
    decodedEntries = new ArrayList<>(definitions.length());
    for (int index = 0; index < definitions.length(); index++) {
      decodedEntries.add(definitions.getJSONObject(index));
    }
  }

  /** Builds the selected license/help banner independently of TLV comment suppression. */
  private StringBuilder banner() {
    StringBuilder sbBanner = new StringBuilder();
    if (templateBannerLevel == TemplateBannerLevel.DISABLE) {
      return sbBanner;
    }

    sbBanner.append("/*\n");

    if (templateBannerLevel != TemplateBannerLevel.FULL) {
      sbBanner.append(Constants.SPDX_NOTICE);
      if (templateBannerLevel == TemplateBannerLevel.SPDX_AUTHOR) {
        sbBanner.append('\n').append(Constants.AUTHOR_NOTICE);
      }
      return sbBanner.append("\n*/\n\n");
    }

    sbBanner.append(Constants.APACHE_20_LICENCE_DISCLAIMER + "\n\n");

    sbBanner.append("\tSnmp11 OID DataType Value\n");

    if (this.configurationType > DOCSIS_VER_40) sbBanner.append("\tSnmp64 OID DataType Value\n\n");
    else sbBanner.append('\n');

    sbBanner.append(
        "\tDOUBLE_BYTE_ARRAY_FORMAT: " + DataTypeFormatConversion.DOUBLE_BYTE_ARRAY_FORMAT + "\n");
    sbBanner.append(
        "\tIPV4_TRANSPORT_FORMAT: " + DataTypeFormatConversion.IPV4_TRANSPORT_FORMAT + "\n");
    sbBanner.append(
        "\tIPV6_TRANSPORT_FORMAT: " + DataTypeFormatConversion.IPV6_TRANSPORT_FORMAT + "\n");
    sbBanner.append("\tSTRING_BITS_FORMAT: " + DataTypeFormatConversion.STRING_BITS_FORMAT + "\n");
    sbBanner.append(
        "\tIPV6_ADDRESS_FORMAT: " + DataTypeFormatConversion.IPV6_ADDRESS_FORMAT + "\n");
    sbBanner.append(
        "\tIPV4_ADDRESS_FORMAT: " + DataTypeFormatConversion.IPV4_ADDRESS_FORMAT + "\n");
    sbBanner.append(
        "\tMAC_ADDRESS_FORMAT: " + DataTypeFormatConversion.MAC_ADDRESS_FORMAT + "\n\n");

    sbBanner.append("*/\n\n");

    return sbBanner;
  }

  // Catalog help and value syntax hints; keep exported field values separate from descriptions.

  private String getDisplayHint(JSONObject definition) {
    return switch (definition.getString(Dictionary.DATA_TYPE)) {
      case DataTypeDictionaryReference.DATA_TYPE_DOUBLE_BYTE_ARRAY ->
          DataTypeFormatConversion.DOUBLE_BYTE_ARRAY_FORMAT;
      case DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR ->
          DataTypeFormatConversion.IPV4_TRANSPORT_FORMAT;
      case DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR ->
          DataTypeFormatConversion.IPV6_TRANSPORT_FORMAT;
      case DataTypeDictionaryReference.DATA_TYPE_STRING_BITS ->
          DataTypeFormatConversion.STRING_BITS_FORMAT;
      case DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV4_ADDR ->
          DataTypeFormatConversion.IPV4_ADDRESS_FORMAT;
      case DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV6_ADDR ->
          DataTypeFormatConversion.IPV6_ADDRESS_FORMAT;
      case DataTypeDictionaryReference.DATA_TYPE_OID ->
          "docsDevFilterLLCUnmatchedAction.0 Integer32 \"1\" \n "
              + "vacmAccessStorageType.'readwritegroup'.''.2.noAuthNoPriv Integer \"2\""
              + "1.3.6.1.2.1.69.1.3.5 Integer32 \"1\"";
      case DataTypeDictionaryReference.DATA_TYPE_INTEGER ->
          "Range: -2,147,483,648 to 2,147,483,647";
      case DataTypeDictionaryReference.DATA_TYPE_STRING -> "Any ASCII String";
      case DataTypeDictionaryReference.DATA_TYPE_STRING_NULL_TERMINATED ->
          "Any ASCII String with a Terminating NULL at the end /0 or 0x00";
      case DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY ->
          "xx:xx:xx......xx:xx:xx - Example: 01:23:34:56:78:9a:bc:cd:ef";
      case DataTypeDictionaryReference.DATA_TYPE_OID_ASN1_OBJECT_6 ->
          "docsDevFilterLLCUnmatchedAction.0 \n "
              + "vacmAccessStorageType.'readwritegroup'.''.2.noAuthNoPriv \n"
              + "1.3.6.1.2.1.69.1.3.5 ";
      case DataTypeDictionaryReference.DATA_TYPE_BYTE -> "Byte: xx Example ff";
      case DataTypeDictionaryReference.DATA_TYPE_MAC_ADDRESS ->
          DataTypeFormatConversion.MAC_ADDRESS_FORMAT;
      case DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_INET_ADDR ->
          DataTypeFormatConversion.IPV4_TRANSPORT_FORMAT
              + " \n\nOR\n\n"
              + DataTypeFormatConversion.IPV6_TRANSPORT_FORMAT;
      default -> "";
    };
  }

  /**
   * This method will remove all TopLevel TLV that are not defined in the Dictionary Currently
   * support 1 byte Length TLVs
   */
  private void removeNonDictionaryTopLevelTLV() {
    /* Get TopLevel List*/
    List<Integer> liTopLevelDict = dictionary.getTopLevelTLV();

    List<Integer> liTopLevelCFE = null;

    try {
      liTopLevelCFE = getTlvBuilder().getTopLevelTlvList();
    } catch (TlvException failure) {
      throw new IllegalArgumentException("Cannot read top-level configuration entries", failure);
    }

    /*This will create a single instance of each Type */
    liTopLevelCFE = new ArrayList<Integer>(new LinkedHashSet<Integer>(liTopLevelCFE));

    /*Remove Types that are not suppose to be There */
    liTopLevelCFE.retainAll(liTopLevelDict);

    logger.debug("removeNonDictionaryTopLevelTLV() -> DICT: {}", liTopLevelDict);
    logger.debug("removeNonDictionaryTopLevelTLV() -> CFE remove DICT: {}", liTopLevelCFE);

    /*Create new ByteArray*/
    binary = TlvBuilder.fetchTlv(liTopLevelCFE, binary);
  }
}
