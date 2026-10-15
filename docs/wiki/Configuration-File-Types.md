# Configuration File Types

## DOCSIS
DOCSIS 3.1 uses the legacy catalog with `-s d 3.1`:

```text
Docsis {
}
```

DOCSIS 4.0 I12 uses its independent catalog with `-s d4 4.0`:

```text
Docsis-4.0 {
}
```

These empty wrappers illustrate syntax only; complete DOCSIS lab files need
Network Access, upstream/downstream service flows and compiler-generated MICs
and End marker. Use the checked-in
[D3.1 text](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.txt) and
[D4.0 text](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.txt)
examples, with commands and binary links in [CLI Examples](./CLI-Examples.md).
Keywords differ between catalogs; do not just change an existing file's wrapper.

## DPoE
```text
DPoE {
}
```

## PacketCable
```text
PacketCable-1.0 {
}
PacketCable-1.5 {
}
PacketCable-2.0 {
}
```

For datatype details, see [TLV Dictionary](./TLV-Dictionary.md).
