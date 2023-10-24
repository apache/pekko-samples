addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "1.0.1")

addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.4") // ALPN agent
addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.25")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.11"
