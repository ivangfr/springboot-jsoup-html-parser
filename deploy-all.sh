#!/usr/bin/env bash

kubectl create namespace dev

helm install my-mongodb \
--namespace dev \
--set image.tag=4.4.2-debian-10-r27 \
--set auth.rootPassword=secret \
--set auth.database=gamescoredb \
--set auth.username=gamescoreuser \
--set auth.password=gamescorepass \
--set persistence.enabled=false \
bitnami/mongodb

WAIT_MONGO=20
echo "Waiting $WAIT_MONGO seconds for MongoDB to get ready ..."
sleep $WAIT_MONGO

kubectl apply --namespace dev -f yaml-files/game-score-collector-job.yaml

WAIT_COLLECTOR=40
echo "Waiting $WAIT_COLLECTOR seconds for game-score-collector to get the data from website for the first time ..."
sleep $WAIT_COLLECTOR

kubectl apply --namespace dev -f yaml-files/game-score-api-deployment.yaml
kubectl apply --namespace dev -f yaml-files/game-score-collector-cronjob.yaml