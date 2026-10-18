# CLI Examples

## Versioned Lab Examples

Run commands from the repository root after `mvn package`. The executable JAR is
`target/oscar-3.2.0-all.jar`; an installed `oscar.jar` from the same build can be
used instead.

| Version | Text Input | Binary Output |
| --- | --- | --- |
| DOCSIS 3.1 | [DOCSIS-3.1-LAB.txt](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.txt) | [DOCSIS-3.1-LAB.bin](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.bin) |
| DOCSIS 4.0 | [DOCSIS-4.0-LAB.txt](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.txt) | [DOCSIS-4.0-LAB.bin](../../src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.bin) |

These complete lab files use the public CMTS key `SHAREDSECRET`, not a production
credential. The compiler inserts CM MIC, CMTS MIC, End marker and padding.
Decompiling displays existing MICs but does not authenticate a file or require
the key. Recompiling recalculates MICs using the supplied key.

The D3.1 profile sets upstream/downstream maximum rates to 100 Mbit/s and
1 Gbit/s. It uses the legacy `Docsis` catalog and its spellings:
`QualityOfServiceParameterSetType 07` and `MaxSubstainedTrafficRate`.
The leading zero in `07` is required by that legacy byte-array codec.

The D4.0 profile sets upstream/downstream maximum rates to 1 and 10 Gbit/s using
`DataRateUnitSetting 3`. Values 0, 1, 2 and 3 select bps, kbps, Mbps and Gbps;
the rate fields remain four octets and burst sizes remain bytes. It also enables
per-flow docsisPIE AQM, a 10 ms classic latency target and downstream histogram
edges of 0.1, 1 and 10 ms. This is not a Dual Queue aggregate service-flow profile.

Neither example promises the configured bandwidth is available. D4.0 coverage
remains [partial](../../development/docsis40-implementation.md), with L2VPN and
eDOCSIS/eSAFE excluded. Real deployment requires an operator-reviewed profile,
private keys and hardware interoperability testing.

## Compile the Lab Files

```bash
mkdir -p output
java -jar target/oscar-3.2.0-all.jar -c -s d 3.1 -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key \
  -i src/com/comcast/oscar/examples/testfiles/DOCSIS-3.1-LAB.txt \
  -o output/DOCSIS-3.1-LAB.bin
java -jar target/oscar-3.2.0-all.jar -c -s d4 4.0 -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key \
  -i src/com/comcast/oscar/examples/testfiles/DOCSIS-4.0-LAB.txt \
  -o output/DOCSIS-4.0-LAB.bin
```

## Decompile and Recompile

Always select the matching family; the binary does not carry Oscar's text wrapper.

```bash
java -jar target/oscar-3.2.0-all.jar -d -s d 3.1 \
  -i output/DOCSIS-3.1-LAB.bin > output/DOCSIS-3.1-roundtrip.txt
java -jar target/oscar-3.2.0-all.jar -d -s d4 4.0 \
  -i output/DOCSIS-4.0-LAB.bin > output/DOCSIS-4.0-roundtrip.txt

java -jar target/oscar-3.2.0-all.jar -c -s d 3.1 -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key \
  -i output/DOCSIS-3.1-roundtrip.txt -o output/DOCSIS-3.1-rebuilt.bin
java -jar target/oscar-3.2.0-all.jar -c -s d4 4.0 -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key \
  -i output/DOCSIS-4.0-roundtrip.txt -o output/DOCSIS-4.0-rebuilt.bin

cmp output/DOCSIS-3.1-LAB.bin output/DOCSIS-3.1-rebuilt.bin
cmp output/DOCSIS-4.0-LAB.bin output/DOCSIS-4.0-rebuilt.bin
```

`cmp` is silent on success. The checked-in pairs are regression-tested for exact
compiled bytes, independently calculated MICs, decoded rates and recompilation.

## Example Sources

- [CM-SP-MULPIv3.1-I21-201020](../../skills/oscar-docsis-31-config-tlvs/references/sources/CM-SP-MULPIv3.1-I21-201020.txt):
  C.2.2.7.1, Service Flow Reference, printed p. 694; C.2.2.7.5, Quality of
  Service Parameter Set Type, p. 695; C.2.2.9.2-3, Maximum Sustained Traffic
  Rate and Maximum Traffic Burst, pp. 699-701; D.1.2-D.2.1, pp. 733-738.
- [CM-SP-MULPIv4.0-I12-261014](../../skills/oscar-docsis-40-config-tlvs/references/sources/CM-SP-MULPIv4.0-I12-261014.txt):
  C.2.2.7.1 and C.2.2.7.5, pp. 813-815; C.2.2.9.2-3, pp. 819-820;
  C.2.2.9.15, AQM Encodings, pp. 826-828; C.2.2.9.16, Data Rate Unit
  Setting, pp. 828-829; Annex D, configuration construction and MICs.

## Compile Text to Binary
```bash
java -jar oscar.jar -c -s d 3 -k src/com/comcast/oscar/examples/testfiles/DOCSIS-LAB.key -i docsis.txt -o docsis.bin
```

## Decompile Binary to Text
```bash
java -jar oscar.jar -d -s d 3 -i docsis.bin > docsis-roundtrip.txt
```

## Generate Templates
```bash
java -jar oscar.jar -s d 3 -ftd > docsis-template.txt
java -jar oscar.jar -s p 2.0 -ftd > packetcable-2.0-template.txt
java -jar oscar.jar -s de 2 -ftd > dpoe-template.txt
```

## Insert Common Values While Compiling
```bash
java -jar oscar.jar -c -s d 3 -i docsis.txt -o docsis.bin -df 723000000 -m 5 -f FirmwareName.bin
```

## Insert TFTP (IPv4 + IPv6)
```bash
java -jar oscar.jar -c -s d 3 -i docsis.txt -o docsis.bin -T v4=10.10.10.10 v6=2001::1
```

## Insert SNMP OIDs
```bash
java -jar oscar.jar -c -s d 3 -i docsis.txt -o docsis.bin -O [sysContact.0][Test][OctetString]
```

## Display Views
```bash
java -jar oscar.jar -j -s d 3 -i docsis.bin
java -jar oscar.jar -x -i docsis.bin
java -jar oscar.jar -td 22.10.1
```

## Bulk and Merge
```bash
java -jar oscar.jar -s d 3 -b txt InputDir OutputDir
java -jar oscar.jar -s d 3 -mbb InputDir1 InputDir2 InputDir3 o=OutputDir e=.bin b
```
