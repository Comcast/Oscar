# VLAN 1501 public-lab adaptation

This example is based on [sunnynetwork's DPoE lab walkthrough](https://sunnynetwork.wordpress.com/2016/03/10/lab-9dpoe-system/)
(2016), specifically its [configuration screenshot](https://sunnynetwork.wordpress.com/wp-content/uploads/2016/03/dpoe_3.png).
The source is a published lab demonstration, not a verified production file or
an I14 conformance vector. We inspected the screenshot; no original binary was
available. No copied screenshots are bundled.

## Files

- [lab-vlan1501-qos.txt](lab-vlan1501-qos.txt): runnable OSCAR classifier/QoS subset.
- [lab-vlan1501-qos.bin](lab-vlan1501-qos.bin): compiled subset for binary inspection,
  using the public lab key; not the original publisher's binary.
- [lab-vlan1501-reference.json](lab-vlan1501-reference.json): numeric transcription
  of the visible source, including fields absent from the runnable subset.
  This is a review record, **not OSCAR's JSON import format**. Lengths are those
  printed in the source; ASCII representation does not establish termination.

| Setting | Published lab | Runnable subset |
| --- | --- | --- |
| Service-flow references | Upstream 1001; downstream 2001 | Retained |
| Rate | 100,000,000 bit/s each way | Retained |
| QoS set | 7 | Retained |
| Upstream scheduling/polling | Type 4; interval 2000 | Retained |
| Classifier references | Upstream 51; downstream 61 | Retained |
| S-VLAN | 1501 | Retained as two bytes `05 DD` |
| Interface mask | `40 00 00 00` in both classifiers | Upstream only |
| VPN association | `EB-TRANS-SVID-1` and transport mode | Omitted |

## Adaptations and boundaries

Added `NetworkAccess 1` locally; it is not shown in the screenshot. OSCAR's
existing identifiers and bit-string syntax replace the source editor labels.
C.1.2 of DPoE-SP-MULPIv2.0-I14-230322, p. 84 directly defines the two-byte S-VID
with four reserved bits and twelve VID bits. C.14/Table 11, pp. 104–106 covers
the service-flow support and QoS-set restriction. Delegated DOCSIS encodings
remain subject to the [registry review](../../../docs/dpoe-registry-review.md).

The current DPoE registry lacks `23.13` and structured vendor/L2VPN descendants.
The runnable file deliberately omits top-level 43, upstream 24.43, downstream
classifier 23.43, and downstream CMIM 23.13. No raw payload was invented to
bypass these gaps. Those omissions remove the VPN/transport association and
make this an **incomplete service profile**, not an equivalent deployable
replacement. Do not infer an actual ONU port from the mask without its interface
mapping. The JSON preserves these omitted source observations for follow-up.

OSCAR currently normalizes child order during serialization; this is not a
byte-for-byte recreation of the source screenshot. The regression verifies the
subset's encoded values, declared omissions, and export/reimport behavior.
The CLI also adds MIC/finalization fields using the public lab key. These are
observed tool behavior, not evidence that DPoE requires MICs in every file.

## Compile, decode and compare

From the repository root with a matching `oscar.jar`:

```bash
mkdir -p output
java -jar oscar.jar -c -s de 2.0 -k examples/docsis/3.1/DOCSIS-LAB.key \
  -i examples/dpoe/2.0/lab-vlan1501-qos.txt -o output/lab-vlan1501-qos.bin
java -jar oscar.jar -d -s de 2.0 -i output/lab-vlan1501-qos.bin > output/lab-vlan1501-decoded.txt
java -jar oscar.jar -c -s de 2.0 -k examples/docsis/3.1/DOCSIS-LAB.key \
  -i output/lab-vlan1501-decoded.txt -o output/lab-vlan1501-rebuilt.bin
cmp output/lab-vlan1501-qos.bin output/lab-vlan1501-rebuilt.bin
```

The shared key is the existing public `SHAREDSECRET` lab key. Hardware
interoperability and end-to-end L2VPN service operation have not been tested.
