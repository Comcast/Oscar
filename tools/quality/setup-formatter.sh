#!/usr/bin/env bash
set -euo pipefail

mkdir -p tools
if [ ! -f tools/google-java-format.jar ]; then
  curl -fsSL https://github.com/google/google-java-format/releases/download/v1.22.0/google-java-format-1.22.0-all-deps.jar -o tools/google-java-format.jar
fi
