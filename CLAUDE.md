# Claude Rules for Apache Pekko Samples

Before opening or updating a PR, verify:

- Non-doc-only changes have directional tests.
- Native `scalafmt` or the sbt scalafmt tasks were run for changed Scala/SBT files, or the missing tool is recorded in `Tests`.
- Code must use syntax compatible with both Scala 2.13 and Scala 3. Do not use Scala 3-only syntax such as significant indentation, `as` for import rename, `*` for wildcard import, or postfix `*` for vararg splices. The `.scalafmt.conf` enforces these restrictions via `dialectOverride`.
- `sbt javafmtAll` was run with JDK 17 when relevant.
- `sbt headerCreateAll` was run to add headers for new files. Never hand-write or invent license headers; let sbt manage them, and preserve existing copyright notices intact.
- Commit messages follow the standard format.
- `Tests` and `References` are present.
- No `Co-authored-by` or AI-assistant trailers are added to commits or PR descriptions.
