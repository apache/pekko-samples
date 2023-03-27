Apache Pekko Replicated Event Sourcing Multi DC Sample
=====================================

This is an example project demonstrating [Apache Pekko Replicated Event Sourcing](https://pekko.apache.org/docs/pekko/current//typed/replicated-eventsourcing.html)
to run a replica per datacenter.

## How to run

1. In terminal 1: `sbt "runMain sample.persistence.multidc.ThumbsUpApp cassandra"`

1. In terminal 2: `sbt "runMain sample.persistence.multidc.ThumbsUpApp 7345 eu-west"`

1. In terminal 3: `sbt "runMain sample.persistence.multidc.ThumbsUpApp 7355 eu-central"`

1. In terminal 4:
    * To add a thumbs-up for resource `pekko` from user `u1` in DC `eu-west`: `curl -X POST http://127.0.0.1:17356/thumbs-up/pekko/u1`
    * To add a thumbs-up for resource `pekko` from user `u2` in DC `eu-west`: `curl -X POST http://127.0.0.1:17357/thumbs-up/pekko/u2`
    * To get the users that gave thumbs-up for resource `pekko`: `curl http://127.0.0.1:17357/thumbs-up/pekko`
    * Note the port numbers 17356 for eu-west and 17357 for eu-central
