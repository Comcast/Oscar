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
It has been updated to the latest JRE baseline, and upcoming work will focus on DOCSIS 4.0 support.

## Quick Start

```bash
./install.sh --build-oscar-jar
```

## Requirements

- Java 21 JDK (not just the JRE). `javac` must be on your PATH for the build to succeed.

## Quick CLI Test

Text to binary:

```bash
java -jar oscar.jar -c -s d 3 -i src/com/comcast/oscar/examples/testfiles/DOCSIS-GOLDEN.txt -o output/DOCSIS-GOLDEN.bin
```

Binary to text:

```bash
java -jar oscar.jar -d -s d 3 -i output/DOCSIS-GOLDEN.bin > output/DOCSIS-GOLDEN-roundtrip.txt
```

📧 [Email](mailto:mgarcia01752@outlook.com)

🔗 [Maurice LinkedIn](https://www.linkedin.com/in/mauricemgarcia)

🔗 [Allen LinkedIn](https://www.linkedin.com/in/aflick/)
