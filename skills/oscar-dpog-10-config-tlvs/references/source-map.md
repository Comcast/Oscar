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

## Delegated sources verified for implementation

DPoG section 2.1 references CM-SP-MULPIv3.0 without an issue identifier.
The implementation consulted the following exact editions; other external
references remain unverified unless recorded separately:

- [CM-SP-MULPIv3.0-C01-171207](sources/CM-SP-MULPIv3.0-C01-171207.txt),
  [CableLabs download](https://account.cablelabs.com/server/alfresco/cce8943e-b49b-4c24-bc83-107acd636001).
  Search C.1.2.1–2 for end/pad markers; D.1.1–D.2.1 for framing/MICs
  (pp. 615–621); C.2.1.4.8 for CMIM; C.1.1.18.1 for Vendor ID;
  C.1.1.18.1.6 for extended MIC fields.
- [CM-SP-L2VPN-I15-150528](sources/CM-SP-L2VPN-I15-150528.txt),
  [CableLabs download](https://account.cablelabs.com/server/alfresco/e562be82-d890-44bf-ac98-9e91b9d053a5).
  B.2.1–2, p. 93 defines DUT filtering TLV 45 and children.

The manifest records acquisition URLs and PDF/text hashes. Full searchable
text retains original notices. Source acquisition does not certify every
inherited registry field. DPoG Table 9 explicitly lists downstream CMIM 23.13,
whereas MULPI 3.0 C.2.1.4.8 lists only upstream 22/60.13: the DPoG entry uses
the delegated bitmap representation with DPoG-specific applicability.
