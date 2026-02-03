## NetSNMP v5.7

OSCAR does **NOT** require NetSNMP, but if you desire to translate Dotted OIDs to Textual OIDs you will need to install NetSNMP

[Download NetSNMP Binaries for your OS](http://sourceforge.net/projects/net-snmp/files/net-snmp%20binaries/5.7-binaries/)

## **Caveat**

When using OSCAR for the first time with NetSNMP, there is a performance hit, a system call is required to the snmptranslate application for OID lookups.  As each time you use OSCAR, a DB mapping of the Dotted-OID <=> Textual-OID is created, this will reduce the lookup time by by-passing snmptranslate.

This file is located in **db/DottedTextualNetSNMPMap.json**

***

## DOCSIS & PacketCable MIBs

Unzip [mibs.zip](https://comcast.github.io/Oscar/mibs/mibs.zip) in the same install directory as oscar.jar

[**Review MIBS**](https://github.com/Comcast/Oscar/tree/master/mibs/txt)

#### **Dotted OID**

`Snmp11 1.3.6.1.2.1.69.1.2.1.2.1 IpAddress "0.0.0.0";`

#### **Textual OID**

`Snmp11 docsDevNmAccessIp.1 IpAddress "0.0.0.0";` 

***

## **Snmp11 vs. Snmp64**

**Snmp11** encodes a ASN.1 Basic Encoding Rule (BER) as part of a SNMP SET encapsulated using TLV Type 11.  The total byte length is less than 256 or a TLV Length of 1 byte.

**Snmp64** encodes a ASN.1 Basic Encoding Rule (BER) as part of a SNMP SET encapsulated using TLV Type 64.  The total byte length is less than 65535 or a TLV Length of 2 bytes.

Typical Usage is reserved to PacketCable setting the DigitMap configuration

***

## **Data Types Mapping of the SYNTAX clause**

#### [**Source: RFC-2578**](http://tools.ietf.org/html/rfc2578)

#### **COUNTER32** 

The Counter32 type represents a non-negative integer which monotonically increases until it reaches a maximum value of 2^32-1 (4294967295 decimal), when it wraps around and starts increasing again from zero.

This is not a typical set for an OID

Syntax: Counter32

#### **COUNTER64**

The Counter64 type represents a non-negative integer which monotonically increases until it reaches a maximum value of 2^64-1 (18446744073709551615 decimal), when it wraps around and starts increasing again from zero. 

This is not a typical set for an OID

Syntax: Counter64

#### **GAUGE32**

The Gauge32 type represents a non-negative integer, which may increase or decrease, but shall never exceed a maximum value, nor fall below a minimum value.  The maximum value can not be greater than 2^32-1 (4294967295 decimal), and the minimum value can not be smaller than 0.  

This is not a typical set for an OID

Syntax: Gauge32

#### **INTEGER and INTEGER32** 

The Integer32 type represents integer-valued information between -2^31 and 2^31-1 inclusive (-2147483648 to 2147483647 decimal).  This type is indistinguishable from the INTEGER type.  Both the INTEGER and Integer32 types may be sub-typed to be more constrained than the Integer32 type. 

The INTEGER type (but not the Integer32 type) may also be used to represent integer-valued information as named-number enumerations. In this case, only those named-numbers so enumerated may be present as a value.  Note that although it is recommended that enumerated values start at 1 and be numbered contiguously, any valid value for Integer32 is allowed for an enumerated value and, further, enumerated values needn't be contiguously assigned.

`Snmp11 docsDevNmAccessControl.1 Integer "3";`

`Snmp11 docsDevNmAccessControl.1 Integer32 "3";`

#### **TIMETICKS** 

This represents a non-negative integer which specifies the elapsed time between two events, in units of hundredth of a second. The range is 0 to 2e32 - 1

This is not a typical set for an OID

Syntax: TimeTicks

#### **IPADDRESS** 

The IpAddress type represents a 32-bit internet address.  It is represented as an OCTET STRING of length 4, in network byte-order.

`Snmp11 docsDevNmAccessIp.1 IpAddress "0.0.0.0";`

#### **OCTETSTRING**

The OCTET STRING type represents arbitrary binary or textual data. Although the SMI-specified size limitation for this type is 65535 octets, MIB designers should realize that there may be implementation and interoperability limitations for sizes in excess of 255 octets. 

`Snmp11 docsDevNmAccessCommunity.1 OctetString "ReadWrite";`

#### **HEXSTRING** 

HexString is not a primitive datatype for SNMP, but it is used many time when the OID definition required a ByteArray representation of data 

`Snmp11 docsDevNmAccessInterfaces.1 HexString "40:00"`

***

## **Unsupported Syntax**

#### **UNSIGNED32**

The Unsigned32 type represents integer-valued information between 0 and 2^32-1 inclusive (0 to 4294967295 decimal).

Not Supported at this time

#### **OBJECT IDENTIFIER**

The OBJECT IDENTIFIER type represents administratively assigned names.  Any instance of this type may have at most 128 sub- identifiers.  Further, each sub-identifier must not exceed the value 2^32-1 (4294967295 decimal).

Not Supported at this time

#### **OPAQUE**

The Opaque type is provided solely for backward-compatibility, and shall not be used for newly-defined object types.

Not Supported at this time

***

###**Syntax API**

[BERService](https://github.com/Comcast/Oscar/blob/master/src/com/comcast/oscar/ber/BERService.java)