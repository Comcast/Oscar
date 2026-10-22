#!/usr/bin/env bash
# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
set -euo pipefail

./mvnw -q -DskipTests package
python3 tools/quality/check-artifact-version.py
./mvnw -q test
