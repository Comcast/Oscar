# Development Guide

## Prerequisites
- JDK 21
- Maven

Install local dependencies:

```bash
./install.sh
```

Install development prerequisites (includes `python3`):

```bash
./install.sh --development
```

Install dependencies and build a local `oscar.jar`:

```bash
./install.sh --build-oscar-jar
```

This creates symlinks `oscar.jar` and `latest-build` that point at the shaded jar in `dist/`.
Release automation in CI still produces a real `oscar.jar` file for packaging.

## Build

```bash
mvn -q -DskipTests package
```

## Test

```bash
mvn -q test
```

## Full Validation

Run format, hygiene, compile, and tests:

```bash
tools/run-all.sh
```

## Clean

Remove generated artifacts:

```bash
tools/clean.sh --all
```

## Release

Build release artifacts:

```bash
python3 tools/release/release.py
```

By default this will:
- bump maintenance version (`X.Y.Z` -> `X.Y.(Z+1)`)
- sync version in `pom.xml` and `src/com/comcast/oscar/constants/Constants.java`
- run checks and package artifacts
- commit the version bump (`release: vX.Y.Z`)
- create release tag (`vX.Y.Z`)
- push commit and tag to `origin`

Release guardrails:
- Must run from `main` or `master`
- Working tree must be clean before release starts

Explicit bump options:

```bash
python3 tools/release/release.py --bump major
python3 tools/release/release.py --bump minor
python3 tools/release/release.py --bump maintenance
```

Change git behavior:

```bash
python3 tools/release/release.py --no-push
python3 tools/release/release.py --no-tag
python3 tools/release/release.py --no-commit
```

Dry-run:

```bash
python3 tools/release/release.py --dry-run
```

Show current and next version only:

```bash
python3 tools/release/release.py -v
python3 tools/release/release.py --next-version --bump minor
```

## Output Locations
- Local generated test/output files: `output/`
- Distributable jars from package flow: `dist/`
- Maven build workspace: `target/`
