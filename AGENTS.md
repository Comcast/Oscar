# AGENTS

This repo is Java-first (JDK 21) with Maven as the primary build/test tool.
Use these guidelines when working in this codebase.

## Environment
- Java: `21`
- Build tool: `mvn` (primary), `build.xml` is legacy
- Entry point: `com.comcast.oscar.cli.Main`

## Required Workflow
- After every change: compile and run tests.
  - Compile: `mvn -q -DskipTests package`
  - Tests: `mvn -q test`
- Keep code hygienic: no unused variables/imports, proper indentation, no dead code.
- Prefer Log4j2 (`logger.debug/info/warn/error`) over `System.out.println`.
- Guard expensive debug work with `logger.isDebugEnabled()`.

## Commands
- Build: `mvn -q -DskipTests package`
- Test: `mvn -q test`
- Clean: `mvn -q clean`
- Verify (compile + test): `./scripts/verify.sh`

## Tests
- Use JUnit 5 (Jupiter).
- Place tests under `src/test/java` following package structure.
- Name tests `*Test.java` and keep them deterministic.

## Repo Notes
- `src/` is the main source directory.
- `res/` contains runtime resources.
- `doc/` is generated documentation; avoid manual edits.
