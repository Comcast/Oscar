# OSCAR implementation and review

Start with [the registry audit](../../../docs/dpoe-registry-review.md), which
records the catalog hash and source-cited findings. Reinspect current files;
the audit is a snapshot, not a promise that later code is unchanged.

## Entry points

- `res/tlv-registry/dpoe.json`: legacy catalog, not an I14 conformance validator.
- `TlvRegistry` and `TlvDefinition` under `src/com/comcast/oscar/dictionary/`:
  immutable symbols, validation, child paths and template generation.
- `TlvDictionary.getAllTlvDefinition`: both DPoE configuration types use `dpoe`.
- `ConfigurationFileTypeConstants`: reuse DPOE_10/20 constants.
- `cli/commands/Specification.java`: DPoE alias and version selection.
- `TlvConfigurationFileParser`, `TlvAssembler`, `TlvDisassemble`:
  inspect field construction, container traversal, string termination and bytes.
- `ConfigurationFile`, `ConfigurationFileExport`, `DPoECompiler`:
  trace end-to-end compilation, output and finalization before changing behavior.
- `src/test/java/com/comcast/oscar/dictionary/TlvRegistryTest.java`:
  existing catalog hygiene and DPoE round-trip checks.

Use source paths relative to `src/com/comcast/oscar/` when locating these classes.
`multi_tlv_byte_array` can expose an opaque payload instead of named children;
missing catalog paths therefore do not by themselves prove raw-wire rejection.

## Review priorities

1. Correct source-confirmed lengths and string termination with expected-byte
   tests, including MESP reference 72.1 and I-TCI [22/23/60].15.3.
2. Separate lookup context from preservation of unsupported/customer bytes.
   Do not remove DHCP or unsupported definitions until their consumers and
   version scope are established.
3. Add missing I14 named fields only with their delegated definitions verified.
   Keep DPoE 1.0 behavior explicit when evolving the shared catalog.
4. Verify semantic constraints: MESP required children and nonzero references,
   terminated names, classifier dependencies/exclusions, QoS set type 0x07,
   SF Collection pairing, and color-field reserved values/repetition.
5. Update user docs and synthetic tests before declaring editor support.
   Keep GUI metadata upstream; do not duplicate registries in OSCAR-UI.

An audit request authorizes review, not an unrelated compiler rewrite. When
implementing an approved correction, test the actual serialized bytes and
reimport, not merely the JSON values. Do not copy DPoG applicability into DPoE.
