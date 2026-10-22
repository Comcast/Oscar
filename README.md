# OSCAR - OpenSource Cablemodem file AssembleR

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="docs/images/DOCSIS_Config_Editor_Dark.png">
    <source media="(prefers-color-scheme: light)" srcset="docs/images/DOCSIS_Config_Editor_Light.png">
    <img alt="OSCAR UI preview" src="docs/images/DOCSIS_Config_Editor_Light.png" width="280" style="border-radius: 28px;">
  </picture>
</p>

[![OSCAR CI and release](https://github.com/Comcast/Oscar/actions/workflows/ci.yml/badge.svg)](https://github.com/Comcast/Oscar/actions/workflows/ci.yml)
[![Dependency security](https://github.com/Comcast/Oscar/actions/workflows/dependency-security.yml/badge.svg)](https://github.com/Comcast/Oscar/actions/workflows/dependency-security.yml)
[![Copilot code review](https://github.com/Comcast/Oscar/actions/workflows/dynamic/copilot-pull-request-reviewer/copilot-pull-request-reviewer/badge.svg)](https://github.com/Comcast/Oscar/actions/workflows/copilot-pull-request-reviewer/copilot-pull-request-reviewer)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
[![Ubuntu LTS CI targets: 22.04, 24.04, 26.04](https://img.shields.io/badge/Ubuntu_LTS-22.04%20%7C%2024.04%20%7C%2026.04-E95420?logo=ubuntu&logoColor=white)](https://github.com/Comcast/Oscar/actions/workflows/ci.yml)
![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)

OSCAR is a Java-based CableLabs&reg; Configuration File Builder API for DOCSIS&reg;, PacketCable&trade;, DPoE&trade; and DPoG&trade;.

## Install and run

Download **oscar.jar** from [GitHub Releases](https://github.com/Comcast/Oscar/releases/latest).
It includes OSCAR's runtime dependencies; install **Java 21** separately.
No source checkout or Maven is needed to run it. OSCAR is a command-line tool.

| Platform | Installation guide |
| --- | --- |
| Windows | [Java setup, download, and PowerShell commands](docs/wiki/Installation.md#windows-powershell) |
| Linux | [Java setup, download, and terminal commands](docs/wiki/Installation.md#linux-installation) |
| macOS | [Java setup, download, and terminal commands](docs/wiki/Installation.md#macos-installation) |

```bash
java -jar oscar.jar --help
```

## Develop OSCAR

See the [development guide](docs/wiki/development.md) for cloning, installing
build prerequisites, building, testing, and preparing releases.

## Documentation

- [Downloadable examples by file type](examples/README.md)
- [Minimum TLVs by file type](docs/minimum-configuration.md)
- [CLI usage](docs/wiki/CLI-Overview.md) and [configuration examples](docs/wiki/CLI-Examples.md)
- [Java API](docs/wiki/API-Overview.md) and [MIC verification](docs/mic-verification.md)
- [SNMP value export](docs/snmp-value-export.md)
- [DOCSIS 4.0 implementation status](development/docsis40-implementation.md) — partial support
- [Apache-2.0 license](LICENSE)
