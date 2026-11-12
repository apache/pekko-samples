## Finite State Machine in Actors

This sample is an adaptation of [Dining Hakkers](http://www.dalnefre.com/wp/2010/08/dining-philosophers-in-humus/). 

Open [DiningHakkers.scala](src/main/scala/sample/DiningHakkers.scala).

It illustrates how the behaviors and transitions can be defined with Apache Pekko Typed.

Start the application by typing `sbt "runMain sample.DiningHakkers"`. In the log output you can see the actions of the `Hakker` actors.

Read more about Apache Pekko Typed in [the documentation](https://pekko.apache.org/docs/pekko/current/typed/index.html).

### Organize Scala imports

Run `sbt sortImports` to organize Scala imports with Scalafix and remove unused
imports. The command enables SemanticDB and unused-import diagnostics for this sbt session
and processes both
main and test sources. Java sources are not rewritten.
