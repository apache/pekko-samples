ThisBuild / organization := "org.apache.pekko"

name := "pekko-sample-cluster-kubernetes-scala"

scalaVersion := "2.13.11"
val pekkoHttpVersion = "0.0.0+4468-963bd592-SNAPSHOT"
val pekkoVersion = "1.0.0"
val pekkoManagementVersion = "0.0.0+757-f7d48cde-SNAPSHOT"
val logbackVersion = "1.2.12"

// allow access to snapshots
resolvers += "Apache Snapshots".at("https://repository.apache.org/content/repositories/snapshots/")

// make version compatible with docker for publishing
ThisBuild / dynverSeparator := "-"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")
classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars
run / fork := true
Compile / run / fork := true

mainClass in (Compile, run) := Some("pekko.sample.cluster.kubernetes.DemoApp")

enablePlugins(JavaServerAppPackaging, DockerPlugin)

dockerExposedPorts := Seq(8080, 8558, 17355)
dockerUpdateLatest := true
dockerUsername := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")
dockerBaseImage := "adoptopenjdk:11-jre-hotspot"

libraryDependencies ++= {
  Seq(
    "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
    "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
    "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion,
    "org.apache.pekko" %% "pekko-cluster-sharding-typed" % pekkoVersion,
    "org.apache.pekko" %% "pekko-stream-typed" % pekkoVersion,
    "org.apache.pekko" %% "pekko-discovery" % pekkoVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.apache.pekko" %% "pekko-discovery-kubernetes-api" % pekkoManagementVersion,
    "org.apache.pekko" %% "pekko-management-cluster-bootstrap" % pekkoManagementVersion,
    "org.apache.pekko" %% "pekko-management-cluster-http" % pekkoManagementVersion,
    "org.apache.pekko" %% "pekko-testkit" % pekkoVersion % "test",
    "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
    "org.apache.pekko" %% "pekko-http-testkit" % pekkoHttpVersion % Test,
    "org.apache.pekko" %% "pekko-testkit" % pekkoVersion % Test,
    "org.apache.pekko" %% "pekko-stream-testkit" % pekkoVersion % Test)
}
