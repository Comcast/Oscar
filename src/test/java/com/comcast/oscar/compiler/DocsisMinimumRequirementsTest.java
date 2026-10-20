/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.compiler;

import static com.comcast.oscar.configurationfile.ConfigurationFileTypeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.DocsisVersion;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.utilities.HexString;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DocsisMinimumRequirementsTest {
  private static final Path EXAMPLES = Path.of("examples/docsis/3.1/minimum");
  // Independent wire fixture: NetworkAccess and US/DS references plus QoS set type.
  private static final byte[] PARAMETERS =
      HexFormat.of().parseHex("030101180701020001060107190701020002060107");
  @TempDir Path temporary;

  @ParameterizedTest
  @ValueSource(
      strings = {"missing-network-access", "missing-upstream", "missing-downstream", "missing-all"})
  void rejectsNegativeTextFilesAndTheirBinaryRepresentations(String name) throws Exception {
    Path path = EXAMPLES.resolve(name + ".txt");
    String text = Files.readString(path);
    var draft = new ConfigurationFileImport(new StringBuilder(text));
    var file =
        new ConfigurationFile(
            DOCSIS_31_CONFIGURATION_TYPE, draft.getTlvBuilder(), "public-lab-key");
    var expected = assertThrows(IllegalArgumentException.class, file::commit).getMessage();
    assertTrue(expected.startsWith("Missing required DOCSIS top-level TLVs:"));
    assertEquals(
        expected,
        assertThrows(
                IllegalArgumentException.class,
                () ->
                    DocsisMinimumRequirements.validateFile(
                        path, DocsisMinimumRequirements.InputFormat.TEXT, DocsisVersion.DOCSIS_3_1))
            .getMessage());
    byte[] finalizedShape = Arrays.copyOf(draft.toByteArray(), draft.toByteArray().length + 37);
    int offset = draft.toByteArray().length;
    finalizedShape[offset] = 6;
    finalizedShape[offset + 1] = 16;
    finalizedShape[offset + 18] = 7;
    finalizedShape[offset + 19] = 16;
    finalizedShape[offset + 36] = (byte) 255;
    Path binary = temporary.resolve(name + ".cfg");
    Files.write(binary, finalizedShape);
    assertEquals(
        expected,
        assertThrows(
                IllegalArgumentException.class,
                () ->
                    DocsisMinimumRequirements.validateFile(
                        binary,
                        DocsisMinimumRequirements.InputFormat.BINARY,
                        DocsisVersion.DOCSIS_3_1))
            .getMessage());
  }

  @Test
  void validatesBothFileFormatsWithoutChangingBytes() throws Exception {
    Path text = EXAMPLES.resolve("valid-minimum.txt");
    DocsisMinimumRequirements.validateFile(
        text, DocsisMinimumRequirements.InputFormat.TEXT, DocsisVersion.DOCSIS_3_1);
    var parsed = new ConfigurationFileImport(new StringBuilder(Files.readString(text)));
    assertArrayEquals(PARAMETERS, parsed.toByteArray());
    var file =
        new ConfigurationFile(
            DOCSIS_31_CONFIGURATION_TYPE, parsed.getTlvBuilder(), "public-lab-key");
    file.commit();
    byte[] original = file.toByteArray().clone();
    Path binary = temporary.resolve("minimum.cfg");
    Files.write(binary, original);
    DocsisMinimumRequirements.validateFile(
        binary, DocsisMinimumRequirements.InputFormat.BINARY, DocsisVersion.DOCSIS_3_1);
    assertArrayEquals(original, Files.readAllBytes(binary));
    for (int type : List.of(3, 24, 25, 6, 7)) {
      byte[] incomplete = TlvBuilder.stripTlv(type, original);
      assertTrue(
          assertThrows(
                  IllegalArgumentException.class,
                  () ->
                      DocsisMinimumRequirements.validateComplete(
                          incomplete, DocsisVersion.DOCSIS_3_1))
              .getMessage()
              .contains(type + " ("));
    }
    byte[] noEnd = Arrays.copyOf(original, PARAMETERS.length + 36);
    assertTrue(
        assertThrows(
                IllegalArgumentException.class,
                () -> DocsisMinimumRequirements.validateComplete(noEnd, DocsisVersion.DOCSIS_3_1))
            .getMessage()
            .contains("255 ("));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4})
  void compilerChecksRawParametersAndDoesNotReusePreviousFinalization(int version)
      throws Exception {
    var compiler = new DocsisCompiler("public-lab-key", version);
    assertFalse(compiler.checkSpecification(false));
    compiler.add(new HexString(PARAMETERS));
    assertTrue(compiler.checkSpecification(false));
    compiler.commit();
    DocsisMinimumRequirements.validateComplete(
        compiler.toByteArray(), DocsisVersion.fromConfigurationFileType(version));
    compiler.clear();
    assertFalse(compiler.checkSpecification(false));
    assertThrows(IllegalArgumentException.class, compiler::commit);
  }

  @Test
  void checksOnlyTopLevelAndRejectsMalformedFramingAndWrongFamily() {
    // NetworkAccess nested inside a vendor payload must not satisfy top-level 3.
    assertThrows(
        IllegalArgumentException.class,
        () ->
            DocsisMinimumRequirements.validateParameters(
                HexFormat.of().parseHex("2b03030101"), DocsisVersion.DOCSIS_3_1));
    for (String hex : List.of("03", "030201", "0300", "ff03")) {
      assertThrows(
          IllegalArgumentException.class,
          () ->
              DocsisMinimumRequirements.validateComplete(
                  HexFormat.of().parseHex(hex), DocsisVersion.DOCSIS_3_1));
    }
    assertThrows(
        IllegalArgumentException.class,
        () ->
            DocsisMinimumRequirements.validateParameters(
                PARAMETERS, DocsisVersion.fromConfigurationFileType(DPOE_20_CONFIGURATION_TYPE)));
    assertThrows(
        IllegalArgumentException.class,
        () -> DocsisMinimumRequirements.validateText("Docsis {}", DocsisVersion.DOCSIS_4_0));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            DocsisMinimumRequirements.validateText(
                "Docsis { NetworkAccess 1; } garbage", DocsisVersion.DOCSIS_3_1));
  }

  @Test
  void cliCompilesValidExampleAndDoesNotWriteRejectedExample() throws Exception {
    for (String name : List.of("valid-minimum", "missing-upstream")) {
      Path output = temporary.resolve(name + ".bin");
      Path log = temporary.resolve(name + ".log");
      var process =
          new ProcessBuilder(
                  Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                  "-cp",
                  System.getProperty(
                      "surefire.test.class.path", System.getProperty("java.class.path")),
                  "com.comcast.oscar.cli.Main",
                  "-c",
                  "-s",
                  "d",
                  "3.1",
                  "-k",
                  "examples/docsis/3.1/DOCSIS-LAB.key",
                  "-i",
                  EXAMPLES.resolve(name + ".txt").toString(),
                  "-o",
                  output.toString())
              .redirectErrorStream(true)
              .redirectOutput(log.toFile())
              .start();
      boolean finished = process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
      if (!finished) process.destroyForcibly();
      assertTrue(finished, "CLI timeout");
      if (name.equals("valid-minimum")) {
        assertEquals(0, process.exitValue(), Files.readString(log));
        DocsisMinimumRequirements.validateFile(
            output, DocsisMinimumRequirements.InputFormat.BINARY, DocsisVersion.DOCSIS_3_1);
      } else {
        assertNotEquals(0, process.exitValue());
        assertFalse(Files.exists(output));
        assertTrue(Files.readString(log).contains("24 (UpstreamServiceFlow)"));
      }
    }
  }

  @ParameterizedTest
  @org.junit.jupiter.params.provider.EnumSource(DocsisVersion.class)
  void protocolRevisionConvertsAtLegacyBoundary(DocsisVersion docsisVersion) {
    assertSame(
        docsisVersion,
        DocsisVersion.fromConfigurationFileType(docsisVersion.configurationFileType()));
  }

  @Test
  void rejectsUnknownLegacyCodesAndNullApiInputs() {
    assertThrows(IllegalArgumentException.class, () -> DocsisVersion.fromConfigurationFileType(-1));
    assertThrows(
        IllegalArgumentException.class,
        () -> DocsisVersion.fromConfigurationFileType(DPOE_20_CONFIGURATION_TYPE));
    assertThrows(
        NullPointerException.class,
        () -> DocsisMinimumRequirements.validateParameters(PARAMETERS, null));
    assertThrows(
        NullPointerException.class,
        () -> DocsisMinimumRequirements.validateComplete(null, DocsisVersion.DOCSIS_3_1));
    assertThrows(
        NullPointerException.class,
        () -> DocsisMinimumRequirements.validateText(null, DocsisVersion.DOCSIS_3_1));
    assertThrows(
        NullPointerException.class,
        () ->
            DocsisMinimumRequirements.validateFile(
                null, DocsisMinimumRequirements.InputFormat.TEXT, DocsisVersion.DOCSIS_3_1));
    assertThrows(
        NullPointerException.class,
        () ->
            DocsisMinimumRequirements.validateFile(
                EXAMPLES.resolve("valid-minimum.txt"), null, DocsisVersion.DOCSIS_3_1));
  }

  @Test
  void allPublishedCompleteDocsisTextExamplesMeetMinimum() throws Exception {
    try (var files = Files.walk(Path.of("examples/docsis"))) {
      for (Path file :
          files
              .filter(p -> p.toString().endsWith(".txt") && !p.toString().contains("missing-"))
              .toList()) {
        int version =
            file.toString().contains("4.0")
                ? DOCSIS_40_CONFIGURATION_TYPE
                : DOCSIS_31_CONFIGURATION_TYPE;
        DocsisMinimumRequirements.validateFile(
            file,
            DocsisMinimumRequirements.InputFormat.TEXT,
            DocsisVersion.fromConfigurationFileType(version));
        var imported = new ConfigurationFileImport(new StringBuilder(Files.readString(file)));
        var compiled = new ConfigurationFile(version, imported.getTlvBuilder(), "SHAREDSECRET");
        compiled.commit();
        DocsisMinimumRequirements.validateComplete(
            compiled.toByteArray(), DocsisVersion.fromConfigurationFileType(version));
      }
    }
  }
}
