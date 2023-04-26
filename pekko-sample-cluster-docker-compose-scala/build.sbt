organization := "org.apache.pekko"
name := "pekko-sample-cluster-docker-compose-scala"

/* scala versions and options */
scalaVersion := "2.13.10"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8",
  "-Xlint")

val pekkoVersion = "0.0.0+26617-325e2156-SNAPSHOT"
// allow access to snapshots
resolvers += "Apache Snapshots".at("https://repository.apache.org/content/repositories/snapshots/")

/* dependencies */
libraryDependencies ++= Seq(
  // -- Logging --
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  // -- Pekko --
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion)

version in Docker := "latest"
dockerExposedPorts in Docker := Seq(1600)
dockerRepository := Some("pekko")
dockerBaseImage := "eclipse-temurin:11"
enablePlugins(JavaAppPackaging)
