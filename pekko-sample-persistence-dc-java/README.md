pekko-sample-persistence-dc-java
===============================

## How to run

1. Setup Apache Cassandra
   * Either start a local Cassnadsra listening on port 9042 or in terminal 1: `mvn exec:java -Dexec.mainClass="sample.persistence.multidc.ThumbsUpApp" -Dexec.args="cassandra"`

1. In terminal 2: `mvn compile exec:java -Dexec.mainClass="sample.persistence.multidc.ThumbsUpApp" -Dexec.args="2551 eu-west"`

1. In terminal 3: `mvn compile exec:java -Dexec.mainClass="sample.persistence.multidc.ThumbsUpApp" -Dexec.args="2552 eu-central"`

1. In terminal 4:
    * To add a thumbs-up for resource `pekko` from user `u1` in DC `eu-west`: `curl -X POST http://127.0.0.1:17356/thumbs-up/pekko/u1`
    * To add a thumbs-up for resource `pekko` from user `u2` in DC `eu-west`: `curl -X POST http://127.0.0.1:17357/thumbs-up/pekko/u2`
    * To get the users that gave thumbs-up for resource `pekko`: `curl http://127.0.0.1:22552/thumbs-up/pekko`
    * Note the port numbers 22551 for eu-west and 22552 for eu-central
