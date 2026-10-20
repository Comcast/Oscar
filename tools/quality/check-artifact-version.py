#!/usr/bin/env python3
# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
"""Verify version metadata in both packaged artifacts against the Maven POM."""
from pathlib import Path
import xml.etree.ElementTree as ET
from zipfile import ZipFile


def main() -> None:
    root = Path(__file__).resolve().parents[2]
    version = ET.parse(root / "pom.xml").getroot().findtext("{http://maven.apache.org/POM/4.0.0}version")
    for suffix in ("", "-all"):
        path = root / "target" / f"oscar-{version}{suffix}.jar"
        with ZipFile(path) as jar:
            properties = jar.read("oscar-build.properties").decode().splitlines()
            if f"version={version}" not in properties:
                raise RuntimeError(f"{path}: build version differs from pom.xml")
            manifest = jar.read("META-INF/MANIFEST.MF").decode().replace("\r\n ", "")
            if f"Implementation-Version: {version}" not in manifest.splitlines():
                raise RuntimeError(f"{path}: manifest version differs from pom.xml")
        print(f"[QA] {path.name}: version metadata verified")


if __name__ == "__main__":
    main()
