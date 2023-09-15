package com.ust.exchange.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class CamelRoute extends RouteBuilder{
	
	@Autowired
	private ProducerTemplate template;

	@Override
	public void configure() throws Exception {
		
		from("direct:sendtoamq")
		.process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				// TODO Auto-generated method stub
				String msg = exchange.getIn().getBody(String.class);
				String msg1 = "Processed Message : " + msg;
				exchange.getIn().setBody(msg1);
			}
		}).to("activemq:queue:toqueue").log("${body}");
		
				
		from("activemq:queue:toqueue")
		.process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				// TODO Auto-generated method stub
				String message = exchange.getIn().getBody(String.class);
				String message1 = "Message consumed from active mq : " + message;
				exchange.getIn().setBody(message1);
			}
		})
		.log("${body}");
		
		from("activemq:queue:toqueue").to("direct:consu1");
		from("direct:consu1").log("message from queue : ${body}");
		
		
		
		from("timer:myTimer?period=10000")
		.setBody(constant("Message sending to AMQ Topic"))
		.to("activemq:topic:totopic");
		
		
	}
	
	public void function() {
		template.sendBody("direct:sendtoamq","Hello from camel route");
	}

}
