package com.ust.exchange.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {

		from("activemq:queue:inputqueue").log("${body}")
		.process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				
				String msg = exchange.getIn().getBody(String.class);
				String msg1 = "Processed Message : " + msg;
				exchange.getIn().setBody(msg1);
				
			}
		}).to("activemq:queue:outputqueue").log("${body}");
		
	}

}
