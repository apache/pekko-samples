resolvers += "Apache Snapshots".at("https://repository.apache.org/content/repositories/snapshots/")
addSbtPlugin("org.apache.pekko" % "sbt-pekko-grpc" % "0.0.0-73-c03eff2b-SNAPSHOT")

addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.4") // ALPN agent
addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.25")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.11"
