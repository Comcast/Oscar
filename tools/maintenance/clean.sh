#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage: tools/clean.sh [options]

Options:
  --all       Clean all generated artifacts (target, output, dist, oscar.jar, latest-build)
  --output    Clean generated files in output/
  --dist      Clean generated files in dist/
  --target    Remove Maven target/
  --jar       Remove oscar.jar and latest-build symlink
  -h, --help  Show this help

Default behavior (no options): same as --all
USAGE
}

clean_output() {
  mkdir -p output
  find output -mindepth 1 ! -name '.gitkeep' -exec rm -rf {} +
  echo "Cleaned output/"
}

clean_dist() {
  mkdir -p dist
  find dist -mindepth 1 ! -name '.gitkeep' -exec rm -rf {} +
  echo "Cleaned dist/"
}

clean_target() {
  rm -rf target
  echo "Removed target/"
}

clean_jar_links() {
  rm -f oscar.jar
  rm -f latest-build
  echo "Removed oscar.jar and latest-build"
}

run_all=0
run_output=0
run_dist=0
run_target=0
run_jar=0

if [ "$#" -eq 0 ]; then
  run_all=1
fi

while [ "$#" -gt 0 ]; do
  case "$1" in
    --all)
      run_all=1
      ;;
    --output)
      run_output=1
      ;;
    --dist)
      run_dist=1
      ;;
    --target)
      run_target=1
      ;;
    --jar)
      run_jar=1
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
  shift
done

if [ "$run_all" -eq 1 ]; then
  clean_output
  clean_dist
  clean_target
  clean_jar_links
  exit 0
fi

[ "$run_output" -eq 1 ] && clean_output
[ "$run_dist" -eq 1 ] && clean_dist
[ "$run_target" -eq 1 ] && clean_target
[ "$run_jar" -eq 1 ] && clean_jar_links
