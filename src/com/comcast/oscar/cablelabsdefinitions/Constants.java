package com.comcast.oscar.cablelabsdefinitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


public class Constants {

	public static final Integer VARIABLE_LENGTH 							= -1;
	public static final Integer NO_LENGTH 									= -2;
	public static final Integer DEPRECATED_TLV 								= -100;
	public static final Integer NO_MAX_VALUE 								= -200;
	public static final Integer NO_MIN_VALUE 								= -201;

	public static final Integer STRING_TYPE 								= 0;
	public static final Integer INTEGER_TYPE 								= 1;
	public static final Integer TEXT_TYPE 									= 2;
	public static final Integer OID_TYPE 									= 3;
	public static final Integer BITS_TYPE 									= 4;

	public static final Integer	SUB_TLV_NOT_INCLUDED 						= 0;
	public static final Integer	SUB_TLV_INCLUDED 							= 1;

	public static final Integer	CONFIGURATION_FILE_TYPE_DOCSIS 				= 0;

	public static final Integer	DOCSIS_TLV_MIN 								= 1;
	public static final Integer	DOCSIS_TLV_MAX 								= 254;

    public static final Integer PAD                          				= 0;
    public static final Integer DS_FREQUENCY               	                = 1;
    public static final Integer US_CHANN_ID             	                  = 2;
    public static final Integer NETWORK_ACC_CTRL_OBJ  	    	              = 3;
    public static final Integer DOC_10_CLASS_OF_SERV                          = 4;
    public static final Integer MODEM_CAP                                     = 5;    
    public static final Integer CM_MIC                                        = 6;
    public static final Integer CMTS_MIC                                      = 7;
    public static final Integer VENDOR_ID_ENC                                 = 8;
    public static final Integer SW_UPGRADE_FILENAME                           = 9;
    public static final Integer SNMP_WRITE_ACC_CTRL                           = 10;
    public static final Integer SNMP_MIB_OBJ                                  = 11;
    public static final Integer MODEM_IP_ADDR                                 = 12;
    public static final Integer SRV_NOT_AVA_RSP                               = 13;
    public static final Integer CPE_ETH_MAC_ADDR                              = 14;
    public static final Integer TELEPHONE_SETTINGS_OPTION                     = 15;
    public static final Integer BPI                                           = 17;
    public static final Integer MAX_CPES                                      = 18;
    public static final Integer TFTP_SRV_TIMESTAMP                            = 19;
    public static final Integer TFTP_SRV_PROV_MODEM_IPV4_ADDR                 = 20;
    public static final Integer SW_UPGRADE_IPV4_TFTP_SRV                      = 21;
    public static final Integer US_PKT_CLASS                                  = 22;
    public static final Integer DS_PKT_CLASS                                  = 23;
    public static final Integer US_SERVICE_FLOW                               = 24;
    public static final Integer DS_SERVICE_FLOW                               = 25;
    public static final Integer PHS                                           = 26;
    public static final Integer HMAC_DIGEST                                   = 27;
    public static final Integer MAX_NUM_CLASS                                 = 28;
    public static final Integer PRIVACY_ENABLE                                = 29;
    public static final Integer AUTHORIZATION_BLOCK                           = 30;
    public static final Integer KEY_SEQUENCE_NUMBER                           = 31;
    public static final Integer MANUFACTURER_CVC                              = 32;
    public static final Integer CO_SIGNER_CVC                                 = 33;
    public static final Integer SNMPV3_KICKSTART_VALUE                        = 34;
    public static final Integer SUB_MGMT_CTRL                                 = 35;
    public static final Integer SUB_MGMT_CPE_IPV4_LIST                        = 36;
    public static final Integer SUB_MGMT_FILTER_GROUPS                        = 37;
    public static final Integer SNMPV3_NOTIFICATION_RCVR                      = 38;
    public static final Integer ENABLE_20_MODE                                = 39;
    public static final Integer ENABLE_TEST_MODES                             = 40;
    public static final Integer DS_CHANN_LIST                                 = 41;
    public static final Integer STATIC_MULTICAST_MAC_ADDRESS                  = 42;
    public static final Integer DOC_EXT_FIELD                                 = 43;
    public static final Integer VENDOR_SPECIFIC_CAP                           = 44;
    public static final Integer DS_UNENCRYPTED_TRAFFIC_FLTR                   = 45;
    public static final Integer TRANSMIT_CHANN_CONFIG                         = 46;
    public static final Integer SERVICE_FLOW_SID_CLUSTER_ASSIGNMENT           = 47;
    public static final Integer RECEIVE_CHANN_PROFILE                         = 48;
    public static final Integer RECEIVE_CHANN_CONFIG                          = 49;
    public static final Integer DSID_ENCS                                     = 50;
    public static final Integer SECURITY_ASSOCIATION_ENC                      = 51;
    public static final Integer INITIALIZING_CHANN_TIMEOUT                    = 52;
    public static final Integer SNMPV1V2C_COEXIST                             = 53;  
    public static final Integer SNMPV3_ACC_VIEW                               = 54;
    public static final Integer SNMP_CPE_ACC_CTRL                             = 55;
    public static final Integer CHANN_ASSIGNMENT                              = 56;
    public static final Integer CM_INIT_REASON                                = 57;
    public static final Integer SW_UPGRADE_IPV6_TFTP_SRV                      = 58;
    public static final Integer TFTP_SRV_PROVISIONED_MODEMIPV6_ADDRESS        = 59;
    public static final Integer US_DROP_PKT_CLASS                             = 60;
    public static final Integer SUB_MGMT_CPE_IPV6_PREFIXLIST                  = 61;
    public static final Integer US_DROP_CLASSIFIER_GROUP_ID                   = 62;
    public static final Integer SUB_MGMT_CTRL_MAX_CPEIPV6_ADDRESSES           = 63;
    public static final Integer CMTS_STATIC_MULTICAST_SESSION_ENC             = 64;
    public static final Integer L2VPN_MAC_AGING_ENC                           = 65;
    public static final Integer MGT_EVENT_CTRL_ENC                            = 66;
    public static final Integer SUB_MGMT_CPE_IPV6_LIST                        = 67;
    public static final Integer DEFAULT_US_TARGET_BUFFER_CONFIG               = 68;
    public static final Integer MAC_ADDR_LEARN_CTRL_ENC                       = 69;  
    public static final Integer US_AGGREGATE_SERVICE_FLOW                     = 70;
    public static final Integer DS_AGGREGATE_SERVICEFLOW                      = 71;
    public static final Integer MESP                                          = 72;
    public static final Integer NETWORK_TIMING_PROFILE                        = 73;  
    public static final Integer ENERGY_MGT_PARA_ENC                           = 74;
    public static final Integer ENERGY_MGT_MODE_INDICATOR                     = 75;  
    public static final Integer END_OF_FILE                                   = 255;

