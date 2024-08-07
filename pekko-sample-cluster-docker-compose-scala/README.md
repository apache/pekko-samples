# pekko-sample-cluster-docker-compose-scala

An example pekko-cluster project with docker support. See [the blog post](http://blog.michaelhamrah.com/2014/11/clustering-akka-applications-with-docker-version-3/). Uses [SBT Native Packager](https://github.com/sbt/sbt-native-packager).

This sample is based on [akka-sample-cluster-docker-compose-scala](https://github.com/akka/akka-sample-cluster-docker-compose-scala).

### How to Run

In SBT, just run `docker:publishLocal` to create a local docker container. 

To run the cluster, run `docker compose up`. This will create 3 nodes, a seed and two regular members, called `seed`, `c1`, and `c2` respectively.

While running, try opening a new terminal and (from the same directory) try things like `docker compose stop seed` and watch the cluster nodes respond.
