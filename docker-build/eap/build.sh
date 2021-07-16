#!/usr/bin/env bash

EAP_VERSION=7.3.0-standalone

docker build -t docker.io/nationminu/jboss-eap:${EAP_VERSION} . -f Dockerfile
docker tag docker.io/nationminu/jboss-eap:${EAP_VERSION}  docker.io/nationminu/jboss-eap:latest
docker push docker.io/nationminu/jboss-eap:${EAP_VERSION} 
docker push docker.io/nationminu/jboss-eap:latest 