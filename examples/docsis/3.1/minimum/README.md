# Minimum top-level validation examples

These synthetic files exercise MULPI 3.1 I21 D.1.2 (p.733). They contain no
customer data. The valid example includes distinct flow references and QoS set
types, but is not a bandwidth-limited or hardware-validated subscriber profile.

| File | Expected compilation result |
| --- | --- |
| [valid-minimum.txt](valid-minimum.txt) | Pass; compiler adds MICs and End |
| [missing-network-access.txt](missing-network-access.txt) | Reject: missing 3 |
| [missing-upstream.txt](missing-upstream.txt) | Reject: missing 24 |
| [missing-downstream.txt](missing-downstream.txt) | Reject: missing 25 |
| [missing-all.txt](missing-all.txt) | Reject: missing 3, 24, 25 |

From the repository root, using a JAR built with minimum validation:

```bash
mkdir -p output/minimum
java -jar oscar.jar -c -s d 3.1 -k examples/docsis/3.1/DOCSIS-LAB.key \
  -i examples/docsis/3.1/minimum/valid-minimum.txt -o output/minimum/valid.bin
java -jar oscar.jar -c -s d 3.1 -k examples/docsis/3.1/DOCSIS-LAB.key \
  -i examples/docsis/3.1/minimum/missing-upstream.txt -o output/minimum/rejected.bin
```

The second command must fail and must not create a new output file. Low-level
text import still accepts the draft: rejection occurs when compiling a complete
file. The public lab key is `SHAREDSECRET`.

[Validation API](../../../../docs/docsis-minimum-validation.md) accepts either
text or binary files. JUnit constructs negative binary fixtures in temporary
storage (including missing MICs and End), avoiding shipped binaries with fake
digests. Run `mvn -Dtest=DocsisMinimumRequirementsTest test` or `tools/run-all.sh`.
