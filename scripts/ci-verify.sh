#!/usr/bin/env bash
set -euo pipefail

# Format codebase
if [ -f "tools/google-java-format.jar" ]; then
  find src src/test/java -name '*.java' -print0 | xargs -0 java -jar tools/google-java-format.jar --replace
else
  echo "Missing tools/google-java-format.jar; run tools/setup-formatter.sh" >&2
  exit 1
fi

# Hygiene (lint/unused) and compile
tools/code-hygene-checker.sh

# Tests
./scripts/verify.sh
