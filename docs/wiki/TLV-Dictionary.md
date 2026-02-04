# TLV Dictionary

OSCAR dictionary data is stored in SQLite:
- `db/dictionary.sqlite`

## Editing Guidance
- Back up the database before edits.
- Use a SQLite editor (for example DB Browser for SQLite).
- Keep naming and datatype choices consistent with parser expectations.

## Common Data Types
- `integer`
- `string`
- `string_null_terminated`
- `byte_array`
- `double_byte_array`
- `multi_tlv_byte_array`
- `oid`
- `transport_address_ipv4_address`
- `transport_address_ipv6_address`
- `mac_address`

## Validation Tip
After dictionary edits, run:
```bash
tools/run-all.sh
```
Then compile/decompile a sample config to verify behavior.
