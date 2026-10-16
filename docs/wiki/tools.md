# Tools Guide

This repository organizes tooling by area (similar to PyPNM style):

- `tools/quality/`
- `tools/maintenance/`
- `tools/release/`

Top-level wrapper scripts in `tools/` are kept for backward compatibility.

## Layout

- `tools/quality/run-all.sh`
  - Runs full verification flow (format, hygiene, compile, tests)
- `tools/quality/code-hygene-checker.sh`
  - Strict compile with lint + warnings as errors
- `tools/quality/setup-formatter.sh`
  - Downloads `google-java-format.jar` into `tools/`
- `tools/quality/markdown-check.sh`
  - Runs markdown lint checks using `markdownlint-cli2` via `npx`
- `tools/maintenance/clean.sh`
  - Cleans generated artifacts
- `tools/bump-version.sh`
  - Updates Maven, CLI, and documented versions without build or Git actions
- `tools/update-tag.sh`
  - Ensures a validated release tag exists, with optional publishing to origin
- `tools/release/release.py`
  - Release automation (version bump, checks/build, commit/tag/push)

## Backward-Compatible Wrappers

These still work and forward to the new layout:

- `tools/run-all.sh`
- `tools/code-hygene-checker.sh`
- `tools/setup-formatter.sh`
- `tools/clean.sh`
- `tools/markdown-check.sh`

## Common Commands

```bash
tools/run-all.sh
tools/clean.sh --all
tools/markdown-check.sh
tools/bump-version.sh --bump minor --dry-run
python3 tools/release/release.py --next-version
python3 tools/release/release.py
```

## Version Bumps

Preview a bump before applying it:

```bash
tools/bump-version.sh --bump minor --dry-run
tools/bump-version.sh --bump minor
tools/run-all.sh
```

The helper requires Python 3 and defaults to a patch bump. `--bump` accepts
`major`, `minor`, `patch`, or the legacy `maintenance` alias. Use
`--set-version 4.0.0` instead to select an explicit version greater than the
current one. It validates the Maven/CLI versions and reads all target files
before writing. The known README, installation, CLI-example, and implementation
commands are updated without replacing unrelated text or dependency versions.
Dry runs do not modify files. No build, commit, tag, or push is performed;
review and commit the changes before tagging separately.

Run the bump-helper regression tests with:

```bash
python3 -m unittest discover -s tools/release -p 'test_release.py' -v
```

## Release Tags

After verifying and committing the release version, create a missing local tag:

```bash
tools/update-tag.sh --tag v3.1.0
```

The `-t` alias is equivalent. Tags must match `vMAJOR.MINOR.PATCH`, such as
`v3.1.0`; a missing prefix, missing component, suffix, or extra component is
rejected. New tags are annotated and point to the current commit. Existing
tags are left unchanged, even if the current commit has advanced.

Explicitly ensure the tag is also available to GitHub Actions:

```bash
tools/update-tag.sh -t v3.1.0 --push
```

Without `--push`, the script only checks local tags. With `--push`, it checks
`origin` first, fetches a remote-only tag rather than recreating it on the
current commit, and pushes a missing remote tag. Different local and remote
tag objects cause an error; neither is overwritten. Remote-check failures
stop the operation before creating a new local tag. No versions, commits,
branches, or GitHub releases are changed. Both wrappers work from another
directory when invoked by their path.

Run the isolated Git-repository tests with:

```bash
python3 -m unittest discover -s tools/git -p 'test_update_tag.py' -v
```
