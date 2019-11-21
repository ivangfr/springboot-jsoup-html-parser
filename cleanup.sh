#!/usr/bin/env bash

helm delete my-mongodb

kubectl delete -f yaml-files/game-score-api-deployment.yaml
kubectl delete -f yaml-files/game-score-collector-job.yaml
kubectl delete -f yaml-files/game-score-collector-cronjob.yaml