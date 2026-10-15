# Save repository changes

Run from the Oscar repository:

```bash
tools/git/git-save.sh --commit-msg "Feature: Add configuration tooling
- Add configuration helpers
- Tests: Run relevant validation"
```

The helper stages all tracked changes, deletions, and non-ignored untracked
files throughout the repository and creates a local commit. Review `git status
--short`, `git diff --stat`, `git diff`, and untracked files first. Existing
staged changes are included; unstaged edits to those files are also staged.

The script locates Oscar relative to its own path, so an absolute invocation
works from another directory. Git and a configured commit identity are required.
It does not push or change Git configuration. Git hooks run normally. A failed
commit returns a nonzero status and leaves changes staged for correction.
Missing or blank messages, unknown arguments, and no changes also return a
nonzero status. Use `tools/git/git-save.sh --help` for usage.
