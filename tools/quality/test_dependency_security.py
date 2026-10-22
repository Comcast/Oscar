# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

spec = importlib.util.spec_from_file_location('security', Path(__file__).with_name('dependency-security.py'))
security = importlib.util.module_from_spec(spec)
spec.loader.exec_module(security)
COORDINATE = ('example', 'library', '1.0')


class DependencySecurityTest(unittest.TestCase):
    def setUp(self):
        quiet = patch('builtins.print')
        quiet.start()
        self.addCleanup(quiet.stop)

    def test_nested_scopes_exclude_project(self):
        leaf = dict(groupId='example', artifactId='library', version='1.0', scope='test')
        tree = {'children': [dict(leaf, children=[dict(leaf, version='2.0')])]}
        self.assertEqual(security.application_coordinates(tree), {COORDINATE, ('example', 'library', '2.0')})

    def test_plugins_include_transitives_and_reject_unknown_rows(self):
        text = 'The following plugins have been resolved:\n   a:plugin:jar:1\n      b:dep:jar:2\n'
        self.assertEqual(security.plugin_coordinates(text), {('a', 'plugin', '1'), ('b', 'dep', '2')})
        for invalid in ('', text + 'unknown format'):
            with self.assertRaises(ValueError):
                security.plugin_coordinates(invalid)

    def test_reenabled_site_is_not_excluded(self):
        xml = '<project xmlns="http://maven.apache.org/POM/4.0.0"><build><plugins>{}</plugins></build></project>'
        self.assertTrue(security.site_is_disabled(xml.format('')))
        plugin = '<plugin><artifactId>maven-site-plugin</artifactId></plugin>'
        self.assertFalse(security.site_is_disabled(xml.format(plugin)))

    def test_clean_response(self):
        self.assertEqual(security.query_advisories({COORDINATE}, lambda _: {'results': [{}]}), {COORDINATE: set()})

    def test_consumes_pages_without_losing_findings(self):
        requests = []
        def request(payload):
            requests.append(payload)
            if len(requests) == 1:
                return {'results': [{'vulns': [{'id': 'FIRST'}], 'next_page_token': 'next'}]}
            return {'results': [{'vulns': [{'id': 'SECOND'}]}]}
        self.assertEqual(security.query_advisories({COORDINATE}, request)[COORDINATE], {'FIRST', 'SECOND'})
        self.assertEqual(requests[1]['queries'][0]['page_token'], 'next')

    def test_partial_and_error_responses_fail(self):
        for response in (None, {}, {'results': []}, {'results': [{'error': 'failed'}]}, {'results': [None]}):
            with self.assertRaises(ValueError):
                security.query_advisories({COORDINATE}, lambda _, value=response: value)
        with self.assertRaises(ValueError):
            security.query_advisories(set())

    def test_repeated_page_token_fails(self):
        with self.assertRaises(ValueError):
            security.query_advisories({COORDINATE}, lambda _: {'results': [{'next_page_token': 'same'}]})

    def test_batches_cover_inventory(self):
        coordinates = {('example', f'library-{index}', '1.0') for index in range(101)}
        sizes = []
        def request(payload):
            sizes.append(len(payload['queries']))
            return {'results': [{} for _ in payload['queries']]}
        self.assertEqual(set(security.query_advisories(coordinates, request)), coordinates)
        self.assertEqual(sizes, [100, 1])

    def test_exit_status_and_retained_report(self):
        for advisories, expected in ((set(), 0), ({'GHSA-test'}, 1)):
            with self.subTest(expected=expected), tempfile.TemporaryDirectory() as directory:
                output = Path(directory)
                with patch.object(security, 'OUTPUT', output), patch.object(security, 'collect_inventory', return_value=({COORDINATE}, set(), True)), patch.object(security, 'query_advisories', return_value={COORDINATE: advisories}):
                    self.assertEqual(security.main(), expected)
                report = json.loads((output / 'report.json').read_text(encoding='utf-8'))
                self.assertEqual(report['status'], 'findings' if expected else 'passed')

    def test_network_failure_overwrites_old_pass(self):
        with tempfile.TemporaryDirectory() as directory:
            output = Path(directory)
            (output / 'report.json').write_text('{"status":"passed"}', encoding='utf-8')
            with patch.object(security, 'OUTPUT', output), patch.object(security, 'collect_inventory', return_value=({COORDINATE}, set(), True)), patch.object(security, 'query_advisories', side_effect=OSError('offline')):
                self.assertEqual(security.main(), 2)
            self.assertEqual(json.loads((output / 'report.json').read_text(encoding='utf-8'))['status'], 'error')
