package com.ust.consumer1.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Consumer1Route extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		
		from("activemq:topic:totopic")
		.routeId("Consumer 1")
		.process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				// TODO Auto-generated method stub
				String message = exchange.getIn().getBody(String.class);
				String message1 = "Message consumed by consumer 1 : " + message;
				exchange.getIn().setBody(message1);
			}
		}).to("activemq:queue:queue1")
		.log("${body}");
		
	}

}
