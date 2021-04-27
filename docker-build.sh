#!/usr/bin/env bash

if [ "$1" = "native" ];
then
  ./mvnw clean spring-boot:build-image --projects game-score-api -DskipTests
  ./mvnw clean spring-boot:build-image --projects game-score-collector -DskipTests
else
  ./mvnw clean package jib:dockerBuild --projects game-score-api -DskipTests
  ./mvnw clean package jib:dockerBuild --projects game-score-collector -DskipTests
fi
