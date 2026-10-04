organization := "org.apache.pekko"
name := "pekko-sample-cluster-docker-compose-scala"

/* scala versions and options */
scalaVersion := "3.3.7"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8")

val pekkoVersion = "2.0.0-M4"
val logbackVersion = "1.3.15"

/* dependencies */
libraryDependencies ++= Seq(
  // -- Logging --
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  // -- Pekko --
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion)

Docker / version := "latest"
Docker / dockerExposedPorts := Seq(1600)
dockerRepository := Some("pekko")
dockerBaseImage := "eclipse-temurin:17"
// Agrona (used by pekko-remote Artery) needs access to jdk.internal.misc.Unsafe
bashScriptExtraDefines += """addJava "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED""""
enablePlugins(JavaAppPackaging)
