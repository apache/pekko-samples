organization := "org.apache.pekko"

/* scala versions and options */
scalaVersion := "3.3.7"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8")

val pekkoVersion = "1.5.0"
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

Docker / dockerEntrypoint := Seq("sh", "-c", "bin/clustering $*")

dockerRepository := Some("pekko")

dockerBaseImage := "eclipse-temurin:11"
enablePlugins(JavaAppPackaging)
