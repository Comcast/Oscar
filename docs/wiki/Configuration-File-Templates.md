# Configuration File Templates

This page is a practical replacement for the legacy giant static TLV listing.

## Generate Templates From CLI

DOCSIS:
```bash
java -jar oscar.jar -s d 3 -ftd > docsis-template.txt
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
