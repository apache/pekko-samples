import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

val pekkoVersion = "0.0.0+26617-325e2156-SNAPSHOT"

val `pekko-sample-distributed-data-scala` = project
  .in(file("."))
  .settings(multiJvmSettings: _*)
  .settings(
    organization := "org.apache.pekko",
    version := "1.0",
    scalaVersion := "2.13.8",
    scalacOptions in Compile ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    javaOptions in run ++= Seq("-Xms128m", "-Xmx1024m"),
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
      "org.apache.pekko" %% "pekko-multi-node-testkit" % pekkoVersion % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
      "ch.qos.logback" % "logback-classic" % "1.2.11" % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test),
    fork in run := true,
    Global / cancelable := false, // ctrl-c
    // disable parallel tests
    parallelExecution in Test := false,
    // show full stack traces and test case durations
    testOptions in Test += Tests.Argument("-oDF"),
    logBuffered in Test := false,
    licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
  )
  .configs (MultiJvm)