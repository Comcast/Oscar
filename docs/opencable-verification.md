# OpenCable/eSTB verification

Target: regular OSCAR `Docsis`, HOST2.1-CFR I17 (2013), DOCSIS 2.0/3.0 eCM.
The internal regular-family identifier is DOCSIS_VER_31; no D4 support is claimed.

## Source decisions

- Sections 15.1.1 and 15.2 place the settings beneath 217.
- [CM-SP-eDOCSIS-I30-190213](https://account.cablelabs.com/server/alfresco/2cc53697-0e59-4983-a5ae-c08d4ab70136),
  section 5.2.8.1, printed p.47 permits multiple eSAFE containers and requires
  forwarding their contents in received order. Regular DOCSIS 217 therefore
  uses `maxInstances: -1`. This does not relax child-specific repetition rules.
- Section 15.2.1.4 prints `1.32`. OSCAR interprets this as 1–32 octets,
  consistent with the view-name index in [RFC 3415](https://www.rfc-editor.org/rfc/rfc3415.html).
  This is a documented interpretation, not an identified CableLabs erratum.
- Section 15.2.2.2 requires ASN.1 OBJECT IDENTIFIER but its sample conflicts
  with BER. OSCAR follows [X.690 section 8.19](https://www.itu.int/rec/dologin_pub.asp?id=T-REC-X.690-201508-S%21%21PDF-E&lang=f&type=items):
  1.3.6 encodes as `06 02 2B 06`. It does not emit `06 03 01 03 06`.
- The duplicate-child wording referencing “54.4” remains a source ambiguity.
  OSCAR preserves supplied instances rather than implementing device discard rules.
- Section 15.2.4–5 places VendorID first inside 43. Legacy standalone 217.8
  is removed from the registry; use 217.43.8.

## Compatibility changes and verified behavior

- Multiple `eSTBConfig` blocks are supported in regular `Docsis` text.
  Container boundaries and encounter order survive binary/text round trips.
  Each container must contain complete child TLVs; automatic fragmentation or
  reassembly of children split across containers is not implemented.
- Repeated 217 children no longer overwrite each other's values. Import and
  export preserve their encounter order, even when InstanceNumber is not sorted.
- Use `Snmp11 OID Type "value";` inside `eSTBConfig`; legacy `SnmpOID` was not
  a usable VarBind grammar form. Wire path remains 217.11.
- Notification port, trap type, timeout and retries are decimal integers with
  two-byte network-order encoding. Previously those entries used raw hex input.
- Filtering and access-view subtree fields use a BER OID, not an SNMP VarBind.
- Use `VendorSpecificInformation { VendorID 00:11:22; }`. An opaque value on
  the vendor container is not supported. Unknown vendor children are rejected;
  their encodings require vendor documentation before adding definitions.
- IPv4/IPv6 transport ports retain all 16 bits; common ports 161/162 are accepted.
- Alphabetic single-byte hex values such as `FF` are quoted on export so the
  lexer accepts the result.
- Registry ranges describe wire widths and source limits. They are not a full
  device-semantic validator: prefix consistency, required peers, reserved enum
  values and receiver dependencies still require profile/device review.

## Automated checks

`OpenCableRegistryTest` exercises independent wire vectors and byte-for-byte
round trips for IPv4/IPv6 prefixes, repeated prefixes in unsorted instance order,
SNMP VarBind, coexistence address/mask, repeated IPv6 transport rows, notification
receiver fields, access-view OID/mask, and nested VendorID. It checks port boundaries
and rejection of unknown/truncated nested data. Repeated parent tests compare
explicit wire vectors, identical duplicates, interleaved top-level TLVs and
complete finalized file bytes including regenerated MICs. D4 isolation is also checked.
Run `mvn -Dtest=OpenCableRegistryTest test`, then `tools/run-all.sh`.

## Hardware acceptance — pending

No eSTB, provisioning server or device access is available in this workspace.
Do not mark this completed based on a successful round trip.

1. Record eSTB model, firmware, eCM DOCSIS version and two-way DSG capability.
2. Start from a known working operator-approved profile. Retain a recovery copy.
   Add one reviewed 217 setting at a time; do not provision synthetic test
   credentials or documentation addresses as a production profile.
3. Compile using the deployment's DOCSIS version and authorized shared secret.
   Check generated integrity fields; retain the original and decoded result
   locally without publishing credentials or customer certificates.
4. Provision in a controlled lab. Confirm eCM registration and the section
   15.1.1 “2-Way OK, UCID” transition before evaluating eSTB configuration.
5. Verify IP mode and both prefix families; test two independent prefix entries
   with distinct instance numbers. Confirm the intended allow/discard behavior.
6. Verify SNMP community access, transport restrictions and views; test both an
   allowed and a denied query. Check notification delivery to a lab receiver.
7. Check eSTB logs for the I453/I454/I438/I456 event families. Record outcomes,
   wire capture where available, firmware differences and rollback results.
8. Vendor-specific payload testing requires that vendor's definitions and
   test equipment. Historical TLS cipher support is not a security endorsement.

## Separate follow-ups

- [ ] Hardware acceptance above and evidence capture.
- [ ] General cross-field/device-semantic validation.
- [ ] Vendor-defined children when authoritative definitions become available.
- [ ] D4 repeated-child parser handling remains separate from OpenCable scope.
- [ ] Obtain CableLabs clarification of the printed ambiguities if available.
