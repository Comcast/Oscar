# Source map

## Provenance

- Baseline: **DPoE-SP-MULPIv2.0-I14-230322**, March 22, 2023.
- Origin: user-provided PDF; no download URL verified.
- [Complete searchable source](sources/DPoE-SP-MULPIv2.0-I14-230322.txt).
- [Source manifest](source-manifest.json): hashes, sizes and extraction checks.

Extracted with `pdftotext -layout -enc UTF-8`. Form feeds preserve all 129 PDF
pages; printed Arabic page numbers match one-based PDF pages in the sections
below. Original notices remain in the text. PDF originals are excluded under
repository policy; reacquire the matching issue for ambiguous figures/tables.
Pages 87, 91 and 96 were visually checked against the extraction.

## Navigation

| Topic | Section | Printed pages |
| --- | --- | --- |
| Exact external editions | 2.1 | 12–13 |
| Configuration processing and invalid MICs | 9.1.5.6 | 71–72 |
| Dynamic update validation | 9.5.2.2 | 78 |
| UNI management / TLV 79 | 9.6 | 79–80 |
| S-Tag/C-Tag classifiers | C.1 | 83–86 |
| I-Tag classifiers | C.2 | 86–90 |
| MPLS classifiers | C.3 | 90 |
| Slow protocol subtype | C.4 | 90–91 |
| MESP, bandwidth, color and names | C.5 | 91–95 |
| Aggregate service flows | C.6 | 95 |
| SF/ASF references, names, SF Collection | C.7 | 96 |
| D-ONU capabilities: DHCP only | C.8 | 96–98 |
| Network timing profile | C.9 | 98–99 |
| Top-level applicability | C.10 / Table 7 | 99–100 |
| SNMP objects | C.11 / Table 8 | 101 |
| Legacy security exclusions | C.12 / Table 9 | 102 |
| Ordinary classifiers | C.13 / Table 10 | 102–104 |
| Service flows and aggregates | C.14 / Table 11 | 104–106 |
| Management TLVs | C.15 / Table 12 | 106–107 |
| DOCSIS extension field | C.16 / Table 13 | 107 |
| L2VPN, MEF and L2CP TLV 83 | C.17 / Table 14 | 107–109 |
| Source address verification | C.18 / Table 15 | 109 |
| Drop classifiers | C.19 / Table 16 | 109–110 |
| CPE devices versus IP-address limits | C.20 | 110–111 |
| eSAFE DHCP snooping | Annex D | 112 |

## Delegated sources (not bundled)

Section 2.1 pins MULPI to **CM-SP-MULPIv3.0-C01-171207**. It also references:

- DPoE-SP-MEFv2.0-I07-230322 and MEF 10.2 (October 2009).
- DPoE-SP-IPNEv2.0-I08-230322.
- DPoE-SP-OSSIv2.0-I13-230322.
- DPoE-SP-OAMv2.0-I15-230322.
- DPoE-SP-SECv2.0-I07-230322.
- CM-SP-L2VPN-I16-220328.

Consult these only for the settings that need them. Their listing here does not
mean their contents have been verified. In particular, TLV 79 and rate-unit
settings 24/25.41 are delegated to MULPIv3.0; L2VPN children need MEF/L2VPN.