    /**
     * D.2.1 CMTS MIC Calculation652
		The CMTS MUST calculate a CMTS MIC Digest value on TLVs of the REG-REQ/REG-REQ-MP message and
		compare it to the CMTS Message Integrity Check configuration setting in TLV7. If the Extended CMTS MIC
		Encoding is present but does not include an Explicit E-MIC Digest subtype, it indicates that the Extended CMTS
		MIC digest is implicitly provided in the CMTS MIC Configuration Setting of TLV7. In this case, the CMTS
		calculates only an Extended CMTS MIC digest using the TLVs indicated in the E-MIC Bitmap and compares it to
		the CMTS MIC Configuration Setting in TLV7. When the Extended CMTS MIC is implicitly provided in TLV7,
		the CMTS MUST confirm that the calculated Extended CMTS MIC digest matches the implicit digest in TLV7 in
		order to authorize the CM for registration.
		If the Extended CMTS MIC Encoding is present and provides an Explicit E-MIC Digest subtype, the CMTS
		calculates both an Extended MIC Digest value and a "pre-3.0 DOCSIS" CMTS MIC digest value using the TLVs
		reported in REG-REQ or REG-REQ-MP. When both the Extended MIC digest and the pre-3.0 DOCSIS CMTS
		Digest are checked, the CMTS MUST consider a CM to be authorized when only the pre-3.0 DOCSIS CMTS
		Digest matches. If the pre-3.0 DOCSIS CMTS MIC digest matches but the explicit Extended CMTS MIC does not,
		the CMTS MUST silently ignore TLVs in REG-REQ and REG-REQ-MP which were marked as protected by the
		Extended CMTS MIC Bitmap and are not one of the pre-3.0 DOCSIS CMTS MIC TLVs provided in the Pre-3.0
		DOCSIS CMTS MIC TLV List below.
		If the Extended CMTS MIC Encoding TLV is not present, or if the Extended CMTS MIC Encoding TLV is present
		and includes an Explicit E-MIC Digest Subtype, then the CMTS MUST calculate the message integrity check
		configuration setting by performing an MD5 digest over the following configuration setting fields when present in
		the REG-REQ or REG-REQ-MP messages, in the order shown:
		
		• Downstream Frequency Configuration Setting
		• Upstream Channel ID Configuration Setting
		• Network Access Configuration Setting
		• DOCSIS 1.0 Class of Service Configuration Setting
		• Baseline Privacy Configuration Setting
		• DOCSIS Extension Field Configuration Settings (including Extended CMTS MIC Params)
		• CM MIC Configuration Setting
		• Maximum Number of CPEs
		• TFTP Server Timestamp
		• TFTP Server Provisioned Modem Address
		• Upstream Packet Classification Setting
		• Downstream Packet Classification Setting
		• Upstream Service Flow Configuration Setting
		• Downstream Service Flow Configuration Setting
		• Maximum Number of Classifiers
		• Privacy Enable Configuration Setting
		• Payload Header Suppression
		• Subscriber Management Control
		• Subscriber Management CPE IP Table
		• Subscriber Management Filter Groups
		• Enable Test Modes
     */
	public static final List<Integer> DOCSIS_CMTS_MIC_TLV_LIST = new ArrayList<Integer>(Arrays.asList(	DS_FREQUENCY,
																											US_CHANN_ID ,
																											NETWORK_ACC_CTRL_OBJ,
																											DOC_10_CLASS_OF_SERV,
																											BPI,
																											DOC_EXT_FIELD,
																											CM_MIC,
																											MAX_CPES,
																											TFTP_SRV_TIMESTAMP,
																											TFTP_SRV_PROV_MODEM_IPV4_ADDR,
																											US_PKT_CLASS,
																											DS_PKT_CLASS,
																											US_SERVICE_FLOW,
																											DS_SERVICE_FLOW,
																											MAX_NUM_CLASS,
																											PRIVACY_ENABLE,
																											PHS,
																											SUB_MGMT_CTRL,
																											SUB_MGMT_CPE_IPV4_LIST,
																											SUB_MGMT_FILTER_GROUPS,
																											ENABLE_TEST_MODES));

