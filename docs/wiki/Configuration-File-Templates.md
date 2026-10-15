# Configuration File Templates

This page is a practical replacement for the legacy giant static TLV listing.

## Runnable DOCSIS Examples

- DOCSIS 3.1: [text](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.txt)
  and [binary](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.bin).
- DOCSIS 4.0: [text](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.txt)
  and [binary](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.bin).

These lab examples use the public key `SHAREDSECRET`. See
[CLI Examples](./CLI-Examples.md) to build, compile, decompile and recompile them.
Generated full dictionaries below are editing references, not complete validated
provisioning profiles; select valid settings and satisfy their dependencies.

## Generate Templates From CLI

DOCSIS:
```bash
java -jar oscar.jar -s d 3 -ftd > docsis-template.txt
```

DOCSIS 4.0 I12 (implemented subset only):
```bash
java -jar oscar.jar -s d4 4.0 -ftd > docsis-4.0-template.txt
```

PacketCable:
```bash
java -jar oscar.jar -s p 2.0 -ftd > packetcable-template.txt
```

DPoE:
```bash
java -jar oscar.jar -s de 2 -ftd > dpoe-template.txt
```

## Why this approach
- Always matches the current dictionary and parser behavior
- Easier to keep accurate than manually maintained static lists
- Better for version-specific workflows

## Related
- [Configuration File Types](./Configuration-File-Types.md)
- [CLI Overview](./CLI-Overview.md)
- [CLI Examples](./CLI-Examples.md)
