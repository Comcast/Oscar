#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd -P)"
die() { echo "[git-push] ERROR: $*" >&2; exit 1; }
MODE=""
FORCE_PUSH=false
while [[ $# -gt 0 ]]; do
  case "$1" in
    --force-push) FORCE_PUSH=true ;;
    --master|--branch)
      [[ -z "$MODE" ]] || die 'Choose exactly one mode.'
      MODE=branch ;;
    --help|-h)
      echo 'Usage: tools/git/git-push.sh --master [--force-push]'
      echo '--branch is an alias for --master.'
      echo '--force-push reruns checks for an up-to-date branch.'
      echo 'Requires a clean checkout. Never rewrites remote history.'
      echo 'Never creates tags, bumps versions, or publishes releases.'
      exit 0 ;;
    *) die 'Use --master or --branch (see --help).' ;;
  esac
  shift
done
[[ -n "$MODE" ]] || die 'Choose --master or --branch.'
cd "$ROOT"
[[ "$(git rev-parse --show-toplevel)" == "$ROOT" ]] || die 'Helper must belong to the checkout root.'
[[ -z "$(git status --porcelain)" ]] || die 'Commit or stash changes before pushing.'
BRANCH="$(git symbolic-ref --quiet --short HEAD)" || die 'Check out main or master.'
[[ "$BRANCH" == main || "$BRANCH" == master ]] || die 'Only main or master may be pushed.'
[[ "$(git config --bool --get remote.origin.mirror || true)" != true ]] || die 'Mirror remotes are not supported.'
FETCH_URL="$(git remote get-url origin)"
PUSH_URLS="$(git remote get-url --push --all origin)"
[[ "$FETCH_URL" == "$PUSH_URLS" ]] || die 'Origin must have one push URL matching its fetch URL.'
REF="refs/heads/$BRANCH"
LOCAL_OID="$(git rev-parse HEAD)"
echo "[git-push] Checking origin/$BRANCH for unpublished commits..."
REMOTE_REF="$(git ls-remote --refs origin "$REF")"
if [[ -n "$REMOTE_REF" ]]; then
  REMOTE_OID="${REMOTE_REF%%$'\t'*}"
  if [[ "$REMOTE_OID" == "$LOCAL_OID" ]]; then
    if [[ "$FORCE_PUSH" == false ]]; then
      echo '[git-push] Nothing to push; checks skipped.'
      echo 'Use --force-push to run checks again without rewriting history.'
      exit 0
    fi
    echo '[git-push] Origin is current; rerunning checks as requested.'
  fi
  git fetch --no-tags origin "$REF"
  git merge-base --is-ancestor FETCH_HEAD HEAD || die 'Branch is behind or diverged; reconcile it first.'
fi
echo '[git-push] Running format, hygiene, compile, and test checks...'
bash tools/run-all.sh
mvn -B test
python3 -m unittest discover -s tools/git -p 'test_*.py'
python3 -m unittest discover -s tools/release -p 'test_*.py'
[[ -z "$(git status --porcelain)" ]] || die 'Checks changed files; review and commit before retrying.'
[[ "$(git symbolic-ref --quiet --short HEAD)" == "$BRANCH" && "$(git rev-parse HEAD)" == "$LOCAL_OID" ]] || die 'Checkout changed during checks; retry.'
echo "[git-push] Checks passed; pushing only $BRANCH..."
git push --no-follow-tags --recurse-submodules=no origin "$REF:$REF"
echo '[git-push] Complete.'
