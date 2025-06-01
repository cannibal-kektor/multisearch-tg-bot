#!/bin/sh
set -e

SECRETS_LOCATION=/run/secrets
KIBANA_USERNAME=$(grep '^KIBANA_USERNAME=' "$SECRETS_LOCATION/kibana_credentials" | cut -d'=' -f2)
export KIBANA_USERNAME
KIBANA_PASSWORD=$(grep '^KIBANA_PASSWORD=' "$SECRETS_LOCATION/kibana_credentials" | cut -d'=' -f2)
export KIBANA_PASSWORD
RABBIT_USERNAME=$(grep '^username=' "$SECRETS_LOCATION/rabbit_credentials" | cut -d'=' -f2)
export RABBIT_USERNAME
RABBIT_PASSWORD=$(grep '^password=' "$SECRETS_LOCATION/rabbit_credentials" | cut -d'=' -f2)
export RABBIT_PASSWORD

exec /bin/tini -- /usr/local/bin/kibana-docker