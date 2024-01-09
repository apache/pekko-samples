import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

val pekkoVersion = "1.0.2"

lazy val `pekko-sample-cluster-client-grpc-scala` = project
  .in(file("."))
  .enablePlugins(JavaAgent)
  .enablePlugins(PekkoGrpcPlugin)
  .settings(multiJvmSettings: _*)
  .settings(
    organization := "org.apache.pekko",
    scalaVersion := "2.13.11",
    Compile / scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlog-reflective-calls",
      "-Xlint"),
    Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    // javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.9" % "runtime",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-cluster" % pekkoVersion,
      "org.apache.pekko" %% "pekko-cluster-tools" % pekkoVersion,
      "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
      "org.apache.pekko" %% "pekko-discovery" % pekkoVersion,
      "org.apache.pekko" %% "pekko-multi-node-testkit" % pekkoVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.15" % Test))
  .configs(MultiJvm)
