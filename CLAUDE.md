# Claude Rules for Apache Pekko Samples

Follow `AGENTS.md`.

Before opening or updating a PR, verify:

- Changed samples compile and run successfully.
- Java and Scala variants of the same sample stay in sync.
- `README.md` is updated for any sample behavior changes.
- Native `scalafmt` or the sbt scalafmt tasks were run for changed Scala/SBT files, or the missing tool is recorded in `Tests`.
- `sbt javafmtAll` was run with JDK 17 when relevant.
- `sbt headerCreateAll` was run to add headers for new files. Never hand-write or invent license headers; let sbt manage them, and preserve existing copyright notices intact.
- `docs-gen` still builds correctly if samples referenced by documentation were changed.
- Commit messages follow the `AGENTS.md` format.
- PR bodies follow the `AGENTS.md` format.
- `Tests` and `References` are present.
- No `Co-authored-by` or AI-assistant trailers are added to commits or PR descriptions.
