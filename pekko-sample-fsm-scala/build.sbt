organization := "org.apache.pekko"
name := "pekko-sample-fsm-scala"

val pekkoVersion = "1.0.1"
val logbackVersion = "1.2.12"

scalaVersion := "2.13.11"
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion)

licenses := Seq(
  ("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
