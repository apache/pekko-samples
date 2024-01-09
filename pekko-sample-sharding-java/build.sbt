val pekkoVersion = "1.0.2"
val pekkoHttpVersion = "1.0.0"
val logbackVersion = "1.2.13"

lazy val buildSettings = Seq(
  organization := "org.apache.pekko",
  scalaVersion := "2.13.11")

lazy val commonJavacOptions = Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation")

lazy val commonSettings = Seq(
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
      "org.apache.pekko" %% "pekko-http-jackson" % pekkoHttpVersion,
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
      "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
      "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion))

// Startup aliases for the first two seed nodes and a third, more can be started.
addCommandAlias("sharding1", "runMain sample.killrweather.KillrWeather 7345")
addCommandAlias("sharding2", "runMain sample.killrweather.KillrWeather 7355")
addCommandAlias("sharding3", "runMain sample.killrweather.KillrWeather 0")
