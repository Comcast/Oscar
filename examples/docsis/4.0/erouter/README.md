# DOCSIS 4.0 eRouter lab profiles

These synthetic profiles combine the DOCSIS 4.0 service-flow lab profile with the [DOCSIS 3.1 eRouter examples](../../3.1/erouter/README.md)
using the `Docsis-4.0` header: [disabled](disabled.txt), [dual-stack](dual-stack.txt)
and [TR-069](tr069-lab.txt). Their source links, public test values and limitations
are documented there. They include the lab service flows required by the D4 compiler.

Both versions use shared eRouter catalog definitions. JUnit merges each fragment
with the matching complete lab profile and verifies byte-identical export/recompile
including regenerated integrity fields. No hardware interoperability test was run.
Use a build containing the embedded-profile loader; earlier releases exclude D4 eRouter.

From the repository root, compile/decode a lab profile with:

```bash
mkdir -p output
java -jar oscar.jar -c -s d4 4.0 -k examples/docsis/4.0/DOCSIS-LAB.key \
  -i examples/docsis/4.0/erouter/tr069-lab.txt -o output/d4-erouter.bin
java -jar oscar.jar -d -s d4 4.0 -i output/d4-erouter.bin > output/d4-erouter.txt
```

Before device provisioning, review operator requirements. ACS Override, TR-369 counts and
SNMP/vendor subtrees remain outside the reviewed D4 subset.
