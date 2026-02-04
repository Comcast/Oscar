### [OSCAR - OpenSource Cablemodem file AssembleR](http://comcast.github.io/Oscar/)

[![CI](https://github.com/mgarcia01752/Oscar/actions/workflows/ci.yml/badge.svg)](https://github.com/mgarcia01752/Oscar/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Ubuntu 22.04](https://img.shields.io/badge/Ubuntu-22.04-E95420?logo=ubuntu&logoColor=white)
![Ubuntu 24.04](https://img.shields.io/badge/Ubuntu-24.04-E95420?logo=ubuntu&logoColor=white)
![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)

OSCAR is a Java-based CableLabs&reg; Configuration File Builder API for DOCSIS&reg;, PacketCable&trade;, DPoE&trade; and DPoG&trade;.

### Quick Start

```bash
./install.sh --build-oscar
```

### Project Guides

For tool organization and command map, see:

- `tools.md`

### Documentation (Local)

The legacy GitHub Wiki has been imported into this repo under `docs/wiki/`.

Start here:
- `docs/wiki/Home.md` (if present)
- `docs/wiki/CLI-Overview.md`
- `docs/wiki/API-Overview.md`

### Quick CLI Test

Text to binary:

```bash
java -jar oscar.jar -c -s d 3 -i src/com/comcast/oscar/examples/testfiles/DOCSIS-GOLDEN.txt -o output/DOCSIS-GOLDEN.bin
```

Binary to text:

```bash
java -jar oscar.jar -d -s d 3 -i output/DOCSIS-GOLDEN.bin > output/DOCSIS-GOLDEN-roundtrip.txt
```
