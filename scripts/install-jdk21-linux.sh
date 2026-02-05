#!/usr/bin/env bash
set -euo pipefail

get_java_major() {
  if ! command -v java >/dev/null 2>&1; then
    return 1
  fi
  java -version 2>&1 | head -n1 | sed -E 's/.*"([0-9]+).*/\1/'
}

get_javac_major() {
  if ! command -v javac >/dev/null 2>&1; then
    return 1
  fi
  javac -version 2>&1 | head -n1 | sed -E 's/.* ([0-9]+).*/\1/'
}

existing_java_major="$(get_java_major || true)"
existing_javac_major="$(get_javac_major || true)"
if [ "${existing_java_major:-}" = "21" ] && [ "${existing_javac_major:-}" = "21" ]; then
  echo "JDK 21 already installed:"
  java -version
  javac -version
  exit 0
fi

if command -v apt-get >/dev/null 2>&1; then
  sudo apt-get update -y
  sudo apt-get install -y openjdk-21-jdk-headless
elif command -v dnf >/dev/null 2>&1; then
  sudo dnf install -y java-21-openjdk-devel
elif command -v yum >/dev/null 2>&1; then
  sudo yum install -y java-21-openjdk-devel
elif command -v zypper >/dev/null 2>&1; then
  sudo zypper install -y java-21-openjdk-devel
elif command -v pacman >/dev/null 2>&1; then
  sudo pacman -S --noconfirm jdk-openjdk
elif command -v apk >/dev/null 2>&1; then
  sudo apk add --no-cache openjdk21-jdk
else
  echo "No supported package manager found."
  echo "Install a JDK 21 package using your distro tools, then re-run."
  exit 1
fi

installed_major="$(get_java_major || true)"
installed_javac_major="$(get_javac_major || true)"
if [ -z "${installed_major:-}" ] || [ -z "${installed_javac_major:-}" ]; then
  echo "JDK install finished but java/javac is still not on PATH."
  echo "Ensure JAVA_HOME and PATH are set, then re-run."
  exit 1
fi

if [ "${installed_major}" != "21" ] || [ "${installed_javac_major}" != "21" ]; then
  echo "JDK is installed but not version 21 (detected java=${installed_major}, javac=${installed_javac_major})."
  echo "Remove the other Java or adjust PATH/JAVA_HOME, then re-run."
  exit 1
fi

java -version
javac -version
