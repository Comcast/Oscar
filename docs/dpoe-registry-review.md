# DPoE 2.0 registry review

Review baseline: **DPoE-SP-MULPIv2.0-I14-230322**, 2023-03-22.
Registry: `res/tlv-registry/dpoe.json`, legacy edition `oscar-legacy-2.2.15`.
Reviewed 2026-10-19. This is an audit; runtime definitions are unchanged.

Registry SHA-256 at review: `dbf2dd4ffe5e9eb10c49a6b3605fa3e0aed66baac6b850826dddb70efd518c62`.

## Findings

The legacy catalog is not an I14 configuration-file eligibility catalog. It
contains DHCP settings, lacks named required paths, and has wire metadata
differences. DPoE 1.0 and 2.0 currently share this catalog, so a future correction
must distinguish the target edition rather than apply DPoG exclusions.

- Missing named MUST-support paths: **61**, including `79`, `83` and its
  listed children, `24.41`, `25.41`, `23.13`, and many L2VPN children.
  See the complete presence matrix below. Most L2VPN gaps sit below opaque
  `multi_tlv_byte_array` containers; absence means missing structured lookup,
  not proof that OSCAR cannot carry their raw bytes.
- All explicitly MUST NOT numeric rows checked are absent from this catalog.
- TLV 5 is present but Table 7 places 5/5.42 in DHCP, not configuration files.
- SHOULD NOT is a recommendation; retain it distinctly from MUST NOT.
- MUST support is not mandatory occurrence. MIC 6/7/43.6 support is MAY.

## Direct wire-definition comparisons

The following mismatches/missing fields are from explicit C.1–C.9 length rows.
Fixed lengths are value octets, not the complete TLV size. Matching length alone
does not establish correct values, repetition, context, or serialization.

| Path | Source length | Registry min/max | Section / printed page | Finding |
| --- | --- | --- | --- | --- |
| 22.15.3 | 4 | 5/5 | C.2.3 / 87 | MISMATCH |
| 23.15.3 | 4 | 5/5 | C.2.3 / 87 | MISMATCH |
| 60.15.3 | 4 | 5/5 | C.2.3 / 87 | MISMATCH |
| 72.1 | 2 | 1/1 | C.5.1 / 91 | MISMATCH |

## Codec and semantic follow-up

- **Serving Group Name termination:** `[24/25/70/71].38` uses `string` in
  the registry. C.7.3, p. 96 requires a zero-terminated ASCII string, length
  2–16. `TlvAssembler` adds the terminator only for `string_null_terminated`;
  the current `string` branch calls `HexString.add(value)` without that flag.
  This is a confirmed codec mismatch, not merely a naming preference.
- **MESP bandwidth container:** `72.2` has fixed 4/4 byte-array metadata,
  despite C.5.2, p. 91 defining a variable-length nested container. It has
  registered children. Trace container handling before changing it; this audit
  does not claim the assembler always emits a four-byte container.
- **Required children and value constraints:** C.5–C.7, pp. 91–96 requires
  exactly one MESP reference and bandwidth child, nonzero references,
  ASF reference/MESP reference children, color configuration rules and
  SF Collection pairing. `maxInstances` alone does not encode minimum
  occurrence, cross-field dependencies or reserved values. Review compiler
  enforcement separately; no runtime rejection test was performed here.
- **Context and source coverage:** `5/5.42` belongs to DHCP (C.8 and Table 7).
  `79` is delegated by §9.6, pp. 79–80; `83` appears in Table 14, pp. 108–109,
  despite being absent from Table 7. Do not build eligibility from Table 7 alone.
- **Referenced authority:** §2.1 pins CM-SP-MULPIv3.0-C01-171207. Detailed
  inherited encodings still require that document; L2VPN/MEF and SNMP object
  requirements also need their explicitly referenced documents. No DOCSIS 3.1
  or DPoG substitution was used for the findings above.

