# Sources and coverage

Source hashes and official download URLs are in [source-manifest.json](source-manifest.json).
PDF originals stay locally under `agents/` and are ignored. Extracted text is tracked.

| Source | Authority |
| --- | --- |
| [PROV1.5 C01](sources/PKT-SP-PROV1.5-C01-191120.txt) | Section 9.1, p. 58: file TLVs 11/38/43/64/254; 11–64 length choice; start/end markers. Section 11, pp. 82–84: children 38.1–38.7. |
| [EUE-PROV C01](sources/PKT-SP-EUE-PROV-C01-140314.txt) | Section 6.4.2 Table 7 p. 40: shared types, six ASCII octets for vendor OUI. Section 6.5 p. 41: 38.8 IPv6 address. |
| [eDOCSIS I31](sources/CM-SP-eDOCSIS-I31-220831.txt) | Section 5.2.8.1 pp. 46–47: wrappers 216/220, ordered repeated payloads, delegated inner encodings. |

## Runtime coverage

`packetcable.json` contains the union of the reviewed standalone type inventory.
The catalog hash identifies the PROV1.5 extracted text; the manifest pins the
additional 2.0 and eDOCSIS sources. `TlvRegistry.forPacketCable` returns the selected
view; 1.5 excludes the 2.0-only receiver. The old unversioned `forFamily` view and
1.0 API remain compatibility selections, not new 1.0 conformance claims.

Type 38 is a composite with children 1–7 in 1.5 and 1–8 in 2.0. Repetition is
unlimited by the registry, which does not imply unlimited device resources.
Type 43 is an opaque scalar holding all vendor sub-TLV bytes, not an integer.
The 2.0 payload validator checks sub-option boundaries and the six-character
hexadecimal ASCII representation when sub-option 8 occurs. It does not impose
unverified vendor-child semantics or claim mandatory OUI presence/order.
Type 254 retains both standalone marker values; it is not an eSAFE wrapper.

## Deliberate limits

- Most provisionable settings are MIB OIDs carried inside 11/64, not new registry
  paths. Required MIB objects, row creation, receiver default processing and
  security authentication need their own semantic review; no new whole-file
  conformance validator is claimed here.
- Do not import section 10 capabilities or section 8 DHCP TLVs as file settings.
- DOCSIS 216/220 remain opaque, with no reference to the standalone catalog.
  The reviewed documents do not establish an interchangeable complete-file payload.
- Standalone synthetic regression fragments are not deployment-ready profiles.

## Regression evidence

`PacketCableProfileTest` initially failed all three cases: incorrect registry
codecs, 1.5 leaking IPv6, and loss of opaque vendor bytes during export/import.
A separate negative test showed 2.0 accepted the binary three-byte OUI before
version-specific payload validation. Retain these tests when modifying codecs.

The IPv6 exact-byte test also reproduced child reordering on text assembly;
PacketCable 1.5/2.0 now use independent ordered container instances. Large
Snmp64 payloads are tested using an explicit two-byte-length map.

Hex reuse review: `HexString.isHexString` accepts removable delimiters, so it is
not a strict ASCII-OUI validator. The payload checker delegates compact hex
syntax to JDK `HexFormat.parseHex`; six-byte OUI width and sub-option bounds
remain protocol checks. No new hex normalization helper was introduced.
