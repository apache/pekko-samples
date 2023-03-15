import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

val pekkoVersion = "0.0.0+26617-325e2156-SNAPSHOT"

val `pekko-sample-distributed-data-java` = project
  .in(file("."))
  .settings(multiJvmSettings: _*)
  .settings(
    organization := "org.apache.pekko",
    version := "1.0",
    scalaVersion := "2.13.8",
    Compile / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    Compile / javacOptions ++= Seq("-parameters", "-Xlint:unchecked", "-Xlint:deprecation", "-Xdiags:verbose"),
    run / javaOptions ++= Seq("-Xms128m", "-Xmx1024m"),
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
      "org.apache.pekko" %% "pekko-multi-node-testkit" % pekkoVersion % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
      "ch.qos.logback" % "logback-classic" % "1.2.11" % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test),
    run / fork := true,
    Global / cancelable := false, // ctrl-c
    // disable parallel tests
    Test / parallelExecution := false,
    // show full stack traces and test case durations
    Test / testOptions += Tests.Argument("-oDF"),
    Test / logBuffered := false,
    licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
  )
  .configs (MultiJvm)
