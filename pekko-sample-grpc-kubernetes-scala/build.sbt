name := "pekko-grpc-kubernetes"
scalaVersion := "2.13.14"

lazy val pekkoVersion = "1.1.1"
lazy val discoveryVersion = "1.0.0"
lazy val pekkoHttpVersion = "1.0.1"

lazy val root = (project in file("."))
  .aggregate(httpToGrpc, grpcService)

// HTTP service that calls out to a gRPC back end
lazy val httpToGrpc = (project in file("http-to-grpc"))
  .enablePlugins(PekkoGrpcPlugin, DockerPlugin, JavaAppPackaging)
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
      "org.apache.pekko" %% "pekko-discovery" % pekkoVersion,
      "org.apache.pekko" %% "pekko-pki" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "org.apache.pekko" %% "pekko-parsing" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-core" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-discovery-kubernetes-api" % discoveryVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.13"),
    dockerExposedPorts := Seq(8080))

// gRPC back end that echoes back messages
lazy val grpcService = (project in file("grpc-service"))
  .enablePlugins(PekkoGrpcPlugin, DockerPlugin, JavaAppPackaging)
  .settings(
    dockerExposedPorts := Seq(8080),
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor" % pekkoVersion,
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-pki" % pekkoVersion,
      "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "org.apache.pekko" %% "pekko-discovery" % pekkoVersion,
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.13"))
