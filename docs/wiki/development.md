# Development Guide

[README](../../README.md) · [Run the release JAR](Installation.md)

## Checkout and prerequisites

Install Git, a **Java 21 JDK** (including `javac`), Maven, and Python 3.
Use a JDK matching the host architecture. Windows and macOS Java instructions
are in the [installation guide](Installation.md); install Maven separately
using its [official instructions](https://maven.apache.org/install.html).
The repository installer is for Linux, not Windows or macOS.

```bash
git clone https://github.com/Comcast/Oscar.git
cd Oscar
java -version
javac -version
mvn -version
```

On supported Linux hosts, install development prerequisites with:

```bash
./install.sh --development
```

To install prerequisites and build the executable JAR:

```bash
./install.sh --build-oscar-jar
```

The build creates local `oscar.jar` and `latest-build` symlinks into `dist/`.
CI publishes a regular `oscar.jar` file. The development installer also provides
GitHub CLI; authentication and publication are separate actions.

## Build and test

Run from the checkout root (Maven commands also work in PowerShell):

```bash
mvn package
mvn test
```

The package step runs tests and builds executable and thin JARs. To run the
current executable build on Linux or macOS:

```bash
java -jar target/oscar-3.2.1-all.jar --help
```

In PowerShell use `java -jar .\target\oscar-3.2.1-all.jar --help`.

Full repository validation uses Bash, the formatter, hygiene checks, and Maven:

```bash
tools/run-all.sh
```

Use the repository's Linux development environment for the complete shell
workflow; Maven alone does not run all tooling checks.

## Artifacts and runtime resources

| Artifact | Purpose |
| --- | --- |
| `oscar-all.jar` / `oscar.jar` | Standalone CLI with runtime dependencies |
| `oscar-thin.jar` | Integration library requiring a dependency classpath |

Release assets include individual SHA-256 checksum files. Neither variant
includes examples or test classes. Build a thin-JAR classpath locally with:

```bash
mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/lib
java -cp "target/oscar-3.2.1.jar:target/lib/*" com.comcast.oscar.cli.Main --help
```

Windows classpaths use `;` instead of `:`. Downloaded thin JARs likewise require
their runtime dependencies; they are not standalone replacements for `oscar.jar`.

SQLite is not required for normal build, test, conversion, or template merging.
The retained legacy SQL API requires a caller-provided driver and database.
See [resource maintenance](../../res/README.md) for the bundled SNMP map and
runtime cache; do not maintain a duplicate map in a consuming application.

| Directory | Contents |
| --- | --- |
| `target/` | Maven output |
| `dist/` | Packaged release artifacts |
| `output/` | Generated examples and test output |

`tools/clean.sh --all` removes generated outputs. Use it only when cleanup is
intended, not as a prerequisite for every build.

## Examples and API development

Use [CLI Examples](CLI-Examples.md) for DOCSIS lab profiles and compile/decompile
commands. The public lab key is not a production credential; `-k` takes a file.
Read [MIC verification](../mic-verification.md),
[SNMP value export](../snmp-value-export.md), and
[DOCSIS 4.0 coverage](../../development/docsis40-implementation.md) for API
behavior and current limitations. Passing fixtures does not establish device
interoperability.

## Version bumps

Use Python 3 and the bump-only helper to keep the Maven version, CLI banner,
and current documentation references synchronized:

```bash
tools/bump-version.sh --bump minor --dry-run
tools/bump-version.sh --bump minor
```

The default is a patch bump; `--bump major`, `--bump minor`, and `--bump patch`
are supported (`maintenance` is an alias for patch). Alternatively, select a
higher numeric version with `--set-version 4.0.0`. The script validates all
input files before writing, preserves unrelated documentation edits, and works
from any directory when invoked by its path. It does not build, commit, tag,
or push, and dry runs leave files unchanged. Review the diff and run
`tools/run-all.sh` before committing and tagging the release.

After committing, ensure a release tag exists locally, or explicitly publish it
to `origin`:

```bash
tools/update-tag.sh --tag v3.2.1
tools/update-tag.sh -t v3.2.1 --push
```

Tags must start with `v` and have three numeric components. Missing local tags
are annotated on the current commit; existing tags are never moved. `--push`
also checks `origin`, fetches remote-only tags, publishes missing remote tags,
and rejects conflicts. Without `--push`, no remote is contacted. The helper
does not bump versions, commit changes, or publish a GitHub release.

## Prepare a release

Commit the intended changes first. The release helper requires a clean working
tree on `main` or `master`. By default it bumps the patch version, runs checks,
builds, commits, tags, and pushes to `origin`:

```bash
python3 tools/release/release.py --bump maintenance
```

Use `--bump minor` or `--bump major` for larger releases. Inspect without changes:

```bash
python3 tools/release/release.py --next-version
python3 tools/release/release.py --dry-run
```

`--no-push`, `--no-tag`, and `--no-commit` control the corresponding Git actions.
Use `--help` for constraints. Preparing a release is separate from publishing
its GitHub release below.

## Publish a GitHub release

Authenticate GitHub CLI after installing development prerequisites:

```bash
gh auth login
gh auth status
```

Publish an existing pushed tag (compatible with older `gh` versions):

```bash
python3 tools/release/publish-release.py --tag v3.2.1 --dry-run
python3 tools/release/publish-release.py --tag v3.2.1
```

This checks the tag on GitHub and creates a published release with generated
notes. The release workflow then builds and uploads the JAR assets. An existing
published release is left unchanged; an existing draft must be reviewed and
published on GitHub. Authentication, network and missing-tag failures stop the
script. `--dry-run` reads GitHub state without publishing.

This publisher does not bump versions, commit, push tags, or build locally.
Use `tools/release/release.py` for local release preparation and
`tools/update-tag.sh --tag v3.2.1 --push` to publish the prepared tag first.

## Documentation maintenance

Keep the root README focused on user installation and links. Maintain setup,
build, release, and resource details here or in focused linked guides. Follow
[documentation-layout](../../skills/documentation-layout/SKILL.md).

Wiki sources live under `docs/wiki/`. Preview with `tools/update-wiki.sh`; see
[wiki publishing](../wiki-publishing.md) before publishing.

## Push branch changes

After committing, run:

```bash
tools/git/git-push.sh --master
```

This checks and pushes only the current main/master branch; it does not create
or publish a release. See the [Git helper guide](../../tools/git/README.md)
for clean-checkout requirements, validation, and retry behavior.
