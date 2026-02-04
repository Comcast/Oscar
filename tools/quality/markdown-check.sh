#!/usr/bin/env bash
set -euo pipefail

if ! command -v npx >/dev/null 2>&1; then
  echo "npx is required for markdown checking. Run ./install.sh --development." >&2
  exit 1
fi

npx -y markdownlint-cli2 "**/*.md" "#node_modules" "#target" "#dist"
