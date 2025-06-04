#!/bin/sh

#mkdir -p ca rabbit/{app,server} mongo/{app,server} elastic/{app,fleet,agent,kibana,server}
#mkdir -p ca rabbit/app rabbit/server mongo/app mongo/server elastic/app elastic/fleet elastic/agent elastic/kibana elastic/server
mkdir certs
echo "Generating Build Phase Mock CA certificate"
openssl genrsa -out certs/ca.key 4096
openssl req -x509 -new -key certs/ca.key --days 1024 -out certs/ca.pem -section ca -config cert_conf/buildOpenssl.conf
echo "Test CA generated successfully at ${PWD}"

create_certificate()
{
  DESTINATION=$1
  SECTION=$2
  echo "Generating Build Phase Mock certificate for $SECTION"
  openssl req -x509 -days 365 -noenc -out $DESTINATION.pem -keyout $DESTINATION.key -section $SECTION -config cert_conf/buildOpenssl.conf -CA certs/ca.pem -CAkey certs/ca.key
  echo "Generated successfully at ${PWD}/$DESTINATION"
}

create_certificate certs/tgAppRabbit rabbit_client
create_certificate certs/tgAppMongo mongo_client
#create_certificate certs/tgAppElastic elastic_client
