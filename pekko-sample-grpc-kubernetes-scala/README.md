# Apache Pekko gRPC Kubernetes

This is an example of an Apache Pekko HTTP service communicating with a gRPC service. Both services are deployed to Kubernetes. The HTTP service uses [Kubernetes API discovery mechanism](https://pekko.apache.org/docs/pekko-management/current/discovery/index.html#discovery-method-kubernetes-api) to find the gRPC service and send messages to the gRPC service.

The Apache Pekko HTTP application discovers the gRPC application using [Akka Discovery](https://developer.lightbend.com/docs/akka-management/current/discovery.html).
It uses the `pekko-dns` mechanism which relies on the `SRV` records created by Kubernetes.
All the technologies used in this example are open source.

## Usage

### Prerequisites

Install the following:

* [Docker](https://docs.docker.com/install/)
* [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* [Minikube](https://github.com/kubernetes/minikube)
* [Sbt](https://www.scala-sbt.org/)

### Publish images

You can publish the Docker images of the `grpcservice` and `httptogrpc` applications. You can use the `sbt docker:publishLocal` command to publish the images to your local Docker registry.

### Running

Once minikube is running and ingress enabled with `minikube addons enable ingress`, the two applications can be deployed using:

`kubectl apply -f kubernetes/grpcservice.yml`

and

`kubectl apply -f kubernetes/httptogrpc.yml`

Verify the deployments:

```
$ kubectl get deployments
NAME                          DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
grpcservice-v0-1-0-snapshot   1         1         1            1           40s
httptogrpc-v0-1-0-snapshot    1         1         1            1           40s
```

There are services for both:
```
$ kubectl get services
NAME          TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
grpcservice   ClusterIP   10.106.188.203   <none>        8080/TCP   1m
httptogrpc    ClusterIP   10.103.134.197   <none>        8080/TCP   1m
```

Ingress just for the HTTP app:

```
$ kubectl get ingress
NAME         HOSTS              ADDRESS   PORTS     AGE
httptogrpc   superservice.com             80        2m
```

The HTTP application periodically hits the gRPC application. You can view the logs of the HTTP application by running
```
$ kubectl logs service/httptogrpc
10/01 18:18:10 INFO [HttpToGrpc-pekko.actor.default-dispatcher-34] o.a.p.a.ActorSystemImpl - Scheduled say hello to chris
10/01 18:18:10 INFO [HttpToGrpc-pekko.actor.default-dispatcher-28] o.a.p.a.ActorSystemImpl - Scheduled say hello response Success(HelloReply(Hello, Christopher,UnknownFieldSet(Map())))
```

And you can send a HTTP request via `Ingress` to the `httptogrpc` application:

```
$ curl -v --header 'Host: superservice.com' $(minikube ip)/hello/donkey
> GET /hello/donkey HTTP/1.1
> Host: superservice.com
> User-Agent: curl/7.59.0
> Accept: */*
> 
< HTTP/1.1 200 OK
< Server: nginx/1.13.12
< Date: Wed, 15 Aug 2018 07:03:56 GMT
< Content-Type: text/plain; charset=UTF-8
< Content-Length: 13
< Connection: keep-alive
< 
* Connection #0 to host 192.168.99.100 left intact
Hello, donkey%
```

The `Host` header needs to be set as that is how minikube [Ingress](https://github.com/kubernetes/ingress-nginx) routes requests to services.
