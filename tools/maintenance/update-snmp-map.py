#!/usr/bin/env python3
"""Preview or refresh cached DOCSIS OID names using an isolated MIB set."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import tempfile
import urllib.request

ROOT = Path(__file__).resolve().parents[2]
SOURCE = "https://mibs.cablelabs.com/MIBs/DOCSIS/"
DEPENDENCY_BASE = (
    'https://raw.githubusercontent.com/PyPNMApps/PyPNM/'
    '82bbf79fe04a0b5e766dc3eb4d5ca2004008bca9/mibs/'
)
DEPENDENCY_SOURCES = {
    name: DEPENDENCY_BASE + filename
    for name, filename in {
        'ENTITY-SENSOR-MIB': 'ENTITY-SENSOR-MIB-rfc3433.txt',
        'IANA-ENTITY-MIB': 'IANA-ENTITY-MIB.mib',
        'URI-TC-MIB': 'URI-TC-MIB-rfc5017.txt',
        'UUID-TC-MIB': 'UUID-TC-MIB.txt',
        'SCTE-HMS-MPEG-MIB': 'scte-hms-mpeg-100308.txt',
        'SCTE-HMS-HEADENDIDENT-TC-MIB': 'scte-hms-headendident-tc-071208-br.txt',
    }.items()
}
DEPENDENCY_SOURCES['RFC1271-MIB'] = (
    'https://raw.githubusercontent.com/librenms/librenms/'
    'c42d6d3d82e5c4ef2a2452bf6655c61af57cba41/mibs/RFC1271-MIB'
)
DEPENDENCY_SOURCES['CLAB-DEF-MIB'] = 'https://mibs.cablelabs.com/MIBs/common/CLAB-DEF-MIB.mib'
MODULE = re.compile(r"([\w-]+)\s+DEFINITIONS\s*::=\s*BEGIN")
OID = re.compile(r"\.?[0-9]+(?:\.[0-9]+)+")


def module_info(data):
    text = data.decode('utf-8', errors='replace')
    text = re.sub(r'--[^\n]*', '', text)
    match = MODULE.search(text)
    if not match:
        raise ValueError('Not a MIB module')
    imports = re.search(r'\bIMPORTS\b(.*?);', text, re.S)
    return match[1], set(re.findall(r'\bFROM\s+([\w-]+)', imports[1])) if imports else set()


def atomic_write(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.NamedTemporaryFile(dir=path.parent, delete=False) as stream:
        temporary = Path(stream.name)
        stream.write(data)
    try:
        temporary.chmod(0o644)
        temporary.replace(path)
    finally:
        temporary.unlink(missing_ok=True)


def fetch(url):
    if not url.startswith(SOURCE) and url not in DEPENDENCY_SOURCES.values():
        raise ValueError('Download source is not allowlisted: ' + url)
    with urllib.request.urlopen(url, timeout=30) as response:
        if url.startswith(SOURCE):
            allowed = response.url.startswith(SOURCE)
        else:
            allowed = response.url == url
        if not allowed:
            raise ValueError('Download redirected outside its allowed source')
        return response.read()


def download():
    index = fetch(SOURCE).decode('utf-8')
    names = sorted(set(re.findall(r'href="((?:DOCS-|CCAP-|DSG-)[A-Za-z0-9-]+\.mib)"', index)))
    if not names:
        raise ValueError('No current DOCSIS MIB links found')
    result = {}
    for name in names:
        data = fetch(SOURCE + name)
        module, _ = module_info(data)
        if module + '.mib' != name:
            raise ValueError('Module name does not match download: ' + name)
        result[name] = data
    for name, url in DEPENDENCY_SOURCES.items():
        data = fetch(url)
        module, _ = module_info(data)
        if module != name:
            raise ValueError('Dependency module name does not match download: ' + name)
        result[name + '.mib'] = data
    return result


def mib_set(downloads):
    # Prefer canonical filenames, then newer module revisions in legacy bundles.
    modules = {}
    ranks = {}
    for path in sorted((ROOT / 'mibs/txt').iterdir()):
        if not path.is_file():
            continue
        data = path.read_bytes()
        try:
            name, imports = module_info(data)
        except ValueError:
            continue
        dates = re.findall(rb'(?:LAST-UPDATED|REVISION)\s+"([0-9]+Z)"', data)
        rank = (max(dates, default=b''), path.stem == name)
        if name not in ranks or rank > ranks[name]:
            modules[name] = (data, imports)
            ranks[name] = rank
    overlay = ROOT / 'mibs/docsis'
    saved = {p.name: p.read_bytes() for p in overlay.glob('*.mib')}
    saved.update(downloads)
    for data in saved.values():
        name, imports = module_info(data)
        modules[name] = (data, imports)
    roots = {name for name in modules if name.startswith(('DOCS-', 'CCAP-', 'DSG-'))}
    selected = set()
    missing = set()
    pending = list(roots)
    while pending:
        name = pending.pop()
        if name in selected:
            continue
        if name not in modules:
            missing.add(name)
            continue
        selected.add(name)
        # Net-SNMP implements these SMIv1 macros internally.
        pending.extend(modules[name][1] - selected - {'RFC-1212', 'RFC-1215'})
    if missing:
        raise ValueError('Missing imported MIB modules: ' + ', '.join(sorted(missing))
                         + '. Run with --update-mibs to fetch allowlisted dependencies.')
    return roots, {name: modules[name][0] for name in selected}


def translate(directory, modules, *options):
    env = dict(os.environ, MIBS='', MIBDIRS=str(directory), SNMPCONFPATH=str(directory))
    result = subprocess.run(['snmptranslate', '-M', str(directory), '-m', ':'.join(sorted(modules)),
                             *options], capture_output=True, text=True, timeout=20, env=env, check=False)
    if result.returncode or result.stderr.strip() or not result.stdout.strip():
        raise ValueError(result.stderr.strip() or 'Translation failed')
    return result.stdout.strip()


def refresh(mapping, directory, roots):
    result = dict(mapping)
    changed = 0
    unresolved = 0
    for key, value in sorted(mapping.items()):
        numeric_key = bool(OID.fullmatch(key))
        oid, old = (key, value) if numeric_key else (value, key)
        if not OID.fullmatch(oid):
            raise ValueError('Invalid cached OID: ' + oid)
        try:
            qualified = translate(directory, roots, '-OS', oid)
            module, separator, _ = qualified.partition('::')
            if not separator or module not in roots:
                continue  # Keep PacketCable, vendor, and generic mappings unchanged.
            new = translate(directory, roots, '-Oas', oid)
            back = translate(directory, roots, '-IR', '-On', new)
            if back.lstrip('.') != oid.lstrip('.'):
                raise ValueError('Round-trip mismatch for ' + new)
        except ValueError as error:
            unresolved += 1
            print(f'UNRESOLVED {oid}: {error}')
            continue
        if new == old:
            continue
        new_key, new_value = (key, new) if numeric_key else (new, value)
        if new_key != key and new_key in result:
            raise ValueError('Conflicting mapping key: ' + new_key)
        if any(k != key and v == new_value for k, v in result.items()):
            raise ValueError('Conflicting mapping value: ' + new_value)
        del result[key]
        result[new_key] = new_value
        changed += 1
        print(f'{oid}: {old} -> {new}')
    print(f'{len(mapping)} mappings; {changed} changed; {unresolved} unresolved (preserved)')
    return result


def parse_oid_dump(output):
    entries = []
    for line in output.splitlines():
        if not line.strip():
            continue
        match = re.fullmatch(r'\s*"([^"]+)"\s+"(\.?[0-9]+(?:\.[0-9]+)+)"\s*', line)
        if not match:
            raise ValueError('Invalid snmptranslate -Tz output: ' + line)
        name, oid = match.groups()
        if not name.startswith('anonymous#'):
            entries.append((name, '.' + oid.lstrip('.')))
    if not entries:
        raise ValueError('No named OIDs in snmptranslate -Tz output')
    return entries


def lookup_many(directory, roots, options, values):
    results = []
    for start in range(0, len(values), 128):
        batch = values[start:start + 128]
        translated = [line.strip() for line in
                  translate(directory, roots, *options, *batch).splitlines() if line.strip()]
        if len(translated) != len(batch):
            raise ValueError('Unexpected number of snmptranslate results')
        results.extend(translated)
    return results


def generate(mapping, directory, roots):
    entries = parse_oid_dump(translate(directory, roots, '-Tz'))
    oids = sorted({oid for _, oid in entries})
    owners = lookup_many(directory, roots, ('-OS',), oids)
    candidates = {}
    qualified_names = {}
    ambiguous = set()
    for oid, qualified in zip(oids, owners):
        module, separator, name = qualified.partition('::')
        if not separator or module not in roots:
            continue
        if not re.fullmatch(r'[A-Za-z][A-Za-z0-9-]*', name):
            raise ValueError('Invalid generated OID name: ' + qualified)
        if name in ambiguous:
            candidates[qualified] = oid
        elif name in candidates and candidates[name] != oid:
            candidates[qualified_names[name]] = candidates.pop(name)
            candidates[qualified] = oid
            ambiguous.add(name)
        else:
            candidates[name] = oid
            qualified_names[name] = qualified
    if not candidates:
        raise ValueError('No DOCSIS OIDs found in loaded MIB tree')
    names = sorted(candidates)
    numeric = lookup_many(directory, roots, ('-IR', '-On'), names)
    for name, oid in zip(names, numeric):
        if '.' + oid.lstrip('.') != candidates[name]:
            raise ValueError('Generated OID round-trip mismatch for ' + name)
    result = dict(mapping)
    existing_oids = set()
    existing_names = {}
    for key, value in mapping.items():
        oid, name = (key, value) if OID.fullmatch(key) else (value, key)
        if not OID.fullmatch(oid):
            raise ValueError('Invalid cached OID: ' + oid)
        normalized = '.' + oid.lstrip('.')
        existing_oids.add(normalized)
        if name in existing_names and existing_names[name] != normalized:
            raise ValueError('Ambiguous cached OID name: ' + name)
        existing_names[name] = normalized
    added = 0
    for name, oid in sorted(candidates.items()):
        if name in existing_names and existing_names[name] != oid:
            raise ValueError('Generated name conflicts with cached mapping: ' + name)
        if oid in existing_oids:
            continue
        if name in result or oid in result.values():
            raise ValueError('Generated mapping conflicts with cache: ' + name)
        result[name] = oid
        existing_oids.add(oid)
        added += 1
    if len(set(result.values())) != len(result):
        raise ValueError('Generated mapping contains duplicate values')
    print(f'{len(candidates)} DOCSIS definitions; {added} added; {len(result)} total mappings')
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--write', action='store_true', help='Apply validated changes (default: preview)')
    parser.add_argument('--update-mibs', action='store_true', help='Download current CableLabs DOCSIS MIBs before checking')
    parser.add_argument('--generate', action='store_true', help='Add all named DOCSIS definitions using snmptranslate -Tz')
    args = parser.parse_args()
    if not shutil.which('snmptranslate'):
        raise ValueError('Install Net-SNMP snmptranslate first')
    path = ROOT / 'db/DottedTextualNetSNMPMap.json'
    mapping = json.loads(path.read_text())
    if not isinstance(mapping, dict) or not all(isinstance(k, str) and isinstance(v, str) for k, v in mapping.items()):
        raise ValueError('Mapping must be a JSON object of string pairs')
    if len(set(mapping.values())) != len(mapping):
        raise ValueError('Duplicate mapping values are incompatible with Oscar')
    downloads = download() if args.update_mibs else {}
    roots, modules = mib_set(downloads)
    print(f'{len(roots)} DOCSIS modules; {len(modules) - len(roots)} imported dependencies')
    with tempfile.TemporaryDirectory(prefix='oscar-docsis-mibs-') as temporary:
        directory = Path(temporary)
        for name, data in modules.items():
            (directory / (name + '.mib')).write_bytes(data)
        # Refuse to use a partially loaded MIB tree, even when Net-SNMP exits zero.
        translate(directory, roots, '-On', 'iso')
        updated = refresh(mapping, directory, roots)
        if args.generate:
            updated = generate(updated, directory, roots)
    if args.write:
        for name, data in downloads.items():
            atomic_write(ROOT / 'mibs/docsis' / name, data)
        if downloads:
            manifest = {name: {'url': DEPENDENCY_SOURCES.get(name[:-4], SOURCE + name),
                               'sha256': hashlib.sha256(data).hexdigest(),
                               'role': 'dependency' if name[:-4] in DEPENDENCY_SOURCES else 'docsis'}
                        for name, data in sorted(downloads.items())}
            atomic_write(ROOT / 'mibs/docsis/source-manifest.json', (json.dumps(manifest, indent=2) + '\n').encode())
        if updated != mapping:
            atomic_write(path, (json.dumps(updated, indent=2, sort_keys=True) + '\n').encode())
        print('Applied validated changes.')
    else:
        print(f'Preview only; {len(downloads)} MIB downloads staged temporarily. Use --write to apply.')


if __name__ == '__main__':
    try:
        main()
    except (ValueError, OSError, subprocess.TimeoutExpired) as error:
        raise SystemExit('ERROR: ' + str(error)) from error
