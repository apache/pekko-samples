organization := "org.apache.pekko"
name := "pekko-sample-fsm-scala"

val pekkoVersion = "1.1.0"
val logbackVersion = "1.3.14"

scalaVersion := "2.13.15"
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion)

licenses := Seq(
  ("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