## Recommended implementation sequence

- [ ] Add expected-byte regressions and correct the four fixed-length entries
  and four Serving Group Name codecs, accounting for the shared 1.0/2.0 catalog.
- [ ] Verify and correct container metadata without changing serialized children.
- [ ] Add version-aware applicability metadata and DHCP/file context filtering.
- [ ] Obtain delegated definitions and add missing structured fields.
- [ ] Add semantic validation and synthetic complete-file round trips.
- [ ] Verify lossless handling, ordering and integrity before enabling editor use.

This review compares catalog metadata and inspected codec branches. It does not
certify the entire catalog, all semantic checks, generated files, or hardware
interoperability. The runtime registry and codecs have not been changed.

## Applicability matrix

Presence comparison for numeric rows in Tables 7 and 9–16. Table 8 contains
SNMP object requirements, not additional numeric TLV paths, and requires the
referenced OSSI document for detailed validation. Unlisted registry paths are
not automatically invalid: check normative prose and delegated specifications.

| Path | Support | Section / printed page | Registry |
| --- | --- | --- | --- |
| 0 | MUST | C.10 / 99 | Present |
| 1 | MUST NOT | C.10 / 99 | Absent |
| 2 | MUST NOT | C.10 / 99 | Absent |
| 3 | MUST | C.10 / 99 | Present |
| 4 | MUST NOT | C.10 / 99 | Absent |
| 5 | MUST | C.10 / 99 | Present; DHCP only |
| 6 | MAY | C.10 / 99 | Present |
| 7 | MAY | C.10 / 99 | Present |
| 9 | MUST | C.10 / 99 | Present |
| 10 | SHOULD NOT | C.10 / 99 | Present |
| 11 | MUST | C.10 / 99 | Present |
| 14 | SHOULD NOT | C.10 / 99 | Present |
| 17 | MUST NOT | C.10 / 99 | Absent |
| 17.1 | MUST NOT | C.12 / 102 | Absent |
| 17.2 | MUST NOT | C.12 / 102 | Absent |
| 17.3 | MUST NOT | C.12 / 102 | Absent |
| 17.4 | MUST NOT | C.12 / 102 | Absent |
| 17.5 | MUST NOT | C.12 / 102 | Absent |
| 17.6 | MUST NOT | C.12 / 102 | Absent |
| 17.7 | MUST NOT | C.12 / 102 | Absent |
| 17.8 | MUST NOT | C.12 / 102 | Absent |
| 17.9 | MUST NOT | C.12 / 102 | Absent |
| 18 | MUST | C.10 / 99 | Present |
| 19 | MUST NOT | C.10 / 99 | Absent |
| 20 | MUST NOT | C.10 / 99 | Absent |
| 21 | MUST | C.10 / 99 | Present |
| 22 | MUST | C.10 / 99 | Present |
| 22.1 | MUST | C.13 / 102 | Present |
| 22.3 | MUST | C.13 / 102 | Present |
| 22.5 | MUST | C.13 / 102 | Present |
| 22.6 | SHOULD NOT | C.13 / 102 | Present |
| 22.9.1 | MUST | C.13 / 102 | Present |
| 22.9.2 | MUST | C.13 / 102 | Present |
| 22.9.3 | MUST | C.13 / 102 | Present |
| 22.9.4 | MUST | C.13 / 102 | Present |
| 22.9.5 | MUST | C.13 / 102 | Present |
| 22.9.6 | MUST | C.13 / 102 | Present |
| 22.9.7 | MUST | C.13 / 102 | Present |
| 22.9.8 | MUST | C.13 / 102 | Present |
| 22.9.9 | MUST | C.13 / 102 | Present |
| 22.9.10 | MUST | C.13 / 102 | Present |
| 22.10.1 | MUST | C.13 / 102 | Present |
| 22.10.2 | MUST | C.13 / 102 | Present |
| 22.10.3 | MUST | C.13 / 103 | Present |
| 22.11.1 | MUST | C.13 / 103 | Present |
| 22.11.2 | MUST | C.13 / 103 | Present |
| 22.12.1 | MUST | C.13 / 103 | Present |
| 22.12.2 | MUST | C.13 / 103 | Present |
| 22.12.3 | MUST | C.13 / 103 | Present |
| 22.12.4 | MUST | C.13 / 103 | Present |
| 22.12.5 | MUST | C.13 / 103 | Present |
| 22.12.6 | MUST | C.13 / 103 | Present |
| 22.12.7 | MUST | C.13 / 103 | Present |
| 22.13 | MUST | C.13 / 103 | Present |
| 22.14 | MUST | C.13 / 103 | Present |
| 22.14.1 | MUST | C.13 / 103 | Present |
| 22.14.2 | MUST | C.13 / 103 | Present |
| 22.14.3 | MUST | C.13 / 103 | Present |
| 22.14.4 | MUST | C.13 / 103 | Present |
| 22.14.5 | MUST | C.13 / 103 | Present |
| 22.14.6 | MUST | C.13 / 103 | Present |
| 22.14.7 | MUST | C.13 / 103 | Present |
| 22.14.8 | MUST | C.13 / 103 | Present |
| 22.14.9 | MUST | C.13 / 103 | Present |
| 22.14.10 | MUST | C.13 / 103 | Present |
| 22.15 | MUST | C.13 / 103 | Present |
| 22.15.1 | MUST | C.13 / 103 | Present |
| 22.15.2 | MUST | C.13 / 103 | Present |
| 22.15.3 | MUST | C.13 / 103 | Present |
| 22.15.4 | MUST | C.13 / 104 | Present |
| 22.15.5 | MUST | C.13 / 104 | Present |
| 22.15.6 | MUST | C.13 / 104 | Present |
| 22.15.7 | MUST | C.13 / 104 | Present |
| 22.15.8 | MUST | C.13 / 104 | Present |
| 22.15.9 | MUST | C.13 / 104 | Present |
| 22.15.10 | MUST | C.13 / 104 | Present |
| 22.15.11 | MUST | C.13 / 104 | Present |
| 22.15.12 | MUST | C.13 / 104 | Present |
| 22.15.13 | MUST | C.13 / 104 | Present |
| 22.16 | MUST | C.13 / 104 | Present |
| 22.17.1 | MUST | C.13 / 104 | Present |
| 22.17.2 | MUST | C.13 / 104 | Present |
| 22.43 | MUST | C.13 / 104 | Present |
| 23 | MUST | C.10 / 99 | Present |
| 23.1 | MUST | C.13 / 102 | Present |
| 23.3 | MUST | C.13 / 102 | Present |
| 23.5 | MUST | C.13 / 102 | Present |
| 23.6 | SHOULD NOT | C.13 / 102 | Present |
| 23.9.1 | MUST | C.13 / 102 | Present |
| 23.9.2 | MUST | C.13 / 102 | Present |
| 23.9.3 | MUST | C.13 / 102 | Present |
| 23.9.4 | MUST | C.13 / 102 | Present |
| 23.9.5 | MUST | C.13 / 102 | Present |
| 23.9.6 | MUST | C.13 / 102 | Present |
| 23.9.7 | MUST | C.13 / 102 | Present |
| 23.9.8 | MUST | C.13 / 102 | Present |
| 23.9.9 | MUST | C.13 / 102 | Present |
| 23.9.10 | MUST | C.13 / 102 | Present |
| 23.10.1 | MUST | C.13 / 102 | Present |
| 23.10.2 | MUST | C.13 / 102 | Present |
| 23.10.3 | MAY | C.13 / 103 | Present |
| 23.11.1 | MUST | C.13 / 103 | Present |
| 23.11.2 | MUST | C.13 / 103 | Present |
| 23.12.1 | MUST | C.13 / 103 | Present |
| 23.12.2 | MUST | C.13 / 103 | Present |
| 23.12.3 | MUST | C.13 / 103 | Present |
| 23.12.4 | MUST | C.13 / 103 | Present |
| 23.12.5 | MUST | C.13 / 103 | Present |
| 23.12.6 | MUST | C.13 / 103 | Present |
| 23.12.7 | MUST | C.13 / 103 | Present |
| 23.13 | MUST | C.13 / 103 | Absent |
| 23.14 | MUST | C.13 / 103 | Present |
| 23.14.1 | MUST | C.13 / 103 | Present |
| 23.14.2 | MUST | C.13 / 103 | Present |
| 23.14.3 | MUST | C.13 / 103 | Present |
| 23.14.4 | MUST | C.13 / 103 | Present |
| 23.14.5 | MUST | C.13 / 103 | Present |
| 23.14.6 | MUST | C.13 / 103 | Present |
| 23.14.7 | MUST | C.13 / 103 | Present |
| 23.14.8 | MUST | C.13 / 103 | Present |
| 23.14.9 | MUST | C.13 / 103 | Present |
| 23.14.10 | MUST | C.13 / 103 | Present |
| 23.15 | MUST | C.13 / 103 | Present |
| 23.15.1 | MUST | C.13 / 103 | Present |
| 23.15.2 | MUST | C.13 / 103 | Present |
| 23.15.3 | MUST | C.13 / 103 | Present |
| 23.15.4 | MUST | C.13 / 104 | Present |
| 23.15.5 | MUST | C.13 / 104 | Present |
| 23.15.6 | MUST | C.13 / 104 | Present |
| 23.15.7 | MUST | C.13 / 104 | Present |
| 23.15.8 | MUST | C.13 / 104 | Present |
| 23.15.9 | MUST | C.13 / 104 | Present |
| 23.15.10 | MUST | C.13 / 104 | Present |
| 23.15.11 | MUST | C.13 / 104 | Present |
| 23.15.12 | MUST | C.13 / 104 | Present |
| 23.15.13 | MUST | C.13 / 104 | Present |
| 23.16 | MUST | C.13 / 104 | Present |
| 23.17.1 | MUST | C.13 / 104 | Present |
| 23.17.2 | MUST | C.13 / 104 | Present |
| 23.43 | MUST | C.13 / 104 | Present |
| 23.43.5.1 | MUST | C.13 / 104 | Absent |
| 23.43.8 | MUST | C.13 / 104 | Absent |
| 24 | MUST | C.10 / 99 | Present |
| 24.1 | MUST | C.14 / 104 | Present |
| 24.4 | MUST | C.14 / 104 | Present |
| 24.6 | MUST | C.14 / 104 | Present |
| 24.7 | MUST | C.14 / 104 | Present |
| 24.8 | MUST | C.14 / 104 | Present |
| 24.9 | MUST | C.14 / 104 | Present |
| 24.10 | MUST | C.14 / 104 | Present |
| 24.11 | SHOULD NOT | C.14 / 105 | Present |
| 24.12 | SHOULD NOT | C.14 / 105 | Present |
| 24.13 | SHOULD NOT | C.14 / 105 | Present |
| 24.14 | MUST NOT | C.14 / 105 | Absent |
| 24.15 | MUST | C.14 / 105 | Present |
| 24.16 | MUST | C.14 / 105 | Present |
| 24.17 | MUST | C.14 / 105 | Present |
| 24.18 | SHOULD NOT | C.14 / 105 | Present |
| 24.19 | SHOULD NOT | C.14 / 105 | Present |
| 24.20 | SHOULD NOT | C.14 / 105 | Present |
| 24.21 | SHOULD NOT | C.14 / 105 | Present |
| 24.22 | SHOULD NOT | C.14 / 105 | Present |
| 24.23 | MUST | C.14 / 105 | Present |
| 24.26 | MUST NOT | C.14 / 105 | Absent |
| 24.27 | SHOULD NOT | C.14 / 105 | Present |
| 24.31 | MUST | C.14 / 105 | Present |
| 24.32 | MAY | C.14 / 105 | Present |
| 24.33 | MUST NOT | C.14 / 105 | Absent |
| 24.34 | SHOULD NOT | C.14 / 105 | Present |
| 24.35.1 | SHOULD NOT | C.14 / 105 | Present |
| 24.35.2 | MUST | C.14 / 105 | Present |
| 24.35.3 | SHOULD NOT | C.14 / 105 | Present |
| 24.36 | MUST | C.14 / 105 | Present |
| 24.37 | MUST | C.14 / 106 | Present |
| 24.41 | MUST | C.14 / 106 | Absent |
| 24.43 | MUST | C.14 / 106 | Present |
| 24.43.5.1 | MUST | C.14 / 106 | Absent |
| 24.43.8 | MUST | C.14 / 106 | Absent |
| 24.44 | MUST | C.14 / 106 | Present |
| 25 | MUST | C.10 / 99 | Present |
| 25.1 | MUST | C.14 / 104 | Present |
| 25.4 | MUST | C.14 / 104 | Present |
| 25.6 | MUST | C.14 / 104 | Present |
| 25.7 | MUST | C.14 / 104 | Present |
| 25.8 | MUST | C.14 / 104 | Present |
| 25.9 | MUST | C.14 / 104 | Present |
| 25.10 | MUST | C.14 / 104 | Present |
| 25.11 | SHOULD NOT | C.14 / 105 | Present |
| 25.12 | SHOULD NOT | C.14 / 105 | Present |
| 25.13 | SHOULD NOT | C.14 / 105 | Present |
| 25.14 | SHOULD NOT | C.14 / 105 | Present |
| 25.17 | MUST NOT | C.14 / 105 | Absent |
| 25.23 | MUST | C.14 / 105 | Present |
| 25.27 | SHOULD NOT | C.14 / 105 | Present |
| 25.31 | MUST | C.14 / 105 | Present |
| 25.32 | MAY | C.14 / 105 | Present |
| 25.33 | MUST NOT | C.14 / 105 | Absent |
| 25.34 | SHOULD NOT | C.14 / 105 | Present |
| 25.35.1 | SHOULD NOT | C.14 / 105 | Present |
| 25.35.2 | MUST | C.14 / 105 | Present |
| 25.35.3 | SHOULD NOT | C.14 / 105 | Present |
| 25.36 | MUST | C.14 / 105 | Present |
| 25.37 | MUST | C.14 / 106 | Present |
| 25.41 | MUST | C.14 / 106 | Absent |
| 25.44 | MUST | C.14 / 106 | Present |
| 26 | MUST NOT | C.10 / 99 | Absent |
| 28 | SHOULD NOT | C.10 / 99 | Present |
| 29 | MUST | C.10 / 100 | Present |
| 32 | MUST | C.10 / 100 | Present |
| 33 | MUST | C.10 / 100 | Present |
| 34 | SHOULD | C.10 / 100 | Present |
| 34.1 | SHOULD | C.15 / 106 | Present |
| 34.2 | SHOULD | C.15 / 106 | Present |
| 35 | MUST | C.10 / 100 | Present |
| 36 | MUST | C.10 / 100 | Present |
| 37 | MUST | C.10 / 100 | Present |
| 38 | MUST | C.10 / 100 | Present |
| 38.1 | MUST | C.15 / 106 | Present |
| 38.2 | MUST | C.15 / 106 | Present |
| 38.3 | MUST | C.15 / 106 | Present |
| 38.4 | MUST | C.15 / 106 | Present |
| 38.5 | MUST | C.15 / 106 | Present |
| 38.6 | MUST | C.15 / 106 | Present |
| 38.7 | MUST | C.15 / 106 | Present |
| 38.8 | MUST | C.15 / 106 | Present |
| 39 | MUST NOT | C.10 / 100 | Absent |
| 40 | MUST NOT | C.10 / 100 | Absent |
| 41 | MUST NOT | C.10 / 100 | Absent |
| 42 | SHOULD | C.10 / 100 | Present |
| 43 | MUST | C.10 / 100 | Present |
| 43.1 | MUST NOT | C.16 / 107 | Absent |
| 43.2 | MUST NOT | C.16 / 107 | Absent |
| 43.3 | MUST NOT | C.16 / 107 | Absent |
| 43.4 | MUST NOT | C.16 / 107 | Absent |
| 43.5 | MUST | C.16 / 107 | Present |
| 43.5.1 | MUST | C.17 / 107 | Absent |
| 43.5.2 | MUST | C.17 / 107 | Absent |
| 43.5.2.1 | SHOULD NOT | C.17 / 107 | Absent |
| 43.5.2.2 | MUST | C.17 / 107 | Absent |
| 43.5.2.3 | MUST | C.17 / 107 | Absent |
| 43.5.2.4 | MUST | C.17 / 107 | Absent |
| 43.5.2.5 | SHOULD NOT | C.17 / 107 | Absent |
| 43.5.2.6 | MUST | C.17 / 107 | Absent |
| 43.5.2.6.1 | MUST | C.17 / 107 | Absent |
| 43.5.2.6.2 | MUST | C.17 / 107 | Absent |
| 43.5.2.6.3 | MUST | C.17 / 107 | Absent |
| 43.5.2.6.4 | MUST | C.17 / 107 | Absent |
| 43.5.2.6.5 | MUST | C.17 / 107 | Absent |
| 43.5.2.6.6 | MUST | C.17 / 107 | Absent |
| 43.5.2.6.7 | MUST | C.17 / 107 | Absent |
| 43.5.2.6.8 | MUST | C.17 / 108 | Absent |
| 43.5.2.6.9 | MUST | C.17 / 108 | Absent |
| 43.5.2.6.10 | MUST | C.17 / 108 | Absent |
| 43.5.2.6.11 | MUST | C.17 / 108 | Absent |
| 43.5.2.6.12 | MUST | C.17 / 108 | Absent |
| 43.5.2.8 | MUST | C.17 / 108 | Absent |
| 43.5.3 | MUST | C.17 / 108 | Absent |
| 43.5.4 | MUST | C.17 / 108 | Absent |
| 43.5.5 | SHOULD NOT | C.17 / 108 | Absent |
| 43.5.6 | SHOULD NOT | C.17 / 108 | Absent |
| 43.5.7 | SHOULD NOT | C.17 / 108 | Absent |
| 43.5.8 | SHOULD NOT | C.17 / 108 | Absent |
| 43.5.9 | SHOULD NOT | C.17 / 108 | Absent |
| 43.5.10 | MUST NOT | C.17 / 108 | Absent |
| 43.5.13 | MUST | C.17 / 108 | Absent |
| 43.5.14 | MUST | C.17 / 108 | Absent |
| 43.5.14.1 | MUST | C.17 / 108 | Absent |
| 43.5.14.2 | MUST | C.17 / 108 | Absent |
| 43.5.14.3 | MUST | C.17 / 108 | Absent |
| 43.5.14.4 | MUST | C.17 / 108 | Absent |
| 43.5.14.5 | MUST | C.17 / 108 | Absent |
| 43.5.14.6 | MUST | C.17 / 108 | Absent |
| 43.5.14.7 | MUST | C.17 / 108 | Absent |
| 43.5.14.8 | MUST | C.17 / 108 | Absent |
| 43.5.16 | MUST | C.17 / 108 | Absent |
| 43.5.18 | MUST | C.17 / 108 | Absent |
| 43.5.19 | MUST | C.17 / 108 | Absent |
| 43.5.19.1 | MUST | C.17 / 108 | Absent |
| 43.5.19.2 | MUST | C.17 / 108 | Absent |
| 43.5.19.3 | MUST | C.17 / 108 | Absent |
| 43.5.19.4 | MUST | C.17 / 108 | Absent |
| 43.5.20.1 | MUST | C.17 / 108 | Absent |
| 43.5.20.2 | MUST | C.17 / 108 | Absent |
| 43.5.20.3 | MUST | C.17 / 108 | Absent |
| 43.5.20.4 | MUST | C.17 / 108 | Absent |
| 43.5.21.1 | MUST | C.17 / 108 | Absent |
| 43.5.21.2 | MUST | C.17 / 108 | Absent |
| 43.5.21.3 | MUST | C.17 / 108 | Absent |
| 43.5.27 | MUST | C.17 / 108 | Absent |
| 43.6 | MAY | C.16 / 107 | Present |
| 43.7 | MUST | C.16 / 107 | Present |
| 43.7.1 | MUST | C.18 / 109 | Present |
| 43.7.2 | MUST | C.18 / 109 | Present |
| 43.7.2.1 | MUST | C.18 / 109 | Present |
| 43.7.2.2 | MUST | C.18 / 109 | Present |
| 43.8 | MUST | C.16 / 107 | Absent |
| 43.9 | MUST NOT | C.16 / 107 | Absent |
| 43.10 | MUST | C.16 / 107 | Present |
| 43.11 | SHOULD NOT | C.16 / 107 | Present |
| 45 | SHOULD NOT | C.10 / 100 | Absent |
| 45.1 | SHOULD NOT | C.17 / 108 | Absent |
| 45.2 | SHOULD NOT | C.17 / 108 | Absent |
| 53 | MUST | C.10 / 100 | Present |
| 53.1 | MUST | C.15 / 106 | Present |
| 53.2 | MUST | C.15 / 106 | Present |
| 53.2.1 | MUST | C.15 / 106 | Present |
| 53.2.2 | MUST | C.15 / 106 | Present |
| 53.3 | MUST | C.15 / 106 | Present |
| 53.4 | MUST | C.15 / 106 | Present |
| 54 | MUST | C.10 / 100 | Present |
| 54.1 | MUST | C.15 / 106 | Present |
| 54.2 | MUST | C.15 / 106 | Present |
| 54.3 | MUST | C.15 / 107 | Present |
| 54.4 | MUST | C.15 / 107 | Present |
| 55 | SHOULD NOT | C.10 / 100 | Present |
| 56 | MUST NOT | C.10 / 100 | Absent |
| 58 | MUST | C.10 / 100 | Present |
| 59 | MUST | C.10 / 100 | Present |
| 60 | MUST | C.10 / 100 | Present |
| 60.1 | MUST | C.19 / 109 | Present |
| 60.5 | MUST | C.19 / 109 | Present |
| 60.9.1 | MUST | C.19 / 109 | Present |
| 60.9.2 | MUST | C.19 / 109 | Present |
| 60.9.3 | MUST | C.19 / 109 | Present |
| 60.9.4 | MUST | C.19 / 109 | Present |
| 60.9.5 | MUST | C.19 / 109 | Present |
| 60.9.6 | MUST | C.19 / 109 | Present |
| 60.9.7 | MUST | C.19 / 109 | Present |
| 60.9.8 | MUST | C.19 / 109 | Present |
| 60.9.9 | MUST | C.19 / 109 | Present |
| 60.9.10 | MUST | C.19 / 109 | Present |
| 60.10.1 | MUST | C.19 / 109 | Present |
| 60.10.2 | MUST | C.19 / 109 | Present |
| 60.10.3 | MUST | C.19 / 109 | Present |
| 60.11.1 | MUST | C.19 / 109 | Present |
| 60.11.2 | MUST | C.19 / 109 | Present |
| 60.12.1 | MUST | C.19 / 109 | Present |
| 60.12.2 | MUST | C.19 / 109 | Present |
| 60.12.3 | MUST | C.19 / 109 | Present |
| 60.12.4 | MUST | C.19 / 109 | Present |
| 60.12.5 | MUST | C.19 / 109 | Present |
| 60.12.6 | MUST | C.19 / 109 | Present |
| 60.12.7 | MUST | C.19 / 109 | Present |
| 60.13 | MUST | C.19 / 109 | Present |
| 60.14 | MUST | C.19 / 109 | Present |
| 60.14.1 | MUST | C.19 / 110 | Present |
| 60.14.2 | MUST | C.19 / 110 | Present |
| 60.14.3 | MUST | C.19 / 110 | Present |
| 60.14.4 | MUST | C.19 / 110 | Present |
| 60.14.5 | MUST | C.19 / 110 | Present |
| 60.14.6 | MUST | C.19 / 110 | Present |
| 60.14.7 | MUST | C.19 / 110 | Present |
| 60.14.8 | MUST | C.19 / 110 | Present |
| 60.14.9 | MUST | C.19 / 110 | Present |
| 60.14.10 | MUST | C.19 / 110 | Present |
| 60.15 | MUST | C.19 / 110 | Present |
| 60.15.1 | MUST | C.19 / 110 | Present |
| 60.15.2 | MUST | C.19 / 110 | Present |
| 60.15.3 | MUST | C.19 / 110 | Present |
| 60.15.4 | MUST | C.19 / 110 | Present |
| 60.15.5 | MUST | C.19 / 110 | Present |
| 60.15.6 | MUST | C.19 / 110 | Present |
| 60.15.7 | MUST | C.19 / 110 | Present |
| 60.15.8 | MUST | C.19 / 110 | Present |
| 60.15.9 | MUST | C.19 / 110 | Present |
| 60.15.10 | MUST | C.19 / 110 | Present |
| 60.15.11 | MUST | C.19 / 110 | Present |
| 60.15.12 | MUST | C.19 / 110 | Present |
| 60.15.13 | MUST | C.19 / 110 | Present |
| 60.16 | MUST | C.19 / 110 | Present |
| 60.17.1 | MUST | C.19 / 110 | Present |
| 60.17.2 | MUST | C.19 / 110 | Present |
| 60.43 | MUST | C.19 / 110 | Present |
| 61 | MUST | C.10 / 100 | Present |
| 62 | MUST | C.10 / 100 | Present |
| 63 | MUST | C.10 / 100 | Present |
| 64 | MUST | C.10 / 100 | Present |
| 65 | MUST | C.10 / 100 | Present |
| 65.1 | MUST | C.17 / 108 | Absent |
| 66 | SHOULD NOT | C.10 / 100 | Present |
| 67 | MUST | C.10 / 100 | Present |
| 70 | MUST | C.10 / 100 | Present |
| 70.1 | MUST | C.14 / 104 | Present |
| 70.37 | MUST | C.14 / 106 | Present |
| 71 | MUST | C.10 / 100 | Present |
| 71.1 | MUST | C.14 / 104 | Present |
| 71.37 | MUST | C.14 / 106 | Present |
| 72 | MUST | C.10 / 100 | Present |
| 73 | MUST | C.10 / 100 | Present |
| 79 | MUST | C.10 / 100 | Absent |
| 83 | MUST | C.17 / 108 | Absent |
| 83.1 | MUST | C.17 / 108 | Absent |
| 83.2 | MUST | C.17 / 108 | Absent |
| 83.3 | MUST | C.17 / 108 | Absent |
| 83.4 | MUST | C.17 / 109 | Absent |
| 255 | MUST | C.10 / 100 | Present |
