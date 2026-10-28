---
name: qa-hygiene
description: Check Oscar Java source license headers, descriptive domain types, public API documentation, TLV registry and parser hygiene, and related tests when optimizing code, fixing defects, changing project-owned source or performing a QA hygiene review.
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

## Generated ANTLR sources

Exclude ANTLR-generated Java (`src/com/comcast/oscar/parser/tlv*.java`, marked
`// Generated from ... by ANTLR`) from manual cleanup, SPDX/header rewrites,
public-Javadoc audits and source formatting. Do not treat generated style as a QA
defect. Keep generated sources in compilation and parser/round-trip tests.
Change the grammar and regenerate when parser behavior must change; do not hand
patch generated output. Handwritten parser adapters remain in scope.

## Release version ownership

Keep OSCAR's release version only in the project version of `pom.xml`.
Maven filters `res/oscar-build.properties`; `BuildInfo.version()` reads the
packaged metadata and `Constants.OSCAR_VERSION` is a compatibility alias.
Never add a Java release-version literal or fallback, or update source versions
with release-script regexes. Missing/unfiltered metadata must fail clearly.
Dependency versions and historical examples are separate from the application
version. Check both thin and shaded JAR metadata after packaging, and test release
bumps in temporary repositories without invoking Git publication.

## Audit reporting

State the files reviewed, automated checks run, corrections made and remaining
legacy gaps. Passing formatting/compilation is not proof that every public API
banner or domain type meets this skill. Use descriptive local names, avoid
reassigning input parameters, and keep blank lines between logical steps rather
than directly inside method openings when changing handwritten code.

## Common registry contract

All base and embedded catalogs must use `docs/tlv-registry.schema.json` and
`docs/tlv-registry-format.md`. Keep the JSON Schema aligned with `TlvDefinition`.
Run `EmbeddedServiceCatalogTest` along with the registry checks; it discovers
all catalogs, including eRouter and OpenCable. Do not add an alternative
`kind`/nested-length schema for new services.

## TLV registry ordering

Keep every `res/tlv-registry/*.json` definitions array in ascending numeric
path order. Put each parent immediately before its descendants, recursively:
`4`, `4.1`, `4.1.1`, `4.2`, `4.10`, then `5`. Compare path components as
integers, not strings. Insert new children in their parent group; never append
them at the bottom of a catalog. Apply this to base catalogs and embedded
catalogs' relative paths, including future catalogs discovered in the directory.

For ordering-only changes, verify that sorting changes no definition fields,
paths, references, edition or source hash. Follow
[the registry format](../../docs/tlv-registry-format.md) and run the catalog
checks and `tools/run-all.sh` to catch serializers that depend on catalog order.

Catalog layout does not define wire order. Preserve document-instance order
and explicit protocol requirements, including DOCSIS 4.0 TLV 43's VendorId-first
rule. Fix ordering dependencies in the owning parser/encoder; do not restore an
unsorted catalog, weaken byte-for-byte round-trip tests, or regenerate expected
binary fixtures merely to make a sorting change pass.

## TLV identifiers

- In `res/tlv-registry/*.json`, `name` is a parser identifier, not a display
  label. Names must be nonempty single tokens with no whitespace, including
  spaces, tabs, or newlines. Keep readable prose in `description`.
- Follow existing identifiers such as `ErroredParameter`, `ErrorCode`, and
  `ErrorMessage`. Do not silently strip whitespace while loading: reject bad
  catalog input so the source can be fixed.
- Discover every catalog under `res/tlv-registry/`, including embedded eRouter
  and OpenCable catalogs and all nested children. Do not use a frozen family list.
- Treat renames as text-format compatibility changes. Update affected fixtures
  and documentation; old templates may need renaming or regeneration. Preserve
  numeric paths and wire encodings when only correcting names.

## Existing safeguards

Inspect `src/com/comcast/oscar/dictionary/TlvRegistry.java` for fail-fast checks:
whitespace names, duplicate paths, case-insensitive duplicate sibling names,
missing parents, unknown codecs, and invalid metadata. Keep source catalogs in
the numeric order described above; verify wire behavior independently. Do not
add duplicate tests for existing coverage.

