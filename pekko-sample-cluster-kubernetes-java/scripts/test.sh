#!/bin/bash

set -exu

eval $(minikube -p minikube docker-env)
mvn clean package docker:build

kubectl apply -f kubernetes/namespace.json
kubectl config set-context --current --namespace=appka-1
kubectl apply -f kubernetes/pekko-cluster.yml
kubectl expose deployment appka --type=LoadBalancer --name=appka-service

for i in {1..10}
do
  echo "Waiting for pods to get ready..."
  kubectl get pods
  [ `kubectl get pods | grep Running | wc -l` -eq 3 ] && break
  sleep 4
done

if [ $i -eq 10 ]
then
  echo "Pods did not get ready"
  exit -1
fi

POD=$(kubectl get pods | grep appka | grep Running | head -n1 | awk '{ print $1 }')

for i in {1..10}
do
  echo "Checking for MemberUp logging..."
  kubectl logs $POD | grep MemberUp || true
  [ `kubectl logs $POD | grep MemberUp | wc -l` -eq 3 ] && break
  sleep 3
done

kubectl get pods

echo "Logs"
echo "=============================="
for POD in $(kubectl get pods | grep appka | awk '{ print $1 }')
do
  echo "Logging for $POD"
  kubectl logs $POD
done

if [ $i -eq 10 ]
then
  echo "No 3 MemberUp log events found"
  echo "=============================="

  exit -1
fi
