# eRouter source map

Baseline: **CM-SP-eRouter-I21-220209**, February 9, 2022, supplied by the user.
The [complete extracted text](sources/CM-SP-eRouter-I21-220209.txt) retains
source notices. The [manifest](source-manifest.json) records hashes and extraction.
No external download URL has been verified.

Extraction: `pdftotext -layout -enc UTF-8 INPUT.pdf OUTPUT.txt`.
All 125 nonempty PDF page segments are present, with no Unicode replacement
characters. Form feeds preserve page boundaries. For Annex B pages below,
printed page numbers equal one-based PDF page numbers. Pages 93 and 95 were
visually compared with the original PDF, including tables and the OID example.
The PDF is excluded after verification under repository policy; obtain this
exact issue again if another table needs visual inspection.

## Navigation

| Topic | Section | Printed pages / full paths |
| --- | --- | --- |
| SNMP framework table population | B.1, especially B.1.3 | Before B.2; tables B-1 through B-9 |
| Runtime mode control versus file settings | B.2 | 87 |
| Repeated container ordering | B.3 | 87; 202 |
| Processing and initialization | B.4.1–B.4.2 | 87–88; 202.1 |
| TR-069 management server | B.4.3 | 88–89; 202.2 and .1–.7 children |
| TR-369 controller count/configuration | B.4.4 | 89–91; 202.4, 202.5 and .1–.5 children |
| Initialization override | B.4.5 | 91; 202.3 |
| SNMP coexistence and transport addresses | B.4.6 | 91–92; 202.53, including 202.53.2.1/.2 |
| SNMP access view | B.4.7 | 92–93; 202.54 |
| Vendor information and OUI | B.4.8 | 93–94; 202.43, 202.43.8 |
| SNMP VarBind | B.4.9 | 94; 202.11 |
| Topology mode | B.4.10 | 94; 202.42 |
| RA interval | B.4.11 | 94–95; 202.10 |
| Multicast configuration server | B.4.12 | 95; 202.12 |
| Link-ID control | B.4.13 | 95; 202.13 |
| Soft reset excluded from configuration-file operation | B.5 | 95–96 |
| Mode precedence and persistence | Annex C | Search Annex C after B.6 |

## Encoding cautions

- B.4.11 explicitly gives a **two-byte** RA interval, 3–1800 seconds,
  default 30. Do not infer integer width from a Java type.
- B.4.3.7 prints ACS Override length `N`; B.4.4 prints integer lengths `n`.
  These do not establish a fixed width. Record unresolved width explicitly.
- B.4.7.2 prints `06 03 01 03 06` for OID 1.3.6. This is present in the
  original PDF, not an extraction error. It conflicts with conventional BER
  first-two-arc packing; resolve against authoritative ASN.1 material/errata
  before choosing encoding. Do not silently normalize the source text.
- B.4.6.4 prints `1.32`; preserve the source and flag the apparent range typo.
- B.4.9 defines 202.11 directly, not 202.43.11. VarBinds may repeat and each
  is limited to 255 bytes. B.2/B.5 exclude init-mode-control/soft-reset SETs
  from file-driven operation.
- TR-069 passwords, connection-request credentials, SNMP community strings
  and credential-bearing VarBinds require synthetic test values and masking
  in UI/logging paths. Display masking must not alter serialized values.

## Delegated sources

Read section 2 of the bundled text for the precise references. eDOCSIS owns
outer encapsulation/eSAFE MIB definitions; MULPI and SEC own DOCSIS framing
and integrity. TR-069, TR-369 and the cited TR-181 data models supply management
parameter semantics. RFC 1157 supplies VarBind structure; RFC 3412/3415 supply
SNMP framework/access-control behavior; MC-EMC supplies multicast server use.
These documents are not bundled here. Do not substitute an arbitrary latest
edition or claim their detailed requirements were verified from this PDF.
