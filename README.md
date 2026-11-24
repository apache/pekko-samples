# Apache Pekko Sample Projects

This repository contains a number of projects that illustrate various usages of [Apache Pekko](https://pekko.apache.org).
Samples are written in Scala and Java and use sbt or maven for build definitions.

## License

Please check the individual sample directories for the LICENSE associated with each sample.

## Organize Scala imports

Each sbt example containing Scala sources includes Scalafix. Run
`sbt sortImports` from that example directory to organize main, test, and multi-JVM test imports
and remove unused imports. Each example carries its own configuration so it
continues to work when copied outside this repository.

This uses Scalafix 0.14.10 and the OrganizeImports grouping from
[apache/pekko#3342](https://github.com/apache/pekko/pull/3342). Relative imports
remain unexpanded and adjacent to their root import.
