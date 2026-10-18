import importlib.util
import json
import shutil
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import MagicMock, patch


SPEC = importlib.util.spec_from_file_location(
    'update_snmp_map', Path(__file__).with_name('update-snmp-map.py'))
updater = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(updater)


def mib(name, imported=None):
    imports = 'IMPORTS value FROM ' + imported + ';' if imported else ''
    return (name + ' DEFINITIONS ::= BEGIN\n' + imports + '\nEND').encode()


class UpdateSnmpMapTest(unittest.TestCase):
    def test_security_modules_are_generation_roots(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            (root / 'mibs/txt').mkdir(parents=True)
            modules = {name + '.mib': mib(name, 'SNMPv2-SMI')
                       for name in updater.SNMP_SECURITY_MODULES}
            modules['SNMPv2-SMI.mib'] = mib('SNMPv2-SMI')
            modules['VENDOR-MIB.mib'] = mib('VENDOR-MIB')
            with patch.object(updater, 'ROOT', root):
                roots, selected = updater.mib_set(modules)
        self.assertEqual(roots, updater.SNMP_SECURITY_MODULES)
        self.assertIn('SNMPv2-SMI', selected)
        self.assertNotIn('SNMPv2-SMI', roots)
        self.assertNotIn('VENDOR-MIB', selected)

    @unittest.skipUnless(shutil.which('snmptranslate'), 'Net-SNMP is required')
    def test_bundled_security_mibs_generate_names_and_resolve_instances(self):
        roots, modules = updater.mib_set({})
        expected = {
            'usmUserSecurityName': '.1.3.6.1.6.3.15.1.2.2.1.3',
            'usmUserAuthKeyChange': '.1.3.6.1.6.3.15.1.2.2.1.6',
            'usmUserPrivKeyChange': '.1.3.6.1.6.3.15.1.2.2.1.9',
            'vacmGroupName': '.1.3.6.1.6.3.16.1.2.1.3',
            'snmpCommunityName': '.1.3.6.1.6.3.18.1.1.1.2',
            'usmDHUserAuthKeyChange': '.1.3.6.1.3.101.1.1.2.1.1',
        }
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            for name, data in modules.items():
                (directory / (name + '.mib')).write_bytes(data)
            generated = updater.generate({}, directory, roots)
            for name, oid in expected.items():
                with self.subTest(name=name):
                    self.assertEqual(generated[name], oid)
            instance = '.5.128.0.0.0.1.8.108.97.98.45.117.115.101.114'
            name = 'usmUserAuthKeyChange' + instance
            oid = expected['usmUserAuthKeyChange'] + instance
            self.assertEqual(updater.translate(directory, roots, '-IR', '-On', name), oid)
            self.assertEqual(updater.translate(directory, roots, '-Obas', oid), name)

    def test_pypnm_dependencies_use_pinned_module_specific_filenames(self):
        self.assertIn('82bbf79fe04a0b5e766dc3eb4d5ca2004008bca9', updater.DEPENDENCY_BASE)
        self.assertEqual(updater.DEPENDENCY_SOURCES['SCTE-HMS-MPEG-MIB'],
                         updater.DEPENDENCY_BASE + 'scte-hms-mpeg-100308.txt')
        self.assertEqual(updater.DEPENDENCY_SOURCES['SCTE-HMS-HEADENDIDENT-TC-MIB'],
                         updater.DEPENDENCY_BASE + 'scte-hms-headendident-tc-071208-br.txt')
        self.assertNotIn('PyPNM', updater.DEPENDENCY_SOURCES['RFC1271-MIB'])

    def test_download_validates_and_includes_allowlisted_dependencies(self):
        responses = {updater.SOURCE: b'<a href="DOCS-TEST-MIB.mib">test</a>',
                     updater.SOURCE + 'DOCS-TEST-MIB.mib': mib('DOCS-TEST-MIB')}
        responses.update({url: mib(name) for name, url in updater.DEPENDENCY_SOURCES.items()})
        with patch.object(updater, 'fetch', side_effect=responses.__getitem__):
            downloaded = updater.download()
        self.assertEqual(len(downloaded), 1 + len(updater.DEPENDENCY_SOURCES))
        self.assertIn('SCTE-HMS-MPEG-MIB.mib', downloaded)

    def test_dependencies_are_not_mapping_roots(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            (root / 'mibs/txt').mkdir(parents=True)
            downloaded = {'DOCS-TEST-MIB.mib': mib('DOCS-TEST-MIB', 'SCTE-HMS-MPEG-MIB'),
                          'SCTE-HMS-MPEG-MIB.mib': mib('SCTE-HMS-MPEG-MIB', 'SCTE-HMS-HEADENDIDENT-TC-MIB'),
                          'SCTE-HMS-HEADENDIDENT-TC-MIB.mib': mib('SCTE-HMS-HEADENDIDENT-TC-MIB')}
            with patch.object(updater, 'ROOT', root):
                roots, modules = updater.mib_set(downloaded)
        self.assertEqual(roots, {'DOCS-TEST-MIB'})
        self.assertEqual(len(modules), 3)

    def test_missing_imports_fail_before_translation(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            (root / 'mibs/txt').mkdir(parents=True)
            with patch.object(updater, 'ROOT', root):
                with self.assertRaisesRegex(ValueError, 'Missing imported MIB modules: MISSING-MIB'):
                    updater.mib_set({'DOCS-TEST-MIB.mib': mib('DOCS-TEST-MIB', 'MISSING-MIB')})

    def test_rejects_unlisted_dependency_source(self):
        with patch.object(updater.urllib.request, 'urlopen') as opened:
            with self.assertRaisesRegex(ValueError, 'not allowlisted'):
                updater.fetch('https://example.com/OTHER-MIB')
        opened.assert_not_called()

    def test_rejects_dependency_redirect(self):
        response = MagicMock()
        response.__enter__.return_value.url = 'https://example.com/OTHER-MIB'
        with patch.object(updater.urllib.request, 'urlopen', return_value=response):
            with self.assertRaisesRegex(ValueError, 'redirected'):
                updater.fetch(updater.DEPENDENCY_SOURCES['SCTE-HMS-MPEG-MIB'])

    def test_preserves_complete_loader_diagnostics(self):
        diagnostic = 'Loader error: ' + 'missing dependency ' * 60
        result = subprocess.CompletedProcess([], 0, '.1', diagnostic)
        with patch.object(updater.subprocess, 'run', return_value=result):
            with self.assertRaises(ValueError) as raised:
                updater.translate(Path('/unused'), {'DOCS-TEST-MIB'}, '-On', 'iso')
        self.assertEqual(str(raised.exception), diagnostic.strip())

    def test_non_docsis_mapping_remains_unchanged(self):
        mapping = {'vendorObject.0': '.1.3.6.1.4.1.99999.1.0'}
        with patch.object(updater, 'translate', return_value='VENDOR-MIB::vendorObject.0'):
            self.assertEqual(updater.refresh(mapping, Path('/unused'), {'DOCS-TEST-MIB'}), mapping)

    def test_parses_dump_and_skips_anonymous_nodes(self):
        output = '"docsObject"\t"1.3.6.1.2"\n"anonymous#1" "1.3.6.1.3"\n'
        self.assertEqual(updater.parse_oid_dump(output), [('docsObject', '.1.3.6.1.2')])

    def test_rejects_malformed_or_empty_dump(self):
        for output in ('', 'unexpected output', '"bad" "1.not.an.oid"'):
            with self.subTest(output=output), self.assertRaises(ValueError):
                updater.parse_oid_dump(output)

    def test_generates_only_docsis_definitions_and_preserves_instances(self):
        mapping = {'docsObject.0': '.1.3.6.1.2.0', 'vendorObject.0': '.1.3.6.1.4.0'}
        responses = ['"docsObject" "1.3.6.1.2"\n"ifIndex" "1.3.6.1.3"',
                     'DOCS-TEST-MIB::docsObject\nIF-MIB::ifIndex', '.1.3.6.1.2']
        with patch.object(updater, 'translate', side_effect=responses):
            result = updater.generate(mapping, Path('/unused'), {'DOCS-TEST-MIB'})
        self.assertEqual(result, dict(mapping, docsObject='.1.3.6.1.2'))

    def test_preserves_existing_reverse_mapping_without_duplicate_oid(self):
        mapping = {'1.3.6.1.2': 'docsObject'}
        responses = ['"docsObject" "1.3.6.1.2"', 'DOCS-TEST-MIB::docsObject', '.1.3.6.1.2']
        with patch.object(updater, 'translate', side_effect=responses):
            self.assertEqual(updater.generate(mapping, Path('/unused'), {'DOCS-TEST-MIB'}), mapping)

    def test_rejects_generated_name_conflict(self):
        responses = ['"docsObject" "1.3.6.1.2"', 'DOCS-TEST-MIB::docsObject', '.1.3.6.1.2']
        with patch.object(updater, 'translate', side_effect=responses):
            with self.assertRaisesRegex(ValueError, 'conflicts with cached'):
                updater.generate({'docsObject': '.1.3.6.1.9'}, Path('/unused'), {'DOCS-TEST-MIB'})

    def test_qualifies_duplicate_names_in_different_modules(self):
        responses = ['"docsObject" "1.3.6.1.2"\n"docsObject" "1.3.6.1.3"',
                     'DOCS-LEGACY-MIB::docsObject\nDOCS-TEST-MIB::docsObject',
                     '.1.3.6.1.2\n\n.1.3.6.1.3']
        with patch.object(updater, 'translate', side_effect=responses):
            result = updater.generate({}, Path('/unused'), {'DOCS-TEST-MIB', 'DOCS-LEGACY-MIB'})
        self.assertEqual(result, {'DOCS-LEGACY-MIB::docsObject': '.1.3.6.1.2',
                                  'DOCS-TEST-MIB::docsObject': '.1.3.6.1.3'})

    def test_rejects_generated_round_trip_mismatch(self):
        responses = ['"docsObject" "1.3.6.1.2"', 'DOCS-TEST-MIB::docsObject', '.1.3.6.1.9']
        with patch.object(updater, 'translate', side_effect=responses):
            with self.assertRaisesRegex(ValueError, 'round-trip mismatch'):
                updater.generate({}, Path('/unused'), {'DOCS-TEST-MIB'})

    def test_lookup_batches_and_checks_output_count(self):
        values = [str(index) for index in range(129)]
        with patch.object(updater, 'translate', side_effect=['\n\n'.join(values[:128]), values[128]]):
            self.assertEqual(updater.lookup_many(Path('/unused'), set(), ('-On',), values), values)
        with patch.object(updater, 'translate', return_value='one result'):
            with self.assertRaisesRegex(ValueError, 'number of snmptranslate results'):
                updater.lookup_many(Path('/unused'), set(), ('-On',), ['first', 'second'])

    def test_generation_cli_preserves_preview_and_writes_only_when_requested(self):
        for write in (False, True):
            with self.subTest(write=write), tempfile.TemporaryDirectory() as temporary:
                root = Path(temporary)
                (root / 'res').mkdir()
                path = root / 'res/DottedTextualNetSNMPMap.json'
                original = b'{"docsObject.0": ".1.3.6.1.2.0"}\n'
                path.write_bytes(original)
                arguments = ['update-snmp-map.py', '--generate'] + (['--write'] if write else [])
                responses = ['.1', '"docsObject" "1.3.6.1.2"',
                             'DOCS-TEST-MIB::docsObject', '.1.3.6.1.2']
                with patch.object(updater, 'ROOT', root), \
                     patch.object(sys, 'argv', arguments), \
                     patch.object(updater.shutil, 'which', return_value='/mock/snmptranslate'), \
                     patch.object(updater, 'mib_set', return_value=(
                         {'DOCS-TEST-MIB'}, {'DOCS-TEST-MIB': mib('DOCS-TEST-MIB')})), \
                     patch.object(updater, 'refresh', side_effect=lambda mapping, *_: mapping), \
                     patch.object(updater, 'translate', side_effect=responses):
                    updater.main()
                if write:
                    self.assertEqual(json.loads(path.read_text()), {
                        'docsObject.0': '.1.3.6.1.2.0', 'docsObject': '.1.3.6.1.2'})
                else:
                    self.assertEqual(path.read_bytes(), original)
                self.assertFalse((root / 'mibs/docsis').exists())


if __name__ == '__main__':
    unittest.main()