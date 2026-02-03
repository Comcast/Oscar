
###[**API Examples**](https://github.com/Comcast/Oscar/tree/master/src/com/comcast/oscar/examples/api )

###[**API Development Examples**](https://github.com/Comcast/Oscar/tree/master/src/com/comcast/oscar/examples)

### Edit Docsis File from Binary

		/*DOCSIS Configuration*/
		String sDocsisConfigFileHex = "03:01:01:18:20:01:02:00:01:04:04:68:73:64:00:06:01:07:08:04:00:0B:B8:00:09:04:00:98:96:80:0E:02:17:C8:0F:01:02:19:13:01:02:00:02:06:01:07:08:04:00:86:47:00:09:04:01:31:2D:00:0B:18:30:16:06:08:2B:06:01:02:01:01:04:00:04:0A:54:65:73:74:53:74:72:69:6E:67:06:10:3F:CE:39:77:E9:F1:CE:08:D7:16:6E:5E:24:20:90:0E:07:10:32:88:B7:8F:05:A3:8D:D1:32:9C:6C:BD:49:4E:1D:5A:FF:00:00:00";
	
		/*Create TLV Builder*/
		TlvBuilder tbDCF = new TlvBuilder();
		
		/*Add Configuration to TLV Builder*/
		try {
			tbDCF.add(new HexString(HexString.toByteArray(sDocsisConfigFileHex)));
		} catch (TlvException e) {
			e.printStackTrace();
		}
		
		/* Disassemble to get JSON Dictionary - DOCSIS TYPE FILE */
		TlvDisassemble tdDCF = new TlvDisassemble(tbDCF,TlvDisassemble.TLV_TYPE_DOCSIS);
		
		/*Get JSON Dictionary*/
		JSONArray ja = tdDCF.getTlvDictionary();
						
		/******************************************************
		 * Editing Section via JSON DICTIONARY- Network Access
		 *****************************************************/
		
		/*Output Dictionary*/
		System.out.println("Before Edit:" + ja);
		
		ArrayDeque<String> ads = DictionaryTLV.getTypeHierarchyStack("3", new DictionarySQLQueries(DictionarySQLQueries.DOCSIS_DICTIONARY_TABLE_NAME));
				
		try {
			DictionaryTLV.updateDictionaryValue(ads ,ja.getJSONObject(0),"0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		/*Output Dictionary*/
		System.out.println("After Edit: " + ja);
			
		ConfigurationFileExport cfe = new ConfigurationFileExport(new TlvAssembler(ja).clone(),ConfigurationFileExport.DOCSIS_VER_10);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
		

### Edit Docsis File from Text
		/* Sample Configuration Does not contain TFTP SERVER ADDRESS*/
		String sDocsisConfiguration = "Docsis { NetworkAccess 1;	 /* TLV: [3]*/ BaselinePrivacyConfigSetting { AuthorizeWaitTimeout 10;	 /* TLV: [17.1]*/ ReauthorizeWaitTimeout 10;	 /* TLV: [17.2]*/ AuthorizationGraceTime 600;	 /* TLV: [17.3]*/ OperationalWaitTimeout 10;	 /* TLV: [17.4]*/ RekeyWaitTimeout 10;	 /* TLV: [17.5]*/ TEKGraceTime 600;	 /* TLV: [17.6]*/ AuthorizeRejectWaitTimeout 60;	 /* TLV: [17.7]*/ SAMapWaitTimeout 1;	 /* TLV: [17.8]*/ SAMapMaxRetries 4;	 /* TLV: [17.9]*/ } PrivacyEnable 1;	 /* TLV: [29]*/ SNMPv1v2cCoexistenceConfig { SNMPv1v2cCommunityName ReadOnly;	 /* TLV: [53.1]*/ SNMPv1v2cTransportAddressAccess { SNMPv1v2cTransportAddress 0.0.0.0(0);	 /* TLV: [53.2.1]*/ SNMPv1v2cTransportMask 0.0.0.0(0);	 /* TLV: [53.2.2]*/ } SNMPv1v2cAccessViewType 2;	 /* TLV: [53.3]*/ SNMPv1v2cAccessViewName docsisManagerView;	 /* TLV: [53.4]*/ } CmMic 96:1F:9E:C4:A4:CF:66:26:D6:DD:25:79:B2:90:5F:B1;	 /* TLV: [6]*/ CmtsMic B0:34:97:66:6A:A5:7E:93:89:E6:93:CC:3B:22:A8:09;	 /* TLV: [7]*/ }";

		//Create a String Builder
		StringBuilder sbDocsisConfiguration = new StringBuilder(sDocsisConfiguration);
		
		//Import Configuration
		ConfigrationFileImport cfiDocsis = new ConfigrationFileImport(sbDocsisConfiguration);
		
		//Conver to a Configuration File Object
		ConfigurationFile cf = new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30,cfiDocsis.getTlvBuilder());

		//Add TFTP Server Address
		try {
			CommonTlvInsertions.insertTftpServerAddress("10.10.10.10", cf, true);
		} catch (ConfigrationFileException e) {
			e.printStackTrace();
		}
		
		ConfigurationFileExport cfe = new ConfigurationFileExport(cf);
		
		System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));


