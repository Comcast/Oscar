# In-memory configuration API tests

Run from the repository root:

```bash
mvn -B -Dtest=ConfigurationApiRoundTripTest,SnmpExportTest test
```

`ConfigurationApiRoundTripTest` creates synthetic text, compiles it with
`ConfigurationFileImport` and `ConfigurationFile`, then passes the resulting
bytes to `ConfigurationFileExport`. Exported text is imported unchanged and
compiled again. Two successive round trips must preserve the complete binary,
including compiler-generated integrity fields and padding.
No configuration files, customer data, or external services are used.

| Family | Editions tested | Representative content |
| --- | --- | --- |
| DOCSIS | 1.0, 1.1, 2.0, 3.0 | Network access and SNMP values |
| DOCSIS | 3.1 | SNMP and nested upstream/downstream service flows |
| DOCSIS | 4.0 | Service class, rate units and nested AQM |
| DPoE | 1.0, 2.0 | SNMP and nested service flows |
| DPoG | 1.0 | SNMP and nested service flows |
| PacketCable | 1.0, 1.5, 2.0 | Delimiters, Snmp11, Snmp64 and generated hash |

Pass the explicit configuration type to the exporter. PacketCable editions
cannot be identified from their common delimiter alone; deprecated automatic
detection still emits `PacketCable-X.X`. DPoE 1.0 and 2.0 use the existing shared
DPoE catalog. DPoG has a dedicated parser/compiler/export path; see the
[DPoG guide](wiki/DPoE-&-DPoG.md) for constraints and additional tests.

These tests verify representative API behavior, not every TLV or device
interoperability. The shared key and all SNMP values are synthetic lab values.
DOCSIS 4.0 uses its currently supported catalog rather than assuming all
legacy DOCSIS entries are present.

## SNMP lookup resource protection

`NetSNMPResourceTest` checks the tracked OID map for duplicate JSON keys,
trailing data, invalid OID/name pairs, required security mappings, and readable
one-entry-per-line formatting. Maven tests use a separate mutable cache under
`target/test-snmp-cache/res/`, selected by `oscar.snmp.cache.path`, so lookup
activity cannot rewrite the tracked `res/DottedTextualNetSNMPMap.json`.
Runtime cache writes sort keys and retain readable JSON formatting.

## CVC binary preservation regression

Run `mvn -B -Dtest=CvcApiRoundTripTest test`. Synthetic byte arrays exercise
manufacturer CVC (32) and co-signer CVC (33), separately and together, at
1, 253, 254, 255, 508 and 1025 bytes. Tests check fragment lengths and payload
order independently, then compare complete binaries byte for byte after two
unchanged text round trips, including MICs and padding. No certificate files
are read; these tests do not validate DER syntax, signatures or certificate trust.
The 254-byte fragmentation expectation follows CM-SP-MULPIv3.1-I21-201020,
sections C.1.2.10 and C.1.2.11.

Known defect exposed by these tests: all 12 individual-type cases pass, but
the six adjacent manufacturer/co-signer cases fail. The exporter loses CVC
entries and duplicates NetworkAccess when switching fragmented TLV types.
The tests remain enabled to reproduce this defect; an exporter fix is pending.
