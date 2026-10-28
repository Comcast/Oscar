# DOCSIS 3.1 registry coverage audit

Reviewed 2026-10-27 against CM-SP-MULPIv3.1-I21-201020 and the supplied
CM-SP-MULPIv3.1-I25-230419. This covers the I25 configuration-file inventory, its missing nested encodings,
and codec regression tests. It is not CM/CMTS certification or validation of all
semantic relationships in a deployable file.

## Confirmed import defects corrected

| Path | Finding | Evidence |
| --- | --- | --- |
| 81 | Missing ManufacturerCVCChain; use existing multi_tlv_byte_array codec | I21 C.1.2.21 p. 626; I25 C.1.2.21 p. 629 |
| 82 | Missing Co-signerCVCChain; same fragmentation rules | I21 C.1.2.22 p. 626; I25 C.1.2.22 p. 629 |
| 53 | Incorrect single-instance registry limit | I21 C.1.2.13 p. 621; I25 C.1.2.13 p. 624 |
| 53.2 | Repeated containers shared mutable child definitions in import and unbounded child searches in export | Same sections; multiple 53.2 containers are explicitly permitted |
| 53.2.1 / 53.2.2 | Keep each address/mask within its own parent; preserve 6-byte IPv4 and 18-byte IPv6 values | I25 C.1.2.13.2.1–2, pp. 624–625 |

A chain is one logical value split into consecutive physical fragments. Each
non-final fragment is 254 bytes. `maxInstances: 1` describes the logical chain,
not the number of binary fragments. Certificate parsing, chain trust, expiry,
and MIC authentication are outside this codec check.

The supplied 2786-byte binary contains 21 payload TLVs after excluding CM MIC,
CMTS MIC and the end marker. Its 2409-byte manufacturer chain occupies nine
254-byte fragments and one 123-byte fragment. After these corrections, in-memory
binary → text → binary preserves all 21 payload entries byte-for-byte, in order.
The original file is not modified and its private values are not published.

## Top-level gaps implemented

Table 108 is an informative applicability index; each implementation must follow
its normative section and review every child. The following previously absent definitions are now in `docsis.json`:

| Type | Setting | I21 / I25 section or delegated source |
| --- | --- | --- |
| 45 | Downstream Unencrypted Traffic Filtering | C.1.1.24 |
| 76 | CM Upstream AQM Disable | C.1.2.20 |
| 79 | UNI Control Encoding | C.3.3 |
| 83 | DTP Mode Configuration | C.1.1.31 |
| 84 | Diplexer Band Edge | C.1.2.23 |
| 88 | QoS Framework for DOCSIS Encodings (opaque HEX only) | C.1.7; LLX I02 Annex C does not assign children |
| 91 | Low Latency Disable | C.1.1.32 |
| 92 | Distributed HQoS Enable | C.1.1.33 |
| 93 | Upstream Enhanced HQoS ASF | Table 108; C.2.2.5–6 and C.2.2.9.18 |
| 94 | Downstream Enhanced HQoS ASF | Table 108; C.2.2.5–6 and C.2.2.9.18 |
| 101 | DOCSIS Sync Configurations (added in I25 table) | DOCSIS SYNC; SYNC I03 D.2.1–D.2.2, with configuration children |
| 102 | PTP Address Configurations (added in I25 table) | DOCSIS SYNC; SYNC I03 D.2.1–D.2.2, with configuration children |

The first ten were already listed as Cfg File settings in I21 (pp. 591–593).
I25's corresponding table is on pp. 594–596. Thus the principal gap is old
registry coverage, not merely the specification edition.

Other Cfg File top-level numbers present in the registry are:
`0, 1, 2, 3, 6, 7, 9, 10, 11, 14, 17, 18, 19, 20, 21, 22, 23, 24, 25,
28, 29, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 53, 54, 55, 56,
58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 81,
82, 255`. Presence does not establish correct child coverage, codecs, values,
instance limits, or applicability of every child. Those need a separate detailed
normative audit. In particular, do not infer coverage from template generation.

The eSAFE range 201–231 delegates to eDOCSIS and individual service specifications.
The current registry has 201, 202, 216, 217, 219, 220 and 221, with reviewed
references for eRouter/eSTB and opaque placeholders elsewhere. This is not
coverage of every delegated service.

## Nested additions and limits

