# NetSNMP

Net-SNMP is optional.

OSCAR works without Net-SNMP, but installing `snmptranslate` improves dotted/textual OID translation workflows.

## Cache Mapping File
When translations are performed, OSCAR caches mappings here:
- `res/DottedTextualNetSNMPMap.json`

Both release JARs include the source map. When using `java -jar`, a missing or
empty cache is initialized under `res/` beside the JAR; existing nonempty
caches are preserved.
See the [resource maintenance guide](../../res/README.md) for preview, refresh,
and generation commands. Rebuild JARs after updating the source map.

## Example OID Forms
Dotted:
- `Snmp11 1.3.6.1.2.1.69.1.2.1.2.1 IpAddress "0.0.0.0";`

Textual:
- `Snmp11 docsDevNmAccessIp.1 IpAddress "0.0.0.0";`

## Example Communities

Documentation and sample configurations use `public` for read-only access and
`private` for read-write access. These are example values, not production
credentials.

```text
Snmp11 docsDevNmAccessCommunity.1 OctetString "public";
Snmp11 docsDevNmAccessControl.1 Integer32 "2";
Snmp11 docsDevNmAccessCommunity.2 OctetString "private";
Snmp11 docsDevNmAccessControl.2 Integer32 "3";
```

## SNMP Data Types Supported in OID Insertion
- `Counter32`
- `Counter64`
- `Gauge32`
- `OctetString`
- `Integer32`
- `TimeTicks`
- `IpAddress`
- `HexString`
