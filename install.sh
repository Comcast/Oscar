#!/usr/bin/env bash
set -euo pipefail

BUILD_OSCAR_JAR=0
DEVELOPMENT_SETUP=0

usage() {
  cat <<'EOF'
Usage: ./install.sh [options]

Options:
  -b, --build-oscar-jar   Build a local oscar.jar after dependency install
  -d, --development       Install developer tooling prerequisites (python3, nodejs, npm)
  -h, --help              Show this help message
EOF
}

while [ $# -gt 0 ]; do
  case "$1" in
    -b|--build-oscar-jar)
      BUILD_OSCAR_JAR=1
      ;;
    -d|--development)
      DEVELOPMENT_SETUP=1
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
  shift
done

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

configure_local_tool_paths() {
  local env_file="${HOME}/.oscar-env"
  local marker_start="# >>> oscar local tools >>>"
  local marker_end="# <<< oscar local tools <<<"

  cat > "${env_file}" <<'EOF'
#!/usr/bin/env bash
if [ -d "${HOME}/.local/temurin-21/bin" ]; then
  export JAVA_HOME="${HOME}/.local/temurin-21"
  case ":${PATH}:" in
    *":${JAVA_HOME}/bin:"*) ;;
    *) export PATH="${JAVA_HOME}/bin:${PATH}" ;;
  esac
fi

if [ -d "${HOME}/.local/apache-maven-3.9.9/bin" ]; then
  case ":${PATH}:" in
    *":${HOME}/.local/apache-maven-3.9.9/bin:"*) ;;
    *) export PATH="${HOME}/.local/apache-maven-3.9.9/bin:${PATH}" ;;
  esac
fi

if [ -d "${HOME}/.local/node-20/bin" ]; then
  case ":${PATH}:" in
    *":${HOME}/.local/node-20/bin:"*) ;;
    *) export PATH="${HOME}/.local/node-20/bin:${PATH}" ;;
  esac
fi
EOF
  chmod +x "${env_file}"

  for shell_rc in "${HOME}/.bashrc" "${HOME}/.profile"; do
    touch "${shell_rc}"
    if ! grep -Fq "${marker_start}" "${shell_rc}"; then
      {
        echo ""
        echo "${marker_start}"
        echo "[ -f \"${env_file}\" ] && . \"${env_file}\""
        echo "${marker_end}"
      } >> "${shell_rc}"
    fi
  done

  # Apply to current shell process for immediate script use.
  # shellcheck source=/dev/null
  . "${env_file}"
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

ensure_snmptranslate() {
  if command -v snmptranslate >/dev/null 2>&1; then
    snmptranslate -V 2>/dev/null || true
    return 0
  fi

  if [ "$(uname -s 2>/dev/null || echo "")" = "Linux" ]; then
    if command -v sudo >/dev/null 2>&1 && sudo -n true 2>/dev/null; then
      sudo apt-get update -y
      sudo apt-get install -y snmp
    fi
  fi

  if command -v snmptranslate >/dev/null 2>&1; then
    snmptranslate -V 2>/dev/null || true
    return 0
  fi

  echo "Net-SNMP (snmptranslate) is required but could not be auto-installed." >&2
  echo "Install package 'snmp' and re-run: ./install.sh" >&2
  return 1
}

ensure_python3() {
  if command -v python3 >/dev/null 2>&1; then
    python3 --version
    return 0
  fi

  if [ "$(uname -s 2>/dev/null || echo "")" = "Linux" ]; then
    if command -v sudo >/dev/null 2>&1 && sudo -n true 2>/dev/null; then
      sudo apt-get update -y
      sudo apt-get install -y python3
      python3 --version
      return 0
    fi
  fi

  echo "Python3 is required for --development but could not be auto-installed." >&2
  echo "Install python3 and re-run: ./install.sh --development" >&2
  return 1
}

get_node_major() {
  if ! command -v node >/dev/null 2>&1; then
    return 1
  fi
  node -v | sed -E 's/^v([0-9]+).*/\1/'
}

install_local_node20() {
  local node_base="${HOME}/.local/node-20"
  local node_tgz="${HOME}/.local/node20.tar.xz"
  local shasums_url="https://nodejs.org/dist/latest-v20.x/SHASUMS256.txt"
  local tar_name=""
  local url=""

  if [ -x "${node_base}/bin/node" ]; then
    return 0
  fi

  mkdir -p "${HOME}/.local"
  if command -v curl >/dev/null 2>&1; then
    tar_name="$(curl -fsSL "$shasums_url" | awk '/linux-x64.tar.xz$/ {print $2; exit}')"
  else
    tar_name="$(wget -qO- "$shasums_url" | awk '/linux-x64.tar.xz$/ {print $2; exit}')"
  fi

  if [ -z "${tar_name:-}" ]; then
    echo "Unable to resolve latest Node 20 Linux tarball from ${shasums_url}" >&2
    return 1
  fi

  url="https://nodejs.org/dist/latest-v20.x/${tar_name}"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$url" -o "$node_tgz"
  else
    wget -qO "$node_tgz" "$url"
  fi

  tar -xJf "$node_tgz" -C "${HOME}/.local"
  rm -f "$node_tgz"

  local extracted_dir
  extracted_dir="$(find "${HOME}/.local" -maxdepth 1 -type d -name 'node-v20.*-linux-x64' | sort | tail -n1 || true)"
  if [ -n "${extracted_dir:-}" ] && [ ! -d "$node_base" ]; then
    mv "$extracted_dir" "$node_base"
  fi
}

ensure_node20() {
  local major
  major="$(get_node_major || true)"
  if [ -n "${major:-}" ] && [ "$major" -ge 20 ]; then
    return 0
  fi

  if [ "$(uname -s 2>/dev/null || echo "")" = "Linux" ]; then
    # Ubuntu apt may provide Node 12; install local Node 20 for markdownlint-cli2 compatibility.
    install_local_node20
    export PATH="${HOME}/.local/node-20/bin:${PATH}"
    major="$(get_node_major || true)"
    if [ -n "${major:-}" ] && [ "$major" -ge 20 ]; then
      return 0
    fi
  fi

  echo "Node.js >=20 is required for markdownlint-cli2." >&2
  return 1
}

ensure_markdownlint() {
  ensure_node20

  if command -v markdownlint-cli2 >/dev/null 2>&1; then
    markdownlint-cli2 --version >/dev/null
    echo "markdownlint-cli2 available"
    return 0
  fi

  if command -v npx >/dev/null 2>&1; then
    npx -y markdownlint-cli2 --version >/dev/null
    echo "markdownlint-cli2 available via npx"
    return 0
  fi

  echo "markdown checker setup failed: markdownlint-cli2 unavailable after Node setup." >&2
  return 1
}

build_oscar_jar() {
  echo "Building oscar.jar ..."
  mvn -q -DskipTests package

  local source_jar=""
  source_jar="$(find dist target -maxdepth 1 -type f -name '*-all.jar' | head -n1 || true)"

  if [ -z "${source_jar}" ]; then
    echo "Build completed, but shaded jar was not found in dist/ or target/." >&2
    return 1
  fi

  cp "${source_jar}" oscar.jar
  echo "Created oscar.jar from ${source_jar}"
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
    ensure_snmptranslate
    configure_local_tool_paths

    if [ "${DEVELOPMENT_SETUP}" -eq 1 ]; then
      ensure_python3
      ensure_markdownlint
    fi

    if [ "${BUILD_OSCAR_JAR}" -eq 1 ]; then
      build_oscar_jar
    fi

    echo "Installation complete."
    echo "Run: source \"${HOME}/.oscar-env\""
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
