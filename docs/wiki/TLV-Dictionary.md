### TLV Dictionary Editing

To add TLVs to the dictionary we recommend using an editor such as [DB Browser for SQLite] (http://sqlitebrowser.org/).


#### Editing
_TLV_NAME Column_

 
* The name may be any Alpha Numeric Character with the following Restrictions
* MUST be one word, No Spaces
* Can have the combination of the following
* Aa-Zz, 0-9, - 

#### DataType Selection
_DATA_TYPE Column_

 
 integer

`MaxCPEAllowed 250;`
 							
 string

	SNMPv1v2cCoexistenceConfig {
		SNMPv1v2cCommunityName hDaFHJG7;	 /* TLV: [217.53.1]*/
	}
 								
 string_bits

	DownstreamPacketClassification	{
		8021adSTagCTagFrameClassification		{
			C-VID (00001111)(01101110);	 /* TLV: [23.14.6]*/
		}
	}
 
 string_null_terminated

	UpstreamServiceFlow {
			ServiceClassName hsd;	 /* TLV: [24.4]*/
	} 
 
 byte_array

	DownstreamPacketClassification	{
		 VendorSpecificClassifierParameters 08:03:FF:FF:FF:05:09:01:07:43:6F:6D:63:61:73:74;	 /* TLV: [23.43]*/
	}
							
 double_byte_array 				
 multi_tlv_byte_array

		ManufacturerCVC 30:82:03:1E:30:82:02:06:A0:03:02:01:02:02:10:30:13:55:A0:4D:95:AF:9B:6D:28:33:BC:0D:D9:67:B1:30:0D:06:09:2A:86:48:86:F7:0D:01:01:05:05:00:30:81:97:31:0B:30:09:06:03:55:04:06:13:02:55:53:31:39:30:37:06:03:55:04:0A:13:30:44:61:74:61:20:4F:76:65:72:20:43:61:62:6C:65:20:53:65:72:76:69:63:65:20:49:6E:74:65:72:66:61:63:65:20:53:70:65:63:69:66:69:63:61:74:69:6F:6E:73:31:15:30:13:06:03:55:04:0B:13:0C:43:61:62:6C:65:20:4D:6F:64:65:6D:73:31:36:30:34:06:03:55:04:03:13:2D:44:4F:43:53:49:53:20:43:61:62:6C:65:20:4D:6F:64:65:6D:20:52:6F:6F:74:20:43:65:72:74:69:66:69:63:61:74:65:20:41:75:74:68:6F:72:69:74:79:30:1E:17:0D:30:33:30:36:31:33:30:30:30:30:30:30:5A:17:0D:31:33:30:36:31:32:32:33:35:39:35:39:5A:30:66:31:0B:30:09:06:03:55:04:06:13:02:43:41:31:1E:30:1C:06:03:55:04:0A:13:15:45:6C:65:63:74:72:6F:6C:69:6E:65:20:45:71:75:69:70:6D:65:6E:74:31:0F:30:0D:06:03:55:04:0B:13:06:44:4F:43:53:49:53:31:26:30:24:06:03:55:04:03:13:1D:43:6F:64:65:20:56:65:72:69:66:69:63:61:74:69:6F:6E:20:43:65:72:74:69:66:69:63:61:74:65:30:81:9F:30:0D:06:09:2A:86:48:86:F7:0D:01:01:01:05:00:03:81:8D:00:30:81:89:02:81:81:00:D1:8B:B7:5E:C3:19:44:B3:F6:69:51:35:6A:B5:A3:BD:2B:BB:20:1E:2A:1B:21:B5:7F:A8:8F:0D:AA:F2:7D:46:B8:6D:71:97:5A:CF:B3:F2:F4:96:83:35:EE:30:E0:19:38:41:A5:CE:EE:3A:A1:68:A2:6D:AB:99:0E:DC:C4:F7:54:52:D9:C9:70:F0:F2:12:9C:B6:75:AD:42:5B:52:7C:D0:22:7A:59:C8:92:23:AE:AD:AA:F6:EB:FA:4C:89:2E:10:1C:81:18:92:8F:E4:4A:77:18:12:CE:56:AC:A1:D2:F8:F3:A5:0A:22:79:93:E1:9A:81:E6:10:4E:EC:FB:69:02:03:01:00:01:A3:1A:30:18:30:16:06:03:55:1D:25:01:01:FF:04:0C:30:0A:06:08:2B:06:01:05:05:07:03:03:30:0D:06:09:2A:86:48:86:F7:0D:01:01:05:05:00:03:82:01:01:00:A4:DB:FB:9B:8F:56:5A:6C:43:1E:B9:85:5A:85:82:89:6C:BC:4D:5D:2B:CF:81:6D:4A:A2:B3:AF:C7:3A:90:A5:FA:28:10:C6:46:B3:9E:40:2F:FA:0A:1E:AA:85:4D:02:1B:AC:94:6A:B5:9B:F9:26:76:B7:BC:01:74:F4:3D:3B:80:D0:E1:8A:DD:4D:70:4D:10:23:80:27:8F:4D:56:88:A0:4E:AC:72:BF:6D:99:E8:1A:D7:E9:1E:C8:43:08:46:B7:28:64:91:B3:6E:49:64:37:85:D5:37:1E:B4:20:CF:B7:AB:5D:48:76:B3:48:F1:51:16:F4:93:B5:3B:FB:6C:2B:45:48:68:38:97:41:AF:94:0F:B9:25:87:5F:D9:77:BE:B3:01:CC:4A:C3:5A:D0:40:83:9D:A2:75:01:05:6D:32:B0:27:BD:AA:D4:DD:E8:11:85:4D:22:77:79:8B:DA:04:DE:18:5E:6D:C2:64:9A:88:8B:EA:D9:D4:F2:8A:E7:83:34:51:69:CE:0B:AF:23:CB:6D:39:D4:98:6C:B0:D2:7E:CD:49:D9:D3:84:8E:0E:72:97:D3:1D:73:8C:C8:56:FA:6D:17:08:00:B9:CC:91:DE:F6:0E:31:20:85:37:97:BA:F9:F4:E5:86:69:0B:EB:3E:05:C1:5F:CA:BA:01:8D;	 /* TLV: [32]*/

 				
 oid 
									
	Snmp11 docsDevNmAccessIp.1 IpAddress  0.0.0.0 ; 

 oid_asn1_object6 

	 SNMPv3NotificationReceiver	{
		 SNMPv3NRFilteringParameters 1.3.6;	 /* TLV: [38.6]*/
	}
			
 byte_array_ipv4_address
 
 byte_array_ipv6_address
 
 byte 
 
 transport_address_ipv4_address 

	SNMPv1v2cTransportAddressAccess {
		SNMPv1v2cTransportAddress 0.0.0.0(0);	 /* TLV: [53.2.1]*/
		SNMPv1v2cTransportMask 0.0.0.0(0);	 /* TLV: [53.2.2]*/
	}
		
 transport_address_ipv6_address 

	SNMPv1v2cTransportAddressAccess {
		SNMPv1v2cTransportAddress 2001:558:0:0:0:0:0:0(0);	 /* TLV: [53.2.1]*/
		SNMPv1v2cTransportMask ffff:ffff:0:0:0:0:0:0(0);	 /* TLV: [53.2.2]*/
	}
		
 byte_array_inet_transport_address
 
 mac_address 
								
									
