# OSCAR - OpenSource Cablemodem file AssembleR

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="docs/images/DOCSIS_Config_Editor_Dark.png">
    <source media="(prefers-color-scheme: light)" srcset="docs/images/DOCSIS_Config_Editor_Light.png">
    <img alt="OSCAR UI preview" src="docs/images/DOCSIS_Config_Editor_Light.png" width="280" style="border-radius: 28px;">
  </picture>
</p>

[![CI](https://github.com/Comcast/Oscar/actions/workflows/ci.yml/badge.svg)](https://github.com/Comcast/Oscar/actions/workflows/ci.yml)
[![CI macOS](https://github.com/Comcast/Oscar/actions/workflows/ci-macos.yml/badge.svg)](https://github.com/Comcast/Oscar/actions/workflows/ci-macos.yml)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Ubuntu 22.04](https://img.shields.io/badge/Ubuntu-22.04-E95420?logo=ubuntu&logoColor=white)
![Ubuntu 24.04](https://img.shields.io/badge/Ubuntu-24.04-E95420?logo=ubuntu&logoColor=white)
![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)

OSCAR is a Java-based CableLabs&reg; Configuration File Builder API for DOCSIS&reg;, PacketCable&trade;, DPoE&trade; and DPoG&trade;.

## Project Status

I have taken this project back and resumed active development, building on the original codebase I wrote years ago.
It has been updated to the latest JRE baseline. Partial DOCSIS 4.0 I12 support is
available through `-s d4 4.0`, including source-verified settings and configuration
file MIC generation. See the [implementation status](development/docsis40-implementation.md)
for verified coverage and remaining work.

## Quick Start

Windows users: see [Java setup](docs/wiki/Installation.md#check-and-install-java)
and [download and run Oscar in PowerShell](docs/wiki/Installation.md#download-and-run-oscar).
The installer command below is for Linux.

```bash
./install.sh --build-oscar-jar
```

## Requirements

- Java 21 to run the downloaded release. See the
  [Windows Java check and installation steps](docs/wiki/Installation.md#check-and-install-java).
- Java 21 JDK (not just the JRE) to build from source. `javac` must be on your PATH for the build to succeed.

## Release JARs

The [release workflow](.github/workflows/release.yml) publishes both variants
with individual SHA-256 checksum files:

| Asset | Contents | Use |
| --- | --- | --- |
| `oscar-all.jar` | Oscar and runtime dependencies | Standalone CLI: `java -jar oscar-all.jar --help` |
| `oscar-thin.jar` | Oscar classes and required resources, without dependencies | Existing applications or an explicit dependency classpath |
| `oscar.jar` | Identical to `oscar-all.jar` | Compatible with existing download links and commands |

Neither variant includes Oscar examples or test classes. The all JAR bundles
runtime dependencies; the thin JAR omits them. Sizes vary between builds.
The thin JAR is not a standalone
replacement for `oscar.jar`: its runtime dependencies must be supplied separately.

SQLite is not required to build, test, or run 3.2.0, including bulk conversion
and template merging. SQLite databases and the JDBC driver are no longer
supplied. Applications using the retained legacy SQL API must supply their own
driver and legacy database; dictionary export is no longer supported.

The bundled SNMP OID map lives in `res/DottedTextualNetSNMPMap.json`; see the
[resource maintenance guide](res/README.md) for refresh commands and runtime
cache behavior. There is no bundled `db/` directory.

From a source checkout, build and run the thin variant on Linux or macOS:

```bash
mvn package
mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/lib
java -cp "target/oscar-3.1.1.jar:target/lib/*" com.comcast.oscar.cli.Main --help
```

For a downloaded thin release, replace `target/oscar-3.1.1.jar` with its path,
such as `oscar-thin.jar`. On Windows, use `;` instead of `:` between classpath
entries and keep the classpath quoted. New asset names become available when
the updated workflow builds a release; existing releases are not changed
automatically.

## Version Bumps

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
tools/update-tag.sh --tag v3.2.0
tools/update-tag.sh -t v3.2.0 --push
```

Tags must start with `v` and have three numeric components. Missing local tags
are annotated on the current commit; existing tags are never moved. `--push`
also checks `origin`, fetches remote-only tags, publishes missing remote tags,
and rejects conflicts. Without `--push`, no remote is contacted. The helper
does not bump versions, commit changes, or publish a GitHub release.

## DOCSIS Examples

| Version | Editable Text | Compiled Binary | Lab Profile |
| --- | --- | --- | --- |
| DOCSIS 3.1 | [DOCSIS-3.1-LAB.txt](src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.txt) | [DOCSIS-3.1-LAB.bin](src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.bin) | 100 Mbit/s upstream, 1 Gbit/s downstream |
| DOCSIS 4.0 | [DOCSIS-4.0-LAB.txt](src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.txt) | [DOCSIS-4.0-LAB.bin](src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.bin) | 1 Gbit/s upstream, 10 Gbit/s downstream; per-flow AQM |

Both binaries use the public [lab-key file](src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key)
containing `SHAREDSECRET`. The `-k` option takes a filename, not a literal key.
They are not production
provisioning profiles or evidence of CM/CMTS interoperability. D4.0 support
remains partial; L2VPN and eDOCSIS/eSAFE are deferred.

## Compile and Decompile

Run from the repository root. Build the current executable JAR:

```bash
mvn package
mkdir -p output
```

Compile the editable examples; CM/CMTS MICs, End marker and padding are generated:

```bash
java -jar target/oscar-3.2.0-all.jar -c -s d 3.1 \
  -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key \
  -i src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.txt \
  -o output/DOCSIS-3.1-LAB.bin
java -jar target/oscar-3.2.0-all.jar -c -s d4 4.0 \
  -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key \
  -i src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.txt \
  -o output/DOCSIS-4.0-LAB.bin
```

Decompile to text; decoding does not require a shared key:

```bash
java -jar target/oscar-3.2.0-all.jar -d -s d 3.1 \
  -i output/DOCSIS-3.1-LAB.bin > output/DOCSIS-3.1-roundtrip.txt
java -jar target/oscar-3.2.0-all.jar -d -s d4 4.0 \
  -i output/DOCSIS-4.0-LAB.bin > output/DOCSIS-4.0-roundtrip.txt
```

See [CLI Examples](docs/wiki/CLI-Examples.md) for recompilation checks and
[CLI Overview](docs/wiki/CLI-Overview.md) for new features and version selection.

## GitHub release tooling

Install developer dependencies, including GitHub CLI (`gh`), on Ubuntu:

```bash
./install.sh --development
gh auth login
gh auth status
```

The installer keeps an existing `gh` installation or installs the `gh` package
from your configured APT repositories, using sudo when needed. Authentication
is a separate interactive step; the installer does not log in or publish releases.
For other installation methods, see the
[GitHub CLI installation guide](https://github.com/cli/cli/blob/trunk/docs/install_linux.md).

## Shared-secret checks and extended MICs

The Java API checks a supplied shared secret against an existing DOCSIS 3.1 or
4.0 binary, including standard and extended MICs (HMAC-MD5 and MMH). It reports
CM, CMTS and extended results separately. Verification is API-only; there is
currently no CLI verification command or OSCAR-UI integration.

Both versions also generate implicit and explicit extended MICs during
compilation. See [MIC verification and generation](docs/mic-verification.md)
for API examples, key handling, result meanings and configuration syntax.
These features require a build containing the new implementation; an older
release JAR does not gain them from these documentation changes.

Maintain wiki pages under `docs/wiki/`. Preview updates with
`tools/update-wiki.sh`; see [wiki publishing](docs/wiki-publishing.md) for
publishing commands and link handling.

📧 [Email](mailto:mgarcia01752@outlook.com)

🔗 [Maurice LinkedIn](https://www.linkedin.com/in/mauricemgarcia)

🔗 [Allen LinkedIn](https://www.linkedin.com/in/aflick/)
