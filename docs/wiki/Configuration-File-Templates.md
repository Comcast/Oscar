[**DOCSIS**](https://github.com/Comcast/Oscar/wiki/Configuration-File-Templates#docsis)

[**DPoE**](https://github.com/Comcast/Oscar/wiki/Configuration-File-Templates#dpoe)

[**PacketCable**](https://github.com/Comcast/Oscar/wiki/Configuration-File-Templates#packet-cable)


# DOCSIS

	 Docsis{
			 DownstreamFrequency;    /* TLV: [1]*/
			 UpstreamChannelID;      /* TLV: [2]*/
			 NetworkAccess;  /* TLV: [3]*/
			 ClassOfService {
					 ClassID;        /* TLV: [4.1]*/
					 MaxDownstreamRate;      /* TLV: [4.2]*/
					 MaxUpstreamRate;        /* TLV: [4.3]*/
					 UpstreamChannelPriority;        /* TLV: [4.4]*/
					 MinUpstreamRate;        /* TLV: [4.5]*/
					 MaxUpstreamBurst;       /* TLV: [4.6]*/
					 CoSPrivacyEnable;       /* TLV: [4.7]*/
			}
			 ModemCapabilities      {
					 ConcatenationSupport;   /* TLV: [5.1]*/
					 DocsisVersion;  /* TLV: [5.2]*/
					 FragmentationSupport;   /* TLV: [5.3]*/
					 PayloadHeaderSuppressionSupport;        /* TLV: [5.4]*/
					 IGMPSupport;    /* TLV: [5.5]*/
					 PrivacySupport;         /* TLV: [5.6]*/
					 DownstreamSaidSupport;  /* TLV: [5.7]*/
					 UpstreamServiceFlowSupport;     /* TLV: [5.8]*/
					 OptionalFilteringSupport;       /* TLV: [5.9]*/
					 TransmitPreEqualizerTapsPerModInt;      /* TLV: [5.10]*/
					 NumberOfTransmitEqualizerTaps;  /* TLV: [5.11]*/
					 DCCSupport;     /* TLV: [5.12]*/
					 IpFiltersSupport;       /* TLV: [5.13]*/
					 LLCFiltersSupport;      /* TLV: [5.14]*/
					 ExpandedUnicastSidSpace;        /* TLV: [5.15]*/
					 RangingHoldOffSupport;  /* TLV: [5.16]*/
					 L2VPNCapability;        /* TLV: [5.17]*/
					 L2VPNeSAFEHostCapability;       /* TLV: [5.18]*/
					 DUTFiltering;   /* TLV: [5.19]*/
					 UpstreamFrequencyRangeSupport;  /* TLV: [5.20]*/
					 UpstreamSymbolRateSupport;      /* TLV: [5.21]*/
					 SACMode2Support;        /* TLV: [5.22]*/
					 CodeHoppingMode2Support;        /* TLV: [5.23]*/
					 MultipleTransmitChannelSupport;         /* TLV: [5.24]*/
					 512MspsUpstreamTransmitChannel;         /* TLV: [5.25]*/
					 256MspsUpstreamTransmitChannel;         /* TLV: [5.26]*/
					 TotalSIDClusterSupport;         /* TLV: [5.27]*/
					 SIDClusterPerServiceFlowSupport;        /* TLV: [5.28]*/
					 MultipleReceiveChannelSupport;  /* TLV: [5.29]*/
					 TotalDSIDSupport;       /* TLV: [5.30]*/
					 ResequencingDSIDSupport;        /* TLV: [5.31]*/
					 MulticastDSIDSupport;   /* TLV: [5.32]*/
					 MulticastDSIDForwarding;        /* TLV: [5.33]*/
					 FrameControlTypeForwardingCapability;   /* TLV: [5.34]*/
					 DPVCapability;  /* TLV: [5.35]*/
					 UGSUpstreamSFSupport;   /* TLV: [5.36]*/
					 MAPAndUCDReceiptSupport;        /* TLV: [5.37]*/
					 UpstreamDropClassifierSupport;  /* TLV: [5.38]*/
					 IPv6Support;    /* TLV: [5.39]*/
					 ExtendedUTPCapability;  /* TLV: [5.40]*/
					 Optional8021adahMPLSClassification;     /* TLV: [5.41]*/
					 D-ONUCapabilities;      /* TLV: [5.42]*/
					 EnergyManagementCapabilities;   /* TLV: [5.44]*/
					 C-DOCSISCapability;     /* TLV: [5.45]*/
			}
			 CmMic;  /* TLV: [6]*/
			 CmtsMic;        /* TLV: [7]*/
			 VendorID;       /* TLV: [8]*/
			 SoftwareUpgradeFilename;        /* TLV: [9]*/
			 SnmpWriteAccessControl;         /* TLV: [10]*/
			 ModemIPAddress;         /* TLV: [12]*/
			 ServicesNotAvailableResponse;   /* TLV: [13]*/
			 CpeEthernetMacAddress;  /* TLV: [14]*/
			 BaselinePrivacyConfigSetting   {
					 AuthorizeWaitTimeout;   /* TLV: [17.1]*/
					 ReauthorizeWaitTimeout;         /* TLV: [17.2]*/
					 AuthorizationGraceTime;         /* TLV: [17.3]*/
					 OperationalWaitTimeout;         /* TLV: [17.4]*/
					 RekeyWaitTimeout;       /* TLV: [17.5]*/
					 TEKGraceTime;   /* TLV: [17.6]*/
					 AuthorizeRejectWaitTimeout;     /* TLV: [17.7]*/
					 SAMapWaitTimeout;       /* TLV: [17.8]*/
					 SAMapMaxRetries;        /* TLV: [17.9]*/
			}
			 MaxCPEAllowed;  /* TLV: [18]*/
			 TFTPServerTimestamp;    /* TLV: [19]*/
			 TftpServerModemIPv4Address;     /* TLV: [20]*/
			 SoftwareUpgradeIPv4TftpServer;  /* TLV: [21]*/
			 UpstreamPacketClassification   {
					 ClassifierReference;    /* TLV: [22.1]*/
					 ClassifierIdentifier;   /* TLV: [22.2]*/
					 ServiceFlowReference;   /* TLV: [22.3]*/
					 ServiceFlowIdentifier;  /* TLV: [22.4]*/
					 RulePriority;   /* TLV: [22.5]*/
					 ClassifierActivationState;      /* TLV: [22.6]*/
					 DynamicServiceChangeAction;     /* TLV: [22.7]*/
					 ClassifierError                {
							 Errored Parameter;      /* TLV: [22.8.1]*/
							 Error Code;     /* TLV: [22.8.2]*/
							 Error Message;  /* TLV: [22.8.3]*/
					}
					 IPv4PacketClassification               {
							 IPv4TypeOfServiceRangeAndMask;  /* TLV: [22.9.1]*/
							 IPProtocol;     /* TLV: [22.9.2]*/
							 IPv4SourceAddress;      /* TLV: [22.9.3]*/
							 IPv4SourceMask;         /* TLV: [22.9.4]*/
							 IPv4DestinationAddress;         /* TLV: [22.9.5]*/
							 IPv4DestinationMask;    /* TLV: [22.9.6]*/
							 TCPUDPSourcePortStart;  /* TLV: [22.9.7]*/
							 TCPUDPSourcePortEnd;    /* TLV: [22.9.8]*/
							 TCPUDPDestinationPortStart;     /* TLV: [22.9.9]*/
							 TCPUDPDestinationPortEnd;       /* TLV: [22.9.10]*/
					}
					 EthernetLLCPacketClassification                {
							 DestinationMACAddress;  /* TLV: [22.10.1]*/
							 SourceMACAddress;       /* TLV: [22.10.2]*/
							 EthertypeDSAPMacType;   /* TLV: [22.10.3]*/
					}
					 8021PQPacketClassification             {
							 8021PUserPriority;      /* TLV: [22.11.1]*/
							 8021QVLANID;    /* TLV: [22.11.2]*/
					}
					 IPv6PacketClassification               {
							 IPv6TrafficClassRangeAndMask;   /* TLV: [22.12.1]*/
							 IPv6FlowLabel;  /* TLV: [22.12.2]*/
							 IPv6NextHeaderType;     /* TLV: [22.12.3]*/
							 IPv6SourceAddress;      /* TLV: [22.12.4]*/
							 IPv6SourcePrefixLength;         /* TLV: [22.12.5]*/
							 IPv6DestinationAddress;         /* TLV: [22.12.6]*/
							 IPv6DestinationPrefixLength;    /* TLV: [22.12.7]*/
					}
					 CMInterfaceMask;        /* TLV: [22.13]*/
					 8021adSTagCTagFrameClassification              {
							 S-TPID;         /* TLV: [22.14.1]*/
							 S-VID;  /* TLV: [22.14.2]*/
							 S-PCP;  /* TLV: [22.14.3]*/
							 S-DEI;  /* TLV: [22.14.4]*/
							 C-TPID;         /* TLV: [22.14.5]*/
							 C-VID;  /* TLV: [22.14.6]*/
							 C-PCP;  /* TLV: [22.14.7]*/
							 C-CFI;  /* TLV: [22.14.8]*/
							 S-TCI;  /* TLV: [22.14.9]*/
							 C-TCI;  /* TLV: [22.14.10]*/
					}
					 8021ahPacketClassification             {
							 I-TPID;         /* TLV: [22.15.1]*/
							 I-SID;  /* TLV: [22.15.2]*/
							 I-TCI;  /* TLV: [22.15.3]*/
							 I-PCP;  /* TLV: [22.15.4]*/
							 I-DEI;  /* TLV: [22.15.5]*/
							 I-UCA;  /* TLV: [22.15.6]*/
							 B-TPID;         /* TLV: [22.15.7]*/
							 B-TCI;  /* TLV: [22.15.8]*/
							 B-PCP;  /* TLV: [22.15.9]*/
							 B-DEI;  /* TLV: [22.15.10]*/
							 B-VID;  /* TLV: [22.15.11]*/
							 B-DA;   /* TLV: [22.15.12]*/
							 B-SA;   /* TLV: [22.15.13]*/
					}
					 ICMPv4ICMPv6PacketClassification               {
							 ICMPv4ICMPv6TypeStart;  /* TLV: [22.16.1]*/
							 ICMPv4ICMPv6TypeEnd;    /* TLV: [22.16.2]*/
					}
					 MPLSClassification             {
							 MPLSTC;         /* TLV: [22.17.1]*/
							 MPLSLabel;      /* TLV: [22.17.2]*/
					}
					 VendorSpecificClassifierParameters;     /* TLV: [22.43]*/
			}
			 DownstreamPacketClassification {
					 ClassifierReference;    /* TLV: [23.1]*/
					 ClassifierIdentifier;   /* TLV: [23.2]*/
					 ServiceFlowReference;   /* TLV: [23.3]*/
					 ServiceFlowIdentifier;  /* TLV: [23.4]*/
					 RulePriority;   /* TLV: [23.5]*/
					 ClassifierActivationState;      /* TLV: [23.6]*/
					 DynamicServiceChangeAction;     /* TLV: [23.7]*/
					 ClassifierError;        /* TLV: [23.8]*/
					 IPv4PacketClassification               {
							 IPv4TypeOfServiceRangeAndMask;  /* TLV: [23.9.1]*/
							 IPProtocol;     /* TLV: [23.9.2]*/
							 IPv4SourceAddress;      /* TLV: [23.9.3]*/
							 IPv4SourceMask;         /* TLV: [23.9.4]*/
							 IPv4DestinationAddress;         /* TLV: [23.9.5]*/
							 IPv4DestinationMask;    /* TLV: [23.9.6]*/
							 TCPUDPSourcePortStart;  /* TLV: [23.9.7]*/
							 TCPUDPSourcePortEnd;    /* TLV: [23.9.8]*/
							 TCPUDPDestinationPortStart;     /* TLV: [23.9.9]*/
							 TCPUDPDestinationPortEnd;       /* TLV: [23.9.10]*/
					}
					 EthernetLLCPacketClassification                {
							 DestinationMACAddress;  /* TLV: [23.10.1]*/
							 SourceMACAddress;       /* TLV: [23.10.2]*/
							 EthertypeDSAPMacType;   /* TLV: [23.10.3]*/
					}
					 IEEE8021PQPacketClassification         {
							 IEEE8021PUserPriority;  /* TLV: [23.11.1]*/
							 IEEE8021QVLANID;        /* TLV: [23.11.2]*/
					}
					 IPv6PacketClassification               {
							 IPv6TrafficClassRangeAndMask;   /* TLV: [23.12.1]*/
							 IPv6FlowLabel;  /* TLV: [23.12.2]*/
							 IPv6NextHeaderType;     /* TLV: [23.12.3]*/
							 IPv6SourceAddress;      /* TLV: [23.12.4]*/
							 IPv6SourcePrefixLength;         /* TLV: [23.12.5]*/
							 IPv6DestinationAddress;         /* TLV: [23.12.6]*/
							 IPv6DestinationPrefixLength;    /* TLV: [23.12.7]*/
					}
					 8021adSTagCTagFrameClassification              {
							 S-TPID;         /* TLV: [23.14.1]*/
							 S-VID;  /* TLV: [23.14.2]*/
							 S-PCP;  /* TLV: [23.14.3]*/
							 S-DEI;  /* TLV: [23.14.4]*/
							 C-TPID;         /* TLV: [23.14.5]*/
							 C-VID;  /* TLV: [23.14.6]*/
							 C-PCP;  /* TLV: [23.14.7]*/
							 C-CFI;  /* TLV: [23.14.8]*/
							 S-TCI;  /* TLV: [23.14.9]*/
							 C-TCI;  /* TLV: [23.14.10]*/
					}
					 8021ahPacketClassification             {
							 I-TPID;         /* TLV: [23.15.1]*/
							 I-SID;  /* TLV: [23.15.2]*/
							 I-TCI;  /* TLV: [23.15.3]*/
							 I-PCP;  /* TLV: [23.15.4]*/
							 I-DEI;  /* TLV: [23.15.5]*/
							 I-UCA;  /* TLV: [23.15.6]*/
							 B-TPID;         /* TLV: [23.15.7]*/
							 B-TCI;  /* TLV: [23.15.8]*/
							 B-PCP;  /* TLV: [23.15.9]*/
							 B-DEI;  /* TLV: [23.15.10]*/
							 B-VID;  /* TLV: [23.15.11]*/
							 B-DA;   /* TLV: [23.15.12]*/
							 B-SA;   /* TLV: [23.15.13]*/
					}
					 ICMPv4ICMPv6PacketClassification               {
							 ICMPv4ICMPv6TypeStart;  /* TLV: [23.16.1]*/
							 ICMPv4ICMPv6TypeEnd;    /* TLV: [23.16.2]*/
					}
					 MPLSClassification             {
							 MPLSTC;         /* TLV: [23.17.1]*/
							 MPLSLabel;      /* TLV: [23.17.2]*/
					}
					 VendorSpecificClassifierParameters;     /* TLV: [23.43]*/
			}
			 UpstreamServiceFlow    {
					 ServiceFlowReference;   /* TLV: [24.1]*/
					 ServiceFlowIdentifier;  /* TLV: [24.2]*/
					 ServiceIdentifier;      /* TLV: [24.3]*/
					 ServiceClassName;       /* TLV: [24.4]*/
					 ServiceFlowError               {
							 ErroredParameter;       /* TLV: [24.5.1]*/
							 ErrorCode;      /* TLV: [24.5.2]*/
							 ErrorMessage;   /* TLV: [24.5.3]*/
					}
					 QualityOfServiceParameterSetType;       /* TLV: [24.6]*/
					 TrafficPriority;        /* TLV: [24.7]*/
					 MaxSubstainedTrafficRate;       /* TLV: [24.8]*/
					 MaxTrafficBurst;        /* TLV: [24.9]*/
					 MinReservedTrafficRate;         /* TLV: [24.10]*/
					 AssumedMinReservedRatePacketSize;       /* TLV: [24.11]*/
					 TimeoutForActiveQoS;    /* TLV: [24.12]*/
					 TimeoutForAdmittedQoS;  /* TLV: [24.13]*/
					 MaxConcatenatedBurst;   /* TLV: [24.14]*/
					 SchedulingType;         /* TLV: [24.15]*/
					 RequestTransmissionPolicy;      /* TLV: [24.16]*/
					 NominalPollingInterval;         /* TLV: [24.17]*/
					 ToleratedPollJitter;    /* TLV: [24.18]*/
					 UnsolicitedGrantSize;   /* TLV: [24.19]*/
					 NominalGrantInterval;   /* TLV: [24.20]*/
					 ToleratedGrantJitter;   /* TLV: [24.21]*/
					 GrantsPerInterval;      /* TLV: [24.22]*/
					 IPTypeOfServiceDSCPOverwrite;   /* TLV: [24.23]*/
					 UnsolicitedGrantTimeReference;  /* TLV: [24.24]*/
					 MultiplierContentionBackoffWindow;      /* TLV: [24.25]*/
					 MultiplierNumberOfBytesRequested;       /* TLV: [24.26]*/
					 UpstreamPeakTrafficRate;        /* TLV: [24.27]*/
					 ServiceFlowRequiredAttrMask;    /* TLV: [24.31]*/
					 ServiceFlowForbiddenAttrMask;   /* TLV: [24.32]*/
					 ServiceFlowAttrAggregationRuleMask;     /* TLV: [24.33]*/
					 ApplicationIdentifier;  /* TLV: [24.34]*/
					 UpstreamBufferControl          {
							 MinBuffer;      /* TLV: [24.35.1]*/
							 TargetBuffer;   /* TLV: [24.35.2]*/
							 MaxBuffer;      /* TLV: [24.35.3]*/
					}
					 AggregateServiceFlowReference;  /* TLV: [24.36]*/
					 MESPReference;  /* TLV: [24.37]*/
					 VendorSpecificQoS;      /* TLV: [24.43]*/
			}
			 DownstreamServiceFlow  {
					 ServiceFlowReference;   /* TLV: [25.1]*/
					 ServiceFlowIdentifier;  /* TLV: [25.2]*/
					 ServiceIdentifier;      /* TLV: [25.3]*/
					 ServiceClassName;       /* TLV: [25.4]*/
					 ServiceFlowError               {
							 ErroredParameter;       /* TLV: [25.5.1]*/
							 ErrorCode;      /* TLV: [25.5.2]*/
							 ErrorMessage;   /* TLV: [25.5.3]*/
					}
					 QualityOfServiceParameterSetType;       /* TLV: [25.6]*/
					 TrafficPriority;        /* TLV: [25.7]*/
					 MaxSubstainedTrafficRate;       /* TLV: [25.8]*/
					 MaxTrafficBurst;        /* TLV: [25.9]*/
					 MinReservedTrafficRate;         /* TLV: [25.10]*/
					 AssumedMinReservedRatePacketSize;       /* TLV: [25.11]*/
					 TimeoutForActiveQoS;    /* TLV: [25.12]*/
					 TimeoutForAdmittedQoS;  /* TLV: [25.13]*/
					 DownstreamResequencing;         /* TLV: [25.17]*/
					 IPTypeOfServiceDSCPOverwrite;   /* TLV: [25.23]*/
					 DownstreamPeakTrafficRate;      /* TLV: [25.27]*/
					 ServiceFlowRequiredAttrMask;    /* TLV: [25.31]*/
					 ServiceFlowForbiddenAttrMask;   /* TLV: [25.32]*/
					 ServiceFlowAttrAggregationRuleMask;     /* TLV: [25.33]*/
					 ApplicationIdentifier;  /* TLV: [25.34]*/
					 DownstreamBufferControl                {
							 MinBuffer;      /* TLV: [25.35.1]*/
							 TargetBuffer;   /* TLV: [25.35.2]*/
							 MaxBuffer;      /* TLV: [25.35.3]*/
					}
					 AggregateServiceFlowReference;  /* TLV: [25.36]*/
					 MESPReference;  /* TLV: [25.37]*/
					 VendorSpecificQoS;      /* TLV: [25.43]*/
					 MaximumDownstreamLatency;       /* TLV: [25.14]*/
			}
			 PayloadHeaderSuppression       {
					 ClassifierReference;    /* TLV: [26.1]*/
					 ClassifierIdentifier;   /* TLV: [26.2]*/
					 ServiceFlowReference;   /* TLV: [26.3]*/
					 ServiceFlowIdentifier;  /* TLV: [26.4]*/
					 DynamicServiceChangeAction;     /* TLV: [26.5]*/
					 PayloadHeaderSuppressionError          {
							 ErroredParameter;       /* TLV: [26.6.1]*/
							 ErrorCode;      /* TLV: [26.6.2]*/
							 ErrorMessage;   /* TLV: [26.6.3]*/
					}
					 PayloadHeaderSuppressionField;  /* TLV: [26.7]*/
					 PayloadHeaderSuppressionIndex;  /* TLV: [26.8]*/
					 PayloadHeaderSuppressionMask;   /* TLV: [26.9]*/
					 PayloadHeaderSuppressionSize;   /* TLV: [26.10]*/
					 PayloadHeaderSuppressionVerification;   /* TLV: [26.11]*/
					 DynamicBondingChangeAction;     /* TLV: [26.23]*/
					 VendorSpecificPHSParameters;    /* TLV: [26.43]*/
			}
			 HMAC-Digest;    /* TLV: [27]*/
			 MaxClassifiers;         /* TLV: [28]*/
			 PrivacyEnable;  /* TLV: [29]*/
			 AuthorizationBlock;     /* TLV: [30]*/
			 KeySequenceNumber;      /* TLV: [31]*/
			 ManufacturerCVC;        /* TLV: [32]*/
			 Co-signerCVC;   /* TLV: [33]*/
			 SNMPv3KickstartValue   {
					 SNMPv3KickstartSecurityName;    /* TLV: [34.1]*/
					 SNMPv3KickstartManagerPublicNumber;     /* TLV: [34.2]*/
			}
			 SubMgmtControl;         /* TLV: [35]*/
			 SubscirberManagementCPEIPv4List;        /* TLV: [36]*/
			 SubMgmtFilterGroups;    /* TLV: [37]*/
			 SNMPv3NotificationReceiver     {
					 SNMPv3NRIPv4Address;    /* TLV: [38.1]*/
					 SNMPv3NRUdpPortNumber;  /* TLV: [38.2]*/
					 SNMPv3NRTrapType;       /* TLV: [38.3]*/
					 SNMPv3NRTimeout;        /* TLV: [38.4]*/
					 SNMPv3NRRetries;        /* TLV: [38.5]*/
					 SNMPv3NRFilteringParameters;    /* TLV: [38.6]*/
					 SNMPv3NRSecurityName;   /* TLV: [38.7]*/
					 SNMPv3NRIPv6Address;    /* TLV: [38.8]*/
			}
			 Enable20Mode;   /* TLV: [39]*/
			 EnableTestModes;        /* TLV: [40]*/
			 DownstreamChannelList  {
					 SingleDownstreamChannel                {
							 SingleDownstreamChannelTimeout;         /* TLV: [41.1.1]*/
							 SingleDownstreamChannelFrequency;       /* TLV: [41.1.2]*/
					}
					 DownstreamFrequencyRange               {
							 DownstreamFrequencyRangeTimeout;        /* TLV: [41.2.1]*/
							 DownstreamFrequencyRangeStart;  /* TLV: [41.2.2]*/
							 DownstreamFrequencyRangeEnd;    /* TLV: [41.2.3]*/
							 DownstreamFrequencyRangeStepSize;       /* TLV: [41.2.4]*/
					}
					 DefaultScanning;        /* TLV: [41.3]*/
			}
			 StaticMulticastMACAddress;      /* TLV: [42]*/
			 VendorSpecific {
					 CMLoadBalancingID;      /* TLV: [43.1]*/
					 CMLoadBalancingPriority;        /* TLV: [43.2]*/
					 CMLoadBalancingGroupID;         /* TLV: [43.3]*/
					 CMRangingClassIDExt;    /* TLV: [43.4]*/
					 L2VPN;  /* TLV: [43.5]*/
					 ExtCmtsMicConf         {
							 ExtCmtsMicHMACType;     /* TLV: [43.6.1]*/
							 ExtCmtsMicBitmap;       /* TLV: [43.6.2]*/
							 ExpExtCmtsMicDigest;    /* TLV: [43.6.3]*/
					}
					 SAVAuthorization               {
							 SAVGroupName;   /* TLV: [43.7.1]*/
							 SAVStaticPrefixRule                    {
									 SAVStaticPrefixAddr;    /* TLV: [43.7.2.1]*/
									 SAVStaticPrefixLength;  /* TLV: [43.7.2.2]*/
							}
					}
					 CMAttributeMask                {
							 CMReqDownstreamAttrMask;        /* TLV: [43.9.1]*/
							 CMReqUpstreamAttrMask;  /* TLV: [43.9.1]*/
							 CMDownstreamForAttrMask;        /* TLV: [43.9.2]*/
							 CMUpstreamForAttrMask;  /* TLV: [43.9.2]*/
					}
					 IPMulticastJoinAuth            {
							 IPMulticastProfileName;         /* TLV: [43.10.1]*/
							 IPMulticastJoinAuthStaticSession                       {
									 RulePriority;   /* TLV: [43.10.2.1]*/
									 AuthorizationAction;    /* TLV: [43.10.2.2]*/
									 SourcePrefixAddress;    /* TLV: [43.10.2.3]*/
									 SourcePrefixLength;     /* TLV: [43.10.2.4]*/
									 GroupPrefixAddress;     /* TLV: [43.10.2.5]*/
									 GroupPrefixLength;      /* TLV: [43.10.2.6]*/
							}
							 MaxMulticastSessions;   /* TLV: [43.10.3]*/
					}
					 ServiceTypeIdentifier;  /* TLV: [43.11]*/
					 DAC;    /* TLV: [43.12]*/
			}
			 Vendor-SpecificCapabilites;     /* TLV: [44]*/
			 TransmitChannelConfig  {
					 TCCReference;   /* TLV: [46.1]*/
					 UpstreamChannelAction;  /* TLV: [46.2]*/
					 UpstreamChannelID;      /* TLV: [46.3]*/
					 NewUpstreamChannelID;   /* TLV: [46.4]*/
					 UCD;    /* TLV: [46.5]*/
					 RangingSID;     /* TLV: [46.6]*/
					 InitializationTechnique;        /* TLV: [46.7]*/
					 RangingParameter               {
							 RangingReferenceChannelID;      /* TLV: [46.8.1]*/
							 TimingOffsetIntegerPart;        /* TLV: [46.8.2]*/
							 TimingOffsetFractionalPart;     /* TLV: [46.8.3]*/
							 PowerOffset;    /* TLV: [46.8.4]*/
							 FrequencyOffset;        /* TLV: [46.8.5]*/
					}
					 DynamicRangeWindow;     /* TLV: [46.9]*/
					 TCCError               {
							 ReportedParameter;      /* TLV: [46.254.1]*/
							 ErrorCode;      /* TLV: [46.254.2]*/
							 ErrorMessage;   /* TLV: [46.254.3]*/
					}
			}
			 ServiceFlowSIDClusterAssignments       {
					 SFID;   /* TLV: [47.1]*/
					 SIDCluster             {
							 SIDClusterID;   /* TLV: [47.2.1]*/
							 SID-to-ChannelMapping                  {
									 UpstreamChannelID;      /* TLV: [47.2.2.1]*/
									 SID;    /* TLV: [47.2.2.2]*/
									 Action;         /* TLV: [47.2.2.3]*/
							}
					}
					 SIDClusterSwitchoverCriteria           {
							 MaxRequestsPerSIDCluster;       /* TLV: [47.3.1]*/
							 MaxOutstandingBytesPerSIDCluster;       /* TLV: [47.3.2]*/
							 MaxTotalBytesRequestedPerSIDCluster;    /* TLV: [47.3.3]*/
							 MaxTimeInSIDCluster;    /* TLV: [47.3.4]*/
					}
			}
			 CMReceiveChannelRCP    {
					 RCP-ID;         /* TLV: [48.1]*/
					 RCPName;        /* TLV: [48.2]*/
					 RCPCenterFrequencySpacing;      /* TLV: [48.3]*/
					 ReceiveModule          {
							 ReceiveModuleIndex;     /* TLV: [48.4.1]*/
							 ReceiveModuleAdjacentChannels;  /* TLV: [48.4.2]*/
							 ReceiveModuleChannelBlockRange                 {
									 ReceiveModuleMinCenterFrequency;        /* TLV: [48.4.3.1]*/
									 ReceiveModuleMaxCenterFrequency;        /* TLV: [48.4.3.2]*/
							}
							 FirstChannelCenterFrequencyAssign;      /* TLV: [48.4.4]*/
							 ResequencingChannelSubsetCapability;    /* TLV: [48.4.5]*/
							 ReceiveModuleConnectivity;      /* TLV: [48.4.6]*/
							 CommonPhysicalLayerParameter;   /* TLV: [48.4.7]*/
					}
					 ReceiveChannels                {
							 ReceiveChannelIndex;    /* TLV: [48.5.1]*/
							 ReceiveChannelConnectivity;     /* TLV: [48.5.2]*/
							 ReceiveChannelConnectedOffset;  /* TLV: [48.5.3]*/
							 PrimaryDownstreamChannelIndicator;      /* TLV: [48.5.5]*/
					}
					 ProfileConfigVendorSpecificParameters;  /* TLV: [48.43]*/
			}
			 CMReceiveChannelRCC    {
					 RCP-ID;         /* TLV: [49.1]*/
					 ReceiveModule          {
							 ReceiveModuleIndex;     /* TLV: [49.4.1]*/
							 ReceiveModuleConnectivity;      /* TLV: [49.4.6]*/
					}
					 ReceiveChannels                {
							 ReceiveChannelIndex;    /* TLV: [49.5.1]*/
							 ReceiveChannelConnectivity;     /* TLV: [49.5.2]*/
							 CenterFrequencyAssignment;      /* TLV: [49.5.4]*/
							 PrimaryDownstreamChannelIndicator;      /* TLV: [49.5.5]*/
					}
					 PartialServiceDownstreamChannels;       /* TLV: [49.6]*/
					 ProfileConfigVendorSpecificParameters;  /* TLV: [49.43]*/
					 RCCError               {
							 ReceiveModuleOrReceiveChannel;  /* TLV: [49.254.1]*/
							 ReceiveModuleOrReceiveChannelIndex;     /* TLV: [49.254.2]*/
							 ReportedParameter;      /* TLV: [49.254.3]*/
							 ErrorCode;      /* TLV: [49.254.4]*/
							 ErrorMessage;   /* TLV: [49.254.5]*/
					}
			}
			 DSID   {
					 DSID;   /* TLV: [50.1]*/
					 DSIDAction;     /* TLV: [50.2]*/
					 DownstreamResequencing         {
							 ResequencingDSID;       /* TLV: [50.3.1]*/
							 DownstreamResequencingChannelList;      /* TLV: [50.3.2]*/
							 DSIDResequencingWaitTime;       /* TLV: [50.3.3]*/
							 ResequencingWarningThreshold;   /* TLV: [50.3.4]*/
							 CMStatusMaxEventHoldOffTimer;   /* TLV: [50.3.5]*/
					}
					 Multicast              {
							 ClientMACAddress                       {
									 ClientMACAddressAction;         /* TLV: [50.4.1.1]*/
									 ClientMACAddress;       /* TLV: [50.4.1.2]*/
							}
							 MulticastCMInterfaceMask;       /* TLV: [50.4.2]*/
							 GroupMACAddresses;      /* TLV: [50.4.3]*/
							 PayloadHeaderSuppression;       /* TLV: [50.4.26]*/
					}
			}
			 SecurityAssociation    {
					 SAAction;       /* TLV: [51.1]*/
					 SA-Descriptor;  /* TLV: [51.23]*/
			}
			 InitializingChannelTimeout;     /* TLV: [52]*/
			 SNMPv1v2cCoexistenceConfig     {
					 SNMPv1v2cCommunityName;         /* TLV: [53.1]*/
					 SNMPv1v2cTransportAddressAccess                {
							 SNMPv1v2cTransportAddress;      /* TLV: [53.2.1]*/
							 SNMPv1v2cTransportMask;         /* TLV: [53.2.2]*/
					}
					 SNMPv1v2cAccessViewType;        /* TLV: [53.3]*/
					 SNMPv1v2cAccessViewName;        /* TLV: [53.4]*/
			}
			 SNMPv3AccessViewConfig {
					 SNMPv3AccessViewName;   /* TLV: [54.1]*/
					 SNMP3AccessViewSubtree;         /* TLV: [54.2]*/
					 SNMPv3AccessViewMask;   /* TLV: [54.3]*/
					 SNMPv3AccessViewType;   /* TLV: [54.4]*/
			}
			 SnmpCpeAccessControl;   /* TLV: [55]*/
			 ChannelAssignmentConfigSettings        {
					 TransmitCAConfigSettings;       /* TLV: [56.1]*/
					 ReceiveCAConfigSettings;        /* TLV: [56.2]*/
			}
			 CMInitializationReason;         /* TLV: [57]*/
			 SoftwareUpgradeIPv6TftpServer;  /* TLV: [58]*/
			 TFTPServerModemIPv6Addr;        /* TLV: [59]*/
			 UpstreamDropPacketClassification       {
					 ClassifierReference;    /* TLV: [60.1]*/
					 ClassifierIdentifier;   /* TLV: [60.2]*/
					 RulePriority;   /* TLV: [60.5]*/
					 DynamicServiceChangeAction;     /* TLV: [60.7]*/
					 ClassifierError;        /* TLV: [60.8]*/
					 IPv4PacketClassification               {
							 IPv4TypeOfServiceRangeAndMask;  /* TLV: [60.9.1]*/
							 IPProtocol;     /* TLV: [60.9.2]*/
							 IPv4SourceAddress;      /* TLV: [60.9.3]*/
							 IPv4SourceMask;         /* TLV: [60.9.4]*/
							 IPv4DestinationAddress;         /* TLV: [60.9.5]*/
							 IPv4DestinationMask;    /* TLV: [60.9.6]*/
							 TCPUDPSourcePortStart;  /* TLV: [60.9.7]*/
							 TCPUDPSourcePortEnd;    /* TLV: [60.9.8]*/
							 TCPUDPDestinationPortStart;     /* TLV: [60.9.9]*/
							 TCPUDPDestinationPortEnd;       /* TLV: [60.9.10]*/
					}
					 EthernetLLCPacketClassification                {
							 DestinationMACAddress;  /* TLV: [60.10.1]*/
							 SourceMACAddress;       /* TLV: [60.10.2]*/
							 EthertypeDSAPMacType                   {
									 8021PQPacketClassification                             {
											 8021PUserPriority;      /* TLV: [60.10.3.11.1]*/
											 8021QVLANID;    /* TLV: [60.10.3.11.2]*/
									}
							}
					}
					 IPv6PacketClassification               {
							 IPv6TrafficClassRangeAndMask;   /* TLV: [60.12.1]*/
							 IPv6FlowLabel;  /* TLV: [60.12.2]*/
							 IPv6NextHeaderType;     /* TLV: [60.12.3]*/
							 IPv6SourceAddress;      /* TLV: [60.12.4]*/
							 IPv6SourcePrefixLength;         /* TLV: [60.12.5]*/
							 IPv6DestinationAddress;         /* TLV: [60.12.6]*/
							 IPv6DestinationPrefixLength;    /* TLV: [60.12.7]*/
					}
					 CMInterfaceMask;        /* TLV: [60.13]*/
					 8021adSTagCTagFrameClassification              {
							 S-TPID;         /* TLV: [60.14.1]*/
							 S-VID;  /* TLV: [60.14.2]*/
							 S-PCP;  /* TLV: [60.14.3]*/
							 S-DEI;  /* TLV: [60.14.4]*/
							 C-TPID;         /* TLV: [60.14.5]*/
							 C-VID;  /* TLV: [60.14.6]*/
							 C-PCP;  /* TLV: [60.14.7]*/
							 C-CFI;  /* TLV: [60.14.8]*/
							 S-TCI;  /* TLV: [60.14.9]*/
							 C-TCI;  /* TLV: [60.14.10]*/
					}
					 8021ahPacketClassification             {
							 I-TPID;         /* TLV: [60.15.1]*/
							 I-SID;  /* TLV: [60.15.2]*/
							 I-TCI;  /* TLV: [60.15.3]*/
							 I-PCP;  /* TLV: [60.15.4]*/
							 I-DEI;  /* TLV: [60.15.5]*/
							 I-UCA;  /* TLV: [60.15.6]*/
							 B-TPID;         /* TLV: [60.15.7]*/
							 B-TCI;  /* TLV: [60.15.8]*/
							 B-PCP;  /* TLV: [60.15.9]*/
							 B-DEI;  /* TLV: [60.15.10]*/
							 B-VID;  /* TLV: [60.15.11]*/
							 B-DA;   /* TLV: [60.15.12]*/
							 B-SA;   /* TLV: [60.15.13]*/
					}
					 ICMPv4ICMPv6PacketClassification               {
							 ICMPv4ICMPv6TypeStart;  /* TLV: [60.16.1]*/
							 ICMPv4ICMPv6TypeEnd;    /* TLV: [60.16.2]*/
					}
					 MPLSClassification             {
							 MPLSTC;         /* TLV: [60.17.1]*/
							 MPLSLabel;      /* TLV: [60.17.2]*/
					}
					 VendorSpecificClassifierParameters;     /* TLV: [60.43]*/
			}
			 SubMgmtCPEIPv6PrefixList;       /* TLV: [61]*/
			 UpstreamDropClassifierGroupID;  /* TLV: [62]*/
			 SubMgmtControlMaxCPEIPv6Addr;   /* TLV: [63]*/
			 CMTSStaticMulticastSession     {
					 StaticMulticastGroup;   /* TLV: [64.1]*/
					 StaticMulticastSource;  /* TLV: [64.2]*/
					 StaticMulticastCMIM;    /* TLV: [64.3]*/
			}
			 L2VPNMacAging;  /* TLV: [65]*/
			 MgmtEventControlEnconding;      /* TLV: [66]*/
			 SubMgmtCPEIPv6List;     /* TLV: [67]*/
			 DefaultUpstreamTargetBufferConfig      {
					 MacAgingMode;   /* TLV: [68.1]*/
			}
			 MacAddressLearningControl      {
					 MacAddressLearningControl;      /* TLV: [69.1]*/
					 MacAddressLearningHoldoffTimer;         /* TLV: [69.2]*/
			}
			 UpstreamAggregateSF    {
					 ServiceFlowReference;   /* TLV: [70.1]*/
					 AggregateServiceFlowReference;  /* TLV: [70.36]*/
					 MESPReference;  /* TLV: [70.37]*/
			}
			 DownstreamAggregateSF  {
					 ServiceFlowReference;   /* TLV: [71.1]*/
					 AggregateServiceFlowReference;  /* TLV: [71.36]*/
					 MESPReference;  /* TLV: [71.37]*/
			}
			 MetroEthernetServiceProfile    {
					 MESPReference;  /* TLV: [72.1]*/
					 MESPBandwidthProfile           {
							 MESP-BPCommittedInformationRate;        /* TLV: [72.2.1]*/
							 MESP-BPCommittedBurstSize;      /* TLV: [72.2.2]*/
							 MESP-BPExcessInformationRate;   /* TLV: [72.2.3]*/
							 MESP-BPExcessBurstSize;         /* TLV: [72.2.4]*/
							 MESP-BPCouplingFlag;    /* TLV: [72.2.5]*/
							 MESP-BPColorMode                       {
									 MESP-BP-CMColorIdentificationField;     /* TLV: [72.2.6.1]*/
									 MESP-BP-CMColorIdentFieldValue;         /* TLV: [72.2.6.2]*/
							}
							 MESP-BPColorMarking                    {
									 MESP-BP-CRColorMarkingField;    /* TLV: [72.2.7.1]*/
									 MESP-BP-CRColorMarkingFieldValue;       /* TLV: [72.2.7.2]*/
							}
					}
					 MESPName;       /* TLV: [72.3]*/
			}
			 NetworkTimingProfile   {
					 NetworkTimingProfileReference;  /* TLV: [73.1]*/
					 NetworkTimingProfileName;       /* TLV: [73.2]*/
			}
			 EnergyMgmtParameter    {
					 EnergyMgmtFeatureControl;       /* TLV: [74.1]*/
					 EnergyMgmt1x1ModeEncondings            {
							 DownstreamActivityDetection                    {
									 DownstreamEntryBitrateThreshold;        /* TLV: [74.2.1.1]*/
									 DownstreamEntryTimeThreshold;   /* TLV: [74.2.1.2]*/
									 DownstreamExitBitrateThreshold;         /* TLV: [74.2.1.3]*/
									 DownstreamExitTimeThreshold;    /* TLV: [74.2.1.4]*/
							}
							 UpstreamActivityDetection                      {
									 UpstreamEntryBitrateThreshold;  /* TLV: [74.2.2.1]*/
									 UpstreamEntryTimeThreshold;     /* TLV: [74.2.2.2]*/
									 UpstreamExitBitrateThreshold;   /* TLV: [74.2.2.3]*/
									 UpstreamExitTimeThreshold;      /* TLV: [74.2.2.4]*/
							}
					}
					 EnergyMgmtCyclePeriod;  /* TLV: [74.3]*/
			}
			 EnergyManagement1x1ModeIndicator;       /* TLV: [75]*/
			 ePS;    /* TLV: [201]*/
			 eRouterConfig  {
					 InitializationMode;     /* TLV: [202.1]*/
					 TR-069ManagementServer         {
							 EnableCWMP;     /* TLV: [202.2.1]*/
							 URL;    /* TLV: [202.2.2]*/
							 Username;       /* TLV: [202.2.3]*/
							 Password;       /* TLV: [202.2.4]*/
							 ConnectionRequestUsername;      /* TLV: [202.2.5]*/
							 ConnectionRequestPassword;      /* TLV: [202.2.6]*/
							 ACSOverride;    /* TLV: [202.2.7]*/
					}
					 InitializationModeOverride;     /* TLV: [202.3]*/
					 RouterAdvertisementTransInterval;       /* TLV: [202.10]*/
					 TopologyMode;   /* TLV: [202.42]*/
					 VendorSpecificInformation              {
							 VendorID;       /* TLV: [202.43.8]*/
							 SnmpMibObject;  /* TLV: [202.43.11]*/
					}
					 SNMPv1v2cCoexistenceConfig             {
							 SNMPv1v2cCommunityName;         /* TLV: [202.53.1]*/
							 SNMPv1v2cTransportAddressAccess                        {
									 SNMPv1v2cTransportAddress;      /* TLV: [202.53.2.1]*/
									 SNMPv1v2cTransportMask;         /* TLV: [202.53.2.2]*/
							}
							 SNMPv1v2cAccessViewType;        /* TLV: [202.53.3]*/
							 SNMPv1v2cAccessViewName;        /* TLV: [202.53.4]*/
					}
					 SNMPv3AccessViewConfig         {
							 SNMPv3AccessViewName;   /* TLV: [202.54.1]*/
							 SNMPv3AccessViewSubtree;        /* TLV: [202.54.2]*/
							 SNMPv3AccessViewMask;   /* TLV: [202.54.3]*/
							 SNMPv3AccessViewType;   /* TLV: [202.54.4]*/
					}
			}
			 eMTAConfig;     /* TLV: [216]*/
			 eSTBConfig     {
					 IPModeControl;  /* TLV: [217.1]*/
					 VendorID;       /* TLV: [217.8]*/
					 SnmpOID;        /* TLV: [217.11]*/
					 SNMPv3NotificationReceiver             {
							 IPv4Address;    /* TLV: [217.38.1]*/
							 UDPPortNumber;  /* TLV: [217.38.2]*/
							 TrapType;       /* TLV: [217.38.3]*/
							 Timeout;        /* TLV: [217.38.4]*/
							 Retries;        /* TLV: [217.38.5]*/
							 SnmpFilteringParameters;        /* TLV: [217.38.6]*/
							 SecurityName;   /* TLV: [217.38.7]*/
							 IPv6Address;    /* TLV: [217.38.8]*/
					}
					 HomeNetworkPrefixValidation            {
							 InstanceNumber;         /* TLV: [217.39.1]*/
							 PrefixUsage;    /* TLV: [217.39.2]*/
							 IPAddressVersion;       /* TLV: [217.39.3]*/
							 IPv4PrefixLength;       /* TLV: [217.39.4]*/
							 IPv4SubnetAddress;      /* TLV: [217.39.5]*/
							 IPv6PrefixLength;       /* TLV: [217.39.6]*/
							 IPv6NetworkAddress;     /* TLV: [217.39.7]*/
					}
					 SEBServerEnableTLSCipherSuites;         /* TLV: [217.40]*/
					 VendorSpecificInformation;      /* TLV: [217.43]*/
					 SNMPv1v2cCoexistenceConfig             {
							 SNMPv1v2cCommunityName;         /* TLV: [217.53.1]*/
							 SNMPv1v2cTransportAddressAccess                        {
									 SNMPv1v2cTransportAddress;      /* TLV: [217.53.2.1]*/
									 SNMPv1v2cTransportMask;         /* TLV: [217.53.2.2]*/
							}
							 SNMPv1v2cAccessViewType;        /* TLV: [217.53.3]*/
							 SNMPv1v2cAccessViewName;        /* TLV: [217.53.4]*/
					}
					 SNMPv3AccessViewConfig         {
							 SNMPv3AccessViewName;   /* TLV: [217.54.1]*/
							 SNMPv3AccessViewSubtree;        /* TLV: [217.54.2]*/
							 SNMPv3AccessViewMask;   /* TLV: [217.54.3]*/
							 SNMPv3AccessViewType;   /* TLV: [217.54.4]*/
					}
			}
			 eTEAConfig;     /* TLV: [219]*/
			 eDVAConfig;     /* TLV: [220]*/
			 eSGConfig;      /* TLV: [221]*/
	}



# DPoE

	 DPoE{
		 NetworkAccess;	 /* TLV: [3]*/
		 ModemCapabilities	{
			 ConcatenationSupport;	 /* TLV: [5.1]*/
			 DocsisVersion;	 /* TLV: [5.2]*/
			 FragmentationSupport;	 /* TLV: [5.3]*/
			 PayloadHeaderSuppressionSupport;	 /* TLV: [5.4]*/
			 IGMPSupport;	 /* TLV: [5.5]*/
			 PrivacySupport;	 /* TLV: [5.6]*/
			 DownstreamSaidSupport;	 /* TLV: [5.7]*/
			 UpstreamServiceFlowSupport;	 /* TLV: [5.8]*/
			 OptionalFilteringSupport;	 /* TLV: [5.9]*/
			 TransmitPreEqualizerTapsPerModInt;	 /* TLV: [5.10]*/
			 NumberOfTransmitEqualizerTaps;	 /* TLV: [5.11]*/
			 DCCSupport;	 /* TLV: [5.12]*/
			 IpFiltersSupport;	 /* TLV: [5.13]*/
			 LLCFiltersSupport;	 /* TLV: [5.14]*/
			 ExpandedUnicastSidSpace;	 /* TLV: [5.15]*/
			 RangingHoldOffSupport;	 /* TLV: [5.16]*/
			 L2VPNCapability;	 /* TLV: [5.17]*/
			 L2VPNeSAFEHostCapability;	 /* TLV: [5.18]*/
			 DUTFiltering;	 /* TLV: [5.19]*/
			 UpstreamFrequencyRangeSupport;	 /* TLV: [5.20]*/
			 UpstreamSymbolRateSupport;	 /* TLV: [5.21]*/
			 SACMode2Support;	 /* TLV: [5.22]*/
			 CodeHoppingMode2Support;	 /* TLV: [5.23]*/
			 MultipleTransmitChannelSupport;	 /* TLV: [5.24]*/
			 512MspsUpstreamTransmitChannel;	 /* TLV: [5.25]*/
			 256MspsUpstreamTransmitChannel;	 /* TLV: [5.26]*/
			 TotalSIDClusterSupport;	 /* TLV: [5.27]*/
			 SIDClusterPerServiceFlowSupport;	 /* TLV: [5.28]*/
			 MultipleReceiveChannelSupport;	 /* TLV: [5.29]*/
			 TotalDSIDSupport;	 /* TLV: [5.30]*/
			 ResequencingDSIDSupport;	 /* TLV: [5.31]*/
			 MulticastDSIDSupport;	 /* TLV: [5.32]*/
			 MulticastDSIDForwarding;	 /* TLV: [5.33]*/
			 FrameControlTypeForwardingCapability;	 /* TLV: [5.34]*/
			 DPVCapability;	 /* TLV: [5.35]*/
			 UGSUpstreamSFSupport;	 /* TLV: [5.36]*/
			 MAPAndUCDReceiptSupport;	 /* TLV: [5.37]*/
			 UpstreamDropClassifierSupport;	 /* TLV: [5.38]*/
			 IPv6Support;	 /* TLV: [5.39]*/
			 ExtendedUTPCapability;	 /* TLV: [5.40]*/
			 Optional8021adahMPLSClassification;	 /* TLV: [5.41]*/
			 D-ONUCapabilities		{
				 DPoEVersionNumber;	 /* TLV: [5.42.1]*/
				 BidirectionalUnicastLLIDsSupported;	 /* TLV: [5.42.2]*/
				 DownstreamMulticastLLIDsSupported;	 /* TLV: [5.42.3]*/
				 MESPSupport;	 /* TLV: [5.42.4]*/
				 D-ONUPorts;	 /* TLV: [5.42.5]*/
				 EPONDataRateSupport;	 /* TLV: [5.42.6]*/
				 ServiceOAM;	 /* TLV: [5.42.7]*/
			}
			 EnergyManagementCapabilities;	 /* TLV: [5.44]*/
			 C-DOCSISCapability;	 /* TLV: [5.45]*/
		}
		 CmMic;	 /* TLV: [6]*/
		 CmtsMic;	 /* TLV: [7]*/
		 VendorID;	 /* TLV: [8]*/
		 SoftwareUpgradeFilename;	 /* TLV: [9]*/
		 SnmpWriteAccessControl;	 /* TLV: [10]*/
		 ModemIPAddress;	 /* TLV: [12]*/
		 ServicesNotAvailableResponse;	 /* TLV: [13]*/
		 CpeEthernetMacAddress;	 /* TLV: [14]*/
		 MaxCPEAllowed;	 /* TLV: [18]*/
		 SoftwareUpgradeIPv4TftpServer;	 /* TLV: [21]*/
		 UpstreamPacketClassification	{
			 ClassifierReference;	 /* TLV: [22.1]*/
			 ClassifierIdentifier;	 /* TLV: [22.2]*/
			 ServiceFlowReference;	 /* TLV: [22.3]*/
			 ServiceFlowIdentifier;	 /* TLV: [22.4]*/
			 RulePriority;	 /* TLV: [22.5]*/
			 ClassifierActivationState;	 /* TLV: [22.6]*/
			 DynamicServiceChangeAction;	 /* TLV: [22.7]*/
			 ClassifierError		{
				 Errored Parameter;	 /* TLV: [22.8.1]*/
				 Error Code;	 /* TLV: [22.8.2]*/
				 Error Message;	 /* TLV: [22.8.3]*/
			}
			 IPv4PacketClassification		{
				 IPv4TypeOfServiceRangeAndMask;	 /* TLV: [22.9.1]*/
				 IPProtocol;	 /* TLV: [22.9.2]*/
				 IPv4SourceAddress;	 /* TLV: [22.9.3]*/
				 IPv4SourceMask;	 /* TLV: [22.9.4]*/
				 IPv4DestinationAddress;	 /* TLV: [22.9.5]*/
				 IPv4DestinationMask;	 /* TLV: [22.9.6]*/
				 TCPUDPSourcePortStart;	 /* TLV: [22.9.7]*/
				 TCPUDPSourcePortEnd;	 /* TLV: [22.9.8]*/
				 TCPUDPDestinationPortStart;	 /* TLV: [22.9.9]*/
				 TCPUDPDestinationPortEnd;	 /* TLV: [22.9.10]*/
			}
			 EthernetLLCPacketClassification		{
				 DestinationMACAddress;	 /* TLV: [22.10.1]*/
				 SourceMACAddress;	 /* TLV: [22.10.2]*/
				 EthertypeDSAPMacType;	 /* TLV: [22.10.3]*/
			}
			 8021PQPacketClassification		{
				 8021PUserPriority;	 /* TLV: [22.11.1]*/
				 8021QVLANID;	 /* TLV: [22.11.2]*/
			}
			 IPv6PacketClassification		{
				 IPv6TrafficClassRangeAndMask;	 /* TLV: [22.12.1]*/
				 IPv6FlowLabel;	 /* TLV: [22.12.2]*/
				 IPv6NextHeaderType;	 /* TLV: [22.12.3]*/
				 IPv6SourceAddress;	 /* TLV: [22.12.4]*/
				 IPv6SourcePrefixLength;	 /* TLV: [22.12.5]*/
				 IPv6DestinationAddress;	 /* TLV: [22.12.6]*/
				 IPv6DestinationPrefixLength;	 /* TLV: [22.12.7]*/
			}
			 CMInterfaceMask;	 /* TLV: [22.13]*/
			 8021adSTagCTagFrameClassification		{
				 S-TPID;	 /* TLV: [22.14.1]*/
				 S-VID;	 /* TLV: [22.14.2]*/
				 S-PCP;	 /* TLV: [22.14.3]*/
				 S-DEI;	 /* TLV: [22.14.4]*/
				 C-TPID;	 /* TLV: [22.14.5]*/
				 C-VID;	 /* TLV: [22.14.6]*/
				 C-PCP;	 /* TLV: [22.14.7]*/
				 C-CFI;	 /* TLV: [22.14.8]*/
				 S-TCI;	 /* TLV: [22.14.9]*/
				 C-TCI;	 /* TLV: [22.14.10]*/
			}
			 8021ahPacketClassification		{
				 I-TPID;	 /* TLV: [22.15.1]*/
				 I-SID;	 /* TLV: [22.15.2]*/
				 I-TCI;	 /* TLV: [22.15.3]*/
				 I-PCP;	 /* TLV: [22.15.4]*/
				 I-DEI;	 /* TLV: [22.15.5]*/
				 I-UCA;	 /* TLV: [22.15.6]*/
				 B-TPID;	 /* TLV: [22.15.7]*/
				 B-TCI;	 /* TLV: [22.15.8]*/
				 B-PCP;	 /* TLV: [22.15.9]*/
				 B-DEI;	 /* TLV: [22.15.10]*/
				 B-VID;	 /* TLV: [22.15.11]*/
				 B-DA;	 /* TLV: [22.15.12]*/
				 B-SA;	 /* TLV: [22.15.13]*/
			}
			 ICMPv4ICMPv6PacketClassification		{
				 ICMPv4ICMPv6TypeStart;	 /* TLV: [22.16.1]*/
				 ICMPv4ICMPv6TypeEnd;	 /* TLV: [22.16.2]*/
			}
			 MPLSClassification		{
				 MPLSTC;	 /* TLV: [22.17.1]*/
				 MPLSLabel;	 /* TLV: [22.17.2]*/
			}
			 VendorSpecificClassifierParameters;	 /* TLV: [22.43]*/
			 8021adSTagCTagFrameClassification		{
				 S-TPID;	 /* TLV: [22.14.1]*/
				 S-VID;	 /* TLV: [22.14.2]*/
				 S-PCP;	 /* TLV: [22.14.3]*/
				 S-DEI;	 /* TLV: [22.14.4]*/
				 C-TPID;	 /* TLV: [22.14.5]*/
				 C-VID;	 /* TLV: [22.14.6]*/
				 C-PCP;	 /* TLV: [22.14.7]*/
				 C-CFI;	 /* TLV: [22.14.8]*/
				 S-TCI;	 /* TLV: [22.14.9]*/
				 C-TCI;	 /* TLV: [22.14.10]*/
			}
			 8021ahPacketClassification		{
				 I-TPID;	 /* TLV: [22.15.1]*/
				 I-SID;	 /* TLV: [22.15.2]*/
				 I-TCI;	 /* TLV: [22.15.3]*/
				 I-PCP;	 /* TLV: [22.15.4]*/
				 I-DEI;	 /* TLV: [22.15.5]*/
				 I-UCA;	 /* TLV: [22.15.6]*/
				 B-TPID;	 /* TLV: [22.15.7]*/
				 B-TCI;	 /* TLV: [22.15.8]*/
				 B-PCP;	 /* TLV: [22.15.9]*/
				 B-DEI;	 /* TLV: [22.15.10]*/
				 B-VID;	 /* TLV: [22.15.11]*/
				 B-DA;	 /* TLV: [22.15.12]*/
				 B-SA;	 /* TLV: [22.15.13]*/
			}
			 MPLSClassification		{
				 MPLSTC;	 /* TLV: [22.17.1]*/
				 MPLSLabel;	 /* TLV: [22.17.2]*/
			}
			 EthernetLLCPacketClassification		{
				 SlowProtocolSubtype;	 /* TLV: [22.10.4]*/
			}
		}
		 DownstreamPacketClassification	{
			 ClassifierReference;	 /* TLV: [23.1]*/
			 ClassifierIdentifier;	 /* TLV: [23.2]*/
			 ServiceFlowReference;	 /* TLV: [23.3]*/
			 ServiceFlowIdentifier;	 /* TLV: [23.4]*/
			 RulePriority;	 /* TLV: [23.5]*/
			 ClassifierActivationState;	 /* TLV: [23.6]*/
			 DynamicServiceChangeAction;	 /* TLV: [23.7]*/
			 ClassifierError;	 /* TLV: [23.8]*/
			 IPv4PacketClassification;	 /* TLV: [23.9]*/
			 EthernetLLCPacketClassification		{
				 DestinationMACAddress;	 /* TLV: [23.10.1]*/
				 SourceMACAddress;	 /* TLV: [23.10.2]*/
				 EthertypeDSAPMacType;	 /* TLV: [23.10.3]*/
			}
			 IEEE8021PQPacketClassification		{
				 IEEE8021PUserPriority;	 /* TLV: [23.11.1]*/
				 IEEE8021QVLANID;	 /* TLV: [23.11.2]*/
			}
			 IPv6PacketClassification		{
				 IPv6TrafficClassRangeAndMask;	 /* TLV: [23.12.1]*/
				 IPv6FlowLabel;	 /* TLV: [23.12.2]*/
				 IPv6NextHeaderType;	 /* TLV: [23.12.3]*/
				 IPv6SourceAddress;	 /* TLV: [23.12.4]*/
				 IPv6SourcePrefixLength;	 /* TLV: [23.12.5]*/
				 IPv6DestinationAddress;	 /* TLV: [23.12.6]*/
				 IPv6DestinationPrefixLength;	 /* TLV: [23.12.7]*/
			}
			 8021adSTagCTagFrameClassification		{
				 S-TPID;	 /* TLV: [23.14.1]*/
				 S-VID;	 /* TLV: [23.14.2]*/
				 S-PCP;	 /* TLV: [23.14.3]*/
				 S-DEI;	 /* TLV: [23.14.4]*/
				 C-TPID;	 /* TLV: [23.14.5]*/
				 C-VID;	 /* TLV: [23.14.6]*/
				 C-PCP;	 /* TLV: [23.14.7]*/
				 C-CFI;	 /* TLV: [23.14.8]*/
				 S-TCI;	 /* TLV: [23.14.9]*/
				 C-TCI;	 /* TLV: [23.14.10]*/
			}
			 8021ahPacketClassification		{
				 I-TPID;	 /* TLV: [23.15.1]*/
				 I-SID;	 /* TLV: [23.15.2]*/
				 I-TCI;	 /* TLV: [23.15.3]*/
				 I-PCP;	 /* TLV: [23.15.4]*/
				 I-DEI;	 /* TLV: [23.15.5]*/
				 I-UCA;	 /* TLV: [23.15.6]*/
				 B-TPID;	 /* TLV: [23.15.7]*/
				 B-TCI;	 /* TLV: [23.15.8]*/
				 B-PCP;	 /* TLV: [23.15.9]*/
				 B-DEI;	 /* TLV: [23.15.10]*/
				 B-VID;	 /* TLV: [23.15.11]*/
				 B-DA;	 /* TLV: [23.15.12]*/
				 B-SA;	 /* TLV: [23.15.13]*/
			}
			 ICMPv4ICMPv6PacketClassification		{
				 ICMPv4ICMPv6TypeStart;	 /* TLV: [23.16.1]*/
				 ICMPv4ICMPv6TypeEnd;	 /* TLV: [23.16.2]*/
			}
			 MPLSClassification		{
				 MPLSTC;	 /* TLV: [23.17.1]*/
				 MPLSLabel;	 /* TLV: [23.17.2]*/
			}
			 VendorSpecificClassifierParameters;	 /* TLV: [23.43]*/
			 8021adSTagCTagFrameClassification		{
				 S-TPID;	 /* TLV: [23.14.1]*/
				 S-VID;	 /* TLV: [23.14.2]*/
				 S-PCP;	 /* TLV: [23.14.3]*/
				 S-DEI;	 /* TLV: [23.14.4]*/
				 C-TPID;	 /* TLV: [23.14.5]*/
				 C-VID;	 /* TLV: [23.14.6]*/
				 C-PCP;	 /* TLV: [23.14.7]*/
				 C-CFI;	 /* TLV: [23.14.8]*/
				 S-TCI;	 /* TLV: [23.14.9]*/
				 C-TCI;	 /* TLV: [23.14.10]*/
			}
			 8021ahPacketClassification		{
				 I-TPID;	 /* TLV: [23.15.1]*/
				 I-SID;	 /* TLV: [23.15.2]*/
				 I-TCI;	 /* TLV: [23.15.3]*/
				 I-PCP;	 /* TLV: [23.15.4]*/
				 I-DEI;	 /* TLV: [23.15.5]*/
				 I-UCA;	 /* TLV: [23.15.6]*/
				 B-TPID;	 /* TLV: [23.15.7]*/
				 B-TCI;	 /* TLV: [23.15.8]*/
				 B-PCP;	 /* TLV: [23.15.9]*/
				 B-DEI;	 /* TLV: [23.15.10]*/
				 B-VID;	 /* TLV: [23.15.11]*/
				 B-DA;	 /* TLV: [23.15.12]*/
				 B-SA;	 /* TLV: [23.15.13]*/
			}
			 MPLSClassification;	 /* TLV: [23.17]*/
			 EthernetLLCPacketClassification		{
				 SlowProtocolSubtype;	 /* TLV: [23.10.4]*/
			}
		}
		 UpstreamServiceFlow	{
			 ServiceFlowReference;	 /* TLV: [24.1]*/
			 ServiceFlowIdentifier;	 /* TLV: [24.2]*/
			 ServiceIdentifier;	 /* TLV: [24.3]*/
			 ServiceClassName;	 /* TLV: [24.4]*/
			 ServiceFlowError		{
				 ErroredParameter;	 /* TLV: [24.5.1]*/
				 ErrorCode;	 /* TLV: [24.5.2]*/
				 ErrorMessage;	 /* TLV: [24.5.3]*/
			}
			 QualityOfServiceParameterSetType;	 /* TLV: [24.6]*/
			 TrafficPriority;	 /* TLV: [24.7]*/
			 MaxSubstainedTrafficRate;	 /* TLV: [24.8]*/
			 MaxTrafficBurst;	 /* TLV: [24.9]*/
			 MinReservedTrafficRate;	 /* TLV: [24.10]*/
			 AssumedMinReservedRatePacketSize;	 /* TLV: [24.11]*/
			 TimeoutForActiveQoS;	 /* TLV: [24.12]*/
			 TimeoutForAdmittedQoS;	 /* TLV: [24.13]*/
			 SchedulingType;	 /* TLV: [24.15]*/
			 RequestTransmissionPolicy;	 /* TLV: [24.16]*/
			 NominalPollingInterval;	 /* TLV: [24.17]*/
			 ToleratedPollJitter;	 /* TLV: [24.18]*/
			 UnsolicitedGrantSize;	 /* TLV: [24.19]*/
			 NominalGrantInterval;	 /* TLV: [24.20]*/
			 ToleratedGrantJitter;	 /* TLV: [24.21]*/
			 GrantsPerInterval;	 /* TLV: [24.22]*/
			 IPTypeOfServiceDSCPOverwrite;	 /* TLV: [24.23]*/
			 UnsolicitedGrantTimeReference;	 /* TLV: [24.24]*/
			 MultiplierContentionBackoffWindow;	 /* TLV: [24.25]*/
			 UpstreamPeakTrafficRate;	 /* TLV: [24.27]*/
			 ServiceFlowRequiredAttrMask;	 /* TLV: [24.31]*/
			 ServiceFlowForbiddenAttrMask;	 /* TLV: [24.32]*/
			 ApplicationIdentifier;	 /* TLV: [24.34]*/
			 UpstreamBufferControl		{
				 MinBuffer;	 /* TLV: [24.35.1]*/
				 TargetBuffer;	 /* TLV: [24.35.2]*/
				 MaxBuffer;	 /* TLV: [24.35.3]*/
			}
			 AggregateServiceFlowReference;	 /* TLV: [24.36]*/
			 MESPReference;	 /* TLV: [24.37]*/
			 VendorSpecificQoS;	 /* TLV: [24.43]*/
			 ServingGroupName;	 /* TLV: [24.38]*/
			 SFCollection;	 /* TLV: [24.44]*/
		}
		 DownstreamServiceFlow	{
			 ServiceFlowReference;	 /* TLV: [25.1]*/
			 ServiceFlowIdentifier;	 /* TLV: [25.2]*/
			 ServiceIdentifier;	 /* TLV: [25.3]*/
			 ServiceClassName;	 /* TLV: [25.4]*/
			 ServiceFlowError		{
				 ErroredParameter;	 /* TLV: [25.5.1]*/
				 ErrorCode;	 /* TLV: [25.5.2]*/
				 ErrorMessage;	 /* TLV: [25.5.3]*/
			}
			 QualityOfServiceParameterSetType;	 /* TLV: [25.6]*/
			 TrafficPriority;	 /* TLV: [25.7]*/
			 MaxSubstainedTrafficRate;	 /* TLV: [25.8]*/
			 MaxTrafficBurst;	 /* TLV: [25.9]*/
			 MinReservedTrafficRate;	 /* TLV: [25.10]*/
			 AssumedMinReservedRatePacketSize;	 /* TLV: [25.11]*/
			 TimeoutForActiveQoS;	 /* TLV: [25.12]*/
			 TimeoutForAdmittedQoS;	 /* TLV: [25.13]*/
			 TimeoutForAdmittedQoS;	 /* TLV: [25.14]*/
			 IPTypeOfServiceDSCPOverwrite;	 /* TLV: [25.23]*/
			 DownstreamPeakTrafficRate;	 /* TLV: [25.27]*/
			 ServiceFlowRequiredAttrMask;	 /* TLV: [25.31]*/
			 ServiceFlowForbiddenAttrMask;	 /* TLV: [25.32]*/
			 ApplicationIdentifier;	 /* TLV: [25.34]*/
			 DownstreamBufferControl		{
				 MinBuffer;	 /* TLV: [25.35.1]*/
				 TargetBuffer;	 /* TLV: [25.35.2]*/
				 MaxBuffer;	 /* TLV: [25.35.3]*/
			}
			 AggregateServiceFlowReference;	 /* TLV: [25.36]*/
			 MESPReference;	 /* TLV: [25.37]*/
			 VendorSpecificQoS;	 /* TLV: [25.43]*/
			 ServingGroupName;	 /* TLV: [25.38]*/
			 SFCollection;	 /* TLV: [25.44]*/
		}
		 HMAC-Digest;	 /* TLV: [27]*/
		 MaxClassifiers;	 /* TLV: [28]*/
		 PrivacyEnable;	 /* TLV: [29]*/
		 AuthorizationBlock;	 /* TLV: [30]*/
		 KeySequenceNumber;	 /* TLV: [31]*/
		 ManufacturerCVC;	 /* TLV: [32]*/
		 Co-signerCVC;	 /* TLV: [33]*/
		 SNMPv3KickstartValue	{
			 SNMPv3KickstartSecurityName;	 /* TLV: [34.1]*/
			 SNMPv3KickstartManagerPublicNumber;	 /* TLV: [34.2]*/
		}
		 SubMgmtControl;	 /* TLV: [35]*/
		 SubscirberManagementCPEIPv4List;	 /* TLV: [36]*/
		 SubMgmtFilterGroups;	 /* TLV: [37]*/
		 SNMPv3NotificationReceiver	{
			 SNMPv3NRIPv4Address;	 /* TLV: [38.1]*/
			 SNMPv3NRUdpPortNumber;	 /* TLV: [38.2]*/
			 SNMPv3NRTrapType;	 /* TLV: [38.3]*/
			 SNMPv3NRTimeout;	 /* TLV: [38.4]*/
			 SNMPv3NRRetries;	 /* TLV: [38.5]*/
			 SNMPv3NRFilteringParameters;	 /* TLV: [38.6]*/
			 SNMPv3NRSecurityName;	 /* TLV: [38.7]*/
			 SNMPv3NRIPv6Address;	 /* TLV: [38.8]*/
		}
		 StaticMulticastMACAddress;	 /* TLV: [42]*/
		 VendorSpecific	{
			 L2VPN;	 /* TLV: [43.5]*/
			 ExtCmtsMicConf		{
				 ExtCmtsMicHMACType;	 /* TLV: [43.6.1]*/
				 ExtCmtsMicBitmap;	 /* TLV: [43.6.2]*/
				 ExpExtCmtsMicDigest;	 /* TLV: [43.6.3]*/
			}
			 SAVAuthorization		{
				 SAVGroupName;	 /* TLV: [43.7.1]*/
				 SAVStaticPrefixRule			{
					 SAVStaticPrefixAddr;	 /* TLV: [43.7.2.1]*/
					 SAVStaticPrefixLength;	 /* TLV: [43.7.2.2]*/
				}
			}
			 IPMulticastJoinAuth		{
				 IPMulticastProfileName;	 /* TLV: [43.10.1]*/
				 IPMulticastJoinAuthStaticSession			{
					 RulePriority;	 /* TLV: [43.10.2.1]*/
					 AuthorizationAction;	 /* TLV: [43.10.2.2]*/
					 SourcePrefixAddress;	 /* TLV: [43.10.2.3]*/
					 SourcePrefixLength;	 /* TLV: [43.10.2.4]*/
					 GroupPrefixAddress;	 /* TLV: [43.10.2.5]*/
					 GroupPrefixLength;	 /* TLV: [43.10.2.6]*/
				}
				 MaxMulticastSessions;	 /* TLV: [43.10.3]*/
			}
			 ServiceTypeIdentifier;	 /* TLV: [43.11]*/
			 DAC;	 /* TLV: [43.12]*/
		}
		 Vendor-SpecificCapabilites;	 /* TLV: [44]*/
		 TransmitChannelConfig	{
			 TCCReference;	 /* TLV: [46.1]*/
			 UpstreamChannelAction;	 /* TLV: [46.2]*/
			 UpstreamChannelID;	 /* TLV: [46.3]*/
			 NewUpstreamChannelID;	 /* TLV: [46.4]*/
			 UCD;	 /* TLV: [46.5]*/
			 RangingSID;	 /* TLV: [46.6]*/
			 InitializationTechnique;	 /* TLV: [46.7]*/
			 RangingParameter		{
				 RangingReferenceChannelID;	 /* TLV: [46.8.1]*/
				 TimingOffsetIntegerPart;	 /* TLV: [46.8.2]*/
				 TimingOffsetFractionalPart;	 /* TLV: [46.8.3]*/
				 PowerOffset;	 /* TLV: [46.8.4]*/
				 FrequencyOffset;	 /* TLV: [46.8.5]*/
			}
			 DynamicRangeWindow;	 /* TLV: [46.9]*/
			 TCCError		{
				 ReportedParameter;	 /* TLV: [46.254.1]*/
				 ErrorCode;	 /* TLV: [46.254.2]*/
				 ErrorMessage;	 /* TLV: [46.254.3]*/
			}
		}
		 ServiceFlowSIDClusterAssignments	{
			 SFID;	 /* TLV: [47.1]*/
			 SIDCluster		{
				 SIDClusterID;	 /* TLV: [47.2.1]*/
				 SID-to-ChannelMapping			{
					 UpstreamChannelID;	 /* TLV: [47.2.2.1]*/
					 SID;	 /* TLV: [47.2.2.2]*/
					 Action;	 /* TLV: [47.2.2.3]*/
				}
			}
			 SIDClusterSwitchoverCriteria		{
				 MaxRequestsPerSIDCluster;	 /* TLV: [47.3.1]*/
				 MaxOutstandingBytesPerSIDCluster;	 /* TLV: [47.3.2]*/
				 MaxTotalBytesRequestedPerSIDCluster;	 /* TLV: [47.3.3]*/
				 MaxTimeInSIDCluster;	 /* TLV: [47.3.4]*/
			}
		}
		 CMReceiveChannelRCP	{
			 RCP-ID;	 /* TLV: [48.1]*/
			 RCPName;	 /* TLV: [48.2]*/
			 RCPCenterFrequencySpacing;	 /* TLV: [48.3]*/
			 ReceiveModule		{
				 ReceiveModuleIndex;	 /* TLV: [48.4.1]*/
				 ReceiveModuleAdjacentChannels;	 /* TLV: [48.4.2]*/
				 ReceiveModuleChannelBlockRange			{
					 ReceiveModuleMinCenterFrequency;	 /* TLV: [48.4.3.1]*/
					 ReceiveModuleMaxCenterFrequency;	 /* TLV: [48.4.3.2]*/
				}
				 FirstChannelCenterFrequencyAssign;	 /* TLV: [48.4.4]*/
				 ResequencingChannelSubsetCapability;	 /* TLV: [48.4.5]*/
				 ReceiveModuleConnectivity;	 /* TLV: [48.4.6]*/
				 CommonPhysicalLayerParameter;	 /* TLV: [48.4.7]*/
			}
			 ReceiveChannels		{
				 ReceiveChannelIndex;	 /* TLV: [48.5.1]*/
				 ReceiveChannelConnectivity;	 /* TLV: [48.5.2]*/
				 ReceiveChannelConnectedOffset;	 /* TLV: [48.5.3]*/
				 PrimaryDownstreamChannelIndicator;	 /* TLV: [48.5.5]*/
			}
			 ProfileConfigVendorSpecificParameters;	 /* TLV: [48.43]*/
		}
		 CMReceiveChannelRCC	{
			 RCP-ID;	 /* TLV: [49.1]*/
			 ReceiveModule		{
				 ReceiveModuleIndex;	 /* TLV: [49.4.1]*/
				 ReceiveModuleConnectivity;	 /* TLV: [49.4.6]*/
			}
			 ReceiveChannels		{
				 ReceiveChannelIndex;	 /* TLV: [49.5.1]*/
				 ReceiveChannelConnectivity;	 /* TLV: [49.5.2]*/
				 CenterFrequencyAssignment;	 /* TLV: [49.5.4]*/
				 PrimaryDownstreamChannelIndicator;	 /* TLV: [49.5.5]*/
			}
			 PartialServiceDownstreamChannels;	 /* TLV: [49.6]*/
			 ProfileConfigVendorSpecificParameters;	 /* TLV: [49.43]*/
			 RCCError		{
				 ReceiveModuleOrReceiveChannel;	 /* TLV: [49.254.1]*/
				 ReceiveModuleOrReceiveChannelIndex;	 /* TLV: [49.254.2]*/
				 ReportedParameter;	 /* TLV: [49.254.3]*/
				 ErrorCode;	 /* TLV: [49.254.4]*/
				 ErrorMessage;	 /* TLV: [49.254.5]*/
			}
		}
		 DSID	{
			 DSID;	 /* TLV: [50.1]*/
			 DSIDAction;	 /* TLV: [50.2]*/
			 DownstreamResequencing		{
				 ResequencingDSID;	 /* TLV: [50.3.1]*/
				 DownstreamResequencingChannelList;	 /* TLV: [50.3.2]*/
				 DSIDResequencingWaitTime;	 /* TLV: [50.3.3]*/
				 ResequencingWarningThreshold;	 /* TLV: [50.3.4]*/
				 CMStatusMaxEventHoldOffTimer;	 /* TLV: [50.3.5]*/
			}
			 Multicast		{
				 ClientMACAddress			{
					 ClientMACAddressAction;	 /* TLV: [50.4.1.1]*/
					 ClientMACAddress;	 /* TLV: [50.4.1.2]*/
				}
				 MulticastCMInterfaceMask;	 /* TLV: [50.4.2]*/
				 GroupMACAddresses;	 /* TLV: [50.4.3]*/
				 PayloadHeaderSuppression;	 /* TLV: [50.4.26]*/
			}
		}
		 SecurityAssociation	{
			 SAAction;	 /* TLV: [51.1]*/
			 SA-Descriptor;	 /* TLV: [51.23]*/
		}
		 InitializingChannelTimeout;	 /* TLV: [52]*/
		 SNMPv1v2cCoexistenceConfig	{
			 SNMPv1v2cCommunityName;	 /* TLV: [53.1]*/
			 SNMPv1v2cTransportAddressAccess		{
				 SNMPv1v2cTransportAddress;	 /* TLV: [53.2.1]*/
				 SNMPv1v2cTransportMask;	 /* TLV: [53.2.2]*/
			}
			 SNMPv1v2cAccessViewType;	 /* TLV: [53.3]*/
			 SNMPv1v2cAccessViewName;	 /* TLV: [53.4]*/
		}
		 SNMPv3AccessViewConfig	{
			 SNMPv3AccessViewName;	 /* TLV: [54.1]*/
			 SNMP3AccessViewSubtree;	 /* TLV: [54.2]*/
			 SNMPv3AccessViewMask;	 /* TLV: [54.3]*/
			 SNMPv3AccessViewType;	 /* TLV: [54.4]*/
		}
		 SnmpCpeAccessControl;	 /* TLV: [55]*/
		 CMInitializationReason;	 /* TLV: [57]*/
		 SoftwareUpgradeIPv6TftpServer;	 /* TLV: [58]*/
		 TFTPServerModemIPv6Addr;	 /* TLV: [59]*/
		 UpstreamDropPacketClassification	{
			 ClassifierReference;	 /* TLV: [60.1]*/
			 ClassifierIdentifier;	 /* TLV: [60.2]*/
			 RulePriority;	 /* TLV: [60.5]*/
			 DynamicServiceChangeAction;	 /* TLV: [60.7]*/
			 ClassifierError;	 /* TLV: [60.8]*/
			 IPv4PacketClassification		{
				 IPv4TypeOfServiceRangeAndMask;	 /* TLV: [60.9.1]*/
				 IPProtocol;	 /* TLV: [60.9.2]*/
				 IPv4SourceAddress;	 /* TLV: [60.9.3]*/
				 IPv4SourceMask;	 /* TLV: [60.9.4]*/
				 IPv4DestinationAddress;	 /* TLV: [60.9.5]*/
				 IPv4DestinationMask;	 /* TLV: [60.9.6]*/
				 TCPUDPSourcePortStart;	 /* TLV: [60.9.7]*/
				 TCPUDPSourcePortEnd;	 /* TLV: [60.9.8]*/
				 TCPUDPDestinationPortStart;	 /* TLV: [60.9.9]*/
				 TCPUDPDestinationPortEnd;	 /* TLV: [60.9.10]*/
			}
			 EthernetLLCPacketClassification		{
				 DestinationMACAddress;	 /* TLV: [60.10.1]*/
				 SourceMACAddress;	 /* TLV: [60.10.2]*/
				 EthertypeDSAPMacType			{
					 8021PQPacketClassification				{
						 8021PUserPriority;	 /* TLV: [60.10.3.11.1]*/
						 8021QVLANID;	 /* TLV: [60.10.3.11.2]*/
					}
				}
				 SlowProtocolSubtype;	 /* TLV: [60.10.4]*/
			}
			 IPv6PacketClassification		{
				 IPv6TrafficClassRangeAndMask;	 /* TLV: [60.12.1]*/
				 IPv6FlowLabel;	 /* TLV: [60.12.2]*/
				 IPv6NextHeaderType;	 /* TLV: [60.12.3]*/
				 IPv6SourceAddress;	 /* TLV: [60.12.4]*/
				 IPv6SourcePrefixLength;	 /* TLV: [60.12.5]*/
				 IPv6DestinationAddress;	 /* TLV: [60.12.6]*/
				 IPv6DestinationPrefixLength;	 /* TLV: [60.12.7]*/
			}
			 CMInterfaceMask;	 /* TLV: [60.13]*/
			 8021adSTagCTagFrameClassification		{
				 S-TPID;	 /* TLV: [60.14.1]*/
				 S-VID;	 /* TLV: [60.14.2]*/
				 S-PCP;	 /* TLV: [60.14.3]*/
				 S-DEI;	 /* TLV: [60.14.4]*/
				 C-TPID;	 /* TLV: [60.14.5]*/
				 C-VID;	 /* TLV: [60.14.6]*/
				 C-PCP;	 /* TLV: [60.14.7]*/
				 C-CFI;	 /* TLV: [60.14.8]*/
				 S-TCI;	 /* TLV: [60.14.9]*/
				 C-TCI;	 /* TLV: [60.14.10]*/
			}
			 8021ahPacketClassification		{
				 I-TPID;	 /* TLV: [60.15.1]*/
				 I-SID;	 /* TLV: [60.15.2]*/
				 I-TCI;	 /* TLV: [60.15.3]*/
				 I-PCP;	 /* TLV: [60.15.4]*/
				 I-DEI;	 /* TLV: [60.15.5]*/
				 I-UCA;	 /* TLV: [60.15.6]*/
				 B-TPID;	 /* TLV: [60.15.7]*/
				 B-TCI;	 /* TLV: [60.15.8]*/
				 B-PCP;	 /* TLV: [60.15.9]*/
				 B-DEI;	 /* TLV: [60.15.10]*/
				 B-VID;	 /* TLV: [60.15.11]*/
				 B-DA;	 /* TLV: [60.15.12]*/
				 B-SA;	 /* TLV: [60.15.13]*/
			}
			 ICMPv4ICMPv6PacketClassification		{
				 ICMPv4ICMPv6TypeStart;	 /* TLV: [60.16.1]*/
				 ICMPv4ICMPv6TypeEnd;	 /* TLV: [60.16.2]*/
			}
			 MPLSClassification		{
				 MPLSTC;	 /* TLV: [60.17.1]*/
				 MPLSLabel;	 /* TLV: [60.17.2]*/
			}
			 VendorSpecificClassifierParameters;	 /* TLV: [60.43]*/
		}
		 SubMgmtCPEIPv6PrefixList;	 /* TLV: [61]*/
		 UpstreamDropClassifierGroupID;	 /* TLV: [62]*/
		 SubMgmtControlMaxCPEIPv6Addr;	 /* TLV: [63]*/
		 CMTSStaticMulticastSession	{
			 StaticMulticastGroup;	 /* TLV: [64.1]*/
			 StaticMulticastSource;	 /* TLV: [64.2]*/
			 StaticMulticastCMIM;	 /* TLV: [64.3]*/
		}
		 L2VPNMacAging;	 /* TLV: [65]*/
		 MgmtEventControlEnconding;	 /* TLV: [66]*/
		 SubMgmtCPEIPv6List;	 /* TLV: [67]*/
		 DefaultUpstreamTargetBufferConfig	{
			 MacAgingMode;	 /* TLV: [68.1]*/
		}
		 MacAddressLearningControl	{
			 MacAddressLearningControl;	 /* TLV: [69.1]*/
			 MacAddressLearningHoldoffTimer;	 /* TLV: [69.2]*/
		}
		 UpstreamAggregateSF	{
			 ServiceFlowReference;	 /* TLV: [70.1]*/
			 AggregateServiceFlowReference;	 /* TLV: [70.36]*/
			 MESPReference;	 /* TLV: [70.37]*/
			 ServingGroupName;	 /* TLV: [70.38]*/
		}
		 DownstreamAggregateSF	{
			 ServiceFlowReference;	 /* TLV: [71.1]*/
			 AggregateServiceFlowReference;	 /* TLV: [71.36]*/
			 MESPReference;	 /* TLV: [71.37]*/
			 ServingGroupName;	 /* TLV: [71.38]*/
		}
		 MetroEthernetServiceProfile	{
			 MESPReference;	 /* TLV: [72.1]*/
			 MESPBandwidthProfile		{
				 MESP-BPCommittedInformationRate;	 /* TLV: [72.2.1]*/
				 MESP-BPCommittedBurstSize;	 /* TLV: [72.2.2]*/
				 MESP-BPExcessInformationRate;	 /* TLV: [72.2.3]*/
				 MESP-BPExcessBurstSize;	 /* TLV: [72.2.4]*/
				 MESP-BPCouplingFlag;	 /* TLV: [72.2.5]*/
				 MESP-BPColorMode			{
					 MESP-BP-CMColorIdentificationField;	 /* TLV: [72.2.6.1]*/
					 MESP-BP-CMColorIdentFieldValue;	 /* TLV: [72.2.6.2]*/
				}
				 MESP-BPColorMarking			{
					 MESP-BP-CRColorMarkingField;	 /* TLV: [72.2.7.1]*/
					 MESP-BP-CRColorMarkingFieldValue;	 /* TLV: [72.2.7.2]*/
				}
			}
			 MESPName;	 /* TLV: [72.3]*/
		}
		 NetworkTimingProfile	{
			 NetworkTimingProfileReference;	 /* TLV: [73.1]*/
			 NetworkTimingProfileName;	 /* TLV: [73.2]*/
		}
		 EnergyMgmtParameter	{
			 EnergyMgmtFeatureControl;	 /* TLV: [74.1]*/
			 EnergyMgmt1x1ModeEncondings		{
				 DownstreamActivityDetection			{
					 DownstreamEntryBitrateThreshold;	 /* TLV: [74.2.1.1]*/
					 DownstreamEntryTimeThreshold;	 /* TLV: [74.2.1.2]*/
					 DownstreamExitBitrateThreshold;	 /* TLV: [74.2.1.3]*/
					 DownstreamExitTimeThreshold;	 /* TLV: [74.2.1.4]*/
				}
				 UpstreamActivityDetection			{
					 UpstreamEntryBitrateThreshold;	 /* TLV: [74.2.2.1]*/
					 UpstreamEntryTimeThreshold;	 /* TLV: [74.2.2.2]*/
					 UpstreamExitBitrateThreshold;	 /* TLV: [74.2.2.3]*/
					 UpstreamExitTimeThreshold;	 /* TLV: [74.2.2.4]*/
				}
			}
			 EnergyMgmtCyclePeriod;	 /* TLV: [74.3]*/
		}
		 EnergyManagement1x1ModeIndicator;	 /* TLV: [75]*/
		 ePS;	 /* TLV: [201]*/
		 eRouterConfig	{
			 InitializationMode;	 /* TLV: [202.1]*/
			 TR-069ManagementServer		{
				 EnableCWMP;	 /* TLV: [202.2.1]*/
				 URL;	 /* TLV: [202.2.2]*/
				 Username;	 /* TLV: [202.2.3]*/
				 Password;	 /* TLV: [202.2.4]*/
				 ConnectionRequestUsername;	 /* TLV: [202.2.5]*/
				 ConnectionRequestPassword;	 /* TLV: [202.2.6]*/
				 ACSOverride;	 /* TLV: [202.2.7]*/
			}
			 InitializationModeOverride;	 /* TLV: [202.3]*/
			 RouterAdvertisementTransInterval;	 /* TLV: [202.10]*/
			 TopologyMode;	 /* TLV: [202.42]*/
			 VendorSpecificInformation		{
				 VendorID;	 /* TLV: [202.43.8]*/
				 SnmpMibObject;	 /* TLV: [202.43.11]*/
			}
			 SNMPv1v2cCoexistenceConfig		{
				 SNMPv1v2cCommunityName;	 /* TLV: [202.53.1]*/
				 SNMPv1v2cTransportAddressAccess			{
					 SNMPv1v2cTransportAddress;	 /* TLV: [202.53.2.1]*/
					 SNMPv1v2cTransportMask;	 /* TLV: [202.53.2.2]*/
				}
				 SNMPv1v2cAccessViewType;	 /* TLV: [202.53.3]*/
				 SNMPv1v2cAccessViewName;	 /* TLV: [202.53.4]*/
			}
			 SNMPv3AccessViewConfig		{
				 SNMPv3AccessViewName;	 /* TLV: [202.54.1]*/
				 SNMPv3AccessViewSubtree;	 /* TLV: [202.54.2]*/
				 SNMPv3AccessViewMask;	 /* TLV: [202.54.3]*/
				 SNMPv3AccessViewType;	 /* TLV: [202.54.4]*/
			}
		}
		 eMTAConfig;	 /* TLV: [216]*/
		 eSTBConfig	{
			 IPModeControl;	 /* TLV: [217.1]*/
			 VendorID;	 /* TLV: [217.8]*/
			 SnmpOID;	 /* TLV: [217.11]*/
			 SNMPv3NotificationReceiver		{
				 IPv4Address;	 /* TLV: [217.38.1]*/
				 UDPPortNumber;	 /* TLV: [217.38.2]*/
				 TrapType;	 /* TLV: [217.38.3]*/
				 Timeout;	 /* TLV: [217.38.4]*/
				 Retries;	 /* TLV: [217.38.5]*/
				 SnmpFilteringParameters;	 /* TLV: [217.38.6]*/
				 SecurityName;	 /* TLV: [217.38.7]*/
				 IPv6Address;	 /* TLV: [217.38.8]*/
			}
			 HomeNetworkPrefixValidation		{
				 InstanceNumber;	 /* TLV: [217.39.1]*/
				 PrefixUsage;	 /* TLV: [217.39.2]*/
				 IPAddressVersion;	 /* TLV: [217.39.3]*/
				 IPv4PrefixLength;	 /* TLV: [217.39.4]*/
				 IPv4SubnetAddress;	 /* TLV: [217.39.5]*/
				 IPv6PrefixLength;	 /* TLV: [217.39.6]*/
				 IPv6NetworkAddress;	 /* TLV: [217.39.7]*/
			}
			 SEBServerEnableTLSCipherSuites;	 /* TLV: [217.40]*/
			 VendorSpecificInformation;	 /* TLV: [217.43]*/
			 SNMPv1v2cCoexistenceConfig		{
				 SNMPv1v2cCommunityName;	 /* TLV: [217.53.1]*/
				 SNMPv1v2cTransportAddressAccess			{
					 SNMPv1v2cTransportAddress;	 /* TLV: [217.53.2.1]*/
					 SNMPv1v2cTransportMask;	 /* TLV: [217.53.2.2]*/
				}
				 SNMPv1v2cAccessViewType;	 /* TLV: [217.53.3]*/
				 SNMPv1v2cAccessViewName;	 /* TLV: [217.53.4]*/
			}
			 SNMPv3AccessViewConfig		{
				 SNMPv3AccessViewName;	 /* TLV: [217.54.1]*/
				 SNMPv3AccessViewSubtree;	 /* TLV: [217.54.2]*/
				 SNMPv3AccessViewMask;	 /* TLV: [217.54.3]*/
				 SNMPv3AccessViewType;	 /* TLV: [217.54.4]*/
			}
		}
		 eTEAConfig;	 /* TLV: [219]*/
		 eDVAConfig;	 /* TLV: [220]*/
		 eSGConfig;	 /* TLV: [221]*/
	}


# Packet Cable

	 PacketCable-X.X{
		 SNMPv3NotificationReceiver	{
			 SNMPv3NRIPv4Address;	 /* TLV: [38.1]*/
			 SNMPv3NRUdpPortNumber;	 /* TLV: [38.2]*/
			 SNMPv3NRTrapType;	 /* TLV: [38.3]*/
			 SNMPv3NRTimeout;	 /* TLV: [38.4]*/
			 SNMPv3NRRetries;	 /* TLV: [38.5]*/
			 SNMPv3NRFilteringParameters;	 /* TLV: [38.6]*/
			 SNMPv3NRSecurityName;	 /* TLV: [38.7]*/
			 SNMPv3NRIPv6Address;	 /* TLV: [38.8]*/
		}
		 VendorSpecific;	 /* TLV: [43]*/
		 TelephonyConfigFileBeginEnd;	 /* TLV: [254]*/
	}