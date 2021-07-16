#!/usr/bin/env bash

CONTAINER_IP=`hostname -I`
${JBOSS_HOME}/bin/standalone.sh -Djboss.bind.address=${CONTAINER_IP} -Djboss.bind.address.management=${CONTAINER_IP} -Djboss.server.default.config=${CONFIG_FILE}  