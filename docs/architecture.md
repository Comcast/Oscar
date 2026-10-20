# OSCAR architecture

## Embedded-device catalogs

Status: architecture and eRouter source catalog prepared; runtime composition
and migration are pending. Existing family loaders and catalogs are unchanged.
The first service is eRouter. eSTB and eMTA are future candidates, subject to
verification of their own specifications and provisioning formats.

### Ownership and attachment

Base family catalogs own outer embedded-device containers. Service catalogs own
relative child paths. A profile manifest will explicitly select base catalogs
and attach service catalogs to containers for a supported family/version.
For example, an eRouter local path `2.4` attaches beneath DOCSIS `202`, producing
public path `202.2.4`. A service is not a new configuration-file family.

Proposed profile shape (not yet consumed by the loader):

```json
{
  "profile": "docsis31",
  "baseCatalog": "docsis.json",
  "attachments": [
    {"parentPath": [202], "catalog": "erouter.json"}
  ]
}
```

The manifest owns applicability. The service's `attachment` metadata records its
specification-defined placement and must agree with the manifest. Do not infer
support from filenames, directory scanning, or reuse of legacy definitions.
DPoE, DPoG and DOCSIS 4.0 require separate applicability decisions. eMTA standalone
PacketCable provisioning must not be conflated with embedded DOCSIS settings.

### Service catalog contract

[erouter.json](../res/tlv-registry/erouter.json) is a source-backed design catalog,
not the existing `TlvDefinition` runtime schema. Its `catalogType` distinguishes
it from family catalogs; `status: design-not-loaded` prevents any claim of active
support. It uses relative paths, explicit semantic kinds, length constraints,
section citations, values, defaults, repetition rules where established, and
sensitivity markers. Missing multiplicity metadata means unspecified, not one.
The edition and SHA-256 identify the extracted source text.

Variable source length `N`/`n` is distinct from a fixed width. Unresolved integer
widths, the printed access-view range, and the OID encoding conflict are recorded
explicitly. No zero-length placeholder stands for an unresolved scalar encoding.
See the [eRouter source map](../skills/oscar-erouter-config-tlvs/references/source-map.md).

### Loader composition and validation

The future loader must validate the profile and every service before publishing
an immutable registry. Resolve catalog references only within the configured
catalog root; reject traversal, missing files, unsupported schema versions and
recursive includes. This initial design permits attachments to base containers,
not recursive service-to-service imports.

Validate the attachment is a container, expand relative paths, and reject missing
parents, duplicate paths, case-insensitive sibling-name collisions, invalid lengths
and incompatible codecs. No last-definition-wins merging. Reject an activation
that includes unresolved encodings; do not silently drop those entries. A future
explicit reviewed subset must declare omissions rather than claim full coverage.
Translate semantic kinds and constraints to runtime codec metadata only after
wire widths, byte order and validation behavior have been established.

Keep the public dictionary API based on full numeric paths. Migrate existing
202 children out of base catalogs atomically with loader activation so definitions
have a single active owner. Preserve existing parser names where compatible;
report changes such as legacy `202.43.11` versus source-defined `202.11` and provide
explicit compatibility/error behavior. Never silently reinterpret an old path.

### Configuration order, integrity and secrets

Definition lookup order is not configuration instance order. Preserve repeated
containers, children, unknown data and encounter order; B.3 treats successive
202 containers as successive portions of one eRouter configuration. Do not sort
instances during composition, editing or export. Recompute MICs only through the
existing configuration integrity pipeline after actual content changes.

Sensitivity metadata applies to display/logging, not stored bytes. Parent hex
previews must respect sensitive descendants. Synthetic credentials are required
for tests and published examples.

### Delivery checklist

- [x] Extract and verify the eRouter I21 source and provenance.
- [x] Define embedded-service ownership and attachment architecture.
- [x] Build the relative-path eRouter catalog with source constraints.
- [x] Add structural and source-boundary catalog regression coverage.
- [ ] Resolve ambiguous widths, access-view range and OID representation.
- [ ] Implement profile composition and fail-fast validation.
- [ ] Migrate legacy entries atomically and review text compatibility.
- [ ] Verify codecs, parser/export integration and value constraints.
- [ ] Test literal wire bytes, repeated-container order and MIC behavior.
- [ ] Verify in-memory round trips for every existing family after activation.
- [ ] Add user examples and editor documentation for enabled functionality.
- [ ] Review eSTB/eMTA specifications before adding their catalogs.

## Configuration text strings

The ANTLR grammar accepts quoted scalar strings and quoted SNMP values.
`TlvTextString` owns escape decoding, export quoting and quote-aware comment
suppression. This keeps URL punctuation and whitespace out of grammar syntax
while preserving decoded bytes. Generated parser files are regenerated with
ANTLR 4.1; they are not edited manually. Quoted-string JUnit tests cover literal
URL bytes, escapes, nesting, SNMP, malformed input and binary/text round trips.
This parser capability is independent of embedded-service catalog activation.
