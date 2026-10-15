# Sources and navigation

## Pinned source

- Identifier: **CM-SP-MULPIv4.0-I11-260219**; issue date **2026-02-19**.
- [Official source/discovery page](https://www.cablelabs.com/specifications/CM-SP-MULPIv4.0).
- [Complete searchable source](sources/CM-SP-MULPIv4.0-I11-260219.txt).

Copied unchanged from the user’s cable-docsis-4-cablelabs-protocol-review skill, all_spec/CM-SP-MULPIv4.0-I11.txt. Its title and running headers identify the full issue. The public URL is a discovery page, not evidence that its current download is the pinned issue.

The supplied source is extracted text, not a PDF. Where columns, line wrapping, diagrams, or missing headings leave an encoding ambiguous, obtain the matching I11 PDF before implementing that field.

The sources retain CableLabs notices. They are included for Oscar product development in accordance with the specification. This package’s guidance is independent implementation guidance, not an amended specification. The supplied text/PDF contains material outside this skill’s scope; consult only configuration-file provisions and necessary dependencies.

## Entry points

All pages below are **printed specification pages**, not viewer indices or TXT line numbers. Search by heading and inspect surrounding prose, not just the first table-of-contents hit.

| Need | Read |
| --- | --- |
| Establish top-level file applicability | Annex C, Table 117, pp. 699–702; then the referenced normative setting |
| Shared configuration/registration settings | C.1.1, p. 702 onward |
| File-only settings | C.1.2, p. 726 onward |
| Classifier and service-flow child definitions | C.2.1 and C.2.2; check provisioned-file context for each child |
| File envelope, mandatory items, creation order | D.1.1–D.1.3, p. 866 onward |
| CM and CMTS MIC rules | D.1.3.1 and D.2.1, p. 869 vicinity; read full algorithms |
| Extension/vendor settings | C.1.1.18 and the applicable external definition |
| Other-interface settings | C.3 only where the top-level table and normative prose permit file use |

Search the bundled TXT with a literal heading or full numeric path. For example, `Network Access Control Object`, `SNMP MIB Object`, `CMTS MIC Calculation`, or `Service Flow Reference`. Use a text search tool with line numbers and sufficient context. Do not load the entire source into a prompt. In extracted tables a number can wrap across lines: never turn a split suffix into two path components without checking the table layout.

## Consulted user resources

Source discovery consulted the user’s `cable-docsis-4-cablelabs-protocol-review/SKILL.md` and its `all_spec` directory under `Downloads/mxl-skill/ai_skills`. The adjacent `docsis-code-review/SKILL.md` was inspected for available spec routing; it is a PR-review orchestrator, not TLV authority. Neither skill is an installation dependency.

## Verified 4.0 lookup anchors

These are navigation aids, not a complete registry; read each section before implementing its constraints.

| Path | Setting | Encoding anchor | Citation |
| --- | --- | --- | --- |
| 84 | Diplexer Band Edge | Length 9; nested fields | C.1.2.23, pp. 737–738 |
| 96 | FDD Diplexer Band Edge | Length 12; children 96.1–96.3 each hold 2-octet MHz values | C.1.2.24, pp. 738–739 |
| 97 | CM Advanced Band Plan Control | Length 1; 0 disable, 1 enable; absent means enabled | C.1.2.25, p. 739 |
| 106 | FDX Downstream Upper Band Edge | Length 2; frequency in MHz | C.1.2.26, p. 739 |

Do not substitute the legacy enumerations in 84 for the frequency values in 96. Before presenting a band-edge control, establish FDD/FDX applicability and the dependencies/precedence in C.1.2.24–C.1.2.26. The example frequencies do not, by themselves, establish a closed enum. Do not present capability reports 5/105 as configuration-file controls: see C.1.3 and Table 117.

Independent byte anchors: `61 01 00` encodes type 97, length 1, disabled. `6a 02 04 c2` encodes type 106, length 2, 1218 MHz. These are TLV fragments, not complete files or endorsements of deployment settings. The meanings are from C.1.2.25 and C.1.2.26; serialization uses C.1 network byte order.
