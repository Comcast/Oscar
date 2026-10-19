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

Verified with the current OSCAR build: compilation and decoding succeed, but
the decoded wrapper is `PacketCable-X.X`, so this guide does not claim that
the decoded text can be recompiled unchanged. The compiler also inserts
finalization fields; the source remains only a delimiter demonstration.
