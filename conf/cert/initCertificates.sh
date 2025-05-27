#!/bin/sh

#mkdir -p ca rabbit/{app,server} mongo/{app,server} elastic/{app,fleet,agent,kibana,server}
#mkdir -p ca rabbit/app rabbit/server mongo/app mongo/server elastic/app elastic/fleet elastic/agent elastic/kibana elastic/server
if [ -e ca/ca.pem ]; then
  echo "Certificates already exists."
  exit 0;
fi
mkdir -p ca app rabbit mongo agent kibana elastic
echo "Generating CA certificate"
openssl genrsa -out ca/ca.key 4096
openssl req -x509 -new -key ca/ca.key --days 1024 -out ca/ca.pem -section ca -config openssl.conf
openssl x509 -in ca/ca.pem -inform PEM -out ca/ca.crt
echo "CA generated successfully at ${PWD}/ca/"

#Copy CA certificate to all subdirectories
find . -mindepth 1 -type d -exec cp ca/ca.pem {} \;

create_certificate()
{
  DESTINATION=$1
  SECTION=$2
  echo "Generating certificate for $SECTION"
  openssl req -x509 -days 365 -noenc -out $DESTINATION.pem -keyout $DESTINATION.key -section $SECTION -config openssl.conf -CA ca/ca.pem -CAkey ca/ca.key
  echo "Generated successfully at ${PWD}/$DESTINATION"
}
create_certificate rabbit/rabbit01 rabbit_server
create_certificate mongo/mongo01 mongo_server
create_certificate elastic/elastic01 elastic_server
create_certificate kibana/kibana kibana
#create_certificate fleet/fleet fleet
create_certificate agent/agent agent
create_certificate app/tgAppRabbit rabbit_client
create_certificate app/tgAppMongo mongo_client
#create_certificate app/tgAppElastic elastic_client

#Combine
cat mongo/mongo01.key >> mongo/mongo01.pem
cat agent/agent.key agent/agent.pem > agent/agentCombined.pem

#chown -R root:root .;
find . -type d -exec chmod 755 {} \;
find . -type f \( -name "*.pem" -o -name "*.key" -o -name "*.crt" \) -exec chmod 644 {} \;