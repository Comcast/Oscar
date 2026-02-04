#!/usr/bin/env bash
set -euo pipefail

"$(dirname "$0")/quality/markdown-check.sh" "$@"
