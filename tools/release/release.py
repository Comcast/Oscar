#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import re
import shutil
import subprocess
import sys
from datetime import datetime, timezone
from pathlib import Path
import xml.etree.ElementTree as ET

VERSION_PATTERN = re.compile(r"^(?P<major>\d+)\.(?P<minor>\d+)\.(?P<maintenance>\d+)$")
BUMP_CHOICES = ("major", "minor", "maintenance")
ALLOWED_RELEASE_BRANCHES = ("main", "master")
CONSTANTS_FILE_PATH = Path("src/com/comcast/oscar/constants/Constants.java")


def run(cmd: list[str], dry_run: bool = False, capture: bool = False) -> str:
    print("+", " ".join(cmd))
    if dry_run:
        return ""
    if capture:
        proc = subprocess.run(cmd, check=True, text=True, capture_output=True)
        return proc.stdout
    subprocess.run(cmd, check=True)
    return ""


def read_version(pom_path: Path) -> str:
    root = ET.parse(pom_path).getroot()
    ns = "{http://maven.apache.org/POM/4.0.0}"
    version = root.findtext(f"{ns}version")
    if version:
      return version.strip()

    parent = root.find(f"{ns}parent")
    if parent is not None:
      parent_version = parent.findtext(f"{ns}version")
      if parent_version:
        return parent_version.strip()

    raise RuntimeError("Unable to determine Maven project version from pom.xml")


def bump_version(version: str, bump: str) -> str:
    match = VERSION_PATTERN.match(version)
    if not match:
        raise RuntimeError(f"Unsupported version format '{version}'. Expected MAJOR.MINOR.MAINTENANCE")

    major = int(match.group("major"))
    minor = int(match.group("minor"))
    maintenance = int(match.group("maintenance"))

    if bump == "major":
        return f"{major + 1}.0.0"
    if bump == "minor":
        return f"{major}.{minor + 1}.0"
    return f"{major}.{minor}.{maintenance + 1}"


def write_version(pom_path: Path, old_version: str, new_version: str, dry_run: bool = False) -> None:
    if old_version == new_version:
        return

    pom_text = pom_path.read_text(encoding="utf-8")
    pattern = re.compile(
        r"(<artifactId>\s*oscar\s*</artifactId>\s*<version>)"
        + re.escape(old_version)
        + r"(</version>)",
        flags=re.MULTILINE,
    )
    updated, replacements = pattern.subn(rf"\g<1>{new_version}\g<2>", pom_text, count=1)
    if replacements != 1:
        raise RuntimeError("Failed to update project version in pom.xml")

    if dry_run:
        print(f"+ update pom.xml version: {old_version} -> {new_version}")
        return

    pom_path.write_text(updated, encoding="utf-8")


def write_constants_version(
    constants_path: Path, old_version: str, new_version: str, dry_run: bool = False
) -> None:
    if old_version == new_version:
        return
    constants_text = constants_path.read_text(encoding="utf-8")
    updated, replacements = re.subn(
        r'(OSCAR_VERSION\s*=\s*")' + re.escape(old_version) + r'(";\s*)',
        rf'\g<1>{new_version}\g<2>',
        constants_text,
        count=1,
    )
    if replacements != 1:
        raise RuntimeError("Failed to update OSCAR_VERSION in Constants.java")
    if dry_run:
        print(f"+ update Constants.java version: {old_version} -> {new_version}")
        return
    constants_path.write_text(updated, encoding="utf-8")


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
      for chunk in iter(lambda: handle.read(1024 * 1024), b""):
        digest.update(chunk)
    return digest.hexdigest()


def ensure_clean_worktree(dry_run: bool = False) -> None:
    output = run(["git", "status", "--porcelain"], dry_run=dry_run, capture=True)
    if dry_run:
        return
    if output.strip():
        raise RuntimeError("Working tree is not clean. Commit or stash changes before tagging.")


def tag_exists(tag_name: str, dry_run: bool = False) -> bool:
    if dry_run:
        return False
    proc = subprocess.run(
        ["git", "rev-parse", "-q", "--verify", f"refs/tags/{tag_name}"],
        text=True,
        capture_output=True,
        check=False,
    )
    return proc.returncode == 0


def create_tag(tag_name: str, version: str, dry_run: bool = False) -> None:
    if tag_exists(tag_name, dry_run=dry_run):
        raise RuntimeError(f"Tag already exists: {tag_name}")
    run(["git", "tag", "-a", tag_name, "-m", f"Release {version}"], dry_run=dry_run)


def push_tag(tag_name: str, dry_run: bool = False) -> None:
    run(["git", "push", "origin", tag_name], dry_run=dry_run)


def current_branch(dry_run: bool = False) -> str:
    branch = run(["git", "rev-parse", "--abbrev-ref", "HEAD"], dry_run=dry_run, capture=True)
    if dry_run:
        return "<current-branch>"
    return branch.strip()


def ensure_release_branch(branch: str, dry_run: bool = False) -> None:
    if dry_run:
        print(f"+ verify release branch is one of: {', '.join(ALLOWED_RELEASE_BRANCHES)}")
        return
    if branch not in ALLOWED_RELEASE_BRANCHES:
        raise RuntimeError(
            f"Release can only run from main or master (current branch: {branch})"
        )


def commit_release(version: str, dry_run: bool = False) -> None:
    run(["git", "add", "pom.xml", str(CONSTANTS_FILE_PATH)], dry_run=dry_run)
    run(["git", "commit", "-m", f"release: v{version}"], dry_run=dry_run)


