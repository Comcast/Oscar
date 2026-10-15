# CLI Overview

## Help
```bash
java -jar oscar.jar -h
```

## Version
```bash
java -jar oscar.jar -v
java -jar oscar.jar --version
```

## Core Flow
- Compile text -> binary: `-c`
- Decompile binary -> text: `-d`
- Set spec/version: `-s`
- Input/output: `-i` and `-o`
- CMTS shared-key file for compilation: `-k path/to/key` (not a literal secret)

## DOCSIS Version Selection

| Target | CLI Selection | Text Wrapper | Catalog |
| --- | --- | --- | --- |
| DOCSIS 3.1 | `-s d 3.1` | `Docsis { ... }` | Legacy DOCSIS |
| DOCSIS 4.0 I12 | `-s d4 4.0` or `-s d 4.0` | `Docsis-4.0 { ... }` | Independent I12 definitions |

D4.0 adds unsigned rate handling, selectable bps/kbps/Mbps/Gbps units,
per-flow AQM and latency histograms, band-plan controls, SYNC settings and common
SSH settings. Its compiler validates supported lengths, repeated fields and
selected dependencies, and generates CM/CMTS MICs using HMAC-MD5 or MMH E-MIC
settings. Text syntax and unknown nested names are checked before assembly.

The D4.0 catalog is intentionally separate from legacy DOCSIS. Its coverage is
[partial](../../development/docsis40-implementation.md); L2VPN and eDOCSIS/eSAFE
are deferred. For runnable text/binary pairs and build, compile, decompile and
recompile commands, see [CLI Examples](./CLI-Examples.md).

## Common Commands

Compile DOCSIS 3 template text to binary:
```bash
java -jar oscar.jar -c -s d 3 -i input.txt -o output.bin
```

Decompile binary to text (stdout):
```bash
java -jar oscar.jar -d -s d 3 -i input.bin
```

Generate full template for a spec:
```bash
java -jar oscar.jar -s d 3 -ftd > docsis-template.txt
java -jar oscar.jar -s p 2.0 -ftd > packetcable-template.txt
java -jar oscar.jar -s de 2 -ftd > dpoe-template.txt
```

## Frequently Used Flags
- `-df` downstream frequency insertion
- `-m` max CPE insertion
- `-f` firmware name insertion
- `-T` TFTP server insertion (v4/v6)
- `-cvc` CVC insertion
- `-O` SNMP OID insertion
- `-t` raw TLV insertion
- `-x` hex display
- `-j` JSON display
- `-tr` OID translate/description

For full command syntax and practical examples, see [CLI Examples](./CLI-Examples.md).
