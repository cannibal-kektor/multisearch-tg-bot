#!/bin/bash
set -e

SECRETS_FILE=/run/secrets/mongo_credentials
ADMIN_USERNAME=$(grep '^admin_username=' "$SECRETS_FILE" | cut -d'=' -f2)
ADMIN_PASSWORD=$(grep '^admin_password=' "$SECRETS_FILE" | cut -d'=' -f2)

echo mongo-replica-setup.sh time now: `date +"%T" `
mongosh --host mongo01:27017 -u $ADMIN_USERNAME -p $ADMIN_PASSWORD --tls --tlsCAFile /etc/ssl/mongo/ca.pem --authenticationDatabase admin  <<EOF
  var cfg = {
    "_id": "rs0",
    "version": 1,
    members: [
        {
            "_id": 0,
            "host": "mongo01:27017",
            "priority": 3
        }
    ]
  };
  rs.initiate(cfg);
EOF