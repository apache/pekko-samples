val pekkoVersion = "1.0.3"
val pekkoHttpVersion = "1.0.1"

val pekkoConnectorsKafkaVersion = "1.0.0"
val pekkoManagementVersion = "1.0.0"
val EmbeddedKafkaVersion = "2.4.1.1"
val logbackVersion = "1.2.13"
val slf4jVersion = "1.7.32"

ThisBuild / scalaVersion := "2.13.14"
ThisBuild / organization := "org.apache.pekko"
ThisBuild / Compile / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xlint")
ThisBuild / Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
ThisBuild / Test / testOptions += Tests.Argument("-oDF")
ThisBuild / licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

Global / cancelable := true // ctrl-c

lazy val `pekko-sample-kafka-to-sharding` = project.in(file(".")).aggregate(producer, processor, client)

lazy val kafka = project
  .in(file("kafka"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "org.slf4j" % "log4j-over-slf4j" % slf4jVersion,
      "io.github.embeddedkafka" %% "embedded-kafka" % EmbeddedKafkaVersion),
    cancelable := false)

lazy val client = project
  .in(file("client"))
  .enablePlugins(PekkoGrpcPlugin, JavaAgent)
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "org.apache.pekko" %% "pekko-discovery" % pekkoVersion))

lazy val processor = project
  .in(file("processor"))
  .enablePlugins(PekkoGrpcPlugin, JavaAgent)
  .settings(javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.9" % "runtime;test")
  .settings(libraryDependencies ++= Seq(
    "org.apache.pekko" %% "pekko-connectors-kafka" % pekkoConnectorsKafkaVersion,
    "org.apache.pekko" %% "pekko-connectors-kafka-cluster-sharding" % pekkoConnectorsKafkaVersion,
    "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
    "org.apache.pekko" %% "pekko-discovery" % pekkoVersion,
    "org.apache.pekko" %% "pekko-cluster-sharding-typed" % pekkoVersion,
    "org.apache.pekko" %% "pekko-stream-typed" % pekkoVersion,
    "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
    "org.apache.pekko" %% "pekko-management" % pekkoManagementVersion,
    "org.apache.pekko" %% "pekko-management-cluster-http" % pekkoManagementVersion,
    "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
    "org.scalatest" %% "scalatest" % "3.2.19" % Test))

lazy val producer = project
  .in(file("producer"))
  .settings(Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value))
  .settings(libraryDependencies ++= Seq(
    "org.apache.pekko" %% "pekko-connectors-kafka" % pekkoConnectorsKafkaVersion,
    "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.scalatest" %% "scalatest" % "3.2.19" % Test))
