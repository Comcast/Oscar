# Oscar implementation guide

This is a workflow for evidence-backed implementation, not a substitute TLV catalog. All wire facts must be checked against this package’s pinned source.

## Represent settings without losing wire information

Identify a setting by version/issue and the complete parent-to-child numeric path, not the leaf number alone. Direction, container context, and configuration-file applicability belong in its definition. Keep unknown bytes, duplicate instances, and ordering available during decoding when Oscar supports lossless round trips. A UI’s unknown-field policy is an Oscar design choice; do not label it a specification mandate without evidence.

For each newly implemented field, keep an evidence record in Oscar’s existing schema or adjacent documentation:

- Full document identifier, section, heading, printed page, and source file.
- Numeric path and enclosing container; whether legal in a provisioned file.
- Type/length widths and what the length measures; scalar, bitfield, string, byte string, BER object, or nested data.
- Units, byte order, permitted values/ranges, reserved values, defaults on absence, and string termination rules.
- Presence requirements, multiplicity, order/fragmentation requirements, direction or mode constraints, dependencies, and MIC implications.

Use “not specified” or “not yet verified” where appropriate. Do not fill missing evidence with a plausible default.

## Decode and encode

Consult D.1.1 and the individual TLV before choosing widths or bounds. Do not assume every octet begins a regular type-length-value triplet: inspect C.1.2.1–C.1.2.2 for terminal/padding markers. Bound each nested parser to its parent’s declared value extent; reject truncated headers/values without reading into the next container. Keep byte offsets and full paths in errors.

Count encoded octets, not text characters. Inspect per-setting string termination, BER structure, signedness, scaling, and field-specific length exceptions. D.1.1’s generic value-size wording and individual encodings may differ; identify the applicable specific provision rather than applying a universal 254/255-byte limit. Do not implement a blanket split/concatenate rule for long values: use only the setting’s prescribed fragmentation and preserve fragment order.

For classifiers, service flows, aggregate service flows, and extensions, verify both the container and each provisioned child against C.2 or C.1.1.18 as appropriate. Do not put response identifiers or dynamic-message action fields into a configuration file merely because they share a container family. Verify reference relationships, directions, rate units, and defaults from the relevant sections before validating a requested service profile.

## Complete files and integrity

Read D.1.2 for mandatory file contents and D.1.3, D.1.3.1, and D.2.1 for creation and MIC computation. A list of supported TLVs is not a list of mandatory occurrences. Distinguish required presence from device support and recommended operator settings.

Implement the exact digest input, omissions, ordering, repeated-instance handling, E-MIC bitmap, and selected algorithm for the requested mode. Reusing a generic digest over the entire file is not evidence of correctness. Read the relevant extension children when E-MIC is involved. Recompute affected integrity values after an edit; never expose a secret in diagnostics, UI metadata, or fixtures. If the required secret or verified MIC algorithm is unavailable, deliver an explicitly unsigned draft or a limited edit result and identify what prevents a deployable file.

Preserve existing byte order during a byte-preserving edit unless the request requires a transformation. Do not globally sort TLVs to simplify display; integrity ordering rules and fragment order must be considered separately. Verify marker/padding placement and file size against the applicable source and device support; a specification’s minimum supported file capacity is not a universal maximum.

## Verification and review

Use Oscar’s existing test tools where possible. For the affected settings, choose checks that can detect real errors:

- Known bytes with independently calculated expected values, including the package’s small verification anchors where relevant.
- Truncated values, wrong lengths, reserved enum values, nesting escapes, and missing dependencies as applicable.
- Duplicate/fragmented instances and absence defaults only where the source defines their treatment.
- Semantic round trips plus byte-identical round trips when lossless behavior is intended; two codec functions agreeing does not alone prove conformance.
- Complete-file requiredness and MIC vectors when those parts change; never treat a fragment test as complete-file validation.
- Issue separation: a definition verified in the other DOCSIS version must not silently become accepted here.

Report the changed behavior, exact source citations, and what was actually tested. State whether output is a verified TLV fragment, an unsigned draft, or a complete file whose applicable structural and integrity checks passed. Use “unverified” for unresolved compliance claims.
