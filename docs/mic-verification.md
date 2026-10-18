# Verify configuration MICs and a supplied shared secret

`com.comcast.oscar.compiler.DocsisMicVerifier` checks original DOCSIS binary
bytes without decoding to text, recompiling, modifying the file, or logging keys.
It checks one supplied key (or an explicit pair of legacy/extended keys).
It does not search for keys and does not require Net-SNMP.

## Availability

Build OSCAR from a revision containing `DocsisMicVerifier` and the shared
D3.1/D4.0 finalizer. These changes do not imply that a new release has been
published. Verification is currently a Java API only, with no CLI flag or UI
control. Never compile with a candidate key and then verify that new output as
a check of the original file: compilation replaces the MICs.

## API

```java
import com.comcast.oscar.compiler.DocsisMicVerifier;
import java.nio.file.Files;
import java.util.Arrays;

// Inside your method; configurationPath and secretPath are java.nio.file.Path values.
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

## Generate extended MICs in DOCSIS 3.1 and 4.0

DOCSIS 3.1 (`ConfigurationFile.DOCSIS_VER_31`) and DOCSIS 4.0 compilation
share `DocsisMicFinalizer`. The D3.1 stub has been removed. Both support implicit
TLV 7 and explicit TLV 43.6.3, with HMAC-MD5 (1) or MMH (2). Compilation uses
the existing shared-secret string API (UTF-8) and requires a nonempty key.
Separate generation keys are not exposed by that API.

D3.1 text retains the existing names and BITS string syntax:

```text
VendorSpecific {
    VendorId [FF:FF:FF];
    ExtCmtsMicConf {
        ExtCmtsMicHMACType 1;
        ExtCmtsMicBitmap 000100000000000000000000000000000000000000010000;
        ExpExtCmtsMicDigest [00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00];
    }
}
```

This is a fragment to include inside `Docsis { ... }`, not a complete provisioned
file. Select D3.1 when constructing `ConfigurationFile`. The bitmap selects TLVs
3 and 43. The explicit placeholder is replaced during compilation; use eight
zero octets for MMH, or omit the digest child for implicit mode. VendorId must
be first. The D3.1 catalog now includes that child and variable bitmap lengths.
Existing identifiers are preserved; D4.0 retains its existing names.

Generation stably sorts top-level parameters by type when extended MIC parameters
are present, preserving repeated-instance order, and zeroes explicit self-coverage
before calculation. It then inserts CM MIC, CMTS MIC, End and padding. Without
extended parameters, input parameter order is retained. Verification always uses
the supplied binary bytes. D3.1 uses the pinned pre-3.0 MIC selection order,
including the IPv6 provisioning address, for standard/explicit TLV 7.

The catalogs are currently **independent**, not an inherited overlay:
`TlvRegistry` loads `docsis.json` and `docsis40.json` separately. Removing D4.0
43.6 definitions would remove D4.0 support. Shared calculation code eliminates
algorithm duplication; catalog inheritance would require a separate loader and
compatibility change, particularly because the names differ.

Source: MULPI 3.1 I21 §C.1.1.18, printed pp. 596-597 (VendorId first, FF:FF:FF),
§C.1.1.18.1.6, pp. 598-599 (43.6 children), and §D.1.3/D.2.1; corresponding
MULPI 4.0 I12 sections are cited above. Tests cover independently calculated
D3.1 generation bytes, text import, both algorithms/modes, repeated commits,
invalid parameters, and existing D4.0 generation cases.

## Remaining legacy compiler follow-up

Older DOCSIS versions still use the historical MIC selection code, whose DEBUG
output can include the shared secret. That previously reported path remains
unchanged. D3.1 and D4.0 now use the shared finalizer, which does not log keys.
No CM/CMTS interoperability claim follows from the automated tests.
