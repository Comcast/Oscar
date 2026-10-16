# TLV Dictionary

Oscar's parser and codecs now use startup-loaded JSON symbol tables under
`res/tlv-registry/`. See [TLV symbol tables](../tlv-symbol-tables.md) for the schema,
editing workflow, external-directory option, and migration compatibility notes.

SQLite dictionaries and the JDBC dependency are no longer supplied. Normal
parser, encoder, decoder, and bulk operations use the JSON catalogs. Legacy SQL
API callers must supply their own driver and database; dictionary export is no
longer supported.
