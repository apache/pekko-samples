organization := "org.apache.pekko"
name := "pekko-sample-replicated-event-sourcing-scala"

scalaVersion := "2.13.11"

val pekkoVersion = "0.0.0+26669-ec5b6764-SNAPSHOT"
val cassandraPluginVersion = "0.0.0-1095-5ca43b58-SNAPSHOT"

val pekkoHttpVersion = "0.0.0+4411-6fe04045-SNAPSHOT"
val pekkoClusterManagementVersion = "0.0.0+752-95cdd415-SNAPSHOT"

val logbackVersion = "1.2.12"

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
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.apache.pekko" %% "pekko-persistence-cassandra-launcher" % cassandraPluginVersion,
  "org.apache.pekko" %% "pekko-persistence-testkit" % pekkoVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test)

// transitive dependency of akka 2.5x that is brought in by addons but evicted
dependencyOverrides += "org.apache.pekko" %% "pekko-protobuf" % pekkoVersion
dependencyOverrides += "org.apache.pekko" %% "pekko-cluster-tools" % pekkoVersion
dependencyOverrides += "org.apache.pekko" %% "pekko-coordination" % pekkoVersion

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
