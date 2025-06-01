#!/bin/sh
set -e

SECRETS_FILE=/run/secrets/mongo_credentials

ADMIN_USERNAME=$(grep '^admin_username=' "$SECRETS_FILE" | cut -d'=' -f2)
ADMIN_PASSWORD=$(grep '^admin_password=' "$SECRETS_FILE" | cut -d'=' -f2)
APP_USERNAME=$(grep '^app_username=' "$SECRETS_FILE" | cut -d'=' -f2)
AGENT_USERNAME=$(grep '^agent_username=' "$SECRETS_FILE" | cut -d'=' -f2)

sed -e "s/ADMIN_USERNAME/$ADMIN_USERNAME/g" \
    -e "s/ADMIN_PASSWORD/$ADMIN_PASSWORD/g" \
    -e "s/APP_USERNAME/$APP_USERNAME/g" \
    -e "s/AGENT_USERNAME/$AGENT_USERNAME/g" \
    /etc/mongo/init.template.js > /docker-entrypoint-initdb.d/init.js

exec docker-entrypoint.sh "$@"