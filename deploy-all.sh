#!/usr/bin/env bash

helm install \
--name my-mongodb \
--set image.tag=4.0.9 \
--set image.pullPolicy=IfNotPresent \
--set usePassword=false \
--set persistence.enabled=false \
stable/mongodb

sleep 20

kubectl apply -f yaml-files/game-score-collector-job.yaml

sleep 40

kubectl apply -f yaml-files/game-score-api-deployment.yaml
kubectl apply -f yaml-files/game-score-collector-cronjob.yaml