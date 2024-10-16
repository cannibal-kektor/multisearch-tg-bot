#!/bin/bash
set -e
echo "Waiting for Elasticsearch availability";
until curl -s --cert config/certs/elastic01.pem --key config/certs/elastic01.key --cacert config/certs/ca.pem https://elastic01:9200 | grep -q "missing authentication credentials"; do sleep 3; done;
echo "Setting kibana_system password";
until curl -s -X POST --cert config/certs/elastic01.pem --key config/certs/elastic01.key --cacert config/certs/ca.pem -u "elastic:${ELASTIC_PASSWORD}" -H "Content-Type: application/json" https://elastic01:9200/_security/user/kibana_system/_password -d "{\"password\":\"${KIBANA_PASSWORD}\"}" | grep -q "^{}"; do sleep 10; done;
#echo "Assigning specific parsing pipelines for docker logs";
#curl -s -X PUT --cert config/certs/elastic01.pem --key config/certs/elastic01.key --cacert config/certs/ca.pem -u "elastic:${ELASTIC_PASSWORD}" -H "Content-Type: application/json" https://elastic01:9200/_ingest/pipeline/logs-docker.container_logs@custom -d '
#{
#  "processors":[
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

#        "if": """
#            String containerName = ctx.container.name;
#            if(containerName != null){
#              for (String tag : tags) {
#                if (tag.toLowerCase().contains('prod')) {
#                  return false;
#                }
#              }
#            }
#            return true;
#        """
echo "Creating stored script";
if curl -s -X PUT --cert config/certs/elastic01.pem --key config/certs/elastic01.key --cacert config/certs/ca.pem -u "elastic:${ELASTIC_PASSWORD}" -H "Content-Type: application/json" https://elastic01:9200/_scripts/check_ecs_format_logging? -d '
{
  "script": {
    "lang": "painless",
    "source": "String cn = ctx.container.name;\nif(cn != null){\n  return cn.startsWith(\"multisearch\") || cn.startsWith(\"elastic\") || cn.startsWith(\"kibana\")\n}\nreturn false;"
    }
}' | grep -q "error"; then
  echo "Error while creating stored script"
  exit 1;
fi

echo "Assigning pipeline for parsing docker logs in ecs json format";
if curl -s -X PUT --cert config/certs/elastic01.pem --key config/certs/elastic01.key --cacert config/certs/ca.pem -u "elastic:${ELASTIC_PASSWORD}" -H "Content-Type: application/json" https://elastic01:9200/_ingest/pipeline/container_logs@ecs -d '
{
  "processors":[
    {
      "json":{
        "field": "message",
        "add_to_root": true,
        "on_failure": [
                  {
                    "set": {
                      "description": "Set error.message",
                      "field": "error.message",
                      "value": "Message is not in json format.",
                      "override": false
                    }
                  }
                ]
      }
    },
    {
      "dot_expander":{
        "field": "*",
        "override": true
      }
    }
  ]
}' | grep -q "error"; then
    echo "Error while creating pipeline for ecs format docker logs"
    exit 1;
fi

#    {
#      "pipeline":{
#        "name":"logs-mongodb.log-1.16.0",
#        "ignore_missing_pipeline":false,
#        "if":"ctx.container?.name?.indexOf(\"mongo\") == 0",
#        "description":"Pipeline for mongo logs"
#      }
#    },

echo "Assigning pipeline for distinguishing specific docker logs";
if curl -s -X PUT --cert config/certs/elastic01.pem --key config/certs/elastic01.key --cacert config/certs/ca.pem -u "elastic:${ELASTIC_PASSWORD}" -H "Content-Type: application/json" https://elastic01:9200/_ingest/pipeline/logs-docker.container_logs@custom -d '
{
  "processors":[
    {
      "pipeline":{
        "name":"container_logs@ecs",
        "ignore_missing_pipeline":false,
        "if": { "id": "check_ecs_format_logging" },
        "description":"Pipeline for logs in ECS format collected by Docker integration"
      }
    },
    {
      "pipeline":{
        "name":"logs-rabbitmq.log-1.16.0",
        "ignore_missing_pipeline":false,
        "if":"ctx.container?.name?.indexOf(\"rabbit\") == 0",
        "description":"Pipeline for rabbit logs"
      }
    },
    {
      "pipeline":{
        "name":"logs-mongodb.log-1.16.0",
        "ignore_missing_pipeline":false,
        "if":"ctx.container?.name?.indexOf(\"mongo\") == 0",
        "description":"Pipeline for mongo logs"
      }
    },
    {
    "set": {
      "field": "event.dataset",
      "value": "docker.container_logs"
      }
    }
  ]
}' | grep -q "error"; then
      echo "Error while creating pipeline for distinguishing specific docker logs"
      exit 1;
fi

echo "All done!";