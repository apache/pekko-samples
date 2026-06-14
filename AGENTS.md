# Agent Rules for Apache Pekko Samples

If this file conflicts with community conventions, update this file.

## Worktree Rules

- Base new work on `origin/main` unless the user or maintainer requests another branch.
- Keep every PR scoped to one change.
- Do not mix behavior changes, formatting churn, dependency updates, and unrelated cleanup.
- Do not revert user changes or unrelated local changes.
- Use `rg` or `rg --files` for repository searches.
- Read neighboring code before editing.
- Preserve existing license and copyright notices.
- Do not add `@author` tags.
- Follow the Licensing Rules below for every new file.

## Project Structure

- Each sample is a standalone sbt project in its own directory (e.g., `pekko-sample-cluster-java/`).
- Each sample has its own `build.sbt` and `README.md`.
- Samples come in Java and Scala variants where applicable.
- The `docs-gen/` directory generates documentation from samples.

## Licensing Rules

- Do not hand-write or invent license headers. Let sbt manage them.
- For new files, run `sbt headerCreateAll` to add the correct header. Do not manually paste header text.
- Existing files with copyright statements must keep those copyright statements intact. Never delete or rewrite an existing copyright notice; only add information.
- New files containing new code must use the standard Apache license header.

## PR Rules

- Every sample change must keep the sample compilable and runnable.
- Sample changes must update the corresponding `README.md`.
- Java and Scala variants of the same sample must stay in sync.
- Dependency changes must verify Apache-compatible licenses.
- Do not break the `docs-gen` build when changing samples referenced by documentation.

## Formatting Rules

- Prefer native scalafmt for changed Scala and SBT files when it is available.

```shell
git fetch origin main
scalafmt --mode diff-ref=origin/main
scalafmt --list --mode diff-ref=origin/main
```

- If native scalafmt is not installed, use the sbt scalafmt tasks or record that scalafmt could not be run.

```shell
sbt scalafmtAll scalafmtSbt
sbt scalafmtCheckAll scalafmtSbtCheck
```

- Use JDK 17 for Java formatter tasks.

```shell
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
sbt javafmtAll
```

- Run header generation before PR.

```shell
sbt headerCreateAll
```

- Do not rely on IDE formatting alone.
- Do not commit unrelated formatting changes.

## Validation Rules

- Run the sample's tests.

```shell
cd pekko-sample-name
sbt test
```

- Compile the sample to verify it remains valid.

```shell
cd pekko-sample-name
sbt compile
```

- Run docs-gen to verify documentation generation.

```shell
cd docs-gen
sbt run
```

- Always run `git diff --check`.
- Do not assume local tools such as `sbt` or `scalafmt` are installed; if a required tool is missing, record the missing tool and skipped command in `Tests`.
- Skipped or environment-failed commands must be recorded in `Tests`.

## Code Rules

- Samples must be self-contained and easy to understand.
- Prefer clarity over cleverness in sample code.
- Each sample should demonstrate a specific Pekko feature or pattern.
- Keep Java and Scala variants functionally equivalent.
- Use `scala.jdk.*` converters for Java/Scala interop in Scala samples.

## Commit and PR Format

- Use this body format for non-trivial commits.

```text
Motivation:
Problem or requirement.

Modification:
Change summary.

Result:
New outcome.

Tests:
- command/result or Not run - docs only

References:
Fixes #1234, Refs #1234, or None - <short context>
```

- Use this PR body format.

```markdown
### Motivation
Problem or requirement.

### Modification
Change summary.

### Result
New outcome.

### Tests
- command/result or Not run - docs only

### References
Fixes #1234, Refs #1234, or None - <short context>
```

- Never omit `Tests`.
- Never omit `References`.
- Use `Refs #...`, `Fixes #...`, or `None - <short context>`.
- Do not add `Co-authored-by` or AI-assistant trailers to commits or PR descriptions.
