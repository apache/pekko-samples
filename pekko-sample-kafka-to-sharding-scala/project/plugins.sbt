addSbtPlugin("org.apache.pekko" % "pekko-grpc-sbt-plugin" % "1.2.0")

addSbtPlugin("com.github.sbt" % "sbt-javaagent" % "0.1.8") // ALPN agent
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.7")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.17"
