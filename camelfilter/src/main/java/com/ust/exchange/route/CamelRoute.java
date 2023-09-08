package com.ust.exchange.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("file:file1?noop=true").filter(body().regex(".*\\bhigher\\b.*"))
		.process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				// TODO Auto-generated method stub
				String msg1 = "";
				String[] msg = exchange.getIn().getBody(String.class).split(" ");
				for (String s : msg) {
					if (s.equals("higher"))
						msg1 = s;
				}
				exchange.getIn().setBody(msg1);
			}
		}).to("file:higher");

	}

}
