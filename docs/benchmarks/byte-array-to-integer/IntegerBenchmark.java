/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
import com.comcast.oscar.utilities.BinaryConversion;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/** Standalone comparative harness; see README for limitations and reproduction. */
public class IntegerBenchmark {
  private static volatile long sink;
  private static final int OPERATIONS = 200_000;
  private static final int WARMUP_ROUNDS = 8;
  private static final int MEASURED_ROUNDS = 7;
  public static void main(String[] args) {
    String mode = args[0];
    int size = Integer.parseInt(args[1]);
    byte[][] inputs = new byte[256][size];
    Random random = new Random(719);
    for (byte[] input : inputs) random.nextBytes(input);
    for (int round = -WARMUP_ROUNDS; round < MEASURED_ROUNDS; round++) {
      long sum = 0;
      long started = System.nanoTime();
      for (int index = 0; index < OPERATIONS; index++) {
        byte[] input = inputs[index % inputs.length];
        sum += switch (mode) {
          case "baseline" -> baseline(input);
          case "builtin" -> builtin(input);
          case "current" -> BinaryConversion.byteArrayToInteger(input);
          default -> throw new IllegalArgumentException(mode);
        };
      }
      long elapsed = System.nanoTime() - started;
      sink = sum;
      if (round >= 0) System.out.printf(java.util.Locale.ROOT, "%s,%d,%d,%.3f,%d%n",
          mode, size, round, (double) elapsed / OPERATIONS, sum);
    }
  }
  private static int builtin(byte[] input) {
    if (input.length >= Integer.BYTES) return ByteBuffer.wrap(input).getInt();
    ByteBuffer padded = ByteBuffer.allocate(Integer.BYTES);
    padded.position(Integer.BYTES - input.length);
    padded.put(input);
    return padded.getInt(0);
  }
  public static int baseline(byte[] bByteArray) {

    ByteArrayOutputStream baosByteToInt = new ByteArrayOutputStream();

    while ((baosByteToInt.size() + bByteArray.length) < 4) {
      baosByteToInt.write(0x00);
    }

    try {
      baosByteToInt.write(bByteArray);
    } catch (IOException e) {
      e.printStackTrace();
    }

    byte[] bByteToInt = baosByteToInt.toByteArray();

    return bByteToInt[0] << 24
        | (bByteToInt[1] & 0xFF) << 16
        | (bByteToInt[2] & 0xFF) << 8
        | (bByteToInt[3] & 0xFF);
  }
}
