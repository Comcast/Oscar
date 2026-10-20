#!/usr/bin/env python3
# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
"""Check the SPDX header on changed handwritten Java sources or explicit paths."""
from pathlib import Path
import re
import subprocess
import sys


def problems(path: Path) -> list[str]:
    text = path.read_text(encoding="utf-8")
    if text.startswith("// Generated from "):
        return []
    header = text.split("package ", 1)[0]
    errors = []
    if header.count("SPDX-License-Identifier: Apache-2.0") != 1:
        errors.append("expected one Apache-2.0 SPDX header before package")
    # HexDump retains the upstream Android attribution; never relabel it as Comcast-owned.
    android_notice = "Copyright (C) 2006 The Android Open Source Project" in header
    if not android_notice and not re.search(r"Copyright \d{4}(?: - \d{4})? Comcast Cable Communications Management, LLC", header):
        errors.append("missing Comcast copyright or malformed year range")
    if "@bannerLicense" in text:
        errors.append("remove the redundant legacy license banner")
    return errors


def main() -> int:
    root = Path(__file__).resolve().parents[2]
    if sys.argv[1:]:
        paths = [Path(argument) for argument in sys.argv[1:]]
    else:
        tracked = subprocess.check_output(["git", "diff", "--name-only", "--diff-filter=ACMR", "-z", "HEAD"], cwd=root)
        untracked = subprocess.check_output(["git", "ls-files", "--others", "--exclude-standard", "-z"], cwd=root)
        paths = [root / name.decode() for name in set((tracked + untracked).split(b"\0")) if name]
    errors = [f"{path}: {message}" for path in paths if path.suffix == ".java" and path.is_file() for message in problems(path)]
    for error in errors:
        print(error, file=sys.stderr)
    return bool(errors)


if __name__ == "__main__":
    sys.exit(main())
