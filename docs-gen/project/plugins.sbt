// allow access to snapshots for pekko-sbt-paradox
resolvers += "Apache Nexus Snapshots".at("https://repository.apache.org/content/repositories/snapshots/")

// We have to deliberately use older versions of sbt-paradox because current Pekko sbt build
// only loads on JDK 1.8 so we need to bring in older versions of parboiled which support JDK 1.8
addSbtPlugin(("org.apache.pekko" % "pekko-sbt-paradox" % "0.0.0+30-8bee46d0-SNAPSHOT").excludeAll(
  "com.lightbend.paradox", "sbt-paradox"))
addSbtPlugin(("com.lightbend.paradox" % "sbt-paradox" % "0.9.2").force())