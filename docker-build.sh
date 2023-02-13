#!/usr/bin/env bash

if [ "$1" = "native" ];
then
  ./mvnw clean -Pnative spring-boot:build-image --projects game-score-api -DskipTests
  ./mvnw clean -Pnative spring-boot:build-image --projects game-score-collector -DskipTests
else
  ./mvnw clean compile jib:dockerBuild --projects game-score-api
  ./mvnw clean compile jib:dockerBuild --projects game-score-collector
fi
