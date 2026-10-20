# Configuration text export

`ConfigurationFileExport` decodes binary configuration entries and renders them
as configuration text. `PrettyPrint` owns final indentation: one tab per nesting
level, LF line endings, and a trailing LF. Nested/repeated entries and quoted
values retain their decoded order and content. Leaf annotations show the full
TLV path, for example `/* TLV: [202.2.2]*/`.

```java
var exporter = new ConfigurationFileExport(builder,
    ConfigurationFileTypeConstants.DOCSIS_31_CONFIGURATION_TYPE);
exporter.setDotTextOIDOutputFormat(ConfigurationFileExport.DOTTED_OID_FORMAT);
String text = exporter.toPrettyPrint(false, false);
```

The first boolean includes valueless reference-template entries when true.
Incomplete SNMP objects remain omitted because they lack an OID/type/value.
The second boolean suppresses TLV comments when true; the license/help banner
remains present. Comment-like text inside quoted values is preserved. This
setting persists for subsequent single-argument `toPrettyPrint` calls.

Pass an explicit family/revision. A binary's first byte cannot identify the
DOCSIS or PacketCable edition. Deprecated automatic detection distinguishes only
DOCSIS from PacketCable; it retains `PacketCable-X.X` for an unknown edition.
The deprecated integer formatting overload retains its historical raw output,
including internal block markers; use the boolean overloads for normal output.

`writeToDisk(file)` writes the formatted found-TLV output as UTF-8. It replaces
an existing file and returns false on an I/O failure. Rendering completes before
the file is opened; malformed decoded entries throw rather than producing
partial text. This is not an atomic-write guarantee if an I/O failure occurs
after opening the destination. Empty binary inputs and unsupported families
produce `IllegalArgumentException`; unreadable files produce
`UncheckedIOException`. Reference templates remain available through the
explicit-type constructor.

`getTlvDefintion(path)` retains its historical spelling. It accepts decimal
paths such as `25.1`, supports container definitions, and fails promptly for
missing or malformed paths instead of repeatedly searching an absent child.

## Output regression tests

```bash
mvn -B -Dtest=ConfigurationFileExportTextTest test
mvn -B test
```

Fixed synthetic fixtures under `src/test/resources/export-text/` were captured
before the refactor. Tests compare the entire body character for character;
only the versioned help/license banner is excluded, with its presence checked
separately. Do not regenerate expected output merely to make a failing test pass.

Coverage includes nested and repeated eRouter entries, SNMP 11/64, quote-safe
comment suppression, certificate text, full leaf paths, all family template
headers, eight-level indentation, constructor consistency, UTF-8 file bytes,
invalid lookups, malformed decoded entries and failed writes. Existing API and
CVC round-trip tests independently check binary preservation, including
fragmented certificates. See [API round trips](api-roundtrip-tests.md).
