ThisBuild / resolvers += Resolver.ApacheMavenSnapshotsRepo

lazy val `pekko-sample-cluster-java` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Cluster with Java",
    baseProject := "pekko-sample-cluster-java")

lazy val `pekko-sample-cluster-scala` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Cluster with Scala",
    baseProject := "pekko-sample-cluster-scala")

lazy val `pekko-sample-distributed-data-java` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Distributed Data with Java",
    baseProject := "pekko-sample-distributed-data-java")

lazy val `pekko-sample-distributed-data-scala` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Distributed Data with Scala",
    baseProject := "pekko-sample-distributed-data-scala")

lazy val `pekko-sample-distributed-workers-scala` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Distributed Workers with Scala",
    baseProject := "pekko-sample-distributed-workers-scala")

lazy val `pekko-sample-fsm-java` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko FSM with Java",
    baseProject := "pekko-sample-fsm-java")

lazy val `pekko-sample-fsm-scala` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko FSM with Scala",
    baseProject := "pekko-sample-fsm-scala")

lazy val `pekko-sample-cluster-docker-compose-java` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Cluster Docker Compose with Java",
    baseProject := "pekko-sample-cluster-docker-compose-java")

lazy val `pekko-sample-cluster-docker-compose-scala` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Cluster Docker Compose with Scala",
    baseProject := "pekko-sample-cluster-docker-compose-scala")

lazy val `pekko-sample-persistence-java` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Persistence with Java",
    baseProject := "pekko-sample-persistence-java")

lazy val `pekko-sample-persistence-scala` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Persistence with Scala",
    baseProject := "pekko-sample-persistence-scala")

lazy val `pekko-sample-sharding-java` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Cluster Sharding with Java",
    baseProject := "pekko-sample-sharding-java")

lazy val `pekko-sample-sharding-scala` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Cluster Sharding with Scala",
    baseProject := "pekko-sample-sharding-scala")

lazy val `pekko-sample-kafka-to-sharding-scala` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko Kafka to Sharding with Scala",
    baseProject := "pekko-sample-kafka-to-sharding-scala")

lazy val `pekko-sample-grpc-kubernetes-scala` = project
  .enablePlugins(PekkoSamplePlugin)
  .settings(
    name := "Apache Pekko gRPC on Kubernetes",
    baseProject := "pekko-sample-grpc-kubernetes-scala")
