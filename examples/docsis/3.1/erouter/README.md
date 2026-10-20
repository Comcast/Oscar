# eRouter lab examples

These are synthetic DOCSIS 3.1 fragments, not complete subscriber service
profiles or customer captures. Merge appropriate settings into an operator-reviewed
lab profile before device testing. No modem/CMTS interoperability test was run.

| File | Purpose | Verified status |
| --- | --- | --- |
| [disabled.txt](disabled.txt) | Disable eRouter with 202.1 = 0 | OSCAR 3.3.0 compile/decompile/recompile: byte-identical |
| [dual-stack.txt](dual-stack.txt) | Enable dual-stack with 202.1 = 3 | OSCAR 3.3.0 compile/decompile/recompile: byte-identical |
| [tr069-lab.txt](tr069-lab.txt) | IPv4 mode, CWMP and synthetic ACS credentials | Verified with the working-tree parser fix; byte-identical round trip |

## Public examples and provenance

Reviewed 2026-10-20:

- [QA Cafe CDRouter DOCSIS guide](https://support.qacafe.com/cdrouter/user-guide/cdrouter-docsis-user-guide#erouter-configuration)
  documents initialization modes for integrated routers.
- Its [TR-069 provisioning exercise](https://support.qacafe.com/cdrouter/user-guide/cdrouter-docsis-user-guide#tr-069-provisioning-via-the-docsis-configuration-file)
  demonstrates configuring an ACS through an embedded router section.

These independently written fragments use those scenarios, OSCAR identifiers,
and synthetic values. They do not reproduce the vendor's full configuration.
The source uses `Main`, `eRouter` and `TR69ManagementServer`; OSCAR uses `Docsis`,
`eRouterConfig` and `TR-069ManagementServer`. Normative meanings were checked
against CM-SP-eRouter-I21-220209 B.4.2 and B.4.3, pages 88–89, in the
[local source reference](../../../../skills/oscar-erouter-config-tlvs/references/source-map.md).
ACSOverride is omitted because its width is unresolved in our source review.
The `.test` ACS host is deliberately nonproduction; all passwords are public lab data.

## Run the examples

From the repository root, with an `oscar.jar` built from the quoted-string parser fix (the published
3.3.0 JAR predates this fix):

```bash
mkdir -p output/erouter-examples
for name in disabled dual-stack tr069-lab; do
  java -jar oscar.jar -c -s d 3.1 \
    -k examples/docsis/3.1/DOCSIS-LAB.key \
    -i "examples/docsis/3.1/erouter/$name.txt" \
    -o "output/erouter-examples/$name.bin" || break
  java -jar oscar.jar -d -s d 3.1 \
    -i "output/erouter-examples/$name.bin" \
    > "output/erouter-examples/$name-decoded.txt" || break
  java -jar oscar.jar -c -s d 3.1 \
    -k examples/docsis/3.1/DOCSIS-LAB.key \
    -i "output/erouter-examples/$name-decoded.txt" \
    -o "output/erouter-examples/$name-rebuilt.bin" || break
  cmp "output/erouter-examples/$name.bin" \
    "output/erouter-examples/$name-rebuilt.bin" || break
done
```

The lab key is `SHAREDSECRET`. Expected container bytes are `CA 03 01 01 00`
and `CA 03 01 01 03` respectively. Both were checked in the compiled output.
These exercises use the existing DOCSIS catalog, not the staged erouter.json.

## TR-069 verification

The published OSCAR 3.3.0 parser rejected ordinary ACS URLs. The working-tree
quoted-string grammar fix now compiles this example and preserves its URL and
synthetic credential bytes. Compile/decompile/recompile was byte-identical for
all three fragments. This is a local build verification, not a released-version
or modem interoperability claim.

Later add RA interval, SNMP and TR-369 fixtures after their catalog/codec
activation is ready. UI secret masking requires its own integration tests.
