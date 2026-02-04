# NetSNMP

Net-SNMP is optional.

OSCAR works without Net-SNMP, but installing `snmptranslate` improves dotted/textual OID translation workflows.

## Cache Mapping File
When translations are performed, OSCAR caches mappings here:
- `db/DottedTextualNetSNMPMap.json`

## Example OID Forms
Dotted:
- `Snmp11 1.3.6.1.2.1.69.1.2.1.2.1 IpAddress "0.0.0.0";`

Textual:
- `Snmp11 docsDevNmAccessIp.1 IpAddress "0.0.0.0";`

## SNMP Data Types Supported in OID Insertion
- `Counter32`
- `Counter64`
- `Gauge32`
- `OctetString`
- `Integer32`
- `TimeTicks`
- `IpAddress`
- `HexString`
