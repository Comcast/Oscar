# TLV registry format

All catalogs under `res/tlv-registry/` follow
[tlv-registry.schema.json](tlv-registry.schema.json). This includes DOCSIS,
DOCSIS 4.0, DPoE, DPoG, PacketCable, eRouter and OpenCable. There is one TLV
record format, matching Java `TlvDefinition`; do not introduce a second codec
or length vocabulary for embedded devices.

## Catalog header

Required fields: `formatVersion` (1), `family`, `edition`, `sourceSha256`, and
`definitions`. The hash identifies the source artifact documented by the catalog's
specification references. Two optional fields control attachment:

- Base catalogs may contain `attachments`, with `parentPath`, `reference`,
  `includedPaths`, and `omissions` for each attachment.
- Embedded catalogs contain `parentPath` and use paths relative to that parent.
  They cannot contain their own attachments. For example, OpenCable local `[39,1]`
  becomes `[217,39,1]`; eRouter `[2,4]` becomes `[202,2,4]`.

`reference` is a local catalog filename, not a URL or JSON Schema reference.
Parent placement is verified against the embedded catalog. Explicit inclusion
lists prevent unreviewed activation. The enclosing family supplies the applicable
`supportedVersion` when composing an embedded definition.

## Definition fields

Every definition requires exactly these fields. Extra fields are rejected.

| Field | Meaning |
| --- | --- |
| `path` | Nonempty array of TLV type octets; full for base catalogs, relative for embedded catalogs |
| `name` | Nonempty parser identifier without whitespace |
| `codec` | Existing OSCAR codec name, from the schema's enumeration |
| `lengthMin`, `lengthMax` | Payload width bounds in octets; minimum must not exceed maximum |
| `lengthBytes` | Number of length-field octets, 1 or 2 |
| `supportedVersion` | Existing family version metadata; inherited from the parent on attachment |
| `maxInstances` | Positive limit or -1 for no registry limit |
| `description` | Meaning, source edition/section and relevant encoding notes |

Containers are identified by their children and have `lengthMin: 0`; there is
no separate `kind: container`. Existing legacy `0/0` variable-width definitions
retain their historical codec behavior. Do not use zero as a placeholder for an
unresolved fixed-width field. Bounds do not express every allowed length: a
transport address is exactly 6 or 18 octets, enforced by its codec.

Example OpenCable definition:

```json
{
  "path": [1],
  "name": "IPModeControl",
  "codec": "integer",
  "lengthMin": 1,
  "lengthMax": 1,
  "lengthBytes": 1,
  "supportedVersion": "3",
  "maxInstances": 1,
  "description": "HOST2.1-CFR I17 15.2.6: 0 IPv4, 1 IPv6, 2 dual-stack."
}
```

Use the existing compact layout: one definition per line, as in `docsis.json`.
The example is expanded for readability.

## Source notes and validation

Detailed defaults, sensitivity, enum constraints, unresolved encodings and
unactivated definitions belong in the specification references:
[eRouter notes](../skills/oscar-erouter-config-tlvs/references/tlv-notes.md) and
[OpenCable notes](../skills/oscar-opencable-config-tlv/references/tlv-notes.md).
Only reviewed, currently selected entries are present in embedded runtime catalogs.
This is a representation change, not an expansion of supported TLVs.

The JSON Schema describes structure. `TlvRegistry` additionally validates codec
names, widths, unique paths and sibling names, parent existence and attachment
boundaries. Its strict deserializer is shared by all catalogs; no service-kind
translation is needed. `EmbeddedServiceCatalogTest` checks every catalog against
the common record fields and strict loader, and checks the schema matches those
fields. Existing byte-vector, round-trip and MIC tests protect behavior.

Run `tools/run-all.sh` after changing a catalog or its schema. Keep the schema,
`TlvDefinition`, loader and tests synchronized when intentionally changing the
format. Registry validation does not replace device-specific semantic validation.
