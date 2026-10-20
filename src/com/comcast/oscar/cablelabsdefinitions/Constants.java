/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cablelabsdefinitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

  public static final Integer VARIABLE_LENGTH = -1;
  public static final Integer NO_LENGTH = -2;
  public static final Integer DEPRECATED_TLV = -100;
  public static final Integer NO_MAX_VALUE = -200;
  public static final Integer NO_MIN_VALUE = -201;

  public static final Integer STRING_TYPE = 0;
  public static final Integer INTEGER_TYPE = 1;
  public static final Integer TEXT_TYPE = 2;
  public static final Integer OID_TYPE = 3;
  public static final Integer BITS_TYPE = 4;

  public static final Integer SUB_TLV_NOT_INCLUDED = 0;
  public static final Integer SUB_TLV_INCLUDED = 1;

  public static final Integer CONFIGURATION_FILE_TYPE_DOCSIS = 0;

  public static final Integer DOCSIS_TLV_MIN = 1;
  public static final Integer DOCSIS_TLV_MAX = 254;

  public static final Integer PAD = 0;
  public static final Integer DS_FREQUENCY = 1;
  public static final Integer US_CHANN_ID = 2;
  public static final Integer NETWORK_ACC_CTRL_OBJ = 3;
  public static final Integer DOC_10_CLASS_OF_SERV = 4;
  public static final Integer MODEM_CAP = 5;
  public static final Integer CM_MIC = 6;
  public static final Integer CMTS_MIC = 7;
  public static final Integer VENDOR_ID_ENC = 8;
  public static final Integer SW_UPGRADE_FILENAME = 9;
  public static final Integer SNMP_WRITE_ACC_CTRL = 10;
  public static final Integer SNMP_MIB_OBJ = 11;
  public static final Integer MODEM_IP_ADDR = 12;
  public static final Integer SRV_NOT_AVA_RSP = 13;
  public static final Integer CPE_ETH_MAC_ADDR = 14;
  public static final Integer TELEPHONE_SETTINGS_OPTION = 15;
  public static final Integer BPI = 17;
  public static final Integer MAX_CPES = 18;
  public static final Integer TFTP_SRV_TIMESTAMP = 19;
  public static final Integer TFTP_SRV_PROV_MODEM_IPV4_ADDR = 20;
  public static final Integer SW_UPGRADE_IPV4_TFTP_SRV = 21;
  public static final Integer US_PKT_CLASS = 22;
  public static final Integer DS_PKT_CLASS = 23;
  public static final Integer US_SERVICE_FLOW = 24;
  public static final Integer DS_SERVICE_FLOW = 25;
  public static final Integer PHS = 26;
  public static final Integer HMAC_DIGEST = 27;
  public static final Integer MAX_NUM_CLASS = 28;
  public static final Integer PRIVACY_ENABLE = 29;
  public static final Integer AUTHORIZATION_BLOCK = 30;
  public static final Integer KEY_SEQUENCE_NUMBER = 31;
  public static final Integer MANUFACTURER_CVC = 32;
  public static final Integer CO_SIGNER_CVC = 33;
  public static final Integer SNMPV3_KICKSTART_VALUE = 34;
  public static final Integer SUB_MGMT_CTRL = 35;
  public static final Integer SUB_MGMT_CPE_IPV4_LIST = 36;
  public static final Integer SUB_MGMT_FILTER_GROUPS = 37;
  public static final Integer SNMPV3_NOTIFICATION_RCVR = 38;
  public static final Integer ENABLE_20_MODE = 39;
  public static final Integer ENABLE_TEST_MODES = 40;
  public static final Integer DS_CHANN_LIST = 41;
  public static final Integer STATIC_MULTICAST_MAC_ADDRESS = 42;
  public static final Integer DOC_EXT_FIELD = 43;
  public static final Integer VENDOR_SPECIFIC_CAP = 44;
  public static final Integer DS_UNENCRYPTED_TRAFFIC_FLTR = 45;
  public static final Integer TRANSMIT_CHANN_CONFIG = 46;
  public static final Integer SERVICE_FLOW_SID_CLUSTER_ASSIGNMENT = 47;
  public static final Integer RECEIVE_CHANN_PROFILE = 48;
  public static final Integer RECEIVE_CHANN_CONFIG = 49;
  public static final Integer DSID_ENCS = 50;
  public static final Integer SECURITY_ASSOCIATION_ENC = 51;
  public static final Integer INITIALIZING_CHANN_TIMEOUT = 52;
  public static final Integer SNMPV1V2C_COEXIST = 53;
  public static final Integer SNMPV3_ACC_VIEW = 54;
  public static final Integer SNMP_CPE_ACC_CTRL = 55;
  public static final Integer CHANN_ASSIGNMENT = 56;
  public static final Integer CM_INIT_REASON = 57;
  public static final Integer SW_UPGRADE_IPV6_TFTP_SRV = 58;
  public static final Integer TFTP_SRV_PROVISIONED_MODEMIPV6_ADDRESS = 59;
  public static final Integer US_DROP_PKT_CLASS = 60;
  public static final Integer SUB_MGMT_CPE_IPV6_PREFIXLIST = 61;
  public static final Integer US_DROP_CLASSIFIER_GROUP_ID = 62;
  public static final Integer SUB_MGMT_CTRL_MAX_CPEIPV6_ADDRESSES = 63;
  public static final Integer CMTS_STATIC_MULTICAST_SESSION_ENC = 64;
  public static final Integer L2VPN_MAC_AGING_ENC = 65;
  public static final Integer MGT_EVENT_CTRL_ENC = 66;
  public static final Integer SUB_MGMT_CPE_IPV6_LIST = 67;
  public static final Integer DEFAULT_US_TARGET_BUFFER_CONFIG = 68;
  public static final Integer MAC_ADDR_LEARN_CTRL_ENC = 69;
  public static final Integer US_AGGREGATE_SERVICE_FLOW = 70;
  public static final Integer DS_AGGREGATE_SERVICEFLOW = 71;
  public static final Integer MESP = 72;
  public static final Integer NETWORK_TIMING_PROFILE = 73;
  public static final Integer ENERGY_MGT_PARA_ENC = 74;
  public static final Integer ENERGY_MGT_MODE_INDICATOR = 75;
  public static final Integer END_OF_FILE = 255;

  /**
   * Historical OSCAR MIC ordering retained as an immutable compatibility alias. New callers should
   * select a coverage profile through DocsisMicRules.
   *
   * @deprecated use DocsisMicRules.orderedTypes with an explicit coverage profile
   */
  @Deprecated
  public static final List<Integer> DOCSIS_CMTS_MIC_TLV_LIST =
      com.comcast.oscar.compiler.DocsisMicRules.orderedTypes(
          com.comcast.oscar.compiler.DocsisMicRules.Coverage.LEGACY_COMPATIBILITY);

  /**
   * D.1.2 Configuration File Settings The following configuration settings are included in the
   * configuration file and MUST be supported by all CMs. The CM MUST NOT send a REG-REQ or
   * REG-REQ-MP based on a configuration file that lacks these mandatory items. Network Access
   * Configuration Setting CM MIC Configuration Setting CMTS MIC Configuration Setting End
   * Configuration Setting DOCSIS 1.0 Class of Service Configuration Setting OR Upstream Service
   * Flow Configuration Setting Downstream Service Flow Configuration Setting
   */
  public static final List<Integer> DOCSIS_MIN_TLV =
      new ArrayList<Integer>(Arrays.asList(NETWORK_ACC_CTRL_OBJ, CM_MIC, CMTS_MIC, END_OF_FILE));

  /** DOCSIS 1.0 or Greater */
  public static final List<Integer> DOCSIS_10_GTR_MIN_TLV =
      new ArrayList<Integer>(Arrays.asList(DOC_10_CLASS_OF_SERV));

  /** DOCSIS 1.1 or Greater */
  public static final List<Integer> DOCSIS_11_GTR_MIN_TLV =
      new ArrayList<Integer>(Arrays.asList(US_SERVICE_FLOW, DS_SERVICE_FLOW));
}
