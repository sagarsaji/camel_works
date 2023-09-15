package com.ust.consumer2.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Consumer2Route extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		
		from("activemq:topic:totopic")
		.routeId("Consumer 2")
		.process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				// TODO Auto-generated method stub
				String message = exchange.getIn().getBody(String.class);
				String message1 = "Message consumed by consumer 2 : " + message;
				exchange.getIn().setBody(message1);
			}
		}).to("activemq:queue:queue2")
		.log("${body}");
		
	}

}
