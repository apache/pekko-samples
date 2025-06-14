organization := "org.apache.pekko"
name := "pekko-sample-fsm-scala"

val pekkoVersion = "1.1.4"
val logbackVersion = "1.3.15"

scalaVersion := "3.3.6"
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion)

licenses := Seq(
  ("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
