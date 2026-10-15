# TLV symbol tables

Oscar's parser, encoder, decoder, dictionary display, and bulk operations load
TLV definitions from JSON files. SQLite is no longer used by these paths.

| Catalog | Contents |
| --- | --- |
| `res/tlv-registry/docsis.json` | 676 legacy DOCSIS definitions |
| `res/tlv-registry/packetcable.json` | 13 legacy PacketCable definitions |
| `res/tlv-registry/dpoe.json` | 624 distinct legacy DPoE definitions |

These are the migrated Oscar 2.2.15 catalogs, identified by edition
`oscar-legacy-2.2.15`. They are not a claim of complete DOCSIS 3.1 or 4.0
specification coverage. This change does not add DOCSIS 4.0 CLI support.

## Editing and loading

Edit the appropriate JSON catalog and run:

```bash
mvn test
mvn package
```

By default, Oscar reads the catalogs packaged in its JAR. There is no extracted
working dictionary to synchronize. To load edited repository files at startup
without rebuilding the JAR, specify a directory containing all three catalogs:

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
`sourceSha256` (the original migration source fingerprint), and `definitions`.
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

Parents are derived from numeric paths; database row IDs are unnecessary.
Definition order is retained because the existing codecs serialize child templates
in catalog order. Keep that order when making unrelated edits.
An entry with children is a container. Use the existing codec vocabulary and
keep sibling names unique without regard to case. The loader rejects duplicate
JSON members, duplicate paths/names, missing parents, unknown codecs, invalid
length bounds, missing fields, and unsupported catalog formats.

Definitions are immutable and loaded once. Existing codecs receive fresh JSON
working templates, so values from one configuration cannot modify the shared
table. `maxInstances` and version metadata do not introduce new runtime
occurrence or specification-conformance enforcement in this migration.

## Compatibility and migration decisions

- The original packaged `res/dictionary.sqlite`, before its TLV tables were
  removed, was the migration baseline. Its integer representation for DOCSIS
  `25.35.1` and `25.35.2` is retained; the old `db/` copy used byte arrays for
  those fields. Catalog `sourceSha256` values identify that original baseline.
- DOCSIS upstream attribute-mask types are corrected to `43.9.3` and `43.9.4`
  using MULPIv3.1-I21, section C.1.1.18.1.8, printed page 601.
- DPoE CLI selections now use the DPoE decoder catalog instead of DOCSIS.
- The misspelled codec `string bits` becomes `string_bits`.
- Duplicate DPoE paths are consolidated using the lowest original row ID.
  Alternative rows and conflicting metadata are retained in the migration audit.
  This is an explicit compatibility policy, not a DPoE specification correction.
- DPoE `25.14` is named `MaximumDownstreamLatency` according to its own legacy
  description, removing a collision with `25.13` (`TimeoutForAdmittedQoS`).

The complete audit and reproducible initial exporter are under
`development/tlv-registry/`. The exporter is a migration tool: rerunning it
requires the original pre-cleanup database and replaces the catalogs, so do not
use it after making intentional catalog edits.

The `docsis_tlv_definition`, `packet_cable_tlv_definition`, and
`dpoe_tlv_definition` tables and their indexes have been removed from both
`res/dictionary.sqlite` and `db/dictionary.sqlite`. Both files remain valid,
empty SQLite databases; TLV definitions live only in the JSON catalogs.
The JDBC dependency and legacy SQL query classes remain, but their TLV queries
require a separately supplied legacy database. Editing SQLite databases no
longer changes the migrated parser/codec behavior. CLI startup no longer
extracts a SQLite dictionary.
