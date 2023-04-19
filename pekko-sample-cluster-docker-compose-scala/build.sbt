name := "akka-sample-cluster-docker-compose-scala"
maintainer := "Michael Hamrah <m@hamrah.com>"

/* scala versions and options */
scalaVersion := "2.13.1"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8",
  "-Xlint",
)

val akka = "2.6.12"

/* dependencies */
libraryDependencies ++= Seq (
  // -- Logging --
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  // -- Akka --
  "com.typesafe.akka" %% "akka-actor-typed"   % akka,
  "com.typesafe.akka" %% "akka-cluster-typed" % akka,
)

version in Docker := "latest"
dockerExposedPorts in Docker := Seq(1600)
dockerRepository := Some("lightbend")
dockerBaseImage := "java"
enablePlugins(JavaAppPackaging)
