---
name: qa-hygiene
description: Check Oscar Java source license headers, descriptive domain types, public API documentation, TLV registry and parser hygiene, and related tests when changing project-owned source or performing a QA hygiene review.
---

# Oscar QA hygiene

Run commands from the Oscar repository root. Inspect the affected catalog,
loader, parser, and existing tests before adding checks. Keep this review scoped
to the requested changes.

## Java license headers

For project-owned Java source, use one short header before the package declaration:

```java
/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
```

The years above are an example, not a value to copy blindly. Check Git history
with `git log --follow --format=%ad --date=format:%Y -- path/to/File.java` and
existing notices for the earliest supported year; checkout timestamps are not
creation dates. Retain the first year and extend the end year when updating a
file. Use a single year for new files and spaces around the range separator.

This rule applies across the OSCAR codebase. Whenever a project-owned source
file is touched for any change, update its license banner in the same change:
add the short SPDX header if missing, update the copyright year range, and
remove the redundant legacy banner. Use the language-appropriate comment syntax
for non-Java source files.

Replace redundant long Apache license
Javadocs (`@bannerLicense`, HTML breaks, and their author-only banner) with this
header. Do not retain both license forms. Keep useful class and function
Javadocs, including input/output examples. The full license remains in root
`LICENSE`, and root `NOTICE` must be retained.

Preserve third-party copyright, attribution, and license notices; do not relabel
third-party code as Comcast-owned. Do not hand-edit generated ANTLR Java merely
to change headers. This convention applies going forward to new or touched files;
it does not require an unrelated repository-wide rewrite.

Validate changed headers with `tools/code-hygene-checker.sh --copyright-only`.
For header-only edits, verify executable code is unchanged and run diff checks;
run the Java checks below when behavior changes.

## Common registry contract

All base and embedded catalogs must use `docs/tlv-registry.schema.json` and
`docs/tlv-registry-format.md`. Keep the JSON Schema aligned with `TlvDefinition`.
Run `EmbeddedServiceCatalogTest` along with the registry checks; it discovers
all catalogs, including eRouter and OpenCable. Do not add an alternative
`kind`/nested-length schema for new services.

## TLV identifiers

- In `res/tlv-registry/*.json`, `name` is a parser identifier, not a display
  label. Names must be nonempty single tokens with no whitespace, including
  spaces, tabs, or newlines. Keep readable prose in `description`.
- Follow existing identifiers such as `ErroredParameter`, `ErrorCode`, and
  `ErrorMessage`. Do not silently strip whitespace while loading: reject bad
  catalog input so the source can be fixed.
- Check all five catalogs: `docsis`, `docsis40`, `dpoe`, `dpog`, and `packetcable`,
  including nested children. Extend the catalog-wide check when adding a family.
- Treat renames as text-format compatibility changes. Update affected fixtures
  and documentation; old templates may need renaming or regeneration. Preserve
  numeric paths and wire encodings when only correcting names.

## Existing safeguards

Inspect `src/com/comcast/oscar/dictionary/TlvRegistry.java` for fail-fast checks:
whitespace names, duplicate paths, case-insensitive duplicate sibling names,
missing parents, unknown codecs, and invalid metadata. Preserve catalog order;
legacy serializers rely on it. Do not add duplicate tests for existing coverage.

`src/test/java/com/comcast/oscar/dictionary/TlvRegistryTest.java` contains:

- `allCatalogNamesAreSingleTokens`: recursively checks every supported catalog.
- `rejectsMalformedCatalogsBeforeUse`: rejects spaces and tabs in names as well
  as other malformed definitions.
- `parsesClassifierErrorNamesAndPreservesEncodedValues`: verifies corrected
  DOCSIS/DPoE names, expected bytes, and exported-text reimport.

For a new defect, add a regression that fails on the faulty input and verifies
observable parser/loader behavior. For identifier fixes, exercise text import,
export, and preserved binary bytes where the codec supports them.

## Validation

Run the focused registry checks:

```bash
mvn -B -Dtest=TlvRegistryTest test
```

For Java or catalog changes, also run `mvn -B test`. Use `tools/run-all.sh` for
broader repository quality checks when relevant; inspect its options before use.
Report commands and results, distinguishing pre-existing checks from new tests.

These naming constraints are Oscar parser requirements, not DOCSIS normative
rules. For changes to TLV meanings or encodings, follow the version-specific
configuration-file skills linked in `AGENTS.md` and their pinned sources.

## User-facing Git commands

Start every copy/paste command block for Git activity with an executable `cd`
to the actual target repository's absolute path. This includes save/commit,
push, tag, release, and direct Git commands. A repository comment alone is not
enough. Verify the target checkout and its helper interface; do not assume the
current shell is already in the correct project. Use `cd /absolute/repo &&`
as the first line so a failed directory change prevents the following command.
Provide push/release commands for the user to execute unless explicitly asked
to execute them.

## Shared validation modules

OSCAR is an object-oriented, modular library consumed by OSCAR-UI and the CLI.
Put reusable validation rules in a focused public class with documented inputs,
results/errors and version scope. Compilers, CLI and UI adapters must call that
owner rather than copy rules or require a CLI subprocess. Keep parsing, validation,
MIC generation and presentation separate; avoid dependencies on GUI state.

For complete-file rules, test both text and binary entry points, incomplete drafts,
negative examples and valid shipped profiles. Apply requiredness after assembly,
not while an editor is adding individual TLVs. Report exactly what a successful
check establishes; minimum presence is not full specification conformance.

## Descriptive domain types

Limit primitive obsession in public APIs. Represent a domain choice with an enum
or a focused value type when its meaning or valid values are not evident from the
primitive type. For example, prefer a `DocsisVersion docsisVersion` parameter over
`int version`: the latter could mean an OSCAR release, DOCSIS revision or internal
configuration-family code. A clearer parameter name alone does not make an integer
type-safe. Reuse an existing domain type before introducing another representation.

- Use enums for closed choices such as DOCSIS revision or input format, and small
  immutable value types where units or invariants need enforcement.
- Keep primitives for actual numeric quantities, offsets, lengths and wire bytes
  when their meaning is clear. Do not wrap every counter or byte unnecessarily.
- Convert legacy integer constants at a documented compatibility boundary; reject
  unsupported values there. Keep wire IDs separate from enum ordinals.
- Preserve established public APIs when migrating consumers; do not silently
  change binary encodings or remove methods used by OSCAR-UI.
- Review newly added or changed signatures for ambiguous `int`, `String` and
  boolean parameters. Do not expand a focused task into a whole-codebase migration.

## Public method banners

Every new or changed public method and constructor needs descriptive Javadoc.
Explain the caller-visible contract, rather than merely repeating the method name
or leaving empty `@param` tags. For each applicable part, document:

- Purpose and when to use the operation, including supported families/versions.
- Each parameter's domain meaning, units, allowed values and null handling.
- Return value or, for `void`, the successful outcome and observable side effects.
- Exceptions and the conditions that cause them.
- Whether input is mutated, files are written, or compiler state is changed.
- A short example with expected output/result for validation, parsing, formatting
  or other non-obvious behavior; use synthetic values, never real secrets.

For validation, explicitly distinguish minimum presence, syntax, full conformance
and MIC authentication. For an override, inherit documentation only when it fully
states the contract; document changed behavior locally. QA must check that banners
match implementation and examples still use the actual API.
