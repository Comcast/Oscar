# Git helpers

Run these helpers from any directory using their checkout path.

| Helper | Purpose |
| --- | --- |
| `git-save.sh --commit-msg "..."` | Stage all changes and commit locally |
| `git-push.sh --master` | Check and push the current main/master branch |
| `update-tag.sh --tag vX.Y.Z [--push]` | Create an annotated tag, optionally push it |

## Push committed changes

```bash
tools/git/git-push.sh --master
```

`--branch` is an alias. The helper requires a clean checkout, including untracked
files. It checks origin before running expensive checks: an identical branch
skips validation and upload; a behind/diverged branch fails. Origin must have
one push URL matching its fetch URL and must not be a mirror.

Before pushing, it runs `tools/run-all.sh`, Maven JUnit tests, and Python Git
and release-tool tests. Failed checks block upload. Formatting can modify files;
review and commit those changes before retrying. Missing formatter dependencies
can be installed with `tools/setup-formatter.sh`. Existing Git hooks still run.
The helper does not install hooks or guard direct `git push` commands.

Only the current main/master branch is pushed, without force or automatic tags.
No versions, commits, tags, or releases are created. For release publication,
use the separate [development instructions](../../docs/wiki/development.md).

Keep this inventory and its examples current when helpers change.

### Test output during push checks

JUnit stdout/stderr, including the intentional DEBUG round-trip test, is saved
in `target/surefire-reports/*-output.txt`. Maven test failures still fail the
push checks and display failure summaries. Inspect those output files and the
corresponding test reports when troubleshooting. To stream test output during a manual diagnostic run, use
`mvn -Dmaven.test.redirectTestOutputToFile=false test`.
