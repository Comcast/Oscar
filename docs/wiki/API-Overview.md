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
- `DocsisMicVerifier`

## Notes
- CLI is the easiest path for operational usage.
- API is best for embedded tooling, automation, and custom transforms.

## Check a shared secret against a binary

Call `DocsisMicVerifier.verify(binary, profile, secretBytes)` with the original
binary and `Profile.DOCSIS_31` or `Profile.DOCSIS_40`. Do not recompile the file
before verification: doing so generates new MICs with the supplied key.

The result has separate `cm()`, `cmts()` and `extended()` statuses. A CM match
alone does not check the secret. A mismatch can indicate either a different key
or modified covered data. Missing and unsupported MICs are reported explicitly.
For separate legacy and extended keys, use the four-argument overload.

See [MIC verification and generation](../mic-verification.md) for imports,
examples, status definitions and supported algorithms. This checker is a Java
API; it is not exposed by the CLI or OSCAR-UI yet.

## Generate extended MICs

Construct `ConfigurationFile` with `DOCSIS_VER_31` or `DOCSIS_VER_40`, the
imported TLVs and a nonempty shared secret, then call `commit()` before reading
`toByteArray()`. Include TLV 43.6 parameters to select implicit or explicit
HMAC-MD5/MMH. The [generation guide](../mic-verification.md#generate-extended-mics-in-docsis-31-and-40)
shows the D3.1 names and bitmap syntax. D4.0 retains its separate catalog names.

## Damaged binary diagnostics

See [Binary TLV diagnostics](Binary-TLV-Diagnostics.md) for bounded, read-only
partial inspection through `TlvDisassemble.diagnose`.

## Complete DOCSIS file validation

Use the public [minimum validator](../docsis-minimum-validation.md) for text or
binary files and in-memory editor content. It checks top-level required settings
without generating MICs or requiring a secret. Compilation enforces the same rule.
