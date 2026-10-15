package com.comcast.oscar.dictionary;

import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Docsis40Test {
  static final String BASE =
      """
      NetworkAccess 1;
      UpstreamServiceFlow { ServiceFlowReference 1; QosParameterSetType 7; }
      DownstreamServiceFlow { ServiceFlowReference 2; QosParameterSetType 7; }
      """;

  @Test
  void matchesSec3PublishedMmhVectors() {
    var ascii = java.nio.charset.StandardCharsets.US_ASCII;
    assertArrayEquals(
        HexFormat.of().parseHex("2970f80c67a4dd80"),
        com.comcast.oscar.compiler.DocsisMmh.digest(
            "Shared secret #1 94476839".getBytes(ascii),
            "DOCSIS 3.0, fulfilling the need for speed".getBytes(ascii)));
    assertArrayEquals(
        HexFormat.of().parseHex("83c9f888cf4c8f86"),
        com.comcast.oscar.compiler.DocsisMmh.digest(
            "Shared secret #2 07782313".getBytes(ascii),
            "The Magic Words are Squeamish Ossifrages".getBytes(ascii)));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void compilesAndRoundTripsMmhExtendedMic(boolean explicit) throws Exception {
    String extra =
        extension(explicit)
            .replace("HmacType 1", "HmacType 2")
            .replace("00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00", "00:00:00:00:00:00:00:00");
    var file = compile(extra);
    byte[] bytes = file.toByteArray();
    var elements = wire(bytes);
    if (explicit) {
      assertEquals(8, value(wire(value(wire(value(elements, 43)), 6)), 3).length);
      verifyLegacyMic(elements);
    } else {
      assertEquals(16, value(elements, 7).length);
      assertArrayEquals(new byte[8], Arrays.copyOfRange(value(elements, 7), 8, 16));
      var selected = new java.io.ByteArrayOutputStream();
      for (byte[] element : elements) {
        if (List.of(3, 24, 25, 43).contains(Byte.toUnsignedInt(element[0])))
          selected.writeBytes(element);
      }
      assertArrayEquals(
          com.comcast.oscar.compiler.DocsisMmh.digest(
              "SHAREDSECRET".getBytes(java.nio.charset.StandardCharsets.US_ASCII),
              selected.toByteArray()),
          Arrays.copyOf(value(elements, 7), 8));
    }
    verifyCmMic(elements);
    file.commit();
    assertArrayEquals(bytes, file.toByteArray());
    var imported = parse(new ConfigurationFileExport(file).toPrettyPrint(false));
    var rebuilt = new ConfigurationFile(5, imported.getTlvBuilder(), "SHAREDSECRET");
    rebuilt.commit();
    assertArrayEquals(bytes, rebuilt.toByteArray());
  }

  @Test
  void compilesCompleteLabFileWithIndependentCmMic() {
    var file = compile("");
    byte[] bytes = file.toByteArray();
    assertEquals(60, bytes.length);
    assertEquals(0, bytes.length % 4);
    assertArrayEquals(
        HexFormat.of().parseHex("1a3ba2e7666290b9725605718c01f967"),
        Arrays.copyOfRange(bytes, 23, 39));
    assertEquals(6, bytes[21]);
    assertEquals(7, bytes[39]);
    assertArrayEquals(
        HexFormat.of().parseHex("2ebded318f4046a0f8701e3e93bfb67b"),
        Arrays.copyOfRange(bytes, 41, 57));
    assertEquals((byte) 255, bytes[57]);
    assertEquals(0, bytes[58]);
    assertEquals(0, bytes[59]);
    file.commit();
    assertArrayEquals(bytes, file.toByteArray());
  }

  @Test
  void computesImplicitExtendedMicIncludingExtensionItself() throws Exception {
    byte[] bytes = compile(extension(false)).toByteArray();
    var elements = wire(bytes);
    assertArrayEquals(
        HexFormat.of().parseHex("32747f77176772e9c0a3d79ec940896f"), value(elements, 7));
    verifyCmMic(elements);
  }

  @Test
  void computesExplicitExtendedMicBeforeCmMic() throws Exception {
    var file = compile(extension(true));
    byte[] bytes = file.toByteArray();
    var elements = wire(bytes);
    byte[] digest = value(wire(value(wire(value(elements, 43)), 6)), 3);
    assertArrayEquals(HexFormat.of().parseHex("7b978ce28c09ddce3bba7ffdf2186023"), digest);
    verifyCmMic(elements);
    verifyLegacyMic(elements);
    file.commit();
    assertArrayEquals(bytes, file.toByteArray());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "NetworkAccess 0;",
        "CmAdvancedBandPlanControl 2;",
        "FdxDownstreamUpperBandEdge 65536;",
        "DiplexerBandEdge { UpstreamUpperBandEdge 5; DownstreamLowerBandEdge 1;"
            + " DownstreamUpperBandEdge 0; }",
        "FddDiplexerBandEdge { UpstreamUpperBandEdge 684; }",
        "UpstreamServiceFlow { ServiceFlowReference 3; }",
        "UpstreamServiceFlow { ServiceFlowReference 0; QosParameterSetType 7; }",
        "UpstreamServiceFlow { ServiceFlowReference 2; QosParameterSetType 7; }",
        "UpstreamServiceFlow { ServiceFlowReference 3; QosParameterSetType 2; }"
      })
  void rejectsInvalidCompleteConfigurations(String extra) {
    assertThrows(IllegalArgumentException.class, () -> compile(extra));
  }

  @Test
  void rejectsMissingRequiredFieldsAndUnsupportedHash() {
    var imported = parse("Docsis-4.0 { NetworkAccess 1; }");
    var file = new ConfigurationFile(5, imported.getTlvBuilder(), "SHAREDSECRET");
    assertThrows(IllegalArgumentException.class, file::commit);
    assertThrows(
        UnsupportedOperationException.class,
        () -> compile(extension(false).replace("HmacType 1", "HmacType 43")));
    assertThrows(
        IllegalArgumentException.class,
        () -> compile(extension(false).replace("[10:00:00:C0:00:10]", "[90]")));
    assertThrows(
        IllegalArgumentException.class,
        () -> compile(extension(true).replace("[10:00:00:C0:00:10]", "[12]")));
  }

  @TestFactory
  Stream<DynamicTest> generatesCompleteLabFileForEverySupportedDefinition() {
    return paths(TlvRegistry.forFamily("docsis40"), List.of()).stream()
        .map(
            path ->
                DynamicTest.dynamicTest(
                    "I12 " + path,
                    () -> {
                      String extra = sample(path);
                      if (path.equals(List.of(24, 41)) || path.equals(List.of(25, 41))) {
                        extra =
                            path.getFirst() == 24
                                ? "UpstreamServiceFlow { ServiceFlowReference 3;"
                                    + " QosParameterSetType 7; MaxSustainedTrafficRate 10;"
                                    + " DataRateUnitSetting 3; }"
                                : "DownstreamServiceFlow { ServiceFlowReference 4;"
                                    + " QosParameterSetType 7; MaxSustainedTrafficRate 10;"
                                    + " DataRateUnitSetting 3; }";
                      }
                      String text = "Docsis-4.0 { " + BASE + extra + " }";
                      byte[] bytes = compile(extra).toByteArray();
                      var elements = wire(bytes);
                      assertTrue(contains(elements, path), "Missing generated path " + path);
                      verifyCmMic(elements);
                      if (!extra.contains("ExtendedCmtsMic") || path.equals(List.of(43, 6, 3))) {
                        verifyLegacyMic(elements);
                      }
                      assertEquals(0, bytes.length % 4);
                      assertTrue(
                          Arrays.stream(elements.toArray(byte[][]::new))
                              .anyMatch(element -> element[0] == 6));
                      assertTrue(elements.stream().anyMatch(element -> element[0] == 7));
                      var decoded =
                          parse(new ConfigurationFileExport(compile(extra)).toPrettyPrint(false));
                      var rebuilt =
                          new ConfigurationFile(5, decoded.getTlvBuilder(), "SHAREDSECRET");
                      rebuilt.commit();
                      assertArrayEquals(bytes, rebuilt.toByteArray());
                      Path directory = Path.of("target", "docsis40-lab");
                      Files.createDirectories(directory);
                      String name =
                          path.stream()
                              .map(String::valueOf)
                              .collect(java.util.stream.Collectors.joining("-"));
                      Files.writeString(directory.resolve(name + ".txt"), text);
                      Files.write(directory.resolve(name + ".bin"), bytes);
                    }));
  }

  private static String sample(List<Integer> path) {
    return switch (path.get(0)) {
      case 1 -> "DownstreamFrequency 100000000;";
      case 2 -> "UpstreamChannelId 7;";
      case 3, 6, 7 -> "";
      case 17 ->
          "BaselinePrivacy { AuthorizeWaitTimeout 10; ReauthorizeWaitTimeout 10;"
              + " AuthorizationGraceTime 600; OperationalWaitTimeout 10; RekeyWaitTimeout 10;"
              + " TekGraceTime 3600; AuthorizeRejectWaitTimeout 60; SaMapWaitTimeout 1;"
              + " SaMapMaxRetries 4; }";
      case 18 -> "MaxCPE 2;";
      case 19 -> "TftpTimestamp 1234567890;";
      case 20 -> "TftpModemAddress 192.0.2.1;";
      case 24 -> serviceFlow("UpstreamServiceFlow", 3);
      case 25 -> serviceFlow("DownstreamServiceFlow", 4);
      case 28 -> "MaxClassifiers 8;";
      case 29 -> "PrivacyEnable 1;";
      case 40 -> "EnableTestModes 0;";
      case 43 -> extension(path.equals(List.of(43, 6, 3)));
      case 55 -> "SnmpCpeAccessControl 0;";
      case 59 -> "TftpModemAddressIPv6 2001:db8:0:0:0:0:0:1;";
      case 68 -> "DefaultUpstreamTargetBuffer 50;";
      case 69 -> "MacAddressLearning { MacAddressLearningControl 1; MacAddressLearningHoldoff 2; }";
      case 76 -> "UpstreamAqmDisable 0;";
      case 83 -> "DtpModeConfiguration 1;";
      case 84 ->
          "DiplexerBandEdge { UpstreamUpperBandEdge 4; DownstreamLowerBandEdge 1;"
              + " DownstreamUpperBandEdge 0; }";
      case 91 ->
          "LowLatencyDisable 1;"
              + extension(false)
                  .replace("[10:00:00:C0:00:10]", "[10:00:00:C0:00:10:00:00:00:00:00:10]");
      case 96 ->
          "FddDiplexerBandEdge { UpstreamUpperBandEdge 684; DownstreamLowerBandEdge 834;"
              + " DownstreamUpperBandEdge 1794; }";
      case 97 -> "CmAdvancedBandPlanControl 1;";
      case 101 ->
          "DocsisSyncConfiguration { PtpMasterClockEnable 1; PtpProfile 3; PtpMaxClockSlaves 8;"
              + " SyncEClockEnable 1; }";
      case 102 ->
          "PtpAddressConfiguration { PtpSourceIpAddress 192.0.2.1; CmtsPtpIpAddress"
              + " 2001:db8:0:0:0:0:0:1; PtpDestinationMulticastMacAddress (01:80:C2:00:00:0E); }";
      case 103 ->
          "CmSshServerConfiguration { CommonSshConfiguration { SshNewConnectionTimeout 0;"
              + " SshInactivityTimeout 1800; SshEnabledInterfaces 1; SshSccaRevocationCheckDisable"
              + " 0; } }";
      case 106 -> "FdxDownstreamUpperBandEdge 1218;";
      default -> throw new AssertionError("Missing lab sample for " + path);
    };
  }

  @Test
  void encodesSyncConfigurationAndMixedFamilyAddressesExactly() {
    String sync = sample(List.of(101)) + sample(List.of(102));
    var imported = parse("Docsis-4.0 { " + sync + " }");
    assertArrayEquals(
        HexFormat.of()
            .parseHex(
                "650c01010102010303010804010166200104c0000201021020010db800000000000000000000000103060180c200000e"),
        imported.toByteArray());
    String text = new ConfigurationFileExport(imported.getTlvBuilder(), 5).toPrettyPrint(false);
    assertArrayEquals(imported.toByteArray(), parse(text).toByteArray());
    compile(sync);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "DocsisSyncConfiguration { PtpMasterClockEnable 0; }",
        "DocsisSyncConfiguration { PtpMasterClockEnable 3; }",
        "DocsisSyncConfiguration { SyncEClockEnable 0; }",
        "DocsisSyncConfiguration { PtpProfile 4; }",
        "DocsisSyncConfiguration { PtpProfile 1; PtpProfile 2; }",
        "PtpAddressConfiguration { PtpDestinationMulticastMacAddress (00:11:22:33:44:55); }"
      })
  void rejectsReservedSyncValues(String extra) {
    assertThrows(IllegalArgumentException.class, () -> compile(extra));
  }

  @Test
  void rejectsInvalidBinarySyncAddressLength() throws Exception {
    var imported = parse("Docsis-4.0 { " + BASE + " }");
    var builder = imported.getTlvBuilder();
    builder.add(102, HexFormat.of().parseHex("01050000000000"));
    var file = new ConfigurationFile(5, builder, "SHAREDSECRET");
    assertThrows(IllegalArgumentException.class, file::commit);
  }

  @Test
  void preservesTwoByteSshLengthAcrossCompilationAndExport() {
    String ssh = sample(List.of(103));
    var imported = parse("Docsis-4.0 { " + ssh + " }");
    assertArrayEquals(
        HexFormat.of().parseHex("6700140112010400000000020400000708030101050100"),
        imported.toByteArray());
    var file = compile(ssh);
    var exported = parse(new ConfigurationFileExport(file).toPrettyPrint(false));
    var rebuilt = new ConfigurationFile(5, exported.getTlvBuilder(), "SHAREDSECRET");
    rebuilt.commit();
    assertArrayEquals(file.toByteArray(), rebuilt.toByteArray());
  }

  @Test
  void stripsOnlyActuallyEmptyVariableWidthTlvs() throws Exception {
    var hex = HexFormat.of();
    var widths = java.util.Map.of(103, 2);
    byte[] ssh = hex.parseHex("6700050103030101");
    assertArrayEquals(ssh, com.comcast.oscar.tlv.TlvBuilder.stripZeroByteTLV(ssh, widths));
    assertArrayEquals(
        hex.parseHex("030101"),
        com.comcast.oscar.tlv.TlvBuilder.stripZeroByteTLV(hex.parseHex("670000030101"), widths));
    assertThrows(
        com.comcast.oscar.tlv.TlvException.class,
        () -> com.comcast.oscar.tlv.TlvBuilder.stripZeroByteTLV(hex.parseHex("67000501"), widths));
  }

  @Test
  void decodesValuesBeyondByteSizedOffsets() {
    StringBuilder extra = new StringBuilder();
    for (int reference = 3; reference < 12; reference++) {
      extra.append(serviceFlow("UpstreamServiceFlow", reference));
    }
    extra.append(sample(List.of(103)));
    var file = compile(extra.toString());
    assertTrue(file.toByteArray().length > 512);
    var imported = parse(new ConfigurationFileExport(file).toPrettyPrint(false));
    var rebuilt = new ConfigurationFile(5, imported.getTlvBuilder(), "SHAREDSECRET");
    rebuilt.commit();
    assertArrayEquals(file.toByteArray(), rebuilt.toByteArray());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "SshNewConnectionTimeout 28801;",
        "SshInactivityTimeout 86401;",
        "SshEnabledInterfaces 2;"
      })
  void rejectsInvalidSshSettings(String setting) {
    assertThrows(
        IllegalArgumentException.class,
        () -> compile("CmSshServerConfiguration { CommonSshConfiguration { " + setting + " } }"));
  }

  @ParameterizedTest
  @org.junit.jupiter.params.provider.CsvSource({
    "AuthorizeWaitTimeout,1,30",
    "ReauthorizeWaitTimeout,1,30",
    "AuthorizationGraceTime,1,6047999",
    "OperationalWaitTimeout,1,10",
    "RekeyWaitTimeout,1,10",
    "TekGraceTime,1,302399",
    "AuthorizeRejectWaitTimeout,1,600",
    "SaMapWaitTimeout,1,10",
    "SaMapMaxRetries,0,10"
  })
  void validatesSec4BpiWireBounds(String name, long minimum, long maximum) {
    compile("BaselinePrivacy { " + name + " " + minimum + "; }");
    compile("BaselinePrivacy { " + name + " " + maximum + "; }");
    assertThrows(
        IllegalArgumentException.class,
        () -> compile("BaselinePrivacy { " + name + " " + (minimum - 1) + "; }"));
    assertThrows(
        IllegalArgumentException.class,
        () -> compile("BaselinePrivacy { " + name + " " + (maximum + 1) + "; }"));
  }

  @Test
  void encodesSec4ZeroRetryCountAsFourOctets() {
    var imported = parse("Docsis-4.0 { BaselinePrivacy { SaMapMaxRetries 0; } }");
    assertArrayEquals(HexFormat.of().parseHex("1106090400000000"), imported.toByteArray());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "PrivacyEnable 2;",
        "EnableTestModes 2;",
        "SnmpCpeAccessControl 2;",
        "UpstreamAqmDisable 2;",
        "DtpModeConfiguration 0;",
        "DtpModeConfiguration 3;",
        "MacAddressLearning { MacAddressLearningControl 2; }",
        "MacAddressLearning { MacAddressLearningHoldoff 11; }",
        "LowLatencyDisable 1;"
      })
  void rejectsReservedProvisioningControlsAndUnprotectedLowLatency(String extra) {
    assertThrows(IllegalArgumentException.class, () -> compile(extra));
  }

  private static List<List<Integer>> paths(TlvRegistry registry, List<Integer> parent) {
    List<List<Integer>> result = new ArrayList<>();
    for (var child : registry.children(parent)) {
      result.add(child.path());
      result.addAll(paths(registry, child.path()));
    }
    return result;
  }

  private static String serviceFlow(String direction, int reference) {
    return direction
        + " { ServiceFlowReference "
        + reference
        + "; QosParameterSetType 7;"
        + " ServiceClassName LAB; TrafficPriority 7; MaxSustainedTrafficRate 3000000000;"
        + " MaxTrafficBurst 3044; MinReservedTrafficRate 100000000;"
        + " AssumedMinReservedRatePacketSize 1000; TimeoutActiveQos 30;"
        + " TimeoutAdmittedQos 200; PeakTrafficRate 4000000000; "
        + aqm()
        + " }";
  }

  private static String aqm() {
    return "AqmEncodings { SfAqmDisable 0; ClassicAqmLatencyTarget 10; AqmAlgorithm 2;"
        + " ImmediateAqmMaxThreshold 1000; ImmediateAqmRangeExponent 19;"
        + " LatencyHistogramEncodings [00:0A:00:64:03:E8]; }";
  }

  @ParameterizedTest
  @ValueSource(strings = {"UpstreamServiceFlow", "DownstreamServiceFlow"})
  void encodesAqmWithSourceDerivedWidthsAndHistogramOrder(String direction) throws Exception {
    String settings =
        direction + " { ServiceFlowReference 3; QosParameterSetType 7; " + aqm() + " }";
    byte[] expected =
        HexFormat.of()
            .parseHex("002101020003060107281801010002010a030102040203e80501130606000a006403e8");
    expected[0] = (byte) (direction.equals("UpstreamServiceFlow") ? 24 : 25);
    assertArrayEquals(expected, parse("Docsis-4.0 { " + settings + " }").toByteArray());
    var file = compile(settings);
    verifyCmMic(wire(file.toByteArray()));
    verifyLegacyMic(wire(file.toByteArray()));
    var imported = parse(new ConfigurationFileExport(file).toPrettyPrint(false));
    var rebuilt = new ConfigurationFile(5, imported.getTlvBuilder(), "SHAREDSECRET");
    rebuilt.commit();
    assertArrayEquals(file.toByteArray(), rebuilt.toByteArray());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "SfAqmDisable 2;",
        "AqmAlgorithm 3;",
        "ImmediateAqmRangeExponent 26;",
        "LatencyHistogramEncodings [00];",
        "LatencyHistogramEncodings [00:01:00];",
        "LatencyHistogramEncodings [00:01:00:01];",
        "LatencyHistogramEncodings [00:02:00:01];",
        "AqmAlgorithm 1; AqmAlgorithm 2;"
      })
  void rejectsReservedAqmValuesAndMalformedHistograms(String setting) {
    for (String direction : List.of("UpstreamServiceFlow", "DownstreamServiceFlow")) {
      assertThrows(
          IllegalArgumentException.class,
          () ->
              compile(
                  direction
                      + " { ServiceFlowReference 3; QosParameterSetType 7; AqmEncodings { "
                      + setting
                      + " } }"));
    }
  }

  @Test
  void acceptsAqmWireBoundariesWithoutEnforcingRecommendations() {
    compile(
        "UpstreamServiceFlow { ServiceFlowReference 3; QosParameterSetType 7; AqmEncodings {"
            + " SfAqmDisable 1; ClassicAqmLatencyTarget 0; AqmAlgorithm 0; ImmediateAqmMaxThreshold"
            + " 0; ImmediateAqmRangeExponent 0; LatencyHistogramEncodings [00:00]; } }");
    compile(
        "DownstreamServiceFlow { ServiceFlowReference 3; QosParameterSetType 7; AqmEncodings {"
            + " ClassicAqmLatencyTarget 255; AqmAlgorithm 1; ImmediateAqmMaxThreshold 65535;"
            + " ImmediateAqmRangeExponent 25; LatencyHistogramEncodings"
            + " [00:00:00:01:00:02:00:03:00:04:00:05:00:06:00:07:00:08:00:09:00:0A:00:0B:00:0C:00:0D:FF:FF];"
            + " } }");
    assertThrows(
        IllegalArgumentException.class,
        () ->
            compile(
                "DownstreamServiceFlow { ServiceFlowReference 3; QosParameterSetType 7;"
                    + " AqmEncodings { LatencyHistogramEncodings"
                    + " [00:00:00:01:00:02:00:03:00:04:00:05:00:06:00:07:00:08:00:09:00:0A:00:0B:00:0C:00:0D:00:0E:00:0F];"
                    + " } }"));
  }

  @Test
  void encodesUnsignedServiceRatesWithExactI12Width() {
    var imported =
        parse(
            "Docsis-4.0 { DownstreamServiceFlow { ServiceFlowReference 1;"
                + " QosParameterSetType 7; MaxSustainedTrafficRate 3000000000; } }");
    assertArrayEquals(
        HexFormat.of().parseHex("190d010200010601070804b2d05e00"), imported.toByteArray());
    var exported = new ConfigurationFileExport(imported.getTlvBuilder(), 5);
    assertArrayEquals(imported.toByteArray(), parse(exported.toPrettyPrint(false)).toByteArray());
  }

  @ParameterizedTest
  @ValueSource(strings = {"UpstreamServiceFlow", "DownstreamServiceFlow"})
  void encodesTenGigabitServiceRatesWithoutChangingWireWidth(String direction) throws Exception {
    String settings =
        direction
            + " { ServiceFlowReference 3; QosParameterSetType 7; MaxSustainedTrafficRate 10;"
            + " MinReservedTrafficRate 5; PeakTrafficRate 12; DataRateUnitSetting 3; }";
    byte[] expected =
        HexFormat.of().parseHex("001c0102000306010708040000000a0a04000000051b040000000c290103");
    expected[0] = (byte) (direction.equals("UpstreamServiceFlow") ? 24 : 25);
    assertArrayEquals(expected, parse("Docsis-4.0 { " + settings + " }").toByteArray());
    var file = compile(settings);
    verifyCmMic(wire(file.toByteArray()));
    verifyLegacyMic(wire(file.toByteArray()));
    var imported = parse(new ConfigurationFileExport(file).toPrettyPrint(false));
    var rebuilt = new ConfigurationFile(5, imported.getTlvBuilder(), "SHAREDSECRET");
    rebuilt.commit();
    assertArrayEquals(file.toByteArray(), rebuilt.toByteArray());
  }

  @ParameterizedTest
  @ValueSource(strings = {"UpstreamServiceFlow", "DownstreamServiceFlow"})
  void validatesDataRateUnitsWithoutScalingBurstSizes(String direction) {
    String start = direction + " { ServiceFlowReference 3; QosParameterSetType 7;";
    for (int unit = 0; unit <= 3; unit++) {
      compile(
          start
              + " MaxSustainedTrafficRate 10; MinReservedTrafficRate 5; DataRateUnitSetting "
              + unit
              + "; }");
    }
    assertThrows(
        IllegalArgumentException.class, () -> compile(start + " DataRateUnitSetting 4; }"));
    assertThrows(
        IllegalArgumentException.class, () -> compile(start + " DataRateUnitSetting 255; }"));
    assertThrows(
        IllegalArgumentException.class,
        () -> compile(start + " DataRateUnitSetting 1; DataRateUnitSetting 2; }"));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            compile(
                start
                    + " MaxSustainedTrafficRate 10; MinReservedTrafficRate 11; DataRateUnitSetting"
                    + " 3; }"));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            compile(
                start
                    + " MaxSustainedTrafficRate 10; MaxTrafficBurst 1521; DataRateUnitSetting 3;"
                    + " }"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "TrafficPriority 8;",
        "MaxSustainedTrafficRate 100000; MinReservedTrafficRate 100001;",
        "MaxSustainedTrafficRate 100000; MaxTrafficBurst 1521;",
        "MaxSustainedTrafficRate 4294967296;"
      })
  void rejectsInvalidServiceRates(String qos) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            compile(
                "UpstreamServiceFlow {"
                    + " ServiceFlowReference 3; QosParameterSetType 7; "
                    + qos
                    + " }"));
  }

  private static String extension(boolean explicit) {
    return "DocsisExtension { VendorId [FF:FF:FF]; ExtendedCmtsMic { HmacType 1; MicBitmap"
        + " [10:00:00:C0:00:10]; "
        + (explicit ? "ExplicitDigest [00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00];" : "")
        + " } }";
  }

  @Test
  void requiresGeneralExtensionVendorIdFirst() {
    assertThrows(
        IllegalArgumentException.class,
        () -> compile(extension(false).replace("VendorId [FF:FF:FF];", "")));
    assertThrows(
        IllegalArgumentException.class,
        () -> compile(extension(false).replace("[FF:FF:FF]", "[00:00:01]")));
    String wrongOrder = extension(false).replace("VendorId [FF:FF:FF];", "");
    assertArrayEquals(
        compile(extension(false)).toByteArray(),
        compile(wrongOrder.substring(0, wrongOrder.length() - 1) + "VendorId [FF:FF:FF]; }")
            .toByteArray());
  }

  private static List<byte[]> wire(byte[] bytes) {
    List<byte[]> elements = new ArrayList<>();
    int offset = 0;
    while (offset < bytes.length) {
      int type = Byte.toUnsignedInt(bytes[offset]);
      if (type == 255) {
        for (int index = offset + 1; index < bytes.length; index++) assertEquals(0, bytes[index]);
        break;
      }
      int width = type == 103 || type == 104 ? 2 : 1;
      assertTrue(offset + width < bytes.length, "Truncated header");
      int length = Byte.toUnsignedInt(bytes[offset + 1]);
      if (width == 2) length = (length << 8) | Byte.toUnsignedInt(bytes[offset + 2]);
      int end = offset + 1 + width + length;
      assertTrue(end <= bytes.length, "Truncated value");
      elements.add(Arrays.copyOfRange(bytes, offset, end));
      offset = end;
    }
    return elements;
  }

  private static byte[] value(List<byte[]> elements, int type) {
    byte[] element =
        elements.stream()
            .filter(bytes -> Byte.toUnsignedInt(bytes[0]) == type)
            .findFirst()
            .orElseThrow();
    return Arrays.copyOfRange(element, type == 103 || type == 104 ? 3 : 2, element.length);
  }

  private static boolean contains(List<byte[]> elements, List<Integer> path) {
    for (byte[] element : elements) {
      if (Byte.toUnsignedInt(element[0]) != path.get(0)) continue;
      if (path.size() == 1
          || contains(wire(value(List.of(element), path.get(0))), path.subList(1, path.size())))
        return true;
    }
    return false;
  }

  private static void verifyCmMic(List<byte[]> elements) throws Exception {
    var digest = MessageDigest.getInstance("MD5");
    elements.stream().filter(bytes -> bytes[0] != 6 && bytes[0] != 7).forEach(digest::update);
    assertArrayEquals(digest.digest(), value(elements, 6));
  }

  private static void verifyLegacyMic(List<byte[]> elements) throws Exception {
    var mac = Mac.getInstance("HmacMD5");
    mac.init(
        new SecretKeySpec(
            "SHAREDSECRET".getBytes(java.nio.charset.StandardCharsets.US_ASCII), "HmacMD5"));
    for (int type :
        List.of(1, 2, 3, 17, 43, 6, 18, 19, 20, 59, 22, 23, 24, 25, 28, 29, 35, 36, 37, 40)) {
      elements.stream().filter(bytes -> Byte.toUnsignedInt(bytes[0]) == type).forEach(mac::update);
    }
    assertArrayEquals(mac.doFinal(), value(elements, 7));
  }

  @Test
  void exportsVersionedBandControlsAndReimportsThem() {
    var imported = parse("Docsis-4.0 { FdxDownstreamUpperBandEdge 1218; }");
    var exported = new ConfigurationFileExport(imported.getTlvBuilder(), 5);
    assertEquals(5, exported.getConfigurationFileType());
    String text = exported.toPrettyPrint(false);
    assertTrue(text.contains("Docsis-4.0{"), text);
    assertTrue(text.contains("FdxDownstreamUpperBandEdge"), text);
    assertArrayEquals(imported.toByteArray(), parse(text).toByteArray());
  }

  static ConfigurationFile compile(String extra) {
    var imported = parse("Docsis-4.0 { " + BASE + extra + " }");
    var file = new ConfigurationFile(5, imported.getTlvBuilder(), "SHAREDSECRET");
    file.commit();
    return file;
  }

  @Test
  void doesNotExposeSharedKeyInConfigurationDiagnostics() {
    var imported = parse("Docsis-4.0 { " + BASE + " }");
    var file = new ConfigurationFile(5, imported.getTlvBuilder(), "public-lab-diagnostic-marker");
    assertFalse(file.toString().contains("public-lab-diagnostic-marker"));
    assertTrue(file.toString().contains("[redacted]"));
  }

  @Test
  void recompilesExportedCompleteFile() {
    var original = compile("FdxDownstreamUpperBandEdge 1218;");
    var exported = new ConfigurationFileExport(original);
    var imported = parse(exported.toPrettyPrint(false));
    assertEquals(5, imported.getConfigurationFileType());
    var rebuilt = new ConfigurationFile(5, imported.getTlvBuilder(), "SHAREDSECRET");
    rebuilt.commit();
    assertArrayEquals(original.toByteArray(), rebuilt.toByteArray());
  }

  @Test
  void preservesUnsigned32BitValuesAndRejectsOverflow() {
    var imported = parse("Docsis-4.0 { TftpTimestamp 4294967295; }");
    assertArrayEquals(HexFormat.of().parseHex("1304ffffffff"), imported.toByteArray());
    var exported = new ConfigurationFileExport(imported.getTlvBuilder(), 5);
    String text = exported.toPrettyPrint(false);
    assertTrue(text.contains("4294967295"), text);
    assertArrayEquals(imported.toByteArray(), parse(text).toByteArray());
    assertThrows(
        IllegalArgumentException.class,
        () -> parse("Docsis-4.0 { TftpTimestamp 4294967296; }").toByteArray());
    assertThrows(
        IllegalArgumentException.class,
        () -> parse("Docsis-4.0 { TftpTimestamp -1; }").toByteArray());
  }

  @ParameterizedTest
  @ValueSource(strings = {"d", "docsis", "d4", "d4.0", "d40", "docsis4", "docsis4.0", "docsis40"})
  void selectsD4CliAliasesWithoutRetainingOtherFamilyState(String alias) {
    var specification = new com.comcast.oscar.cli.commands.Specification();
    specification.setValues(new String[] {"p", "2.0"});
    specification.setValues(new String[] {alias, "4.0"});
    assertEquals(5, specification.getConfigurationFileType());
    assertEquals(TlvDictionary.DOCSIS_40_DICTIONARY_TABLE_NAME, specification.getTlvDisassemble());
    assertFalse(specification.isPacketCable());
  }

  @Test
  void loadsIndependentI12Catalog() {
    var registry = TlvRegistry.forFamily("docsis40");
    assertEquals(2, registry.find(List.of(96, 1)).lengthMin());
    assertEquals(12, registry.find(List.of(96)).lengthMin());
    assertEquals(1, registry.find(List.of(97)).lengthMin());
    assertEquals(2, registry.find(List.of(106)).lengthMin());
    for (var path :
        List.of(
            List.of(5),
            List.of(65),
            List.of(85),
            List.of(105),
            List.of(201),
            List.of(43, 5),
            List.of(24, 2),
            List.of(25, 2))) {
      assertNull(registry.find(path), path.toString());
    }
    assertNull(TlvRegistry.forFamily("docsis").find(List.of(106)));
  }

  @Test
  void encodesI12BandControlsWithSourceDerivedWidths() {
    var imported =
        parse(
            """
            Docsis-4.0 {
              DiplexerBandEdge {
                UpstreamUpperBandEdge 4;
                DownstreamLowerBandEdge 1;
                DownstreamUpperBandEdge 0;
              }
              FddDiplexerBandEdge {
                UpstreamUpperBandEdge 684;
                DownstreamLowerBandEdge 834;
                DownstreamUpperBandEdge 1794;
              }
              CmAdvancedBandPlanControl 1;
              FdxDownstreamUpperBandEdge 1218;
            }
            """);
    assertEquals(5, imported.getConfigurationFileType());
    assertArrayEquals(
        new byte[] {
          84,
          9,
          1,
          1,
          4,
          2,
          1,
          1,
          3,
          1,
          0,
          96,
          12,
          1,
          2,
          2,
          (byte) 172,
          2,
          2,
          3,
          66,
          3,
          2,
          7,
          2,
          97,
          1,
          1,
          106,
          2,
          4,
          (byte) 194
        },
        imported.toByteArray());
    assertArrayEquals(imported.toByteArray(), imported.getTlvBuilder().toByteArray());
  }

  @Test
  void rejectsUnverifiedAndWrongEditionNames() {
    assertThrows(
        IllegalArgumentException.class, () -> parse("Docsis { FdxDownstreamUpperBandEdge 1218; }"));
    assertThrows(
        IllegalArgumentException.class,
        () -> parse("Docsis-4.0 { ClassOfService { ClassID 1; } }"));
    assertThrows(
        IllegalArgumentException.class,
        () -> parse("Docsis-4.0 { UpstreamServiceFlow { ServiceFlowID 1; } }"));
  }

  static ConfigurationFileImport parse(String text) {
    return new ConfigurationFileImport(new StringBuilder(text));
  }
}
