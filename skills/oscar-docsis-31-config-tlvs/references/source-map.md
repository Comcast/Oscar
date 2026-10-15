# Sources and navigation

## Pinned source

- Identifier: **CM-SP-MULPIv3.1-I21-201020**; issue date **2020-10-20**.
- [Official source/discovery page](https://account.cablelabs.com/server/alfresco/3a751ba9-593c-464a-bc91-3675e1824e30).
- [Complete searchable source](sources/CM-SP-MULPIv3.1-I21-201020.txt).
- [Original PDF](sources/CM-SP-MULPIv3.1-I21-201020.pdf).

Retrieved directly from CableLabs on 2026-10-15. The complete PDF is retained unchanged; the TXT is a search aid extracted with pypdf. The PDF controls when extraction is ambiguous.

This is a deliberately pinned older 3.1 edition, not a claim to cover the latest 3.1 issue. No local 3.1 specification skill/source was found in the searched user files.

The sources retain CableLabs notices. They are included for Oscar product development in accordance with the specification. This package’s guidance is independent implementation guidance, not an amended specification. The supplied text/PDF contains material outside this skill’s scope; consult only configuration-file provisions and necessary dependencies.

## Entry points

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

## Edition boundary

In this issue, Table 108 labels 85–87 and 96–97 reserved for DOCSIS 4.0; do not borrow their 4.0 definitions. Annex C.1.3 is registration-specific. Use Annex D.1.2 to establish required file contents rather than treating all supported settings as mandatory.

Small verification anchor: `03 01 01` is the Network Access Control Object with value 1 (C.1.1.3, p. 594). It is only a TLV fragment, not a complete file. Check the source for its complete semantics before changing forwarding-related UI descriptions.
