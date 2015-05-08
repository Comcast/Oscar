package com.comcast.oscar.examples;

import com.comcast.oscar.sql.queries.DictionarySQLQueries;

public class DictionaryToJSON {

	public static void main(String[] args) {

		DictionarySQLQueries dsq = new DictionarySQLQueries(DictionarySQLQueries.DOCSIS_DICTIONARY_TABLE_NAME);
		
		System.out.println(dsq.getAllTlvDefinition(DictionarySQLQueries.CONFIGURATION_FILE_TYPE_DOCSIS).toString());

	}

}
