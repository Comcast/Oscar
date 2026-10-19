# DPoG 1.0 lab example

[Example index](../../README.md) · [DPoG guide](../../../docs/wiki/DPoE-&-DPoG.md)

[lab-service-flows.txt](lab-service-flows.txt) is a synthetic complete compiler
input with network access, two SNMP system strings, upstream/downstream service
flows, classifier references and CMIM masks. It requests 1 Gbit/s downstream,
100 Mbit/s upstream and a 6,000,000-byte burst. These are lab choices, not an
operator recommendation or a tested GPON service.

Sources: DPoG C01 Tables 7, 9 and 10; delegated DOCSIS 3.0 C01 C.2.1/C.2.2.
Downstream CMIM is explicitly listed in DPoG Table 9. The file contains no
customer values, community strings or passwords. It does not configure an
L2VPN service or OMCI mappings.

From the repository root with the current build of `oscar.jar`:

```bash
mkdir -p output
printf %s SHAREDSECRET > output/dpog-lab.key
java -jar oscar.jar -c -s dg 1.0 -k output/dpog-lab.key -i examples/dpog/1.0/lab-service-flows.txt -o output/dpog.bin
java -jar oscar.jar -d -s dg 1.0 -i output/dpog.bin > output/dpog.txt
java -jar oscar.jar -c -s dg 1.0 -k output/dpog-lab.key -i output/dpog.txt -o output/dpog-rebuilt.bin
cmp output/dpog.bin output/dpog-rebuilt.bin
```

The command explicitly supplies the public lab string `SHAREDSECRET`.
Use `-k KEY_FILE` for your own key; never commit a production key. The Java API also
supports unsigned output with an explicit empty/null key.

The commands above were verified locally, alongside in-memory JUnit round trips
and independent MIC byte-vector checks. Device interoperability is unverified.
See the [catalog matrix](../../../docs/dpog-catalog.md) for applicability and
remaining inherited metadata review.
