#!/bin/sh
set -e

SECRETS_FILE=/run/secrets/elastic_credentials

KIBANA_FLEET_USERNAME=$(grep '^ELASTIC_USERNAME=' "$SECRETS_FILE" | cut -d'=' -f2)
export KIBANA_FLEET_USERNAME
KIBANA_FLEET_PASSWORD=$(grep '^ELASTIC_PASSWORD=' "$SECRETS_FILE" | cut -d'=' -f2)
export KIBANA_FLEET_PASSWORD

update-ca-certificates

exec /usr/bin/tini -- /usr/local/bin/docker-entrypoint