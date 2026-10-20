# erouter TLV source notes

Source: **CM-SP-eRouter-I21-220209**. Paths below are relative to 202.

Runtime definitions use the common [registry format](../../../docs/tlv-registry-format.md).
Unselected definitions remain reference material, not enabled runtime entries.

## 1 InitializationMode

Enabled; section B.4.2.

Operator initialization mode.

- kind: `"unsignedInteger"`
- length: `{"bytes": 1}`
- values: `{"0": "Disabled", "1": "IPv4", "2": "IPv6", "3": "Dual IP"}`
- default: `3`
- maxInstances: `1`

## 2 TR-069ManagementServer

Enabled; section B.4.3.

TR-069 management server parameters; absent parameters use delegated TR-069/TR-181 defaults.

- kind: `"container"`
- length: `{"symbol": "N", "status": "variable"}`

## 2.1 EnableCWMP

Enabled; section B.4.3.1.

Device.ManagementServer.EnableCWMP parameter.

- kind: `"unsignedInteger"`
- length: `{"bytes": 1}`
- maxInstances: `1`
- values: `{"0": "Disabled", "1": "Enabled"}`

## 2.2 URL

Enabled; section B.4.3.2.

Device.ManagementServer.URL parameter.

- kind: `"string"`
- length: `{"symbol": "N", "status": "variable"}`
- maxInstances: `1`

## 2.3 Username

Enabled; section B.4.3.3.

Device.ManagementServer.Username parameter.

- kind: `"string"`
- length: `{"symbol": "N", "status": "variable"}`
- maxInstances: `1`
- sensitive: `true`

## 2.4 Password

Enabled; section B.4.3.4.

Device.ManagementServer.Password parameter.

- kind: `"string"`
- length: `{"symbol": "N", "status": "variable"}`
- maxInstances: `1`
- sensitive: `true`

## 2.5 ConnectionRequestUsername

Enabled; section B.4.3.5.

Device.ManagementServer.ConnectionRequestUsername parameter.

- kind: `"string"`
- length: `{"symbol": "N", "status": "variable"}`
- maxInstances: `1`
- sensitive: `true`

## 2.6 ConnectionRequestPassword

Enabled; section B.4.3.6.

Device.ManagementServer.ConnectionRequestPassword parameter.

- kind: `"string"`
- length: `{"symbol": "N", "status": "variable"}`
- maxInstances: `1`
- sensitive: `true`

## 2.7 ACSOverride

Deferred; section B.4.3.7.

Device.ManagementServer.ACSOverride parameter.

- kind: `"unsignedInteger"`
- length: `{"symbol": "N", "status": "unresolved", "reason": "B.4.3.7 gives Boolean values but no fixed wire width."}`
- maxInstances: `1`
- values: `{"0": "Disabled", "1": "Enabled"}`

## 3 InitializationModeOverride

Enabled; section B.4.5.

Applies only when eRouter is disabled.

- kind: `"unsignedInteger"`
- length: `{"bytes": 1}`
- values: `{"0": "Follow initialization mode", "1": "Keep disabled"}`
- default: `0`
- maxInstances: `1`

## 4 ControllerNumberOfEntries

Deferred; section B.4.4.1.

TR-369 entry count; wire width requires clarification.

- kind: `"unsignedInteger"`
- length: `{"symbol": "n", "status": "unresolved", "reason": "Source specifies Integer without a fixed width."}`

## 5 ControllerConfiguration

Deferred; section B.4.4.2.

Repeated controller configuration; at least one instance per controller counted by type 4.

- kind: `"container"`
- length: `{"symbol": "N", "status": "variable"}`
- repeatable: `true`

## 5.1 Endpoint

Deferred; section B.4.4.3.

Device.LocalAgent.Controller instance parameter.

- kind: `"string"`
- length: `{"symbol": "N", "status": "variable"}`
- maxInstances: `1`

## 5.2 Enable

Deferred; section B.4.4.4.

Device.LocalAgent.Controller instance parameter.

- kind: `"unsignedInteger"`
- length: `{"bytes": 1}`
- maxInstances: `1`
- values: `{"0": "False", "1": "True"}`

## 5.3 AssignedRole

Deferred; section B.4.4.5.

Device.LocalAgent.Controller instance parameter.

- kind: `"string"`
- length: `{"symbol": "N", "status": "variable"}`
- maxInstances: `1`

## 5.4 MTPNumberOfEntries

Deferred; section B.4.4.6.

TR-369 entry count; wire width requires clarification.

- kind: `"unsignedInteger"`
- length: `{"symbol": "n", "status": "unresolved", "reason": "Source specifies Integer without a fixed width."}`

## 5.5 Alias

Deferred; section B.4.4.7.

Device.LocalAgent.Controller instance parameter.

- kind: `"string"`
- length: `{"symbol": "N", "status": "variable"}`
- maxInstances: `1`

## 10 RouterAdvertisementTransInterval

Enabled; section B.4.11.

Router advertisement interval.

