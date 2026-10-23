# DOCSIS 3.1 lab example

[Example index](../../README.md)

Use [DOCSIS-3.1-LAB.txt](DOCSIS-3.1-LAB.txt), [DOCSIS-3.1-LAB.bin](DOCSIS-3.1-LAB.bin), and
[DOCSIS-LAB.key](DOCSIS-LAB.key). These are copies of OSCAR's existing synthetic
regression fixtures. Source references and limitations are documented in
[CLI examples](../../../docs/wiki/CLI-Examples.md).

From the repository root with `oscar.jar` available:

```bash
mkdir -p output
java -jar oscar.jar -c -s d 3.1 -k examples/docsis/3.1/DOCSIS-LAB.key \
  -i examples/docsis/3.1/DOCSIS-3.1-LAB.txt -o output/DOCSIS-3.1-LAB.bin
java -jar oscar.jar -d -s d 3.1 -i output/DOCSIS-3.1-LAB.bin > output/DOCSIS-3.1-LAB-decoded.txt
```

The compiler supplies integrity fields and file finalization. The key is public
lab data. Successful compilation does not establish device interoperability.

## eRouter lab profiles

See [eRouter examples](erouter/README.md) for initialization-mode and TR-069 lab profiles
with both service flows. See [minimum-validation examples](minimum/README.md) for
valid and intentionally incomplete configurations and expected errors.

## Text lint examples

See [lint examples](lint/README.md) for intentionally malformed fragments,
expected error locations and CLI commands. These are separate from complete
lab profiles and demonstrate pre-compilation checks only.
