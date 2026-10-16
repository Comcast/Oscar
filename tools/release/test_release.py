import contextlib
import io
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

import release


class BumpVersionTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        (self.root / "pom.xml").write_text(
            '<project xmlns="http://maven.apache.org/POM/4.0.0">'
            '<artifactId>oscar</artifactId><version>3.1.0</version>'
            '<dependencies><dependency><artifactId>other</artifactId>'
            '<version>3.1.0</version></dependency></dependencies></project>',
            encoding="utf-8",
        )
        constants = self.root / release.CONSTANTS_FILE_PATH
        constants.parent.mkdir(parents=True)
        constants.write_text('public static final String OSCAR_VERSION = "3.1.0";\n')
        for relative_path in release.VERSION_DOCUMENTATION_PATHS:
            path = self.root / relative_path
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(b"Custom notes\r\noscar-3.1.0-all.jar v3.1.0 13.1.0 3.1.01\r\n")
        self.output = io.StringIO()
        self.redirect = contextlib.redirect_stdout(self.output)
        self.redirect.__enter__()
        self.addCleanup(self.redirect.__exit__, None, None, None)

    def snapshot(self):
        return {path: path.read_bytes() for path in self.root.rglob("*") if path.is_file()}

    def test_bump_calculations(self):
        for bump, expected in (("major", "4.0.0"), ("minor", "3.2.0"),
                               ("patch", "3.1.1"), ("maintenance", "3.1.1")):
            with self.subTest(bump=bump):
                self.assertEqual(release.bump_version("3.1.0", bump), expected)

    def test_updates_versions_and_docs_without_changing_dependencies_or_notes(self):
        release.bump_project_version(self.root, "3.2.0")
        self.assertEqual(release.read_version(self.root / "pom.xml"), "3.2.0")
        self.assertIn('<version>3.1.0</version></dependency>',
                      (self.root / "pom.xml").read_text())
        self.assertIn('OSCAR_VERSION = "3.2.0"',
                      (self.root / release.CONSTANTS_FILE_PATH).read_text())
        for relative_path in release.VERSION_DOCUMENTATION_PATHS:
            self.assertEqual((self.root / relative_path).read_bytes(),
                             b"Custom notes\r\noscar-3.2.0-all.jar v3.2.0 13.1.0 3.1.01\r\n")

    def test_dry_run_leaves_all_files_unchanged(self):
        before = self.snapshot()
        release.bump_project_version(self.root, "3.2.0", dry_run=True)
        self.assertEqual(self.snapshot(), before)
        self.assertIn("Preview: 3.1.0 -> 3.2.0", self.output.getvalue())

    def test_mismatched_constants_fail_before_writing(self):
        (self.root / release.CONSTANTS_FILE_PATH).write_text('OSCAR_VERSION = "2.0.0";\n')
        before = self.snapshot()
        with self.assertRaisesRegex(RuntimeError, "OSCAR_VERSION"):
            release.bump_project_version(self.root, "3.2.0")
        self.assertEqual(self.snapshot(), before)

    def test_missing_documentation_fails_before_writing(self):
        (self.root / release.VERSION_DOCUMENTATION_PATHS[-1]).unlink()
        before = self.snapshot()
        with self.assertRaises(FileNotFoundError):
            release.bump_project_version(self.root, "3.2.0")
        self.assertEqual(self.snapshot(), before)

    def test_invalid_or_nonincreasing_versions_fail_without_writes(self):
        before = self.snapshot()
        for target in ("3.1.0", "3.0.0", "3.2", "3.2.0-SNAPSHOT", "3.2.0\n"):
            with self.subTest(target=target), self.assertRaises(RuntimeError):
                release.bump_project_version(self.root, target)
            self.assertEqual(self.snapshot(), before)

    def test_bump_only_cli_never_runs_release_or_git_commands(self):
        with patch.object(release, "find_repo_root", return_value=self.root), \
             patch.object(release.sys, "argv", ["release.py", "--bump-only", "--bump", "minor"]), \
             patch.object(release, "build_release") as build, \
             patch.object(release, "run") as run:
            self.assertEqual(release.main(), 0)
            build.assert_not_called()
            run.assert_not_called()
        self.assertEqual(release.read_version(self.root / "pom.xml"), "3.2.0")

    def test_explicit_version_cli(self):
        with patch.object(release, "find_repo_root", return_value=self.root), \
             patch.object(release.sys, "argv", ["release.py", "--bump-only",
                                               "--set-version", "4.2.1"]):
            self.assertEqual(release.main(), 0)
        self.assertEqual(release.read_version(self.root / "pom.xml"), "4.2.1")

    def test_explicit_version_requires_bump_only(self):
        with patch.object(release.sys, "argv", ["release.py", "--set-version", "4.2.1"]), \
             contextlib.redirect_stderr(io.StringIO()), self.assertRaises(SystemExit) as error:
            release.parse_args()
        self.assertEqual(error.exception.code, 2)


if __name__ == "__main__":
    unittest.main()