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

install_local_jdk21() {
  local jdk_base="${HOME}/.local/temurin-21"
  local jdk_tgz="${HOME}/.local/temurin21.tgz"
  local url="https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse"

  if [ -x "${jdk_base}/bin/javac" ]; then
    return 0
  fi

  mkdir -p "${HOME}/.local"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$url" -o "$jdk_tgz"
  else
    wget -qO "$jdk_tgz" "$url"
  fi

  tar -xzf "$jdk_tgz" -C "${HOME}/.local"
  rm -f "$jdk_tgz"

  local extracted_dir
  extracted_dir="$(find "${HOME}/.local" -maxdepth 1 -type d -name 'jdk-21*' | head -n1 || true)"
  if [ -n "${extracted_dir:-}" ] && [ ! -d "$jdk_base" ]; then
    mv "$extracted_dir" "$jdk_base"
  fi
}

install_local_maven() {
  local maven_version="3.9.9"
  local maven_base="${HOME}/.local/apache-maven-${maven_version}"
  local maven_tgz="${HOME}/.local/maven.tgz"
  local url="https://archive.apache.org/dist/maven/maven-3/${maven_version}/binaries/apache-maven-${maven_version}-bin.tar.gz"

  if [ -x "${maven_base}/bin/mvn" ]; then
    return 0
  fi

  mkdir -p "${HOME}/.local"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$url" -o "$maven_tgz"
  else
    wget -qO "$maven_tgz" "$url"
  fi

  tar -xzf "$maven_tgz" -C "${HOME}/.local"
  rm -f "$maven_tgz"
}

ensure_maven() {
  if command -v mvn >/dev/null 2>&1; then
    mvn -v
    return 0
  fi

  if [ "$(uname -s 2>/dev/null || echo "")" = "Linux" ]; then
    if command -v sudo >/dev/null 2>&1; then
      if sudo -n true 2>/dev/null; then
        sudo apt-get update -y
        sudo apt-get install -y maven
      else
        install_local_maven
      fi
    else
      install_local_maven
    fi
  else
    echo "Maven is not installed and this installer only supports auto-install on Linux."
    return 1
  fi
}

case "$(uname -s 2>/dev/null || echo "")" in
  Linux)
    # Keep existing Java 21 if already available.
    if ! verify_java21 >/dev/null 2>&1; then
      if command -v sudo >/dev/null 2>&1 && sudo -n true 2>/dev/null; then
        "$(dirname "$0")/scripts/install-jdk21-linux.sh"
      else
        install_local_jdk21
        export JAVA_HOME="${HOME}/.local/temurin-21"
        export PATH="${JAVA_HOME}/bin:${PATH}"
      fi
      verify_java21
    else
      verify_java21
    fi
    ensure_maven
    exit 0
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
      exit 0
    fi
    ;;
esac

echo "Unsupported or unknown OS."
echo "Linux: ./scripts/install-jdk21-linux.sh"
echo "Windows: powershell -NoProfile -ExecutionPolicy Bypass -File .\\scripts\\install-jdk21-windows.ps1"
exit 1
