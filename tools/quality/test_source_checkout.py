#!/usr/bin/env python3
# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
"""Keep hashed specification sources byte-identical in Windows-style checkouts."""
import pathlib
import subprocess
import tempfile
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]


class SpecificationCheckoutTest(unittest.TestCase):
    def test_windows_checkout_preserves_source_bytes(self):
        """Exercise Git's real conversion with both historical CRLF and LF sources."""
        for source_bytes in (b'Specification\nSecond line\n', b'Specification\r\nSecond line\r\n'):
            with self.subTest(source_bytes=source_bytes), tempfile.TemporaryDirectory() as temporary:
                checkout = pathlib.Path(temporary)
                subprocess.run(['git', 'init', '-q', str(checkout)], check=True)
                (checkout / '.gitattributes').write_bytes((ROOT / '.gitattributes').read_bytes())
                relative = 'skills/example/references/sources/specification.txt'
                source = checkout / relative
                source.parent.mkdir(parents=True)
                source.write_bytes(source_bytes)
                subprocess.run(['git', '-c', 'core.autocrlf=false', '-C', str(checkout),
                                'add', '.'], check=True, capture_output=True)
                source.unlink()
                subprocess.run(['git', '-c', 'core.autocrlf=true', '-C', str(checkout),
                                'checkout-index', '--', relative], check=True, capture_output=True)
                self.assertEqual(source_bytes, source.read_bytes())


if __name__ == '__main__':
    unittest.main()
