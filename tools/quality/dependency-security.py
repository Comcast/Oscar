#!/usr/bin/env python3
# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
"""Audit resolved Maven dependencies with OSV; exit 1 for matches, 2 for errors."""

import json
import os
from pathlib import Path
import re
import subprocess
import urllib.request
import xml.etree.ElementTree as ET
from datetime import datetime, timezone

ROOT = Path(__file__).resolve().parents[2]
OUTPUT = ROOT / 'target/security-audit'
OSV_URL = 'https://api.osv.dev/v1/querybatch'


def application_coordinates(tree):
    """Collect every dependency scope, excluding the project's own root node."""
    result = set()
    for child in tree.get('children', []):
        result.add(tuple(child[key] for key in ('groupId', 'artifactId', 'version')))
        result.update(application_coordinates(child))
    if not result and 'children' in tree:
        raise ValueError('Empty application dependency inventory')
    return result


def plugin_coordinates(text):
    """Parse plugin and transitive coordinates; reject unexpected output rows."""
    result = set()
    for line in text.splitlines():
        line = line.strip()
        if not line or line == 'The following plugins have been resolved:':
            continue
        match = re.fullmatch(r'([^:\s]+):([^:\s]+):(?:jar|maven-plugin):([^:\s]+)', line)
        if not match:
            raise ValueError(f'Unrecognized plugin coordinate: {line}')
        result.add(match.groups())
    if not result:
        raise ValueError('Empty plugin dependency inventory')
    return result


def site_is_disabled(effective_pom):
    """Exclude Site only when absent from the effective build's active plugins."""
    return not any(p.findtext('{*}artifactId') == 'maven-site-plugin'
                   for p in ET.fromstring(effective_pom).findall('{*}build/{*}plugins/{*}plugin'))


def post_osv(payload):
    """Send public package coordinates over HTTPS; fail on network/API errors."""
    request = urllib.request.Request(OSV_URL, json.dumps(payload).encode('utf-8'),
                                     {'Content-Type': 'application/json'})
    with urllib.request.urlopen(request, timeout=60) as response:
        return json.load(response)


def query_advisories(coordinates, request=post_osv):
    """Return IDs for each coordinate, consuming every page and rejecting partial results."""
    coordinates = sorted(coordinates)
    if not coordinates:
        raise ValueError('Cannot scan an empty inventory')
    findings = {coordinate: set() for coordinate in coordinates}
    pending = [(coordinate, {'package': {'ecosystem': 'Maven', 'name': ':'.join(coordinate[:2])},
                             'version': coordinate[2]}) for coordinate in coordinates]
    seen_tokens = set()
    while pending:
        batch, pending = pending[:100], pending[100:]
        response = request({'queries': [query for _, query in batch]})
        if not isinstance(response, dict):
            raise ValueError('Invalid OSV response')
        results = response.get('results')
        if not isinstance(results, list) or len(results) != len(batch):
            raise ValueError('Incomplete OSV response')
        for (coordinate, query), result in zip(batch, results):
            if not isinstance(result, dict) or 'error' in result:
                raise ValueError('Invalid OSV result')
            for vulnerability in result.get('vulns', []):
                findings[coordinate].add(vulnerability['id'])
            token = result.get('next_page_token')
            if token:
                key = (coordinate, token)
                if key in seen_tokens:
                    raise ValueError('Repeated OSV pagination token')
                seen_tokens.add(key)
                pending.append((coordinate, dict(query, page_token=token)))
    return findings


def collect_inventory():
    """Resolve application/test and plugin dependencies using the project's pinned wrapper."""
    wrapper = ROOT / ('mvnw.cmd' if os.name == 'nt' else 'mvnw')

    def maven(*arguments):
        subprocess.run([str(wrapper), '-B', '-ntp', *arguments], cwd=ROOT, check=True)

    tree = OUTPUT / 'dependencies.json'
    plugins = OUTPUT / 'plugins.txt'
    effective = OUTPUT / 'effective-pom.xml'
    # Never reuse an old successful inventory if a resolver invocation fails.
    for path in (tree, plugins, effective):
        path.unlink(missing_ok=True)
    maven('dependency:tree', '-DoutputType=json', f'-DoutputFile={tree}')
    maven('help:effective-pom', f'-Doutput={effective}')
    exclude_site = site_is_disabled(effective.read_text(encoding='utf-8'))
    arguments = ['dependency:resolve-plugins', f'-DoutputFile={plugins}', '-DoutputEncoding=UTF-8']
    if exclude_site:
        arguments.append('-DexcludeArtifactIds=maven-site-plugin')
    maven(*arguments)
    application = application_coordinates(json.loads(tree.read_text(encoding='utf-8')))
    build = plugin_coordinates(plugins.read_text(encoding='utf-8'))
    return application, build, exclude_site


def main():
    """Write a timestamped report and summary even when dependency resolution or OSV fails."""
    OUTPUT.mkdir(parents=True, exist_ok=True)
    report = {'checked_at': datetime.now(timezone.utc).isoformat(), 'status': 'error'}
    summary = 'Dependency security scan did not complete.'
    exit_code = 2
    try:
        application, build, exclude_site = collect_inventory()
        findings = query_advisories(application | build)
        entries = [{'coordinate': ':'.join(coordinate), 'advisories': sorted(ids),
                    'scopes': [name for name, values in [('application/test', application),
                                                        ('build-plugin', build)] if coordinate in values]}
                   for coordinate, ids in sorted(findings.items())]
        affected = sum(bool(entry['advisories']) for entry in entries)
        report.update(status='findings' if affected else 'passed', dependencies=entries,
                      excluded_site=exclude_site)
        summary = f'OSV: {len(entries)} Maven coordinates checked; {affected} with advisory matches.'
        for entry in entries:
            if entry['advisories']:
                summary += '\n- ' + entry['coordinate'] + ': ' + ', '.join(entry['advisories'])
        summary += '\nScope: application/test and resolved build plugins; not a guarantee of security.'
        if exclude_site:
            summary += '\nUnused Maven Site is excluded; its dependencies are not claimed fixed.'
        exit_code = 1 if affected else 0
    except (OSError, ValueError, KeyError, TypeError, ET.ParseError, subprocess.CalledProcessError) as error:
        report['error'] = str(error)
        summary += '\n' + str(error)
    (OUTPUT / 'report.json').write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')
    (OUTPUT / 'summary.md').write_text(summary + '\n', encoding='utf-8')
    print(summary)
    return exit_code


if __name__ == '__main__':
    raise SystemExit(main())
