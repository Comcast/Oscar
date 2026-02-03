#!/usr/bin/env bash
set -euo pipefail

mvn -q -DskipTests package
mvn -q test
