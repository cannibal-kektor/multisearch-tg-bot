#!/bin/sh

#mkdir -p ca rabbit/{app,server} mongo/{app,server} elastic/{app,fleet,agent,kibana,server}
#mkdir -p ca rabbit/app rabbit/server mongo/app mongo/server elastic/app elastic/fleet elastic/agent elastic/kibana elastic/server
mkdir testCert
echo "Generating Test CA certificate"
openssl genrsa -out testCert/ca.key 4096
openssl req -x509 -new -key testCert/ca.key --days 1024 -out testCert/ca.pem -section ca -config cert_conf/openssl.conf
echo "Test CA generated successfully at ${PWD}"

create_certificate()
{
  DESTINATION=$1
  SECTION=$2
  echo "Generating certificate for $SECTION"
  openssl req -x509 -days 365 -noenc -out $DESTINATION.pem -keyout $DESTINATION.key -section $SECTION -config cert_conf/openssl.conf -CA testCert/ca.pem -CAkey testCert/ca.key
  echo "Generated successfully at ${PWD}/$DESTINATION"
}

create_certificate testCert/tgAppRabbit rabbit_client
create_certificate testCert/tgAppMongo mongo_client
#create_certificate testCert/tgAppElastic elastic_client

#chown -R root:root .;
find testCert -type d -exec chmod 755 {} \;
find testCert -type f \( -name "*.pem" -o -name "*.key" \) -exec chmod 644 {} \;