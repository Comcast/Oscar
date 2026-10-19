from pathlib import Path
import os
import shutil
import subprocess
import tempfile
import unittest


class GitPushTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.base = Path(self.temp.name)
        self.root = self.base / 'checkout'
        (self.root / 'tools/git').mkdir(parents=True)
        shutil.copy2(Path(__file__).with_name('git-push.sh'), self.root / 'tools/git/git-push.sh')
        (self.root / 'tools/run-all.sh').write_text('echo checked >> "$CHECK_LOG"\nexit "${CHECK_STATUS:-0}"\n')
        self.bin = self.base / 'bin'
        self.bin.mkdir()
        for name in ('mvn', 'python3'):
            script = self.bin / name
            script.write_text('#!/bin/sh\nexit 0\n')
            script.chmod(0o755)
        self.env = dict(os.environ, PATH=str(self.bin) + os.pathsep + os.environ['PATH'],
                        CHECK_LOG=str(self.base / 'checks'))
        self.git('init', '-b', 'master')
        self.git('config', 'user.name', 'Test')
        self.git('config', 'user.email', 'test@example.invalid')
        self.commit()
        self.remote = self.base / 'origin.git'
        subprocess.run(['git', 'init', '--bare', str(self.remote)], check=True, capture_output=True)
        self.git('remote', 'add', 'origin', str(self.remote))

    def git(self, *args):
        return subprocess.run(['git', '-C', str(self.root), *args], check=True,
                              capture_output=True, text=True).stdout.strip()

    def commit(self):
        self.git('add', '.')
        self.git('commit', '--allow-empty', '-m', 'Fixture')

    def run_helper(self, success=True, *args):
        result = subprocess.run(['bash', str(self.root / 'tools/git/git-push.sh'), *(args or ('--master',))],
                                cwd=self.base, env=self.env, capture_output=True, text=True)
        self.assertEqual(result.returncode == 0, success, result.stdout + result.stderr)
        return result

    def test_push_and_noop_skip_checks(self):
        self.git('tag', '-a', 'v1.0.0', '-m', 'Tag')
        self.git('config', 'push.followTags', 'true')
        self.run_helper()
        self.assertEqual(self.git('ls-remote', '--tags', 'origin'), '')
        self.assertIn('Nothing to push', self.run_helper().stdout)
        self.assertEqual((self.base / 'checks').read_text(), 'checked\n')

    def test_dirty_tree_rejected_before_checks(self):
        (self.root / 'untracked').write_text('dirty')
        self.run_helper(False)
        self.assertFalse((self.base / 'checks').exists())

    def test_failed_checks_block_push(self):
        self.env['CHECK_STATUS'] = '1'
        self.run_helper(False)
        self.assertEqual(self.git('ls-remote', '--heads', 'origin'), '')

    def test_behind_branch_rejected_before_checks(self):
        initial = self.git('rev-parse', 'HEAD')
        self.commit()
        self.git('push', 'origin', 'master')
        self.git('reset', '--hard', initial)
        self.run_helper(False)
        self.assertFalse((self.base / 'checks').exists())

    def test_other_branch_and_invalid_mode_rejected(self):
        self.git('checkout', '-b', 'feature')
        self.run_helper(False)
        self.run_helper(False, '--tag-patch')
        self.assertFalse((self.base / 'checks').exists())

    def test_formatting_changes_block_push(self):
        (self.root / 'tools/run-all.sh').write_text('echo changed > changed.txt\n')
        self.commit()
        self.run_helper(False)
        self.assertEqual(self.git('ls-remote', '--heads', 'origin'), '')
