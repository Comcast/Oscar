#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd -- "${SCRIPT_DIR}/../.." && pwd)"
TAG=""
PUSH=false

usage() {
  cat <<'EOF'
Usage: update-tag.sh --tag <vMAJOR.MINOR.PATCH> [--push]

Create an annotated tag on HEAD if it is missing. Existing tags are never moved.
By default, only local tags are checked and no remote is contacted.

Options:
  -t, --tag <tag>  Required tag, for example v3.1.0.
  --push          Also check origin and publish a missing tag.
  -h, --help      Show this help and exit.

With --push, a remote-only tag is fetched rather than recreated on HEAD.
Conflicting local and remote tags cause an error; neither is overwritten.
This script does not commit files, bump versions, or publish a GitHub release.
EOF
}

die() {
  echo "ERROR: $*" >&2
  exit 1
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    -t|--tag)
      [[ $# -ge 2 ]] || die "$1 requires a tag"
      [[ -z "$TAG" ]] || die "Specify --tag only once"
      TAG="$2"
      shift 2
      ;;
    --push)
      PUSH=true
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *) die "Unknown argument: $1 (see --help)" ;;
  esac
done

[[ "$TAG" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]] ||
  die "--tag must use vMAJOR.MINOR.PATCH, for example v3.1.0"
GIT_ROOT="$(git -C "$PROJECT_DIR" rev-parse --show-toplevel 2>/dev/null)" ||
  die "Project directory is not a Git working tree: $PROJECT_DIR"
[[ "$(cd -- "$GIT_ROOT" && pwd -P)" == "$(cd -- "$PROJECT_DIR" && pwd -P)" ]] ||
  die "The script must belong to the root Oscar working tree"

REMOTE_TAG=""
if "$PUSH"; then
  if REMOTE_TAG="$(git -C "$PROJECT_DIR" ls-remote --refs --exit-code origin "refs/tags/$TAG")"; then
    if ! git -C "$PROJECT_DIR" show-ref --verify --quiet "refs/tags/$TAG"; then
      git -C "$PROJECT_DIR" fetch --no-tags origin "refs/tags/$TAG:refs/tags/$TAG"
    fi
  else
    status="$?"
    [[ "$status" -eq 2 ]] || die "Cannot check origin for $TAG (git exit $status)"
  fi
fi

if git -C "$PROJECT_DIR" show-ref --verify --quiet "refs/tags/$TAG"; then
  echo "Tag already exists locally: $TAG"
else
  git -C "$PROJECT_DIR" tag -a "$TAG" -m "Oscar ${TAG#v}"
  echo "Created local tag: $TAG"
fi

if "$PUSH"; then
  if [[ -n "$REMOTE_TAG" ]]; then
    remote_oid="${REMOTE_TAG%%$'\t'*}"
    local_oid="$(git -C "$PROJECT_DIR" rev-parse "refs/tags/$TAG")"
    [[ "$local_oid" == "$remote_oid" ]] ||
      die "$TAG differs from origin; refusing to overwrite either tag"
    echo "Tag already exists on origin: $TAG"
  else
    git -C "$PROJECT_DIR" push origin "refs/tags/$TAG:refs/tags/$TAG"
  fi
else
  echo "Not pushed. Use --push to ensure the tag also exists on origin."
fi