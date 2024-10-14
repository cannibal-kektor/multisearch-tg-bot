#!/bin/bash
echo Enrolling Elastic Agent in Fleet
echo Requesting enrollment token for policy $1
export FLEET_ENROLLMENT_TOKEN=$(curl -s --cacert certs/ca.pem -u "${KIBANA_FLEET_USERNAME}:${KIBANA_FLEET_PASSWORD}" https://kibana:5601/api/fleet/enrollment_api_keys | grep -o 'items.*]' | grep -o "[^{]*$1[^}]*" | grep -o '"api_key":"[^, ]*"' | grep -o '[^:]*$' | grep -o '[^"]*')
if [ ! -z $FLEET_ENROLLMENT_TOKEN ]; then
    echo Enrollment token recieved. Starting agent ...
    exec /usr/bin/tini -- /usr/local/bin/docker-entrypoint "$@"
else
    echo Enrollment token not recieved. Exit
    exit 1
fi