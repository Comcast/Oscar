# Sources and navigation

## Current review source: I25

- **CM-SP-MULPIv3.1-I25-230419**, issued 2023-04-19.
- [Searchable text](sources/CM-SP-MULPIv3.1-I25-230419.txt).
- [Provenance and SHA-256](source-manifest-i25.json).
- Supplied by the user on 2026-10-27; extracted with `pdftotext -layout`.
  The local PDF remains under `agents/` and is excluded from Git.
- [Registry coverage audit](coverage-audit.md) distinguishes catalog presence
  from verified codec coverage. The catalog edition and source hash now identify
  I25; legacy compatibility entries remain documented in the audit.

| Need | I25 printed pages / section |
| --- | --- |
| Top-level Cfg File applicability | Table 108, pp. 594–596; follow normative references |
| Repeated SNMP coexistence rows | C.1.2.13, pp. 624–625 |
| Manufacturer / co-signer CVC chains | C.1.2.21 / C.1.2.22, p. 629 |
| Packet classifiers / service flows | C.2.1 p. 681 / C.2.2 p. 696 onward |
| File format / mandatory contents | D.1.1 / D.1.2, p. 737 |
| File creation / CMTS MIC | D.1.3 p. 738 / D.2.1 p. 741 |

I25 adds types 98–104 to Table 108 compared with I21. Types 98–100
are registration-only; 101/102 delegate configuration definitions to DOCSIS SYNC.
Types 103/104 are reserved for DOCSIS 4.0 despite Cfg File marks; do not invent
DOCSIS 3.1 encodings. Preserve I21 as a separate historical authority below.


## Historical I21 source

- Identifier: **CM-SP-MULPIv3.1-I21-201020**; issue date **2020-10-20**.
- [Official source/discovery page](https://account.cablelabs.com/server/alfresco/3a751ba9-593c-464a-bc91-3675e1824e30).
- [Complete searchable source](sources/CM-SP-MULPIv3.1-I21-201020.txt).

Retrieved directly from CableLabs on 2026-10-15. The extracted TXT is retained; the PDF was removed at user request. Obtain the original matching issue if extraction is ambiguous.

This is a deliberately pinned older 3.1 edition, not a claim to cover the latest 3.1 issue. No local 3.1 specification skill/source was found in the searched user files.

The sources retain CableLabs notices. They are included for Oscar product development in accordance with the specification. This package’s guidance is independent implementation guidance, not an amended specification. The supplied text/PDF contains material outside this skill’s scope; consult only configuration-file provisions and necessary dependencies.

## Historical I21 entry points

All pages below are **printed specification pages**, not viewer indices or TXT line numbers. Search by heading and inspect surrounding prose, not just the first table-of-contents hit.

| Need | Read |
| --- | --- |
| Establish top-level file applicability | Annex C, Table 108, pp. 591–593; then the referenced normative setting |
| Shared configuration/registration settings | C.1.1, p. 593 onward |
| File-only settings | C.1.2, p. 616 onward |
| Classifier and service-flow child definitions | C.2.1 and C.2.2; check provisioned-file context for each child |
| File envelope, mandatory items, creation order | D.1.1–D.1.3, p. 733 onward |
| CM and CMTS MIC rules | D.1.3.1 and D.2.1, p. 737 vicinity; read full algorithms |
| Extension/vendor settings | C.1.1.18 and the applicable external definition |
| Other-interface settings | C.3 only where the top-level table and normative prose permit file use |

Search the bundled TXT with a literal heading or full numeric path. For example, `Network Access Control Object`, `SNMP MIB Object`, `CMTS MIC Calculation`, or `Service Flow Reference`. Use a text search tool with line numbers and sufficient context. Do not load the entire source into a prompt. In extracted tables a number can wrap across lines: never turn a split suffix into two path components without checking the table layout.

## Consulted user resources

Source discovery consulted the user’s `cable-docsis-4-cablelabs-protocol-review/SKILL.md` and its `all_spec` directory under `Downloads/mxl-skill/ai_skills`. The adjacent `docsis-code-review/SKILL.md` was inspected for available spec routing; it is a PR-review orchestrator, not TLV authority. Neither skill is an installation dependency.

## Historical I21 edition boundary

In this issue, Table 108 labels 85–87 and 96–97 reserved for DOCSIS 4.0; do not borrow their 4.0 definitions. Annex C.1.3 is registration-specific. Use Annex D.1.2 to establish required file contents rather than treating all supported settings as mandatory.

Small verification anchor: `03 01 01` is the Network Access Control Object with value 1 (C.1.1.3, p. 594). It is only a TLV fragment, not a complete file. Check the source for its complete semantics before changing forwarding-related UI descriptions.

## Delegated configuration encodings

The [delegated source manifest](source-manifest-delegated.json) records hashes
and origins. These are the editions referenced by I25.

- [L2VPN I16](sources/CM-SP-L2VPN-I16-220328.txt): B.2 and B.3.4,
  TLV 45 downstream unencrypted traffic filtering.
- [SYNC I03](sources/CM-SP-SYNC-I03-220715.txt): D.2.1–D.2.2,
  TLVs 101/102 and their configuration children.
- [LLX I02](sources/CM-SP-LLX-I02-200623.txt): Annex C does not assign
  TLV 88 child numbers. Only opaque payload preservation is supported.
