# TLV registry format

All catalogs under `res/tlv-registry/` follow
[tlv-registry.schema.json](tlv-registry.schema.json). This includes DOCSIS,
DOCSIS 4.0, DPoE, DPoG, PacketCable, eRouter and OpenCable. There is one TLV
record format, matching Java `TlvDefinition`; do not introduce a second codec
or length vocabulary for embedded devices.

## Catalog header

Required fields: `formatVersion` (1), `family`, `edition`, `sourceSha256`, and
`definitions`. The hash identifies the source artifact documented by the catalog's
specification references. Embedded catalogs additionally declare `parentPath`.
Base parent definitions have an optional `reference` filename, such as
`"path": [202], "reference": "erouter.json"` or
`"path": [217], "reference": "opencable.json"` (alongside the required fields).
All definitions from the referenced catalog are included with relative paths
prefixed by the parent. No `attachments` or `includedPaths` fields are used.
Service catalogs cannot reference other catalogs; this also prevents cycles.
References must stay within the registry directory and match the service's
`parentPath`. The parent supplies the applicable `supportedVersion`.

## Definition fields

Every definition requires these fields. Only `reference` is additionally allowed
on base container definitions; other extra fields are rejected.

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
Only reviewed entries are present in embedded runtime catalogs.
See the compatibility notes in [architecture](architecture.md).

The JSON Schema describes structure. `TlvRegistry` additionally validates codec
names, widths, unique paths and sibling names, parent existence and attachment
boundaries. Its strict deserializer is shared by all catalogs; no service-kind
translation is needed. `EmbeddedServiceCatalogTest` checks every catalog against
the common record fields and strict loader, and checks the schema matches those
fields. Existing byte-vector, round-trip and MIC tests protect behavior.

Run `tools/run-all.sh` after changing a catalog or its schema. Keep the schema,
`TlvDefinition`, loader and tests synchronized when intentionally changing the
format. Registry validation does not replace device-specific semantic validation.

## Entering unsupported embedded-service payloads

Regular DOCSIS exposes ePS (201), eMTAConfig (216), eTEAConfig (219),
eDVAConfig (220), and eSGConfig (221) as opaque hexadecimal payloads.
For example, this synthetic fragment encodes payload bytes `01 02 aa ff`:

```text
Docsis { eMTAConfig 0102aaff; }
```

Supply payload bytes only, without the outer type/length. Hex must contain
complete byte pairs, with a maximum of 254 bytes per instance. Inner service
semantics are not checked. This fragment is not a deployable voice configuration.
See [architecture compatibility notes](architecture.md#compatibility-and-opaque-esafe-payloads)
for removed legacy entries and deferred services.
