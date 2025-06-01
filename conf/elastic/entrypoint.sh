#!/bin/sh

set -e

SECRETS_FILE=/run/secrets/elastic_credentials

ELASTIC_PASSWORD=$(grep '^ELASTIC_PASSWORD=' "$SECRETS_FILE" | cut -d'=' -f2)
export ELASTIC_PASSWORD

exec /bin/tini -- /usr/local/bin/docker-entrypoint.sh eswrapper