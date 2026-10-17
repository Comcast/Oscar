# SNMP OID Map

[DottedTextualNetSNMPMap.json](DottedTextualNetSNMPMap.json) caches numeric OIDs
and symbolic names for Oscar's bidirectional SNMP lookups. Use the
[update script](../tools/update-snmp-map.sh) to refresh names or generate
additional DOCSIS definitions instead of editing thousands of entries manually.

The source map is bundled in both release JARs. For `java -jar`, Oscar uses a
writable `res/DottedTextualNetSNMPMap.json` beside the JAR and initializes a
missing or empty cache from the bundled map. Existing nonempty caches are
preserved; runtime translations may add entries. SQLite is not used, and no
`db/` directory is created. Preserve any custom mappings from an older
installation by copying its map into the new `res/` location before use.

The maintenance script updates the source map in this repository. Rebuild the
JARs after refreshing it to include the updated map in release artifacts.

## Prerequisites

Run the commands below from the Oscar repository root, not from `res/`.
Python 3 and Net-SNMP's `snmptranslate` must be available:

```bash
command -v python3
command -v snmptranslate
snmptranslate -V
```

On Ubuntu, install missing prerequisites with:

```bash
sudo apt-get update
sudo apt-get install python3 snmp
```

`--update-mibs` also requires internet access to CableLabs and the allowlisted
dependency sources. Preview downloads are temporary and do not update saved
MIBs or the JSON map.

## Preview Updates

Refresh names for existing cache entries using the current upstream MIBs:

```bash
bash tools/update-snmp-map.sh --update-mibs
```

To also enumerate and add named DOCSIS definitions using `snmptranslate -Tz`:

```bash
bash tools/update-snmp-map.sh --update-mibs --generate
```

Review the changed, added and unresolved counts before applying anything.
Without `--write`, neither command changes the map or saved MIBs. Omit
`--update-mibs` to use the existing local MIB set; missing imports may require
an initial upstream refresh.

## Apply Updates

To refresh existing names only:

```bash
bash tools/update-snmp-map.sh --update-mibs --write
```

To refresh names and add all discovered DOCSIS definitions:

```bash
bash tools/update-snmp-map.sh --update-mibs --generate --write
```

With `--update-mibs --write`, downloaded MIBs are saved under `mibs/docsis/`
alongside a manifest recording source URLs, SHA-256 hashes and module roles.
The JSON file is replaced atomically when its contents change. The full set of
MIB, manifest and map writes is not a single transaction.

## Review and Verify

```bash
git diff -- res/DottedTextualNetSNMPMap.json
git status --short
python3 -m json.tool res/DottedTextualNetSNMPMap.json > /dev/null
python3 -m unittest discover -s tools/maintenance -p 'test_update_snmp_map.py' -v
tools/run-all.sh
```

Review newly downloaded MIB files and their manifest as well as the map diff.
Do not commit generated Python `__pycache__` directories.

## Mapping Rules

- Both numeric-key and symbolic-key orientations are supported by Oscar.
  Existing orientations are preserved; new generated entries use symbolic
  keys and dotted numeric values. A reverse entry is not required.
- Preserve numeric spelling, including leading dots: Oscar's cache lookup uses
  exact strings rather than normalizing every input.
- Generated definitions must belong to the selected `DOCS-`, `CCAP-` or `DSG-`
  modules. Imported support modules are loaded but are not generation targets.
- Current DOCSIS MIBs come from the
  [CableLabs DOCSIS directory](https://mibs.cablelabs.com/MIBs/DOCSIS/).
  Selected dependencies use pinned PyPNM and LibreNMS files, plus the current
  CableLabs common definition MIB; see the
  [implementation](../tools/maintenance/update-snmp-map.py) for exact URLs.
- Existing instance OIDs and out-of-scope entries are preserved. An ARRIS
  enterprise OID, for example, needs its vendor MIB to obtain a full name and
  is not resolved by this DOCSIS-only generator.
- Zero unresolved entries does not mean every vendor OID is fully named:
  skipped out-of-scope entries are not included in that count.
- Ambiguous generated labels use `MODULE::name` keys. Round-trip mismatches,
  conflicting mappings and partial MIB-load failures must be investigated,
  not bypassed by deleting the cache or suppressing loader errors.

See the [development checkpoint](../development/snmp-map-refresh/README.md)
for implementation details, verification results and remaining work.


## TLV registry names

Registry `name` values are Oscar text identifiers and must not contain whitespace;
descriptions may use normal prose. The loader rejects whitespace in names.

The legacy DOCSIS and DPoE classifier-error children at `22.8.1`, `22.8.2`,
and `22.8.3` use `ErroredParameter`, `ErrorCode`, and `ErrorMessage`.
Older generated templates using spaced spellings must update those names.
These are symbol spelling corrections, not changes to numeric paths, codecs,
or protocol eligibility. DOCSIS 4.0 and PacketCable names were already single
tokens. Consumers such as oscar-ui need an Oscar artifact containing this fix.
