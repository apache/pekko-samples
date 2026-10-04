import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.{ jvmOptions, MultiJvm }

ThisBuild / evictionErrorLevel := Level.Info
val pekkoVersion = "2.0.0-M4"

lazy val `pekko-sample-cluster-client-grpc-scala` = project
  .in(file("."))
  .enablePlugins(JavaAgent)
  .enablePlugins(PekkoGrpcPlugin)
  .settings(multiJvmSettings: _*)
  .settings(
    organization := "org.apache.pekko",
    // Agrona (used by pekko-remote Artery) needs access to jdk.internal.misc.Unsafe
    MultiJvm / jvmOptions ++= Seq("--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED"),
    scalaVersion := "3.3.7",
    Compile / scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked"),
    Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    // javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.9" % "runtime",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-cluster" % pekkoVersion,
      "org.apache.pekko" %% "pekko-cluster-tools" % pekkoVersion,
      "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
      "org.apache.pekko" %% "pekko-discovery" % pekkoVersion,
      "org.apache.pekko" %% "pekko-multi-node-testkit" % pekkoVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test))
  .configs(MultiJvm)
