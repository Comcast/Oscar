# OSCAR OpenCable implementation workflow

This skill is reference preparation only; it does not activate a new catalog.

## Current entry points and observations

Inspect `res/tlv-registry/docsis.json`, `docsis40.json`, `dpoe.json` and the
`TlvRegistry`, `TlvDefinition`, `TlvDictionary` classes under
`src/com/comcast/oscar/dictionary/`. Trace `TlvAssembler`, `TlvDisassemble`,
`ConfigurationFileImport`, `ConfigurationFileExport` and the text grammar before
assuming a definition alone enables an editor or codec.

Observed 2026-10-20: DOCSIS and DPoE contain legacy `eSTBConfig` at 217 and its
children; D4 has no 217 definitions. The legacy VendorID is at 217.8, whereas
15.2.4–5 places its meaningful use beneath vendor container 217.43. Investigate
compatibility before moving or removing it. Legacy presence is not normative
verification, especially for family applicability, SNMP codecs and length rules.

## Shared catalog design

Follow `docs/architecture.md`: use relative service paths and a base-catalog
`attachments` entry containing `parentPath`, `reference`, and `includedPaths`.
A future OpenCable/eSTB reference attaches beneath 217, not eRouter's 202.
Do not create a separate profile file or copy children across family catalogs.
Activate only a reviewed subset; unresolved entries remain excluded explicitly.
The current shared loader handles only its reviewed semantic kinds; SNMP,
transport-address and other kinds need verified codec integration first.

## Acceptance checklist for subsequent implementation

- Record issue/section/page and complete per-path constraints.
- Audit parent placement, enums, byte lengths and duplicate handling against 15.2.
- Preserve public identifiers where correct; document incompatible corrections.
- Reuse QA hygiene and family-specific integrity tests.
- Check literal expected bytes and both text/binary directions for supported
  families, including 217.53.2 nesting, mixed-family rejection, repeated children,
  truncation, string escaping and instance isolation.
- Test prefix family consistency and notification dependencies separately from
  successful round trips. Do not sort serialized entries to implement device
  lookup semantics.
- Use public lab credentials; keep source provenance in future user examples.

Do not change assembly/disassembly merely to mirror device discard behavior.
Report confirmed OSCAR defects before choosing an upstream or UI integration fix.
