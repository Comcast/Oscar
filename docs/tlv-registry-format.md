# TLV registry format

All catalogs under `res/tlv-registry/` follow
[tlv-registry.schema.json](tlv-registry.schema.json). This includes DOCSIS,
DOCSIS 4.0, DPoE, DPoG, PacketCable, eRouter and OpenCable. There is one TLV
record format, matching Java `TlvDefinition`; do not introduce a second codec
or length vocabulary for embedded devices.

## Catalog header

Required fields: `formatVersion` (1), `family`, `edition`, `sourceSha256`, and
`definitions`. The hash identifies the source artifact documented by the catalog's
specification references. Bundled specification TXT files are marked `-text`
in `.gitattributes`: Git must preserve their original bytes on every platform,
including Windows, so provenance hashes do not depend on checkout line endings.
Embedded catalogs additionally declare `parentPath`.
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
The example is expanded for readability. Keep definitions in ascending numeric
path order, with each parent immediately before its descendants: `4`, `4.1`,
`4.2`, `4.10`, then `5`. Apply the same rule to relative paths in embedded
catalogs. Do not append new children at the bottom of the catalog.

Catalog layout is not a wire-order requirement. DOCSIS 3.1/4.0 text import
preserves child statement order; DOCSIS 4.0 still normalizes the general
extension VendorId to the first child as required by its encoding. Duplicate
child rejection for DOCSIS 4.0 remains enforced.

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

## SNMP statement names

For complete VarBind fields, OSCAR uses `Snmp11` with a one-byte TLV length
field and `Snmp64` with a two-byte length field. These are grammar identifiers;
retain specification terminology in the description. This rule does not apply
to bare object identifiers such as access-view subtrees. See the
[architecture contract](architecture.md#snmp-varbind-naming-and-editing-contract).

DPoE's existing 217.11 VarBind is now named `Snmp11` instead of `SnmpOID`.
Its numeric path, one-byte length, and BER payload are unchanged. Existing
text using `SnmpOID` must use `Snmp11 OID Type "value";` under `eSTBConfig`.
A blank old placeholder is not a complete SNMP statement. No new grammar
production is needed: the existing Snmp11 production already accepts the
OID, type and value fields. DPoE 1.0 and 2.0 share this catalog; regression
coverage verifies both export routes. This naming correction does not claim
new DPoE provisioning conformance or change the legacy support scope.

## All-TLV template coverage

`AllTlvTemplateCoverageTest` compares the fully composed registry with the
all-TLV text export for every supported configuration type (DOCSIS 1.0–4.0,
PacketCable 1.0/1.5/2.0, DPoE 1.0/2.0, and DPoG 1.0). It checks containers
and leaves under their own parent hierarchy, so an identical name elsewhere
or in the syntax banner cannot hide a missing entry. Failures list the missing
numeric paths. The registry is the inventory. The only omission exception is
an empty VarBind with codec `oid` and the exact name `Snmp11` or `Snmp64`,
because its statement requires an OID, type and value. Bare OID fields and
other SNMP-related settings are still required. If a VarBind is emitted, its
parent location is still checked. Populated VarBinds remain covered by the
separate byte-for-byte round-trip tests.

This is a template-completeness check, separate from byte-for-byte round trips
and from configuration validity. Empty template values are not deployment values.
Run it directly with `mvn -B -Dtest=AllTlvTemplateCoverageTest test`, or as part
of `tools/run-all.sh`.
