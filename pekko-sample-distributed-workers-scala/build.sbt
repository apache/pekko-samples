name := "pekko-distributed-workers"

version := "1.0"

scalaVersion := "2.13.8"
val pekkoVersion = "0.0.0+26617-325e2156-SNAPSHOT"

val cassandraPluginVersion = "1.0.6"

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
  "commons-io" % "commons-io" % "2.4" % Test)
