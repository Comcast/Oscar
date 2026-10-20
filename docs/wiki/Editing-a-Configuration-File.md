# Editing a Configuration File

## Recommended Workflow
1. Generate a template for your spec/version (`-ftd`).
2. Edit with a plain text editor.
3. Compile with `-c` and verify by decompiling with `-d`.

## Editor Support
A Notepad++ user-defined language file is available in this repo:
- `Notepad++/`

Legacy editor XML can still be used if preferred.

## Quoted string values

Use double quotes for text containing URL punctuation, spaces, or delimiters:

```text
URL "https://acs.example.test:7547/cwmp?unit=lab&mode=1";
Password "lab password";
```

Quotes are syntax, not encoded value bytes. Supported escapes are `\"`, `\\`,
`\n`, `\r`, and `\t`. Literal newlines and unsupported escapes inside quotes
are rejected. Existing unquoted identifiers remain supported. SNMP values also
use double quotes; whitespace inside them is preserved. Export escapes strings
when necessary, including literal comment delimiters, so they can be reimported.
Empty quoted ordinary TLVs remain subject to the legacy zero-length TLV removal
behavior; this change does not enable zero-length binary settings.
