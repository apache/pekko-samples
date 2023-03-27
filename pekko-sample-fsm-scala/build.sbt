organization := "org.apache.pekko"
name := "pekko-sample-fsm-scala"

def pekkoVersion = "0.0.0+26626-3e1231c3-SNAPSHOT"

// allow access to snapshots
resolvers += "Apache Nexus Snapshots".at("https://repository.apache.org/content/groups/snapshots/")

scalaVersion := "2.13.8"
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.11")

licenses := Seq(
  ("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
