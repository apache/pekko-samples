pekko-sample-persistence-dc-java
===============================

## How to run

1. Setup Apache Cassandra
   * Either start a local Cassandra listening on port 9042 or in terminal 1: `mvn exec:java -Dexec.mainClass=sample.persistence.res.MainApp -Dexec.args="cassandra"`

1. In terminal 2: `mvn compile exec:java -Dexec.mainClass=sample.persistence.res.MainApp -Dexec.args="7345 eu-west"`

1. In terminal 3: `mvn compile exec:java -Dexec.mainClass=sample.persistence.res.MainApp -Dexec.args="7355 eu-central"`

1. In terminal 4:
    * To add a thumbs-up for resource `pekko` from user `u1` in DC `eu-central`: `curl -X POST http://127.0.0.1:27355/thumbs-up/pekko/u1`
    * To add a thumbs-up for resource `pekko` from user `u2` in DC `eu-west`: `curl -X POST http://127.0.0.1:27345/thumbs-up/pekko/u2`
    * To get the users that gave thumbs-up for resource `pekko`: `curl http://127.0.0.1:27345/thumbs-up/pekko`
    * Note the port numbers 27345 for eu-west and 27355 for eu-central
