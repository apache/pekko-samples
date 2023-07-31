Apache Pekko Replicated Event Sourcing Multi DC Sample
=====================================

This is an example project demonstrating [Apache Pekko Replicated Event Sourcing](https://pekko.apache.org/docs/pekko/current/typed/replicated-eventsourcing.html)
to run a replica per datacenter.

## How to run

1. In terminal 1: `sbt "runMain sample.persistence.res.MainApp cassandra"`

1. In terminal 2: `sbt "runMain sample.persistence.res.MainApp 7345 eu-west"`

1. In terminal 3: `sbt "runMain sample.persistence.res.MainApp 7355 eu-central"`

1. In terminal 4:
   * To add a thumbs-up for resource `pekko` from user `u1` in DC `eu-west`: `curl -X POST http://127.0.0.1:27355/thumbs-up/pekko/u1`
   * To add a thumbs-up for resource `pekko` from user `u2` in DC `eu-west`: `curl -X POST http://127.0.0.1:27345/thumbs-up/pekko/u2`
   * To get the users that gave thumbs-up for resource `pekko`: `curl http://127.0.0.1:27355/thumbs-up/pekko`
   * Note the port numbers 27355 for eu-west and 27345 for eu-central
