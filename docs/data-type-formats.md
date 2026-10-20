# Data type text formats

`DataTypeFormatConversion` validates whole strings with precompiled patterns.
Use `matches()`, not `find()`. Transport patterns expose named `address` and
`port` groups; callers should not depend on the former numbered groups.

| Format | Accepted input | Encoded output (hex) |
| --- | --- | --- |
| IPv4 transport | `192.0.2.1(161)` | `C0:00:02:01:00:A1` |
| IPv6 transport | `::1(161)` | 15 zero bytes followed by `01:00:A1` |
| IPv4-mapped IPv6 | `::ffff:192.0.2.1(161)` | 10 zero bytes followed by `FF:FF:C0:00:02:01:00:A1` |
| Decimal byte pairs | `(0,255)(1,2)` | `00:FF:01:02` |
| Binary bits | `(00000000)(11111111)` or `0000000011111111` | `00:FF` |
| MAC list | `(00:11:22:33:44:ff)` | `00:11:22:33:44:FF` |

Addresses must be literals; transport conversion does not resolve hostnames.
IPv6 permits one `::` compression and an optional dotted IPv4 tail, but excludes
zone IDs and brackets. Ports are decimal 0–65535. Decimal octets accept legacy
leading zeroes; ports accept up to four digits with leading zeroes, or a valid
five-digit value without a leading zero. Whitespace and trailing text are rejected.
MAC octets accept colon, dot or hyphen separators (including mixed separators)
and either hex case. Lists require at least one complete item.

Null text raises `NullPointerException`. For compatibility, the family-specific
IPv4/IPv6 encoders return an empty array for malformed text; the combined
`inetTransportAddressToByteArray` throws `IllegalArgumentException` instead.
Pair and MAC encoders also throw `IllegalArgumentException` for malformed text.
Transport decoders require exactly six or eighteen bytes and do not mutate input.
IPv6 decoding uses expanded lowercase notation, including for mapped addresses.

Tests cover port/octet boundaries, IPv6 compression positions, mapped IPv6 width,
known bytes, deterministic binary round trips, separators, malformed suffixes and
10,000-item lists. These check datatype syntax and encoding, not TLV applicability.

Binary masks require complete eight-bit groups, either all raw or all
parenthesized. Empty text, mixed grouping, unmatched parentheses and non-binary
characters are rejected by both validation and encoding. Grouped bit formatting
requires a positive group size.
