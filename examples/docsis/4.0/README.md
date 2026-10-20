# DOCSIS 4.0 lab example

[Example index](../../README.md)

Use [DOCSIS-4.0-LAB.txt](DOCSIS-4.0-LAB.txt), [DOCSIS-4.0-LAB.bin](DOCSIS-4.0-LAB.bin), and
[DOCSIS-LAB.key](DOCSIS-LAB.key). These are copies of OSCAR's existing synthetic
regression fixtures. Source references and limitations are documented in
[CLI examples](../../../docs/wiki/CLI-Examples.md).

From the repository root with `oscar.jar` available:

```bash
mkdir -p output
java -jar oscar.jar -c -s d4 4.0 -k examples/docsis/4.0/DOCSIS-LAB.key \
  -i examples/docsis/4.0/DOCSIS-4.0-LAB.txt -o output/DOCSIS-4.0-LAB.bin
java -jar oscar.jar -d -s d4 4.0 -i output/DOCSIS-4.0-LAB.bin > output/DOCSIS-4.0-LAB-decoded.txt
```

The compiler supplies integrity fields and file finalization. The key is public
lab data. Successful compilation does not establish device interoperability.

## eRouter

See [eRouter lab profiles](erouter/README.md) for shared DOCSIS/D4 scenarios and verification.
