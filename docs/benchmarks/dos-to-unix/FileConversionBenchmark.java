/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
import com.comcast.oscar.utilities.FileConversions;
import com.comcast.oscar.utilities.HexString;
import java.nio.charset.StandardCharsets;

/** Comparative harness; the retained baseline has incorrect output. See README. */
public class FileConversionBenchmark {
  private static volatile byte[] sink;
  public static void main(String[] args) {
    String mode = args[0];
    int size = Integer.parseInt(args[1]);
    byte[][] inputs = new byte[16][size];
    for (int row = 0; row < inputs.length; row++) {
      java.util.Arrays.fill(inputs[row], (byte) ('a' + row));
      for (int index = 14; index + 1 < size; index += 16) {
        inputs[row][index] = '\r';
        inputs[row][index + 1] = '\n';
      }
    }
    int operations = Math.max(8, 65536 / size);
    for (int round = -4; round < 5; round++) {
      long started = System.nanoTime();
      for (int operation = 0; operation < operations; operation++) {
        byte[] input = inputs[operation % inputs.length];
        sink = switch (mode) {
          case "baseline" -> baseline(input);
          case "simple" -> simple(input);
          case "loops" -> loops(input);
          case "current" -> FileConversions.dosToUnix(input);
          default -> throw new IllegalArgumentException(mode);
        };
      }
      long elapsed = System.nanoTime() - started;
      if (round >= 0) System.out.printf(java.util.Locale.ROOT, "%s,%d,%d,%.3f,%d%n",
          mode, size, round, (double) elapsed / operations, sink.length);
    }
  }
  private static byte[] baseline(byte[] input) {
    String text = HexString.toAsciiBinary(input).replaceAll("\r\n", "\n");
    return HexString.toByteArray(HexString.asciiToHex(text));
  }
  private static byte[] simple(byte[] input) {
    return new String(input, StandardCharsets.ISO_8859_1)
        .replace("\r\n", "\n")
        .getBytes(StandardCharsets.ISO_8859_1);
  }
  private static byte[] loops(byte[] input) {
    int removedBytes = 0;
    for (int index = 0; index + 1 < input.length; index++) {
      if (input[index] == '\r' && input[index + 1] == '\n') removedBytes++;
    }

    byte[] output = new byte[input.length - removedBytes];
    int destination = 0;
    for (int index = 0; index < input.length; index++) {
      if (input[index] == '\r' && index + 1 < input.length && input[index + 1] == '\n') {
        continue;
      }
      output[destination++] = input[index];
    }
    return output;
  }
}
