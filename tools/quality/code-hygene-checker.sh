#!/usr/bin/env bash
set -euo pipefail

MAVEN_VERSION="3.9.9"
MAVEN_BASE="${HOME}/.local/apache-maven-${MAVEN_VERSION}"
JAVA_HOME_DEFAULT="${JAVA_HOME:-}"
LOCAL_JAVA_HOME="${HOME}/.local/temurin-21"

if [ -x "${MAVEN_BASE}/bin/mvn" ]; then
  PATH="${MAVEN_BASE}/bin:${PATH}"
fi

if [ -n "${JAVA_HOME_DEFAULT}" ]; then
  PATH="${JAVA_HOME_DEFAULT}/bin:${PATH}"
elif [ -x "${LOCAL_JAVA_HOME}/bin/javac" ]; then
  export JAVA_HOME="${LOCAL_JAVA_HOME}"
  PATH="${JAVA_HOME}/bin:${PATH}"
fi

mvn -DskipTests compile -DcompilerArgs="-Xlint:all -Werror"
