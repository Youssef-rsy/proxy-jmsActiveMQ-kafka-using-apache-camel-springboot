package com.local.ysf.integration.camel;

import com.local.ysf.integration.camel.processor.JmsToKafkaProcessor;
import com.local.ysf.integration.camel.processor.KafkaToJmsProcessor;
import com.local.ysf.integration.common.SourceDestinationConfig;
import com.local.ysf.integration.model.Event;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Component
public class DynamicRoutes extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(DynamicRoutes.class);

    final SourceDestinationConfig sourceDestinationConfig;

    public DynamicRoutes(SourceDestinationConfig sourceDestinationConfig) {
        this.sourceDestinationConfig = sourceDestinationConfig;
    }

    @Override
    public void configure() throws JAXBException {
        addJmsToKafkaRoutes();
        addKafkaToJmsRoutes();
    }

    private void addJmsToKafkaRoutes() throws JAXBException {
        JaxbDataFormat jaxb = new JaxbDataFormat();
        JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);
        jaxb.setContext(jaxbContext);
        sourceDestinationConfig.getJmsToKafkaQueueTopicPairs().forEach(queueTopicPair ->
                from("activemq:" + queueTopicPair.getQueue() + "?transacted=true")
                        //EIP : MESSAGE ROUTER  for "dynamic routing based on headers"
                        .choice()
                        .when(simple("${header.test} == 'true'"))
                        .description("Use this route when the headers contain a header property called test with the value true")
                        .unmarshal(jaxb)
                        .removeHeaders("JMS*") // REMOVE JMS HEADER BEFORE COMMIT TO KAFAKA
                        .process(new JmsToKafkaProcessor()).marshal().json(JsonLibrary.Jackson)
                        //.to("seda:async-transfers") async : create new thread for async
                        .log("Camel: read message from queue " + queueTopicPair.getQueue() + ": " + "${body}")
                        .to("kafka:" + queueTopicPair.getTopic())
                        .otherwise()
                        .log(LoggingLevel.INFO, "!!!!!!!!!!!!!!! No route provided !!!!!!!!!!!!!!!")
                        .endChoice()
        );
    }

    private void addKafkaToJmsRoutes() throws JAXBException {

        JaxbDataFormat jaxb = new JaxbDataFormat();
        JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);
        jaxb.setContext(jaxbContext);
        sourceDestinationConfig.getKafkaToJmsQueueTopicPairs().forEach(queueTopicPair ->
                from("kafka:" + queueTopicPair.getTopic())
                        .log("Camel: read message from topic " + queueTopicPair.getTopic() + ": " + "${body}")
                        .unmarshal().json(JsonLibrary.Jackson, Event.class)
                        .process(new KafkaToJmsProcessor())
                        .marshal().jaxb(jaxb.getContextPath())
                        .log("1- Camel: read message from topic " + queueTopicPair.getTopic() + ": " + "${body}")
                        .to("activemq:" + queueTopicPair.getQueue())
                        .end()
                        .process(this::commitOffsetsManually))
        ;
    }

    private void commitOffsetsManually(Exchange exchange) {
        KafkaManualCommit manual =
                exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
        if (manual != null) {
            log.info("manually committing the offset");
            manual.commitSync();
        }
    }
}
