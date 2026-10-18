# Verify configuration MICs and a supplied shared secret

`com.comcast.oscar.compiler.DocsisMicVerifier` checks original DOCSIS binary
bytes without decoding to text, recompiling, modifying the file, or logging keys.
It checks one supplied key (or an explicit pair of legacy/extended keys).
It does not search for keys and does not require Net-SNMP.

## API

```java
byte[] binary = Files.readAllBytes(configurationPath);
byte[] secret = Files.readAllBytes(secretPath); // exact bytes; no newline trimming
try {
    var result = DocsisMicVerifier.verify(
        binary, DocsisMicVerifier.Profile.DOCSIS_31, secret);
    System.out.println(result); // fixed enums only; no keys or digests
} finally {
    Arrays.fill(secret, (byte) 0);
}
```

Use `Profile.DOCSIS_40` for the DOCSIS 4.0 TLV length definitions. The binary
itself does not identify the edition. PacketCable and DPoE are outside this API's
scope. No changes to OSCAR-UI or the existing CLI are included in this API feature.

For separate secrets, call `verify(binary, profile, legacySecret, extendedSecret)`.
The three-argument overload uses the supplied secret for both. Key bytes are not
converted through a charset, trimmed, or retained. The verifier clears its private
key copies; callers must clear their own arrays. This is not a guarantee that
cryptographic providers or the JVM erase every intermediate copy.

## Interpret results

Each result reports `cm`, `cmts`, and `extended`, independently:

| Status | Meaning |
| --- | --- |
| `MATCH` | The calculated digest equals the stored digest. |
| `MISMATCH` | The digest differs: the secret may be wrong or covered data may have changed. |
| `NOT_PRESENT` | The corresponding digest is absent; this is not a successful check. |
| `CANNOT_VERIFY` | A key is missing, input is malformed/unsupported, or the selected algorithm is unsupported. |

`reason` contains only a fixed enum; it never echoes input values or credentials.
For malformed or ambiguous framing all digest results are `CANNOT_VERIFY`.
A missing key does not prevent computing the unkeyed CM MIC.

- CM MIC is MD5 over original parameter TLVs excluding TLVs 6 and 7.
- Without extended parameters, TLV 7 is the legacy HMAC-MD5 digest.
- With implicit extended MIC, TLV 7 carries the extended digest; `cmts` and
  `extended` therefore report the same check. MMH's eight-byte digest is checked
  with the required zero padding to 16 bytes.
- With explicit extended MIC, TLV 7 is checked using the legacy key and
  TLV 43.6.3 is checked separately using the extended key. Its value is zeroed
  in a private copy when calculating extended coverage of TLV 43.
- Bitmap-selected TLVs and their children retain physical byte order. Repeated
  instances are not collapsed. Legacy selection uses the specified type order,
  preserving order among repeated instances.

A CM MIC match alone says nothing about the shared secret. A matching CMTS MIC
is evidence for that digest and covered bytes, not proof of full configuration
validity, encryption, or modem interoperability. An explicit extended mismatch
can coexist with a matching legacy digest; callers must display both results
rather than claiming complete integrity or predicting CMTS authorization policy.

The parser bounds all TLVs and rejects truncation, duplicate MIC/extended fields,
invalid digest sizes, missing End markers, nonzero bytes after End, invalid
bitmaps, and unsupported algorithms. It accepts zero padding after End. Unknown
top-level types and PAD before End currently return `CANNOT_VERIFY`; it never
guesses their length or silently excludes them. This is an integrity checker,
not a full nested-TLV schema or mandatory-provisioning validator.

## Source requirements and validation

The implementation follows these separately pinned sources:

- **CM-SP-MULPIv3.1-I21-201020**, §C.1.1.18.1.6 (extended parameters),
  §D.1.3–D.1.3.1, printed pp. 735–737, and §D.2.1, pp. 737–739.
  [Bundled source](../skills/oscar-docsis-31-config-tlvs/references/sources/CM-SP-MULPIv3.1-I21-201020.txt).
- **CM-SP-MULPIv4.0-I12-261014**, §C.1.1.6 (TLV 7 padding),
  §C.1.1.18.1.6, pp. 700–701; §D.1.3–D.1.3.1, pp. 858–860;
  §D.2.1, pp. 860–863.
  [Bundled source](../skills/oscar-docsis-40-config-tlvs/references/sources/CM-SP-MULPIv4.0-I12-261014.txt).
- **CM-SP-SEC3.0-C01-171207**, §11.7.3–11.7.4, pp. 95–97: MMH output
  size and CMTS key derivation. Reuses `DocsisMmh`, tested against published
  SEC3 vectors.
  [Bundled source](../skills/oscar-docsis-40-config-tlvs/references/sources/CM-SP-SEC3.0-C01-171207.txt).

Tests construct HMAC fixtures independently of OSCAR's compiler, exercise both
profiles, both extended algorithms, separate keys, self-coverage, repeated TLVs,
wrong keys, altered bytes, missing fields/keys, malformed/truncated input, and
two-byte lengths. Additional integration tests verify DOCSIS 4.0 compiler output.
No live CM/CMTS interoperability testing has been performed.

```bash
mvn -B -Dtest=DocsisMicVerifierTest,Docsis40Test test
mvn -B test
```

## Existing compiler findings (separate follow-up)

Inspection found that legacy `DocsisCompiler.setExtendedCMTSMic` is a stub and
legacy compiler DEBUG output can include its shared secret. Those existing paths
were reported to the author and remain unchanged pending a separate decision.
The new verifier does not call them. The older legacy MIC selection list also
contains historical fields and omits the IPv6 provisioning address; verification
uses the pinned MULPI order already implemented by `Docsis40Compiler` instead.
