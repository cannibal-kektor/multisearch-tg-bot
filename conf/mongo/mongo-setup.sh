#!/bin/bash
echo "sleeping for 10 seconds"

echo mongo_setup.sh time now: `date +"%T" `
mongosh --host mongo01:27017 -u ${MONGO_INITDB_ROOT_USERNAME} -p ${MONGO_INITDB_ROOT_PASSWORD} --tls --tlsCAFile /etc/ssl/mongo/ca.pem --authenticationDatabase admin  <<EOF
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
#mongosh --host mongodb:27017 -u ${MONGO_INITDB_ROOT_USERNAME} -p ${MONGO_INITDB_ROOT_PASSWORD} --tls --tlsCAFile /etc/ssl/ca.pem --authenticationDatabase admin  <<EOF
#  db.getSiblingDB("\$external").runCommand(
#  {
#    createUser: "CN=testUser",
#    roles: [
#         { role: "root", db: "admin" }
#    ],
#    writeConcern: { w: "majority" , wtimeout: 5000 }
#   }
#  );
#EOF