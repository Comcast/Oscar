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
manufacturer CVC (32), co-signer CVC (33), manufacturer chain (81) and
co-signer chain (82), separately and together, at
1, 253, 254, 255, 508, 1025 and 2409 bytes. Tests check fragment lengths and payload
order independently, then compare complete binaries byte for byte after two
unchanged text round trips, including MICs and padding. No certificate files
are read; these tests do not validate DER syntax, signatures or certificate trust.
The 254-byte fragmentation expectation follows CM-SP-MULPIv3.1-I21-201020,
sections C.1.2.10 and C.1.2.11 for legacy CVCs, and
CM-SP-MULPIv3.1-I25-230419, C.1.2.21 and C.1.2.22 for chains.

The exporter flushes each contiguous fragment group before switching TLV types,
clears its payload buffer, and starts the next group independently. Tests cover
both certificate orders and raw inputs ending without an EOF marker, as well as
finalized configurations. All 56 cases must preserve bytes exactly. This guards
against the former loss of adjacent CVC entries and duplication of NetworkAccess.

The DOCSIS chain text names are `ManufacturerCVCChain` and `Co-signerCVCChain`.
They use the same hexadecimal payload syntax as the legacy CVC fields.
`Docsis31CvcChainTest` also checks independent, repeated TLV 53.2 containers
with IPv4 and IPv6 address/mask pairs. See the
[DOCSIS 3.1 coverage audit](../skills/oscar-docsis-31-config-tlvs/references/coverage-audit.md)
for the exact source editions and coverage limits.


## I25 configuration encoding regression

`Docsis31I25CatalogTest` checks the Table 108 configuration inventory, excluded
REG-only/reserved additions, repeatable metadata and the edition/source hash.
`Docsis31I25WireTest` uses explicit wire fragments for new configuration families,
including repeated ASF/HQoS children, channel selection, light sleep, AQM and
SYNC IPv4/IPv6. These fragments test codecs; they are not complete profiles.

## Exact text output

`ConfigurationFileExportTextTest` adds character-for-character output checks to
binary round trips: indentation, full TLV paths, repeated containers, SNMP 11/64,
certificate text, quote-safe comment suppression and family headers. It also
checks bounded lookup, constructor errors and disk-write behavior. See
[text export](text-export.md) and the fixed fixtures under
`src/test/resources/export-text/`.

## Raw SNMP builder regression

`TlvBuilderSnmpTest` tests the public OID-based builder overloads directly.
It checks a literal Snmp64 byte fixture, both length widths, complete BER payloads
at 255/256 and 65535/65536 bytes, multibyte strings, numeric ranges, IP addresses,
and unchanged builder state after rejected input. These are synthetic TLV
fragments, not complete provisionable configuration files.

Run with `mvn -B -Dtest=TlvBuilderSnmpTest test`. The initial nine regression
cases produced eight failures against the old implementation before the fix.

## Hexadecimal display regression

`HexDumpTest` checks exact row layout at 16-byte boundaries, nonzero offsets,
ASCII limits, every byte value, fixed-width conversions, input immutability and
invalid ranges/hex. Seven failing cases were reproduced before fixing the
implementation. See [hexadecimal display](hex-dump.md) and its benchmark record.

## Public PacketCable interoperability fixtures

`PublicPacketCableRoundTripTest` tests two unchanged binaries from the independent
[rlaager/docsis project](https://github.com/rlaager/docsis), pinned to commit
`94e8845838c0bea2c12e8002001f04a62ca1e3d0`. The digit-map and TLV-64 fixtures
are tested under explicit PacketCable 1.5 and 2.0 profiles, with SHA-256 checks
and exact binary → text → binary comparisons. Tests require no network and use
numeric OIDs rather than host-specific MIB name resolution.

See [fixture provenance and license](../src/test/resources/packetcable-public/README.md)
for source links, unchanged text inputs, hashes and coverage limits. These tests
exercise common SNMP and delimiter encodings, not device provisioning validity,
2.0-only fields or embedded DOCSIS payloads. Run with:

```bash
./mvnw -B -Dtest=PublicPacketCableRoundTripTest test
```
