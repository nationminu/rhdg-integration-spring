#!/usr/bin/env bash

CONTAINER_IP=`hostname -I`
${JDG_HOME}/bin/server.sh --bind-address=${CONTAINER_IP}