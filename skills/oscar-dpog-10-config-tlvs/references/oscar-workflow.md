# OSCAR implementation and editor follow-up

## Current entry points (reinspect before editing)

- `src/com/comcast/oscar/configurationfile/ConfigurationFileTypeConstants.java`:
  `DPOG_10_CONFIGURATION_TYPE` identifies DPoG. Reuse the constant.
- `src/com/comcast/oscar/cli/commands/Specification.java`: DPoG CLI routing.
- `src/com/comcast/oscar/configurationfile/ConfigurationFile.java`:
  `stripFinalize` checks a compiler superclass name and strips CM/CMTS MIC,
  end marker, and padding. Trace actual DPoG callers before drawing conclusions;
  the comment mentions both DPoE and DPoG but is not conformance evidence.
- `res/tlv-registry/` and `src/com/comcast/oscar/dictionary/`: inspect current
  catalogs, family routing, nested identifiers, lengths, and lookup behavior.
- `docs/wiki/DPoE-&-DPoG.md`: currently documents DPoE-oriented construction
  and the absence of enforced DPoG conformance checks.

Do not duplicate protocol registries in OSCAR-UI. Establish upstream API and
catalog ownership first. For a suspected upstream defect encountered from the
UI, present evidence and impact before choosing an upstream fix or workaround.

## Catalog baseline

`res/tlv-registry/dpog.json` is now a separate provisional lookup catalog.
See [applicability and gaps](../../../docs/dpog-catalog.md) and
[implementation checklist](../../../docs/wiki/DPoE-&-DPoG.md).
`TlvDictionary` routes DPoG configuration-type lookup to this catalog.
Wire metadata remains inherited and unverified; CLI/compiler/editor routing
and encoding verification are pending. Do not infer conformance from lookup.

## Revisit the DPoG editor (pending)

Skill preparation does not authorize a registry/compiler/editor rewrite.
When that follow-up starts:

- Trace DPoG selection end to end: family metadata, parser/compiler, binary
  serialization, tree projection, Add/Edit choices, and Save/reopen.
- Build a source-cited applicability matrix from Table 7 and C.3–C.10. Exclude
  DHCP-only capabilities from configuration editing, while distinguishing
  unsupported standard settings from unknown/vendor bytes that must survive
  lossless handling.
- Obtain the referenced DOCSIS 3.0 definitions needed for exact encodings and
  complete-file requirements; do not copy the 3.1/4.0 catalog wholesale.
- Verify SNMP fields/types, nested classifier/service-flow constraints, units,
  repetitions, and container boundaries against actual referenced definitions.
- Decide integrity/marker/padding behavior from source and requested output
  mode, then test it separately from applicability. Preserve TLV order.
- Use synthetic vectors for context filtering, malformed/truncated input,
  semantic and byte-preserving round trips, and edits refreshing both panels.
  No customer secrets or production fixtures belong in committed tests.
- Update user docs, architecture, and checklist when editor behavior changes.
  Report the exact supported subset, not blanket DPoG compliance.
