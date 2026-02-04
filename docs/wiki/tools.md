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
python3 tools/release/release.py --next-version
python3 tools/release/release.py
```
