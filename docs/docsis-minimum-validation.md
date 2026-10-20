# DOCSIS minimum top-level validation

For other families, see [minimum configuration by file type](minimum-configuration.md).

OSCAR checks the minimum top-level settings at complete-file finalization, after
text parsing and TLV insertions, before writing a compiled file. Incomplete drafts
and codec fragments can still be imported and exported through the low-level APIs.
They are not complete, deployable configurations.

## Required settings

MULPI **CM-SP-MULPIv3.1-I21-201020**, D.1.2, printed p.733, requires Network Access,
upstream and downstream service flows, CM MIC, CMTS MIC and End. See the
[local source](../skills/oscar-docsis-31-config-tlvs/references/sources/CM-SP-MULPIv3.1-I21-201020.txt).

| Stage | Required top-level types |
| --- | --- |
| DOCSIS 1.1–4.0 provisioned parameters | 3, 24, 25 |
| DOCSIS 1.0 provisioned parameters | 3, 4 (legacy Class of Service) |
| Finalized binary | Provisioned parameters plus 6, 7, 255 |

The legacy 1.0/1.1 selection follows OSCAR's existing version-specific minimum
lists; the pinned 3.1 source does not establish all older-version requirements.
DOCSIS 4.0 retains its additional compiler checks.

A missing or empty required parameter raises `IllegalArgumentException` identifying
all missing types and registry names. For example:

```text
Missing required DOCSIS top-level TLVs: 24 (UpstreamServiceFlow), 25 (DownstreamServiceFlow)
```

Presence is not full DOCSIS conformance: the checker does not validate child QoS
requirements, repetition limits, MIC authenticity, or hardware interoperability.
MICs and End are generated during compilation; do not insert placeholder MICs
into source text. Use the separate MIC verifier to check digest authenticity.

## Reusable API for OSCAR-UI and other callers

`com.comcast.oscar.compiler.DocsisMinimumRequirements` has no CLI or GUI dependency.
It does not change input files, generate MICs, or require a shared secret.
Callers supply `InputFormat` and a typed `DocsisVersion`; a binary does not reliably identify
its version, and filename extensions are not a reliable format detector.

```java
import com.comcast.oscar.compiler.DocsisMinimumRequirements;
import com.comcast.oscar.compiler.DocsisMinimumRequirements.InputFormat;
import com.comcast.oscar.configurationfile.DocsisVersion;

DocsisMinimumRequirements.validateFile(textPath, InputFormat.TEXT, DocsisVersion.DOCSIS_3_1);
DocsisMinimumRequirements.validateFile(binaryPath, InputFormat.BINARY, DocsisVersion.DOCSIS_3_1);

// In-memory editor content and complete binaries:
DocsisMinimumRequirements.validateText(editorText, DocsisVersion.DOCSIS_3_1);
DocsisMinimumRequirements.validateParameters(assembledBytes, DocsisVersion.DOCSIS_3_1);
DocsisMinimumRequirements.validateComplete(binaryBytes, DocsisVersion.DOCSIS_3_1);
```

`DocsisVersion.fromConfigurationFileType(...)` adapts legacy OSCAR integer codes
at existing API boundaries and rejects non-DOCSIS codes. Enum ordinals are never
used as file-type codes. Null API arguments are rejected with `NullPointerException`.

Text is parsed completely before the presence check; its wrapper must match the
selected DOCSIS family. Binary input is walked at top level with bounded lengths;
nested type numbers cannot satisfy missing top-level settings. Bytes after End
must be padding. Unsupported file families are rejected by this DOCSIS-specific API.

`DocsisCompiler.checkSpecification()` reports whether provisioned parameters meet
the minimum. It no longer has the inverted presence test. `commit()` enforces the
same rule and checks generated MIC/End presence afterward. DPoE/DPoG retain their
own family behavior. This release adds the reusable API in OSCAR; an OSCAR-UI
validation screen is separate integration work.

## Examples and verification

Use [positive and negative examples](../examples/docsis/3.1/minimum/README.md).
The shipped DOCSIS lab profiles and eRouter profiles contain the minimum settings.
Tests cover text and binary files, each missing required type, incomplete drafts,
malformed framing, repeated compilation and unchanged input bytes. Existing
independent MIC and binary round-trip tests continue to validate finalized output.
