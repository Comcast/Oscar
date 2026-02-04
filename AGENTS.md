# AGENTS

This repo is Java-first (JDK 21) with Maven as the primary build/test tool.
Use these guidelines when working in this codebase.

## Environment
- Java: `21`
- Build tool: `mvn` (primary), `build.xml` is legacy
- Entry point: `com.comcast.oscar.cli.Main`

## Required Workflow
- After every change: run the single verification script.
  - `tools/run-all.sh`
- Keep code hygienic: no unused variables/imports, proper indentation, no dead code.
- Prefer Log4j2 (`logger.debug/info/warn/error`) over `System.out.println`.
- Guard expensive debug work with `logger.isDebugEnabled()`.

## Scripts
- `tools/run-all.sh`: format, hygiene checks, compile, and tests.
- `tools/clean.sh`: remove generated artifacts (`--all`, `--output`, `--dist`, `--target`, `--jar`).
- `tools/release.py`: release helper to run checks, package jars, and refresh `oscar.jar` + `latest-build`.
- `scripts/ci-verify.sh`: invoked by `tools/run-all.sh`.
- `tools/code-hygene-checker.sh`: strict compile with `-Xlint:all -Werror`.
- `tools/setup-formatter.sh`: fetches `google-java-format`.
- `scripts/verify.sh`: compile + test.

## Tests
- Use JUnit 5 (Jupiter).
- Place tests under `src/test/java` following package structure.
- Name tests `*Test.java` and keep them deterministic.

## Repo Notes
- `src/` is the main source directory.
- `res/` contains runtime resources.
- `doc/` is generated documentation; avoid manual edits.
- `output/` is the standard location for generated local test/output files.
