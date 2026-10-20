/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.datatype;

import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.smi.OID;

public class DataTypeFormatConversion {
  private static final Logger logger = LogManager.getLogger(DataTypeFormatConversion.class);

  public static final Integer IPV4_ADDRESS_BYTE_LENGTH = 4;
  public static final Integer IPV6_ADDRESS_BYTE_LENGTH = 16;
  public static final Integer IP_PORT_BYTE_LENGTH = 2;
  public static final Integer MAC_ADDRESS_LENGTH = 6;
  public static final Integer INET_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH =
      (IPV6_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH);
  public static final Integer IPV6_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH =
      (IPV6_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH);
  public static final Integer IPV4_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH =
      (IPV4_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH);

  /** Decimal port 0..65535. Matches "0", "161", "65535"; rejects "65536". */
  public static final String IP_PORT_RANGE =
      "(?:[0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])";

  // Decimal bytes retain legacy leading-zero support, e.g. "001" means decimal 1.
  private static final String DECIMAL_BYTE = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})";
  private static final String IPV4_LITERAL = "(?:" + DECIMAL_BYTE + "\\.){3}" + DECIMAL_BYTE;
  private static final int IPV6_WORD_COUNT = IPV6_ADDRESS_BYTE_LENGTH / Short.BYTES;
  private static final int IPV4_WORD_COUNT = IPV4_ADDRESS_BYTE_LENGTH / Short.BYTES;
  private static final String HEX_WORD = "[0-9a-fA-F]{1,4}";
  private static final String IPV6_LITERAL = ipv6LiteralPattern();

  /**
   * Full IPv6 literal: eight hex words, one optional :: compression, or a dotted IPv4 tail
   * occupying two words. matches() accepts "::", "2001:db8::1", "::ffff:192.0.2.1"; rejects
   * "1::2::3", zone IDs, brackets, whitespace and hostnames. No capture groups.
   */
  public static final Pattern IPV6_ADDRESS_REGEX = Pattern.compile(IPV6_LITERAL);

  /**
   * IPv6(port), with named groups address and port. "2001:db8::1(161)" yields address "2001:db8::1"
   * and port "161"; conversion produces 16 address bytes then 00:A1. Requires a full matches()
   * match; ports outside 0..65535 are rejected.
   */
  public static final Pattern IPV6_TRANSPORT_REGEX =
      Pattern.compile("(?<address>" + IPV6_LITERAL + ")\\((?<port>" + IP_PORT_RANGE + ")\\)");

  /**
   * Four decimal octets 0..255; matches() accepts "192.0.2.1" and "001.002.003.004", rejects
   * "256.0.0.1", short forms, whitespace and trailing text. No capture groups.
   */
  public static final Pattern IPV4_ADDRESS_REGEX = Pattern.compile(IPV4_LITERAL);

  /**
   * IPv4(port), with named groups address and port. "192.0.2.1(161)" yields address "192.0.2.1" and
   * port "161"; conversion produces C0:00:02:01:00:A1. Requires a full matches() match; ports
   * outside 0..65535 are rejected.
   */
  public static final Pattern IPV4_TRANSPORT_REGEX =
      Pattern.compile("(?<address>" + IPV4_LITERAL + ")\\((?<port>" + IP_PORT_RANGE + ")\\)");

  /**
   * One or more adjacent decimal byte pairs, each component 0..255. matches() accepts
   * "(0,255)(1,2)" (bytes 00:FF:01:02); rejects empty text, spaces and "(256,0)". Possessive
   * repetition prevents backtracking over an already complete pair.
   */
  public static final Pattern DOUBLE_BYTE_ARRAY_REGEX =
      Pattern.compile("(?:\\(" + DECIMAL_BYTE + "," + DECIMAL_BYTE + "\\))++");

  /**
   * One or more parenthesized six-byte MACs, with two hex digits per byte, any case. Separators may
   * be colon, dot or hyphen (mixed separators remain accepted). matches() accepts
   * "(00:11:22:33:44:ff)" (bytes 00:11:22:33:44:FF), rejects empty text and missing/extra bytes.
   * Possessive repetition handles long lists.
   */
  public static final Pattern MAC_ADDRESS_REGEX =
      Pattern.compile("(?:\\((?:[0-9a-fA-F]{2}[.:-]){5}[0-9a-fA-F]{2}\\))++");

  /**
   * Complete bytes as raw bits or adjacent parenthesized groups. matches() accepts
   * "0000000011111111" and "(00000000)(11111111)" (00:FF); rejects empty text, partial bytes, mixed
   * grouping, signs and non-binary characters.
   */
  public static final Pattern BINARY_BIT_MASK_REGEX =
      Pattern.compile("(?:(?:[01]{8})++|(?:\\([01]{8}\\))++)");

  // Enumerate the bounded word counts instead of lookarounds or backreferences.
  // A dotted tail occupies two of the eight words; :: must replace at least one.
  private static String ipv6LiteralPattern() {
    var alternatives = new ArrayList<String>();
    alternatives.add("(?:" + HEX_WORD + ":){" + (IPV6_WORD_COUNT - 1) + "}" + HEX_WORD);
    alternatives.add(
        "(?:" + HEX_WORD + ":){" + (IPV6_WORD_COUNT - IPV4_WORD_COUNT) + "}" + IPV4_LITERAL);
    for (int leftWords = 0; leftWords < IPV6_WORD_COUNT; leftWords++) {
      String left = "";
      if (leftWords > 0) left = HEX_WORD + "(?::" + HEX_WORD + "){" + (leftWords - 1) + "}";
      int remainingWords = IPV6_WORD_COUNT - 1 - leftWords;
      String right =
          remainingWords == 0
              ? ""
              : "(?:" + HEX_WORD + "(?::" + HEX_WORD + "){0," + (remainingWords - 1) + "})?";
      alternatives.add(left + "::" + right);
      if (remainingWords >= IPV4_WORD_COUNT)
        alternatives.add(
            left
                + "::(?:"
                + HEX_WORD
                + ":){0,"
                + (remainingWords - IPV4_WORD_COUNT)
                + "}"
                + IPV4_LITERAL);
    }
    return "(?:" + String.join("|", alternatives) + ")";
  }

  public static final String DOUBLE_BYTE_ARRAY_FORMAT =
      "(0,0)(0,0)(0,0)(0,0) = (Integer < 256,Integer < 256)(Integer < 256,Integer < 256)....";
  public static final String IPV4_TRANSPORT_FORMAT = "0.0.0.0(0) = IPv4_Address(Port)";
  public static final String IPV6_TRANSPORT_FORMAT =
      "XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX(0) = IPv6_Address(Port)";
  public static final String STRING_BITS_FORMAT =
      "(00000000)(11111111)(00000000)(11111111) = 8 Bits Per Byte = 00:FF:00:FF";
  public static final String IPV6_ADDRESS_FORMAT =
      "XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX = IPv6 Address";
  public static final String IPV4_ADDRESS_FORMAT = "0.0.0.0 = IPv4 Address";
  public static final String MAC_ADDRESS_FORMAT = "(xx:xx:xx:xx:xx:xx)";

  /**
   * Formats 16 address bytes plus a two-byte big-endian port as IPv6(port). For example 15 zero
   * bytes, 01:00:A1 produce "0:0:0:0:0:0:0:1(161)". IPv4-mapped addresses remain IPv6 text. The
   * input is not mutated.
   *
   * @param bIPv6TransportAddress non-null 18-byte transport address
   * @return expanded lowercase IPv6(port) text
   * @throws DataTypeFormatException if length is not 18
   * @throws NullPointerException if the input is null
   */
  public static String byteArrayToIPv6TransportAddress(byte[] bIPv6TransportAddress)
      throws DataTypeFormatException {
    if (bIPv6TransportAddress.length != (IPV6_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH)) {
      throw new DataTypeFormatException("Invalid bIpv6TransportAddress Byte Length");
    }

    ByteArrayOutputStream baosIpv6Address = new ByteArrayOutputStream();

    baosIpv6Address.write(bIPv6TransportAddress, 0, IPV6_ADDRESS_BYTE_LENGTH);

    String sIPv6Address = ipv6AddressText(baosIpv6Address.toByteArray());

    ByteArrayOutputStream baosTransport = new ByteArrayOutputStream();

    baosTransport.write(bIPv6TransportAddress, IPV6_ADDRESS_BYTE_LENGTH, IP_PORT_BYTE_LENGTH);

    int iIpPort = new HexString(baosTransport.toByteArray()).toInteger();

    String sIPv6TransportAddress = sIPv6Address + "(" + iIpPort + ")";

    logger.debug(
        "DataTypeFormatConversion.byteArrayToIpv6TransportAddress() : {}", sIPv6TransportAddress);

    return sIPv6TransportAddress;
  }

  /**
   * Encodes a literal IPv6(port) as 16 address bytes and a two-byte big-endian port. For example
   * "::1(161)" ends in 00:01:00:A1 and has length 18. Mapped IPv4 addresses retain the IPv6 width.
   * No DNS lookup or caller mutation occurs.
   *
   * @param sIPv6TransportAddress non-null IPv6(port) text
   * @return encoded bytes, or an empty array for malformed text (legacy behavior)
   * @throws NullPointerException if the input is null
   */
  public static byte[] ipv6TransportAddressToByteArray(String sIPv6TransportAddress) {
    Matcher matcher = IPV6_TRANSPORT_REGEX.matcher(sIPv6TransportAddress);
    if (!matcher.matches()) return new byte[0];
    String literal = matcher.group("address");
    if (literal.indexOf('.') >= 0) {
      int tailOffset = literal.lastIndexOf(':') + 1;
      String[] octets = literal.substring(tailOffset).split("\\.");
      int highWord = (Integer.parseInt(octets[0]) << Byte.SIZE) | Integer.parseInt(octets[1]);
      int lowWord = (Integer.parseInt(octets[2]) << Byte.SIZE) | Integer.parseInt(octets[3]);
      literal =
          literal.substring(0, tailOffset)
              + Integer.toHexString(highWord)
              + ":"
              + Integer.toHexString(lowWord);
    }
    byte[] address = HexString.inetAddressToByteArray(literal);
    if (address.length == IPV4_ADDRESS_BYTE_LENGTH) {
      // InetAddress collapses IPv4-mapped IPv6 literals; restore the wire prefix.
      byte[] mapped = new byte[IPV6_ADDRESS_BYTE_LENGTH];
      int tailOffset = IPV6_ADDRESS_BYTE_LENGTH - IPV4_ADDRESS_BYTE_LENGTH;
      mapped[tailOffset - 2] = (byte) 0xff;
      mapped[tailOffset - 1] = (byte) 0xff;
      System.arraycopy(address, 0, mapped, tailOffset, address.length);
      address = mapped;
    }
    return appendPort(address, Integer.parseInt(matcher.group("port")));
  }

  /**
   * Validates the entire input without conversion or mutation. "2001:db8::1(161)" returns true;
   * empty text, whitespace, trailing text and out-of-range fields return false.
   *
   * @param sIPv6TransportAddress non-null text in the corresponding pattern's documented format
   * @return true only when the whole input matches
   * @throws NullPointerException if the input is null
   */
  public static boolean verifyIPv6TransportAddressFormat(String sIPv6TransportAddress) {
    return IPV6_TRANSPORT_REGEX.matcher(sIPv6TransportAddress).matches();
  }

  /**
   * Formats four IPv4 bytes and a two-byte big-endian port without modifying input.
   * C0:00:02:01:00:A1 yields "192.0.2.1(161)".
   *
   * @param bIPv4TransportAddress non-null six-byte transport address
   * @return IPv4(port) text
   * @throws DataTypeFormatException if length is not six
   * @throws NullPointerException if input is null
   */
  public static String byteArrayToIPv4TransportAddress(byte[] bIPv4TransportAddress)
      throws DataTypeFormatException {
    if (bIPv4TransportAddress.length != (IPV4_ADDRESS_BYTE_LENGTH + IP_PORT_BYTE_LENGTH)) {
      throw new DataTypeFormatException("Invalid bIPv4TransportAddress Byte Length");
    }

    ByteArrayOutputStream baosIPv4Address = new ByteArrayOutputStream();

    baosIPv4Address.write(bIPv4TransportAddress, 0, IPV4_ADDRESS_BYTE_LENGTH);

    String sIPv4Address = HexString.toInetAddress(baosIPv4Address.toByteArray());

    ByteArrayOutputStream baosTransport = new ByteArrayOutputStream();

    baosTransport.write(bIPv4TransportAddress, IPV4_ADDRESS_BYTE_LENGTH, IP_PORT_BYTE_LENGTH);

    int iIpPort = new HexString(baosTransport.toByteArray()).toInteger();

    String sIPv4TransportAddress = sIPv4Address + "(" + iIpPort + ")";

    logger.debug(
        "DataTypeFormatConversion.byteArrayToIpv6TransportAddress() : {}", sIPv4TransportAddress);

    return sIPv4TransportAddress;
  }

  /**
   * Encodes IPv4(port) as four address bytes and a two-byte big-endian port. For example
   * "192.0.2.1(161)" produces C0:00:02:01:00:A1. Leading-zero octets are decimal. No DNS lookup or
   * caller mutation occurs.
   *
   * @param sIpv4TransportAddress non-null IPv4(port) text
   * @return encoded bytes, or an empty array for malformed text (legacy behavior)
   * @throws NullPointerException if the input is null
   */
  public static byte[] ipv4TransportAddressToByteArray(String sIpv4TransportAddress) {
    Matcher matcher = IPV4_TRANSPORT_REGEX.matcher(sIpv4TransportAddress);
    if (!matcher.matches()) return new byte[0];
    String[] octets = matcher.group("address").split("\\.");
    byte[] address = new byte[IPV4_ADDRESS_BYTE_LENGTH];
    for (int index = 0; index < address.length; index++)
      address[index] = (byte) Integer.parseInt(octets[index]);
    return appendPort(address, Integer.parseInt(matcher.group("port")));
  }

  private static String ipv6AddressText(byte[] address) {
    try {
      return Inet6Address.getByAddress(null, address, -1).getHostAddress();
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("IPv6 requires 16 address bytes", e);
    }
  }

  private static byte[] appendPort(byte[] address, int port) {
    byte[] result = Arrays.copyOf(address, address.length + IP_PORT_BYTE_LENGTH);
    result[address.length] = (byte) (port >>> Byte.SIZE);
    result[address.length + 1] = (byte) port;
    return result;
  }

  /**
   * Formats an IPv4 or IPv6 transport, selected by its length, without mutating input.
   * C0:00:02:01:00:A1 produces "192.0.2.1(161)"; IPv6 remains IPv6 even when mapped.
   *
   * @param bInetTransportAddress non-null 6-byte IPv4 or 18-byte IPv6 transport
   * @return address(port), with the unsigned big-endian port in decimal
   * @throws DataTypeFormatException if length is neither 6 nor 18
   * @throws NullPointerException if the input is null
   */
  public static String byteArrayToInetTransportAddress(byte[] bInetTransportAddress)
      throws DataTypeFormatException {
    String sInetAddress = null;

    String sInetTransportAddress = null;

    ByteArrayOutputStream baosInetAddress = new ByteArrayOutputStream();

    ByteArrayOutputStream baosTransport = new ByteArrayOutputStream();

    if (bInetTransportAddress.length == IPV6_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH) {
      baosInetAddress.write(bInetTransportAddress, 0, IPV6_ADDRESS_BYTE_LENGTH);

      sInetAddress = ipv6AddressText(baosInetAddress.toByteArray());

      baosTransport.write(bInetTransportAddress, IPV6_ADDRESS_BYTE_LENGTH, IP_PORT_BYTE_LENGTH);

    } else if (bInetTransportAddress.length == IPV4_TRANSPORT_ADDRESS_MAX_BYTE_LENGTH) {
      baosInetAddress.write(bInetTransportAddress, 0, IPV4_ADDRESS_BYTE_LENGTH);

      sInetAddress = HexString.toInetAddress(baosInetAddress.toByteArray());

      baosTransport.write(bInetTransportAddress, IPV4_ADDRESS_BYTE_LENGTH, IP_PORT_BYTE_LENGTH);
    } else {
      throw new DataTypeFormatException(
          "Invalid InetTransportAddress Byte Length: "
              + bInetTransportAddress.length
              + " -> Hex: "
              + new HexString(bInetTransportAddress).toString(":"));
    }

    int iIpPort = new HexString(baosTransport.toByteArray()).toInteger();

    sInetTransportAddress = sInetAddress + "(" + iIpPort + ")";

    logger.debug(
        "DataTypeFormatConversion.byteArrayToIpv6TransportAddress() : {}", sInetTransportAddress);

    return sInetTransportAddress;
  }

  /**
   * Encodes a literal IPv4(port) or IPv6(port), preserving address family and source order.
   * "192.0.2.1(161)" yields C0:00:02:01:00:A1; IPv6 produces eighteen bytes. No DNS lookup is
   * performed.
   *
   * @param sInetTransportAddress non-null literal transport text
   * @return six IPv4 or eighteen IPv6 transport bytes
   * @throws IllegalArgumentException if the whole input is malformed
   * @throws NullPointerException if input is null
   */
  public static byte[] inetTransportAddressToByteArray(String sInetTransportAddress) {
    if (verifyIPv6TransportAddressFormat(sInetTransportAddress)) {
      return ipv6TransportAddressToByteArray(sInetTransportAddress);

    } else if (verifyIPv4TransportAddressFormat(sInetTransportAddress)) {
      return ipv4TransportAddressToByteArray(sInetTransportAddress);

    } else {
      logger.debug(
          "DataTypeFormatConversion.inetTransportAddressToByteArray() IncorrectFormat: {}",
          sInetTransportAddress);
    }

    throw new IllegalArgumentException("Invalid IPv4/IPv6 transport address or port");
  }

  /**
   * Validates the entire input without conversion or mutation. "192.0.2.1(161)" returns true; empty
   * text, whitespace, trailing text and out-of-range fields return false.
   *
   * @param sIPv4TransportAddress non-null text in the corresponding pattern's documented format
   * @return true only when the whole input matches
   * @throws NullPointerException if the input is null
   */
  public static boolean verifyIPv4TransportAddressFormat(String sIPv4TransportAddress) {
    return IPV4_TRANSPORT_REGEX.matcher(sIPv4TransportAddress).matches();
  }

  /**
   * Formats bytes as unsigned decimal pairs without modifying input. 00:FF:01:02 yields
   * "(0,255)(1,2)". Empty bytes retain the legacy "()" representation, which the text validator
   * intentionally rejects.
   *
   * @param bytes non-null array with even length
   * @return adjacent parenthesized decimal pairs
   * @throws IllegalArgumentException if byte count is odd
   * @throws NullPointerException if input is null
   */
  public static String doubleByteArray(byte[] bytes) throws IllegalArgumentException {
    if ((bytes.length % 2) != 0)
      throw new IllegalArgumentException("Decimal pairs require an even byte count");
    if (bytes.length == 0) return "()"; // Preserve the legacy empty representation.
    StringBuilder output = new StringBuilder();
    for (int index = 0; index < bytes.length; index += 2) {
      output
          .append('(')
          .append(Byte.toUnsignedInt(bytes[index]))
          .append(',')
          .append(Byte.toUnsignedInt(bytes[index + 1]))
          .append(')');
    }
    return output.toString();
  }

  /**
   * Converts adjacent decimal pairs to bytes in source order; "(0,255)(1,2)" gives 00:FF:01:02.
   * Each component is 0..255; leading zeroes remain decimal.
   *
   * @param input non-null text containing at least one pair
   * @return two bytes per pair
   * @throws IllegalArgumentException if the input does not match DOUBLE_BYTE_ARRAY_REGEX
   * @throws NullPointerException if the input is null
   */
  public static byte[] doubleByteArray(String input) throws IllegalArgumentException {
    if (!doubleByteArrayValidate(input)) {
      throw new IllegalArgumentException("Invalid decimal byte pairs: " + input);
    }
    String numbers = input.replace(")(", ",").replace(")", "").replace("(", "");
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    for (String number : numbers.split(",")) {
      output.write(Integer.parseInt(number));
    }
    return output.toByteArray();
  }

  /**
   * Validates the entire input without conversion or mutation. "(0,255)(1,2)" returns true; empty
   * text, whitespace, trailing text and out-of-range fields return false.
   *
   * @param sDoubleByteArray non-null text in the corresponding pattern's documented format
   * @return true only when the whole input matches
   * @throws NullPointerException if the input is null
   */
  public static boolean doubleByteArrayValidate(String sDoubleByteArray) {
    return DOUBLE_BYTE_ARRAY_REGEX.matcher(sDoubleByteArray).matches();
  }

  /**
   * Encodes complete binary bytes in most-significant-bit order. "(00000000)(11111111)" and
   * "0000000011111111" both produce 00:FF.
   *
   * @param input non-null, nonempty binary text, either raw bytes or parenthesized bytes
   * @return one byte per eight bits
   * @throws IllegalArgumentException for malformed grouping, partial bytes or non-binary digits
   * @throws NullPointerException if input is null
   */
  public static byte[] binaryBitMaskToByteArray(String input) {
    if (!binaryBitMaskValidate(input))
      throw new IllegalArgumentException("Expected complete binary bytes, raw or parenthesized");
    return BinaryConversion.binaryMSBMaskToByteArray(input);
  }

  /**
   * Returns the legacy reversed bit sequence without modifying input. A single 01 byte yields
   * "10000000". Use the overload with a group size for MSB-first text.
   *
   * @param bBinaryBitMask non-null bytes; an empty array is allowed
   * @return a new builder containing the reversed, ungrouped binary string
   * @throws NullPointerException if input is null
   */
  public static StringBuilder byteArrayBinaryBitMaskToString(byte[] bBinaryBitMask) {
    return new StringBuilder(new HexString(bBinaryBitMask).toAsciiBinary()).reverse();
  }

  /**
   * Formats MSB-first bits into parenthesized groups without modifying input. For 00:FF and group
   * size 8, returns "(00000000)(11111111)". A final short group is retained; empty input produces
   * "()".
   *
   * @param bBinaryBitMask non-null bytes
   * @param iBitGroup positive number of bits per output group
   * @return a new builder containing the grouped binary string
   * @throws IllegalArgumentException if the group size is not positive
   * @throws NullPointerException if input is null
   */
  public static StringBuilder byteArrayBinaryBitMaskToString(byte[] bBinaryBitMask, int iBitGroup) {
    if (iBitGroup <= 0) throw new IllegalArgumentException("Bit group size must be positive");

    logger.debug(
        () ->
            "sbBinaryBitMask: "
                + new HexString(bBinaryBitMask).toAsciiBinary()
                + " -> BitGroup: "
                + iBitGroup);

    StringBuilder sbBinaryBitMask =
        new StringBuilder(new HexString(bBinaryBitMask).toAsciiBinary());

    logger.debug("sbBinaryBitMask: {}", sbBinaryBitMask);

    StringBuilder sbBinaryBitMaskUpdate = new StringBuilder();

    sbBinaryBitMaskUpdate.append('(');

    for (int iIndex = 0; iIndex < sbBinaryBitMask.length(); iIndex++) {
      if (((iIndex % (iBitGroup)) == 0) && (iIndex != 0)) sbBinaryBitMaskUpdate.append(")(");

      sbBinaryBitMaskUpdate.append(sbBinaryBitMask.charAt(iIndex));
    }

    sbBinaryBitMaskUpdate.append(')');

    return sbBinaryBitMaskUpdate;
  }

  /**
   * Validates complete raw or parenthesized binary bytes without conversion. "(00000001)" returns
   * true; "(1)", empty text and malformed parentheses return false.
   *
   * @param input non-null binary text
   * @return true for a whole-string match
   * @throws NullPointerException if input is null
   */
  public static boolean binaryBitMaskValidate(String input) {
    return BINARY_BIT_MASK_REGEX.matcher(input).matches();
  }

  /**
   * Formats six-byte MAC addresses in source order without modifying input. 00:11:22:33:44:FF
   * produces "(00:11:22:33:44:FF)".
   *
   * @param bMacAddressFormatList non-null bytes, with a length divisible by six
   * @return adjacent parenthesized MACs, or empty text for empty bytes
   * @throws IllegalArgumentException if the byte count is not divisible by six
   * @throws NullPointerException if input is null
   */
  public static String byteArrayToMacAddressFormat(byte[] bMacAddressFormatList)
      throws IllegalArgumentException {
    if ((bMacAddressFormatList.length % MAC_ADDRESS_LENGTH) != 0) {
      throw new IllegalArgumentException(
          "DataTypeFormatConversion.byteArrayToMacAddress() ByteArray Must be a Multiple of 6"
              + " Bytes: ByteLength: "
              + bMacAddressFormatList.length);
    }

    // ByteArray -> 00:00:00:00:00:00
    StringBuilder sbMacAddress = new StringBuilder();

    for (byte[] bByteArray : HexString.byteArrayGroup(bMacAddressFormatList, 6)) {
      sbMacAddress.append('(');
      sbMacAddress.append(new HexString(bByteArray).toString(":")).append(')');
    }

    return sbMacAddress.toString();
  }

  /**
   * Encodes adjacent parenthesized MACs, accepting colon, dot or hyphen separators.
   * "(00-11-22-33-44-ff)" produces 00:11:22:33:44:FF. Case is ignored.
   *
   * @param sMacAddressFormatList non-null text containing at least one six-byte MAC
   * @return six bytes per MAC in source order
   * @throws IllegalArgumentException if the whole input does not match MAC_ADDRESS_REGEX
   * @throws NullPointerException if the input is null
   */
  public static byte[] macAddressFormatToByteArray(String sMacAddressFormatList)
      throws IllegalArgumentException {
    if (!macAddressFormatValidation(sMacAddressFormatList)) {
      throw new IllegalArgumentException("Invalid MacAddress Format: " + sMacAddressFormatList);
    }

    return HexString.toByteArray(sMacAddressFormatList.replace('.', ':').replace('-', ':'));
  }

  /**
   * Validates the entire input without conversion or mutation. "(00:11:22:33:44:55)" returns true;
   * empty text, whitespace, trailing text and out-of-range fields return false.
   *
   * @param sMacAddress non-null text in the corresponding pattern's documented format
   * @return true only when the whole input matches
   * @throws NullPointerException if the input is null
   */
  public static boolean macAddressFormatValidation(String sMacAddress) {
    return MAC_ADDRESS_REGEX.matcher(sMacAddress).matches();
  }

  /**
   * Delegates address conversion to HexString. Unlike transport parsing, this legacy API may
   * perform DNS resolution for hostnames.
   *
   * @param sInetAddress address literal or resolvable hostname; null uses Java loopback semantics
   * @return four IPv4 or sixteen IPv6 bytes; mapped literals may collapse to IPv4
   * @throws NullPointerException if the legacy helper cannot resolve the input
   */
  public static byte[] inetAddressToByteArray(String sInetAddress) {
    return HexString.inetAddressToByteArray(sInetAddress);
  }

  /**
   * Formats address bytes without modifying input. C0:00:02:01 produces "192.0.2.1"; IPv4-mapped
   * IPv6 may display as IPv4.
   *
   * @param bInetAddress non-null array of four or sixteen bytes
   * @return numeric IP address text
   * @throws NullPointerException if the legacy helper cannot decode the byte array
   */
  public static String inetAddressToString(byte[] bInetAddress) {
    return HexString.toInetAddress(bInetAddress);
  }

  /**
   * Returns the first decoded byte using HexString syntax. "FF" produces the signed Java byte -1;
   * additional bytes are ignored.
   *
   * @param sHexByte non-null hex text decoding to at least one byte
   * @return the first decoded byte
   * @throws ArrayIndexOutOfBoundsException if no byte is decoded
   * @throws NullPointerException if input is null
   */
  public static byte hexStringToByte(String sHexByte) {
    return HexString.toByteArray(sHexByte)[0];
  }

  /**
   * Returns the display hint for a supported datatype name, compared without case sensitivity. For
   * example the IPv4 transport datatype returns the IPv4_Address(Port) hint.
   *
   * @param sDisplayHelp non-null datatype identifier
   * @return the format hint, or the legacy "No Display Help Avaliable" message
   * @throws NullPointerException if input is null
   */
  public static String getDataTypeDisplayHelp(String sDisplayHelp) {
    if (sDisplayHelp.equalsIgnoreCase(DataTypeDictionaryReference.DATA_TYPE_DOUBLE_BYTE_ARRAY)) {
      return DOUBLE_BYTE_ARRAY_FORMAT;
    } else if (sDisplayHelp.equalsIgnoreCase(
        DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR)) {
      return IPV4_TRANSPORT_FORMAT;
    } else if (sDisplayHelp.equalsIgnoreCase(
        DataTypeDictionaryReference.DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR)) {
      return IPV6_TRANSPORT_FORMAT;
    } else if (sDisplayHelp.equalsIgnoreCase(DataTypeDictionaryReference.DATA_TYPE_STRING_BITS)) {
      return STRING_BITS_FORMAT;
    } else if (sDisplayHelp.equalsIgnoreCase(
        DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV6_ADDR)) {
      return IPV6_ADDRESS_FORMAT;
    } else if (sDisplayHelp.equalsIgnoreCase(
        DataTypeDictionaryReference.DATA_TYPE_BYTE_ARRAY_IPV4_ADDR)) {
      return IPV4_ADDRESS_FORMAT;
    } else {
      return "No Display Help Avaliable";
    }
  }

  /**
   * Encodes an SNMP OID as an ASN.1 OBJECT IDENTIFIER including tag and length. "1.3.6.1" produces
   * 06:03:2B:06:01. No files or caller state are modified.
   *
   * @param sOidObj6 non-null dotted OID accepted by SNMP4J
   * @return BER bytes; the legacy error path logs an IOException and returns bytes written so far
   * @throws IllegalArgumentException if SNMP4J rejects the OID text
   */
  public static byte[] oidObj6ToByteArray(String sOidObj6) {
    OID oid = new OID(sOidObj6);

    ByteArrayOutputStream baosOID = new ByteArrayOutputStream();

    try {
      oid.encodeBER(baosOID);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return baosOID.toByteArray();
  }
}
