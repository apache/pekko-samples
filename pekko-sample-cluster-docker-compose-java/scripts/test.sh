#!/bin/bash

set -x

sbt docker:publishLocal

docker-compose up -d

docker logs akka-sample-cluster-docker-compose-java_seed_1

for i in {1..10}
do
  echo "Checking for MemberUp logging..."
  docker logs akka-sample-cluster-docker-compose-java_seed_1 | grep "Member is Up" | wc -l || true
  [ `docker logs akka-sample-cluster-docker-compose-java_seed_1 | grep "Member is Up" | wc -l` -eq 3 ] && break
  sleep 4
done

if [ $i -eq 10 ]
then
  echo "No 3 MemberUp log events found:"
  docker logs akka-sample-cluster-docker-compose-java_seed_1
  exit -1
fi
