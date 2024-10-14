#!/bin/bash
echo "Waiting for Elasticsearch availability";
until curl -s --cert config/certs/elastic01.pem --key config/certs/elastic01.key --cacert config/certs/ca.pem https://elastic01:9200 | grep -q "missing authentication credentials"; do sleep 3; done;
echo "Setting kibana_system password";
until curl -s -X POST --cert config/certs/elastic01.pem --key config/certs/elastic01.key --cacert config/certs/ca.pem -u "elastic:${ELASTIC_PASSWORD}" -H "Content-Type: application/json" https://elastic01:9200/_security/user/kibana_system/_password -d "{\"password\":\"${KIBANA_PASSWORD}\"}" | grep -q "^{}"; do sleep 10; done;
#echo "Assigning specific parsing pipelines for docker logs";
#curl -s -X PUT --cert config/certs/elastic01.pem --key config/certs/elastic01.key --cacert config/certs/ca.pem -u "elastic:${ELASTIC_PASSWORD}" -H "Content-Type: application/json" https://elastic01:9200/_ingest/pipeline/logs-docker.container_logs@custom -d '
#{
#  "processors":[
#    {
#      "pipeline":{
#        "name":"logs-rabbitmq.log-1.16.0",
#        "ignore_missing_pipeline":false,
#        "if":"ctx?.container?.name?.indexOf(\"rabbit\") == 0",
#        "description":"[Fleet] Pipeline for rabbit logs"
#      }
#    },
#    {
#      "pipeline":{
#        "name":"logs-mongodb.log-1.16.0",
#        "ignore_missing_pipeline":false,
#        "if":"ctx?.container?.name?.indexOf(\"mongo\") == 0",
#        "description":"[Fleet] Pipeline for mongo logs"
#      }
#    },    
#	{
#      "pipeline":{
#        "name":"logs-elasticsearch.server-1.15.0",
#        "ignore_missing_pipeline":false,
#        "if":"ctx?.container?.name?.indexOf(\"elastic\") == 0",
#        "description":"[Fleet] Pipeline for elastic logs"
#      }
#    },	
#	{
#      "pipeline":{
#        "name":"logs-kibana.log-2.5.4",
#        "ignore_missing_pipeline":false,
#        "if":"ctx?.container?.name?.indexOf(\"kibana\") == 0",
#        "description":"[Fleet] Pipeline for kibana logs"
#      }
#    },
#    {
#    "set": {
#      "field": "event.dataset",
#      "value": "docker.container_logs"
#      }
#    }
#  ]
#}'
echo "All done!";