`src/test/java/com/comcast/oscar/dictionary/TlvRegistryTest.java` contains:

- `allCatalogNamesAreSingleTokens`: recursively checks every supported catalog.
- `rejectsMalformedCatalogsBeforeUse`: rejects spaces and tabs in names as well
  as other malformed definitions.
- `parsesClassifierErrorNamesAndPreservesEncodedValues`: verifies corrected
  DOCSIS/DPoE names, expected bytes, and exported-text reimport.

For a new defect, add a regression that fails on the faulty input and verifies
observable parser/loader behavior. For identifier fixes, exercise text import,
export, and preserved binary bytes where the codec supports them.

## Reproduce defects before fixing them

For an identified defect, use this sequence before claiming it is fixed:

1. Add a focused automated regression test that expresses the correct observable
   behavior using synthetic inputs. Keep expected results independent of the
   faulty implementation; for wire formats include known bytes where practical.
2. Run that test against the unfixed code. Confirm it fails for the identified
   defect, not a compilation problem, missing dependency or unrelated failure.
   Record the test command and the relevant failure.
3. Fix the implementation without weakening the expected behavior or deleting
   the failing assertion. Keep the regression test in the repository.
4. Rerun the same test and confirm it passes, then run relevant adjacent tests
   and the required repository checks. Report the before/after results.

If the defect cannot be reproduced in the available environment, state the
limitation explicitly; do not claim verified resolution. If a fix already
exists, demonstrate the regression against the old code in an isolated temporary
checkout when feasible, without reverting or overwriting the user's work.
Do not modify generated ANTLR Java to create or repair a reproduction; test its
public parser behavior and change the grammar when required.

## Benchmark before optimizing

When the user requests optimization of a method or class, measure performance
before editing the implementation. Fewer lines or allocations alone do not
establish a speed improvement.

1. Create a repeatable benchmark for the requested operation with synthetic,
   representative small, typical and large inputs. Establish correctness tests
   first and keep input generation outside the timed region unless it is the
   operation being measured.
2. Run the benchmark against the original implementation and retain the baseline
   results before changing it. Record the source revision and any local changes,
   command, JDK/JVM flags, OS/CPU, input sizes, warm-up, iterations and forks.
3. Optimize, then rerun the same benchmark with the same inputs, measurement
   settings and environment. Consume results to prevent dead-code elimination;
   use warm-up and multiple independent JVM runs. Prefer JMH if available. Keep
   benchmark dependencies out of the shipped runtime artifact.
4. Compare per-scenario timing or throughput with explicit units, sample count
   and variability. Report regressions as well as gains; do not claim a speedup
   when differences are within measurement noise. Measure allocation separately
   if making allocation claims. Do not invent performance thresholds.
5. Retain the benchmark source, commands and before/after results with the
   development documentation, and rerun correctness and round-trip tests.
   Use [the optimization QA checklist](../../docs/optimization-qa.md).

Do not replace correctness assertions with timing assertions. Ordinary JUnit
runs must remain deterministic; performance tests run separately under controlled
conditions. If a reliable baseline cannot be obtained, report the limitation
and do not present the change as a measured performance improvement. If work
has already started, recover the original implementation in an isolated checkout
without reverting the user's files, and label the baseline as reconstructed.

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

## Debug logging

Use Log4j parameterized messages for constants and readily available values:
`logger.debug("Adding {} padding bytes", paddingBytes)`. Do not surround these
calls with `isDebugEnabled()`; Log4j already checks the level and defers formatting.
Pass objects directly instead of eagerly calling their `toString()`.

Use a Log4j message/argument supplier for expensive conversion when captured locals
are effectively final. Preserve a guard for diagnostic-only loops, checked-exception
operations, mutable loop state or other work that cannot safely move into a supplier.
Do not remove a guard if that would make debug-only side effects run unconditionally.
Keep diagnostic output in Log4j rather than guarded `System.out.println` calls.
Never log shared secrets. Verify representative binary round trips with DEBUG both
on and off when changing logging across parser/compiler paths.
