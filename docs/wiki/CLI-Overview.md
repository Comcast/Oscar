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
