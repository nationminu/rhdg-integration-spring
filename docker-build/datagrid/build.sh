#!/usr/bin/env bash

DG_VERSION=8.2

docker build -t docker.io/nationminu/rhdg-server:${DG_VERSION} . -f Dockerfile
docker tag docker.io/nationminu/rhdg-server:${DG_VERSION}  docker.io/nationminu/rhdg-server:latest
docker push docker.io/nationminu/rhdg-server:${DG_VERSION} 
docker push docker.io/nationminu/rhdg-server:latest 