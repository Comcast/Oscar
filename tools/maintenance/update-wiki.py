#!/usr/bin/env python3
"""Preview or explicitly publish docs/wiki to the separate GitHub wiki repository."""
import argparse
from pathlib import Path
import re
import subprocess
import tempfile
from urllib.parse import quote, unquote, urlsplit

ROOT = Path(__file__).resolve().parents[2]
WIKI_REMOTE = 'https://github.com/Comcast/Oscar.wiki.git'
REPOSITORY_URL = 'https://github.com/Comcast/Oscar'
# Only ordinary inline Markdown links outside fenced/inline code are rewritten.
LINK = re.compile(r'(\]\()([^\s)]+)(\))')
CODE = re.compile(r'(`+)[^`]*?\1')


def git(directory, *args, capture=True):
    return subprocess.run(['git', '-C', str(directory), *args], check=True,
                          text=True, stdout=subprocess.PIPE if capture else None).stdout


def render(text, page, source, repository, repository_url, ref):
    def replace(match):
        target = match[2]
        parsed = urlsplit(target)
        if parsed.scheme or parsed.netloc or target.startswith(('#', '/')):
            return match[0]
        path = (page.parent / unquote(parsed.path)).resolve()
        if not path.is_relative_to(repository.resolve()) or not path.exists():
            raise ValueError(f'{page.name}: missing or external local link {target}')
        if path.parent == source.resolve() and path.suffix == '.md' and path.name != 'AGENTS.md':
            url = repository_url + '/wiki/' + quote(path.stem, safe='-')
        else:
            relative = path.relative_to(repository.resolve()).as_posix()
            kind = 'tree' if path.is_dir() else 'blob'
            url = f'{repository_url}/{kind}/{quote(ref, safe="")}/{quote(relative, safe="/")}'
        if parsed.query:
            url += '?' + parsed.query
        if parsed.fragment:
            url += '#' + parsed.fragment
        return match[1] + url + match[3]

    output = []
    fence = None
    for line in text.splitlines(keepends=True):
        marker = re.match(r'^\s{0,3}(`{3,}|~{3,})', line)
        if marker:
            if fence is None:
                fence = marker[1]
            elif marker[1][0] == fence[0] and len(marker[1]) >= len(fence):
                fence = None
            output.append(line)
        elif fence or line.startswith(('    ', '\t')):
            output.append(line)
        else:
            position = 0
            rewritten = ''
            for code in CODE.finditer(line):
                rewritten += LINK.sub(replace, line[position:code.start()]) + code[0]
                position = code.end()
            output.append(rewritten + LINK.sub(replace, line[position:]))
    return ''.join(output)


def validate_communities(text, filename):
    """Reject literal non-example community values without echoing them."""
    patterns = (
        r'\bSNMPv1v2cCommunityName\s+([^;\s]+)\s*;',
        r'\bdocsDevNmAccessCommunity\.\d+\s+OctetString\s+"([^"\n]*)"',
        r'\bsnmp(?:get|walk|set|bulkwalk|bulkget|trap)\b[^\n]*?\s-c\s+("[^"\n]*"|\S+)',
    )
    for pattern in patterns:
        for match in re.finditer(pattern, text):
            if match[1].strip('"\'') not in {'public', 'private'}:
                line = text.count('\n', 0, match.start()) + 1
                raise ValueError(f'{filename}:{line}: community must be public or private')


def sync(source, checkout, repository, repository_url, ref):
    pages = sorted(p for p in source.glob('*.md') if p.name != 'AGENTS.md')
    if not pages or not (source / 'Home.md').is_file():
        raise ValueError('Wiki source must contain Home.md')
    # Prepare everything before modifying the checkout, so broken links fail early.
    rendered = {p.name: render(p.read_text(), p, source, repository, repository_url, ref)
                for p in pages}
    for page in checkout.glob('*.md'):
        if page.name not in rendered:
            validate_communities(page.read_text(), page.name)
    for name, content in rendered.items():
        validate_communities(content, name)
        target = checkout / name
        if target.is_symlink():
            raise ValueError('Refusing to overwrite wiki symlink: ' + name)
        target.write_text(content)
    # Preserve pages and assets that are maintained directly in the wiki.
    git(checkout, 'add', '--', *rendered)
    return len(rendered)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--publish', action='store_true', help='Commit and push the displayed changes')
    parser.add_argument('--wiki-url', default=WIKI_REMOTE, help='Wiki Git remote (default: Comcast/Oscar.wiki.git)')
    parser.add_argument('--repository-url', default=REPOSITORY_URL, help='Web URL used for published links')
    parser.add_argument('--ref', default='master', help='Main-repository branch or tag for file links (default: master)')
    parser.add_argument('--message', default='Documentation: Refresh Oscar wiki from repository', help='Wiki commit message')
    args = parser.parse_args()
    if not args.message.strip() or not args.ref.strip():
        parser.error('Commit message and repository ref must not be empty')
    url = args.repository_url.rstrip('/')
    if not url.startswith('https://'):
        parser.error('--repository-url must be an HTTPS web URL')
    with tempfile.TemporaryDirectory(prefix='oscar-wiki-') as temporary:
        checkout = Path(temporary) / 'wiki'
        subprocess.run(['git', 'clone', '--quiet', '--', args.wiki_url, str(checkout)], check=True)
        branch = git(checkout, 'symbolic-ref', '--short', 'HEAD').strip()
        count = sync(ROOT / 'docs/wiki', checkout, ROOT, url, args.ref)
        print(f'Prepared {count} pages for {args.wiki_url} ({branch}).', flush=True)
        if not git(checkout, 'diff', '--cached', '--name-only').strip():
            print('Wiki already matches. Nothing to publish.')
            return
        git(checkout, '--no-pager', 'diff', '--cached', '--stat', capture=False)
        git(checkout, '--no-pager', 'diff', '--cached', '--no-ext-diff', capture=False)
        if not args.publish:
            print('Preview only. Run with --publish to commit and push. Other wiki pages are preserved.')
            return
        # Honor identity configured in Oscar without changing global configuration.
        for setting in ('user.name', 'user.email'):
            value = subprocess.run(['git', '-C', str(ROOT), 'config', '--get', setting],
                                   capture_output=True, text=True)
            if value.returncode == 0:
                git(checkout, 'config', setting, value.stdout.strip())
        git(checkout, 'commit', '-m', args.message, capture=False)
        # A regular push rejects concurrent edits; never force overwrite them.
        git(checkout, 'push', 'origin', f'HEAD:refs/heads/{branch}', capture=False)
        print('Published wiki update.')


if __name__ == '__main__':
    try:
        main()
    except (OSError, ValueError, subprocess.CalledProcessError) as error:
        raise SystemExit('ERROR: ' + str(error))
