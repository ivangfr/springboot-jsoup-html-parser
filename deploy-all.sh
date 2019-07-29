#!/usr/bin/env bash

helm install \
--name my-mongodb \
--set image.tag=4.0.10 \
--set image.pullPolicy=IfNotPresent \
--set usePassword=false \
--set persistence.enabled=false \
stable/mongodb

WAIT_MONGO=20
echo "Waiting ${WAIT_MONGO} seconds for MongoDB to get ready ..."
sleep ${WAIT_MONGO}

kubectl apply -f yaml-files/game-score-collector-job.yaml

WAIT_COLLECTOR=40
echo "Waiting ${WAIT_COLLECTOR} seconds for game-score-collector to get the data from website for the first time ..."
sleep ${WAIT_COLLECTOR}

kubectl apply -f yaml-files/game-score-api-deployment.yaml
kubectl apply -f yaml-files/game-score-collector-cronjob.yaml