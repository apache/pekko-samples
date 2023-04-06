name := "pekko-distributed-workers"

version := "1.0"

scalaVersion := "2.13.10"
val pekkoVersion = "0.0.0+26617-325e2156-SNAPSHOT"

// allow access to snapshots
resolvers += "Apache Nexus Snapshots".at("https://repository.apache.org/content/groups/snapshots/")

val cassandraPluginVersion = "0.0.0-1068-9a5c7580-SNAPSHOT"

Global / cancelable := false

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-query" % pekkoVersion,
  "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-cassandra" % cassandraPluginVersion,
  // this allows us to start cassandra from the sample
  "org.apache.pekko" %% "pekko-persistence-cassandra-launcher" % cassandraPluginVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  // test dependencies
  "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "commons-io" % "commons-io" % "2.11.0" % Test)
