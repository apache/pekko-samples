name := "pekko-distributed-workers"

version := "1.0"

scalaVersion := "2.13.11"
val pekkoVersion = "1.0.1"
val cassandraPluginVersion = "0.0.0-1102-939e199d-SNAPSHOT"
val logbackVersion = "1.2.12"

// allow access to snapshots
resolvers += "Apache Nexus Snapshots".at("https://repository.apache.org/content/groups/snapshots/")

Global / cancelable := false

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-query" % pekkoVersion,
  "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-cassandra" % cassandraPluginVersion,
  // this allows us to start cassandra from the sample
  "org.apache.pekko" %% "pekko-persistence-cassandra-launcher" % cassandraPluginVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  // test dependencies
  "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "commons-io" % "commons-io" % "2.11.0" % Test)
