# DPoE 2.0 examples

[Example index](../../README.md)

Start with [the VLAN 1501 public-lab adaptation](lab-vlan1501-notes.md):
100 Mbit/s upstream/downstream flows, two VLAN classifiers, scheduling/polling,
and an upstream interface mask. The guide explains the source, runnable subset,
missing L2VPN/CMIM fields, and compile/decode/round-trip commands.

The [numeric source reference](lab-vlan1501-reference.json) records the complete
visible screenshot fields for follow-up; it is not an OSCAR import file.

## Minimal syntax fragment

[network-access.txt](network-access.txt) is a synthetic, single-TLV codec
fragment. It is not the internet lab configuration discussed previously, nor a
complete deployable DPoE service profile. It avoids the known MESP/I-TCI/name
codec mismatches in the [registry review](../../../docs/dpoe-registry-review.md).

From the repository root with `oscar.jar` available:

```bash
mkdir -p output
java -jar oscar.jar -c -s de 2.0 -i examples/dpoe/2.0/network-access.txt -o output/dpoe-network-access.bin
java -jar oscar.jar -d -s de 2.0 -i output/dpoe-network-access.bin > output/dpoe-network-access-decoded.txt
```

This exercises OSCAR's current legacy DPoE behavior, not I14 conformance.
Add a full service-flow/MESP example after the documented registry fixes and
independent byte/round-trip verification. Keep external examples' provenance
and adaptation notes with the file when such examples are added.

Verified with the current OSCAR build: compilation and decoding retain
`NetworkAccess 1`. The legacy compiler also adds MIC/finalization fields;
this is observed tool behavior, not a DPoE requirement for MIC presence.
