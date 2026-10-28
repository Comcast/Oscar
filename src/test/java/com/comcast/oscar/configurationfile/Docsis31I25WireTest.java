/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE;
import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.tlv.TlvBuilder;
import java.util.HexFormat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Source-derived encoding fragments; these are not complete deployable profiles. */
class Docsis31I25WireTest {
  @ParameterizedTest
  @ValueSource(
      strings = {
        "290a01030301000203050101",
        "4a2e042c01140104000186a00202001e030400030d400402000a02140104000186a00202001e030400030d400402000a",
        "1818281601010002010a030102040203e80501130604006400c8",
        "1918281601010002010a030102040203e80501130604006400c8",
        "462a2a28010200020308636c61737369630004046c6f77000501140601e6070101080203e809020fa00a0113",
        "472a2a28010200020308636c61737369630004046c6f77000501140601e6070101080203e809020fa00a0113",
        "461d0102000a0701070804ee6b28000904000186a00a04773594000b0205dc",
        "471d0102000a0701070804ee6b28000904000186a00a04773594000b0205dc",
        "5d1d0102000a0701070804ee6b28000904000186a00a04773594000b0205dc",
        "5e1d0102000a0701070804ee6b28000904000186a00a04773594000b0205dc",
        "2d0701010102028000", // L2VPN B.2: DUT control and interface mask.
        "4c0101",
        "530102",
        "5b0101",
        "5c0101", // MULPI AQM, DTP, LL, HQoS.
        "4f1d01028000020101030101040103050102060101070205ee080101090101",
        "5409010104020101030102", // I25 C.1.2.23: diplexer edges.
        "580400ff0102", // LLX delegated bytes retained without guessing children.
        "650c010101020100030102040101", // SYNC D.2.1.
        "660c0104c00002010204c0000202", // SYNC D.2.2 IPv4.
        "6612011020010db8000000000000000000000001", // SYNC IPv6.
        "6608030601005e000181", // PTP destination multicast MAC.
        "1818010200012c0200642d04ffffffff2e020032300101310101", // PGS widths.
        "18160102000127046c616200280701010004020100290103", // IATC/AQM/unit.
        "46140102000126060104000000012606010400000002", // Repeated criteria.
        "461201020001260c010400000001010400000002", // Repeated application IDs.
        "5d16010200012f07010200020401012f0701020003050102", // HQoS constituents.
        "5e16010200012f07010200020401012f0701020003050102"
      })
  void preservesSourceDerivedWireEncodings(String hex) throws Exception {
    byte[] wire = HexFormat.of().parseHex(hex);
    var builder = new TlvBuilder();
    builder.add(new com.comcast.oscar.utilities.HexString(wire));
    String text =
        new ConfigurationFileExport(builder, DOCSIS_31_CONFIGURATION_TYPE)
            .toPrettyPrint(false, true);
    assertArrayEquals(
        wire, new ConfigurationFileImport(new StringBuilder(text)).toByteArray(), text);
  }
}
