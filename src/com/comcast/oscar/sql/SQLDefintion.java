package com.comcast.oscar.sql;

/**
 * @bannerLicense
	Copyright 2015 Comcast Cable Communications Management, LLC<br>
	___________________________________________________________________<br>
	Licensed under the Apache License, Version 2.0 (the "License")<br>
	you may not use this file except in compliance with the License.<br>
	You may obtain a copy of the License at<br>
	http://www.apache.org/licenses/LICENSE-2.0<br>
	Unless required by applicable law or agreed to in writing, software<br>
	distributed under the License is distributed on an "AS IS" BASIS,<br>
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
	See the License for the specific language governing permissions and<br>
	limitations under the License.<br>


 * @author Maurice Garcia (maurice.garcia.2015@gmail.com)
 */


public class SQLDefintion {
	
	public static final Integer DB_TYPE_SQL_LITE 	= 0;
	
	private final String MYSQL_JDBC 	= "jdbc:mysql:";
	private final String SQL_LITE_JDBC 	= "jdbc:Sqlite:";
	
	private Map<String,String> mssDbDefinition= new HashMap<String,String>();
	
	private String sCurrentPath = "";
	
	/**
	 * 
	 */
	public SQLDefintion() {
		mssDbDefinition.put("DB_TYPE", Integer.toString(0));
		mssDbDefinition.put("HOST", sCurrentPath);
		mssDbDefinition.put("USER", "");
		mssDbDefinition.put("PASSWORD", "");
		mssDbDefinition.put("PORT", "");
		mssDbDefinition.put("JDBC", SQL_LITE_JDBC);
		mssDbDefinition.put("DRIVER", MYSQL_JDBC);	
	}
	
	/**
	 * 
	
	 * @return SQL Definition */
	public Map<String,String> getDefinition() {
		return mssDbDefinition;
	}
	

}
