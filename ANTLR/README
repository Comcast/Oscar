# Generate the OSCAR text parser

Run the commands below from the repository root in Bash (Linux, macOS, or
Git Bash on Windows), with Java and curl available. For development setup,
see [Development](../docs/wiki/development.md).

## Source and generated files

- `grammar/tlv.g4` is the source grammar for OSCAR configuration text.
- `src/com/comcast/oscar/parser/` contains the generated Java and token files
  compiled by Maven, alongside the handwritten `TlvConfigurationFileParser.java`.
- `ANTLR/output/` is a historical output snapshot, not Maven's parser source.
- `RunTlvLex.sh` is a legacy example with caller-relative paths. Use the
  commands here instead.

The Maven runtime is currently ANTLR **4.1**. Generate with the matching **4.1
complete tool JAR**, not the runtime-only JAR or the latest ANTLR release.
Changing the tool/runtime version is a separate migration. Maven does not
regenerate the grammar automatically.

## Regenerate after editing tlv.g4

Download the matching tool into ignored build storage:

```bash
mkdir -p target/antlr-doc-check
curl --fail --location \
  --output target/antlr-doc-check/antlr-4.1-complete.jar \
  https://www.antlr.org/download/antlr-4.1-complete.jar
```

Edit `ANTLR/grammar/tlv.g4`, then generate into a staging directory. Running
from the grammar directory keeps machine-specific paths out of generated headers.

```bash
(
  cd ANTLR/grammar
  java -jar ../../target/antlr-doc-check/antlr-4.1-complete.jar \
    -o ../../target/antlr-doc-check/generated tlv.g4
)
```

Review generator errors/warnings and the staged files before replacing sources.
Copy only the six generated files; preserve the handwritten parser adapter:

```bash
for generated_file in tlvLexer.java tlvParser.java tlvListener.java \
    tlvBaseListener.java tlv.tokens tlvLexer.tokens; do
  cp "target/antlr-doc-check/generated/$generated_file" \
    "src/com/comcast/oscar/parser/$generated_file"
done
mvn test
```

Run generation successfully before running the copy block; do not copy stale
output after a failed generation. Review `git diff` and run the repository
formatting/hygiene checks described in the development guide. Commit the grammar,
all changed generated sources/token files, and relevant regression tests together.
Do not hand-edit generated Java to change parser behavior.

## Generate a new grammar file

For a separate grammar, create `ANTLR/grammar/example.g4`. Its grammar name must
match the filename. A minimal combined grammar using OSCAR's package convention:

```antlr
grammar example;

@lexer::header {
package com.comcast.oscar.parser;
}

@parser::header {
package com.comcast.oscar.parser;
}

configuration : WORD EOF;
WORD : [a-zA-Z]+;
WS : [ \t\r\n]+ -> skip;
```

Generate it independently:

```bash
(
  cd ANTLR/grammar
  java -jar ../../target/antlr-doc-check/antlr-4.1-complete.jar \
    -o ../../target/antlr-doc-check/example-generated example.g4
)
```

This produces `exampleLexer.java`, `exampleParser.java`, `exampleListener.java`,
`exampleBaseListener.java`, `example.tokens`, and `exampleLexer.tokens`.
The headers supply the package declaration, including for listeners; do not
also add `-package` with the same declaration.

A new grammar is not automatically used by OSCAR. Integrate its generated
classes and entry rule explicitly, add valid/invalid input tests, and run
`mvn test`. If extending existing configuration syntax, modify `tlv.g4` and
its adapter/tests instead of introducing an unused second parser. Keep separate
grammars in separate staging directories to avoid mixing generated output.
