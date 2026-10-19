# OSCAR DPoG implementation workflow

Read [source map](source-map.md) and the user-facing
[architecture/checklist](../../../docs/wiki/DPoE-&-DPoG.md).

## Implemented entry points

- `ANTLR/grammar/tlv.g4`: `DPoG` text header; generated Java lives under
  `src/com/comcast/oscar/parser/`. Regenerate as documented in `ANTLR/README.md`.
- `TlvConfigurationFileParser`: dedicated catalog selection and DPoG child
  encounter order. Duplicate child occurrences are diagnosed rather than overwritten.
- `DPoGTlvCodec`: bounded, ordered parsing and explicit unsupported-content errors.
- `DPoGCompiler`: required network/service-flow fields, QoS 0x07 validation,
  MIC replacement, end marker and padding. A nonempty key produces MICs;
  null/empty key selects unsigned output. Extended MIC needs a key.
- `ConfigurationFile`, `ConfigurationFileExport`, `TlvDisassemble`, and CLI
  `Specification`: DPoG-specific routing. Diagnostic family `dpog` is supported.
- `ConfigurationApiRoundTripTest` and `DPoGConfigurationTest`: synthetic API,
  byte-vector, order, malformed-data, signed/unsigned and extended-MIC tests.

## Boundaries

`dpog.json` is the DPoG applicability catalog. Newly added CMIM, vendor-ID,
DUT and extended-MIC fields cite their delegated wire sources. Other legacy
metadata still needs field-by-field verification; working codecs are not
whole-spec certification. Unknown content and repeated child encodings that
cannot be exported losslessly must fail explicitly. Do not silently remove them.

The OSCAR-UI integration remains a separate task in its repository. Do not
change its catalogs locally or claim that a JAR update alone enables its editor.
Device-specific GPON capacity and OMCI provisioning are outside this compiler.