def push_branch(branch: str, dry_run: bool = False) -> None:
    run(["git", "push", "origin", branch], dry_run=dry_run)


def build_release(
    repo_root: Path,
    skip_checks: bool,
    dry_run: bool,
    bump: str,
    commit_changes: bool,
    create_git_tag: bool,
    push_changes: bool,
) -> None:
    pom_path = repo_root / "pom.xml"
    ensure_clean_worktree(dry_run=dry_run)

    current_version = read_version(pom_path)
    new_version = bump_version(current_version, bump)
    write_version(pom_path, current_version, new_version, dry_run=dry_run)
    write_constants_version(
        repo_root / CONSTANTS_FILE_PATH, current_version, new_version, dry_run=dry_run
    )
    version = new_version
    tag_name = f"v{version}"
    branch = current_branch(dry_run=dry_run)
    ensure_release_branch(branch, dry_run=dry_run)

    if not skip_checks:
        run(["tools/run-all.sh"], dry_run=dry_run)

    run(["mvn", "-q", "-DskipTests", "package"], dry_run=dry_run)

    dist_dir = repo_root / "dist"
    dist_dir.mkdir(parents=True, exist_ok=True)

    if dry_run:
        source_jar = dist_dir / "oscar-<version>-all.jar"
    else:
        candidates = sorted(dist_dir.glob("*-all.jar"))
        if not candidates:
            candidates = sorted((repo_root / "target").glob("*-all.jar"))
        if not candidates:
            raise RuntimeError("No shaded jar found in dist/ or target/ after package")
        source_jar = candidates[-1]

    oscar_jar = repo_root / "oscar.jar"
    if dry_run:
        print(f"+ copy {source_jar} {oscar_jar}")
    else:
        shutil.copy2(source_jar, oscar_jar)

    latest_symlink = repo_root / "latest-build"
    if dry_run:
        print("+ ln -sfn oscar.jar latest-build")
    else:
        if latest_symlink.exists() or latest_symlink.is_symlink():
            latest_symlink.unlink()
        latest_symlink.symlink_to("oscar.jar")

    sha256 = "dry-run"
    if not dry_run:
        sha256 = sha256_file(oscar_jar)
    timestamp = datetime.now(timezone.utc).isoformat()

    report_path = dist_dir / "release-info.txt"
    if dry_run:
        print(f"+ write {report_path}")
    else:
        report_path.write_text(
            "\n".join(
                [
                    f"version={version}",
                    f"timestamp_utc={timestamp}",
                    f"source_jar={source_jar}",
                    f"oscar_jar_sha256={sha256}",
                ]
            )
            + "\n",
            encoding="utf-8",
        )

    if commit_changes:
        commit_release(version, dry_run=dry_run)

    if create_git_tag:
        create_tag(tag_name, version, dry_run=dry_run)

    if push_changes:
        if not commit_changes:
            raise RuntimeError("push requested without commit enabled")
        push_branch(branch, dry_run=dry_run)
        if create_git_tag:
            push_tag(tag_name, dry_run=dry_run)

    print("Release build complete")
    print(f" version: {version}")
    print(f" source : {source_jar}")
    print(f" oscar  : {oscar_jar}")
    print(f" sha256 : {sha256}")
    print(f" info   : {report_path}")
    if commit_changes:
        print(f" commit : release: v{version}")
    if create_git_tag:
        print(f" tag    : {tag_name}")
    if push_changes:
        print(f" pushed : origin/{branch}")
        if create_git_tag:
            print(f" pushed : tag {tag_name}")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Build release artifacts for Oscar")
    parser.add_argument(
        "-v",
        "--next-version",
        dest="show_version",
        action="store_true",
        help="show current version and next version for selected bump, then exit",
    )
    parser.add_argument(
        "--skip-checks",
        action="store_true",
        help="skip tools/run-all.sh before packaging",
    )
    parser.add_argument(
        "--bump",
        choices=BUMP_CHOICES,
        default="maintenance",
        help="version bump type (default: maintenance)",
    )
    parser.add_argument(
        "--no-tag",
        action="store_true",
        help="do not create a release tag",
    )
    parser.add_argument(
        "--no-commit",
        action="store_true",
        help="do not create a release commit",
    )
    parser.add_argument(
        "--no-push",
        action="store_true",
        help="do not push release commit/tag to origin",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="print release/tag commands without changing files or git refs",
    )
    return parser.parse_args()


def find_repo_root(start: Path) -> Path:
    for candidate in [start, *start.parents]:
        if (candidate / "pom.xml").is_file():
            return candidate
    raise RuntimeError("Unable to locate repo root (pom.xml not found in parent paths)")


def main() -> int:
    args = parse_args()
    repo_root = find_repo_root(Path(__file__).resolve().parent)

    try:
        if args.show_version:
            current = read_version(repo_root / "pom.xml")
            next_version = bump_version(current, args.bump)
            print(f"current_version={current}")
            print(f"next_version={next_version}")
            print(f"bump={args.bump}")
            return 0

        build_release(
            repo_root,
            skip_checks=args.skip_checks,
            dry_run=args.dry_run,
            bump=args.bump,
            commit_changes=not args.no_commit,
            create_git_tag=not args.no_tag,
            push_changes=not args.no_push,
        )
        return 0
    except subprocess.CalledProcessError as exc:
        print(f"ERROR: command failed with exit code {exc.returncode}", file=sys.stderr)
        return exc.returncode
    except Exception as exc:  # pragma: no cover
        print(f"ERROR: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
