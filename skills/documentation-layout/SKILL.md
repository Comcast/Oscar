---
name: documentation-layout
description: Organize OSCAR README, installation, and development documentation when adding or moving setup instructions, examples, release workflows, or feature guidance.
---

# OSCAR documentation layout

Keep the root README a concise entry point: project purpose, existing branding
and badges, release-JAR installation links for Windows/Linux/macOS, a development
setup link, and a short documentation index. Do not grow it into a build,
troubleshooting, release, or API manual.

## Where information belongs

- `docs/wiki/Installation.md`: user Java requirements, release asset selection,
  platform-specific download/run commands, updates, and removal. Users running
  the standalone JAR do not need a checkout or Maven. Keep Linux source setup
  separate from release-JAR installation and do not imply a native app installer.
- `docs/wiki/development.md`: checkout, development prerequisites, source builds,
  tests, artifacts, resource maintenance links, version bumps, tags, and release
  publication. The repository installer is Linux-specific; label that scope.
- Existing focused guides: CLI examples, APIs, MICs, SNMP behavior, implementation
  status, and resource maintenance. Link to these rather than duplicating them.

When relocating content, preserve useful details at their owning destination,
remove duplicate instructions, and repair relative links and incoming anchors.
Keep historical limitations distinct from current behavior. Do not claim an
operating system was tested merely because Java commands are documented.

## Keep commands maintainable

Check commands against current scripts and `pom.xml`. Prefer links to the
release listing in the README. Keep versioned download/build examples in guides;
if a new guide contains active version references, include it in
`VERSION_DOCUMENTATION_PATHS` in `tools/release/release.py` and run its tests.
Do not execute an installer, push, or release just to validate documentation.

Validate local links and anchors, command syntax, and the canonical skill plus
both discovery symlinks. Follow the applicable `AGENTS.md` validation rules,
including those under `docs/wiki/`. Keep the README brief as features are added:
add a focused-guide link rather than moving detailed instructions back into it.
