# API Overview

## Source Examples
- `src/com/comcast/oscar/examples/`

## Typical API Flow
1. Parse/import config text or binary into TLV structures.
2. Modify values or insert TLVs.
3. Export back to text or binary.

## Key Classes
- `ConfigurationFileImport`
- `ConfigurationFileExport`
- `ConfigurationFile`
- `TlvBuilder`
- `TlvDisassemble`
- `TlvAssembler`

## Notes
- CLI is the easiest path for operational usage.
- API is best for embedded tooling, automation, and custom transforms.
