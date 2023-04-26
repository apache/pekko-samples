#!/bin/bash

set -exu

clean_up () {
    echo "Cleaning up test kubernetes instance and minikube container"
    minikube delete
} 

eval $(minikube -p minikube docker-env)
mvn clean package docker:build

export KUBECONFIG=~/.kube/config
kubectl config set-context docker-desktop

kubectl apply -f kubernetes/namespace.json
kubectl config set-context --current --namespace=appka-1
kubectl apply -f kubernetes/pekko-cluster.yml
kubectl expose deployment appka --type=LoadBalancer --name=appka-service

for i in {1..10}
do
  echo "Waiting for pods to get ready..."
  kubectl get pods
  [ `kubectl get pods | grep Running | wc -l` -eq 2 ] && break
  sleep 4
done

if [ $i -eq 10 ]
then
  echo "Pods did not get ready"
  clean_up
  exit -1
fi

POD=$(kubectl get pods | grep appka | grep Running | head -n1 | awk '{ print $1 }')

for i in {1..10}
do
  echo "Checking for MemberUp logging..."
  kubectl logs $POD | grep MemberUp || true
  [ `kubectl logs $POD | grep MemberUp | wc -l` -eq 2 ] && break
  sleep 6
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
  echo "No 2 MemberUp log events found"
  echo "=============================="
  clean_up
  exit -1
fi

clean_up