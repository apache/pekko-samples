organization := "org.apache.pekko"
name := "pekko-sample-replicated-event-sourcing-scala"

scalaVersion := "2.13.10"

val pekkoVersion = "0.0.0+26617-325e2156-SNAPSHOT"
val cassandraPluginVersion = "0.0.0-1068-9a5c7580-SNAPSHOT"

val pekkoHttpVersion = "0.0.0+4335-81a9800e-SNAPSHOT"
val pekkoClusterManagementVersion = "0.0.0+710-b49055bd-SNAPSHOT"

// allow access to snapshots
resolvers += "Apache Nexus Snapshots".at("https://repository.apache.org/content/groups/snapshots/")

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-cluster-sharding-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-persistence-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
  "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
  "org.apache.pekko" %% "pekko-management" % pekkoClusterManagementVersion,
  "org.apache.pekko" %% "pekko-management-cluster-http" % pekkoClusterManagementVersion,
  "org.apache.pekko" %% "pekko-persistence-cassandra" % cassandraPluginVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "org.apache.pekko" %% "pekko-persistence-cassandra-launcher" % cassandraPluginVersion,
  "org.apache.pekko" %% "pekko-persistence-testkit" % pekkoVersion % Test,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test)

// transitive dependency of akka 2.5x that is brought in by addons but evicted
dependencyOverrides += "org.apache.pekko" %% "pekko-protobuf" % pekkoVersion
dependencyOverrides += "org.apache.pekko" %% "pekko-cluster-tools" % pekkoVersion
dependencyOverrides += "org.apache.pekko" %% "pekko-coordination" % pekkoVersion

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
