# eRouter lab examples

These are synthetic DOCSIS 3.1 lab profiles with Network Access and both service
flows (100 Mbit/s upstream, 1 Gbit/s downstream). They are complete for compilation,
not operator-approved subscriber profiles or customer captures. No modem/CMTS interoperability test was run.

| File | Purpose | Verified status |
| --- | --- | --- |
| [disabled.txt](disabled.txt) | Disable eRouter with 202.1 = 0 | Current source compile/decompile/recompile: byte-identical |
| [dual-stack.txt](dual-stack.txt) | Enable dual-stack with 202.1 = 3 | Current source compile/decompile/recompile: byte-identical |
| [tr069-lab.txt](tr069-lab.txt) | IPv4 mode, CWMP and synthetic ACS credentials | Current source: byte-identical round trip |

## Public examples and provenance

Reviewed 2026-10-20:

- [QA Cafe CDRouter DOCSIS guide](https://support.qacafe.com/cdrouter/user-guide/cdrouter-docsis-user-guide#erouter-configuration)
  documents initialization modes for integrated routers.
- Its [TR-069 provisioning exercise](https://support.qacafe.com/cdrouter/user-guide/cdrouter-docsis-user-guide#tr-069-provisioning-via-the-docsis-configuration-file)
  demonstrates configuring an ACS through an embedded router section.

These independently written profiles use those scenarios, OSCAR identifiers,
and synthetic values. They do not reproduce the vendor's full configuration.
The source uses `Main`, `eRouter` and `TR69ManagementServer`; OSCAR uses `Docsis`,
`eRouterConfig` and `TR-069ManagementServer`. Normative meanings were checked
against CM-SP-eRouter-I21-220209 B.4.2 and B.4.3, pages 88–89, in the
[local source reference](../../../../skills/oscar-erouter-config-tlvs/references/source-map.md).
ACSOverride is omitted because its width is unresolved in our source review.
The `.test` ACS host is deliberately nonproduction; all passwords are public lab data.

## Run the examples

From the repository root, with an `oscar.jar` built from current source:

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

The lab key is `SHAREDSECRET`. These profiles compile with minimum top-level
validation and are checked by the in-memory round-trip suite for DOCSIS 3.1 and
4.0. The eRouter child definitions come from the referenced `erouter.json` catalog.
Successful compilation is not a modem interoperability claim.
