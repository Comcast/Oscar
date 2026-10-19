# SNMP value export

`ConfigurationFileExport` uses the decoded BER type and value for both Snmp11
and Snmp64. The outer TLV's one-byte or two-byte length field does not change
the value representation. For example, Snmp64 `IpAddress "192.0.2.1"` exports
as an IPv4 address, not the ASCII hex bytes of that address string.

This corrects the extra ASCII-to-hex conversion in releases through 3.2.0.
Consumers need a JAR containing the fix; changing the UI alone does not update
its pinned OSCAR dependency.

`SnmpExportTest` checks PacketCable 1.0, 1.5, and 2.0 with both Snmp11 and
Snmp64. It verifies IPv4 addresses, signed integers, counters, gauges, text,
and non-text hex octets, including byte-identical reimport. These are codec
regressions, not evidence of device interoperability.

## Separate limitations

- PacketCable export uses the `PacketCable-X.X` placeholder. The caller must
  choose the known version before reimport; the regression substitutes its
  original header explicitly.
- TimeTicks currently exports SNMP4J's duration text (for example `0:00:01.00`),
  while text import expects decimal ticks. This affects Snmp11 as well and
  requires a separate fix with tick-value boundary tests.
- Counter64 is listed by the BER API but is absent from the text grammar.
  Its text compile/export support requires separate validation.

- Text import currently loses spaces in SNMP OctetString values: the synthetic
  value `"lab  modem;a"` was exported as `"labmodem;a"` after import. This is
  separate from `PrettyPrint`, which now preserves quoted contents. Fixing
  formatter whitespace handling does not repair values already changed by import.
