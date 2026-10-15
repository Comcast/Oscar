# Sources and navigation

## Pinned source

- Identifier: **CM-SP-MULPIv4.0-I12-261014**; issue date **2026-10-14**.
- [Official source/discovery page](https://www.cablelabs.com/specifications/CM-SP-MULPIv4.0).
- [Original PDF](sources/CM-SP-MULPIv4.0-I12-261014.pdf).
- [Complete searchable source](sources/CM-SP-MULPIv4.0-I12-261014.txt).

The user supplied the original I12 PDF on 2026-10-15. Its title and document
status sheet identify the issue and date. The PDF is preserved unchanged;
the TXT was generated using `pdftotext -layout` and is a search aid. The PDF
controls if extraction leaves an encoding ambiguous. The public URL is a
discovery page, not evidence that its current download is the pinned issue.

The [previous I11 text](sources/CM-SP-MULPIv4.0-I11-260219.txt) is retained
unchanged for historical comparisons. It was originally copied from the user's
`cable-docsis-4-cablelabs-protocol-review` skill. Its definitions and printed
page numbers must not be silently reused as I12 evidence. File sizes and hashes
for both issues are recorded in [the source manifest](source-manifest.json).

The sources retain CableLabs notices. They are included for Oscar product development in accordance with the specification. This package’s guidance is independent implementation guidance, not an amended specification. The supplied text/PDF contains material outside this skill’s scope; consult only configuration-file provisions and necessary dependencies.

## Entry points

### External configuration dependencies

The following user-supplied PDFs were moved unchanged from the DOCSIS 3.1
skill root on 2026-10-15. They supplement, and do not replace, MULPI I12.
Their exact editions, hashes, and derived text are recorded in the manifest.

| Dependency | Original PDF | Searchable text | Use |
| --- | --- | --- | --- |
| CM-SP-SEC3.0-C01-171207 | [PDF](sources/CM-SP-SEC3.0-C01-171207.pdf) | [TXT](sources/CM-SP-SEC3.0-C01-171207.txt) | MMH algorithms referenced by MULPI D.2.1 |
| CM-SP-SECv4.0-I08-250903 | [PDF](sources/CM-SP-SECv4.0-I08-250903.pdf) | [TXT](sources/CM-SP-SECv4.0-I08-250903.txt) | Security and certificate configuration dependencies |
| CM-SP-SYNC-I03-220715 | [PDF](sources/CM-SP-SYNC-I03-220715.pdf) | [TXT](sources/CM-SP-SYNC-I03-220715.txt) | Configuration TLVs 101 and 102 |

MULPI calls the SEC3 document `CM-SP-SECv3.0-C01-171207`; the supplied
document's cover and running headers use `CM-SP-SEC3.0-C01-171207`. The stored
filename follows the cover. Cite the document actually consulted. External
definitions remain unverified until their normative provisions and test vectors
have been checked; bundling a PDF does not establish implementation coverage.

### MULPI navigation

All pages below are **printed specification pages**, not viewer indices or TXT line numbers. Search by heading and inspect surrounding prose, not just the first table-of-contents hit.

| Need | Read |
| --- | --- |
| Establish top-level file applicability | Annex C, Table 117, pp. 692-695; then the referenced normative setting |
| Shared configuration/registration settings | C.1.1, p. 695 onward |
| File-only settings | C.1.2, p. 719 onward |
| Classifier and service-flow child definitions | C.2.1 and C.2.2; check provisioned-file context for each child |
| File envelope, mandatory items, creation order | D.1.1-D.1.3, p. 857 onward |
| CM and CMTS MIC rules | D.1.3.1 and D.2.1, p. 860 onward; read full algorithms |
| Extension/vendor settings | C.1.1.18 and the applicable external definition |
| Other-interface settings | C.3 only where the top-level table and normative prose permit file use |

Search the bundled TXT with a literal heading or full numeric path. For example, `Network Access Control Object`, `SNMP MIB Object`, `CMTS MIC Calculation`, or `Service Flow Reference`. Use a text search tool with line numbers and sufficient context. Do not load the entire source into a prompt. In extracted tables a number can wrap across lines: never turn a split suffix into two path components without checking the table layout.

## Consulted user resources

Source discovery consulted the user’s `cable-docsis-4-cablelabs-protocol-review/SKILL.md` and its `all_spec` directory under `Downloads/mxl-skill/ai_skills`. The adjacent `docsis-code-review/SKILL.md` was inspected for available spec routing; it is a PR-review orchestrator, not TLV authority. Neither skill is an installation dependency.

## I12 edition and implementation boundaries

Table 117 identifies TLVs 84, 96, 97, and 106 as configuration-file settings;
follow C.1.2.23-C.1.2.26 for their current encodings and dependencies. The I11
lookup vectors are not carried forward as verified I12 vectors without checking
those sections. Capability reports 5/105 and FDX MAC-message-only fields 85-87
are not configuration-file controls.

For the current Oscar milestone, defer L2VPN-specific settings (including TLV
65 and L2VPN-specific nested extensions) and eDOCSIS/eSAFE TLVs 201-231. These
are scope exclusions, not claims that the fields are invalid in configuration
files. Report other unresolved external definitions as unverified; excluding
L2VPN and eDOCSIS does not verify DOCSIS SYNC or other referenced specifications.

Generate lab fixtures using a public test key only. Supporting an I12 reference
does not itself implement a DOCSIS 4.0 runtime mode or demonstrate CM/CMTS
interoperability.
