# Initial Installation

		java -jar oscar.jar

# General Usage

#### Text To Binary

		java -jar oscar.jar -c -s d 3 -i docsis.txt -o docsis.bin

#### Binary to Text

		java -jar oscar.jar -d -s d 3 -i docsis.bin -o docsis.txt

#### Binary to Text -> Display all TLV default per specified Specification 

		java -jar oscar.jar -d verbose -s d 3 -i docsis.bin -o docsis.txt

#### Binary to Text -> Dotted OID Output

		java -jar oscar.jar -d dotted -s d 3 -i docsis.bin -o docsis.txt

#### Binary to Text -> Suppress `/*TLV: [x.x]*/`

		java -jar oscar.jar -d suppress -s d 3 -i docsis.bin -o docsis.txt

#### Binary To Text -> Add OID and Display

		java -jar oscar.jar -d -s d 3 -i docsis.bin -O [sysContact.0][1234567890][OctetString]

#### Binary To Text -> Add Downstream Frequency and Display

		java -jar oscar.jar -d -s d 3 -i docsis.bin -df 723000000

#### Binary To Text -> Add CVC Co-Sign Certificate

		java -jar oscar.jar -d -s d 3 -i docsis.bin -cvc c=COCVC.der

#### Binary To Text -> Add CVC Manufacture Certificate

		java -jar oscar.jar -s d 3 -d -i docsis.bin -cvc m=MANFUFACTURE-CVC.der

#### Binary To Text -> Add Software Download Filename

		java -jar oscar.jar -d -s d 3 -i docsis.bin -f FirmwareName.bin

#### Add TFTP Server Address IPv4

		java -jar oscar.jar -d -s d 3 -i docsis.bin -T v4=10.10.10.10

#### Binary To Text -> Add TFTP Server IPv6 Long and Short Form

		java -jar oscar.jar -s d 3 -d -i docsis.bin -T v6=2001::1

		java -jar oscar.jar -s d 3 -d -i docsis.bin -T v6=2001:558:ff2e:a92e:221:9bff:fe99:c7a0 

# DOCSIS                                         

#### Create DOCSIS Template File

		java -jar oscar.jar -s d 3 -ftd > docsis.txt

#### Binary To Text -> Add TLV - i.e. UpStream ChannelID

		java -jar oscar.jar -d -s d 3 -i docsis.bin -t 020101

# DPoE

#### Create DPoE Template File

		java -jar oscar.jar -s de -ftd > dpoe.txt

# PacketCable 2.0

#### Create Packet Cable Template File

		java -jar oscar.jar -s p 2.0 -ftd > pktcbl-2.0.txt

#### Binary To Text -> Add DigitMap

		java -jar oscar.jar -s p 2.0 -dm digitMap.txt -c -i pktcbl-2.0.bin -o pktcbl-2.0-DM.bin


# UTILITIES

#### Display DigitMap (Packet Cable Only)

		java -jar oscar.jar -ddm -i pktcbl-2.0-DM.bin

#### Bulk Compiling

		java -jar oscar.jar -s d 3 --key key.txt --bulk txt InputDir OutputDir -df 723000000

#### Merge Building -> Binary (Extension is optional and can be anything)

		java -jar oscar.jar -s d 3 --key key.txt -mbb InputDir1 InputDir2 InputDir3 o=OutputDir e=.bin b

#### Merge Building -> Text (Extension is optional and can be anything)

		java -jar oscar.jar -s d 3 --key key.txt -mbb InputDir1 InputDir2 InputDir3 o=OutputDir e=.txt b

#### Get TLV Definition - DestinationMACAddress	 /* TLV: [22.10.1]*/

		java -jar oscar.jar -td 22.10.1

#### HEX Dump

		java -jar oscar.jar -x -i pktcbl-2.0-DigitMap.bin

		java -jar oscar.jar -x t -i docsis.bin

#### Snmptranslate

		java -jar oscar.jar -tr docsDev

		java -jar oscar.jar -tr 1.3.6.1.2.1.69
