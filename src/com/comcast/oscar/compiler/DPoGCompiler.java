/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants;
import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.tlv.DPoGTlvCodec;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.util.List;

/** DPoG C01 file processing with delegated MULPI 3.0 C01 Annex D integrity rules. */
public final class DPoGCompiler extends DocsisCompiler {
  private static final int SERVICE_FLOW_REFERENCE = 1;
  private static final int QOS_PARAMETER_SET = 6;
  private static final int EXTENDED_MIC = 6;
  private static final int TLV_HEADER_BYTES = 2;
  private String sharedSecret;
  private byte[] finalizedBytes;

  /**
   * Creates a DPoG 1.0 compiler. A nonempty key generates CM and CMTS MICs; null or empty selects
   * unsigned output, permitted by DPoG Table 7. Input TLVs are added before commit.
   *
   * @param sharedSecret provisioning key, or null/empty for no MIC fields
   */
  public DPoGCompiler(String sharedSecret) {
    super(sharedSecret, ConfigurationFileTypeConstants.DPOG_10_CONFIGURATION_TYPE);
    this.sharedSecret = sharedSecret;
  }

  /**
   * Sets the key for the next commit and invalidates the previous output.
   *
   * @param sharedSecret provisioning key, or null/empty for unsigned output
   */
  @Override
  public void setSharedSecretKey(String sharedSecret) {
    this.sharedSecret = sharedSecret;
    finalizedBytes = null;
  }

  /** Clears both source TLVs and finalized output; subsequent output is empty. */
  @Override
  public void clear() {
    super.clear();
    finalizedBytes = null;
  }

  /**
   * Returns independent output bytes: source bytes before commit, finalized bytes afterward.
   *
   * @return a defensive copy of the current binary
   */
  @Override
  public byte[] toByteArray() {
    return finalizedBytes == null ? super.toByteArray() : finalizedBytes.clone();
  }

  /**
   * @return hexadecimal text of the current binary, without separators
   */
  @Override
  public String toString() {
    return finalizedBytes == null ? super.toString() : new HexString(finalizedBytes).toString();
  }

  /**
   * Formats the current binary as hexadecimal text.
   *
   * @param separator delimiter between octets
   * @return hexadecimal output using the requested delimiter
   */
  @Override
  public String toString(String separator) {
    return finalizedBytes == null
        ? super.toString(separator)
        : new HexString(finalizedBytes).toString(separator);
  }

  /**
   * Checks supported DPoG file constraints without changing the binary.
   *
   * @param report retained for compatibility; no values or secrets are printed
   * @return true when the source forms a supported DPoG configuration
   */
  @Override
  public boolean checkSpecification(Boolean report) {
    try {
      parameters();
      return true;
    } catch (IllegalArgumentException | UnsupportedOperationException exception) {
      return false;
    }
  }

  /**
   * Validates and finalizes the source, replacing stale MICs. Output ends in FF and zero padding to
   * a four-octet boundary. Repeated commits produce identical bytes.
   *
   * @throws IllegalArgumentException for malformed or invalid supported settings
   * @throws UnsupportedOperationException for unknown settings that cannot be preserved
   */
  @Override
  public void commit() {
    finalizedBytes = null;
    byte[] parameters = parameters();
    if (sharedSecret != null && !sharedSecret.isEmpty()) {
      finalizedBytes =
          DocsisMicFinalizer.finish(
              parameters, TlvRegistry.forFamily(TlvRegistry.DPOG_FAMILY), sharedSecret);
    } else {
      var output = new ByteArrayOutputStream();
      output.writeBytes(parameters);
      output.write(Constants.END_OF_FILE);
      while (output.size() % Integer.BYTES != 0) {
        output.write(Constants.PAD);
      }
      finalizedBytes = output.toByteArray();
    }
  }

  /**
   * Validates a binary or TLV fragment before export; never removes unknown data.
   *
   * @param binary raw DPoG bytes, optionally including end marker and padding
   * @throws IllegalArgumentException for malformed encodings
   * @throws UnsupportedOperationException for unsupported encodings
   */
  public static void validateEncoding(byte[] binary) {
    DPoGTlvCodec.parse(binary);
  }

  private byte[] parameters() {
    var elements = DPoGTlvCodec.parse(super.toByteArray());
    for (int requiredType :
        List.of(
            Constants.NETWORK_ACC_CTRL_OBJ, Constants.US_SERVICE_FLOW, Constants.DS_SERVICE_FLOW)) {
      if (elements.stream().noneMatch(element -> element.definition().type() == requiredType)) {
        throw new IllegalArgumentException("Missing required DPoG TLV " + requiredType);
      }
    }
    java.util.Set<Integer> references = new java.util.HashSet<>();
    for (var element : elements) {
      int tlvType = element.definition().type();
      if (tlvType == Constants.US_SERVICE_FLOW || tlvType == Constants.DS_SERVICE_FLOW) {
        for (int childType : List.of(SERVICE_FLOW_REFERENCE, QOS_PARAMETER_SET)) {
          if (element.children().stream()
              .noneMatch(child -> child.definition().type() == childType)) {
            throw new IllegalArgumentException("DPoG service flow requires reference and QoS set");
          }
        }
        var reference =
            element.children().stream()
                .filter(child -> child.definition().type() == SERVICE_FLOW_REFERENCE)
                .findFirst()
                .orElseThrow()
                .encoded();
        int referenceValue =
            (Byte.toUnsignedInt(reference[TLV_HEADER_BYTES]) << Byte.SIZE)
                | Byte.toUnsignedInt(reference[TLV_HEADER_BYTES + 1]);
        if (!references.add(referenceValue)) {
          throw new IllegalArgumentException(
              "Duplicate DPoG service-flow reference " + referenceValue);
        }
      }
      if ((sharedSecret == null || sharedSecret.isEmpty())
          && tlvType == Constants.DOC_EXT_FIELD
          && element.children().stream()
              .anyMatch(child -> child.definition().type() == EXTENDED_MIC)) {
        throw new IllegalArgumentException("DPoG extended MIC requires a shared secret");
      }
    }
    var output = new ByteArrayOutputStream();
    for (var element : elements) {
      int tlvType = element.definition().type();
      if (tlvType != Constants.CM_MIC && tlvType != Constants.CMTS_MIC) {
        output.writeBytes(element.encoded());
      }
    }
    return output.toByteArray();
  }
}
