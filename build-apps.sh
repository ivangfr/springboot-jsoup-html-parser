#!/usr/bin/env bash

if [ "$1" = "native" ];
then
  ./mvnw clean spring-boot:build-image -DskipTests --projects game-score-api
  ./mvnw clean spring-boot:build-image -DskipTests --projects game-score-collector
else
  ./mvnw clean compile jib:dockerBuild -DskipTests --projects game-score-api
  ./mvnw clean compile jib:dockerBuild -DskipTests --projects game-score-collector
fi
