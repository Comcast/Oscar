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
