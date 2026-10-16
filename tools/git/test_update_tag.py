from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


class UpdateTagTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.directory = Path(temporary.name)
        self.root = self.directory / "checkout"
        (self.root / "tools/git").mkdir(parents=True)
        source = Path(__file__).resolve().parents[2]
        for relative in ("tools/update-tag.sh", "tools/git/update-tag.sh"):
            shutil.copy2(source / relative, self.root / relative)
        self.git("init", "--quiet")
        self.git("config", "user.name", "Oscar tests")
        self.git("config", "user.email", "oscar@example.invalid")
        self.git("commit", "--quiet", "--allow-empty", "-m", "Initial fixture")

    def git(self, *arguments):
        return subprocess.run(["git", "-C", str(self.root), *arguments],
                              check=True, capture_output=True, text=True).stdout.strip()

    def script(self, *arguments, success=True):
        result = subprocess.run(
            ["bash", str(self.root / "tools/update-tag.sh"), *arguments],
            cwd=self.directory, check=False, capture_output=True, text=True,
        )
        if success:
            self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        else:
            self.assertNotEqual(result.returncode, 0)
        return result

    def add_remote(self):
        remote = self.directory / "origin.git"
        subprocess.run(["git", "init", "--bare", "--quiet", str(remote)],
                       check=True, capture_output=True, text=True)
        self.git("remote", "add", "origin", str(remote))
        return remote

    def test_creates_annotated_tag_at_head_from_another_directory(self):
        self.script("--tag", "v3.1.0")
        self.assertEqual(self.git("cat-file", "-t", "refs/tags/v3.1.0"), "tag")
        self.assertEqual(self.git("rev-parse", "v3.1.0^{commit}"), self.git("rev-parse", "HEAD"))

    def test_existing_tag_is_not_moved(self):
        self.script("-t", "v3.1.0")
        original = self.git("rev-parse", "refs/tags/v3.1.0")
        self.git("commit", "--quiet", "--allow-empty", "-m", "Next fixture")
        result = self.script("-t", "v3.1.0")
        self.assertIn("already exists locally", result.stdout)
        self.assertEqual(self.git("rev-parse", "refs/tags/v3.1.0"), original)

    def test_rejects_invalid_tags_without_creating_refs(self):
        for tag in ("3.1.0", "v3.1", "v3.1.0.1", "v3.a.0", "v3.1.0-rc1", "v3.1.0\n"):
            with self.subTest(tag=tag):
                self.script("--tag", tag, success=False)
        self.assertEqual(self.git("tag", "--list"), "")

    def test_rejects_missing_duplicate_and_unknown_arguments(self):
        for arguments in ((), ("-t",), ("--tag", "v1.0.0", "-t", "v2.0.0"), ("--force",)):
            with self.subTest(arguments=arguments):
                self.script(*arguments, success=False)
        self.assertEqual(self.git("tag", "--list"), "")

    def test_push_creates_missing_remote_tag_and_is_idempotent(self):
        self.add_remote()
        self.script("-t", "v3.1.0", "--push")
        original = self.git("rev-parse", "refs/tags/v3.1.0")
        self.assertIn(original, self.git("ls-remote", "--refs", "origin", "refs/tags/v3.1.0"))
        self.script("--tag", "v3.1.0", "--push")
        self.assertEqual(self.git("rev-parse", "refs/tags/v3.1.0"), original)

    def test_push_publishes_existing_local_tag(self):
        self.add_remote()
        self.script("-t", "v3.1.0")
        original = self.git("rev-parse", "refs/tags/v3.1.0")
        self.script("-t", "v3.1.0", "--push")
        self.assertIn(original, self.git("ls-remote", "--refs", "origin", "refs/tags/v3.1.0"))

    def test_fetches_remote_only_tag_without_recreating_it_on_head(self):
        self.add_remote()
        self.script("-t", "v3.1.0", "--push")
        original = self.git("rev-parse", "refs/tags/v3.1.0")
        self.git("tag", "-d", "v3.1.0")
        self.git("commit", "--quiet", "--allow-empty", "-m", "Next fixture")
        self.script("-t", "v3.1.0", "--push")
        self.assertEqual(self.git("rev-parse", "refs/tags/v3.1.0"), original)
        self.assertNotEqual(self.git("rev-parse", "v3.1.0^{commit}"), self.git("rev-parse", "HEAD"))

    def test_conflicting_tags_are_never_overwritten(self):
        self.add_remote()
        self.script("-t", "v3.1.0", "--push")
        remote_original = self.git("ls-remote", "--refs", "origin", "refs/tags/v3.1.0")
        self.git("tag", "-d", "v3.1.0")
        self.git("commit", "--quiet", "--allow-empty", "-m", "Next fixture")
        self.script("-t", "v3.1.0")
        local_original = self.git("rev-parse", "refs/tags/v3.1.0")
        result = self.script("-t", "v3.1.0", "--push", success=False)
        self.assertIn("refusing to overwrite", result.stderr)
        self.assertEqual(self.git("rev-parse", "refs/tags/v3.1.0"), local_original)
        self.assertEqual(self.git("ls-remote", "--refs", "origin", "refs/tags/v3.1.0"),
                         remote_original)

    def test_remote_failure_does_not_create_a_local_tag(self):
        self.script("-t", "v3.1.0", "--push", success=False)
        self.assertEqual(self.git("tag", "--list"), "")


if __name__ == "__main__":
    unittest.main()