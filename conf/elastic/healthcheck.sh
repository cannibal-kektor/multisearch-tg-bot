#!/bin/sh
set -e
SECRETS_FILE=/run/secrets/kibana_credentials
PASSWORD=$(grep '^KIBANA_PASSWORD=' "$SECRETS_FILE" | cut -d'=' -f2)
curl -sS --cacert config/certs/ca.pem -u kibana_system:"$PASSWORD" \
'https://localhost:9200/_cluster/health?wait_for_status=yellow&timeout=30s'
exit $?