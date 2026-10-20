# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
import importlib.util
from pathlib import Path
import tempfile
import unittest

spec = importlib.util.spec_from_file_location("headers", Path(__file__).with_name("check-java-headers.py"))
headers = importlib.util.module_from_spec(spec)
spec.loader.exec_module(headers)

class HeaderTest(unittest.TestCase):
    def check(self, text):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "Example.java"
            path.write_text(text)
            return headers.problems(path)

    def test_missing_header_fails(self):
        self.assertTrue(self.check("package example; public class Example {}"))

    def test_spdx_header_passes(self):
        self.assertFalse(self.check("/*\n * Copyright 2026 Comcast Cable Communications Management, LLC\n * SPDX-License-Identifier: Apache-2.0\n */\npackage example;"))

    def test_generated_parser_is_exempt(self):
        self.assertFalse(self.check("// Generated from tlv.g4 by ANTLR 4.1\npackage example;"))

    def test_duplicate_notice_is_rejected(self):
        self.assertTrue(self.check("/* SPDX-License-Identifier: Apache-2.0\n SPDX-License-Identifier: Apache-2.0 */\npackage example;"))
