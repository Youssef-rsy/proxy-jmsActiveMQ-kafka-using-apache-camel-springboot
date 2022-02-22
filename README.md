# proxy-jmsActiveMQ-kafka

This project is a POC for integration data from JMS queues(ActiveMQ) to kafka topics and vice versa.

The integration from a queue to a topic is added dynamically at runtime, so in properties yml file we have a list of value pair (queue/topic) tha we should add in order to set routes between these two systems (jms and kafka).

 
## integration with apache camel

To use apache camel for data integration you should set the profile to **camel**

## setup
### activeMQ
you can run activeMQ using docker image as follow : 
docker run -it --rm -p 8161:8161 -p 61616:61616  -e ARTEMIS_USERNAME=admin -e ARTEMIS_PASSWORD=admin  vromero/activemq-artemis

### kafka
make sure you use kafka with sasl_plaintext 


##Data flow 
![img_2.png](img_2.png)

### Data sets
Here is a example of payload that you can use for test purpose 

For JSON FORMAT
```
{
    "event_type": "test on 22/02/2022 09:37 from -JSON- formt  "
}
```
For XML FORMAT
```
<?xml version="1.0" encoding="utf-8"?>
<event>
  <event_type>test on 22/02/2022 09:37 from -XML- formt  </event_type>
</event>
```