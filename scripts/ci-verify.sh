#!/usr/bin/env bash
set -euo pipefail

# Format codebase
if [ -f "tools/google-java-format.jar" ]; then
  FORMAT_DIRS=()
  [ -d "src" ] && FORMAT_DIRS+=("src")
  [ -d "src/test/java" ] && FORMAT_DIRS+=("src/test/java")

  if [ "${#FORMAT_DIRS[@]}" -gt 0 ]; then
    find "${FORMAT_DIRS[@]}" -name '*.java' -print0 \
      | xargs -0 java -jar tools/google-java-format.jar --replace
  fi
else
  echo "Missing tools/google-java-format.jar; run tools/setup-formatter.sh" >&2
  exit 1
fi

# Hygiene (lint/unused) and compile
tools/code-hygene-checker.sh

# Tests
./scripts/verify.sh
