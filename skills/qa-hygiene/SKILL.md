---
name: qa-hygiene
description: Check Oscar TLV registry and parser hygiene when changing catalog names, registry validation, or related tests, or when asked for an Oscar QA hygiene review.
---

# Oscar QA hygiene

Run commands from the Oscar repository root. Inspect the affected catalog,
loader, parser, and existing tests before adding checks. Keep this review scoped
to the requested changes.

## TLV identifiers

- In `res/tlv-registry/*.json`, `name` is a parser identifier, not a display
  label. Names must be nonempty single tokens with no whitespace, including
  spaces, tabs, or newlines. Keep readable prose in `description`.
- Follow existing identifiers such as `ErroredParameter`, `ErrorCode`, and
  `ErrorMessage`. Do not silently strip whitespace while loading: reject bad
  catalog input so the source can be fixed.
- Check all four catalogs: `docsis`, `docsis40`, `dpoe`, and `packetcable`,
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
