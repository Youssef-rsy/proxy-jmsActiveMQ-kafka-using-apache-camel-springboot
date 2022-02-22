package com.local.ysf.integration.camel.processor;

import com.local.ysf.integration.model.Event;
import org.apache.camel.Exchange;

public class JmsToKafkaProcessor implements org.apache.camel.Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Event event = exchange.getIn().getBody(Event.class);
        //event.setEventType("changed event type ");
        exchange.getIn().setBody(event);
    }
}
