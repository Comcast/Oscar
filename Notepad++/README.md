# Oscar syntax highlighting for Notepad++

Import `OSCAR` using **Language > User Defined Language > Define your
language > Import**, then restart Notepad++ and select **Language > OSCAR**.
If the file picker filters for XML files, select all files or copy `OSCAR`
to `OSCAR.xml` before importing. Remove an existing OSCAR language definition
before importing its replacement.

The language highlights all seven configuration headers accepted by Oscar,
including `Docsis-4.0`, TLV names from the four JSON catalogs under
`res/tlv-registry/`, and SNMP keywords. It supports brace folding,
`/* block comments */`, quoted values, numbers, and punctuation. `Snmp11`
and `Snmp64` are keywords, not comments. The parser does not support `//`
line comments.

No file extension is assigned automatically; select OSCAR for configuration
text files manually. Colors target a light background.

Highlighting combines catalog names for convenience; it does not validate
whether a name is legal for a particular version or parent TLV. Compile and
decompile with the intended Oscar version to check the configuration.

When the catalogs or `ANTLR/grammar/tlv.g4` change, refresh `Keywords1`
from the grammar's `configType`, `Keywords2` from the unique catalog names,
and `Keywords3` from `snmpType` and `dataType`. Exclude the SNMP keywords
from `Keywords2` so their separate styling takes effect.

See the [Notepad++ UDL documentation](https://npp-user-manual.org/docs/user-defined-language-system/)
for language import and customization details.
