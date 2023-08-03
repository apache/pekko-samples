// allow access to snapshots
resolvers += "Apache Snapshots".at("https://repository.apache.org/content/repositories/snapshots/")

addSbtPlugin("org.apache.pekko" % "sbt-pekko-grpc" % "0.0.0-73-c03eff2b-SNAPSHOT")
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")
