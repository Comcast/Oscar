---
name: oscar-packetcable-config-tlvs
description: Review and implement OSCAR standalone PacketCable 1.5 and 2.0 configuration TLVs, registry metadata, codecs and round trips. Distinguish DOCSIS eMTA/eDVA wrappers from standalone files; excludes DHCP capability TLVs and SIP signaling implementation.
---

# PacketCable configuration TLVs

Read [source map and coverage](references/source-map.md) before changing definitions.
The package shares references and implementation guidance, not one undifferentiated
protocol edition. Preserve the exact issue and section for every encoding decision.

- Use PROV1.5 section 9.1 for standalone framing, sections 9.1.1–9.1.6 for
  provisioned MIB attributes, and section 11 for notification receivers.
- Use EUE-PROV sections 6.4.2/6.5 for 2.0 differences: ASCII OUI and IPv6
  receiver 38.8. Never copy DHCP option-43/option-60 capability suboptions into
  the configuration catalog simply because they also use TLVs.
- Keep one shared catalog and version-specific API views. Keep names compatible,
  definitions in ascending numeric path order, parents before descendants.
- Embedded device does not mean embedded file. eDOCSIS section 5.2.8.1 assigns
  216 to eMTA and 220 to eDVA but delegates inner encodings to eSAFE specifications.
  Do not attach the standalone registry beneath either wrapper without explicit
  payload and fragmentation evidence. Preserve opaque bytes meanwhile.
- Vendor payloads are opaque; do not invent child semantics. PacketCable 2.0
  sub-option 8 uses six ASCII hex characters. Do not apply DOCSIS binary-OUI
  validation to that representation.
- Use QA hygiene: reproduce defects before fixing, test independent wire vectors,
  both versions, malformed input, repeated receivers, and exact round trips.
  Do not claim complete device provisioning validity from registry coverage.
- Public APIs must distinguish protocol edition from OSCAR release and expose
  reusable metadata to the UI. Binary framing cannot determine the edition.
