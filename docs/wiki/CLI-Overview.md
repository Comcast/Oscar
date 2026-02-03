### Help
For information about all CLI commands:

    java -jar oscar.jar -help

### Usage
Typical usage:

    usage: java -jar oscar.jar -k <key> -i <.txt> -o <.bin> -c

### Bulk Build 
Compile all files to binary from the input directory. Output directory optional.

    -b,--bulk <bin|txt> <input dir> <output dir>

### Compile
Compile text to binary.
 
    -c,--compile                                     
    
### CVC Insertion
Add this CVC during file compilation. For CoSigner use this format for the argument: **c=filename**. For Manufacturer use this format for the argument: **m=filename**. Both CVCs can be inserted simultaneously (space delimited).

    -cvc,--certificate <c|m=<filename>>

Examples:

* Manufacturer

        java -jar oscar.jar -c -i input.txt -o output.bin -cvc m=manufacturer.cvc

* CoSigner

        java -jar oscar.jar -c -i input.txt -o output.bin -cvc c=cosigner.cvc

* Both

        java -jar oscar.jar -c -i input.txt -o output.bin -cvc m=manufacturer.cvc c=cosigner.cvc

### Decompile
Decompile binary to text. 
Option v for full TLV defaults display.
Option d to display Dotted OID for SNMP.
Option s to suppress TLV: [x.x] from Configuration            

    -d,--decompile <v{erbose}> <d{otted}> <s{uppress}>

### Display Digitmap
Display the DigitMap of the input file - PacketCable ONLY.

    -ddm,--displaydigitmap

### Downstream Frequency Insertion
Insert this downstream frequency during file compilation.
                       
     -df,--dsfreq <downstream frequency>

Example:

    java -jar oscar.jar -c -i input.txt -o output.bin -df 723000000
               

### Digitmap Insertion
Insert this Digitmap into a file when compiling - PacketCable ONLY. Multiple Digitmaps can be inserted simultaneously (space delimited).

     -dm,--digitmap <[<filename>][<OID>]>

Examples:

* One Digitmap

        java -jar oscar.jar -c -s packetcable -i input.txt -o output.bin -dm digitmap.txt

* One Digitmap with OID

        java -jar oscar.jar -c -s packetcable -i input.txt -o output.bin -dm [digitmap.txt][.1.2.3.4.5]

* Two Digitmaps

        java -jar oscar.jar -c -s packetcable -i input.txt -o output.bin -dm digitmap1.txt digitmap2.txt

### Firmware Insertion
Insert this firmware during file compilation.
         
    -f,--firmware <filename>

### Full TLV Display
Display all TLVs available in the dictionary for the defined specification.

    -ftd,--fulltlvdisplay

Examples:

* DOCSIS

        java -jar oscar.jar -s docsis -ftd

* PacketCable

        java -jar oscar.jar -s packetcable -ftd

### Input File
File to analyze and/or compile/decompile.
                             
    -i,--input <filename> 

### Display JSON
Display the JSON of the input file.
                            
    -j,--json

### Display TLV of JSON array
View the TLV number of a JSON array within the file.
                                    
    -j2t,--json2tlv <filename>

### Key (Shared Secret)
Use this sharedsecret to compile the file - DOCSIS ONLY.

    -k,--key <filename>

Example:

    java -jar oscar.jar -c -i input.txt -o output.bin -k sharedsecret.txt

### Maximum CPEs Insertion
Insert the maximum CPEs during file compilation.

    -m,--maxCPE <maximum CPEs>       

Example:

    java -jar oscar.jar -c -i input.txt -o output.bin -m 5

