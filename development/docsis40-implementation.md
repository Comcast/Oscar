# DOCSIS 4.0 implementation status

## Recovery checkpoint

Updated 2026-10-15 after recovering the interrupted D4.0 implementation session.
Runtime support exists, but **complete I12 configuration-file coverage is not
finished**. Preserve the existing uncommitted implementation when resuming.

The baseline is **CM-SP-MULPIv4.0-I12-261014**. The independent catalog at
`res/tlv-registry/docsis40.json` currently contains 101 definitions, counting
containers and generated MIC fields as well as configurable leaves. This count
is not a percentage of specification coverage. Each definition records its source
section and printed page. Source files and hashes are under
`skills/oscar-docsis-40-config-tlvs/references/`.

## Implemented and exercised

- `Docsis-4.0 { ... }` import/export and CLI selection with `-s d4 4.0`;
  the legacy DOCSIS catalog remains separate.
- Required Network Access and upstream/downstream provisioned service flows;
  service-flow references, QoS set consistency, unsigned 32-bit rates, priorities,
  service class names, bursts and rate relationships.
- Data Rate Unit Setting at `24.41` and `25.41`: bps, kbps, Mbps and Gbps.
  Independent exact-byte tests exercise 10-Gbit/s profiles without changing the
  four-octet rate fields or scaling byte-valued burst sizes.
- Per-flow AQM at `[24/25].40.1-6`: disable, classic latency target, algorithm,
  Immediate AQM threshold/range and latency histogram edges. Reserved enums,
  odd-length histograms and non-increasing edges are rejected. Recommendations
  such as the classic target's 10-100 ms range are not treated as wire bounds.
- I12 diplexer, FDD, FDX and advanced-band-plan controls; basic provisioning
  controls, MAC learning and E-MIC-protected Low Latency Disable.
- SYNC TLVs 101/102, including mixed IPv4/IPv6 address fields, from
  CM-SP-SYNC-I03-220715, Annex D.2.
- Common SSH settings at `103.1.1`, `.2`, `.3` and `.5`, with the two-octet
  top-level length; large-file export/recompile beyond 255-byte offsets.
- Baseline Privacy parameters `17.1-9` and bounds from
  CM-SP-SECv4.0-I08-250903, Annex A.1.1.
- CM MIC and legacy/extended CMTS MIC generation, HMAC-MD5 and MMH, explicit
  and implicit E-MIC modes, End marker and padding. MMH is checked against
  CM-SP-SEC3.0-C01-171207 published vectors. Diagnostics redact the shared key.
- Generated complete public-lab-key files for every catalog definition, with
  independent digest checks and byte-identical export/recompile. Generated
  fixtures are under `target/docsis40-lab/`, not checked-in production files.

The high-rate and AQM additions are grounded in CM-SP-MULPIv4.0-I12-261014,
sections C.2.2.9.15.1-6, printed pp. 827-828, and C.2.2.9.16, printed
pp. 828-829. File construction follows Annex D. Passing these tests is not
proof of CM/CMTS interoperability or a complete security conformance review.

## Remaining Work

The following are **unverified or unfinished**, not implemented merely because
they appear in a legacy catalog or a bundled source:

- Finish a path-by-path I12 Table 117 applicability audit and nested-child
  inventory. Many eligible settings remain outside the independent catalog,
  including classifiers, aggregate service flows and additional service-flow,
  management and software-download settings.
- Implement aggregate service-flow references and Dual Queue/Low Latency
  configuration before claiming full Low Latency support. Per-flow AQM alone
  does not configure a Dual Queue aggregate service flow.
- Finish SSH source CIDR, URL and credential encodings and dependencies.
  C.3.1.2 names CIDR and URL values without fully specifying their byte-string
  representation there; verify applicable SEC4 definitions before choosing a
  codec. Do not silently guess string termination or credential formats.
- Implement security TLV 104 and authenticated certificate/credential structures
  against SEC4. Implement source-prescribed fragmentation/reassembly for SSH,
  security and other fragmentable fields; current SSH support is single-fragment.
- Add required cross-field and mode dependencies as their settings are verified.
  Current checks cover the implemented subset, not all Annex C or D rules.
- Validate deployed files on actual D4.0 CMs/CMTSs with operator-controlled keys.
  Vendor-specific E-MIC algorithm 43 remains unverified and rejected.

**Deferred by agreement:** L2VPN-specific settings, including TLV 65 and nested
L2VPN extensions, and eDOCSIS/eSAFE TLVs 201-231. These are scope exclusions,
not declarations that those settings are forbidden in configuration files.

## Resume and Verify

Start with this checkpoint, the D4.0 skill's source map and implementation guide,
then the relevant source passage and a focused test. Do not merge I11 or DOCSIS
3.1 definitions into the I12 catalog without re-verification.

```bash
mvn -Dtest=Docsis40Test test
mvn test
tools/run-all.sh
```

The focused suite generates fixtures for every implemented numeric path. For a
packaged CLI smoke test after building, use the public lab key, never a production
key in a test command:

```bash
java -jar target/oscar-3.1.1-all.jar -c -s d4 4.0 \
  -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key \
  -i target/docsis40-lab/25-41.txt -o target/docsis40-lab/cli-10g.bin
java -jar target/oscar-3.1.1-all.jar -d -s d4 4.0 \
  -i target/docsis40-lab/cli-10g.bin
```

## Checkpoint Verification

- `tools/run-all.sh` passed formatting, hygiene compilation, packaging and all
  222 JUnit cases: 186 D4.0 and 36 registry/input-validation cases, none skipped.
- The packaged CLI compiled the generated `25-41.txt` 10-Gbit/s fixture,
  decoded it and recompiled it with a byte-identical binary result.
- The new checkpoint and symbol-table documentation have no editor diagnostics.
  `tools/markdown-check.sh` could not run because `npx` is not installed; the
  README's existing inline-HTML warnings remain unchanged.

