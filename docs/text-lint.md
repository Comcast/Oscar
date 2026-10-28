# Text formatting lint

OSCAR can check text without compiling it, resolving MIBs or writing output files.
It collects recoverable syntax errors and validates all SNMP value types supported
by the grammar. A clean lint report does **not** establish registry validity,
minimum required TLVs, MIB-specific constraints, encoded lengths, MIC validity or
device acceptance. Existing compilation entry points retain their behavior.

## CLI

With a downloaded standalone JAR (replace the JAR filename):

```bash
java -jar oscar-all.jar --lint-text configuration.txt
java -jar oscar-all.jar --lint-text configuration.txt --lint-json
```

These options are standalone; do not combine them with compilation options.
Input must be UTF-8. Exit codes are 0 for clean formatting, 1 for findings or an
incomplete scan, and 2 for usage or file-read failures. Unexpected Java failures
are not caught and disguised as input errors.

Text output contains `file:line:column`, category and an explanation. JSON
contains `schemaVersion`, `source`, `scope`, `fullyScanned`, `hasErrors`,
`diagnostics` and leaf `statements`. Diagnostic messages omit raw values;
filenames and lexical TLV names remain visible. Do not assume a diagnostic report
is fully anonymized.

For example, this input reports two `SNMP_VALUE` errors and still checks
`NetworkAccess`:

```text
Docsis {
    Snmp11 1.3.6.1.2.1.1.7.0 Integer32 "abc";
    Snmp11 1.3.6.1.2.1.1.7.0 IpAddress "999.2.3.4";
    NetworkAccess 1;
}
```

## Java API and OSCAR-UI integration

```java
TextLintReport report = TextLinter.lint(editorText);
for (TextLintReport.Diagnostic diagnostic : report.diagnostics()) {
    // Highlight diagnostic.range() in the unchanged editor buffer.
    // Display diagnostic.code() and diagnostic.message().
}
```

Import these types from `com.comcast.oscar.validation`. An overload accepts
`TextLintOptions` for input length, statement length, nesting and diagnostic
limits. Defaults are 1,048,576 UTF-16 units of input, 65,536 per statement,
32 container levels and 256 diagnostics. Limits produce a `LIMIT` finding and
`fullyScanned=false`; an invalid wrapper also prevents a complete scan.

Reports are immutable. Offsets are zero-based UTF-16 units with an exclusive
end; lines and columns are one-based UTF-16 units. CRLF is one line break.
Missing delimiters can be represented by an empty insertion range. `tlvNames`
is lexical ancestry, not a numeric registry path. Positions distinguish repeated
occurrences. `ACCEPTED` means formatting accepted, not compiled or deployable;
`SKIPPED` means the leaf failed a syntax or SNMP value check. Structural errors
may exist outside individual leaf results, so always inspect the entire report.

An editor should run lint on a snapshot and discard results if that snapshot is
no longer current. UI display integration is a separate change; the UI can call
this API without running a CLI subprocess. The API performs no file writes.

## Recovery and scope

ANTLR's generated lexer supplies tokens. The generated parser and its default
error strategy validate each bounded statement; an error listener collects
failures without printing source values. The existing wildcard SNMP OID rule can
consume following valid TLVs before default recovery runs. A small boundary
adapter therefore isolates statements at semicolons/braces and detects missing
semicolons before recognizable subsequent statements. Standalone quote tokens
indicate malformed quoted values; recovery resumes after that line. This uses
the existing grammar rather than a second hand-written lexer or grammar.

Recovery is best effort: ambiguous damage can skip more than one intended TLV.
An unterminated block comment prevents checking the remaining text. A completed
scan does not mean every intended TLV could be reconstructed. No partial binary
is produced, and errors are never silently turned into a successful compile.

SNMP checks cover Integer/Integer32, Counter/Counter32, Gauge/Gauge32, TimeTicks,
IpAddress, OctetString and HexString, for both Snmp11 and Snmp64. Numeric checks
use the encoder's decimal/hex/octal notation and signed/unsigned 32-bit ranges.
The encoder maps `Integer`, `Counter` and `Gauge` to the same BER tags as
`Integer32`, `Counter32` and `Gauge32`; both spellings produce identical bytes.
TLV availability still depends on the configuration family: syntax acceptance
of `Snmp64` does not make it a valid top-level DOCSIS TLV.
IP addresses require four decimal octets without DNS resolution; hexadecimal
values require complete byte pairs. OctetString uses the grammar's quoted-string
and escape rules. OID existence and MIB-specific ranges are outside lint scope.

## Verification evidence

`AntlrRecoveryTest` records the default-parser limitation: an unquoted SNMP
value consumes the subsequent `NetworkAccess` statement as part of the wildcard
OID. The original recovery assertion failed before adding the adapter; the test
now checks both that baseline and the successful lint recovery. An additional
empty-container regression failed before correction. `TextLinterTest` covers
supported wrappers/types, malformed syntax, nested contexts, offsets, limits,
immutability and subsequent valid statements. `LintTextCliTest` checks text/JSON,
exit codes, unchanged input and absence of generated files.

Ready-to-run [example text files](../examples/docsis/3.1/lint/README.md) include
missing quotes, missing semicolons, invalid SNMP values and a registry-name
limitation example, with verified diagnostic locations and exit codes.

## TODO: Digit-map text validation and editing API

- [ ] Add reusable validation for standalone digit-map text and digit maps
  extracted from SNMP OCTET STRING values. Keep the editor interface in OSCAR-UI;
  OSCAR owns language rules, diagnostics and encoding APIs.
- [ ] Add and pin the source specifications in the PacketCable skill before
  implementation: [PKT-SP-RSTF-C01-140314](https://account.cablelabs.com/server/alfresco/ab3282a9-3a2b-4bf9-a512-2b038e141674),
  section 7.1.1, for syntax, matching, timers and actions; and
  [PKT-SP-RST-EUE-PROV-C01-140314](https://account.cablelabs.com/server/alfresco/38c14465-ea4c-49f8-b543-89996a8f262f)
  for digit-map MIB provisioning. Keep RST and legacy NCS/MGCP dialects explicit;
  do not validate every dial plan with one assumed grammar.
- [ ] Evaluate an ANTLR grammar for the selected dialect. Return structured
  diagnostics with line, column, source span, severity and stable diagnostic code.
  Recover where possible to report multiple errors without hiding unexpected Java
  failures. Support repeated calls for UI Validate and debounced live checking.
- [ ] Expose focused APIs for text/file validation and extraction/replacement of
  the selected digit-map VarBind. Do not write files during validation or silently
  modify input. Keep user-controlled companion-file saves separate from applying
  edited contents to the parent configuration.
- [ ] Validate syntax and specification-defined semantic rules such as references,
  timers and actions where confirmed. Distinguish language validity, provisioning
  constraints and device acceptance. Apply the RST `pktcEUERSTDMValue` limit of
  8192 octets to that object/profile, not as a universal limit for every digit-map
  OID; establish encoding and line-ending rules before counting bytes.
- [ ] Retain valid and invalid fixtures covering missing quotes/delimiters,
  malformed patterns, unresolved references, invalid timer/action forms and
  payload boundaries. Test multiple diagnostics, source locations, unchanged
  inputs and exact extraction/edit/re-embedding round trips through TLV 64.
- [ ] Document the public API and CLI usage, supported dialects and validation
  limits; coordinate with the OSCAR-UI Digit Map editor TODO.

Status: deferred; existing digit-map insertion transports content and is not a
claim of dial-plan language validation. No editor or new validator is added here.
