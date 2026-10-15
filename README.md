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

```bash
./install.sh --build-oscar-jar
```

## Requirements

- Java 21 JDK (not just the JRE). `javac` must be on your PATH for the build to succeed.

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
java -jar target/oscar-2.2.15-all.jar -c -s d 3.1 \
  -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key \
  -i src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.txt \
  -o output/DOCSIS-3.1-LAB.bin
java -jar target/oscar-2.2.15-all.jar -c -s d4 4.0 \
  -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key \
  -i src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.txt \
  -o output/DOCSIS-4.0-LAB.bin
```

Decompile to text; decoding does not require a shared key:

```bash
java -jar target/oscar-2.2.15-all.jar -d -s d 3.1 \
  -i output/DOCSIS-3.1-LAB.bin > output/DOCSIS-3.1-roundtrip.txt
java -jar target/oscar-2.2.15-all.jar -d -s d4 4.0 \
  -i output/DOCSIS-4.0-LAB.bin > output/DOCSIS-4.0-roundtrip.txt
```

See [CLI Examples](docs/wiki/CLI-Examples.md) for recompilation checks and
[CLI Overview](docs/wiki/CLI-Overview.md) for new features and version selection.

📧 [Email](mailto:mgarcia01752@outlook.com)

🔗 [Maurice LinkedIn](https://www.linkedin.com/in/mauricemgarcia)

🔗 [Allen LinkedIn](https://www.linkedin.com/in/aflick/)
