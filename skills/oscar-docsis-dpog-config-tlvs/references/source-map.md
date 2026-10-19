# Source map

## Provenance

- Baseline: **DPoG-SP-MULPIv1.0-C01-160830**, 2016-08-30, CLOSED.
- Origin: user-provided PDF in this skill directory; no download URL verified.
- [Complete searchable text](sources/DPoG-SP-MULPIv1.0-C01-160830.txt).
- [Source manifest](source-manifest.json): original PDF and extracted text
  sizes/hashes, extraction tool, and verification details.

The 97-page PDF was extracted with Poppler `pdftotext -layout -enc UTF-8`.
Form feeds preserve page boundaries. Printed page numbers match one-based PDF
pages for the navigation anchors below. Original CableLabs notices remain in
text; skill guidance is separate from the specification. The PDF was removed
at the user's request after extraction checks. Reacquire this same issue for
ambiguous diagrams/tables; text extraction does not preserve graphical detail.

## Navigation

| Task | Section | Printed page |
| --- | --- | ---: |
| File validation and invalid MIC handling | 9.1.5.6 | 61 |
| Dynamic-update file validation | 9.5.2.2 | 68 |
| Applicability and top-level support table | Annex C intro, C.1, Table 7 | 73–75 |
| D-ONU capabilities (DHCP, not file-editor fields) | C.2–C.2.3 | 75–76 |
| SNMP MIB objects | C.3, Table 8 | 76 |
| Upstream/downstream classifiers | C.4, Table 9 | 77–79 |
| Service flows and aggregates | C.5, Table 10 | 80–81 |
| Device management | C.6, Table 11 | 81–82 |
| DOCSIS extension field, extended MIC | C.7, Table 12 | 82 |
| DPoE MEF and L2VPN applicability | C.8, Table 13 | 82–84 |
| Subscriber management | C.9, Table 14 | 84 |
| Upstream drop classifiers | C.10, Table 15 | 84–86 |

Search a section heading and inspect the actual body, not its table-of-contents
entry. Preserve indentation while reading table rows; wrapped text may belong
to the preceding row. PDF page 73 was visually compared against the extracted
Annex C introduction and Table 7 during skill creation.

## Verification anchors and limits

Table 7, p. 73: TLV 1 and 2 support is MUST NOT; TLV 3 support is MUST;
TLV 5's comment excludes it from configuration files; TLVs 6 and 7 are MAY.
These are applicability anchors, not complete byte-encoding test vectors.
C.7/Table 12, p. 82 lists 43.6 as MAY. Section 9.1.5.6, p. 61 distinguishes
mandatory-item/file validation failures from optional rejection of invalid MICs.

Section 2.1 references CM-SP-MULPIv3.0 without an issue identifier. This skill
bundles only the supplied DPoG document: inherited lengths, enums, mandatory
file elements, and MIC algorithms requiring that reference are not verified
merely by reading its DPoG support table. Follow other references such as
DPoG-OSSI/OAM or DPoE-MEF only when needed; record missing evidence explicitly.
