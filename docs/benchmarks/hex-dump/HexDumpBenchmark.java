/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
import com.comcast.oscar.utilities.HexDump;

/** Comparative harness; see README for methodology and output corrections. */
public class HexDumpBenchmark {
  private static volatile String sink;
  public static void main(String[] args) {
    String mode = args[0];
    int size = Integer.parseInt(args[1]);
    byte[][] inputs = new byte[16][size];
    for (int row = 0; row < inputs.length; row++) {
      java.util.Arrays.fill(inputs[row], (byte) ('A' + row));
    }
    int operations = Math.max(128, 65536 / size);
    for (int round = -8; round < 7; round++) {
      long started = System.nanoTime();
      for (int operation = 0; operation < operations; operation++) {
        byte[] input = inputs[operation % inputs.length];
        sink = mode.equals("baseline") ? LegacyHexDump.dumpHexString(input) : HexDump.dumpHexString(input);
      }
      long elapsed = System.nanoTime() - started;
      if (round >= 0) System.out.printf(java.util.Locale.ROOT, "%s,%d,%d,%.3f,%d%n",
          mode, size, round, (double) elapsed / operations, sink.length());
    }
  }
}
