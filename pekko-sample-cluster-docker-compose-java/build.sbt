organization := "com.mlh"

/* scala versions and options */
scalaVersion := "2.13.1"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8",
  "-Xlint",
)

val akka = "2.6.11"

/* dependencies */
libraryDependencies ++= Seq (
  // -- Logging --
  "ch.qos.logback"    % "logback-classic"           % "1.2.3",
  // -- Akka --
  "com.typesafe.akka" %% "akka-actor-typed"         % akka,
  "com.typesafe.akka" %% "akka-cluster-typed"       % akka
)

version in Docker := "latest"

dockerExposedPorts in Docker := Seq(1600)

dockerEntrypoint in Docker := Seq("sh", "-c", "bin/clustering $*")

dockerRepository := Some("pekko")

dockerBaseImage := "java"
enablePlugins(JavaAppPackaging)
