#!/usr/bin/env bash
set -euo pipefail

mvn -q -DskipTests package
python3 tools/quality/check-artifact-version.py
mvn -q test
