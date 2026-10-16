# SNMP Map Refresh

## Checkpoint

Status: in progress, recorded 2026-10-15. Finish and validate the updater before
applying a bulk refresh to [the cached map](../../res/DottedTextualNetSNMPMap.json).
The map has no uncommitted changes at this checkpoint. The two updater scripts
are present but untracked; this note does not stage them or change the map.

## Dependency Loading Fix

Current DOCSIS definitions still come from the
[CableLabs DOCSIS directory](https://mibs.cablelabs.com/MIBs/DOCSIS/).
Only DOCSIS mapping roots are eligible for refresh; imported support modules
are not additional mapping targets. Existing unrelated entries remain unchanged.

Six support modules now use the user-suggested
[PyPNM MIB collection](https://github.com/PyPNMApps/PyPNM/tree/82bbf79fe04a0b5e766dc3eb4d5ca2004008bca9/mibs),
pinned to commit `82bbf79fe04a0b5e766dc3eb4d5ca2004008bca9`:
`ENTITY-SENSOR-MIB`, `IANA-ENTITY-MIB`, `URI-TC-MIB`, `UUID-TC-MIB`,
`SCTE-HMS-MPEG-MIB` and `SCTE-HMS-HEADENDIDENT-TC-MIB`.
Each download must declare the expected module name and stay at its allowlisted
URL. Saved manifests record the actual source URL, content hash and role.

PyPNM's `RFC1271-MIB` imports `RFC1158-MIB`, which is not in that directory.
The updater therefore retains the compatible commit-pinned LibreNMS copy of
`RFC1271-MIB`. Current `CLAB-DEF-MIB` comes from the CableLabs common directory
because the bundled version lacks `CpeInterfaceMaskType` required by current
DOCSIS modules. Missing dependencies fail preflight; loader diagnostics are no
longer truncated.

## Existing Implementation

- [NetSNMP.java](../../src/com/comcast/oscar/netsnmp/NetSNMP.java) loads the map
  into a bidirectional map. Cache misses in `toDottedOID` and `toTextualOID`
  invoke `snmptranslate`; `UpdateJsonDB` saves lookup results to disk.
- [The shell wrapper](../../tools/update-snmp-map.sh) invokes
  [the maintenance script](../../tools/maintenance/update-snmp-map.py).
- The script defaults to preview mode and refreshes existing entries.
  `--generate` additionally enumerates named definitions using `snmptranslate
  -Tz`, following the user's PyPNM updater as a guide.
- It selects DOCSIS, CCAP and DSG modules with imported dependencies in a
  temporary directory. Other families are intended to remain unchanged.
- Candidate name changes require numeric OID round trips. Unresolved entries
  are reported and preserved; duplicate values and conflicting changes are
  rejected. These paths still need automated regression coverage.
- `--update-mibs` downloads current modules from the CableLabs DOCSIS directory.
  With `--write`, downloaded modules and their URL/hash manifest are saved under
  `mibs/docsis/`, and a changed cache is replaced using an atomic file write.
  The separate file replacements are not a transaction across all outputs.

## Full Dictionary Generation

The PyPNM reference enumerates the loaded tree and lets preferred MIBs overwrite
duplicate names. Oscar instead filters each enumerated OID by its owning
DOCSIS module and verifies its numeric round trip. Imported generic or vendor
definitions are not added, so an `IF-MIB` preference pass would be out of scope.
Ambiguous labels from different modules use `MODULE::name` keys; unambiguous
labels stay plain. Existing instance entries and reverse mappings are retained,
and existing numeric OIDs are not duplicated.

Preview all additions against current upstream DOCSIS MIBs:

```bash
bash tools/update-snmp-map.sh --update-mibs --generate
```

After reviewing the preview, explicitly write the expanded map and downloaded
MIBs:

```bash
bash tools/update-snmp-map.sh --update-mibs --generate --write
```

## Resume Commands

Run from the repository root with Python 3 and Net-SNMP installed:

```bash
command -v python3
command -v snmptranslate
python3 -m json.tool res/DottedTextualNetSNMPMap.json > /dev/null
bash tools/update-snmp-map.sh
```

To preview against current upstream modules without saving downloaded files:

```bash
bash tools/update-snmp-map.sh --update-mibs
```

Only after reviewing the preview and completing the safety checks below, apply
the refresh. This writes both downloaded MIBs and validated map changes:

```bash
bash tools/update-snmp-map.sh --update-mibs --write
git diff -- res/DottedTextualNetSNMPMap.json
git status --short
tools/run-all.sh
```

Do not delete the cache to force a refresh: retain vendor, PacketCable and
unresolved entries. Keep temporary reports outside this development folder.

## Completion Checklist

- [x] Capture a baseline preview: 210 mappings, 0 changed, 0 unresolved.
- [ ] Confirm module selection, imported dependencies and loader diagnostics
  using the local MIB set before downloading newer modules.
- [ ] Add offline tests using a mocked `snmptranslate` and local download data.
- [ ] Cover both map orientations, leading dots, instance suffixes, duplicate
  keys/values, name collisions and preservation of unrelated mappings.
- [ ] Cover invalid JSON, empty output, loader errors, process timeouts,
  download failures and interrupted writes without losing the original cache.
- [ ] Verify preview leaves the map and saved MIBs unchanged, and a second
  successful write produces no further changes.
- [ ] Review Java cache-miss handling for failed or empty translations before
  persisting results; verify compatibility with the refreshed map in both
  lookup directions.
- [ ] Decide whether bulk refresh stays an explicit maintenance command or
  gains an opt-in runtime trigger; do not add implicit network downloads.
- [ ] Apply and review the map/MIB updates, record source hashes and unresolved
  entries, document user commands, and run the repository verification gate.

## Verification Boundary

On 2026-10-15, all 17 offline Python regression tests passed and
`bash tools/update-snmp-map.sh --update-mibs` completed successfully with the
PyPNM dependencies: 40 DOCSIS roots, 32 imported dependencies, 35 temporary
downloads and 210 cached mappings, with 0 changes and 0 unresolved entries.
The full `--generate` preview found 5,164 DOCSIS definitions and proposed
5,164 additions, for 5,374 total mappings. Generation tests cover DOCSIS-only
ownership, duplicate labels, numeric round trips, batched output, preserving
instances and reverse mappings, preview preservation and explicit writes to a
temporary fixture. No live `--write` execution was performed; the repository
map and saved MIBs were not changed.
Broader failure-path tests, complete DOCSIS discovery and Windows invocation
remain to be completed.

Run the focused tests from the repository root:

```bash
python3 -m unittest discover -s tools/maintenance -p 'test_update_snmp_map.py' -v
```
