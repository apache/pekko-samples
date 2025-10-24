name := "pekko-distributed-workers-scala"

version := "1.0"

scalaVersion := "3.3.7"

val pekkoVersion = "1.2.1"
val cassandraPluginVersion = "1.1.0"
val logbackVersion = "1.3.15"

Global / cancelable := false

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-query" % pekkoVersion,
  "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-cassandra" % cassandraPluginVersion,
  // this allows us to start cassandra from the sample
  "org.testcontainers" % "testcontainers-cassandra" % "2.0.1",
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  // test dependencies
  "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "commons-io" % "commons-io" % "2.20.0" % Test)
