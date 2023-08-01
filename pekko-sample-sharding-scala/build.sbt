val pekkoVersion = "1.0.1"
val pekkoHttpVersion = "0.0.0+4468-963bd592-SNAPSHOT"
val logbackVersion = "1.2.12"

lazy val buildSettings = Seq(
  organization := "org.apache.pekko",
  scalaVersion := "2.13.11",
  // allow access to snapshots
  resolvers += "Apache Nexus Snapshots".at("https://repository.apache.org/content/groups/snapshots/"))

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-unused:imports",
  "-encoding", "UTF-8")

lazy val commonJavacOptions = Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation")

lazy val commonSettings = Seq(
  Compile / scalacOptions ++= commonScalacOptions,
  Compile / javacOptions ++= commonJavacOptions,
  run / javaOptions ++= Seq("-Xms128m", "-Xmx1024m"),
  run / fork := false,
  Global / cancelable := false,
  licenses := Seq(
    ("CC0", url("http://creativecommons.org/publicdomain/zero/1.0"))))

lazy val killrweather = project
  .in(file("killrweather"))
  .settings(buildSettings)
  .settings(commonSettings)
  .settings(
    Compile / run / mainClass := Some("sample.killrweather.KillrWeather"),
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-cluster-sharding-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
      "org.apache.pekko" %% "pekko-distributed-data" % pekkoVersion,
      "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion))

lazy val `killrweather-fog` = project
  .in(file("killrweather-fog"))
  .settings(buildSettings)
  .settings(commonSettings)
  .settings(
    Compile / run / mainClass := Some("sample.killrweather.fog.Fog"),
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
      "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion))

// Startup aliases for the first two seed nodes and a third, more can be started.
addCommandAlias("sharding1", "killrweather/runMain sample.killrweather.KillrWeather 7345")
addCommandAlias("sharding2", "killrweather/runMain sample.killrweather.KillrWeather 7355")
addCommandAlias("sharding3", "killrweather/runMain sample.killrweather.KillrWeather 0")