# Apache Pekko Sample Projects

This repository contains a number of projects that illustrate various usages of [Apache Pekko](https://pekko.apache.org).
Samples are written in Scala and Java and use sbt or maven for build definitions.

## License

Please check the individual sample directories for the LICENSE associated with each sample.

## Code formatting

Each sbt example uses Apache Pekko's Scalafmt configuration. Examples containing
Java sources also use sbt-java-formatter with the JDK 17 formatter and import
sorting disabled. From an example directory, use `sbt applyCodeStyle` to format
sources and build files, and `sbt checkCodeStyle` to check them. Use JDK 17 or later
for Java formatting. Managed/generated sources are not formatting inputs.
Maven-only examples retain their existing build tooling.
