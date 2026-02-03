#!/usr/bin/env bash
set -euo pipefail

get_java_major() {
  if ! command -v java >/dev/null 2>&1; then
    return 1
  fi
  java -version 2>&1 | head -n1 | sed -E 's/.*"([0-9]+).*/\1/'
}

verify_java21() {
  local major
  major="$(get_java_major || true)"
  if [ -z "${major:-}" ]; then
    echo "Java is not on PATH after install."
    return 1
  fi
  if [ "${major}" != "21" ]; then
    echo "Java is installed but not version 21 (detected ${major})."
    return 1
  fi
  java -version
}

case "$(uname -s 2>/dev/null || echo "")" in
  Linux)
    "$(dirname "$0")/scripts/install-jdk21-linux.sh"
    verify_java21
    ;;
  Darwin)
    echo "macOS is not supported by this installer yet."
    echo "Install a Java 21 JRE manually."
    exit 1
    ;;
  MINGW*|MSYS*|CYGWIN*|Windows_NT)
    if command -v powershell >/dev/null 2>&1; then
      echo "Windows installer currently installs JDK 21, not JRE."
      powershell -NoProfile -ExecutionPolicy Bypass -File ".\\scripts\\install-jdk21-windows.ps1"
      verify_java21
    fi
    ;;
esac

echo "Unsupported or unknown OS."
echo "Linux: ./scripts/install-jdk21-linux.sh"
echo "Windows: powershell -NoProfile -ExecutionPolicy Bypass -File .\\scripts\\install-jdk21-windows.ps1"
exit 1
