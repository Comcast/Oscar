"""Offline link and Git integration checks for wiki publication."""
import contextlib
import importlib.util
import io
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch

spec = importlib.util.spec_from_file_location('update_wiki', Path(__file__).with_name('update-wiki.py'))
wiki = importlib.util.module_from_spec(spec)
spec.loader.exec_module(wiki)


class WikiTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name) / 'project'
        self.source = self.root / 'docs/wiki'
        self.source.mkdir(parents=True)
        (self.root / 'LICENSE').write_text('license')
        (self.source / 'Home.md').write_text('[Help](./Help.md#usage)\n[License](../../LICENSE)\n')
        (self.source / 'Help.md').write_text('# Help\n')
        (self.source / 'AGENTS.md').write_text('Private authoring instructions')

    def render(self, text):
        return wiki.render(text, self.source / 'Home.md', self.source, self.root,
                           'https://github.com/example/Oscar', 'release/test')

    def test_links_and_fragment(self):
        rendered = self.render((self.source / 'Home.md').read_text())
        self.assertIn('/wiki/Help#usage', rendered)
        self.assertIn('/blob/release%2Ftest/LICENSE', rendered)

    def test_code_external_and_anchors_are_unchanged(self):
        text = ('```markdown\n[x](missing.md)\n```\n`[x](missing.md)`\n'
                '[web](https://example.com) [section](#usage)\n')
        self.assertEqual(text, self.render(text))

    def test_invalid_local_links_fail(self):
        for target in ('missing.md', '../../../outside.txt'):
            with self.subTest(target=target), self.assertRaises(ValueError):
                self.render(f'[x]({target})')

    def test_community_guard(self):
        wiki.validate_communities('SNMPv1v2cCommunityName private;', 'Example.md')
        wiki.validate_communities('snmpwalk -v2c -c public host', 'Example.md')
        for text in ('SNMPv1v2cCommunityName example-forbidden;',
                     'Snmp11 docsDevNmAccessCommunity.1 OctetString "example-forbidden";',
                     'snmpwalk -v2c -c example-forbidden host'):
            with self.subTest(text=text), self.assertRaises(ValueError) as caught:
                wiki.validate_communities(text, 'Example.md')
            self.assertNotIn('example-forbidden', str(caught.exception))

    def test_preview_publish_and_idempotence(self):
        remote = Path(self.temp.name) / 'wiki.git'
        seed = Path(self.temp.name) / 'seed'
        def run(*args):
            return subprocess.run(['git', *map(str, args)], check=True, capture_output=True,
                                  text=True).stdout.strip()
        run('init', '--bare', remote)
        run('init', seed)
        run('-C', seed, 'config', 'user.name', 'Wiki Test')
        run('-C', seed, 'config', 'user.email', 'wiki-test@example.invalid')
        (seed / 'Home.md').write_text('Old home')
        (seed / 'Legacy.md').write_text('Preserve this page')
        run('-C', seed, 'add', '.')
        run('-C', seed, 'commit', '-m', 'Initial wiki')
        run('-C', seed, 'branch', '-M', 'master')
        run('-C', seed, 'remote', 'add', 'origin', remote)
        run('-C', seed, 'push', '-u', 'origin', 'master')
        run('--git-dir', remote, 'symbolic-ref', 'HEAD', 'refs/heads/master')
        run('init', self.root)
        run('-C', self.root, 'config', 'user.name', 'Wiki Test')
        run('-C', self.root, 'config', 'user.email', 'wiki-test@example.invalid')
        original = run('--git-dir', remote, 'rev-parse', 'HEAD')
        def invoke(publish=False):
            argv = ['update-wiki', '--wiki-url', str(remote)]
            if publish:
                argv.append('--publish')
            with patch.object(wiki, 'ROOT', self.root), patch('sys.argv', argv), contextlib.redirect_stdout(io.StringIO()):
                wiki.main()
        invoke()
        self.assertEqual(original, run('--git-dir', remote, 'rev-parse', 'HEAD'))
        invoke(True)
        published = run('--git-dir', remote, 'rev-parse', 'HEAD')
        self.assertNotEqual(original, published)
        files = run('--git-dir', remote, 'ls-tree', '--name-only', 'HEAD').splitlines()
        self.assertIn('Legacy.md', files)
        self.assertIn('Help.md', files)
        self.assertNotIn('AGENTS.md', files)
        self.assertIn('/wiki/Help#usage', run('--git-dir', remote, 'show', 'HEAD:Home.md'))
        invoke(True)
        self.assertEqual(published, run('--git-dir', remote, 'rev-parse', 'HEAD'))


if __name__ == '__main__':
    unittest.main()
