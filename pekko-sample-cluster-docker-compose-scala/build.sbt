organization := "org.apache.pekko"
name := "pekko-sample-cluster-docker-compose-scala"

/* scala versions and options */
scalaVersion := "3.3.7"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8",
  "-Xlint")

val pekkoVersion = "1.2.1"
val logbackVersion = "1.3.15"

/* dependencies */
libraryDependencies ++= Seq(
  // -- Logging --
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  // -- Pekko --
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion)

version in Docker := "latest"
dockerExposedPorts in Docker := Seq(1600)
dockerRepository := Some("pekko")
dockerBaseImage := "eclipse-temurin:11"
enablePlugins(JavaAppPackaging)
