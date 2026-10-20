# OSCAR implementation workflow

The source-backed relative-path catalog is `res/tlv-registry/erouter.json`.
DOCSIS and DOCSIS 4.0 profiles load an explicit reviewed subset. See `docs/architecture.md`
for composition, unresolved encoding gates and the activation checklist.

## Trace points

Start with `res/tlv-registry/docsis.json` and the selected family's registry.
Use `src/com/comcast/oscar/dictionary/{TlvRegistry,TlvDefinition,TlvDictionary}.java`
to trace lookup, then `src/com/comcast/oscar/tlv/{TlvAssembler,TlvDisassemble}.java`
and `src/com/comcast/oscar/configurationfile/ConfigurationFileExport.java`.
Inspect the selected text grammar and parser rather than assuming a registry
entry alone enables text compilation. Consult `skills/qa-hygiene/SKILL.md`
for naming and source hygiene when making implementation changes.

## Initial observations, 2026-10-20

The DOCSIS and DPoE catalogs contain legacy type-202 entries. Their presence
is not proof of family applicability or complete eRouter I21 coverage.
Recheck these observations against the current checkout before implementing:

- TR-369 paths 202.4 and 202.5 (with children) are absent.
- 202.12 and 202.13 are absent.
- `SnmpMibObject` appears at 202.43.11, whereas B.4.9 defines 202.11.
  Investigate consumers and compatibility before moving/removing an entry.
- RA interval 202.10 has lengthMin/lengthMax zero; B.4.11 specifies two bytes.
  Trace how zero metadata is interpreted by the codec before selecting a fix.

Do not extend DPoE/DPoG or DOCSIS 4.0 merely because the legacy DOCSIS catalog
contains these entries. Consult family applicability and deferred-scope rules.
Report upstream defects and the affected APIs before choosing integration fixes.

## Implementation acceptance

For each approved TLV, document a source-derived full-path/codec/length/default/
repeatability matrix. Test literal expected bytes, parser acceptance and failure,
and in-memory binary → text → binary behavior. Include repeated 202 containers,
repeated SNMP VarBinds, nested transport-address containers, boundary lengths,
and encounter order. Verify MIC regeneration at the configuration-file level
rather than hashing an independently reordered child list.

Explicitly resolve the source-map ambiguities before asserting coverage. Avoid
customer configuration files and real credentials in fixtures. Keep user-facing
examples and documentation synchronized only when functionality is implemented.

## Common registry format

Follow `docs/tlv-registry-format.md` and `docs/tlv-registry.schema.json`.
Use the same flat `TlvDefinition` fields as DOCSIS, with relative paths and a
`parentPath` header. Do not add `kind` or a nested `length` object. Preserve
source constraints and deferred definitions in [TLV notes](tlv-notes.md).
