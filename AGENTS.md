# Agent instructions

These instructions apply to the entire Oscar repository. Follow additional
instructions in nested `AGENTS.md` files when working in those directories.

## Configuration-file skills

Use the appropriate repository skill when implementing, explaining, encoding,
decoding, or validating DOCSIS configuration-file TLVs, including registry,
editor, codec, and file-integrity changes:

- DOCSIS 3.1: [oscar-docsis-31-config-tlvs](skills/oscar-docsis-31-config-tlvs/SKILL.md),
  pinned to **CM-SP-MULPIv3.1-I21-201020**.
- DOCSIS 4.0: [oscar-docsis-40-config-tlvs](skills/oscar-docsis-40-config-tlvs/SKILL.md),
    pinned to **CM-SP-MULPIv4.0-I12-261014**.

Current DOCSIS 4.0 implementation scope excludes L2VPN-specific settings and
eDOCSIS/eSAFE settings. Record them as deferred rather than claiming coverage.
Generated configuration tests use a public lab key, not production credentials;
passing them is not proof of CM/CMTS interoperability.

Choose the version from the requested target and existing project context. If
the target remains ambiguous and affects the encoding, clarify it before making
version-dependent changes. For work spanning both versions, consult each skill
separately and preserve their edition boundaries; do not silently mix definitions.
These skills cover configuration files, not general DOCSIS protocol review.

Read the selected skill's source map first and its implementation guide when
changing code. Verify configuration-file applicability and encoding against the
bundled source, cite the exact issue and section, and test serialized bytes and
decoded meaning against source-derived expectations. Mark unresolved definitions
as unverified rather than inventing standard encodings.

## Skill maintenance

Keep canonical skill files and bundled references under `skills/<skill-name>/`.
Expose each skill through relative symbolic links at both
`.agents/skills/<skill-name>` and `.claude/skills/<skill-name>`; do not duplicate
the contents in discovery directories. Keep directory names and frontmatter
names identical. Preserve bundled source files and their manifest hashes.

After updating a skill, validate its `SKILL.md`, resolve both discovery links,
and check referenced files and source-manifest hashes. Keep extracted specification text and its provenance manifests. PDF originals
are intentionally excluded; obtain the matching issue if text is ambiguous.

## Commit workflow

Use `tools/git/git-save.sh --commit-msg "<message>"` to stage all repository
changes and create a local commit when requested. See [usage](docs/git-save.md).
Before preparing a commit message, inspect `git status --short`,
`git diff --stat`, `git diff`, and every untracked file so the message covers
the complete staged scope. When asked only for a message, return a copy/paste
helper command without executing it.

Use `Feature`, `BugFix`, or `Documentation` followed by a colon and an imperative
summary. Add `- ` detail lines, including a `- Tests: ` line describing validation.
Keep each message line at most 60 characters. Use a double-quoted argument with
literal newlines for multiline messages.
