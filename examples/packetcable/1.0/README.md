# PacketCable 1.0 example

[Example index](../../README.md)

[delimiter-fragment.txt](delimiter-fragment.txt) demonstrates OSCAR's existing
opening-delimiter syntax only. It is not a complete MTA/voice provisioning file.
No usable SIP/IMS credentials or operator network settings are included.

From the repository root with `oscar.jar` available:

```bash
mkdir -p output
java -jar oscar.jar -c -s p 1.0 -i examples/packetcable/1.0/delimiter-fragment.txt -o output/packetcable-delimiter.bin
java -jar oscar.jar -d -s p 1.0 -i output/packetcable-delimiter.bin > output/packetcable-delimiter-decoded.txt
```

Compilation exercises the current tool behavior; it does not turn this fragment
into a valid service profile. Use an operator-reviewed profile for actual MTAs.

With an explicit PacketCable version, decoded text retains its importable
version header. Recompilation replaces the generated integrity hash rather
than appending another one. In-memory regression tests cover all three
PacketCable editions; see [API round-trip tests](../../../docs/api-roundtrip-tests.md).
The source remains only a delimiter demonstration.
