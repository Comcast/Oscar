# DPoE and DPoG

DPoE uses the existing `dpoe.json` catalog. Its
[DPoE 2.0 I14 registry review](../dpoe-registry-review.md) records confirmed
length/codec mismatches and missing structured fields. DPoG 1.0 now has a separate,
provisional `dpog.json`, based on DPoG-SP-MULPIv1.0-C01-160830 Annex C.
See the [path-by-path applicability matrix](../dpog-catalog.md).

The DPoG catalog excludes DHCP-only capabilities and MUST NOT settings.
SHOULD NOT entries remain available as recommendations, and MICs 6, 7 and
43.6 retain MAY support. MUST support does not mean mandatory file presence.
Classifier restrictions are parent-specific: ordinary classifiers 22/23 and
drop classifier 60 do not have identical children.

## Current scope

Developers can use `TlvRegistry.forFamily("dpog")` or
`TlvDictionary.getAllTlvDefinition(DPOG_10_CONFIGURATION_TYPE)` for lookup.
Descriptions carry source references and verification status. Existing codec
lengths, types and repetition limits are inherited from OSCAR's legacy DPoE
catalog; they have not been verified against the delegated DOCSIS 3.0 edition.
This lookup catalog is not a DPoG encoder or conformance validator.

CLI routing, text grammar, compiler/export and OSCAR-UI integration remain
pending. Do not use a DPoE compilation result as proof of DPoG compatibility.
Catalog filtering must not be used to silently discard unsupported or unknown
bytes from an existing file.

## Follow-up checklist

- [x] Separate the DPoG lookup catalog and configuration-type lookup.
- [x] Record Annex C applicability and test parent-specific exclusions.
- [ ] Obtain and verify delegated DOCSIS 3.0 wire definitions and file rules.
- [ ] Resolve absent definitions: 45, 23.13, 23.43.8, 24.43.8 and 43.8.
- [ ] Verify opaque/vendor containers and any unlisted nested encodings.
- [ ] Enforce source-backed value constraints, including QoS set type 0x07.
- [ ] Implement DPoG grammar, CLI, compiler/export and editor routing.
- [ ] Test synthetic byte vectors, malformed input, order and save/reopen.
- [ ] Verify MIC, end-marker and padding handling separately.
