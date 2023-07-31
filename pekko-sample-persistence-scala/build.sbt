organization := "org.apache.pekko"
name := "pekko-sample-persistence-scala"

scalaVersion := "2.13.11"
val pekkoVersion = "1.0.1"
val logbackVersion = "1.2.12"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-persistence-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test)

Compile / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")

// show full stack traces and test case durations
Test / testOptions += Tests.Argument("-oDF")
Test / logBuffered := false

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
