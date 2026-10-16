# Publishing the GitHub wiki

Maintain pages in `docs/wiki/`, then use the separate wiki update tool.
Pushing the Oscar code repository does not publish these pages.

Requirements: Git, Python 3.9 or newer, network access, and (for publishing)
Git credentials with write access to the target wiki. Git must have a configured
commit name and email. The target wiki must already be initialized.

From the repository root:

```bash
# Clone the current wiki temporarily and show the proposed patch.
tools/update-wiki.sh

# Review the preview, then commit and push the wiki changes.
tools/update-wiki.sh --publish
```

From `tools/`, use `./update-wiki.sh`. Paths are resolved from the script, so
it also works when invoked from another directory.

The default destination is `https://github.com/Comcast/Oscar.wiki.git`.
The script copies top-level Markdown pages except `AGENTS.md`, including
`Home.md` and `_Footer.md`. Existing wiki-only pages and assets are preserved;
renamed or obsolete pages must be removed separately after review. SNMP community examples are checked in both copied and preserved Markdown
pages: recognized literal community values must be `public` or `private`.
Errors report only the file and line, never the rejected value. This check
covers named community settings and common SNMP CLI commands; it is not a
general secret scanner. No files in
the main repository are staged or committed by this tool.

Ordinary inline relative Markdown links are rewritten to wiki-page URLs or
main-repository file URLs. Code examples, external URLs, anchors, and absolute
paths are left alone. Missing local targets fail before publication. Reference
links and HTML links are not converted; use inline Markdown links for relative
links in the source pages. Images/assets are not uploaded automatically.

Repository file links default to the `master` branch. Publish referenced code
and documentation to that branch first, or select an existing tag/branch with
`--ref`. Local target checks do not prove those files exist on GitHub.

For a fork or release-specific documentation:

```bash
tools/update-wiki.sh \
  --wiki-url https://github.com/YOUR-ACCOUNT/Oscar.wiki.git \
  --repository-url https://github.com/YOUR-ACCOUNT/Oscar \
  --ref YOUR-BRANCH
```

Add `--publish` only when ready. Publishing creates a normal wiki commit and
pushes without force; concurrent remote changes cause the push to fail. Rerun
the preview to incorporate those changes. An unchanged wiki produces no commit.

The temporary checkout is removed on completion or failure. To retry a failed
push, rerun the command; the source pages remain in `docs/wiki/`.

Validation:

```bash
python3 -B -m unittest discover -s tools/maintenance -p 'test_update_wiki.py'
bash -n tools/update-wiki.sh
```
