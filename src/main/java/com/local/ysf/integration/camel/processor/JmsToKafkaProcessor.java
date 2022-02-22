package com.local.ysf.integration.camel.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.ysf.integration.model.Event;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import org.apache.camel.Exchange;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

public class JmsToKafkaProcessor implements org.apache.camel.Processor {

    public static final String APPLICATION_JSON = "application/json";

    @Override
    public void process(Exchange exchange) throws Exception {
        Event body = exchange.getIn().getBody(Event.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValueAsBytes(body);
        CloudEventData cloudEventData  = BytesCloudEventData.wrap(objectMapper.writeValueAsBytes(body));
        exchange.getMessage().setHeader("content-type", APPLICATION_JSON);
        CloudEvent event = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())//RANDOM ID
                .withType(body.getClass().getName())
                .withSource(URI.create("http://localhost"))
                .withTime(OffsetDateTime.now())
                .withDataContentType(APPLICATION_JSON)
                .withData(cloudEventData)
                .build();
        exchange.getIn().setBody(event);
    }
}
