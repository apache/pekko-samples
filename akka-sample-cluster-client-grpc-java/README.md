# Cluster Client with gRPC transport
	
See purpose of this example and important clarifications of when to use this approach in
[Migration to Akka gRPC](https://pekko.apache.org/docs/pekko/current/cluster-client.html#migration-to-apache-pekko-grpc).

Project structure:

* [clusterclient.proto](src/main/protobuf/clusterclient.proto) defines the gRPC service and messages
  that are exchanged between client and cluster (server)
* [ClusterClient](src/main/java/sample/cluster/client/grpc/ClusterClient.java) is the actor on the client
  side that messages are sent via
* [ClusterClientReceptionist](src/main/java/sample/cluster/client/grpc/ClusterClientReceptionist.java)
  is an Akka extension on the cluster (server) side that implements the gRPC service and delegates
  messages to actors in the cluster that have been registered in Distributed PubSub. 
