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

  // Boxed compatibility aliases; wire IDs are owned by DocsisTlvTypes.
  public static final Integer PAD = DocsisTlvTypes.PAD;
  public static final Integer DS_FREQUENCY = DocsisTlvTypes.DS_FREQUENCY;
  public static final Integer US_CHANN_ID = DocsisTlvTypes.US_CHANN_ID;
  public static final Integer NETWORK_ACC_CTRL_OBJ = DocsisTlvTypes.NETWORK_ACC_CTRL_OBJ;
  public static final Integer DOC_10_CLASS_OF_SERV = DocsisTlvTypes.DOC_10_CLASS_OF_SERV;
  public static final Integer MODEM_CAP = DocsisTlvTypes.MODEM_CAP;
  public static final Integer CM_MIC = DocsisTlvTypes.CM_MIC;
  public static final Integer CMTS_MIC = DocsisTlvTypes.CMTS_MIC;
  public static final Integer VENDOR_ID_ENC = DocsisTlvTypes.VENDOR_ID_ENC;
  public static final Integer SW_UPGRADE_FILENAME = DocsisTlvTypes.SW_UPGRADE_FILENAME;
  public static final Integer SNMP_WRITE_ACC_CTRL = DocsisTlvTypes.SNMP_WRITE_ACC_CTRL;
  public static final Integer SNMP_MIB_OBJ = DocsisTlvTypes.SNMP_MIB_OBJ;
  public static final Integer MODEM_IP_ADDR = DocsisTlvTypes.MODEM_IP_ADDR;
  public static final Integer SRV_NOT_AVA_RSP = DocsisTlvTypes.SRV_NOT_AVA_RSP;
  public static final Integer CPE_ETH_MAC_ADDR = DocsisTlvTypes.CPE_ETH_MAC_ADDR;
  public static final Integer TELEPHONE_SETTINGS_OPTION = DocsisTlvTypes.TELEPHONE_SETTINGS_OPTION;
  public static final Integer BPI = DocsisTlvTypes.BPI;
  public static final Integer MAX_CPES = DocsisTlvTypes.MAX_CPES;
  public static final Integer TFTP_SRV_TIMESTAMP = DocsisTlvTypes.TFTP_SRV_TIMESTAMP;
  public static final Integer TFTP_SRV_PROV_MODEM_IPV4_ADDR =
      DocsisTlvTypes.TFTP_SRV_PROV_MODEM_IPV4_ADDR;
  public static final Integer SW_UPGRADE_IPV4_TFTP_SRV = DocsisTlvTypes.SW_UPGRADE_IPV4_TFTP_SRV;
  public static final Integer US_PKT_CLASS = DocsisTlvTypes.US_PKT_CLASS;
  public static final Integer DS_PKT_CLASS = DocsisTlvTypes.DS_PKT_CLASS;
  public static final Integer US_SERVICE_FLOW = DocsisTlvTypes.US_SERVICE_FLOW;
  public static final Integer DS_SERVICE_FLOW = DocsisTlvTypes.DS_SERVICE_FLOW;
  public static final Integer PHS = DocsisTlvTypes.PHS;
  public static final Integer HMAC_DIGEST = DocsisTlvTypes.HMAC_DIGEST;
  public static final Integer MAX_NUM_CLASS = DocsisTlvTypes.MAX_NUM_CLASS;
  public static final Integer PRIVACY_ENABLE = DocsisTlvTypes.PRIVACY_ENABLE;
  public static final Integer AUTHORIZATION_BLOCK = DocsisTlvTypes.AUTHORIZATION_BLOCK;
  public static final Integer KEY_SEQUENCE_NUMBER = DocsisTlvTypes.KEY_SEQUENCE_NUMBER;
  public static final Integer MANUFACTURER_CVC = DocsisTlvTypes.MANUFACTURER_CVC;
  public static final Integer CO_SIGNER_CVC = DocsisTlvTypes.CO_SIGNER_CVC;
  public static final Integer MANUFACTURER_CVC_CHAIN = DocsisTlvTypes.MANUFACTURER_CVC_CHAIN;
  public static final Integer CO_SIGNER_CVC_CHAIN = DocsisTlvTypes.CO_SIGNER_CVC_CHAIN;
  public static final Integer ESTB_CONFIGURATION = DocsisTlvTypes.ESTB_CONFIGURATION;
  public static final Integer SNMPV3_KICKSTART_VALUE = DocsisTlvTypes.SNMPV3_KICKSTART_VALUE;
  public static final Integer SUB_MGMT_CTRL = DocsisTlvTypes.SUB_MGMT_CTRL;
  public static final Integer SUB_MGMT_CPE_IPV4_LIST = DocsisTlvTypes.SUB_MGMT_CPE_IPV4_LIST;
  public static final Integer SUB_MGMT_FILTER_GROUPS = DocsisTlvTypes.SUB_MGMT_FILTER_GROUPS;
  public static final Integer SNMPV3_NOTIFICATION_RCVR = DocsisTlvTypes.SNMPV3_NOTIFICATION_RCVR;
  public static final Integer ENABLE_20_MODE = DocsisTlvTypes.ENABLE_20_MODE;
  public static final Integer ENABLE_TEST_MODES = DocsisTlvTypes.ENABLE_TEST_MODES;
  public static final Integer DS_CHANN_LIST = DocsisTlvTypes.DS_CHANN_LIST;
  public static final Integer STATIC_MULTICAST_MAC_ADDRESS =
      DocsisTlvTypes.STATIC_MULTICAST_MAC_ADDRESS;
  public static final Integer DOC_EXT_FIELD = DocsisTlvTypes.DOC_EXT_FIELD;
  public static final Integer VENDOR_SPECIFIC_CAP = DocsisTlvTypes.VENDOR_SPECIFIC_CAP;
  public static final Integer DS_UNENCRYPTED_TRAFFIC_FLTR =
      DocsisTlvTypes.DS_UNENCRYPTED_TRAFFIC_FLTR;
  public static final Integer TRANSMIT_CHANN_CONFIG = DocsisTlvTypes.TRANSMIT_CHANN_CONFIG;
  public static final Integer SERVICE_FLOW_SID_CLUSTER_ASSIGNMENT =
      DocsisTlvTypes.SERVICE_FLOW_SID_CLUSTER_ASSIGNMENT;
  public static final Integer RECEIVE_CHANN_PROFILE = DocsisTlvTypes.RECEIVE_CHANN_PROFILE;
  public static final Integer RECEIVE_CHANN_CONFIG = DocsisTlvTypes.RECEIVE_CHANN_CONFIG;
  public static final Integer DSID_ENCS = DocsisTlvTypes.DSID_ENCS;
  public static final Integer SECURITY_ASSOCIATION_ENC = DocsisTlvTypes.SECURITY_ASSOCIATION_ENC;
  public static final Integer INITIALIZING_CHANN_TIMEOUT =
      DocsisTlvTypes.INITIALIZING_CHANN_TIMEOUT;
  public static final Integer SNMPV1V2C_COEXIST = DocsisTlvTypes.SNMPV1V2C_COEXIST;
  public static final Integer SNMPV3_ACC_VIEW = DocsisTlvTypes.SNMPV3_ACC_VIEW;
  public static final Integer SNMP_CPE_ACC_CTRL = DocsisTlvTypes.SNMP_CPE_ACC_CTRL;
  public static final Integer CHANN_ASSIGNMENT = DocsisTlvTypes.CHANN_ASSIGNMENT;
  public static final Integer CM_INIT_REASON = DocsisTlvTypes.CM_INIT_REASON;
  public static final Integer SW_UPGRADE_IPV6_TFTP_SRV = DocsisTlvTypes.SW_UPGRADE_IPV6_TFTP_SRV;
  public static final Integer TFTP_SRV_PROVISIONED_MODEMIPV6_ADDRESS =
      DocsisTlvTypes.TFTP_SRV_PROVISIONED_MODEMIPV6_ADDRESS;
  public static final Integer US_DROP_PKT_CLASS = DocsisTlvTypes.US_DROP_PKT_CLASS;
  public static final Integer SUB_MGMT_CPE_IPV6_PREFIXLIST =
      DocsisTlvTypes.SUB_MGMT_CPE_IPV6_PREFIXLIST;
  public static final Integer US_DROP_CLASSIFIER_GROUP_ID =
      DocsisTlvTypes.US_DROP_CLASSIFIER_GROUP_ID;
  public static final Integer SUB_MGMT_CTRL_MAX_CPEIPV6_ADDRESSES =
      DocsisTlvTypes.SUB_MGMT_CTRL_MAX_CPEIPV6_ADDRESSES;
  public static final Integer CMTS_STATIC_MULTICAST_SESSION_ENC =
      DocsisTlvTypes.CMTS_STATIC_MULTICAST_SESSION_ENC;
  public static final Integer L2VPN_MAC_AGING_ENC = DocsisTlvTypes.L2VPN_MAC_AGING_ENC;
  public static final Integer MGT_EVENT_CTRL_ENC = DocsisTlvTypes.MGT_EVENT_CTRL_ENC;
  public static final Integer SUB_MGMT_CPE_IPV6_LIST = DocsisTlvTypes.SUB_MGMT_CPE_IPV6_LIST;
  public static final Integer DEFAULT_US_TARGET_BUFFER_CONFIG =
      DocsisTlvTypes.DEFAULT_US_TARGET_BUFFER_CONFIG;
  public static final Integer MAC_ADDR_LEARN_CTRL_ENC = DocsisTlvTypes.MAC_ADDR_LEARN_CTRL_ENC;
  public static final Integer US_AGGREGATE_SERVICE_FLOW = DocsisTlvTypes.US_AGGREGATE_SERVICE_FLOW;
  public static final Integer DS_AGGREGATE_SERVICEFLOW = DocsisTlvTypes.DS_AGGREGATE_SERVICEFLOW;
  public static final Integer MESP = DocsisTlvTypes.MESP;
  public static final Integer NETWORK_TIMING_PROFILE = DocsisTlvTypes.NETWORK_TIMING_PROFILE;
  public static final Integer ENERGY_MGT_PARA_ENC = DocsisTlvTypes.ENERGY_MGT_PARA_ENC;
  public static final Integer ENERGY_MGT_MODE_INDICATOR = DocsisTlvTypes.ENERGY_MGT_MODE_INDICATOR;
  public static final Integer END_OF_FILE = DocsisTlvTypes.END_OF_FILE;

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
