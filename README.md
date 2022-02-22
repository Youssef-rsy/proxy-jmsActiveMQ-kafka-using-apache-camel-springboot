# proxy-jmsActiveMQ-kafka

This project is a POC for integration data from JMS queues(ActiveMQ) to kafka topics and vice versa.

The integration from a queue to a topic : is added dynamically at runtime, so in properties yml file we have a list of value pair (queue/topic) tha we should add in order to set routes between these two systems (jms and kafka).

 
## integration with apache camel

To use apache camel for data integration you should set the profile to **camel**

## setup
### activeMQ
you can run activeMQ using docker image as follow : 
docker run -it --rm -p 8161:8161 -p 61616:61616  -e ARTEMIS_USERNAME=admin -e ARTEMIS_PASSWORD=admin  vromero/activemq-artemis

### kafka


##Data flow 
    Camel2 --> jmstokafka2 --> camel2receiver --> jmstokafka --> camelreceiver