	/**
	 * D.1.2 Configuration File Settings
		The following configuration settings are included in the configuration file and MUST be supported by all CMs. The
		CM MUST NOT send a REG-REQ or REG-REQ-MP based on a configuration file that lacks these mandatory items.
		* Network Access Configuration Setting
		* CM MIC Configuration Setting
		* CMTS MIC Configuration Setting
		* End Configuration Setting
		* DOCSIS 1.0 Class of Service Configuration Setting
						OR
		* Upstream Service Flow Configuration Setting
		* Downstream Service Flow Configuration Setting
	 */
	public static final List<Integer> DOCSIS_MIN_TLV = new ArrayList<Integer>(Arrays.asList(	NETWORK_ACC_CTRL_OBJ,
																								CM_MIC,
																								CMTS_MIC,
																								END_OF_FILE));
	
	/**
	 * DOCSIS 1.0 or Greater	
	 */
	public static final List<Integer> DOCSIS_10_GTR_MIN_TLV = new ArrayList<Integer>(Arrays.asList(DOC_10_CLASS_OF_SERV));
	
	/**
	 * DOCSIS 1.1 or Greater
	 */
	public static final List<Integer> DOCSIS_11_GTR_MIN_TLV = new ArrayList<Integer>(Arrays.asList(US_SERVICE_FLOW,DS_SERVICE_FLOW));
	
	
}
