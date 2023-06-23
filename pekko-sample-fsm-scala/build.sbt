organization := "org.apache.pekko"
name := "pekko-sample-fsm-scala"

val pekkoVersion = "0.0.0+26669-ec5b6764-SNAPSHOT"
val logbackVersion = "1.2.12"

// allow access to snapshots
resolvers += "Apache Nexus Snapshots".at("https://repository.apache.org/content/groups/snapshots/")

scalaVersion := "2.13.11"
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion)

licenses := Seq(
  ("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
