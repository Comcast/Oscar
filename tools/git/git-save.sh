#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd -- "${SCRIPT_DIR}/../.." && pwd)"
COMMIT_MSG=""

usage() {
  cat <<'EOF'
Usage: git-save.sh --commit-msg <message>

Stage all Oscar repository changes and create a local Git commit.
Includes tracked changes, deletions, and non-ignored untracked files.
Does not push. Git hooks and commit failures propagate to the caller.

Options:
  --commit-msg <message>  Required commit message; may contain newlines.
  -h, --help             Show this help and exit.

Example:
  tools/git/git-save.sh --commit-msg "Feature: Add configuration tooling"
EOF
}

die() {
  echo "ERROR: $*" >&2
  exit 1
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --commit-msg)
      [[ $# -ge 2 ]] || die "--commit-msg requires a message"
      COMMIT_MSG="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *) die "Unknown argument: $1 (see --help)" ;;
  esac
done

[[ -n "${COMMIT_MSG//[[:space:]]/}" ]] || die "--commit-msg is required"
GIT_ROOT="$(git -C "${PROJECT_DIR}" rev-parse --show-toplevel 2>/dev/null)" ||
  die "Project directory is not a Git working tree: ${PROJECT_DIR}"
[[ "$(cd -- "${GIT_ROOT}" && pwd -P)" == "$(cd -- "${PROJECT_DIR}" && pwd -P)" ]] ||
  die "The script must belong to the root Oscar working tree"

git -C "${PROJECT_DIR}" add --all
if git -C "${PROJECT_DIR}" diff --cached --quiet; then
  die "No staged changes to commit"
fi

echo "Staged changes:"
git -C "${PROJECT_DIR}" diff --cached --stat
printf '\nCommit message: %s\n' "${COMMIT_MSG}"
git -C "${PROJECT_DIR}" commit -m "${COMMIT_MSG}"