- kind: `"unsignedInteger"`
- length: `{"bytes": 2}`
- minimum: `3`
- maximum: `1800`
- default: `30`
- units: `"seconds"`

## 11 SnmpMibObject

Deferred; section B.4.9.

ASN.1 BER SNMP VarBind per RFC 1157. Sets treated as simultaneous; excludes runtime-only controls from B.2/B.5.

- kind: `"snmpVarBind"`
- length: `{"max": 255}`
- repeatable: `true`
- sensitive: `true`

## 12 IPMulticastConfigurationServer

Enabled; section B.4.12.

ASCII IP address or DNS FQDN; conditional MC-EMC support.

- kind: `"ascii"`
- length: `{"symbol": "N", "status": "variable"}`

## 13 LinkIDControl

Enabled; section B.4.13.

Link-ID control.

- kind: `"unsignedInteger"`
- length: `{"bytes": 1}`
- values: `{"0": "Disabled", "1": "Enabled"}`
- default: `0`
- maxInstances: `1`

## 42 TopologyMode

Enabled; section B.4.10.

Absent: SHOULD favor depth below eight customer-facing physical interfaces, otherwise width, unless administratively reconfigured.

- kind: `"unsignedInteger"`
- length: `{"bytes": 1}`
- values: `{"1": "Favor depth", "2": "Favor width"}`
- maxInstances: `1`

## 43 VendorSpecificInformation

Deferred; section B.4.8.

Exactly one vendor ID; ignore on device if OUI does not match.

- kind: `"container"`
- length: `{"symbol": "N", "status": "variable"}`
- requiredChildren: `[[8]]`

## 43.8 VendorID

Deferred; section B.4.8.1.

Vendor OUI; FFFFFF reserved.

- kind: `"bytes"`
- length: `{"bytes": 3}`
- maxInstances: `1`

## 53 SNMPv1v2cCoexistenceConfig

Deferred; section B.4.6.

Community plus one or more transport access instances. Device supports at least five containers; this is not a file maximum.

- kind: `"container"`
- length: `{"symbol": "N", "status": "variable"}`
- repeatable: `true`
- requiredChildren: `[[1], [2]]`

## 53.1 SNMPv1v2cCommunityName

Deferred; section B.4.6.1.

SNMP community string.

- kind: `"string"`
- length: `{"min": 1, "max": 32}`
- sensitive: `true`

## 53.2 SNMPv1v2cTransportAddressAccess

Deferred; section B.4.6.2.

One address and optional mask.

- kind: `"container"`
- length: `{"symbol": "N", "status": "variable"}`
- repeatable: `true`
- requiredChildren: `[[1]]`

## 53.2.1 SNMPv1v2cTransportAddress

Deferred; section B.4.6.2.1.

IPv4 or IPv6 transport address.

- kind: `"transportAddress"`
- length: `{"allowedBytes": [6, 18]}`
- maxInstances: `1`

## 53.2.2 SNMPv1v2cTransportMask

Deferred; section B.4.6.2.2.

IPv4 or IPv6 transport address mask.

- kind: `"transportAddress"`
- length: `{"allowedBytes": [6, 18]}`
- maxInstances: `1`

## 53.3 SNMPv1v2cAccessViewType

Deferred; section B.4.6.3.

Access permission.

- kind: `"unsignedInteger"`
- length: `{"bytes": 1}`
- values: `{"1": "Read-only", "2": "Read-write"}`
- default: `1`

## 53.4 SNMPv1v2cAccessViewName

Deferred; section B.4.6.4.

Access view name.

- kind: `"string"`
- length: `{"sourceText": "1.32", "status": "unresolved", "reason": "Apparent printed range typo; confirm before enforcing."}`

## 54 SNMPv3AccessViewConfig

Deferred; section B.4.7.

Conditional on SNMPv3 support; nonpersistent view entry.

- kind: `"container"`
- length: `{"symbol": "N", "status": "variable"}`
- repeatable: `true`
- requiredChildren: `[[1]]`

## 54.1 SNMPv3AccessViewName

Deferred; section B.4.7.1.

View name.

- kind: `"string"`
- length: `{"min": 1, "max": 32}`
- maxInstances: `1`

## 54.2 SNMPv3AccessViewSubtree

Deferred; section B.4.7.2.

OID subtree, default 1.3.6.

- kind: `"oid"`
- length: `{"symbol": "N", "status": "variable"}`
- encodingStatus: `"unresolved"`
- issue: `"Printed 06 03 01 03 06 conflicts with conventional BER OID arc packing; resolve before encoding."`
- default: `"1.3.6"`

## 54.3 SNMPv3AccessViewMask

Deferred; section B.4.7.3.

Optional mask; absent means zero-length string.

- kind: `"bits"`
- length: `{"min": 0, "max": 16}`
- default: `""`

## 54.4 SNMPv3AccessViewType

Deferred; section B.4.7.4.

Optional inclusion or exclusion.

- kind: `"unsignedInteger"`
- length: `{"bytes": 1}`
- values: `{"1": "Included", "2": "Excluded"}`
- default: `1`
