#!/usr/bin/env bash
# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
set -euo pipefail

REPO_ROOT="$(git -C "$(dirname "$0")" rev-parse --show-toplevel)"
cd "$REPO_ROOT"
CURRENT_YEAR="$(date +%Y)"
COPYRIGHT_ONLY=false
if [[ "${1:-}" == "--copyright-only" ]]; then
  COPYRIGHT_ONLY=true
  shift
fi

copyright_files() {
  if [[ "$#" -gt 0 ]]; then
    printf '%s\0' "$@"
  else
    git diff --name-only --diff-filter=ACMR -z HEAD
    git ls-files --others --exclude-standard -z
  fi
}

python3 tools/quality/check-java-headers.py "$@"

COPYRIGHT_FAILED=0
while IFS= read -r -d '' file; do
  case "$file" in
    skills/*/references/sources/*|.agents/*|.claude/*) continue ;;
  esac
  [[ -f "$file" && ! -L "$file" ]] || continue
  if ! LC_ALL=C awk -v year="$CURRENT_YEAR" '
    match($0, /Copyright[ \t]+[0-9][0-9][0-9][0-9]([ \t]*-[ \t]*[0-9][0-9][0-9][0-9])?[ \t]+Comcast Cable Communications Management, LLC/) {
      banner = substr($0, RSTART, RLENGTH)
      years = banner
      sub(/^Copyright[ \t]+/, "", years)
      sub(/[ \t]+Comcast Cable Communications Management, LLC$/, "", years)
      split(years, endpoints, /[ \t]*-[ \t]*/)
      expected = endpoints[1] == year ? year : endpoints[1] " - " year
      if (years != expected) {
        printf "%s:%d: copyright years must be %s (found %s)\n", FILENAME, FNR, expected, years > "/dev/stderr"
        failed = 1
      }
    }
    END { exit failed }
  ' "$file"; then
    COPYRIGHT_FAILED=1
  fi
done < <(copyright_files "$@")
if [[ "$COPYRIGHT_FAILED" -ne 0 ]]; then
  exit 1
fi
if [[ "$COPYRIGHT_ONLY" == true ]]; then
  exit 0
fi

JAVA_HOME_DEFAULT="${JAVA_HOME:-}"
LOCAL_JAVA_HOME="${HOME}/.local/temurin-21"

if [ -n "${JAVA_HOME_DEFAULT}" ]; then
  PATH="${JAVA_HOME_DEFAULT}/bin:${PATH}"
elif [ -x "${LOCAL_JAVA_HOME}/bin/javac" ]; then
  export JAVA_HOME="${LOCAL_JAVA_HOME}"
  PATH="${JAVA_HOME}/bin:${PATH}"
fi

./mvnw -DskipTests compile -DcompilerArgs="-Xlint:all -Werror"