### Merge Bulk Build
Merge multiple text files from directories into one binary. Output directory and b{inary}/t{ext} are optional. [Example Diagram](https://github.com/Comcast/Oscar/wiki/Template-Merge-Bulk-Build)

        -mbb,--mergebulkbuild <input dirs> <o=<output dir>> <e=<extension>> <b{inary}/t{ext}>

Example:

* To text

        -mbb inputDirectoryModel inputDirectoryTier inputDirectoryCPE o=outputDirectory e=.txt t

* To binary

        -mbb inputDirectoryModel inputDirectoryTier inputDirectoryCPE o=outputDirectory e=.bin b

### Output File
Compile the input file to this output file.
        
    -o,--output <filename>

### OID Insertion
Insert this OID into a file when compiling. Multiple OIDs can be inserted simultaneously (space delimited). Applicable datatypes: [Counter32, Counter64, Gauge32, OctetString, Integer32, TimeTicks, IpAddress, HexString]                            
     
    -O,--OID <[<OID>][<value>][<data type>]>

Example:

    java -jar oscar.jar -c -i input.txt -o output.bin -OID [sysContact.0][TestInsert-0][OctectString] [sysContact.1][TestInsert-1][OctectString] 

### Specification
Set specification and version of the file to be compiled/decompiled.
         
    -s,--spec <d{ocsis}|p{acketcable}|d{po}e> <version>

Examples:

* DOCSIS

        java -jar oscar.jar -s d 3 -c -i input.txt -o output.bin

* PacketCable

        java -jar oscar.jar -s p 1.5 -c -i input.txt -o output.bin

* DPoE

        java -jar oscar.jar -s de 2 -c -i input.txt -o output.bin


Supported Versions:

* DOCSIS 1.0, 1.1, 2.0, 3.0, 3.1
* PacketCable 1.0, 1.5, 2.0
* DPoE 1.0, 2.0

### TFTP Server Insertion
Add this TFTP server during file compilation. For IPv4 use this format for the argument: **v4=<server address>**. For IPv6 use this format for the argument: **v6=<server address>**. Both address versions can be inserted simultaneously (space delimited).
             
    -T,--tftp <v4|v6=<tftp address>>

Examples:

* IPv4 Address

        java -jar oscar.jar -c -i input.txt -o output.bin -T v4=192.168.0.1

* IPv6 Address

        java -jar oscar.jar -c -i input.txt -o output.bin -T v6=2001:0DB8:AC10:FE01:0000:0000:0000:0000

* Both

        java -jar oscar.jar -c -i input.txt -o output.bin -T v4=192.168.0.1 v6=2001:0DB8:AC10:FE01:0000:0000:0000:0000
                  
### TLV Insertion
Insert this TLV during file compilation.

    -t,--tlv <TLV>

Example:

    java -jar oscar.jar -c -i input.txt -o output.bin -t 030101

### Display JSON Array of TLV
View the JSON array of a TLV.
                                    
    -t2j,--tlv2json <TLV>

Example:

    java -jar oscar.jar -t2j 030101 

### Display TLV Description
Display the TLV description.
                            
     -td,--tlvdescription <TLV dot notation>

Example:

    java -jar oscar.jar -td 53.2.1

	`SNMPv1v2cTransportAddress:`

	`This sub-TLV specifies the Transport Address to use in conjunction with the `
	`Transport Address Mask used by the CM to grant access to the SNMP entity querying the CM. `
	`The CM MUST reject the configuration file if sub-TLV 53.2.1 is not present.`

	`Note: Length is 6 bytes for IPv4 and 18 bytes for IPv6.`

	`String Format:`

	`0.0.0.0(0) = IPv4_Address(Port)`

	`OR`

	`XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX(0) = IPv6_Address(Port)`
	


### Current Version
Display current version.

    -version

### Display Hex
Display the hex of the input file. Option t creates a newline at the start of every top level TLV (binary files only).
                                      
    -x,--hex <s{tring}> <t{oplevel}>

#### Snmptranslate Get OID Description ([NetSNMP](https://github.com/Comcast/Oscar/wiki/NetSNMP) MUST be Installed)
Display the description of a given Dotted or Textual OID from the MIB

    -tr,--translate <OID> 

Examples: 

* From textual notation

        -tr docsDev

* From dotted notation

        -tr 1.3.6.1.2.1.69.                                       