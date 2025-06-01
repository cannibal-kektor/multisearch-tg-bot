#!/bin/sh
set -e

SECRETS_FILE=/run/secrets/rabbit_credentials

USERNAME=$(grep '^username=' "$SECRETS_FILE" | cut -d'=' -f2)
PASSWORD=$(grep '^password=' "$SECRETS_FILE" | cut -d'=' -f2)

sed -e "s/RABBITMQ_USERNAME/$USERNAME/g" \
    -e "s/RABBITMQ_PASSWORD/$PASSWORD/g" \
    /etc/rabbitmq/definitions.template.json > /opt/rabbit/setup_definition.json

exec docker-entrypoint.sh rabbitmq-server