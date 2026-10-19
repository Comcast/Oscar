# Security scanning

## Enabled GitHub features

Dependabot alerts and automatic security-update PRs, secret scanning, and push
protection are enabled in repository settings. `.github/dependabot.yml` adds
weekly Maven and GitHub Actions version-update PRs. Changes are reviewed and
validated; nothing is automatically merged. Alerts depend on available advisory
and dependency metadata, not just on whether a build succeeds.

## Workflows and release gates

`security.yml` runs on dependency-related default-branch pushes and pull
requests, manual dispatch, and every release through the reusable workflow.
Path filters watch Maven declarations/wrappers, installation inputs, and Actions
workflows. OSCAR-UI also watches OSCAR/Qt bootstrap scripts, source archive pins,
the packaging dependency declarations, and the OSCAR updater. Ordinary Java
source, documentation, and image changes do not trigger dependency scans.
Changing a Java `import` alone does not change the resolved package version.

`codeql.yml` runs weekly and manually, independently of dependency changes.
CodeQL is no longer part of the per-push, per-PR, or release workflow gate.
The checks have separate responsibilities:

- CodeQL analyzes Java, Python, and GitHub Actions. Java uses a manual compile.
  Alerts appear in the repository Security tab. A successful analysis does not
  mean there are no findings; review the alerts separately.
- Grype scans resolved runtime JARs, including transitive Maven dependencies,
  and fails on high/critical findings even when no fix is available. Scanner or
  database failures also block this job. The JSON findings and dependency tree
  are retained as workflow artifacts, including on scan failure.
- On matching default-branch pushes, Maven dependency submission populates GitHub's
  dependency graph for Dependabot. Submission has contents-write permission;
  it does not run for fork PRs or release tags. The reusable release caller
  grants the permission ceiling, while scanning jobs retain read access.

`dependency-review.yml` uses the same path filters and checks pull requests
for newly introduced known high/critical dependency vulnerabilities. It supplements the full runtime scan;
PR review alone would not cover direct pushes. No branch-protection rules were
changed: direct pushes remain possible, but the release workflow requires the
dependency-security jobs to finish successfully before building/uploading
release assets.
Repository administrators can separately make these checks required for PRs.

The Dependency security and CodeQL badges link to their separate workflows,
not a certification that the project has no vulnerabilities. Each shows its
latest default-branch result; code-only pushes need not produce a new result.
New workflow files and badges become active after they are pushed. Dependabot
alerts remain enabled to detect newly disclosed dependency vulnerabilities
even without package changes. Weekly CodeQL scans inspect source code.

## Scope and limitations

OSCAR-UI bootstraps the pinned OSCAR release before resolving dependencies and
includes both Linux and Windows Maven profiles in the scan. OSCAR itself is
scanned from its source dependency graph. Dependency submission cannot make
unpublished OSCAR versions automatically updatable by Dependabot: use the
existing OSCAR update helper for those pins and checksums.

The runtime-JAR scan is not a complete native installer audit. Bundled OpenJDK,
Qt DLLs/shared libraries, ICU, and Visual C++ runtimes require additional
component inventory and native vulnerability coverage. Bootstrap Python tooling
and test/build-only dependencies are also outside this runtime scan. Do not
interpret a green badge as proof that these components were scanned.
Secret scanning detects supported patterns; it does not replace keeping customer
configuration files and community strings out of Git.

## Handling failures

Review the dependency-security-reports artifact and CodeQL Security alerts.
Prefer updating the affected dependency, then run tests and the security scan.
Do not lower the threshold or broadly suppress findings to publish. Any future
exception needs a reviewed advisory/package/version scope, rationale, owner,
and expiration; no vulnerability exceptions are configured here.

For OSCAR-UI, a failure prevents installer publication. OSCAR's existing release
workflow starts after release publication: this gate blocks JAR build/upload,
not creation of the release page itself. Manual OSCAR release builds scan the
same selected tag as the build job. Changes to the security workflows must be
present in the workflow revision used for that release.

## Verification

Lint the workflow files with actionlint and verify dependency resolution using
the Maven commands in `security.yml`. Confirm the first hosted run resolves
CodeQL packs, the Grype database, and submits the graph successfully. These
network-backed checks are not replaced by local Java tests. Check that both
release workflows require the dependency Security job and retain reports after failures.
