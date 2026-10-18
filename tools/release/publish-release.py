#!/usr/bin/env python3
"""Publish an existing GitHub tag without building or changing Git refs."""
import argparse
import json
from pathlib import Path
import re
import subprocess
import sys

REPO_ROOT = Path(__file__).resolve().parents[2]


def gh(*arguments):
    """Run GitHub CLI from this checkout, preserving authentication failures."""
    return subprocess.run(
        ["gh", *arguments], cwd=REPO_ROOT, text=True,
        stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True,
    ).stdout.strip()


def publish(tag, dry_run=False):
    """Require a remote tag, then create at most one published release."""
    if not re.fullmatch(r"v\d+\.\d+\.\d+", tag):
        raise ValueError("Tag must use vMAJOR.MINOR.PATCH")
    repository = gh("repo", "view", "--json", "nameWithOwner", "--jq", ".nameWithOwner")
    # Explicitly check the remote tag instead of using newer gh --verify-tag.
    gh("api", f"repos/{repository}/git/ref/tags/{tag}")
    try:
        release = json.loads(gh("api", f"repos/{repository}/releases/tags/{tag}"))
    except subprocess.CalledProcessError as failure:
        if "(HTTP 404)" not in (failure.stderr or ""):
            raise
    else:
        if release.get("draft"):
            raise ValueError("A draft release already exists; review and publish it on GitHub.")
        print(f"Release already published: {release['html_url']}")
        return
    if dry_run:
        print(f"Would publish {repository} {tag} with generated release notes.")
        return
    print(gh("release", "create", tag, "--repo", repository,
             "--title", f"OSCAR {tag}", "--generate-notes"))
    print("Release published. GitHub Actions builds and uploads the JAR assets.")


def main():
    """Parse the requested existing tag and report actionable failures."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--tag", required=True, help="Existing remote tag, such as v3.2.0")
    parser.add_argument("--dry-run", action="store_true", help="Check GitHub without publishing")
    args = parser.parse_args()
    try:
        publish(args.tag, args.dry_run)
    except FileNotFoundError:
        print("GitHub CLI missing. Run ./install.sh --development, then gh auth login.", file=sys.stderr)
        return 1
    except subprocess.CalledProcessError as failure:
        print(failure.stderr or str(failure), file=sys.stderr)
        print("Check gh auth status and ensure the tag is pushed to GitHub.", file=sys.stderr)
        return 1
    except (ValueError, KeyError) as failure:
        print(str(failure), file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
