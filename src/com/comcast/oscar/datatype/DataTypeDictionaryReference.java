package com.comcast.oscar.datatype;

/**
 * @bannerLicense Copyright 2015 Comcast Cable Communications Management, LLC<br>
 *     ___________________________________________________________________<br>
 *     Licensed under the Apache License, Version 2.0 (the "License")<br>
 *     you may not use this file except in compliance with the License.<br>
 *     You may obtain a copy of the License at<br>
 *     http://www.apache.org/licenses/LICENSE-2.0<br>
 *     Unless required by applicable law or agreed to in writing, software<br>
 *     distributed under the License is distributed on an "AS IS" BASIS,<br>
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
 *     See the License for the specific language governing permissions and<br>
 *     limitations under the License.<br>
 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */
public class DataTypeDictionaryReference {

  public static final String DATA_TYPE_INTEGER = "integer"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_STRING = "string"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_STRING_BITS =
      "string_bits"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_STRING_NULL_TERMINATED =
      "string_null_terminated"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_BYTE_ARRAY =
      "byte_array"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_DOUBLE_BYTE_ARRAY =
      "double_byte_array"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_MULTI_TLV_BYTE_ARRAY =
      "multi_tlv_byte_array"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_OID = "oid"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_OID_ASN1_OBJECT_6 = "oid_asn1_object6"; //
  public static final String DATA_TYPE_BYTE_ARRAY_IPV4_ADDR =
      "byte_array_ipv4_address"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_BYTE_ARRAY_IPV6_ADDR =
      "byte_array_ipv6_address"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_BYTE = "byte"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR =
      "transport_address_ipv4_address"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR =
      "transport_address_ipv6_address"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_TRANSPORT_ADDR_INET_ADDR =
      "byte_array_inet_transport_address"; //  (TlvAssemble)  (TlvDisassemble)
  public static final String DATA_TYPE_MAC_ADDRESS =
      "mac_address"; //  (TlvAssemble)  (TlvDisassemble)
}
