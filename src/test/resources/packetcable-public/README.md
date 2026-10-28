# Public PacketCable regression fixtures

Credit: the [DOCSIS configuration-file encoder project, rlaager/docsis](https://github.com/rlaager/docsis)
and its contributors. These files are copied unchanged from commit
`94e8845838c0bea2c12e8002001f04a62ca1e3d0`.
The upstream GPLv2 license is retained in [COPYING](COPYING); these third-party
fixtures are not relicensed under OSCAR's Apache license. Original text inputs
are retained alongside the binaries in the upstream tool's syntax.

| Local file | Upstream path | SHA-256 |
| --- | --- | --- |
| `packetcable_digitmap.bin` | [examples/packetcable_digitmap.bin](https://github.com/rlaager/docsis/blob/94e8845838c0bea2c12e8002001f04a62ca1e3d0/examples/packetcable_digitmap.bin) | `c95f601becd0c946d2b6635fde759b280c3b02d66069e26176f24820325c25a3` |
| `packetcable_digitmap.txt` | [examples/packetcable_digitmap.txt](https://github.com/rlaager/docsis/blob/94e8845838c0bea2c12e8002001f04a62ca1e3d0/examples/packetcable_digitmap.txt) | `2d5d022c17dc8980af3897c1e9b2f5f2d4e2983be661cc70c8231b3c48e883ca` |
| `PacketCable_TLV64.cm` | [tests/PacketCable_TLV64.cm](https://github.com/rlaager/docsis/blob/94e8845838c0bea2c12e8002001f04a62ca1e3d0/tests/PacketCable_TLV64.cm) | `58f5b375528103ff511c66eefe7c98a063ea35d5fcaf59da227280d4fc387bea` |
| `PacketCable_TLV64.conf` | [tests/PacketCable_TLV64.conf](https://github.com/rlaager/docsis/blob/94e8845838c0bea2c12e8002001f04a62ca1e3d0/tests/PacketCable_TLV64.conf) | `9bcde8ae6a50402d5be2c9c597c560fda0312c4514563aff8ce1d9e69676cecc` |

`PublicPacketCableRoundTripTest` verifies fixture hashes and performs exact
binary → OSCAR text → binary comparisons under explicit PacketCable 1.5 and
2.0 profiles. It uses numeric OIDs to avoid dependence on host MIB resolution.
The 9,696-byte digit-map fixture contains 51 TLV-11 VarBinds, one TLV-64 VarBind,
and two TLV-254 delimiters. The 536-byte fixture contains one TLV-64 VarBind
and two delimiters.

These public samples exercise common encodings, not 2.0-specific IPv6 receivers,
ASCII vendor OUIs or DOCSIS 216/220 encapsulation. They are not deployment
recommendations or proof of device acceptance, complete provisioning validity,
or authentication. Tests read them without modifying them and require no network.

Run from the repository root:

```bash
./mvnw -B -Dtest=PublicPacketCableRoundTripTest test
```
