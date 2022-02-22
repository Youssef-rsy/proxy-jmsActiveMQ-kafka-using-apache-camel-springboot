package com.local.ysf.integration.camel.processor;

import lombok.extern.slf4j.Slf4j;
import com.local.ysf.integration.model.Event;
import org.apache.camel.Exchange;

import javax.jms.DeliveryMode;
@Slf4j
public class KafkaToJmsProcessor implements org.apache.camel.Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Event event = exchange.getIn().getBody(Event.class);
        exchange.getMessage().setHeader("test", true);
        exchange.getMessage().setHeader("JMSDeliveryMode", DeliveryMode.PERSISTENT);
        //event.setEventType("changed event type ");
        log.info(String.valueOf(exchange.getMessage().getHeaders()));
        exchange.getIn().setBody(event);
    }
}