- Repeated 41/41.1/41.2 and 70/71 instances are available in registry consumers
  (C.1.1.22 and 7.7.4). A repeatable marker is not a device-capacity guarantee.
- 41.1.3 / 41.2.5: OFDM or SC-QAM channel selection (C.1.1.22).
- 45.1–2: DUT control and interface mask (L2VPN I16 B.2/B.3.4).
- 74.4 and activity detection children: light sleep (C.1.1.30.3–4).
- 79.1–9: UNI context and control (C.3.3).
- 84.1–3: three diplexer edges (C.1.2.23), nine-byte parent payload.
- 24/25.39–41: IATC, AQM and rate units (C.2.2.9.14–16).
- 24.44–46,48–49: PGS timing/rate, queue depth and activity detection
  (C.2.2.10.13–17).
- 70/71: QoS profile, traffic parameters, peak rate, matching criteria and
  low-latency controls (C.2.2.9). Repeated matching children retain wire order.
- 93/94: references, traffic parameters and repeated constituent scheduling
  (C.2.2.7, C.2.2.9.1–5 and C.2.2.9.18).
- 101.1–4 / 102.1–3: SyncE/PTP controls, IPv4/IPv6 and multicast MAC
  (SYNC I03 D.2.1–2).

TLV 88 supports raw bytes only. The exact LLX I02 referenced by I25 leaves
management encoding details for a future edition; structured children remain
unverified. There is no universal claim of support for every eSAFE, vendor
extension, or later specification. Existing width/codec validation does not
replace semantic checks for all enum ranges, required siblings, references,
capability dependencies or mutually exclusive settings.

## Do not import every number as a configuration setting

I25 98–100 are registration-only. 85–87, 96–97, and 103–104 are reserved for
DOCSIS 4.0; the Cfg File marks on 103/104 do not define a DOCSIS 3.1 encoding.
Other absent numbers such as 77, 78, 80, 89, 90 and 95 are not marked Cfg File.
Existing legacy definitions include registration/message-only or deprecated
entries (e.g. 4, 5, 8, 12, 13, 26, 27, 30, 31, 44, 46–52, 57, 75).
They are retained for compatibility; their presence is not permission to provision
them in a DOCSIS 3.1 file. No wholesale deletion is part of this correction.

The runtime edition/hash now identify I25 and its bundled searchable text.
This source tag does not certify the inherited legacy entries or delegated
services. I21 source and provenance remain unchanged.

## Repeatable verification

- `Docsis31CvcChainTest`: registry presence, fragment preservation, repeatable
  parent metadata, independent mixed IPv4/IPv6 address rows.
- `CvcApiRoundTripTest`: certificate lengths around fragment boundaries, both
  chain types, legacy certificates plus chains, finalized lab-key files and
  repeated byte-for-byte round trips.
- `Docsis31I25CatalogTest`: missing configuration roots and child availability;
  excluded response-only children.
- `Docsis31I25WireTest`: independent wire fragments for new families, repeated
  HQoS/ASF children, unsigned rates, AQM, light sleep and SYNC IPv4/IPv6.
- `TlvRegistryTest` and `EmbeddedServiceCatalogTest`: shared catalog contract.
- `tools/run-all.sh`: complete repository checks.

Initial regressions failed before fixes: both chain definitions were absent;
53 was limited to one instance; the mixed-address round trip reduced a synthetic
140-byte input to 40 bytes (104 bytes after correcting only binary decoding).
Fixing both binary decoding and text-instance ownership is necessary.

New ASF/HQoS wire regressions failed in four cases before generalizing independent
container ownership. Entries were dropped or merged. The unchanged wire fixtures
pass with the corrected parser and bounded decoder.

## Validation result (2026-10-27)

`tools/run-all.sh` passed: 701 Java tests, zero failures/errors, two skipped;
32 Python tests passed. The catalog adds 137 definitions (14 top-level entries
and 123 descendants). Skill validation passed through the canonical path and
both discovery links; bundled source hashes were verified.

The supplied binary passed the 21-payload-entry ordered comparison again after
the complete update. OSCAR-UI's existing `OscarBinaryService.openAuto` opened it
as DOCSIS when tested with the updated OSCAR classes. This does not update the
UI's pinned JAR or claim native UI validation. Production values were not added
to test fixtures, and the supplied file was not modified.
