#!/bin/sh

docker build -t frontend-service:v1 ./frontend/

./gradlew bootBuildImage -x test

docker compose -f ../ up -d