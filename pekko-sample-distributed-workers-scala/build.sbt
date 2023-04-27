name := "pekko-distributed-workers"

version := "1.0"

scalaVersion := "2.13.10"
val pekkoVersion = "0.0.0+26617-325e2156-SNAPSHOT"
val cassandraPluginVersion = "0.0.0-1068-9a5c7580-SNAPSHOT"
val logbackVersion = "1.2.11"

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
