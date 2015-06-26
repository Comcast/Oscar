package com.comcast.oscar.dictionary;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 
	+------------------------------+----------------------+------+-----+---------+----------------+
	| Field                        | Type                 | Null | Key | Default | Extra          |
	+------------------------------+----------------------+------+-----+---------+----------------+
	| ID                           | int(10) unsigned     | NO   | PRI | NULL    | auto_increment | 
	| TYPE                         | smallint(5) unsigned | NO   |     | NULL    |                | 
	| PARENT_ID                    | int(10) unsigned     | YES  |     | NULL    |                | 
	| TLV_NAME                     | varchar(50)          | NO   |     | NULL    |                | 
	| TLV_DESCRIPTION              | text                 | NO   |     | NULL    |                | 
	| LENGTH_MIN                   | smallint(5) unsigned | NO   |     | NULL    |                | 
	| LENGTH_MAX                   | smallint(5) unsigned | NO   |     | NULL    |                | 
	| DATA_TYPE                    | varchar(50)          | YES  |     | NULL    |                | 
	| BYTE_LENGTH                  | tinyint(3) unsigned  | NO   |     | NULL    |                | 
	| MIN_SUPPORTED_VERSION        | varchar(50)          | NO   |     | NULL    |                | 
	+------------------------------+----------------------+------+-----+---------+----------------+
	
	JSON Formatter
		http://jsonviewer.stack.hu/
		http://jsonformatter.curiousconcept.com/
	


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

public interface Dictionary {

	public final static String 	DB_TBL_COL_ID 					= "ID";
	public final static String 	DB_TBL_COL_TYPE 				= "TYPE";
	public final static String 	DB_TBL_COL_PARENT_ID 			= "PARENT_ID";
	public final static String 	DB_TBL_COL_TLV_NAME 			= "TLV_NAME";
	public final static String 	DB_TBL_COL_DATA_TYPE 			= "DATA_TYPE";
	public final static String 	DB_TBL_COL_TLV_DESCRIPTION 		= "TLV_DESCRIPTION";
	public final static String 	DB_TBL_COL_LENGTH_MIN 			= "LENGTH_MIN";
	public final static String 	DB_TBL_COL_LENGTH_MAX 			= "LENGTH_MAX";
	public final static String 	DB_TBL_COL_SUPPORTED_VERSIONS	= "MIN_SUPPORTED_CONFIG_FILE_TYPE";
	public final static String 	DB_TBL_COL_BYTE_LENGTH 			= "BYTE_LENGTH";
	
	public final static String 	TYPE 							= "Type";
	public final static String 	TLV_NAME 						= "TlvName";
	public final static String 	DATA_TYPE 						= "DataType";
	public final static String 	TLV_DESCRIPTION 				= "TlvDescription";
	public final static String 	LENGTH_MIN 						= "LengthMin";
	public final static String 	LENGTH_MAX 						= "LengthMax";
	public final static String 	SUPPORTED_VERSIONS				= "SupportedVersion";
	public final static String 	ARE_SUBTYPES 					= "AreSubTypes";
	public final static String 	SUBTYPE_ARRAY 					= "SubTypeArray";
	public final static String 	PARENT_TYPE_LIST 				= "ParentTypeList";
	public final static String 	VALUE 							= "Value";
	public final static String 	BYTE_LENGTH 					= "ByteLength";

	public final static Integer	DOCSIS 							= 0;	
	public final static Integer PACKET_CABLE 			   		= 1;
	public final static Integer DPOE							= 2;
	
	/* Needed to Fix an Issue where More than 1 SubTLV is found */
	public final static String MULTI_SUB_TLV_INSTANCE			= "MULTI_SUB_TLV_INSTANCE";
	
	/* *****************************************************************************
	 *  								Methods
	 ******************************************************************************/
	
	/**
	 * 
	 * @param iType
	 */
	public void getMajorTlvDefinition(Integer iType);
	
	/**
	 * 
	 * @param iType
	
	 * @return JSONObject
	 */
	public JSONObject getTlvDefinition(Integer iType);
	
	/**
	 * 
	 * @param iCableLabsConfigType
	
	 * @return JSONArray
	 */
	public JSONArray getAllTlvDefinition(int iCableLabsConfigType);
}
