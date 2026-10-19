# DPoE and DPoG

DPoE uses `dpoe.json`; the [DPoE review](../dpoe-registry-review.md) tracks its
remaining metadata issues. DPoG 1.0 uses its own `dpog.json`, text parser,
compiler and exporter. Select the family explicitly; a binary does not reliably
identify itself as DOCSIS, DPoE or DPoG.

## DPoG commands

Use a `DPoG { ... }` text wrapper and `-s dg 1.0` (also `-s dpog 1.0`).
From the repository root with a current `oscar.jar`:

```bash
mkdir -p output
printf %s SHAREDSECRET > output/dpog-lab.key
java -jar oscar.jar -c -s dg 1.0 -k output/dpog-lab.key -i examples/dpog/1.0/lab-service-flows.txt -o output/dpog.bin
java -jar oscar.jar -d -s dg 1.0 -i output/dpog.bin > output/dpog.txt
java -jar oscar.jar --diagnose-binary output/dpog.bin --diagnostic-family dpog
```

The command explicitly creates and supplies the public lab key `SHAREDSECRET`.
Supply your own key through `-k KEY_FILE` when required.
See the [example guide](../../examples/dpog/1.0/README.md).

## API and architecture

`ConfigurationFileImport` selects the DPoG catalog from the text header.
`ConfigurationFile(DPOG_10_CONFIGURATION_TYPE, builder, sharedSecret)` delegates
to `DPoGCompiler`. `commit()` validates supported settings and emits finalized
bytes. `ConfigurationFileExport` accepts that configuration or a binary builder
plus the same explicit type. `TlvDisassemble.TLV_TYPE_DPOG` selects direct
binary inspection; `DisassemblyOptions.Family.DPOG` selects recovery diagnostics.

`DPoGTlvCodec` checks nested boundaries and catalog presence before normal
export. It preserves encounter order, including child TLVs. Unsupported types,
truncated lengths, invalid supported values and repeated children that the
legacy dictionary model cannot represent losslessly cause explicit errors.
Use diagnostic mode to inspect such input without exporting a lossy replacement.

The compiler requires NetworkAccess, upstream/downstream service flows,
nonzero unique service-flow references and QoS parameter set `07`. DPoG
Table 10 requires `07`; MUST-support catalog rows do not independently require
all listed TLVs to occur in every file. DHCP-only and MUST NOT fields are absent
from the DPoG catalog. SHOULD NOT fields remain available as recommendations.

## Integrity and ordering

A nonempty shared secret generates CM and CMTS MICs, replacing imported hashes.
Passing null or an empty secret explicitly to the Java API selects unsigned
output and removes imported CM/CMTS MICs. Extended MIC requests require a key
and use the shared finalizer; stale explicit digests are recalculated.

Output ends with the one-octet `FF` end marker and zero padding to a four-byte
boundary. Normal compilation preserves parameter order. Extended-MIC creation
uses the existing finalizer's top-level type ordering; child order remains intact.
Repeated commits are deterministic. Byte-identical round trips are tested for
compiler-produced files, not arbitrary input with redundant padding or unusual
integrity-field placement.

## Sources and limits

DPoG-SP-MULPIv1.0-C01-160830 §9.1.5.6 and Annex C supply applicability;
Tables 7 and 12 make MIC support optional. Delegated framing/markers and
integrity use CM-SP-MULPIv3.0-C01-171207 C.1.2.1–2 and D.1–D.2
(pp. 615–621). DUT fields use CM-SP-L2VPN-I15-150528 B.2 (pp. 92–93).
Exact text sources, URLs and hashes are in the
[source map](../../skills/oscar-dpog-10-config-tlvs/references/source-map.md).

This completes the upstream parser/compiler/export path, not device certification.
Inherited catalog metadata still needs exhaustive field-by-field verification;
operator policy, GPON T-CONT capacity and OMCI provisioning are not validated.
Unknown vendor data and unsupported repetitions are rejected, not silently dropped.
OSCAR-UI selection/editor integration remains a separate repository task.

## Implementation checklist

- [x] Dedicated DPoG catalog, grammar and regenerated parser.
- [x] CLI, API, compiler, template, export and diagnostic routing.
- [x] Obtain delegated DOCSIS 3.0 and L2VPN source editions.
- [x] Add missing 45, 23.13, 23.43.8, 24.43.8 and 43.8 definitions.
- [x] Validate supported boundaries, QoS 0x07 and required flow fields.
- [x] Preserve child order; diagnose unsupported content before export.
- [x] Test unsigned vectors, MIC refresh, end marker and padding.
- [x] Include DPoG in synthetic in-memory API round trips.
- [ ] Audit every inherited codec/constraint against delegated source text.
- [ ] Support arbitrary vendor payloads and repeated children losslessly.
- [ ] Integrate DPoG selection and editing in OSCAR-UI.
- [ ] Validate generated profiles with a DPoG System/D-ONU.

Run the focused checks with:

```bash
mvn -B -Dtest=DPoGConfigurationTest,DpogRegistryTest,ConfigurationApiRoundTripTest test
```
