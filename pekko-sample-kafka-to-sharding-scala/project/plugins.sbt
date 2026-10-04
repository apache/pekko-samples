addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "2.0.0-M2")

addSbtPlugin("com.github.sbt" % "sbt-javaagent" % "0.2.0") // ALPN agent
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.8")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "1.0.0-alpha.5"
