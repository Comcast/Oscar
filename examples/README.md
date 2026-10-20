# Configuration examples

Examples are grouped by configuration family and version. Start with the
DOCSIS lab files for complete compile/decompile exercises. All examples are
public lab material, not operator-approved production profiles.

| Family | Directory | Contents and status |
| --- | --- | --- |
| DOCSIS 3.1 | [docsis/3.1](docsis/3.1/README.md) | Text/binary pair: 1 Gbit/s downstream, 100 Mbit/s upstream; [eRouter lab profiles](docsis/3.1/erouter/README.md) |
| DOCSIS 4.0 | [docsis/4.0](docsis/4.0/README.md) | Text/binary pair: 10 Gbit/s downstream, 1 Gbit/s upstream; partial D4 support; [eRouter lab profiles](docsis/4.0/erouter/README.md) |
| DPoE 2.0 | [dpoe/2.0](dpoe/2.0/README.md) | Public-lab VLAN 1501 classifier/QoS adaptation plus minimal fragment; full L2VPN profile pending |
| PacketCable 1.0 | [packetcable/1.0](packetcable/1.0/README.md) | Opening-delimiter fragment; not a complete voice profile |
| DPoG 1.0 | [dpog/1.0](dpog/1.0/README.md) | Synthetic service flows, classifiers and SNMP; runnable lab profile |

## Using these files

Download the example files with **Raw / Download raw file**, or obtain them in
the repository's source archive. Put a matching release `oscar.jar` at the
repository root for the commands in the family guides, or adjust its path.
Run those commands from the repository root. Generated files go into `output/`.
For Java and JAR installation, see [installation](../docs/wiki/Installation.md).

The DOCSIS key files contain the public test string `SHAREDSECRET` with no
trailing newline. Use them only for the lab examples; key bytes affect MICs.
The DOCSIS examples are published copies of the existing regression fixtures
under `src/com/comcast/oscar/examples/testfiles/`, kept there for existing tests
and download links. Keep both copies synchronized when changing a lab fixture.

The DPoE and PacketCable fragments demonstrate input syntax and codec behavior;
they are not complete provisioning files. No customer configurations or internet
production binaries are included. See [CLI examples](../docs/wiki/CLI-Examples.md)
for additional commands and source references.

## Adding examples

Use `examples/<family>/<version>/`. Update this index and the local README with
purpose, source/provenance, required inputs, exact commands, validation performed,
and whether the file is a fragment or a complete lab profile. Include only
synthetic/public lab values; keep generated output out of the example directory.

DOCSIS [minimum-validation examples](docsis/3.1/minimum/README.md) include a valid
profile and intentionally incomplete inputs that must fail complete-file compilation.
