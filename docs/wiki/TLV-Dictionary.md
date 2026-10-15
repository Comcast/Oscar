# TLV Dictionary

Oscar's parser and codecs now use startup-loaded JSON symbol tables under
`res/tlv-registry/`. See [TLV symbol tables](../tlv-symbol-tables.md) for the schema,
editing workflow, external-directory option, and migration compatibility notes.

The SQLite dictionaries remain available to legacy SQL API callers. They are no
longer the definition source for normal parser, encoder, decoder, or bulk operations.
