#!/usr/bin/env bash
# Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
set -euo pipefail

repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
antlr_version="$(python3 - "$repo_root/pom.xml" <<'PY'
import sys
import xml.etree.ElementTree as ET
root = ET.parse(sys.argv[1]).getroot()
print(root.findtext('{*}properties/{*}antlr.version'))
PY
)"
build_dir="$repo_root/target/antlr-generator"
tool_jar="$build_dir/antlr-$antlr_version-complete.jar"
mkdir -p "$build_dir"
if [[ ! -f "$tool_jar" ]]; then
  curl --fail --location "https://www.antlr.org/download/antlr-$antlr_version-complete.jar" \
    --output "$tool_jar.download"
  mv -- "$tool_jar.download" "$tool_jar"
fi
staging_dir="$(mktemp -d "$build_dir/generated.XXXXXX")"
trap 'rm -rf -- "$staging_dir"' EXIT
(
  cd "$repo_root/ANTLR/grammar"
  java -jar "$tool_jar" -Werror -o "$staging_dir" tlv.g4
)
# Copy only generated sources; preserve the handwritten listener adapter.
for generated_file in tlvLexer.java tlvParser.java tlvListener.java \
    tlvBaseListener.java tlv.tokens tlvLexer.tokens; do
  cp -- "$staging_dir/$generated_file" "$repo_root/src/com/comcast/oscar/parser/$generated_file"
done
printf 'Regenerated OSCAR parser with ANTLR %s. Run tools/run-all.sh next.\n' "$antlr_version"
