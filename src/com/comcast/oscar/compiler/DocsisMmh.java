package com.comcast.oscar.compiler;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class DocsisMmh {
  private DocsisMmh() {}

  public static byte[] digest(byte[] secret, byte[] message) {
    if (secret.length == 0) throw new IllegalArgumentException("MMH requires a nonempty secret");
    byte[] first = new byte[(secret.length + 1) / 2];
    byte[] second = new byte[secret.length / 2];
    for (int index = 0; index < secret.length; index++) {
      if (index % 2 == 0) first[index / 2] = secret[index];
      else second[index / 2] = secret[index];
    }
    byte[] padded = Arrays.copyOf(message, (message.length + 1) / 2 * 2);
    byte[] key = expand(first, "CMTS-EMIC", padded.length + 6);
    ByteBuffer hash = ByteBuffer.allocate(8);
    for (int output = 0; output < 4; output++) {
      int sum = 0;
      for (int offset = 0; offset < padded.length; offset += 2) {
        sum += word(key, offset + output * 2) * word(padded, offset);
      }
      hash.putShort((short) (Math.floorMod(sum, 65537) & 65535));
    }
    byte[] hashBytes = hash.array();
    byte[] padSecret = new byte[8 + second.length];
    System.arraycopy(hashBytes, 0, padSecret, 0, 8);
    System.arraycopy(second, 0, padSecret, 8, second.length);
    byte[] pad = expand(padSecret, "CMTS-EMIC-PAD", 8);
    long result = ByteBuffer.wrap(hashBytes).getLong() + ByteBuffer.wrap(pad).getLong();
    return ByteBuffer.allocate(8).putLong(result).array();
  }

  private static short word(byte[] bytes, int offset) {
    return (short)
        ((Byte.toUnsignedInt(bytes[offset]) << 8) | Byte.toUnsignedInt(bytes[offset + 1]));
  }

  private static byte[] expand(byte[] secret, String seed, int length) {
    byte[] folded = new byte[16];
    for (int index = 0; index < secret.length; index++) folded[index % 16] ^= secret[index];
    byte[] initial = Arrays.copyOf(seed.getBytes(StandardCharsets.US_ASCII), 16);
    byte[] result = new byte[length];
    try {
      Cipher aes = Cipher.getInstance("AES/ECB/NoPadding");
      aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(folded, "AES"));
      for (int offset = 0, block = 1; offset < length; offset += 16, block++) {
        byte[] counter = initial.clone();
        for (int index = 0; index < 4; index++) {
          counter[15 - index] ^= (byte) (block >>> (index * 8));
        }
        byte[] generated = aes.doFinal(counter);
        System.arraycopy(generated, 0, result, offset, Math.min(16, length - offset));
      }
      return result;
    } catch (GeneralSecurityException failure) {
      throw new IllegalStateException("AES-128 unavailable for DOCSIS MMH", failure);
    }
  }
}
