# Minimum configuration by file type

Use this guide before compiling a text configuration into a finalized binary.
A TLV can be supported by the registry without being required in every file.
Likewise, a file that parses or round-trips successfully is not necessarily a
complete provisioning profile.

## What OSCAR currently enforces

The table describes OSCAR's compiler behavior, not a complete conformance
checklist for every specification. Required parameters belong at the top level;
an identically numbered child TLV does not satisfy them.

| File family / edition | Settings supplied in the text | Finalization and limits |
| --- | --- | --- |
| DOCSIS 1.0 | 3 `NetworkAccess`; 4 Class of Service | Legacy version-specific minimum. Compiler generates 6 CM MIC, 7 CMTS MIC and 255 End. Older-edition requirements have not been re-audited against their original specifications. |
| DOCSIS 1.1, 2.0, 3.0, 3.1 | 3 `NetworkAccess`; 24 `UpstreamServiceFlow`; 25 `DownstreamServiceFlow` | Compiler rejects missing or empty required parameters and generates 6, 7 and 255. The pinned DOCSIS 3.1 source confirms the mandatory set; legacy editions use OSCAR's existing selection. |
| DOCSIS 4.0 | 3 `NetworkAccess`; 24 `UpstreamServiceFlow`; 25 `DownstreamServiceFlow` | Same top-level minimum, plus the DOCSIS 4.0 compiler's supported value and child checks. Compiler generates 6, 7 and 255. |
| DPoE 1.0 / 2.0 | No comprehensive family-specific minimum-presence gate is implemented | The DPoE compiler bypasses the DOCSIS minimum check. Successful finalization does **not** certify a complete DPoE service profile. |
| DPoG 1.0 | 3 `NetworkAccess`; 24 upstream and 25 downstream service flows | Each service flow must contain child 1 (reference) and child 6 (QoS parameter set); references must be unique across the flows. Finalization appends End and padding. A supplied shared secret enables MIC generation; an extended-MIC request requires a secret. These are the current compiler checks, not a complete DPoG conformance claim. |
| PacketCable 1.0 / 1.5 / 2.0 | No comprehensive family-specific minimum voice-provisioning gate is implemented | The compiler handles file markers and integrity processing. A delimiter-only example is a fragment, not a usable voice profile. |

The DOCSIS mandatory items are specified in **CM-SP-MULPIv3.1-I21-201020,
D.1.2, printed p.733**, and independently in **CM-SP-MULPIv4.0-I12-261014,
D.1.2, printed p.857**. See the bundled
[3.1 source](../skills/oscar-docsis-31-config-tlvs/references/sources/CM-SP-MULPIv3.1-I21-201020.txt)
and [4.0 source](../skills/oscar-docsis-40-config-tlvs/references/sources/CM-SP-MULPIv4.0-I12-261014.txt).
Do not transfer these minimum rules to DPoE, DPoG or PacketCable by analogy.

## Drafts versus finalized files

For example, `Docsis { NetworkAccess 1; }` is an editable fragment. Complete-file
compilation rejects it because it lacks upstream and downstream service flows:

```text
Missing required DOCSIS top-level TLVs: 24 (UpstreamServiceFlow), 25 (DownstreamServiceFlow)
```

For DOCSIS, supply the service-flow settings and the shared secret to the
compiler; do not hand-fill CM MIC, CMTS MIC or End placeholders. Additional
child settings, dependencies and operator policies may be necessary for a
working modem configuration. eRouter and eSTB settings are embedded services;
adding them does not replace the enclosing DOCSIS minimum.

Low-level text parsing and TLV APIs intentionally accept fragments for editing
and codec tests. Use complete-file finalization to apply compiler requirements.
For a separate read-only DOCSIS check of text or binary input, use
[`DocsisMinimumRequirements`](docsis-minimum-validation.md#reusable-api-for-oscar-ui-and-other-callers).
It does not authenticate MICs; use [MIC verification](mic-verification.md)
for that task. This validator rejects non-DOCSIS families.

## Examples and implementation references

- [DOCSIS positive and intentionally invalid minimum examples](../examples/docsis/3.1/minimum/README.md)
- [Examples by family](../examples/README.md), with fragment and lab-profile status
- [Detailed DOCSIS validation behavior](docsis-minimum-validation.md)
- [DOCSIS validator](../src/com/comcast/oscar/compiler/DocsisMinimumRequirements.java)
- [DPoE compiler](../src/com/comcast/oscar/compiler/DPoECompiler.java), using the family branch in [DocsisCompiler](../src/com/comcast/oscar/compiler/DocsisCompiler.java)
- [DPoG finalization and parameter checks](../src/com/comcast/oscar/compiler/DPoGCompiler.java)
- [PacketCable finalization](../src/com/comcast/oscar/compiler/PacketCableCompiler.java)

Keep this table synchronized when a compiler's required settings change.
A future DPoE or PacketCable minimum gate needs its own specification review,
positive and missing-setting tests, and documented distinction between a
fragment and a complete profile.
