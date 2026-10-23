# Generate the OSCAR text parser

Run the commands below from the repository root in Bash (Linux, macOS, or
Git Bash on Windows), with Java and curl available. For development setup,
see [Development](../docs/wiki/development.md).

## Source and generated files

- `grammar/tlv.g4` is the source grammar for OSCAR configuration text.
- `src/com/comcast/oscar/parser/` contains the generated Java and token files
  compiled by Maven, alongside the handwritten `TlvConfigurationFileParser.java`.
- `bin/ANT.xml` is an obsolete Eclipse export recipe, not a supported build path.
- `RunTlvLex.sh` regenerates the active parser from any working directory.

The generator and runtime share `antlr.version` in `pom.xml` (currently
**4.13.2**). Maven compiles the checked-in generated sources; it does not
regenerate them automatically. Never update the runtime without regenerating.

## Regenerate after editing tlv.g4

With Java 21, Python 3 and curl available, run from the repository root:

```bash
bash ANTLR/RunTlvLex.sh
tools/run-all.sh
```

The script reads the version from the POM, downloads the matching complete tool
JAR into ignored `target/antlr-generator/`, and generates into a fresh temporary
directory. Warnings or errors stop generation before any source is copied.
Only the four generated Java files and two token files are copied into `src/`;
the handwritten adapter is preserved. Interpreter `.interp` files are not needed
by the Java runtime. Temporary generation output is removed on exit.

Review the generated diff and commit the grammar, generated files and relevant
tests together. Do not hand-edit or format generated Java. To verify reproducible
generation, rerun the script and confirm that the generated files do not change.

The 4.13.2 migration removed the unused empty lexer rule `NO_WS : '';`, which
modern ANTLR rejects. A shared `@header` now supplies the package to the lexer,
parser and listeners. Other grammar rules retain their previous meaning.
Existing syntax, export and all-family round-trip tests cover compatibility;
numeric generated token IDs are implementation details, not stable API values.

## Generate a new grammar file

For a separate grammar, create `ANTLR/grammar/example.g4`. Its grammar name must
match the filename. A minimal combined grammar using OSCAR's package convention:

```antlr
grammar example;

@header {
package com.comcast.oscar.parser;
}

configuration : WORD EOF;
WORD : [a-zA-Z]+;
WS : [ \t\r\n]+ -> skip;
```

Run the main generation script above first to download the tool, then generate
it independently (from the repository root):

```bash
antlr_version=$(python3 -c 'import xml.etree.ElementTree as E; print(E.parse("pom.xml").getroot().findtext("{*}properties/{*}antlr.version"))')
(
  cd ANTLR/grammar
  java -jar "../../target/antlr-generator/antlr-$antlr_version-complete.jar" \
    -Werror -o ../../target/antlr-generator/example-generated example.g4
)
```

This produces `exampleLexer.java`, `exampleParser.java`, `exampleListener.java`,
`exampleBaseListener.java`, `example.tokens`, and `exampleLexer.tokens`.
The shared header supplies the package declaration, including for listeners; do not
also add `-package` with the same declaration.

A new grammar is not automatically used by OSCAR. Integrate its generated
classes and entry rule explicitly, add valid/invalid input tests, and run
`./mvnw test`. If extending existing configuration syntax, modify `tlv.g4` and
its adapter/tests instead of introducing an unused second parser. Keep separate
grammars in separate staging directories to avoid mixing generated output.
