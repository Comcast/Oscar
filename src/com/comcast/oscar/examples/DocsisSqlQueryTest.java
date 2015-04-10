package com.comcast.oscar.examples;
import org.json.JSONArray;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.sql.SqlConnection;
import com.comcast.oscar.sql.docsisqueries.DocsisSqlQuery;

/*
	Copyright 2015 Comcast Cable Communications Management, LLC
	___________________________________________________________________
	Licensed under the Apache License, Version 2.0 (the "License")
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/

/**
 */
public class DocsisSqlQueryTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DocsisSqlQuery dsqTest = new DocsisSqlQuery(new SqlConnection().getConnectionID());
		
		JSONArray jaTlvDef = dsqTest.getAllTlvDefinition(Constants.CONFIGURATION_FILE_TYPE_DOCSIS);
		
		System.out.println("jaTlvDef: " + jaTlvDef);

	}

}
