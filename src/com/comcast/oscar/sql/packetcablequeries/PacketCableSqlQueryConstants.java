package com.comcast.oscar.sql.packetcablequeries;

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

public class PacketCableSqlQueryConstants {

/*
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
	| MIN_SUPPORTED_DOCSIS_VERSION | varchar(50)          | NO   |     | NULL    |                | 
	+------------------------------+----------------------+------+-----+---------+----------------+
*/
	
	public final static String TYPE = "Type";
	public final static String TLV_NAME = "TlvName";
	public final static String DATA_TYPE = "DataType";
	public final static String TLV_DESCRIPTION = "TlvDescription";
	public final static String LENGTH_MIN = "LengthMin";
	public final static String LENGTH_MAX = "LengthMax";
	public final static String SUPPORTED_DOCSIS_VERSIONS = "SupportDocsisVersion";
	public final static String ARE_SUBTYPES = "AreSubTypes";
	public final static String SUBTYPE_ARRAY = "SubTypeArray";
	public final static String PARENT_TYPE_LIST = "ParentTypeList";
	public final static String VALUE = "Value";
	public final static String BYTE_LENGTH = "ByteLength";
	
	public final static String DATA_TYPE_INTEGER = "integer";
	public final static String DATA_TYPE_STRING = "string";
	public final static String DATA_TYPE_STRING_BITS = "string_bits";
	public final static String DATA_TYPE_STRING_NULL_TERMINATED = "string_null_terminated";
	public final static String DATA_TYPE_BYTE_ARRAY = "byte_array";
	public final static String DATA_TYPE_MULTI_TLV_BYTE_ARRAY = "multi_tlv_byte_array";
	public final static String DATA_TYPE_OID = "oid";
	public final static String DATA_TYPE_OID_ASN1_OBJECT_6 = "oid_asn1_object6";
	public final static String DATA_TYPE_BYTE_ARRAY_IPV4_ADDR = "byte_array_ipv4_address";
	public final static String DATA_TYPE_BYTE_ARRAY_IPV6_ADDR = "byte_array_ipv6_address";
	public final static String DATA_TYPE_BYTE = "byte";
	public final static String DATA_TYPE_TRANSPORT_ADDR_IPV4_ADDR = "transport_address_ipv4_address";
	public final static String DATA_TYPE_TRANSPORT_ADDR_IPV6_ADDR = "transport_address_ipv6_address";	
	
	
}
