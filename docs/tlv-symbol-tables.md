# TLV symbol tables

Oscar's parser, encoder, decoder, dictionary display, and bulk operations load
TLV definitions from JSON files.

| Catalog | Contents |
| --- | --- |
| `res/tlv-registry/docsis.json` | 676 legacy DOCSIS definitions |
| `res/tlv-registry/packetcable.json` | 13 legacy PacketCable definitions |
| `res/tlv-registry/dpoe.json` | 624 distinct legacy DPoE definitions |
| `res/tlv-registry/docsis40.json` | Source-verified DOCSIS 4.0 I12 definitions; see [coverage](../development/docsis40-implementation.md) |

The first three are the migrated Oscar 2.2.15 catalogs, identified by edition
`oscar-legacy-2.2.15`. They are not a claim of complete DOCSIS 3.1 or 4.0
specification coverage. The separate `docsis40` catalog is pinned to
`CM-SP-MULPIv4.0-I12-261014`. Select it with `-s d4 4.0` and use
`Docsis-4.0 { ... }` text input. Its runtime support is partial, not complete I12
coverage; unknown or unverified names are rejected instead of borrowing legacy
definitions.

Runnable [D3.1 text](../src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.txt)
and [D4.0 text](../src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.txt)
examples, compiled binaries and version-specific commands are documented in
[CLI Examples](wiki/CLI-Examples.md).

## Editing and loading

Edit the appropriate JSON catalog and run:

```bash
mvn test
mvn package
```

By default, Oscar reads the catalogs packaged in its JAR. There is no extracted
working dictionary to synchronize. To load edited repository files at startup
without rebuilding the JAR, specify a directory containing all four catalogs:

```bash
java -Doscar.tlv.directory="$PWD/res/tlv-registry" -jar oscar.jar \
  -c -s d 3 -i input.txt -o output.bin
```

Use a JAR built with this change. The directory option replaces the complete
catalog set; missing or invalid files fail startup rather than falling back to
older packaged definitions. Changes take effect on the next process start.
Relative directory paths resolve from the caller's working directory.

## Definition fields

Each catalog has `formatVersion` (currently 1), `family`, `edition`,
`sourceSha256` (the migration source or pinned specification fingerprint), and `definitions`.
Each definition is one JSON object:

| Field | Meaning |
| --- | --- |
| `path` | Array of numeric types from root to leaf, e.g. `[25,35,1]` |
| `name` | Parser keyword, unique among siblings |
| `codec` | Existing Oscar data-type identifier |
| `lengthMin`, `lengthMax` | Legacy value-length metadata, in octets |
| `lengthBytes` | Number of octets in the TLV length field; distinct from value size |
| `supportedVersion` | Preserved legacy version metadata |
| `maxInstances` | Preserved legacy occurrence metadata; `-1` means unrestricted in the legacy data |
| `description` | Help text |

Parents are derived from numeric paths.
Definition order is retained because the existing codecs serialize child templates
in catalog order. Keep that order when making unrelated edits.
An entry with children is a container. Use the existing codec vocabulary and
keep sibling names unique without regard to case. The loader rejects duplicate
JSON members, duplicate paths/names, missing parents, unknown codecs, invalid
length bounds, missing fields, and unsupported catalog formats.

Definitions are immutable and loaded once. Existing codecs receive fresh JSON
working templates, so values from one configuration cannot modify the shared
table. The legacy catalogs' `maxInstances` and version metadata do not introduce
new runtime occurrence or specification-conformance enforcement in the migration.
The DOCSIS 4.0 compiler additionally validates its catalog's lengths and
multiplicity plus the source-derived constraints described in its coverage notes.

## Catalog compatibility

- DOCSIS `25.35.1` and `25.35.2` retain integer value encoding.
- DOCSIS upstream attribute-mask types are corrected to `43.9.3` and `43.9.4`
  using MULPIv3.1-I21, section C.1.1.18.1.8, printed page 601.
- DPoE CLI selections now use the DPoE decoder catalog instead of DOCSIS.
- The misspelled codec `string bits` becomes `string_bits`.
- DPoE `25.14` is named `MaximumDownstreamLatency` according to its own legacy
  description, removing a collision with `25.13` (`TimeoutForAdmittedQoS`).

The SNMP OID map is separately maintained under
`res/DottedTextualNetSNMPMap.json`; see the [resource guide](../res/README.md).

## Negative-input tests

Run the registry and input-validation suite with:

```bash
mvn -Dtest=TlvRegistryTest test
```

`src/test/java/com/comcast/oscar/dictionary/TlvRegistryTest.java` contains
inline invalid text and binary inputs, so no generated bad files are needed.
It checks unknown names in each catalog family, names used under the wrong
parent, unknown empty TLVs, scalar TLVs used as containers, invalid integer
text, missing delimiters, illegal characters, and trailing input. Syntax errors
include line and column information; nested-name errors identify the parent.
Truncated binary headers and values must fail without changing a `TlvBuilder`.

The suite also imports the existing DOCSIS golden, duplicate-OID, and PacketCable
text examples and rejects an undefined-name mutation of the golden example.
These checks cover registry lookup and input handling, not full specification
conformance or a new policy for unknown binary TLV types.
