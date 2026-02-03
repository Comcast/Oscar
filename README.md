### [OSCAR - OpenSource Cablemodem file AssembleR](http://comcast.github.io/Oscar/)

OSCAR is a Java-based CableLabs&reg; Configuration File Builder API for DOCSIS&reg;, PacketCable&trade;, DPoE&trade; and DPoG&trade;.

### Install Java (JRE 21)

Run:

```bash
./install.sh
```

### Build (JDK 21 required)

```bash
mvn -q -DskipTests package
```

[Wiki](https://github.com/Comcast/Oscar/wiki)

[JavaDoc](http://comcast.github.io/Oscar/doc/index.html)

### Travis CL Build Status

[![Build Status](https://travis-ci.org/Comcast/Oscar.svg?branch=master)](https://travis-ci.org/Comcast/Oscar)

### New

* Configuration Merge Bulk Build
* NetSNMP OID Description
* Dotted or Textual OID Text Configuration File Output
* Toplevel TLV HEX Dump

### 2015 TODO's Features
* ADD REST Support - 2015
* DPoG Support - Later 2015
* ADD Extended CMTS MIC
* Provide GUI Support to ADD/EDIT TLV Types to SQLite Database (soon)
* Compile a JSON to Configuration File via CLI Later 2015
* Complete CLI Interpreter functionality(API Started)
* Edit Service Flow via Service Flow ID Directly

### Known Issues

* Add Multiple CommonTlvInserts at one time

