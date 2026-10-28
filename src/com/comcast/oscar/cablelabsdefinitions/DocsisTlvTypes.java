/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.cablelabsdefinitions;

/**
 * Named DOCSIS wire type identifiers used by codecs and MIC coverage rules. These primitive
 * compile-time constants have no dependency on compiler initialization. Registry catalogs remain
 * authoritative for per-family applicability and encoding.
 */
public final class DocsisTlvTypes {
  private DocsisTlvTypes() {}

  public static final int PAD = 0;
  public static final int DS_FREQUENCY = 1;
  public static final int US_CHANN_ID = 2;
  public static final int NETWORK_ACC_CTRL_OBJ = 3;
  public static final int DOC_10_CLASS_OF_SERV = 4;
  public static final int MODEM_CAP = 5;
  public static final int CM_MIC = 6;
  public static final int CMTS_MIC = 7;
  public static final int VENDOR_ID_ENC = 8;
  public static final int SW_UPGRADE_FILENAME = 9;
  public static final int SNMP_WRITE_ACC_CTRL = 10;
  public static final int SNMP_MIB_OBJ = 11;
  public static final int MODEM_IP_ADDR = 12;
  public static final int SRV_NOT_AVA_RSP = 13;
  public static final int CPE_ETH_MAC_ADDR = 14;
  public static final int TELEPHONE_SETTINGS_OPTION = 15;
  public static final int BPI = 17;
  public static final int MAX_CPES = 18;
  public static final int TFTP_SRV_TIMESTAMP = 19;
  public static final int TFTP_SRV_PROV_MODEM_IPV4_ADDR = 20;
  public static final int SW_UPGRADE_IPV4_TFTP_SRV = 21;
  public static final int US_PKT_CLASS = 22;
  public static final int DS_PKT_CLASS = 23;
  public static final int US_SERVICE_FLOW = 24;
  public static final int DS_SERVICE_FLOW = 25;
  public static final int PHS = 26;
  public static final int HMAC_DIGEST = 27;
  public static final int MAX_NUM_CLASS = 28;
  public static final int PRIVACY_ENABLE = 29;
  public static final int AUTHORIZATION_BLOCK = 30;
  public static final int KEY_SEQUENCE_NUMBER = 31;
  public static final int MANUFACTURER_CVC = 32;
  public static final int CO_SIGNER_CVC = 33;
  public static final int SNMPV3_KICKSTART_VALUE = 34;
  public static final int SUB_MGMT_CTRL = 35;
  public static final int SUB_MGMT_CPE_IPV4_LIST = 36;
  public static final int SUB_MGMT_FILTER_GROUPS = 37;
  public static final int SNMPV3_NOTIFICATION_RCVR = 38;
  public static final int ENABLE_20_MODE = 39;
  public static final int ENABLE_TEST_MODES = 40;
  public static final int DS_CHANN_LIST = 41;
  public static final int STATIC_MULTICAST_MAC_ADDRESS = 42;
  public static final int DOC_EXT_FIELD = 43;
  public static final int VENDOR_SPECIFIC_CAP = 44;
  public static final int DS_UNENCRYPTED_TRAFFIC_FLTR = 45;
  public static final int TRANSMIT_CHANN_CONFIG = 46;
  public static final int SERVICE_FLOW_SID_CLUSTER_ASSIGNMENT = 47;
  public static final int RECEIVE_CHANN_PROFILE = 48;
  public static final int RECEIVE_CHANN_CONFIG = 49;
  public static final int DSID_ENCS = 50;
  public static final int SECURITY_ASSOCIATION_ENC = 51;
  public static final int INITIALIZING_CHANN_TIMEOUT = 52;
  public static final int SNMPV1V2C_COEXIST = 53;
  public static final int SNMPV3_ACC_VIEW = 54;
  public static final int SNMP_CPE_ACC_CTRL = 55;
  public static final int CHANN_ASSIGNMENT = 56;
  public static final int CM_INIT_REASON = 57;
  public static final int SW_UPGRADE_IPV6_TFTP_SRV = 58;
  public static final int TFTP_SRV_PROVISIONED_MODEMIPV6_ADDRESS = 59;
  public static final int US_DROP_PKT_CLASS = 60;
  public static final int SUB_MGMT_CPE_IPV6_PREFIXLIST = 61;
  public static final int US_DROP_CLASSIFIER_GROUP_ID = 62;
  public static final int SUB_MGMT_CTRL_MAX_CPEIPV6_ADDRESSES = 63;
  public static final int CMTS_STATIC_MULTICAST_SESSION_ENC = 64;
  public static final int L2VPN_MAC_AGING_ENC = 65;
  public static final int MGT_EVENT_CTRL_ENC = 66;
  public static final int SUB_MGMT_CPE_IPV6_LIST = 67;
  public static final int DEFAULT_US_TARGET_BUFFER_CONFIG = 68;
  public static final int MAC_ADDR_LEARN_CTRL_ENC = 69;
  public static final int US_AGGREGATE_SERVICE_FLOW = 70;
  public static final int DS_AGGREGATE_SERVICEFLOW = 71;
  public static final int MESP = 72;
  public static final int NETWORK_TIMING_PROFILE = 73;
  public static final int ENERGY_MGT_PARA_ENC = 74;
  public static final int ENERGY_MGT_MODE_INDICATOR = 75;
  public static final int MANUFACTURER_CVC_CHAIN = 81;
  public static final int CO_SIGNER_CVC_CHAIN = 82;
  public static final int ESTB_CONFIGURATION = 217;
  public static final int END_OF_FILE = 255;
}
