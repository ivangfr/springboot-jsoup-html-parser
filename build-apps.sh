#!/usr/bin/env bash

./mvnw clean compile jib:dockerBuild -DskipTests --projects game-score-api
./mvnw clean compile jib:dockerBuild -DskipTests --projects game-score-collector
