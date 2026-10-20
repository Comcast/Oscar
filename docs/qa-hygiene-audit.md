# QA hygiene audit

This audit covers recent datatype conversion, registry lookup and version work,
plus the quality/release scripts. It is not a claim of repository-wide conformance.

## Corrected

- The release version is now owned by `pom.xml`, generated into a resource and
  loaded by `BuildInfo`. Release scripts no longer rewrite Java constants.
- Changed handwritten Java requires one SPDX header before the package declaration.
  Missing headers and duplicate SPDX identifiers now fail the helper.
- ANTLR-generated Java is exempt from manual style/header/Javadoc work and
  formatting; it remains compiled and covered by parser and round-trip tests.
- Public methods in `DataTypeFormatConversion` and `TlvDictionary` now describe
  observable behavior, outputs, mutation and applicable errors. Existing integer
  configuration codes remain an explicit compatibility boundary; magic family
  range endpoints now use named constants.
- Byte-pair encoding uses descriptive locals, does not overwrite the input, and
  writes validated bytes directly. Formatting uses StringBuilder.
- Bit-mask validation no longer returns true for every input. Encoders reject
  malformed or mixed grouping; the grouped formatter rejects nonpositive sizes.
- Quality/release helper tests run in the standard check entry point. Packaged
  runtime metadata is compared with the POM for both JARs.

## Remaining legacy work

- Older classes outside this audit still have incomplete public API documentation
  and legacy primitive configuration codes. Migrate these with their consumers,
  not by changing public signatures without compatibility review.
- The existing compiler invocation passes `-DcompilerArgs`, but the Maven compiler
  configuration does not bind that property. It is a compilation check, not proven
  `-Xlint:all -Werror` enforcement. A separate lint modernization must wire real
  compiler arguments and address the existing compiler warnings;
  do not hide them with broad suppression or report them as already enforced.
- Legacy address helpers can resolve hostnames and have inconsistent failures;
  these contracts are documented rather than silently changed. Empty byte-pair
  formatting retains `()` for compatibility even though the text validator rejects
  that representation. The legacy reversed bit formatter also remains explicit.

Validation results are reported with each change; passing tests do not establish
that every legacy banner, domain type or API contract has been modernized.
