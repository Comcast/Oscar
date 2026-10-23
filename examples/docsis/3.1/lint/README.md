# Text lint examples

These are synthetic syntax-test fragments authored for OSCAR lint. They are
**not complete provisioning profiles** and must not be deployed. Error examples
are deliberately malformed; even the clean-format examples lack the complete-file
minimum requirements. No shared secret or customer data is used.

| File | Expected findings (line:column) | Exit |
| --- | --- | --- |
| [missing-semicolon.txt](missing-semicolon.txt) | `MISSING_SEMICOLON` at 2:20 | 1 |
| [missing-quote.txt](missing-quote.txt) | `UNTERMINATED_STRING` at 2:30 | 1 |
| [invalid-snmp-values.txt](invalid-snmp-values.txt) | `SNMP_VALUE` at 2:30, 3:30 and 4:30 | 1 |
| [multiple-errors.txt](multiple-errors.txt) | `SNMP_VALUE` at 2:30, `UNTERMINATED_STRING` at 3:32, `MISSING_SEMICOLON` at 4:20 | 1 |
| [unknown-name.txt](unknown-name.txt) | None: `NetworkAccesss` deliberately demonstrates that lint does not validate registry names | 0 |
| [valid-format.txt](valid-format.txt) | None: syntax and value formats only | 0 |

Each error file ends with a valid leaf so you can see that checking continues.
The unknown-name example is syntactically valid but does not establish that the
name can be compiled. Empty placeholders are allowed by the grammar.

## Try one file

From a built checkout, select the standalone JAR without hard-coding a release:

```bash
cd ~/Projects/Oscar &&
jar_candidates=(target/oscar-*-all.jar) &&
oscar_jar="${jar_candidates[0]}" &&
java -jar "$oscar_jar" --lint-text \
  examples/docsis/3.1/lint/multiple-errors.txt
```

This Bash example expects a built standalone JAR under `target/`. If several
versions exist, set `oscar_jar` to the intended one explicitly. To use a downloaded
release instead, set `oscar_jar` to its path. Add `--lint-json` for structured
source ranges, diagnostic codes and accepted/skipped statement results.

An exit of `1` is expected for the error examples; it does not mean the lint tool
crashed. No binary is generated and the source remains unchanged.

## Try every file

```bash
cd ~/Projects/Oscar &&
jar_candidates=(target/oscar-*-all.jar) &&
oscar_jar="${jar_candidates[0]}" &&
for config in examples/docsis/3.1/lint/*.txt; do
  printf '\n%s\n' "$config"
  java -jar "$oscar_jar" --lint-text "$config" || {
    lint_status=$?
    if [ "$lint_status" -ne 1 ]; then
      break
    fi
  }
done
```

Verified against the built CLI: all six files returned the exit codes and
locations above; every error file retained its final accepted leaf. Source files
were unchanged. See [text lint documentation](../../../../docs/text-lint.md) for
recovery limits and the API contract.
