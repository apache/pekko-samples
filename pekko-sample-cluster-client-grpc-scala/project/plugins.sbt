// allow access to snapshots
resolvers += Resolver.ApacheMavenSnapshotsRepo

addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "1.0.0-RC1-3-ae23c14d-SNAPSHOT")
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")
