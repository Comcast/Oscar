# CLI Examples

## Compile Text to Binary
```bash
java -jar oscar.jar -c -s d 3 -i docsis.txt -o docsis.bin
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
