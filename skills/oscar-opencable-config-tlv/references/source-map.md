# OpenCable source map

Baseline: **OC-SP-HOST2.1-CFR-I17-130418**, April 18, 2013, supplied by the user.
[Full text](sources/OC-SP-HOST2.1-CFR-I17-130418.txt) retains original notices;
[manifest](source-manifest.json) records source hashes, sizes and extraction.
No external download URL was verified.

Extraction used `pdftotext -layout -enc UTF-8 INPUT.pdf OUTPUT.txt`.
All 209 nonempty page segments are present, with no Unicode replacement characters.
For section 15, one-based PDF page = printed page + 10. PDF pages 184 and 191
(printed 174 and 181) were visually compared against their extracted tables.
Form feeds preserve page boundaries. The PDF was removed after verification per
repository policy; reacquire this exact issue for further visual clarification.

## Configuration navigation

| Topic | Section | Printed pages | Full paths |
| --- | --- | --- | --- |
| eCM proxy and provisioning timing | 15.1.1 | 171–172 | 217 |
| Two-way DSG applicability and errors | 15.2 | 172 | 217 children |
| SNMP coexistence | 15.2.1 | 172–174 | 217.53 and children |
| SNMP access view | 15.2.2 | 174–176 | 217.54 and children |
| SNMP MIB object / BER VarBind | 15.2.3 | 176 | 217.11 |
| Vendor ID and vendor container | 15.2.4–5 | 176–177 | 217.43.8 beneath 217.43 |
| WAN-facing IP mode | 15.2.6 | 177–178 | 217.1 |
| Notification receivers | 15.2.7 | 178–180 | 217.38.1–8 |
| SEB TLS cipher bitmap | 15.2.8 | 180–181 | 217.40 |
| Home network prefix validation | 15.2.9 | 181–182 | 217.39.1–7 |
| Event codes, not configuration TLVs | Annex A | Search Annex A | Diagnostic reference only |

Section 14.4 defines SNMP coexistence/access-view tables; section 14.5 supplies
notification table behavior. Section 2 identifies delegated documents and editions.

## Rules that differ from eRouter

- 217.1 is one byte: **0 IPv4, 1 IPv6, 2 dual-stack**. Absence follows the eCM's
  acquired address types. Duplicate instances retain the first and log an error.
  These are not the eRouter initialization enum or default.
- 217.53 requires community and transport access. Transport access may repeat;
  duplicate other children retain the first and log errors. Transport lengths are
  exactly 6 or 18 bytes, not the entire inclusive range 6–18.
- 217.43 requires exactly one vendor ID, first in the container. Section 15.2.4
  says a standalone vendor ID has no meaning and is ignored. FFFFFF stays reserved.
- 217.38 permits up to ten unique receivers; check its IPv4/IPv6 and TrapType
  dependencies. Device duplicate handling does not mean editor deduplication.
- 217.40 disallows multiple instances: device rejects all duplicates and uses
  default E0. This is different from the first-instance rule for 217.1.
- Repeated 217.39 entries define an ordered prefix search by Instance Number,
  stopping at the first match. An entry cannot mix IPv4 and IPv6 content. Distinguish
  this device lookup order from preserving the original serialized order.

## Ambiguities and delegated requirements

15.2.1.4 prints length `1.32` (verified visually), an apparent range typo.
15.2.2.2 prints OID 1.3.6 as `06 03 01 03 06`, inconsistent with conventional BER
arc packing; resolve against authoritative ASN.1 material/errata before choosing
wire bytes. 15.2.2 refers to duplicate children in a “TLV 54.4”; inspect the
context and seek clarification before generalizing that wording.

Do not modernize the historical cipher bitmap in 15.2.8 or treat it as a current
security recommendation. A future encoder must describe this issue's bits exactly.

External dependencies include eDOCSIS (encapsulation/eSAFE MIB), RFIv2.0 and
MULPIv3.0 (referenced DOCSIS behavior), HOST-MIB, RFC 1157 (VarBind), the referenced
SNMP framework RFCs, and RFC 5246 (TLS). Their full requirements are not bundled
here. Do not substitute a newer edition or infer standalone eMTA support.
