organization := "org.apache.pekko"
name := "pekko-sample-replicated-event-sourcing-scala"

scalaVersion := "2.13.14"

val pekkoVersion = "1.1.1"
val cassandraPluginVersion = "1.0.0"

val pekkoHttpVersion = "1.0.1"
val pekkoClusterManagementVersion = "1.0.0"

val logbackVersion = "1.2.13"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-cluster-sharding-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
  "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
  "org.apache.pekko" %% "pekko-management" % pekkoClusterManagementVersion,
  "org.apache.pekko" %% "pekko-management-cluster-http" % pekkoClusterManagementVersion,
  "org.apache.pekko" %% "pekko-persistence-cassandra" % cassandraPluginVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.apache.pekko" %% "pekko-persistence-cassandra-launcher" % cassandraPluginVersion,
  "org.apache.pekko" %% "pekko-persistence-testkit" % pekkoVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.19" % Test)

// transitive dependency of akka 2.5x that is brought in by addons but evicted
dependencyOverrides += "org.apache.pekko" %% "pekko-protobuf" % pekkoVersion
dependencyOverrides += "org.apache.pekko" %% "pekko-cluster-tools" % pekkoVersion
dependencyOverrides += "org.apache.pekko" %% "pekko-coordination" % pekkoVersion

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

// Startup aliases for the cassandra server and for two seed nodes one for eu-west and another for eu-central
addCommandAlias("cassandra", "runMain sample.persistence.res.MainApp cassandra")
addCommandAlias("eu-west", "runMain sample.persistence.res.MainApp 7345 eu-west")
addCommandAlias("eu-central", "runMain sample.persistence.res.MainApp 7355 eu-central")
